package net.rptools.maptool.client.functions;

import java.math.BigDecimal;
import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolVariableResolver;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Token;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;

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
		super(1, 3, "getState", "setState", "setAllStates");
	}

	@Override
	public Object childEvaluate(Parser parser, String functionName,
			List<Object> args) throws ParserException {
		
		if (functionName.equals("setAllStates")) {
			return setAllStates(parser, args);
		} else if (functionName.equals("getState")) {
			return getState(parser, args);
		} else {
			return setState(parser, args);
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
		
		if (args.size() > 1 && args.get(0) instanceof GUID) {
			token = MapTool.getFrame().getCurrentZoneRenderer().getZone().getToken((GUID)args.get(0));
			stateName = args.get(1).toString();
		} else if (args.size() > 1) {
			throw new ParserException("Usage: getState(state) or getState(target, state)");
		} else {
			MapToolVariableResolver res = (MapToolVariableResolver)parser.getVariableResolver();
			token = res.getTokenInContext();
			stateName = args.get(0).toString();
		}
		
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
		
		if (args.size() > 2 && args.get(0) instanceof GUID) {
			token = MapTool.getFrame().getCurrentZoneRenderer().getZone().getToken((GUID)args.get(0));
			stateName = args.get(1).toString();
			val = args.get(2);
		} else if (args.size() > 2) {
			throw new ParserException("Usage: setState(state, val) or setState(target, state, val)");
		} else {
			MapToolVariableResolver res = (MapToolVariableResolver)parser.getVariableResolver();
			token = res.getTokenInContext();
			stateName = args.get(0).toString();
			val = args.get(1);
		}
		
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
		
		MapToolVariableResolver res = (MapToolVariableResolver)parser.getVariableResolver();
		Token token = res.getTokenInContext();
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
	private boolean getBooleanTokenState(Token token, String stateName) throws ParserException {
		if (!MapTool.getCampaign().getTokenStatesMap().containsKey(stateName)) {
			throw new ParserException("Unknown token name " + stateName);
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

}
