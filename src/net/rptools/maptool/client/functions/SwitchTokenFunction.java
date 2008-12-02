package net.rptools.maptool.client.functions;

import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolVariableResolver;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;

public class SwitchTokenFunction extends AbstractFunction {

	private static final SwitchTokenFunction instance = new SwitchTokenFunction();

	private SwitchTokenFunction() {
		super(1, 1, "switchToken");
	}

	
	public static SwitchTokenFunction getInstance() {
		return instance;
	}

	@Override
	public Object childEvaluate(Parser parser, String functionName,
			List<Object> parameters) throws ParserException {
		
		if (!MapTool.getParser().isMacroTrusted()) {
			throw new ParserException("You do not have permissions to call switchToken()");
		}
		
		if (parameters.size() < 1) {
			throw new ParserException("Not enough parameters for switchToken(identifier)");
		}
		
		Zone zone = MapTool.getFrame().getCurrentZoneRenderer().getZone();
		Token token = zone.resolveToken(parameters.get(0).toString());
		if (token == null) {
			throw new ParserException("switchToken(): " + parameters.get(0).toString() + " not found");			
		}
		
		((MapToolVariableResolver)parser.getVariableResolver()).setTokenIncontext(token);
		return "";
		
	}

}
