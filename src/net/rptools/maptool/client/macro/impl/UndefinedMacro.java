package net.rptools.maptool.client.macro.impl;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.macro.Macro;
import net.rptools.maptool.client.macro.MacroDefinition;

@MacroDefinition(
	name = "undefined", 
	description = "Undefined macro.", 
	hidden = true
)
public class UndefinedMacro implements Macro {

	public void execute(String macro) {
		MapTool.addLocalMessage("'" + macro
				+ "': Unknown command.  Try /help for a list of commands.");
	}

}
