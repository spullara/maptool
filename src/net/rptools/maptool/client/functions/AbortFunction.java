
package net.rptools.maptool.client.functions;

import java.math.BigDecimal;
import java.util.List;

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
        	throw new AbortFunctionException("Abort() function called.");
        else
        	return new BigDecimal(0);
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

