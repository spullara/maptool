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
import net.rptools.parser.function.AbstractNumberFunction;

/** Aborts the current parser evaluation. 
 * 
 * @author knizia.fan 
 */
public class AbortFunction extends AbstractNumberFunction {
	public AbortFunction() {
		super(1, 1, "abort");
	}

	/** The singleton instance. */
	private final static AbortFunction instance = new AbortFunction();
	
	/** Gets the Input instance.
	 * @return the instance. */
	public static AbortFunction getInstance() {
		return instance;
	}
	
	@Override
	public Object childEvaluate(Parser parser, String functionName,	List<Object> parameters) throws ParserException {
        BigDecimal value = (BigDecimal) parameters.get(0);
        if (value.intValue() == 0) 
        	throw new AbortFunctionException(I18N.getText("macro.function.abortFunction.message", "Abort()"));
        else
        	return new BigDecimal(value.intValue());
	}

	/** Exception type thrown by abort() function. Semantics are to silently halt the current execution. */
	public class AbortFunctionException extends ParserException {

		public AbortFunctionException(Throwable cause) {
	        super(cause);
	    }
	    
	    public AbortFunctionException(String msg) {
	    	super(msg);
	    }
	}
	
	
}

