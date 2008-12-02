package net.rptools.maptool.client.functions;

import java.math.BigDecimal;
import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolVariableResolver;
import net.rptools.maptool.model.Token;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;

public class TokenSightFunctions extends AbstractFunction {
	
	private static final TokenSightFunctions instance = new TokenSightFunctions();
	
	
	private TokenSightFunctions() {
		super(0,1, "hasSight", "setHasSight", "getSightType", "setSightType");
	}
	
	
	public static TokenSightFunctions getInstance() { 
		return instance;
	}

	
	@Override
	public Object childEvaluate(Parser parser, String functionName,
			List<Object> parameters) throws ParserException {
		
		Token tokenInContext = ((MapToolVariableResolver)parser.getVariableResolver()).getTokenInContext();

		if (functionName.equals("hasSight")) {
			return tokenInContext.getHasSight() ? BigDecimal.ONE : BigDecimal.ZERO;
		}
		
		if (functionName.equals("getSightType")) {
			return tokenInContext.getSightType();		
		}
		
		if (functionName.equals("setHasSight")) {
			if (parameters.size() < 1) {
				throw new ParserException("Not enough parameters for function setHasSight(sight)");
			}
			if (tokenInContext.getType() == Token.Type.PC) {
				tokenInContext.setHasSight(!parameters.get(0).equals(BigDecimal.ZERO));
		 		MapTool.serverCommand().putToken(MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(), tokenInContext);
			}
			return "";
		}
		
		
		if (functionName.equals("setSightType")) {
			if (parameters.size() < 1) {
				throw new ParserException("Not enough parameters for function setHasSight(sight)");
			}
			tokenInContext.setSightType(parameters.get(0).toString());
	 		MapTool.serverCommand().putToken(MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(), tokenInContext);
			return "";
		}
		
		return null;
		
	}

}
