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
	
	protected List<String> getWords(String line) {
		
		List<String> list = new ArrayList<String>();

		while (line != null && line.trim().length() > 0) {

			line = line.trim();
			System.out.println("'" + line + "'");
			List<String> split = splitNextWord(line);

			String nextWord = split.get(0);
			line = split.get(1);

			if (nextWord == null) {
				continue;
			}
			
			list.add(nextWord);
		}
		
		return list;
	}
	
	protected String getFirstWord(String line) {
		List<String> split = splitNextWord(line);
		return split != null ? split.get(0) : null;
	}
	
	protected List<String> splitNextWord(String line) {
		
		line = line.trim();
		if (line.length() == 0) {
			return null;
		}
		
		StringBuilder builder = new StringBuilder();
		
		boolean quoted = line.charAt(0) == '"';

		int start = quoted ? 1 : 0;
		int end = start;
		for (; end < line.length(); end++) {
			
			char c = line.charAt(end);
			if (quoted) {
				if (c == '"') {
					break;
				}
			} else {
				if (Character.isWhitespace(c)) {
					break;
				}
			}
			
			builder.append(c);
		}
		
		return Arrays.asList(new String[]{line.substring(start, end), line.substring(Math.min(end+1, line.length()))});
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
