
package net.rptools.maptool.client.functions;


import java.awt.Dimension;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolUtil;
import net.rptools.maptool.client.MapToolVariableResolver;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Grid;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;
import net.sf.json.JSONArray;

public class TokenCopyDeleteFunctions extends AbstractFunction {
	
	private static final TokenCopyDeleteFunctions instance = new TokenCopyDeleteFunctions();

	private TokenCopyDeleteFunctions() {
		super(1, 3, "copyToken", "removeToken");
	}
	
	
	public static TokenCopyDeleteFunctions getInstance() {
		return instance;
	}


	@Override
	public Object childEvaluate(Parser parser, String functionName,
			List<Object> parameters) throws ParserException {
		if (!MapTool.getParser().isMacroTrusted()) {
			throw new ParserException("You do not have permission to call the " + functionName + "() function.");
		}
		
		MapToolVariableResolver res = (MapToolVariableResolver) parser.getVariableResolver();
		if (functionName.equals("copyToken")) {
			return copyTokens(res, parameters);
		}
		
		if (functionName.equals("removeToken")) {
			return deleteToken(res, parameters);
		}
		
		
		throw new ParserException("Unknown function "+ functionName + "()");
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


	private Object copyTokens(MapToolVariableResolver res, List<Object> param) throws ParserException {
		if (param.size() < 1) {
			throw new ParserException("copyTokens(): First argument must be a token.");
		}

		String zoneName = null;
		if (param.size() > 2) {
			zoneName = param.get(2).toString();
		}
		
		Token token = FindTokenFunctions.findToken(param.get(0).toString(), zoneName);
		
		
		if (token == null) {
			throw new ParserException("copyToken(): Token not found.");
		}
		int numberCopies = 1;
		if (param.size() > 1) {
			if (!(param.get(1) instanceof BigDecimal)) {
				throw new ParserException("copyTokens(): Second parameter must be numbers");
			}		
			numberCopies = ((BigDecimal)param.get(1)).intValue();
			
		}
		
		Zone zone = MapTool.getFrame().getCurrentZoneRenderer().getZone();

		List<String> newTokens = new ArrayList<String>(numberCopies);
		

		for (int i = 0; i < numberCopies; i++) {
			Token t = new Token(token);
			// check the token's name, don't change PC token names ... ever
			if (token.getType() != Token.Type.PC) {
				t.setName(MapToolUtil.nextTokenId(zone, token));
			}

			zone.putToken(t);
			MapTool.serverCommand().putToken(zone.getId(), t);
			newTokens.add(t.getId().toString());
		}
	
		if (numberCopies == 1) {
			return newTokens.get(0).toString();
		} else {
			return JSONArray.fromObject(newTokens);
		}
	}
	
	
	

}

