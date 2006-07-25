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

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import javax.swing.JFileChooser;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.macro.Macro;
import net.rptools.maptool.client.macro.MacroDefinition;
import net.rptools.maptool.client.ui.token.TokenOverlay;
import net.rptools.maptool.client.ui.token.TokenStates;

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
  public void execute(String macro) {
    
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
      List<TokenOverlay> overlays = (List<TokenOverlay>)decoder.readObject();
      decoder.close();
      for (TokenOverlay overlay : overlays) {
        TokenStates.putOverlay(overlay);
      } // endfor
      MapTool.addLocalMessage("There were " + overlays.size() + " token states loaded.");
    } catch (FileNotFoundException e) {
      MapTool.addLocalMessage("Could not load the token states file: File not found");
    } // endtry
  }

}
