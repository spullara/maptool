
package net.rptools.maptool.client.functions;


import java.math.BigDecimal;
import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;

public class DefineMacroFunction extends AbstractFunction {
	
	private static final DefineMacroFunction instance = new DefineMacroFunction();

	private DefineMacroFunction() {
		super(0, 2, "defineFunction", "isFunctionDefined", "oldFunction");
	}
	
	
	public static DefineMacroFunction getInstance() {
		return instance;
	}


	@Override
	public Object childEvaluate(Parser parser, String functionName,
			List<Object> parameters) throws ParserException {
		if (functionName.equals("defineFunction")) {
			if (!MapTool.getParser().isMacroTrusted()) {
				throw new ParserException("You do not have permission to call defineFunction()");
			}

			if (parameters.size() < 2) {
				throw new ParserException("Not enough parameters for define function.");
			}
		
			String macro = parameters.get(1).toString();
			if (macro.toLowerCase().endsWith("@this")) {
				macro = macro.substring(0, macro.length() - 4) + MapTool.getParser().getMacroSource();
			}
			
			UserDefinedMacroFunctions.getInstance().defineFunction(parser, parameters.get(0).toString(), macro);
			return parameters.get(0) + "() function defined";
		} else if (functionName.equals("oldFunction")) {
			return UserDefinedMacroFunctions.getInstance().executeOldFunction(parser, parameters);
		} else { // isFunctionDefined
			
			if (UserDefinedMacroFunctions.getInstance().isFunctionDefined(parameters.get(0).toString())) {
				return BigDecimal.ONE;
			} 
			
			if (parser.getFunction(parameters.get(0).toString()) != null) {
				return BigDecimal.valueOf(2);
			}
			
			return BigDecimal.ZERO;
		}
		
	}

}