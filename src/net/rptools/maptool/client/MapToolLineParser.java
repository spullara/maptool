package net.rptools.maptool.client;

import net.rptools.common.expression.ExpressionParser;
import net.rptools.common.expression.Result;
import net.rptools.maptool.client.functions.LookupTableFunction;
import net.rptools.maptool.model.Token;
import net.rptools.parser.ParserException;
import net.rptools.parser.VariableResolver;

public class MapToolLineParser {

    private static final int PARSER_MAX_RECURSE = 50;

    private enum State {
    	TEXT,
    	ROLL,
    	COMMAND
    }
    
    private int parserRecurseDepth;
    
    public String parseLine(String line) throws ParserException {
    	return parseLine(null, line);
    }
    public String parseLine(Token tokenInContext, String line) throws ParserException {

    	if (line == null) {
    		return "";
    	}
    	
    	line = line.trim();
    	if (line.length() == 0) {
    		return "";
    	}
    	
    	// Keep the same context for this line
    	MapToolVariableResolver resolver = new MapToolVariableResolver(tokenInContext);

    	State state = State.TEXT;
    	StringBuilder builder = new StringBuilder();
    	StringBuilder expressionBuilder = new StringBuilder();
    	for (int index = 0; index < line.length(); index++) {
    		
    		char ch = line.charAt(index);
    		switch (state) {
    		case TEXT:
    			if (ch == '[') {
    				state = State.ROLL;
    				expressionBuilder.setLength(0);
    				break;
    			}
    			if (ch == '{') {
    				state = State.COMMAND;
    				expressionBuilder.setLength(0);
    				break;
    			}
    			builder.append(ch);
    			break;
    		case ROLL:
    			if (ch == ']') {
    				try {
	    				String roll = expressionBuilder.toString();
	
	    				// Preprocessed roll already ?
	    	   			if (roll.startsWith("roll")) {
	    	   				continue;
	    	   			}

	    	   			builder.append("[roll "+ roll + " = " + expandRoll(resolver, tokenInContext, roll)+"]" );

    				} finally {
        	   			state = State.TEXT;
    				}

    				break;
    			}
    			
    			expressionBuilder.append(ch);
    			break;
    		case COMMAND: 
    			if (ch == '}') {
    				try {
	    				String cmd = expressionBuilder.toString();
	    				
	    	   			// Preprocessed roll already ?
	    	   			if (cmd.startsWith("cmd")) {
	    	   				continue;
	    	   			}
	    	   			Result result = parseExpression(resolver, tokenInContext, cmd);
	    	   			builder.append(result != null ? result.getValue().toString() : "");

    				} finally {
	    	   			state = State.TEXT;
    				}
    	   			break;
    			}
    			
    			expressionBuilder.append(ch);
    			break;
    		}
    			
    	}

   		return builder.toString();
    }
    
    public Result parseExpression(String expression) throws ParserException {
    	return parseExpression(null, expression);
    }
    
    public Result parseExpression(Token tokenInContext, String expression) throws ParserException {
    	
        return parseExpression(new MapToolVariableResolver(tokenInContext), tokenInContext, expression);
    }
    public Result parseExpression(VariableResolver resolver, Token tokenInContext, String expression) throws ParserException {

    	if (parserRecurseDepth > PARSER_MAX_RECURSE) {
    		throw new ParserException("Max recurse limit reached");
    	}
        try {
        	parserRecurseDepth ++;
        	return  createParser(resolver).evaluate(expression);
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
    	return expandRoll(null, roll);
    }
    
    public String expandRoll(Token tokenInContext, String roll) {
    	return expandRoll(new MapToolVariableResolver(tokenInContext), tokenInContext, roll);
    }
    
    public String expandRoll(MapToolVariableResolver resolver, Token tokenInContext, String roll) {
    	
      	try {
			Result result = parseExpression(resolver, tokenInContext, roll);

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
    
    private ExpressionParser createParser(VariableResolver resolver) {
    	ExpressionParser parser = new ExpressionParser(resolver);
    	parser.getParser().addFunction(new LookupTableFunction());
    	return parser;
    }
}
