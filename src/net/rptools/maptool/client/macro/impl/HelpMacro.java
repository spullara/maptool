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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.macro.Macro;
import net.rptools.maptool.client.macro.MacroContext;
import net.rptools.maptool.client.macro.MacroDefinition;
import net.rptools.maptool.client.macro.MacroManager;

@MacroDefinition(name = "help", aliases = { "h" }, description = "Display list of available commands.")
public class HelpMacro implements Macro {

	private static Comparator<Macro> MACRO_NAME_COMPARATOR = new Comparator<Macro>() {
		public int compare(Macro macro1, Macro macro2) {
			MacroDefinition def1 = macro1.getClass().getAnnotation(
					MacroDefinition.class);
			MacroDefinition def2 = macro2.getClass().getAnnotation(
					MacroDefinition.class);

			return def1.name().compareTo(def2.name());
		}
	};

	public void execute(MacroContext context, String parameter) {
		
		StringBuilder builder = new StringBuilder();

		List<Macro> macros = new ArrayList<Macro>(MacroManager.getRegisteredMacros());
		Collections.sort(macros, MACRO_NAME_COMPARATOR);

		builder.append("<table border='1'>");
		builder.append("<tr><td><b>Command</b></td><td><b>Aliases</b></td><td><b>Description</b></td></tr>");
		for (Macro macro : macros) {
			MacroDefinition def = macro.getClass().getAnnotation(
					MacroDefinition.class);
			if (!def.hidden()) {
				builder.append("<TR>");

				builder.append("<TD>").append(def.name()).append("</TD>");

				builder.append("<td>");
				String[] aliases = def.aliases();
				if (aliases != null && aliases.length > 0) {
					for (int i = 0; i < aliases.length; i++) {
						if (i > 0) {
							builder.append(", ");
						}
						
						builder.append(aliases[i]);
					}
				}
				builder.append("</td>");

				// Escape HTML from the desciption
				String description = def.description().replace("<", "&lt;").replace(">", "&gt;");
				
				builder.append("<TD>").append(description).append("</td>");
				
			}
		}
		builder.append("</table>");
		
		MapTool.addLocalMessage(builder.toString());
	}
}
