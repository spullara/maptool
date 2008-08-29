package net.rptools.maptool.client.script;

import net.rptools.parser.Expression;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import junit.framework.TestCase;

public class JavascriptFunctionTest extends TestCase {

    public void testFunction_VariableArguments() throws ParserException {
        Parser parser = new Parser();
        
        parser.addFunction(new JavascriptFunction("rptools.test.parserFunction", JavascriptFunction.ArrayType.Numbers, 0, -1, true, "tpf"));
        
        Expression xp = parser.parseExpression("tpf(\"foo\", 10, 20, 30)");
        
        Object result = xp.evaluate();
        System.out.printf("%s: %s (%s)", xp.format(), result, result.getClass().getName());
    }

    public void testFunction_FixedArguments() throws ParserException {
        Parser parser = new Parser();
        
        parser.addFunction(new JavascriptFunction("rptools.test.parserFunctionFixedArguments", JavascriptFunction.ArrayType.Numbers, 0, -1, true, "tpf"));
        
        Expression xp = parser.parseExpression("tpf(10, 20)");
        
        Object result = xp.evaluate();
        System.out.printf("%s: %s (%s)", xp.format(), result, result.getClass().getName());
    }
}
