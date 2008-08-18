package net.rptools.maptool.client.functions;

import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolVariableResolver;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Token;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;

public class TokenGMNameFunction extends AbstractFunction {
	
	private TokenGMNameFunction() {
		super(0,1, "getGMName", "setGMName");
	}

	
	/** Singleton instance of GMName.*/
	private final static TokenGMNameFunction instance = new TokenGMNameFunction();
	
	/**
	 * Gets the singleton instance of GMName.
	 * @return the instance.
	 */
	public final static TokenGMNameFunction getInstance() {
		return instance;
	}
	
	/**
	 * Gets the GMName of the specified token.
	 * @param token the token to get theGMName of.
	 * @return the GMName.
	 * @throws ParserException if the user does not have the permission.
	 */
	public String getGMName(Token token) throws ParserException {
		if (!MapTool.getPlayer().isGM()) {
			throw new ParserException("Must be GM to query GMName.");
		}
		return token.getGMName() != null ? token.getGMName() : "";
	}
	
	
	/**
	 * Sets the GMName of the token.
	 * @param token the token to set the GMName of.
	 * @param naeme The name to set the GMName to.
	 * @throws ParserException if the user does not have the permission.
	 */
	public void setGMName(Token token, String name) throws ParserException {
		token.setGMName(name);
	}
	
	
	@Override
	public Object childEvaluate(Parser parser, String functionName, List<Object> args)
			throws ParserException {

		if (functionName.equals("getGMName")) {
			return getGMName(parser, args);
		} else {
			return setGMName(parser, args);
		}
	}
	
	
	
	
	/**
	 * Gets the GM name of the token
	 * @param parser The parser that called the Object.
	 * @param args The arguments passed.
	 * @return the name of the token.
	 * @throws ParserException when an error occurs.
	 */
	private Object getGMName(Parser parser, List<Object> args) throws ParserException {
		Token token;
		
		if (args.size() > 0 && args.get(0) instanceof GUID) {
			token = MapTool.getFrame().getCurrentZoneRenderer().getZone().getToken((GUID)args.get(0));
		} else if (args.size() > 0) {
			throw new ParserException("Usage: getGMame() or getGMName(target)");
		} else {
			MapToolVariableResolver res = (MapToolVariableResolver)parser.getVariableResolver();
			token = res.getTokenInContext();
		}
		return getGMName(token);
	}

	/**
	 * Sets the GM name of the token.
	 * @param parser The parser that called the Object.
	 * @param args The arguments passed.
	 * @return the new name of the token.
	 * @throws ParserException when an error occurs.
	 */
	private Object setGMName(Parser parser, List<Object> args) throws ParserException {
		MapToolVariableResolver res = (MapToolVariableResolver)parser.getVariableResolver();
		
		res.getTokenInContext().setGMName(args.get(0).toString());
		MapTool.serverCommand().putToken(MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(),
        		res.getTokenInContext());
		return args.get(0);
	}

}
