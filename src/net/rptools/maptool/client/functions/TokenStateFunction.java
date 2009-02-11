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
import java.util.List;
import java.util.Set;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolVariableResolver;
import net.rptools.maptool.model.GUID;
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
			throw new ParserException(functionName + "(): Unknown function.");
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
	 		MapTool.serverCommand().putToken(MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(), token);
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
				throw new ParserException("getState(): No impersonated token");
			}			
		} else if (args.size() == 2) {
			if (!MapTool.getParser().isMacroTrusted()) {
				throw new ParserException("setState(): You do not have permission to refefer to other tokens.");
			}
			
			token = FindTokenFunctions.findToken(args.get(1).toString(), null);
			if (token == null) {
				throw new ParserException("getState(): can not find token or ID " + args.get(1));
			}


		} else {
			throw new ParserException("getState(): Incorrect number of parameters");
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
				throw new ParserException("setState(): You do not have permission to refefer to other tokens.");
			}
			token = FindTokenFunctions.findToken(args.get(2).toString(), null);
			if (token == null) {
				throw new ParserException("setState(): can not find token or ID " + args.get(1));
			} 
		} else if (args.size() == 2) {
			MapToolVariableResolver res = (MapToolVariableResolver)parser.getVariableResolver();
			token = res.getTokenInContext();
			if (token == null) {
				throw new ParserException("setState(): No impersonated token");
			}
		} else {
			throw new ParserException("setState(): Incorrect number of parameters.");
		}
		stateName = args.get(0).toString();
		val = args.get(1);
		
		setBooleanTokenState(token, stateName, val);
 		MapTool.serverCommand().putToken(MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(), token);
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
				throw new ParserException("setAllStates(): No impersonated token");
			}
		} else if (args.size() == 2) {
			if (!MapTool.getParser().isMacroTrusted()) {
				throw new ParserException("setAllStates(): You do not have permission to refefer to other tokens.");
			}

			token = FindTokenFunctions.findToken(args.get(1).toString(), null);
			if (token == null) {
				throw new ParserException("setAllStates(): can not find token or ID " + args.get(1));
			}
		} else { 
			throw new ParserException("setAllStates(): Incorrect number of parameters.");
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
			throw new ParserException("Unknown token state name " + stateName);
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
			throw new ParserException("Unknown token state name " + stateName);
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

		Set<String> stateNames = MapTool.getCampaign().getTokenStatesMap().keySet();
		
		if ("json".equals(delim)) {
			return JSONArray.fromObject(stateNames).toString();
		} 
		
		StringBuilder sb = new StringBuilder();
		for (String s : stateNames) {
			if (sb.length() != 0) {
				sb.append(delim);
			} 
			sb.append(s);
		}
		
		return sb.toString();
		
	}
}
