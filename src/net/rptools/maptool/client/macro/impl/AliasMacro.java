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
package net.rptools.maptool.client.macro.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.macro.Macro;
import net.rptools.maptool.client.macro.MacroDefinition;
import net.rptools.maptool.client.macro.MacroManager;

/**
 * Macro to clear the message panel
 * 
 * @author jgorrell
 * @version $Revision$ $Date$ $Author$
 */
@MacroDefinition(
    name = "alias",
    aliases = { "alias" },
    description = "Create an alias."
)
public class AliasMacro implements Macro {

	public void execute(String macro) {
		
		macro = macro.trim();
		
		// Request for list ?
		if (macro.length() == 0) {
			handlePrintAliases();
			return;
		}
		
		// Split into components
		String name = macro;
		String value = null;
		int split = macro.indexOf(" "); // LATER: this character should be externalized and shared with the load alias macro
		if (split > 0) {
			name = macro.substring(0, split);
			value = macro.substring(split).trim();
		}
		
		MacroManager.setAlias(name, value);
		if (value != null) {
			MapTool.addLocalMessage("Alias '" + name + "' added");
		} else {
			MapTool.addLocalMessage("Alias '" + name + "' removed");
		}
	}
	
	private void handlePrintAliases() {
		StringBuilder builder = new StringBuilder();
		builder.append("Aliases:<br>");
		
		Map<String, String> aliasMap = MacroManager.getAliasMap();
		List<String> nameList = new ArrayList<String>();
		nameList.addAll(aliasMap.keySet());
		Collections.sort(nameList);
		
		for (String name : nameList) {
			String value = aliasMap.get(name);
			if (value == null) {
				continue;
			}
			
			builder.append("&nbsp;&nbsp;&nbsp;/").append(name).append(" => ").append(value).append("<br>");
		}

		MapTool.addLocalMessage(builder.toString());
	}
}
