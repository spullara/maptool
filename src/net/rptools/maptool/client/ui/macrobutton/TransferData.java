package net.rptools.maptool.client.ui.macrobutton;

import java.io.Serializable;

public class TransferData implements Serializable {
	
	public String macro;
	public String command;

	public TransferData(String macro, String command) {
		this.macro = macro;
		this.command = command;
	}
}
