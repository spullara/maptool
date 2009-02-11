/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package net.rptools.maptool.client.macro.impl;

import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolMacroContext;
import net.rptools.maptool.client.macro.MacroContext;
import net.rptools.maptool.client.macro.MacroDefinition;
import net.rptools.maptool.client.macro.MacroManager;
import net.rptools.maptool.model.LookupTable;
import net.rptools.maptool.model.TextMessage;
import net.rptools.maptool.model.LookupTable.LookupEntry;
import net.rptools.maptool.util.StringUtil;
import net.rptools.parser.ParserException;

@MacroDefinition(
	name = "table",
	aliases = { "tbl" },
	description = "Run a table lookup. Usage: /tbl &lt;table name*gt; [value to lookup, can be a dice roll]"
)
public class LookupTableMacro extends AbstractMacro {

    public void execute(MacroContext context, String macro, MapToolMacroContext executionContext) {
    	macro = processText(macro).trim();
        StringBuilder sb = new StringBuilder();

        if (macro.trim().length() == 0) {
        	MapTool.addLocalMessage("Must specify a table");
        	return;
        }

        List<String> words = StringUtil.splitNextWord(macro);
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
