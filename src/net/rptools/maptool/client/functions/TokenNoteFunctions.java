package net.rptools.maptool.client.functions;

import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolVariableResolver;
import net.rptools.maptool.model.Token;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;


public class TokenNoteFunctions extends AbstractFunction {
	
	
	private static final TokenNoteFunctions instance = new TokenNoteFunctions();

	private TokenNoteFunctions() {
		super(0, 1, "getNotes", "getGMNotes", "setNotes", "setGMNotes");
	}
	
	public static TokenNoteFunctions getInstance() {
		return instance;
	}
	
	@Override
	public Object childEvaluate(Parser parser, String functionName,
			List<Object> parameters) throws ParserException {
		
		Token tokenInContext = ((MapToolVariableResolver)parser.getVariableResolver()).getTokenInContext();
		if (functionName.equals("getNotes")) {
			return tokenInContext.getNotes();
		}
		
		if (functionName.equals("setNotes")) {
			if (parameters.size() < 1) {
				throw new ParserException("Not enough parameters for setNotes(note)");
			}
			tokenInContext.setNotes(parameters.get(0).toString());
	 		MapTool.serverCommand().putToken(MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(), tokenInContext);
			return "";
		}
		
		if (functionName.equals("getGMNotes")) {
			if (!MapTool.getParser().isMacroTrusted()) {
				throw new ParserException("You do not have permissions to call getGMNotes() function.");
			}
			return tokenInContext.getGMNotes();
		}
		
		if (functionName.equals("setGMNotes"))  {
			if (!MapTool.getParser().isMacroTrusted()) {
				throw new ParserException("You do not have permissions to call getGMNotes() function.");
			}
			tokenInContext.setGMNotes(parameters.get(0).toString());
	 		MapTool.serverCommand().putToken(MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(), tokenInContext);
			return "";
		}
		
		return null;
	}

}
