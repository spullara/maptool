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

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.macro.Macro;
import net.rptools.maptool.client.macro.MacroDefinition;
import net.rptools.maptool.model.LookupTable;
import net.rptools.maptool.model.TextMessage;

@MacroDefinition(
	name = "table",
	aliases = { "tbl" },
	description = "Run a table lookup. Usage: /tbl &lt;table name*gt; [value to lookup, can be a dice roll]"
)
public class LookupTableMacro extends AbstractMacro {

    public void execute(String macro) {
    	macro = processText(macro).trim();
        StringBuilder sb = new StringBuilder();

        if (macro.trim().length() == 0) {
        	MapTool.addLocalMessage("Must specify a table");
        	return;
        }
        
        int split = macro.indexOf(" ");
        String tableName = macro;
        String value = null;
        if (split > 0) {
        	tableName = macro.substring(0, split).trim();
        	value = macro.substring(split+1).trim();
        	
        	if (value.length() == 0) {
        		value = null;
        	}
        }
        
        
    	LookupTable lookupTable = MapTool.getCampaign().getLookupTableMap().get(tableName);
    	if (lookupTable == null) {
    		MapTool.addLocalMessage("No such table '" + tableName + "'");
    		return;
    	}

    	sb.append("Table ").append(tableName).append(" (");
        sb.append(MapTool.getFrame().getCommandPanel().getIdentity());
        sb.append("): ");
        
        sb.append("<span style='color:red'>");
        
        sb.append(lookupTable.getLookup(value));
        sb.append("</span>");
        MapTool.addMessage(TextMessage.say(sb.toString()));
    }
}
