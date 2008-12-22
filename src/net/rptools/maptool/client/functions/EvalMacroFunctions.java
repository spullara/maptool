package net.rptools.maptool.client.functions;

import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolLineParser;
import net.rptools.maptool.client.MapToolMacroContext;
import net.rptools.maptool.client.MapToolVariableResolver;
import net.rptools.maptool.model.Token;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;

public class EvalMacroFunctions extends AbstractFunction {

	/** The singleton instance. */
	private static final EvalMacroFunctions instance = new EvalMacroFunctions();
	
	
	private EvalMacroFunctions() {
		super(1, 1, "evalMacro", "execMacro");
	}
	
	
	/**
	 * Gets the instance of EvalMacroFunction.
	 * @return the instance.
	 */
	public static EvalMacroFunctions getInstance() {
		return instance;
	}

	@Override
	public Object childEvaluate(Parser parser, String functionName,
			List<Object> parameters) throws ParserException {
		MapToolLineParser lineParser = MapTool.getParser();
		
		
		if (!lineParser.isMacroTrusted()) {
			throw new ParserException("You do not have permission's to execute this macro");
		}
		
		MapToolMacroContext context = new MapToolMacroContext("<dynamic>", "<dynamic>", true);		
		MapToolVariableResolver resolver = (MapToolVariableResolver) parser.getVariableResolver();
		Token tokenInContext = resolver.getTokenInContext();
		
		// execMacro has new variable scope where as evalMacro does not.
		if (functionName.equals("execMacro")) {
			resolver = new MapToolVariableResolver(tokenInContext);
		}
		
		return lineParser.parseLine(resolver, tokenInContext, parameters.get(0).toString(), context);
	}

}
