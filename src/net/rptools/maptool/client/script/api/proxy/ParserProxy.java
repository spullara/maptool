package net.rptools.maptool.client.script.api.proxy;

import java.math.BigDecimal;

import net.rptools.parser.Expression;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;

public class ParserProxy {
    private final Parser parser;
    
    public ParserProxy(Parser parser) {
        this.parser = parser;
    }
    
    public void setVariable(String name, Object value) throws ParserException {
        if (value instanceof Number)
            parser.setVariable(name, new BigDecimal(((Number) value).doubleValue()));
        else
            parser.setVariable(name, value);
    }

    public Object getVariable(String variableName) throws ParserException {
        return parser.getVariable(variableName);
    }
    
    public Object evaluate(String expression) throws ParserException {
        Expression xp = parser.parseExpression(expression);
        return xp.evaluate();
    }
}
