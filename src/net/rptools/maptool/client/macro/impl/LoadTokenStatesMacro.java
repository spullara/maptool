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

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import javax.swing.JFileChooser;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.macro.Macro;
import net.rptools.maptool.client.macro.MacroContext;
import net.rptools.maptool.client.macro.MacroDefinition;
import net.rptools.maptool.client.ui.token.BooleanTokenOverlay;

/**
 * Load the token states from a file.
 * 
 * @author jgorrell
 * @version $Revision$ $Date$ $Author$
 */
@MacroDefinition(
    name = "loadtokenstates",
    aliases = { "tsl" },
    description = "Load all of the token states from a file."
)
public class LoadTokenStatesMacro implements Macro {

  /**
   * @see net.rptools.maptool.client.macro.Macro#execute(java.lang.String)
   */
  @SuppressWarnings("unchecked")
  public void execute(MacroContext context, String macro, boolean trusted, String macroName) {
    
    // Was the token states file passed?
    File aliasFile = null;
    if (macro.length() > 0) {
      aliasFile = new File(macro);
    } else {
      
      // Ask the user for the token states file
      JFileChooser chooser = MapTool.getFrame().getLoadFileChooser();
      chooser.setDialogTitle("Load Token States");
      chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      if (chooser.showOpenDialog(MapTool.getFrame()) != JFileChooser.APPROVE_OPTION) return;
      aliasFile = chooser.getSelectedFile();
    } // endif

    // Make it an XML file if type isn't set, check for existance
    if (aliasFile.getName().indexOf(".") < 0)
      aliasFile = new File(aliasFile.getAbsolutePath() + "-tokenStates.xml");
    if (!aliasFile.exists()) {
      MapTool.addLocalMessage("Could not find token states file: " + aliasFile);
      return;
    } // endif
    
    // Read the serialized set of states
    try {
      XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(aliasFile)));
      List<BooleanTokenOverlay> overlays = (List<BooleanTokenOverlay>)decoder.readObject();
      decoder.close();
      for (BooleanTokenOverlay overlay : overlays) {
          MapTool.getCampaign().getTokenStatesMap().put(overlay.getName(), overlay);
      } // endfor
      MapTool.addLocalMessage("There were " + overlays.size() + " token states loaded.");
    } catch (FileNotFoundException e) {
      MapTool.addLocalMessage("Could not load the token states file: File not found");
    } // endtry
  }

}
