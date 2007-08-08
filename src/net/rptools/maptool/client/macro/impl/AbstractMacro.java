package net.rptools.maptool.client.macro.impl;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.macro.Macro;

public abstract class AbstractMacro implements Macro {


	protected String processText(String incoming) {
		return MapTool.getFrame().getCommandPanel().getChatProcessor().process(incoming);
	}
}
