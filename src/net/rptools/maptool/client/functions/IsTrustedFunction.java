package net.rptools.maptool.client.functions;

import java.math.BigDecimal;
import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;

public class IsTrustedFunction extends AbstractFunction {
	

	private static final IsTrustedFunction instance = new IsTrustedFunction();

	private IsTrustedFunction() {
		super(0, 0, "isTrusted", "isGM");
	}

	
	public static IsTrustedFunction getInstance() {
		return instance;
	}


	@Override
	public Object childEvaluate(Parser parser, String functionName,
			List<Object> parameters) throws ParserException {
		if (functionName.equals("isTrusted")) {
			return MapTool.getParser().isMacroTrusted() ? BigDecimal.ONE : BigDecimal.ZERO; 
		} else {
			return MapTool.getPlayer().isGM() ? BigDecimal.ONE : BigDecimal.ZERO;
		}
	}

}
