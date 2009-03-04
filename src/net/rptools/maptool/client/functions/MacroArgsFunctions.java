package net.rptools.maptool.client.functions;


import java.math.BigDecimal;
import java.util.List;

import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;

public class MacroArgsFunctions extends AbstractFunction {
	
	private static final MacroArgsFunctions instance = new MacroArgsFunctions();

	private MacroArgsFunctions() {
		super(0, 1, "arg", "argCount");
	}
	
	
	public static MacroArgsFunctions getInstance() {
		return instance;
	}


	@Override
	public Object childEvaluate(Parser parser, String functionName,
			List<Object> parameters) throws ParserException {
		
		Object numArgs = parser.getVariable("macro.args.num");
		int argCount = 0;
		
		if (numArgs instanceof BigDecimal) {
			argCount = ((BigDecimal)numArgs).intValue();
		}
		
		if (functionName.equals("argCount")) {
			return BigDecimal.valueOf(argCount);
		}
		
		if (parameters.size() != 1 || !(parameters.get(0) instanceof BigDecimal)) {
			throw new ParserException("arg() function must have a single numeric argument");
		}
		
		int argNo = ((BigDecimal)parameters.get(0)).intValue();

		if (argCount == 0 && argNo == 0) {
			return parser.getVariable("macro.args");
		}

		if (argNo < 0 || argNo >= argCount) {
			throw new ParserException("arg(): Argument index out of range " + argNo + " (max = " + (argCount-1)+ ")");
		}
		
		return parser.getVariable("macro.args." + argNo);
	}


}