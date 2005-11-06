package net.rptools.maptool.client.macro.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.macro.Macro;
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

	public void execute(String parameter) {
		MapTool.addMessage("List of current commands:");

		List<Macro> macros = new ArrayList<Macro>(MacroManager
				.getRegisteredMacros());
		Collections.sort(macros, MACRO_NAME_COMPARATOR);

		for (Macro macro : macros) {
			MacroDefinition def = macro.getClass().getAnnotation(
					MacroDefinition.class);
			if (!def.hidden()) {
				StringBuilder sb = new StringBuilder(64);
				sb.append(def.name()).append(": ").append(
						def.description());
				String[] aliases = def.aliases();
				if (aliases != null && aliases.length > 0) {
					sb.append(" (");
					for (int i = 0; i < aliases.length; i++) {
						if (i > 0)
							sb.append(", ");
						sb.append(aliases[i]);
					}
					sb.append(")");
				}
				MapTool.addMessage(sb.toString());
			}
		}
	}
}
