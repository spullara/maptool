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

import net.rptools.maptool.language.I18N;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;
import net.rptools.parser.function.ParameterException;


/** Throws an exception with a user-defined error message if a condition is false.
 *  <br>Usage: <code>assert(<i>condition</i>, <i>errorMessage</i>)</code> 
 * 
 * @author knizia.fan 
 */
public class AssertFunction extends AbstractFunction {
	public AssertFunction() {
		// Defining ourselves to be "non-deterministic" is a small hack that allows
		// comparison operations to be passed in as arguments, due to an error (?)
		// in InlineTreeFormatter.java.
		super(2, 3, false, "assert");
	}

	/** The singleton instance. */
	private final static AssertFunction instance = new AssertFunction();

	/** Gets the instance. */
	public static AssertFunction getInstance() {
		return instance;
	}

	@Override
	public Object childEvaluate(Parser parser, String functionName,	List<Object> parameters) throws ParserException {
		if (BigDecimal.ZERO.equals((BigDecimal) parameters.get(0))) {
			if (parameters.size() > 2 && parameters.get(2).equals(BigDecimal.ZERO)) {
				throw new AssertFunctionException(parameters.get(1).toString());				
			} else {
				throw new AssertFunctionException(
						I18N.getText("macro.function.assert.message", parameters.get(1).toString()));
			}
		}
		return new BigDecimal(1);
	}

	@Override
	public void checkParameters(List<Object> parameters) throws ParameterException {
		super.checkParameters(parameters);
		if (! (parameters.get(1) instanceof String)) {
			throw new ParameterException(I18N.getText("macro.function.assert.message",
					"macro.function.assert.mustBeString", "assert()", parameters.get(1).toString()));
		}
	}


	/** Exception type thrown by assert() function, allowing a user-defined error message.  */
	public class AssertFunctionException extends ParameterException {
		public AssertFunctionException(String msg) {
			super(msg);
		}
	}

}

