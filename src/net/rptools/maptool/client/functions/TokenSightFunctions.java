package net.rptools.maptool.client.functions;

import java.math.BigDecimal;
import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolVariableResolver;
import net.rptools.maptool.language.I18N;
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
		if (tokenInContext == null) {
			throw new ParserException(I18N.getText("macro.function.general.noImpersonated", functionName));
		}
		
		if (functionName.equals("hasSight")) {
			return tokenInContext.getHasSight() ? BigDecimal.ONE : BigDecimal.ZERO;
		}
		
		if (functionName.equals("getSightType")) {
			return tokenInContext.getSightType();		
		}
		
		if (functionName.equals("setHasSight")) {
			if (parameters.size() < 1) {
				throw new ParserException(I18N.getText("macro.function.general.notEnoughParam", functionName));
			}
			tokenInContext.setHasSight(!parameters.get(0).equals(BigDecimal.ZERO));
			MapTool.getFrame().getCurrentZoneRenderer().getZone().putToken(tokenInContext);
	 		MapTool.serverCommand().putToken(MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(), tokenInContext);
	    	MapTool.getFrame().getCurrentZoneRenderer().flushLight();
			return "";
		}
		
		
		if (functionName.equals("setSightType")) {
			if (parameters.size() < 1) {
				throw new ParserException(I18N.getText("macro.function.general.notEnoughParam", functionName));
			}
			tokenInContext.setSightType(parameters.get(0).toString());
			MapTool.getFrame().getCurrentZoneRenderer().getZone().putToken(tokenInContext);
	 		MapTool.serverCommand().putToken(MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(), tokenInContext);
	    	MapTool.getFrame().getCurrentZoneRenderer().flushLight();
	 		return "";
		}
		
		return null;
		
	}

}
