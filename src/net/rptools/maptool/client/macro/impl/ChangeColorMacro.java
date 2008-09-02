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

import java.awt.Color;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.macro.MacroContext;
import net.rptools.maptool.client.macro.MacroDefinition;

@MacroDefinition(
	name = "color",
	aliases = { "cc" },
	description = "Change your chat text color via macros.  Color must be in Hexadecimal format.  Example: /cc #ff0099"
)
public class ChangeColorMacro extends AbstractMacro {

	public void execute(MacroContext context, String macro) {
		macro = processText(macro);
		Color newColor = Color.decode(macro);
		MapTool.getFrame().getCommandPanel().getTextColorWell().setColor(newColor);
	}
}
