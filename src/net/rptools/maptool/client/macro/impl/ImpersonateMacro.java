/* The MIT License
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
package net.rptools.maptool.client.macro.impl;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.macro.Macro;
import net.rptools.maptool.client.macro.MacroDefinition;
import net.rptools.maptool.client.macro.MacroManager;
import net.rptools.maptool.model.TextMessage;

@MacroDefinition(
        name = "impersonate",
        aliases = { "im" },
        description = "Impersonate Character."
    )
public class ImpersonateMacro implements Macro {
	
	public void execute(String macro) {

		int index = macro.indexOf(":");
		if ( index > 0 ) {
			MapTool.getFrame().getCommandPanel().setIdentity(macro.substring(0,index));
			MacroManager.executeMacro(macro.substring(index+1));
			MapTool.getFrame().getCommandPanel().setIdentity(null);
		} else if ( macro.length() > 0 ) {
			MapTool.getFrame().getCommandPanel().setIdentity(macro);
            MapTool.addMessage(TextMessage.me("You're now impersonating "+macro));
        } else {
        	MapTool.getFrame().getCommandPanel().setIdentity(null);
            MapTool.addMessage(TextMessage.me("You're no longer impersonating anyone"));
        }
	}

}