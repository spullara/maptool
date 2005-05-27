package net.rptools.maptool.client.macro;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.macro.MacroManager.COMMAND;

public class HelpMacro implements Macro {

	public void execute(String macro) {
		MapTool.addMessage("List of current commands:");
		
		for(COMMAND cmd : COMMAND.values()) {
			MapTool.addMessage("  " + cmd.name());
		}
	}

}
