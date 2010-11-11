
package net.rptools.maptool.client.functions;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolUtil;
import net.rptools.maptool.client.MapToolVariableResolver;
import net.rptools.maptool.language.I18N;
import net.rptools.maptool.model.Grid;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.TokenFootprint;
import net.rptools.maptool.model.Zone;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class TokenCopyDeleteFunctions extends AbstractFunction {

	private static final TokenCopyDeleteFunctions instance = new TokenCopyDeleteFunctions();

	private static final String COPY_FUNC = "copyToken";
	private static final String REMOVE_FUNC = "removeToken";

	private TokenCopyDeleteFunctions() {
		super(1, 4, COPY_FUNC, REMOVE_FUNC);
	}


	public static TokenCopyDeleteFunctions getInstance() {
		return instance;
	}


	@Override
	public Object childEvaluate(Parser parser, String functionName,
			List<Object> parameters) throws ParserException {
		if (!MapTool.getParser().isMacroTrusted()) {
			throw new ParserException(I18N.getText("macro.function.general.noPerm", functionName));
		}

		MapToolVariableResolver res = (MapToolVariableResolver) parser.getVariableResolver();
		if (functionName.equals(COPY_FUNC)) {
			return copyTokens(res, parameters);
		}

		if (functionName.equals(REMOVE_FUNC)) {
			return deleteToken(res, parameters);
		}

		throw new ParserException(I18N.getText("macro.function.general.unknownFunction", functionName));
	}



	private String deleteToken(MapToolVariableResolver res, List<Object> parameters) throws ParserException {
		Token token = FindTokenFunctions.findToken(parameters.get(0).toString(), null);

		if (token == null) {
			throw new ParserException("Can not find token " + parameters.get(0));
		}
		Zone zone = MapTool.getFrame().getCurrentZoneRenderer().getZone();
		MapTool.serverCommand().removeToken(zone.getId(), token.getId());
		return "Deleted token " + token.getId() + " (" + token.getName() + ")";
	}


	/*
	 * Token		copyToken(String tokenId, Number numCopies: 1, String fromMap: (""|currentMap()), JSONObject updates: null)
	 * JSONArray	copyToken(String tokenId, Number numCopies,     String fromMap: (""|currentMap()), JSONObject updates: null)
	 */
	private Object copyTokens(MapToolVariableResolver res, List<Object> param) throws ParserException {
		Token token = null;
		int numberCopies = 1;
		String zoneName = null;
		JSONObject newVals = null;

		int size = param.size();
		switch (size) {
		default :	// Come here with four or more parameters
			throw new ParserException(I18N.getText("macro.function.general.tooManyParam", COPY_FUNC, 4, size));
		case 4 :
			Object o = JSONMacroFunctions.asJSON(param.get(3));
			if (!(o instanceof JSONObject)) {
				throw new ParserException(I18N.getText("macro.function.general.argumentTypeO", COPY_FUNC, 4));
			}
			newVals = (JSONObject) o;
		case 3 :
			zoneName = param.get(2).toString();
		case 2 :
			if (!(param.get(1) instanceof BigDecimal)) {
				throw new ParserException(I18N.getText("macro.function.general.argumentTypeI", COPY_FUNC, 2, param.get(1).toString()));
			}
			numberCopies = ((BigDecimal)param.get(1)).intValue();
		case 1 :
			token = FindTokenFunctions.findToken(param.get(0).toString(), zoneName);
			if (token == null) {
				throw new ParserException(I18N.getText("macro.function.general.unknownTokenOnMap", COPY_FUNC, param.get(0), zoneName));
			}
			Zone zone = MapTool.getFrame().getCurrentZoneRenderer().getZone();
			List<String> newTokens = new ArrayList<String>(numberCopies);
			for (int i = 0; i < numberCopies; i++) {
				Token t = new Token(token);
				setTokenValues(t, newVals, zone, res);
				zone.putToken(t);

				MapTool.serverCommand().putToken(zone.getId(), t);
				newTokens.add(t.getId().toString());
			}
			MapTool.getFrame().getCurrentZoneRenderer().flushLight();
			if (numberCopies == 1) {
				return newTokens.get(0);
			} else {
				return JSONArray.fromObject(newTokens);
			}
		case 0 :
			throw new ParserException(I18N.getText("macro.function.general.argumentTypeT", COPY_FUNC, 1));	// should be notEnoughParams
		}
	}

	private void setTokenValues(Token token, JSONObject vals, Zone zone, MapToolVariableResolver res) throws ParserException {
		JSONObject newVals = JSONObject.fromObject(vals);
		newVals = (JSONObject) JSONMacroFunctions.getInstance().JSONEvaluate(res, newVals);

		// FJE Should we remove the keys as we process them?  We could then warn the user
		// if there are still keys in the hash at the end...

		// Update the Token Name.
		if (newVals.containsKey("name")) {
			if(newVals.getString("name").equals("")){
				throw new ParserException(I18N.getText("macro.function.tokenName.emptyTokenNameForbidden", COPY_FUNC));
			}
			token.setName(newVals.getString("name"));
		} else {
			// check the token's name, don't change PC token names ... ever
			if (token.getType() != Token.Type.PC) {
				token.setName(MapToolUtil.nextTokenId(zone, token));
			}
		}

		// Label
		if (newVals.containsKey("label")) {
			token.setLabel(newVals.getString("label"));
		}

		// GM Name
		if (newVals.containsKey("gmName")) {
			token.setGMName(newVals.getString("gmName"));
		}

		// Layer
		if (newVals.containsKey("layer")) {
			TokenPropertyFunctions.getInstance().setLayer(token, newVals.getString("layer"));
		}

		// Location...
		boolean useDistance = true;
		if (newVals.containsKey("useDistance")) {
			if (newVals.getInt("useDistance") == 0) {
				useDistance = false;
			}
		}
		Grid grid = zone.getGrid();
		int x = token.getX(), y = token.getY();
		boolean tokenMoved = false;
		boolean delta = false;

		if (newVals.containsKey("delta")) {
			if (newVals.getInt("delta") != 0) {
				delta = true;
			}
		}

		// X
		if (newVals.containsKey("x")) {
			x = newVals.getInt("x") * (useDistance ? grid.getSize() : 1) + (delta ? x : 0);
			tokenMoved = true;
		}

		// Y
		if (newVals.containsKey("y")) {
			y = newVals.getInt("y") * (useDistance ? grid.getSize() : 1) + (delta ? y : 0);
			tokenMoved = true;
		}

		if (tokenMoved) {
			TokenLocationFunctions.getInstance().moveToken(token, x, y, true);		// Always using ZonePoint coords
		}

		// Facing
		if (newVals.containsKey("facing")) {
			token.setFacing(newVals.getInt("facing"));
//			MapTool.getFrame().getCurrentZoneRenderer().flushLight();	// FJE Already part of copyToken()
		}

		// Size
		if (newVals.containsKey("size")) {				// FJE ... && token.isSnapToScale()) {
			String size = newVals.getString("size");
			for (TokenFootprint footprint : grid.getFootprints()) {
				if (footprint.getName().equalsIgnoreCase(size)) {
					token.setFootprint(grid, footprint);
					token.setSnapToScale(true);
					break;
				}
			}
		}
	}
}
