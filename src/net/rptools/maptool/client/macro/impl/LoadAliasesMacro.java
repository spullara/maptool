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
import java.util.List;

import javax.swing.JFileChooser;

import net.rptools.lib.FileUtil;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.macro.Macro;
import net.rptools.maptool.client.macro.MacroDefinition;
import net.rptools.maptool.client.macro.MacroManager;

@MacroDefinition(
	name = "loadaliases",
	aliases = { },
	description = "Load a file that contains aliases, one per line, with a : between the name and the value (just as if you were typing it in)"
)
public class LoadAliasesMacro implements Macro {

    public void execute(String macro) {

    	File aliasFile = null;
    	if (macro.length() > 0) {
    		aliasFile = new File(macro);
    	} else {
    	
	    	JFileChooser chooser = MapTool.getFrame().getLoadFileChooser();
			chooser.setDialogTitle("Load Aliases");
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	
			if (chooser.showOpenDialog(MapTool.getFrame()) != JFileChooser.APPROVE_OPTION) {
				return;
			}
			
			aliasFile = chooser.getSelectedFile();
    	}
    	
		if (aliasFile.getName().indexOf(".") < 0) {
			aliasFile = new File(aliasFile.getAbsolutePath() + ".alias");
		}
		if (!aliasFile.exists()) {
			MapTool.addLocalMessage("Could not find alias file: " + aliasFile);
			return;
		}

		try {
			MapTool.addLocalMessage("Loading aliases:");
			List<String> lineList = FileUtil.getLines(aliasFile);
			
			for (String line : lineList) {
				
				line = line.trim();
				if (line.length() == 0 || line.charAt(0) == '#') {
					continue;
				}
				
				// Split into components
				String name = line;
				String value = null;
				int split = line.indexOf(":");
				if (split > 0) {
					name = line.substring(0, split);
					value = line.substring(split+1).trim();
				}
				
				if (value != null) {
					MapTool.addLocalMessage("&nbsp;&nbsp;&nbsp;'" + name + "'");
					MacroManager.setAlias(name, value);
				} else {
					MapTool.addLocalMessage("&nbsp;&nbsp;&nbsp;Ignoring '" + name + "'");
				}
				
			}
			
		} catch (FileNotFoundException fnfe) {
			MapTool.addLocalMessage("Could not load alias file: File not found");
		} catch (IOException ioe) {
			MapTool.addLocalMessage("Could not load alias file: " + ioe);
		}
    }
}
