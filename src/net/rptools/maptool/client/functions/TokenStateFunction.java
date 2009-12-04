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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolVariableResolver;
import net.rptools.maptool.client.ui.token.BooleanTokenOverlay;
import net.rptools.maptool.language.I18N;
import net.rptools.maptool.model.Token;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;
import net.sf.json.JSONArray;

public class TokenStateFunction extends AbstractFunction {

    /** The value for setting all states. */
    public final static String			ALL_STATES = "ALL";


	/** The singleton instance. */
	private final static TokenStateFunction instance = new TokenStateFunction();
	
	
	/**
	 * Gets the singleton instance of the state.
	 * @return the instance.
	 */
	public static TokenStateFunction getInstance() {
		return instance;
	}

	
	private TokenStateFunction() {
		super(0, 3, "getState", "setState", "setAllStates", "getTokenStates");
	}

	@Override
	public Object childEvaluate(Parser parser, String functionName,
			List<Object> args) throws ParserException {
		
		if (functionName.equals("setAllStates")) {
			return setAllStates(parser, args);
		} else if (functionName.equals("getState")) {
			return getState(parser, args);
		} else if (functionName.equals("setState")){
			return setState(parser, args);
		} else if (functionName.equals("getTokenStates")) {
			return getTokenStates(parser, args);
		} else {
			throw new ParserException(I18N.getText("macro.function.general.unknownFunction", functionName));
		}
		
	}
	
	/**
	 * Gets the state of the specified token.
	 * @param token The token.
	 * @param stateName the name of the state to get.
 	 * @return the value of the state.
	 * @throws ParserException if the state is unknown.
	 */
	public Object getState(Token token, String stateName) throws ParserException {
		return getBooleanTokenState(token, stateName) ? BigDecimal.valueOf(1) :  BigDecimal.valueOf(0);
	}
	
	
	/**
	 * Sets the state of the specified token.
	 * @param token The token to set.
	 * @param stateName the name of the state or {@link #ALL_STATES}
	 * @param value the value to set it to.
	 * @throws ParserException if the state is unknown.
	 */
	public void setState(Token token, String stateName, Object value) throws ParserException {
		if (stateName.equals(ALL_STATES)) {
			for (Object sname : MapTool.getCampaign().getTokenStatesMap().keySet()) {
				setState(token, sname.toString(), value);
			}
		} else {
			setBooleanTokenState(token, stateName, value);
		}
	}
	

		
	/**
	 * Gets the state of the token.
	 * @param parser The parser that called the object.
	 * @param args The arguments.
	 * @return the state.
	 * @throws ParserException if an error occurs.
	 */
	private Object getState(Parser parser, List<Object> args) throws ParserException {
		Token token;
		String stateName;
		
		if (args.size() == 1) {
			MapToolVariableResolver res = (MapToolVariableResolver)parser.getVariableResolver();
			token = res.getTokenInContext();
			if (token == null) {
				return I18N.getText("macro.function.general.noImpersonated", "getState");
			}			
		} else if (args.size() == 2) {
			if (!MapTool.getParser().isMacroTrusted()) {
				return I18N.getText("macro.function.general.noPermOther", "getState");
			}
			
			token = FindTokenFunctions.findToken(args.get(1).toString(), null);
			if (token == null) {
				return I18N.getText("macro.function.general.unknownToken", "getState", args.get(1));
			}
		} else {
			throw new ParserException(I18N.getText("macro.function.general.notEnoughParam", "getState"));
		}
		stateName = args.get(0).toString();
		
		return getState(token, stateName);
	}
	
	/**
	 * Sets the state of the token.
	 * @param parser The parser that called the object.
	 * @param args The arguments.
	 * @return the state.
	 * @throws ParserException if an error occurs.
	 */
	private Object setState(Parser parser, List<Object> args) throws ParserException {
		Token token;
		String stateName;
		Object val;
		
		if (args.size() == 3) {
			
			if (!MapTool.getParser().isMacroTrusted()) {
				return I18N.getText("macro.function.general.noPermOther", "setState");
			}
			token = FindTokenFunctions.findToken(args.get(2).toString(), null);
			if (token == null) {
				return I18N.getText("macro.function.general.unknownToken", "setState", args.get(1));
			} 
		} else if (args.size() == 2) {
			MapToolVariableResolver res = (MapToolVariableResolver)parser.getVariableResolver();
			token = res.getTokenInContext();
			if (token == null) {
				return I18N.getText("macro.function.general.noImpersonated", "setState");
			}
		} else {
			throw new ParserException(I18N.getText("macro.function.general.notEnoughParam", "setState"));
		}
		stateName = args.get(0).toString();
		val = args.get(1);
		
		setBooleanTokenState(token, stateName, val);
		((MapToolVariableResolver)parser.getVariableResolver()).addDelayedAction(new MapToolVariableResolver.PutTokenAction(MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(), token));

		return val;
	}
	
	/**
	 * Sets the state of the token.
	 * @param parser The parser that called the object.
	 * @param args The arguments.
	 * @return the state.
	 * @throws ParserException if an error occurs.
	 */
	private Object setAllStates(Parser parser, List<Object> args) throws ParserException {
	
		Token token;
		if (args.size() == 1) {
			MapToolVariableResolver res = (MapToolVariableResolver)parser.getVariableResolver();
			token = res.getTokenInContext();
			if (token == null) {
				return I18N.getText("macro.function.general.noImpersonated", "setAllStates");
			}
		} else if (args.size() == 2) {
			if (!MapTool.getParser().isMacroTrusted()) {
				return I18N.getText("macro.function.general.noPermOther", "setAllStates");
			}

			token = FindTokenFunctions.findToken(args.get(1).toString(), null);
			if (token == null) {
				return I18N.getText("macro.function.general.unknownToken", "setAllStates", args.get(1));
			}
		} else { 
			throw new ParserException(I18N.getText("macro.function.general.notEnoughParam", "setAllStates"));
		}
		
		Object val = args.get(0);
		
		for (Object stateName : MapTool.getCampaign().getTokenStatesMap().keySet()) {
			setBooleanTokenState(token, stateName.toString(), val);
		}
  		MapTool.serverCommand().putToken(MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(), token);

  		return val;
	}
	

	/**
	 * Gets the boolean value of the tokens state.
	 * 
	 * @param token
	 *            The token to get the state of.
	 * @param stateName
	 *            The name of the state to get.
	 * @return the value of the state.
	 * @throws ParserException if an error occurs.
	 */
	public boolean getBooleanTokenState(Token token, String stateName) throws ParserException {
		if (!MapTool.getCampaign().getTokenStatesMap().containsKey(stateName)) {
			throw new ParserException(I18N.getText("macro.function.tokenStateFunctions.unknownState", stateName));
		}

		Object val = token.getState(stateName);
		if (val == null) { // If state does not exist then it can't be set ;)
			return false;
		}
		if (val instanceof Integer) {
			return ((Integer) val).intValue() != 0;
		} else if (val instanceof Boolean) {
			return ((Boolean) val).booleanValue();
		} else {
			try {
				return Integer.parseInt(val.toString()) != 0;
			} catch (NumberFormatException e) {
				return Boolean.parseBoolean(val.toString());
			}
		}
	}
	
	/**
	 * Sets the boolean state of a token.
	 * 
	 * @param token The token to set the state of.
	 * @param stateName The state to set.
	 * @param val set or unset the state.
	 * @throws ParserException if an error occurs.
	 */
	private void setBooleanTokenState(Token token, String stateName, Object val) throws ParserException {
		if (!MapTool.getCampaign().getTokenStatesMap().containsKey(stateName)) {
			throw new ParserException(I18N.getText("macro.function.tokenStateFunctions.unknownState", stateName));
		}
		
		boolean set;
		if (val instanceof Integer) {
			set = ((Integer) val).intValue() != 0;
		} else if (val instanceof Boolean) {
			set = ((Boolean) val).booleanValue();
		} else {
			try {
				set = Integer.parseInt(val.toString()) != 0;
			} catch (NumberFormatException e) {
				set = Boolean.parseBoolean(val.toString());
			}
			token.setState(stateName, set);
		}
	}

	/**
	 * Gets a list of the valid token states.
	 * @param parser The parser.
	 * @param args The arguments.
	 * @return A string with the states.
	 */
	private String getTokenStates(Parser parser, List<Object> args) {
		String delim = args.size() > 0 ? args.get(0).toString() : ",";
		Set<String> stateNames;

		if (args.size() > 1) {
			String group = (String) args.get(1);
			Map<String, BooleanTokenOverlay> states = MapTool.getCampaign().getTokenStatesMap();
			stateNames = new HashSet<String>();
			for (BooleanTokenOverlay bto : states.values()) { 
				if (group.equals(bto.getGroup())) {
					stateNames.add(bto.getName());
				}
			}
		} else {
			stateNames = MapTool.getCampaign().getTokenStatesMap().keySet();
		}

		
		StringBuilder sb = new StringBuilder();		
		if ("json".equals(delim)) {
			return JSONArray.fromObject(stateNames).toString();
		} else {
			for (String s : stateNames) {
				if (sb.length() > 0) {
					sb.append(delim);
				} 
				sb.append(s);
			}	
			return sb.toString();
		}	
	}
	
}
