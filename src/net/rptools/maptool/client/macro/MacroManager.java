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

import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author drice
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class MacroManager {

    private static enum COMMANDS {
        say, tell, roll
    };

    private static COMMANDS DEFAULT_COMMAND = COMMANDS.say;
    
    private static Map<COMMANDS, Macro> MACROS = new EnumMap<COMMANDS, Macro>(COMMANDS.class);
    static {
        MACROS.put(COMMANDS.say, new SayMacro());
        MACROS.put(COMMANDS.tell, new TellMacro());
        MACROS.put(COMMANDS.roll, new RollMacro());
    }
    
    private static final Pattern MACRO_PAT = Pattern.compile("^\\/(\\w+)\\s+(.*)$");
    public static void executeMacro(String macro) {
        Matcher m = MACRO_PAT.matcher(macro);
        if (m.matches()) {
            executeMacro(resolveCommand(m.group(1)), m.group(2));
        } else {
            executeMacro(DEFAULT_COMMAND, macro);
        }
    }
    
    private static void executeMacro(COMMANDS macro, String parameter) {
        Macro m = MACROS.get(macro);
        
        m.execute(parameter);
    }

    /**
     * Resolve the command by first trying to find an exact match, and if an
     * exact match is not found, search for the first command that matches the
     * most characters in the command. This does a case-insensitive search.
     * 
     * For example: s -> say t -> tell
     */
    private static COMMANDS resolveCommand(String command) {
        if (command != null && command.length() > 0) {
            for (int i = command.length(); i > 0; i--) {
                String c = command.substring(0, i);
                for (COMMANDS cmd : COMMANDS.values()) {
                    if (cmd.name().startsWith(c)) {
                        return cmd;
                    }
                }
            }
        }
        return DEFAULT_COMMAND;
    }
}
