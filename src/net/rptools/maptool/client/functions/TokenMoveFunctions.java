/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *  
 *	http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package net.rptools.maptool.client.functions;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.rptools.maptool.client.AppPreferences;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolVariableResolver;
import net.rptools.maptool.client.functions.AbortFunction.AbortFunctionException;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.client.walker.WalkerMetric;
import net.rptools.maptool.client.walker.ZoneWalker;
import net.rptools.maptool.language.I18N;
import net.rptools.maptool.model.AbstractPoint;
import net.rptools.maptool.model.CellPoint;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Grid;
import net.rptools.maptool.model.Path;
import net.rptools.maptool.model.Player;
import net.rptools.maptool.model.TextMessage;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.ZonePoint;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

/**
 * @author Joe.Frazier
 * 
 */
public class TokenMoveFunctions extends AbstractFunction {

	private final static TokenMoveFunctions instance = new TokenMoveFunctions();
	private static final String ON_TOKEN_MOVE_COMPLETE_CALLBACK = "onTokenMove";
	private static final String ON_MULTIPLE_TOKENS_MOVED_COMPLETE_CALLBACK = "onMultipleTokensMove";
	private static final String NO_GRID = "NO_GRID";

	private static final Logger log = Logger.getLogger(TokenMoveFunctions.class);

	private TokenMoveFunctions() {
		super(0, 2, "getLastPath", "movedOverToken", "movedOverPoints", "getMoveCount");
	}

	public static TokenMoveFunctions getInstance() {
//		log.setLevel(Level.INFO);
		return instance;
	}

	@Override
	public Object childEvaluate(Parser parser, String functionName, List<Object> parameters) throws ParserException {
		final Token tokenInContext = ((MapToolVariableResolver) parser.getVariableResolver()).getTokenInContext();
		if (tokenInContext == null) {
			throw new ParserException(I18N.getText("macro.function.general.noImpersonated", functionName));
		}
		boolean useDistancePerCell = true;
		Zone zone = MapTool.getFrame().getCurrentZoneRenderer().getZone();

		if (functionName.equals("getLastPath")) {
			BigDecimal val = null;
			if (parameters.size() == 1) {
				if (!(parameters.get(0) instanceof BigDecimal)) {
					throw new ParserException(I18N.getText("macro.function.general.argumentTypeN", functionName, 1));
				}
				val = (BigDecimal) parameters.get(0);
				useDistancePerCell = val != null && val.equals(BigDecimal.ZERO) ? false : true;
			}
			Path<?> path = tokenInContext.getLastPath();

			List<Map<String, Integer>> pathPoints = getLastPathList(path, useDistancePerCell);
			return pathPointsToJSONArray(pathPoints);
		}
		if (functionName.equals("movedOverPoints")) {
			//macro.function.general.noPerm
			if (!MapTool.getParser().isMacroTrusted()) {
				throw new ParserException(I18N.getText("macro.function.general.noPerm", functionName));
			}
			Path<?> path = tokenInContext.getLastPath();

			List<Map<String, Integer>> returnPoints = new ArrayList<Map<String, Integer>>();
			Token target;

			if ((parameters.size() == 1) || parameters.size() == 2) {
				String points = (String) parameters.get(0);
				String jsonPath = (String) (parameters.size() == 2 ? parameters.get(1) : "");

				List<Map<String, Integer>> pathPoints = null;
				if (jsonPath != null && !jsonPath.equals("")) {
					returnPoints = crossedPoints(zone, tokenInContext, points, jsonPath);
				} else {
					pathPoints = getLastPathList(path, true);
					returnPoints = crossedPoints(zone, tokenInContext, points, pathPoints);
				}
				JSONArray retVal = pathPointsToJSONArray(returnPoints);
				returnPoints = null;
				return retVal;
			} else {
				throw new ParserException(I18N.getText("macro.function.general.wrongNumParam", functionName, 2, parameters.size()));
			}
		}
		if (functionName.equals("getMoveCount")) {
			return getMovement(tokenInContext);
		}
		if (functionName.equals("movedOverToken")) {
			//macro.function.general.noPerm
			if (!MapTool.getParser().isMacroTrusted()) {
				throw new ParserException(I18N.getText("macro.function.general.noPerm", functionName));
			}
			Path<?> path = tokenInContext.getLastPath();
			List<Map<String, Integer>> returnPoints = new ArrayList<Map<String, Integer>>();
			Token target;

			if ((parameters.size() == 1) || parameters.size() == 2) {
				String targetToken = (String) parameters.get(0);
				String jsonPath = (String) (parameters.size() == 2 ? parameters.get(1) : "");
				target = zone.resolveToken(targetToken);
				if (target == null) {
					throw new ParserException(I18N.getText("macro.function.general.unknownToken", functionName, targetToken));
				}
				List<Map<String, Integer>> pathPoints = null;
				if (jsonPath != null && !jsonPath.equals("")) {
					returnPoints = crossedToken(zone, tokenInContext, target, jsonPath);
				} else {
					pathPoints = getLastPathList(path, true);
					returnPoints = crossedToken(zone, tokenInContext, target, pathPoints);
				}
				JSONArray retVal = pathPointsToJSONArray(returnPoints);
				returnPoints = null;
				return retVal;
			} else {
				throw new ParserException(I18N.getText("macro.function.general.wrongNumParam", functionName, 2, parameters.size()));
			}
		}
		return null;
	}

	private List<Map<String, Integer>> crossedToken(final Zone zone, final Token tokenInContext, final Token target, final String pathString) {
		Object jsonObject = JSONMacroFunctions.asJSON(pathString);

		ArrayList<Map<String, Integer>> pathPoints = new ArrayList<Map<String, Integer>>();
		if (jsonObject instanceof JSONArray) {
			ArrayList<?> tempPoints = (ArrayList<?>) JSONArray.toCollection((JSONArray) jsonObject);

			for (Object o : tempPoints) {
				MorphDynaBean bean = (MorphDynaBean) o;
				//System.out.println(bean.get("x"));
				Map<String, Integer> point = new HashMap<String, Integer>();
				point.put("x", (Integer) bean.get("x"));
				point.put("y", (Integer) bean.get("y"));
				pathPoints.add(point);
			}
			return getInstance().crossedToken(zone, tokenInContext, target, pathPoints);
		}
		return pathPoints;
	}

	private List<Map<String, Integer>> crossedPoints(final Zone zone, final Token tokenInContext, final String pointsString, final String pathString) {
		List<Map<String, Integer>> pathPoints = convertJSONStringToList(pathString);

		pathPoints = getInstance().crossedPoints(zone, tokenInContext, pointsString, pathPoints);
		return pathPoints;
	}

	/**
	 * @param zone
	 * @param target
	 * @param pathPoints
	 * @return
	 */
	private List<Map<String, Integer>> crossedPoints(final Zone zone, final Token tokenInContext, final String pointsString, final List<Map<String, Integer>> pathPoints) {
		List<Map<String, Integer>> returnPoints = new ArrayList<Map<String, Integer>>();

		List<Map<String, Integer>> targetPoints = convertJSONStringToList(pointsString);
		if (pathPoints == null) {
			return returnPoints;
		}
		for (Map<String, Integer> entry : pathPoints) {
			Map<String, Integer> thePoint = new HashMap<String, Integer>();
			Grid grid = zone.getGrid();
			Rectangle originalArea = null;
			Polygon targetArea = new Polygon();
			for (Map<String, Integer> points : targetPoints) {
				int x = points.get("x");
				int y = points.get("y");
				targetArea.addPoint(x, y);
			}
			if (tokenInContext.isSnapToGrid()) {
				originalArea = tokenInContext.getFootprint(grid).getBounds(grid, grid.convert(new ZonePoint(entry.get("x"), entry.get("y"))));
			} else {
				originalArea = tokenInContext.getBounds(zone);
			}
			Rectangle2D oa = originalArea.getBounds2D();
			if (targetArea.contains(oa) || targetArea.intersects(oa)) {
				thePoint.put("x", entry.get("x"));
				thePoint.put("y", entry.get("y"));
				returnPoints.add(thePoint);
			}
			thePoint = null;
		}
		return returnPoints;
	}

	/**
	 * @param zone
	 * @param target
	 * @param pathPoints
	 * @return
	 */
	private List<Map<String, Integer>> crossedToken(final Zone zone, final Token tokenInContext, final Token target, final List<Map<String, Integer>> pathPoints) {
		List<Map<String, Integer>> returnPoints = new ArrayList<Map<String, Integer>>();

		if (pathPoints == null) {
			return returnPoints;
		}
		for (Map<String, Integer> entry : pathPoints) {
			Map<String, Integer> thePoint = new HashMap<String, Integer>();
			Grid grid = zone.getGrid();
			Rectangle originalArea = null;

			if (tokenInContext.isSnapToGrid()) {
				originalArea = tokenInContext.getFootprint(grid).getBounds(grid, grid.convert(new ZonePoint(entry.get("x"), entry.get("y"))));
			} else {
				originalArea = tokenInContext.getBounds(zone);
			}
			Rectangle targetArea = target.getBounds(zone);
			if (targetArea.intersects(originalArea) || originalArea.intersects(targetArea)) {
				thePoint.put("x", entry.get("x"));
				thePoint.put("y", entry.get("y"));
				returnPoints.add(thePoint);
			}
			thePoint = null;
		}
		return returnPoints;
	}

	private JSONArray pathPointsToJSONArray(final List<Map<String, Integer>> pathPoints) {
		if (log.isInfoEnabled()) {
			log.info("DEVELOPMENT: in pathPointsToJSONArrayt.  Converting list to JSONArray");
		}
		JSONArray jsonArr = new JSONArray();
		JSONObject pointObj = new JSONObject();
		if (pathPoints == null) {
			return jsonArr;
		}
		for (Map<String, Integer> entry : pathPoints) {
			pointObj.element("x", entry.get("x"));
			pointObj.element("y", entry.get("y"));
			jsonArr.add(pointObj);
		}
		if (log.isInfoEnabled()) {
			log.info("DEVELOPMENT: in pathPointsToJSONArrayt.  return JSONArray");
		}
		return jsonArr;
	}

	private List<Map<String, Integer>> getLastPathList(final Path<?> path, final boolean useDistancePerCell) {
		List<Map<String, Integer>> points = new ArrayList<Map<String, Integer>>();
		if (path != null) {
			Zone zone = MapTool.getFrame().getCurrentZoneRenderer().getZone();
			AbstractPoint zp = null;

			if (log.isInfoEnabled()) {
				log.info("DEVELOPMENT: in getLastPathList.  Loop over each path elements");
			}
			for (Object pathCells : path.getCellPath()) {
				if (log.isInfoEnabled()) {
					log.info("DEVELOPMENT: in getLastPathList.  Converting each path item to a cell point or zone point.");
				}
				if (pathCells instanceof CellPoint) {
					CellPoint cp = (CellPoint) pathCells;
					if (useDistancePerCell) {
						zp = zone.getGrid().convert((CellPoint) pathCells);
					} else {
						zp = cp;
					}
				} else {
					zp = (ZonePoint) pathCells;
				}
				if (zp != null) {
					if (log.isInfoEnabled()) {
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

	public static BigDecimal tokenMoved(final Token originalToken, final Path<?> path, final List<GUID> filteredTokens) {
		Token token = getMoveMacroToken(ON_TOKEN_MOVE_COMPLETE_CALLBACK);

		List<Map<String, Integer>> pathPoints = getInstance().getLastPathList(path, true);
		JSONArray pathArr = getInstance().pathPointsToJSONArray(pathPoints);
		String pathCoordinates = pathArr.toString();
		// If we get here it is trusted so try to execute it.
		if (token != null) {
			try {
				MapToolVariableResolver newResolver = new MapToolVariableResolver(null);
				newResolver.setVariable("tokens.denyMove", 0);
				newResolver.setVariable("tokens.moveCount", filteredTokens.size());
				newResolver.setTokenIncontext(originalToken);
				String resultVal = MapTool.getParser().runMacro(newResolver, originalToken, ON_TOKEN_MOVE_COMPLETE_CALLBACK + "@" + token.getName(), pathCoordinates, false);
				if (resultVal != null && !resultVal.equals("")) {
					MapTool.addMessage(new TextMessage(TextMessage.Channel.SAY, null, MapTool.getPlayer().getName(), resultVal, null));
				}
				BigDecimal denyMove = BigDecimal.ZERO;

				if (newResolver.getVariable("tokens.denyMove") instanceof BigDecimal) {
					denyMove = (BigDecimal) newResolver.getVariable("tokens.denyMove");
				}
				return (denyMove != null && denyMove.intValue() == 1) ? BigDecimal.ONE : BigDecimal.ZERO;
			} catch (AbortFunctionException afe) {
				// Do nothing
			} catch (Exception e) {
				MapTool.addLocalMessage("Error running " + ON_TOKEN_MOVE_COMPLETE_CALLBACK + " on " + token.getName() + " : " + e.getMessage());
			}
		}
		return BigDecimal.ZERO;
	}

	private static Token getMoveMacroToken(final String macroCallback) {
		List<ZoneRenderer> zrenderers = MapTool.getFrame().getZoneRenderers();
		for (ZoneRenderer zr : zrenderers) {
			List<Token> tokenList = zr.getZone().getTokensFiltered(new Zone.Filter() {
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
				if (token.getMacro(macroCallback, false) != null) {
					return token;
				}
			}
		}
		return null;
	}

	private String getMovement(final Token source) throws ParserException {
		ZoneWalker walker = null;

		WalkerMetric metric = MapTool.isPersonalServer() ? AppPreferences.getMovementMetric() : MapTool.getServerPolicy().getMovementMetric();

		ZoneRenderer zr = MapTool.getFrame().getCurrentZoneRenderer();
		Zone zone = zr.getZone();
		Grid grid = zone.getGrid();

		Path<ZonePoint> gridlessPath;
		int x = source.getLastPath().getCellPath().get(0).x;
		int y = source.getLastPath().getCellPath().get(0).y;

		if (source.isSnapToGrid() && grid.getCapabilities().isSnapToGridSupported()) {
			if (zone.getGrid().getCapabilities().isPathingSupported()) {
				List<CellPoint> cplist = new ArrayList<CellPoint>();
				walker = grid.createZoneWalker();
				walker.replaceLastWaypoint(new CellPoint(x, y));
				for (AbstractPoint point : source.getLastPath().getCellPath()) {
					CellPoint tokenPoint = new CellPoint(point.x, point.y);
					//walker.setWaypoints(tokenPoint);
					walker.replaceLastWaypoint(tokenPoint);
					cplist.add(tokenPoint);
				}
				int bar = calculateGridDistance(cplist, zone.getUnitsPerCell(), metric);
				return Integer.valueOf(bar).toString();

				//return  Integer.toString(walker.getDistance());
			}
		} else {
			gridlessPath = new Path<ZonePoint>();
			for (AbstractPoint point : source.getLastPath().getCellPath()) {
				gridlessPath.addPathCell(new ZonePoint(point.x, point.y));
			}
			double c = 0;
			ZonePoint lastPoint = null;
			for (ZonePoint zp : gridlessPath.getCellPath()) {
				if (lastPoint == null) {
					lastPoint = zp;
					continue;
				}
				int a = lastPoint.x - zp.x;
				int b = lastPoint.y - zp.y;
				c += Math.hypot(a, b);
				lastPoint = zp;
			}
			c /= zone.getGrid().getSize(); // Number of "cells"
			c *= zone.getUnitsPerCell(); // "actual" distance traveled
			return String.format("%.1f", c);
		}
		return "";
	}

	public static BigDecimal multipleTokensMoved(List<GUID> filteredTokens) {
		Token token = getMoveMacroToken(ON_MULTIPLE_TOKENS_MOVED_COMPLETE_CALLBACK);
		if (token != null) {
			try {
				JSONArray json = new JSONArray();
				for (GUID tokenGuid : filteredTokens) {
					json.add(tokenGuid.toString());
				}
				MapToolVariableResolver newResolver = new MapToolVariableResolver(null);
				newResolver.setVariable("tokens.denyMove", 0);
				newResolver.setVariable("tokens.moveCount", filteredTokens.size());
				String resultVal = MapTool.getParser().runMacro(newResolver, null, ON_MULTIPLE_TOKENS_MOVED_COMPLETE_CALLBACK + "@" + token.getName(), json.toString(), false);
				if (resultVal != null && !resultVal.equals("")) {
					MapTool.addMessage(new TextMessage(TextMessage.Channel.ALL, null, MapTool.getPlayer().getName(), resultVal, null));
				}
				BigDecimal denyMove = BigDecimal.ZERO;

				if (newResolver.getVariable("tokens.denyMove") instanceof BigDecimal) {
					denyMove = (BigDecimal) newResolver.getVariable("tokens.denyMove");
				}
				return (denyMove != null && denyMove.intValue() == 1) ? BigDecimal.ONE : BigDecimal.ZERO;
			} catch (AbortFunctionException afe) {
				// Do nothing
			} catch (Exception e) {
				MapTool.addLocalMessage("Error running " + ON_MULTIPLE_TOKENS_MOVED_COMPLETE_CALLBACK + " on " + token.getName() + " : " + e.getMessage());
			}
		}
		return BigDecimal.ZERO;
	}

	private List<Map<String, Integer>> convertJSONStringToList(final String pointsString) {
		Object jsonObject = JSONMacroFunctions.asJSON(pointsString);

		ArrayList<Map<String, Integer>> pathPoints = new ArrayList<Map<String, Integer>>();
		if (jsonObject instanceof JSONArray) {
			ArrayList<?> tempPoints = (ArrayList<?>) JSONArray.toCollection((JSONArray) jsonObject);
			for (Object o : tempPoints) {
				MorphDynaBean bean = (MorphDynaBean) o;
				Map<String, Integer> point = new HashMap<String, Integer>();
				point.put("x", (Integer) bean.get("x"));
				point.put("y", (Integer) bean.get("y"));
				pathPoints.add(point);
			}
		}
		return pathPoints;
	}

	public int calculateGridDistance(List<CellPoint> path, int feetPerCell, WalkerMetric metric) {
		if (path == null || path.size() == 0)
			return 0;

		final int feetDistance;

		{
			int numDiag = 0;
			int numStrt = 0;

			CellPoint previousPoint = null;
			for (CellPoint point : path) {
				if (previousPoint != null) {
					int change = Math.abs(previousPoint.x - point.x) + Math.abs(previousPoint.y - point.y);
					switch (change) {
					case 1:
						numStrt++;
						break;
					case 2:
						numDiag++;
						break;
					default:
						assert false : String.format("Illegal path, cells are not contiguous change=%d", change);
						return -1;
					}
				}
				previousPoint = point;
			}
			final int cellDistance;
			switch (metric) {
			case MANHATTAN:
			case NO_DIAGONALS:
				cellDistance = (numStrt + numDiag * 2);
				break;
			case ONE_ONE_ONE:
				cellDistance = (numStrt + numDiag);
				break;
			default:
			case ONE_TWO_ONE:
				cellDistance = (numStrt + numDiag + numDiag / 2);
				break;
			}
			feetDistance = cellDistance * feetPerCell;
		}
		return feetDistance;
	}
}
