package net.rptools.maptool.client.functions;

import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolVariableResolver;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Token;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;

public class TokenLabelFunction extends AbstractFunction {

	/** The singleton instance. */
	private final static TokenLabelFunction instance = new TokenLabelFunction();
	
	
	/**
	 * 
	 * @return
	 */
	public static TokenLabelFunction getInstance() {
		return instance;
	}
	
	private TokenLabelFunction() {
		super(0,1, "getLabel", "setLabel");
	}

	/**
	 * Gets the label for the specified token.
	 * @param token The token to get the label for.
	 * @return the label.
	 */
	public String getLabel(Token token) {
		return token.getLabel() != null ? token.getLabel() : "";
	}

	/**
	 * Sets the label for the specified token.
	 * @param token The token to set the label for.
	 * @param label the label to set.
	 */
	public void setLabel(Token token, String label) {
		token.setLabel(label);
		MapTool.serverCommand().putToken(MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(), token);
	}
	
	
	@Override
	public Object childEvaluate(Parser parser, String functionName, List<Object> args)
			throws ParserException {
		if (functionName.equals("getLabel")) {
			return getLabel(parser, args);
		} else {
			return setLabel(parser, args);
		}
	}
	
	
	/**
	 * Gets the label of the token
	 * @param parser The parser that called the Object.
	 * @param args The arguments passed.
	 * @return the name of the token.
	 * @throws ParserException when an error occurs.
	 */
	private Object getLabel(Parser parser, List<Object> args) throws ParserException {
		Token token;
		
		if (args.size() > 0 && args.get(0) instanceof GUID) {
			token = MapTool.getFrame().getCurrentZoneRenderer().getZone().getToken((GUID)args.get(0));
		} else if (args.size() > 0) {
			throw new ParserException("Usage: getLabel() or getLabel(target)");
		} else {
			MapToolVariableResolver res = (MapToolVariableResolver)parser.getVariableResolver();
			token = res.getTokenInContext();
		}
		return getLabel(token);
	}

	/**
	 * Sets the label of the token.
	 * @param parser The parser that called the Object.
	 * @param args The arguments passed.
	 * @return the new name of the token.
	 * @throws ParserException when an error occurs.
	 */
	private Object setLabel(Parser parser, List<Object> args) throws ParserException {
		MapToolVariableResolver res = (MapToolVariableResolver)parser.getVariableResolver();
		
		setLabel(res.getTokenInContext(), args.get(0).toString());
		return args.get(0);
	}


}
