/* The MIT License
 * 
 * Copyright (c) 2005 David Rice, Trevor Croft
 * 
 * Permission is hereby granted, free of charge, to any person 
 * obtaining a copy of this software and associated documentation files 
 * (the "Software"), to deal in the Software without restriction, 
 * including without limitation the rights to use, copy, modify, merge, 
 * publish, distribute, sublicense, and/or sell copies of the Software, 
 * and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be 
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS 
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN 
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
 * SOFTWARE.
 */
package net.rptools.maptool.client.macro;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.rptools.maptool.client.macro.impl.ClearMacro;
import net.rptools.maptool.client.macro.impl.GotoMacro;
import net.rptools.maptool.client.macro.impl.HelpMacro;
import net.rptools.maptool.client.macro.impl.RollAllMacro;
import net.rptools.maptool.client.macro.impl.RollGMMacro;
import net.rptools.maptool.client.macro.impl.RollMeMacro;
import net.rptools.maptool.client.macro.impl.SayMacro;
import net.rptools.maptool.client.macro.impl.UndefinedMacro;

/**
 * @author drice
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class MacroManager {
    private static Macro UNDEFINED_MACRO = new UndefinedMacro();
    private static Map<String, Macro> MACROS = new HashMap<String, Macro>();
    
    static {
    	registerMacro(new SayMacro());
    	registerMacro(new HelpMacro());
    	registerMacro(new GotoMacro());
    	registerMacro(new ClearMacro());
        registerMacro(new RollMeMacro());
        registerMacro(new RollAllMacro());
        registerMacro(new RollGMMacro());
        
    	registerMacro(UNDEFINED_MACRO);
    }
    
    public static Set<Macro> getRegisteredMacros() {
    	Set<Macro> ret = new HashSet<Macro>();
    	ret.addAll(MACROS.values());
    	return ret;
    }
    
    public static Macro getRegisteredMacro(String name) {
    	Macro ret = MACROS.get(name);
    	if (ret == null) return UNDEFINED_MACRO;
    	return ret; 
    }

    public static void registerMacro(Macro macro) {
    	MacroDefinition def = macro.getClass().getAnnotation(MacroDefinition.class);
    	
    	if (def == null) return;
    	
    	MACROS.put(def.name(), macro);
    	for (String alias : def.aliases()) {
        	MACROS.put(alias, macro);
    	}
    }
    
    private static final Pattern MACRO_PAT = Pattern.compile("^(\\w+)\\s*(.*)$");
    public static void executeMacro(String command) {
      
        // Macro name is the first word
        Matcher m = MACRO_PAT.matcher(command);
        if (m.matches()) {
        	Macro macro = getRegisteredMacro(m.group(1));
          if (macro != UNDEFINED_MACRO) {
            executeMacro(macro, m.group(2));
            return;
          } // endif
        } // endif
        
        // Undefined macro shows the bad command
        executeMacro(UNDEFINED_MACRO, command);
    }
    
    private static void executeMacro(Macro macro, String parameter) {
    	macro.execute(parameter);
    }

}
