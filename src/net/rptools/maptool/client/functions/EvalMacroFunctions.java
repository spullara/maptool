/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *  
 *	http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package net.rptools.maptool.client.functions;

import java.math.BigDecimal;
import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolLineParser;
import net.rptools.maptool.client.MapToolMacroContext;
import net.rptools.maptool.client.MapToolVariableResolver;
import net.rptools.maptool.language.I18N;
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
			throw new ParserException(I18N.getText("macro.function.general.noPerm", functionName));
		}
		
		MapToolVariableResolver resolver = (MapToolVariableResolver) parser.getVariableResolver();
		Token tokenInContext = resolver.getTokenInContext();
		
		// execMacro has new variable scope where as evalMacro does not.
		if (functionName.equals("execMacro")) {
			return execMacro(tokenInContext, parameters.get(0).toString());
		} else {
			return evalMacro(resolver, tokenInContext, parameters.get(0).toString());
		}
		
	}

	
	/**
	 * Executes the macro with a new variable scope.
	 * @param tokenInContext The token in context.
	 * @param line the macro to execute.
	 * @return the result of the execution.
	 * @throws ParserException if an error occurs.
	 */
	public static Object execMacro(Token tokenInContext, String line) throws ParserException {
		return evalMacro(null, tokenInContext, line);
	}
	
	/**
	 * Executes the macro with the specified variable scope.
	 * @param tokenInContext The token in context.
	 * @param line the macro to execute.
	 * @return the result of the execution.
	 * @throws ParserException if an error occurs.
	 */	
	public static Object evalMacro(MapToolVariableResolver res, Token tokenInContext, String line) throws ParserException {
		res = res == null ? new MapToolVariableResolver(tokenInContext) : res;
		
		MapToolMacroContext context = new MapToolMacroContext("<dynamic>", MapTool.getParser().getContext().getSouce(), true);		
		String  ret = MapTool.getParser().parseLine(res, tokenInContext, line, context);	
		
		// Try to convert to a number
		try {
			return new BigDecimal(ret);
		} catch (Exception e) {
			return ret;
		}
	}
}
