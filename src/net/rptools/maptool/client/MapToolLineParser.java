package net.rptools.maptool.client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.rptools.common.expression.ExpressionParser;
import net.rptools.common.expression.Result;
import net.rptools.parser.ParserException;

public class MapToolLineParser {

    private static final int PARSER_MAX_RECURSE = 50;
    private static final Pattern INLINE_ROLL = Pattern.compile("\\[([^\\]]+)\\]");
    private static final Pattern INLINE_COMMAND = Pattern.compile("\\{([^\\}]+)\\}");
    
    private int parserRecurseDepth;
    private ExpressionParser parser;
    
    public String parseLine(String line) throws ParserException {

    	// TODO: This isn't right, but is an intermediary step while moving towards a better line parser
        Matcher m = INLINE_ROLL.matcher(line);
        StringBuffer buf = new StringBuffer();
   		while( m.find()) {
   			String roll = m.group(1);
   			
   			// Preprocessed roll already ?
   			if (roll.startsWith("roll")) {
   				continue;
   			}
   			
   			m.appendReplacement(buf, "[roll "+ roll + " &#8658; " + expandRoll(roll)+"]" );
       	}
   		m.appendTail(buf);

        m = INLINE_COMMAND.matcher(line);
        buf = new StringBuffer();
   		while( m.find()) {
   			String roll = m.group(1);
   			
   			// Preprocessed roll already ?
   			if (roll.startsWith("cmd")) {
   				continue;
   			}
   			
   			m.appendReplacement(buf, "{cmd "+ parseExpression(roll).getValue()+"}" );
       	}
   		m.appendTail(buf);

   		return buf.toString();
    }
    
    public Result parseExpression(String expression) throws ParserException {
        if (parser == null) {
            parser = new ExpressionParser(new MapToolVariableResolver());
        }
        
    	if (parserRecurseDepth > PARSER_MAX_RECURSE) {
    		throw new ParserException("Max recurse limit reached");
    	}
        try {
        	parserRecurseDepth ++;
        	return parser.evaluate(expression);
        } catch (RuntimeException re) {
        	
        	if (re.getCause() instanceof ParserException) {
        		throw (ParserException) re.getCause();
        	}
        	
        	throw re;
        } finally {
        	parserRecurseDepth--;
        }
    }	
    
    public String expandRoll(String roll) {
    	
      	try {
			Result result = parseExpression(roll);

	    	StringBuilder sb = new StringBuilder();
	    	
	    	if (result.getDetailExpression().equals(result.getValue().toString())) {
	    		sb.append(result.getDetailExpression());
	    	} else {
	    		sb.append(result.getDetailExpression()).append(" = ").append(result.getValue());
	    	}
	
	        return sb.toString();
		} catch (ParserException e) {
			return "Invalid expression: " + roll;
		}
    	
    }    
}
