/* The MIT License
 * 
 * Copyright (c) 2005 David Rice, Trevor Croft, Jay Gorrell
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

import java.util.Set;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.macro.Macro;
import net.rptools.maptool.client.macro.MacroDefinition;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Token;

/**
 * Macro to run the macro on the selected tokens
 * 
 */
@MacroDefinition(
    name = "tmacro",
    aliases = { "tm" },
    description = "Run the given macro on the currently selected tokens"
)
public class RunTokenMacroMacro implements Macro {
  
  /**
   * @see net.rptools.maptool.client.macro.Macro#execute(java.lang.String)
   */
  public void execute(String macro) {
	  
	  Set<GUID> selectedTokenSet = MapTool.getFrame().getCurrentZoneRenderer().getSelectedTokenSet();
	  if (selectedTokenSet.size() == 0) {
		  MapTool.addLocalMessage("No tokens selected");
		  return;
	  }
	  
	  for (GUID tokenId : selectedTokenSet) {
		  
		  Token token = MapTool.getFrame().getCurrentZoneRenderer().getZone().getToken(tokenId);
		  if (token == null) {
			  continue;
		  }
		  
		  String tmacro = token.getMacro(macro);
		  if (tmacro == null) {
			  continue;
		  }
		  
		  MapTool.getFrame().getCommandPanel().getCommandTextArea().setText("/im " + token.getId() + ": " + tmacro);
		  MapTool.getFrame().getCommandPanel().commitCommand();
	  }
  }
}
