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

import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolMacroContext;
import net.rptools.maptool.client.macro.Macro;
import net.rptools.maptool.client.macro.MacroContext;
import net.rptools.maptool.client.macro.MacroDefinition;
import net.rptools.maptool.client.ui.token.BooleanTokenOverlay;

/**
 * Save the current list of token states for use later.
 * 
 * @author jgorrell
 * @version $Revision$ $Date$ $Author$
 */
@MacroDefinition(
    name = "savetokenstates",
    aliases = { "tss" },
    description = "Save the current set of token states to a file."
)
public class SaveTokenStatesMacro implements Macro {

  /**
   * @see net.rptools.maptool.client.macro.Macro#execute(java.lang.String)
   */
  public void execute(MacroContext context, String macro, MapToolMacroContext executionContext) {
    
    // Read the file from the command line
    File aliasFile = null;
    if (macro.length() > 0) {
      aliasFile = new File(macro);
    } else {
      
      // Not on the command line, ask the user for a file.
      JFileChooser chooser = MapTool.getFrame().getSaveFileChooser();
      chooser.setDialogTitle("Save Token States");
      chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      if (chooser.showOpenDialog(MapTool.getFrame()) != JFileChooser.APPROVE_OPTION) return;
      aliasFile = chooser.getSelectedFile();
    } // endif
    
    // Make it an XML file if type isn't set, check for overwrite
    if (aliasFile.getName().indexOf(".") < 0)
      aliasFile = new File(aliasFile.getAbsolutePath() + "-tokenStates.xml");
    if (aliasFile.exists() && !MapTool.confirm("Overwrite existing file?")) return;
    
    // Save the file using a decoder
    try {
      XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(aliasFile)));
      List<BooleanTokenOverlay> overlays = new ArrayList<BooleanTokenOverlay>();
      for (String overlay : MapTool.getCampaign().getTokenStatesMap().keySet()) {
        overlays.add(MapTool.getCampaign().getTokenStatesMap().get(overlay));
      } // endfor
      encoder.writeObject(overlays);
      encoder.close();
      MapTool.addLocalMessage("There were " + overlays.size() + " token states saved.");
    } catch (FileNotFoundException fnfe) {
      MapTool.addLocalMessage("Could not save the token states file: File not found");
    } // endif
  }
}
