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

import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.macro.MacroContext;
import net.rptools.maptool.client.macro.MacroDefinition;
import net.rptools.maptool.client.macro.MacroManager;
import net.rptools.maptool.model.LookupTable;
import net.rptools.maptool.model.TextMessage;
import net.rptools.maptool.model.LookupTable.LookupEntry;
import net.rptools.parser.ParserException;

@MacroDefinition(
	name = "table",
	aliases = { "tbl" },
	description = "Run a table lookup. Usage: /tbl &lt;table name*gt; [value to lookup, can be a dice roll]"
)
public class LookupTableMacro extends AbstractMacro {

    public void execute(MacroContext context, String macro) {
    	macro = processText(macro).trim();
        StringBuilder sb = new StringBuilder();

        if (macro.trim().length() == 0) {
        	MapTool.addLocalMessage("Must specify a table");
        	return;
        }

        List<String> words = splitNextWord(macro);
        String tableName = words.get(0);
        String value = null;
        if (words.size() > 1) {
        	value = words.get(1);
        	
        	if (value.length() == 0) {
        		value = null;
        	}
        }
        
        
    	LookupTable lookupTable = MapTool.getCampaign().getLookupTableMap().get(tableName);
    	if (lookupTable == null) {
    		MapTool.addLocalMessage("No such table '" + tableName + "'");
    		return;
    	}

    	try {
	    	LookupEntry result = lookupTable.getLookup(value);
	    	String lookupValue = result.getValue();
	
	    	// Command handling
	    	if (result != null && lookupValue.startsWith("/")) {
	    		MacroManager.executeMacro(lookupValue);
	    		return;
	    	}
	    	
	    	sb.append("Table ").append(tableName).append(" (");
	        sb.append(MapTool.getFrame().getCommandPanel().getIdentity());
	        sb.append("): ");
	        
	    	if (result.getImageId() != null) {
	    		sb.append("<img src=\"asset://").append(result.getImageId()).append("\" alt=\"").append(result.getValue()).append("\">");
	    	} else {
		        sb.append("<span style='color:red'>");
		        
		        sb.append(lookupValue);
		        sb.append("</span>");
	    	}

	    	MapTool.addMessage(TextMessage.say(context.getTransformationHistory(), sb.toString()));
    	} catch (ParserException pe) {
	        MapTool.addLocalMessage("Could not do table lookup: " + pe.getMessage());
    	}
    }
}
