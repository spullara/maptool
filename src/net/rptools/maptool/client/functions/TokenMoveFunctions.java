/**
 * 
 */
package net.rptools.maptool.client.functions;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolVariableResolver;
import net.rptools.maptool.client.functions.AbortFunction.AbortFunctionException;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.client.ui.zone.ZoneRenderer.TokenMoveCompletion;
import net.rptools.maptool.language.I18N;
import net.rptools.maptool.model.AbstractPoint;
import net.rptools.maptool.model.CellPoint;
import net.rptools.maptool.model.Grid;
import net.rptools.maptool.model.Path;
import net.rptools.maptool.model.Player;
import net.rptools.maptool.model.SquareGrid;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.TokenFootprint;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.ZonePoint;
import net.rptools.maptool.tool.TokenFootprintCreator;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * @author Joe.Frazier
 *
 */
public class TokenMoveFunctions extends AbstractFunction {

	
	private final static TokenMoveFunctions instance = new TokenMoveFunctions();
	private static final String ON_TOKEN_MOVE_COMPLETE_CALLBACK = "onTokenMove";
	private static final Logger log = Logger.getLogger(TokenMoveFunctions.class);
	
	private TokenMoveFunctions() {
		super(0,2, "getLastPath", "movedOverToken");
		
	}


	public static TokenMoveFunctions getInstance() {
		log.setLevel(Level.INFO);
		return instance;
	}
	
	@Override
	public Object childEvaluate(Parser parser, String functionName,
			List<Object> parameters) throws ParserException {
		
		final Token tokenInContext = ((MapToolVariableResolver)parser.getVariableResolver()).getTokenInContext();
		if (tokenInContext == null) {
			throw new ParserException(I18N.getText("macro.function.general.noImpersonated", functionName));
		}
		
		boolean useDistancePerCell = true;
		if ( log.isInfoEnabled()) {
			log.info("DEVELOPMENT: in childEvaluate.  Getting zone");
		}
		Zone zone = MapTool.getFrame().getCurrentZoneRenderer().getZone();
		if ( log.isInfoEnabled()) {
			log.info("DEVELOPMENT: in childEvaluate.  Got zone");
		}
		if (functionName.equals("getLastPath")) {	
			BigDecimal val = null;
			if (parameters.size() ==1) {

				if (!(parameters.get(0) instanceof BigDecimal)) {
					throw new ParserException(I18N
							.getText("macro.function.general.argumentTypeN",
									functionName, 1));
				}
				val = (BigDecimal) parameters.get(0);
				useDistancePerCell = val != null && val.equals(BigDecimal.ZERO) ? false
						: true;
			}
			if ( log.isInfoEnabled()) {
				log.info("DEVELOPMENT: in childEvaluate.  Getting Last path");
			}
			Path<?> path = tokenInContext.getLastPath();
			if ( log.isInfoEnabled()) {
				log.info("DEVELOPMENT: in childEvaluate.  Got last Path");
			}
			List<Map<String, Integer>> pathPoints = getLastPathList(path, useDistancePerCell);
			if ( log.isInfoEnabled()) {
				log.info("DEVELOPMENT: in childEvaluate.  Translated path to pathPoints list");
			}
			return pathPointsToJSONArray(pathPoints);
			
		}
		if(functionName.equals("movedOverToken"))
		{
			//macro.function.general.noPerm
			if(!MapTool.getParser().isMacroTrusted())
			{
				throw new ParserException(I18N.getText("macro.function.general.noPerm",	functionName));
			}
			if ( log.isInfoEnabled()) {
				log.info("DEVELOPMENT: in childEvaluate.  Getting Last path");
			}
			Path<?> path = tokenInContext.getLastPath();
			if ( log.isInfoEnabled()) {
				log.info("DEVELOPMENT: in childEvaluate.  Got last Path");
			}
			List<Map<String, Integer>> returnPoints = new ArrayList<Map<String, Integer>>();
			Token target;
			
			if((parameters.size()==1) || parameters.size()==2 )
			{
				String targetToken = (String) parameters.get(0);
				String jsonPath = (String) (parameters.size() == 2? parameters.get(1) : "");
				target = zone.getTokenByName(targetToken);
				if(target == null)
				{
					throw new ParserException(I18N.getText("macro.function.general.unknownToken",functionName, targetToken));
				}
				
				List<Map<String, Integer>> pathPoints = null;
				if(jsonPath != null && !jsonPath.equals(""))
				{
						
					returnPoints = crossedToken(zone,tokenInContext, target, jsonPath	);
				}
				else 
				{
					pathPoints = getLastPathList(path, true);
					returnPoints = crossedToken(zone,tokenInContext, target, pathPoints);
				}
				if ( log.isInfoEnabled()) {
					log.info("DEVELOPMENT: in childEvaluate.  Translated path to pathPoints list");
				}
				
				if ( log.isInfoEnabled()) {
					log.info("DEVELOPMENT: in childEvaluate.  Got Return Points from moved over Token");
				}
				JSONArray retVal = pathPointsToJSONArray(returnPoints);
				returnPoints = null;
				return retVal;
			}
			else
			{
				throw new ParserException(I18N.getText("macro.function.general.wrongNumParam",functionName, 2, parameters.size( )));
			}
		}		
		return null;
	}

	private List<Map<String, Integer>> crossedToken(final Zone zone, final Token tokenInContext, final Token target,
			final String pathString) {
		Object jsonObject = JSONMacroFunctions.asJSON(pathString);
		
		ArrayList<Map<String, Integer>> pathPoints = new ArrayList<Map<String, Integer>>() ;
		if(jsonObject instanceof JSONArray)
		{
			ArrayList<?> tempPoints = (ArrayList<?>) JSONArray.toCollection((JSONArray) jsonObject);
			
			for(Object o: tempPoints)
			{
				MorphDynaBean bean = (MorphDynaBean)o;
				//System.out.println(bean.get("x"));
				Map<String, Integer> point = new HashMap<String, Integer>();
				point.put("x", (Integer) bean.get("x"));
				point.put("y", (Integer) bean.get("y"));
				pathPoints.add(point);
			}
			return getInstance().crossedToken(zone,tokenInContext, target, pathPoints);
		}
		return pathPoints;
	
		
	}

	/**
	 * @param zone
	 * @param target
	 * @param pathPoints
	 * @return
	 */
	private List<Map<String, Integer>> crossedToken(final Zone zone, final Token tokenInContext, final Token target,
			final List<Map<String, Integer>> pathPoints) {
		List<Map<String, Integer>> returnPoints = new ArrayList<Map<String, Integer>>();
		
		if ( log.isInfoEnabled()) {
			log.info("DEVELOPMENT: in crossedToken.  Looping over all of the movement points:" + pathPoints.size());
		}
		if(pathPoints == null)
		{
			return returnPoints;
		}
		for(Map<String, Integer> entry: pathPoints)
		{
			Map<String, Integer> thePoint = new HashMap<String, Integer>();

			Grid grid = zone.getGrid();
			
			Rectangle originalArea = null;
			if ( log.isInfoEnabled()) {
				log.info("DEVELOPMENT: in crossedToken.  Getting Footprint:" );
			}
			if (tokenInContext.isSnapToGrid()) {
				originalArea = tokenInContext.getFootprint(grid).getBounds(grid, grid.convert(new ZonePoint(entry.get("x"), entry.get("y"))));
			} else {
				originalArea = tokenInContext.getBounds(zone);
			}
			if ( log.isInfoEnabled()) {
				log.info("DEVELOPMENT: in crossedToken.  Got footprint for this movement cellpoint" );
			}
			
			Rectangle targetArea = target.getBounds(zone);
			if(targetArea.intersects(originalArea) || originalArea.intersects(targetArea))
			{
				if ( log.isInfoEnabled()) {
					log.info("DEVELOPMENT: in crossedToken.  Found a match!  Adding to the list" );
				}
				thePoint.put("x", entry.get("x"));
				thePoint.put("y", entry.get("y"));
				returnPoints.add(thePoint);
			}
			thePoint= null;
		}
		return returnPoints;
	}	

	private JSONArray pathPointsToJSONArray( final List<Map<String, Integer>> pathPoints)
	{
		if ( log.isInfoEnabled()) {
			log.info("DEVELOPMENT: in pathPointsToJSONArrayt.  Converting list to JSONArray");
		}
		JSONArray jsonArr = new JSONArray();
		JSONObject pointObj = new JSONObject();
		if(pathPoints == null)
		{
			return jsonArr;
		}
		for(Map<String, Integer> entry: pathPoints)
		{
			pointObj.element("x", entry.get("x"));
			pointObj.element("y", entry.get("y"));
			jsonArr.add(pointObj); 
		}
		if ( log.isInfoEnabled()) {
			log.info("DEVELOPMENT: in pathPointsToJSONArrayt.  return JSONArray");
		}
		return jsonArr;
	}
	private List<Map<String, Integer>> getLastPathList(final Path<?> path, final boolean useDistancePerCell)
	{
		List<Map<String, Integer>> points = new ArrayList<Map<String, Integer>>();
		if(path != null)
		{
			Zone zone = MapTool.getFrame().getCurrentZoneRenderer().getZone();
			AbstractPoint zp = null;
			
			if ( log.isInfoEnabled()) {
				log.info("DEVELOPMENT: in getLastPathList.  Loop over each path elements");
			}
			for( Object pathCells: path.getCellPath())
			{
				if ( log.isInfoEnabled()) {
					log.info("DEVELOPMENT: in getLastPathList.  Converting each path item to a cell point or zone point.");
				}
				if (pathCells instanceof CellPoint)
				{
					CellPoint cp = (CellPoint) pathCells;
					if(useDistancePerCell)
					{
						zp = zone.getGrid().convert((CellPoint) pathCells);
					}
					else
					{
						zp = cp;
					}
				}
				else
				{
					zp = (ZonePoint) pathCells;
				}
				if (zp != null)
				{
					if ( log.isInfoEnabled()) {
						log.info("DEVELOPMENT: in getLastPathList.  Got a point, adding to list.");
					}
					Map<String, Integer> tokenLocationPoint = new HashMap<String, Integer>();
					tokenLocationPoint.put("x", Integer.valueOf(zp.x));
					tokenLocationPoint.put("y", Integer.valueOf(zp.y));
					points.add(tokenLocationPoint);
				}
			}
		}	
		return points;
	}
	public static BigDecimal tokenMoveCompleteFunctions(final Token originalToken, final Path path) {
		
		List<ZoneRenderer> zrenderers = MapTool.getFrame().getZoneRenderers();
		for (ZoneRenderer zr : zrenderers) {
			List<Token> tokenList = zr.getZone().getTokensFiltered(
					new Zone.Filter() {
						public boolean matchToken(Token t) {
							return t.getName().toLowerCase().startsWith("lib:");
						}
					});

			for (Token token : tokenList) {
				// If the token is not owned by everyone and all owners are GMs
				// then we are in
				// its a trusted Lib:token so we can run the macro
				if (token != null) {
					if (token.isOwnedByAll()) {
						continue;
					} else {
						Set<String> gmPlayers = new HashSet<String>();
						for (Object o : MapTool.getPlayerList()) {
							Player p = (Player) o;
							if (p.isGM()) {
								gmPlayers.add(p.getName());
							}
						}
						for (String owner : token.getOwners()) {
							if (!gmPlayers.contains(owner)) {
								continue;
							}
						}
					}
				}
				
				List<Map<String, Integer>> pathPoints = getInstance().getLastPathList(path, true);
				JSONArray pathArr = getInstance().pathPointsToJSONArray(pathPoints);
				String pathCoordinates = pathArr.toString();
				// If we get here it is trusted so try to execute it.
				if (token.getMacro(ON_TOKEN_MOVE_COMPLETE_CALLBACK, false) != null) {
					try {
						String resultVal = MapTool.getParser().runMacro(
								new MapToolVariableResolver(originalToken),
								originalToken,
								ON_TOKEN_MOVE_COMPLETE_CALLBACK + "@"
										+ token.getName(), pathCoordinates);
						//MapTool.addLocalMessage("Return Value :'" + resultVal + "'");
						return (resultVal.equals("0") || resultVal.equalsIgnoreCase("false"))? BigDecimal.ZERO : BigDecimal.ONE;
						
					} catch (AbortFunctionException afe) {
						// Do nothing
					} catch (Exception e) {
						MapTool.addLocalMessage("Error running "
								+ ON_TOKEN_MOVE_COMPLETE_CALLBACK+ " on "
								+ token.getName() + " : " + e.getMessage());
					}
				}
			}
		}
		return BigDecimal.ONE;
	}
}
