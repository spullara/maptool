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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.swing.JFileChooser;

import net.rptools.lib.FileUtil;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolMacroContext;
import net.rptools.maptool.client.macro.Macro;
import net.rptools.maptool.client.macro.MacroContext;
import net.rptools.maptool.client.macro.MacroDefinition;
import net.rptools.maptool.client.macro.MacroManager;

@MacroDefinition(
	name = "loadaliases",
	aliases = { },
	description = "Load a file that contains aliases, one per line, with a : between the name and the value (just as if you were typing it in)"
)
public class LoadAliasesMacro implements Macro {

    public void execute(MacroContext context, String macro, MapToolMacroContext executionContext) {

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
