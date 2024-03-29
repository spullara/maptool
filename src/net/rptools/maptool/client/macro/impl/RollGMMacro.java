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
import net.rptools.maptool.client.macro.MacroContext;
import net.rptools.maptool.client.macro.MacroDefinition;
import net.rptools.maptool.language.I18N;
import net.rptools.maptool.model.TextMessage;

@MacroDefinition(
		name = "rollgm",
		aliases = { "rgm" },
		description = "rollgm.description")
public class RollGMMacro extends AbstractRollMacro {
	public void execute(MacroContext context, String macro, MapToolMacroContext executionContext) {
		String result = roll(macro);
		if (result != null) {
			MapTool.addMessage(new TextMessage(TextMessage.Channel.GM, null, MapTool.getPlayer().getName(), "* " +
					I18N.getText("rollgm.gm.string", MapTool.getPlayer().getName(), result), context.getTransformationHistory()));
			MapTool.addMessage(new TextMessage(TextMessage.Channel.ME, null, MapTool.getPlayer().getName(), "* " +
					I18N.getText("rollgm.self.string", result), context.getTransformationHistory()));
		}
	}
}
