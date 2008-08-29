package net.rptools.maptool.client.script;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class ScriptManagerTest extends TestCase {
    public void testDoit() throws IOException {
        Map<String, Object> globals = new HashMap<String, Object>();
        
        globals.put("myString", "MY STRING");
        
        System.out.println(ScriptManager.evaluate(globals, "rptools.test.echo(myString)"));

        globals.put("myValue", 10);
        System.out.println(ScriptManager.evaluate(globals, "rptools.test.add(myValue)"));
        
        globals.put("myArray", new Object[] {10, 20, 30 });
        System.out.println(ScriptManager.evaluate(globals, "rptools.test.arrayLength(myArray)"));
    }

}
