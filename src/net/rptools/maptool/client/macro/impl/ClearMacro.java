/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package net.rptools.maptool.client.macro.impl;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolMacroContext;
import net.rptools.maptool.client.macro.Macro;
import net.rptools.maptool.client.macro.MacroContext;
import net.rptools.maptool.client.macro.MacroDefinition;

/**
 * Macro to clear the message panel
 * 
 * @author jgorrell
 * @version $Revision$ $Date$ $Author$
 */
@MacroDefinition(
		name = "clear",
		aliases = { "clr", "cls" },
		description = "clear.description")
public class ClearMacro implements Macro {
	/**
	 * @see net.rptools.maptool.client.macro.Macro#execute(java.lang.String)
	 */
	public void execute(MacroContext context, String macro, MapToolMacroContext executionContext) {
		MapTool.getFrame().getCommandPanel().clearMessagePanel();
	}
}
