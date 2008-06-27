/* The MIT License
 * 
 * Copyright (c) 2005 David Rice, Trevor Croft, Jay Gorrell
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

import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.macro.Macro;
import net.rptools.maptool.client.macro.MacroDefinition;
import net.rptools.maptool.client.ui.token.TokenOverlay;

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
  public void execute(String macro) {
    
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
      List<TokenOverlay> overlays = new ArrayList<TokenOverlay>();
      for (String overlay : MapTool.getCampaign().getCampaignProperties().getTokenStatesMap().keySet()) {
        overlays.add(MapTool.getCampaign().getCampaignProperties().getTokenStatesMap().get(overlay));
      } // endfor
      encoder.writeObject(overlays);
      encoder.close();
      MapTool.addLocalMessage("There were " + overlays.size() + " token states saved.");
    } catch (FileNotFoundException fnfe) {
      MapTool.addLocalMessage("Could not save the token states file: File not found");
    } // endif
  }
}
