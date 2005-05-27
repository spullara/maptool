package net.rptools.maptool.client.macro;

import net.rptools.maptool.client.MapTool;

public class UndefinedMacro implements Macro {

	public void execute(String macro) {
		MapTool.addMessage("'" + macro + "': Unknown command.  Try /help for a list of commands");
	}

}
