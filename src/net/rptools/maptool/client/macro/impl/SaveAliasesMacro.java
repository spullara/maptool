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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;

import net.rptools.lib.FileUtil;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.macro.Macro;
import net.rptools.maptool.client.macro.MacroDefinition;
import net.rptools.maptool.client.macro.MacroManager;

@MacroDefinition(
	name = "savealiases",
	aliases = { },
	description = "Save all current aliases to a file.  See loadaliases to load them back in."
)
public class SaveAliasesMacro implements Macro {

    public void execute(String macro) {
    	
    	File aliasFile = null;
    	if (macro.length() > 0) {
    		aliasFile = new File(macro);
    	} else {
    	
	    	JFileChooser chooser = MapTool.getSaveFileChooser();
			chooser.setDialogTitle("Save Aliases");
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	
			if (chooser.showOpenDialog(MapTool.getFrame()) != JFileChooser.APPROVE_OPTION) {
				return;
			}
			
			aliasFile = chooser.getSelectedFile();
    	}

    	if (aliasFile.getName().indexOf(".") < 0) {
			aliasFile = new File(aliasFile.getAbsolutePath()+".alias");
		}
		if (aliasFile.exists() && !MapTool.confirm("Overwrite existing file?")) {
			return;
		}

		try {
			
			StringBuilder builder = new StringBuilder();
			builder.append("# MapTool Aliases - created ").append(new SimpleDateFormat().format(new Date())).append("\n\n");

			Map<String, String> aliasMap = MacroManager.getAliasMap();
			List<String> aliasList = new ArrayList<String>();
			aliasList.addAll(aliasMap.keySet());
			Collections.sort(aliasList);
			for (String key : aliasList) {
				String value = aliasMap.get(key);
				
				builder.append(key).append(":").append(value).append("\n"); // LATER: this character should be externalized and shared with the load alias macro
			}
			
			FileUtil.writeBytes(aliasFile, builder.toString().getBytes());

			MapTool.addLocalMessage("Aliases saved.");
		} catch (FileNotFoundException fnfe) {
			MapTool.addLocalMessage("Could not load alias file: File not found");
		} catch (IOException ioe) {
			MapTool.addLocalMessage("Could not load alias file: " + ioe);
		}
    }
}
