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
import net.rptools.maptool.client.macro.MacroContext;
import net.rptools.maptool.client.macro.MacroDefinition;
import net.rptools.maptool.client.macro.MacroManager;

@MacroDefinition(
	name = "savealiases",
	aliases = { },
	description = "Save all current aliases to a file.  See loadaliases to load them back in."
)
public class SaveAliasesMacro implements Macro {

    public void execute(MacroContext context, String macro, boolean trusted, String macroName) {
    	
    	File aliasFile = null;
    	if (macro.length() > 0) {
    		aliasFile = new File(macro);
    	} else {
    	
	    	JFileChooser chooser = MapTool.getFrame().getSaveFileChooser();
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
