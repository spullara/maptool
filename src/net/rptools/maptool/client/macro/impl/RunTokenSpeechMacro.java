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

import java.util.Set;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.macro.Macro;
import net.rptools.maptool.client.macro.MacroContext;
import net.rptools.maptool.client.macro.MacroDefinition;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Token;

/**
 * Macro to run the speech ID on the given token
 * 
 */
@MacroDefinition(
    name = "tsay",
    aliases = { "ts" },
    description = "Say the given speech on the currently selected tokens"
)
public class RunTokenSpeechMacro implements Macro {
  
  /**
   * @see net.rptools.maptool.client.macro.Macro#execute(java.lang.String)
   */
  public void execute(MacroContext context, String macro, boolean trusted, String macroName) {
	  
	  Set<GUID> selectedTokenSet = MapTool.getFrame().getCurrentZoneRenderer().getSelectedTokenSet();
	  if (selectedTokenSet.size() == 0) {
		  MapTool.addLocalMessage("No tokens selected");
		  return;
	  }
	  
	  for (GUID tokenId : selectedTokenSet) {
		  
		  Token token = MapTool.getFrame().getCurrentZoneRenderer().getZone().getToken(tokenId);
		  if (token == null) {
			  continue;
		  }
		  
		  String tmacro = token.getSpeech(macro);
		  if (tmacro == null) {
			  continue;
		  }
		  
		  MapTool.getFrame().getCommandPanel().getCommandTextArea().setText("/im " + token.getId() + ": " + tmacro);
		  MapTool.getFrame().getCommandPanel().commitCommand();
	  }
  }
}
