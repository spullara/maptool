package net.rptools.maptool.client.functions;

import java.awt.Dimension;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolVariableResolver;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.client.walker.ZoneWalker;
import net.rptools.maptool.model.CellPoint;
import net.rptools.maptool.model.Grid;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.ZonePoint;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;

public class TokenLocationFunctions extends AbstractFunction {
	
	private static class TokenLocation {
		int x;
		int y;
		int z;
	}
	
	/** Singleton for class/ */
	private static final TokenLocationFunctions instance = 
		new TokenLocationFunctions();

	private TokenLocationFunctions() {
		super(0, 4, "getTokenX", "getTokenY", "getTokenZ", "getDistance", "moveToken", 
				    "goto", "getDistanceToXY");
	}


	/** 
	 * Gets an instance of TokenLocationFunctions.
	 */
	public static TokenLocationFunctions getInstance() {
		return instance;
	}
	
	@Override
	public Object childEvaluate(Parser parser, String functionName,
			List<Object> parameters) throws ParserException {
		
		if (!MapTool.getParser().isMacroTrusted()) {
			throw new ParserException(functionName + "(): you do not have permisison");
		}
		
		MapToolVariableResolver res = (MapToolVariableResolver) parser.getVariableResolver();
		
		
		if (functionName.equals("getTokenX")) {
			return getTokenLocation(res, parameters).x;
		}

		if (functionName.equals("getTokenY")) {
			return getTokenLocation(res, parameters).y;
		}
		
		if (functionName.equals("getTokenZ")) {
			return getTokenLocation(res, parameters).z;
		}
		
		if (functionName.equals("getDistance")) {
			return getDistance(res, parameters);
		}
		
		if (functionName.equals("getDistanceToXY")) {
			return getDistanceToXY(res, parameters);
		}
		
		if (functionName.equals("goto")) {
			return gotoLoc(res, parameters);
		}
		
		if (functionName.equals("moveToken")) {
			return moveToken(res, parameters);
		}
		
		
		throw new ParserException("Unknown function " + functionName);
	}
	


	/**
	 * Gets the location of the token on the map.
	 * @param res The variable resolver.
	 * @param args The arguments.
	 * @return the location of the token.
	 * @throws ParserException if an error occurs.
	 */
	private TokenLocation getTokenLocation(MapToolVariableResolver res, List<Object> args) throws ParserException {
		Token token = getTokenFromParam(res, "getTokenLocation", args, 1);
		boolean useDistancePerCell = true;
		
		if (args.size() > 0) {
			if (!(args.get(0) instanceof BigDecimal)) {
				throw new ParserException("getTokenLocation(): First parameter must be a number");
			} 
			BigDecimal val = (BigDecimal)args.get(0);
			useDistancePerCell = val.equals(BigDecimal.ZERO) ? false : true;
		}
	
		CellPoint cellPoint = getTokenCell(token);
		int x = useDistancePerCell ? cellPoint.x * getDistancePerCell() : cellPoint.x;
		int y = useDistancePerCell ? cellPoint.y * getDistancePerCell() : cellPoint.y;
		
		TokenLocation loc = new TokenLocation();
		loc.x = x;
		loc.y = y;
		loc.z = token.getZOrder();
		return loc;
	}	
	
	/**
	 * Gets the distance between two tokens.
	 * @param source 
	 * @param target
	 * @param gridUnits
	 * @return
	 */
	public double getDistance(Token source, Token target, boolean units) {
		ZoneRenderer renderer = MapTool.getFrame().getCurrentZoneRenderer();
		Grid grid = renderer.getZone().getGrid();
		
		if (grid.getCapabilities().isPathingSupported()) {
			
			// Get the center of our tokens so we can get which cells it occupies.
			Dimension dim = grid.getCellOffset();
			double d = source.getFootprint(grid).getScale();
			double sourceCenterX = source.getX() + dim.getWidth() + (d * grid.getSize())/2;
			double sourceCenterY = source.getY() + dim.getHeight() + (d *  grid.getSize())/2;
			d = target.getFootprint(grid).getScale();
			double targetCenterX = target.getX() - grid.getOffsetX() + (d * grid.getSize())/2;
			double targetCenterY = target.getY() - grid.getOffsetY() + (d * grid.getSize())/2;
			
			// Get which cells our tokens occupy
			Set<CellPoint> sourceCells = source.getFootprint(grid).getOccupiedCells(grid.convert(new ZonePoint((int)sourceCenterX, (int)sourceCenterY)));
			Set<CellPoint> targetCells = target.getFootprint(grid).getOccupiedCells(grid.convert(new ZonePoint((int)targetCenterX, (int)targetCenterY)));

			ZoneWalker walker = grid.createZoneWalker();
			// Get the distances from each source to target cell and keep the minimum one
			int distance = Integer.MAX_VALUE;
			for (CellPoint scell : sourceCells) {
				for (CellPoint tcell : targetCells) {
		            walker.setWaypoints(scell, tcell);
		            distance = Math.min(distance, walker.getDistance());
				}
			}
			
            if (units) {
            	return distance;
            } else {
            	return distance / getDistancePerCell();
            }
		} else {

			double d = source.getFootprint(grid).getScale();
			double sourceCenterX = source.getX() + (d * grid.getSize())/2;
			double sourceCenterY = source.getY() + (d * grid.getSize())/2;
			d = target.getFootprint(grid).getScale();
			double targetCenterX = target.getX() + (d * grid.getSize())/2;
			double targetCenterY = target.getY() + (d * grid.getSize())/2;
			double a = sourceCenterX - targetCenterX;
			double b = sourceCenterY - targetCenterY;
			double h = Math.sqrt(a*a + b*b);
            h /= renderer.getZone().getGrid().getSize();
            if (units) {
            	h *= renderer.getZone().getUnitsPerCell();
            }
            return h;
		}
	}
	

	/**
	 * Gets the distance to a target x,y co-ordinate following map movement rules.
	 * @param source The token to get the distance from.
	 * @param x the x co-ordinate to get the distance to.
	 * @param y the y co-ordinate to get the distance to.
	 * @param units get the distance in the units specified for the map.
	 * @return
	 */
	public double getDistance(Token source, int x, int y, boolean units) {
		ZoneRenderer renderer = MapTool.getFrame().getCurrentZoneRenderer();
		Grid grid = renderer.getZone().getGrid();
		
		if (grid.getCapabilities().isPathingSupported()) {
			
			// Get the center of our tokens so we can get which cells it occupies.
			Dimension dim = grid.getCellOffset();
			double d = source.getFootprint(grid).getScale();
			double sourceCenterX = source.getX() + dim.getWidth() + (d * grid.getSize())/2;
			double sourceCenterY = source.getY() + dim.getHeight() + (d * grid.getSize())/2;
			
			// Get which cells our tokens occupy
			Set<CellPoint> sourceCells = source.getFootprint(grid).getOccupiedCells(grid.convert(new ZonePoint((int)sourceCenterX, (int)sourceCenterY)));

			ZoneWalker walker = grid.createZoneWalker();
			// Get the distances from each source to target cell and keep the minimum one
			int distance = Integer.MAX_VALUE;
			CellPoint targetCell = new CellPoint(x,y);
			for (CellPoint scell : sourceCells) {
	            walker.setWaypoints(scell, targetCell);
	            distance = Math.min(distance, walker.getDistance());
			}
			
            if (units) {
            	return distance;
            } else {
            	return distance / getDistancePerCell();
            }
		} else {

			double d = source.getFootprint(grid).getScale();
			double sourceCenterX = source.getX() + (d * grid.getSize())/2;
			double sourceCenterY = source.getY() + (d * grid.getSize())/2;
			double a = sourceCenterX - x;
			double b = sourceCenterY - y;
			double h = Math.sqrt(a*a + b*b);
            h /= renderer.getZone().getGrid().getSize();
            if (units) {
            	h *= renderer.getZone().getUnitsPerCell();
            }
            return h;
		}
	}
	
	/**
	 * Gets the distance to another token. 
	 * @param res The variable resolver.
	 * @param args arguments to the function.
	 * @return the distance between tokens.
	 * @throws ParserException if an error occurs.
	 */
	private BigDecimal getDistance(MapToolVariableResolver res, List<Object> args) throws ParserException {
		if (args.size() < 1) {
			throw new ParserException("getDistance(): Not enough parameters");
		}

		Token target = getTokenFromParam(res, "getDistance", args, 0);
		Token source = getTokenFromParam(res, "getDistance", args, 2);
		
		boolean useDistancePerCell = true;
		if (args.size() > 1) {
			if (!(args.get(1) instanceof BigDecimal)) {
				throw new ParserException("getDistance(): Second parameter must be a number");
			} 
			BigDecimal val = (BigDecimal)args.get(1);
			useDistancePerCell = val.equals(BigDecimal.ZERO) ? false : true;
		}
		
		double dist = getDistance(source, target, useDistancePerCell);
		
		if (dist == Math.floor(dist)) {
			return BigDecimal.valueOf((int)dist);
		} else {
			return BigDecimal.valueOf(dist);
		}
		
	}

	/**
	 * Gets the distance to an x,y location. 
	 * @param res The variable resolver.
	 * @param args arguments to the function.
	 * @return the distance between tokens.
	 * @throws ParserException if an error occurs.
	 */
	private BigDecimal getDistanceToXY(MapToolVariableResolver res, List<Object> args) throws ParserException {
		if (args.size() < 2) {
			throw new ParserException("getDistanceToXY(): Not enough parameters");
		}

		Token source = getTokenFromParam(res, "getDistanceToXY", args, 3);
		

		if (!(args.get(0) instanceof BigDecimal)) {
			throw new ParserException("getDistanceToXY(): First parameter must be a number");
		} 
		if (!(args.get(1) instanceof BigDecimal)) {
			throw new ParserException("getDistanceToXY(): Second parameter must be a number");
		} 
		
		int x = ((BigDecimal)args.get(0)).intValue();
		int y = ((BigDecimal)args.get(1)).intValue();

		boolean useDistancePerCell = true;
		if (args.size() > 2) {
			if (!(args.get(2) instanceof BigDecimal)) {
				throw new ParserException("getDistanceToXY(): Third parameter must be a number");
			} 
			BigDecimal val = (BigDecimal)args.get(2);
			useDistancePerCell = val.equals(BigDecimal.ZERO) ? false : true;
		}
		
		double dist = getDistance(source, x, y, useDistancePerCell);
		
		if (dist == Math.floor(dist)) {
			return BigDecimal.valueOf((int)dist);
		} else {
			return BigDecimal.valueOf(dist);
		}
		
	}
	
	/**
	 * Moves a token to the specified x,y location.
	 * @param token The token to move.
	 * @param x the x co-ordinate of the destination.
	 * @param y the y co-ordinate of the destination.
	 * @param z the z order of the destination.
	 * @param units use map units or not.
	 */
	private void moveToken(Token token, int x, int y, int z, boolean units) {
		Grid grid = MapTool.getFrame().getCurrentZoneRenderer().getZone().getGrid();
		Dimension dim = grid.getCellOffset();

		x = units ? x / getDistancePerCell() : x;
		y = units ? y / getDistancePerCell() : y;

		x = x * grid.getSize() + dim.width + grid.getOffsetX();
		y = y * grid.getSize() - dim.height + grid.getOffsetY();
		
 
		token.setX(x);
		token.setY(y);
		
		if (z >= 0) {
			token.setZOrder(z);
		}
		
 		MapTool.serverCommand().putToken(MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(), token);

		
	}

	/**
	 * Moves a token to the specified location.
	 * @param token The token to move.
	 * @param args the arguments to the function.
	 */
	private String moveToken(MapToolVariableResolver res, List<Object> args) throws ParserException {
		Token token = getTokenFromParam(res, "moveToken", args, 4);
		boolean useDistance = true;
		
		if (args.size() < 2) {
			throw new ParserException("moveToken(): Not enough parameters");
		}
	
		int x,y,z;
		
		if (!(args.get(0) instanceof BigDecimal)) {
			throw new ParserException("moveToken(): First Parameter must be a number");
		} 
		if (!(args.get(1) instanceof BigDecimal)) {
			throw new ParserException("moveToken(): Second Parameter must be a number");
		} 
		
		x = ((BigDecimal)args.get(0)).intValue(); 
		y = ((BigDecimal)args.get(1)).intValue(); 
		
		if (args.size() > 2) {
			if (!(args.get(2) instanceof BigDecimal)) {
				throw new ParserException("moveToken(): Third Parameter must be a number");
			}
			z = ((BigDecimal)args.get(2)).intValue(); 
		} else {
			z = token.getZOrder();
		}
		
		if (args.size() > 3) {
			if (!(args.get(3) instanceof BigDecimal)) {
				throw new ParserException("moveToken(): Fourth Parameter must be a number");
			}
			BigDecimal val = (BigDecimal)args.get(3);
			useDistance = val.equals(BigDecimal.ZERO) ? false : true;			
		}
		
		moveToken(token, x, y, z, useDistance);
	
		return "";
	}
	
	/**
	 * Centers the map on a new location.
	 * @param res The variable resolver.
	 * @param args The arguments to the function.
	 * @return an empty string.
	 * @throws ParserException if an error occurs.
	 */
	private String gotoLoc(MapToolVariableResolver res, List<Object> args) throws ParserException {
		Token token = null;
		int x;
		int y;
		
		if (!MapTool.getParser().isMacroTrusted()) {
			throw new ParserException("goto(): You do not have permission to call this function");
		}
		
		if (args.size() < 2) {
			token = getTokenFromParam(res, "goto", args, 0);
			x = token.getX();
			y = token.getY();
			MapTool.getFrame().getCurrentZoneRenderer().centerOn(new ZonePoint(x, y));
		} else {
			
			if (!(args.get(0) instanceof BigDecimal)) {
				throw new ParserException("goto(x,y,...): First parameter must be a number");
			} 

			if (!(args.get(1) instanceof BigDecimal)) {
				throw new ParserException("goto(x,y,...): Second parameter must be a number");
			} 
			
			x = ((BigDecimal)args.get(0)).intValue();
			y = ((BigDecimal)args.get(1)).intValue();
			MapTool.getFrame().getCurrentZoneRenderer().centerOn(new CellPoint(x, y));
		}

		return "";
	}
	
	
	/**
	 * Gets the distance for each cell.
	 * @return the distance for each cell.
	 */
	private int getDistancePerCell() {
		return MapTool.getFrame().getCurrentZoneRenderer().getZone().getUnitsPerCell();
	}
	
	/**
	 * Gets the cell point that the token is at.
	 * @param token
	 * @return
	 */
	public CellPoint getTokenCell(Token token) {
		Zone zone = MapTool.getFrame().getCurrentZoneRenderer().getZone();
		return zone.getGrid().convert(new ZonePoint(token.getX(), token.getY()));
	}
	
	
	
	/**
	 * Gets the token from the specified index or returns the token in context. This method
	 * will check the list size before trying to retrieve the token so it is safe to use
	 * for functions that have the token as a optional argument.
	 * @param res The variable resolver.
	 * @param functionName The function name (used for generating exception messages).
	 * @param param The parameters for the function.
	 * @param index The index to find the token at.
	 * @return the token.
	 * @throws ParserException if a token is specified but the macro is not trusted, or the 
	 *                         specified token can not be found, or if no token is specified
	 *                         and no token is impersonated.
	 */
	private Token getTokenFromParam(MapToolVariableResolver res, String functionName, List<Object> param, int index) throws ParserException {
		Token token;
		if (param.size() > index) {
			if (!MapTool.getParser().isMacroTrusted()) {
				throw new ParserException(functionName + "(): You do not have permission to refer to another token");
			}
			
			token = FindTokenFunctions.findToken(param.get(index).toString(), null);
			if (token == null) {
				throw new ParserException(functionName + "(): Unknown token or id" + param.get(index));
			}
		} else {
			token = res.getTokenInContext();
			if (token == null) {
				throw new ParserException(functionName + "(): No impersonated token");
			}
		}
		return token;
	}

}
