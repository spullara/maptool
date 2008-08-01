package net.rptools.maptool.client.macro.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.macro.Macro;

public abstract class AbstractMacro implements Macro {


	protected String processText(String incoming) {
		return MapTool.getFrame().getCommandPanel().getChatProcessor().process(incoming);
	}
	

	
//	public static void main(String[] args) {
//		new AbstractMacro(){
//			public void execute(String macro) {
//
//				System.out.println(getWords(macro));
//			}
//		}.execute("one \"two three\" \"four five\"");
//	}
}
