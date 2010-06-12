/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.rptools.maptool.client.functions;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.rptools.maptool.client.AppPreferences;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolVariableResolver;
import net.rptools.maptool.client.ui.tokenpanel.InitiativePanel;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.language.I18N;
import net.rptools.maptool.model.Grid;
import net.rptools.maptool.model.GridFactory;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.server.ServerPolicy;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;
import net.sf.json.JSONArray;
import net.sf.json.JSONFunction;
import net.sf.json.JSONObject;

public class getInfoFunction extends AbstractFunction {

	/** The singleton instance. */
	private static final getInfoFunction instance = new getInfoFunction();


	private getInfoFunction() {
		super(1, 1, "getInfo");
	}


	/**
	 * Gets the instance of getInfoFunction.
	 * @return the instance.
	 */
	public static getInfoFunction getInstance() {
		return instance;
	}




	@Override
	public Object childEvaluate(Parser parser, String functionName, List<Object> param)
	throws ParserException {
		String infoType = param.get(0).toString();
		
		
		
		if (infoType.equalsIgnoreCase("map") || infoType.equalsIgnoreCase("zone")) {
			return getMapInfo();
		} else if (infoType.equalsIgnoreCase("client")) {
			return getClientInfo();
		} else if (infoType.equals("server")) {
			return getServerInfo();
		} else {
			throw new ParserException(I18N.getText("macro.function.getInfo.invalidArg", param.get(0).toString()));
		}

	}

	
	/**
	 * Retrieves the information about the current zone/map and returns it as a JSON Object.
	 * @return The information about the map.
	 * @throws ParserException when there is an error.
	 */
	private JSONObject getMapInfo() throws ParserException {
		
		Map<String, Object> minfo = new HashMap<String, Object>();
		
		Zone zone = MapTool.getFrame().getCurrentZoneRenderer().getZone();
		
		if (!MapTool.getParser().isMacroTrusted()) {
			if (!zone.isVisible()) {
				throw new ParserException(I18N.getText("macro.function.general.noPerm", "getInfo('map')"));
			}
		}

		
		minfo.put("name", zone.getName());
		minfo.put("image x scale", zone.getImageScaleX());
		minfo.put("image y scale", zone.getImageScaleY());
		minfo.put("player visible", zone.isVisible() ? 1 : 0);

		if (MapTool.getParser().isMacroTrusted()) {
			minfo.put("id", zone.getId().toString());
			minfo.put("creation time", zone.getCreationTime());
			minfo.put("width", zone.getWidth());
			minfo.put("height", zone.getHeight());
			minfo.put("largest Z order", zone.getLargestZOrder());
		}
		
		String visionType = "off";
		switch (zone.getVisionType()) {
			case DAY:
				visionType = "day";
				break;
			case NIGHT:
				visionType = "night";
				break;
			case OFF:
				visionType = "off";
				break;
		}
		minfo.put("vision type", visionType);
				
		Map<String, Object> ginfo = new HashMap<String, Object>();
		minfo.put("grid", ginfo);

		Grid grid = zone.getGrid();
		
		ginfo.put("type", GridFactory.getGridType(grid));
		ginfo.put("color", String.format("%h", zone.getGridColor()));
		ginfo.put("units per cell", zone.getUnitsPerCell());
		ginfo.put("cell height", zone.getGrid().getCellHeight());
		ginfo.put("cell width", zone.getGrid().getCellWidth());
		ginfo.put("cell offset width", zone.getGrid().getCellOffset().getWidth());
		ginfo.put("cell offset height", zone.getGrid().getCellOffset().getHeight());
		ginfo.put("size", zone.getGrid().getSize());
		ginfo.put("x offset", zone.getGrid().getOffsetX());
		ginfo.put("y offset", zone.getGrid().getOffsetY());
		ginfo.put("second dimension", grid.getSecondDimension());
		
		
		return JSONObject.fromObject(minfo);
	}
	
	/**
	 * Retrieves the client side preferences that do not have server over rides as a
	 * json object.
	 * @return the client side preferences
	 */
	private JSONObject getClientInfo() {
		Map<String, Object> cinfo = new HashMap<String, Object>();

		cinfo.put("face edge", AppPreferences.getFaceEdge() ? BigDecimal.ONE : BigDecimal.ZERO);
		cinfo.put("face vertex", AppPreferences.getFaceVertex() ? BigDecimal.ONE : BigDecimal.ZERO);
		cinfo.put("movement metric", AppPreferences.getMovementMetric().toString());
		cinfo.put("portrait size", AppPreferences.getPortraitSize());
		cinfo.put("show stat sheet", AppPreferences.getShowStatSheet());
		cinfo.put("version", MapTool.getVersion());
		
		if (MapTool.getParser().isMacroTrusted()) {
			Map<String, Object> libInfo = new HashMap<String, Object>();
			for (ZoneRenderer zr : MapTool.getFrame().getZoneRenderers()) {
				Zone zone = zr.getZone();
				for (Token token : zone.getTokens()) {
					if (token.getName().toLowerCase().startsWith("lib:")) {
						if (token.getProperty("libversion") != null) {
							libInfo.put(token.getName(), token.getProperty("libversion"));
						} else {
							libInfo.put(token.getName(), "unknown");
						}
					}
				}
			}
			if (libInfo.size() > 0) {
				cinfo.put("library tokens", libInfo);
			}
			
			cinfo.put("user defined functions", JSONArray.fromObject(UserDefinedMacroFunctions.getInstance().getAliases()));
		}
		
		return JSONObject.fromObject(cinfo);
	}

	/**
	 * Retrieves the server side preferences as a json object.
	 * @return the server side preferences
	 */
	private JSONObject getServerInfo() {
		Map<String, Object> sinfo = new HashMap<String, Object>();
		ServerPolicy sp =  MapTool.getServerPolicy();
		
		sinfo.put("tooltips for default roll format", sp.getUseToolTipsForDefaultRollFormat() ? BigDecimal.ONE : BigDecimal.ZERO);
		sinfo.put("players can reveal", sp.getPlayersCanRevealVision() ? BigDecimal.ONE : BigDecimal.ZERO);
		sinfo.put("movement locked", sp.isMovementLocked() ? BigDecimal.ONE : BigDecimal.ZERO);
		sinfo.put("restricted impersonation", sp.isRestrictedImpersonation() ? BigDecimal.ONE : BigDecimal.ZERO);
		sinfo.put("individual views", sp.isUseIndividualViews() ? BigDecimal.ONE : BigDecimal.ZERO);
		sinfo.put("strict token management", sp.useStrictTokenManagement() ? BigDecimal.ONE : BigDecimal.ZERO);
		sinfo.put("players receive campaign macros", sp.playersReceiveCampaignMacros() ? BigDecimal.ONE : BigDecimal.ZERO);
		
		InitiativePanel ip = MapTool.getFrame().getInitiativePanel();
		if (ip != null) {
			sinfo.put("initiative owner permissions", ip.isOwnerPermissions() ? BigDecimal.ONE : BigDecimal.ZERO);
		}
		return JSONObject.fromObject(sinfo);
	}
}
