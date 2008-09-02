package net.rptools.maptool.client.script;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mozilla.javascript.Context;

import net.rptools.common.expression.RunData;
import net.rptools.maptool.client.script.api.proxy.ParserProxy;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;

public class JavascriptFunction extends AbstractFunction {

    public enum ArrayType {
        Mixed,
        Numbers,
        Strings,
    }
    
    private final String javascriptFunction;
    private final ArrayType javascriptArrayType;
    

    public JavascriptFunction(String javascriptFunction, ArrayType javascriptArrayType, int minParameters, int maxParameters, boolean deterministic,
            String... aliases) {
        super(minParameters, maxParameters, deterministic, aliases);
        this.javascriptFunction = javascriptFunction;
        this.javascriptArrayType = javascriptArrayType;
    }

    @Override
    public Object childEvaluate(Parser parser, String functionName, List<Object> parameters) throws ParserException {

        Map<String, Object> globals = new HashMap<String, Object>();
        
        globals.put("parser", new ParserProxy(parser));
        globals.put("functionName", functionName);
        globals.put("rundata", null /*RunData.getCurrent() */);
        globals.put("result", null /*RunData.getCurrent().getResult() */);
        
        StringBuilder sb = new StringBuilder();
        
        sb.append("(function() { rptools.scope = { parser: parser, name: functionName, rundata: rundata, result: result }; ");
        
        sb.append("var result = ").append(javascriptFunction).append('(');
        
        boolean first = true;
        for (Object o : parameters) {
            if (!first)
                sb.append(',');
            
            if (o instanceof String)
                sb.append('"').append(o).append('"');
            else
                sb.append(o);
            
            first = false;
        }
        
        sb.append("); rptools.scope = null; return result; })();");
        
        System.out.println(sb.toString());

        try {
            Object result = ScriptManager.evaluate(globals, sb.toString());
            
            if (result instanceof BigDecimal)
                return result;
            else if (result instanceof Number)
                return new BigDecimal(((Number) result).doubleValue());
            
            return result;
        } catch (IOException ex) {
            throw new ParserException(ex);
        }
    }
}
