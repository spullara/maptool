/* The MIT License
 * 
 * Copyright (c) 2005 David Rice, Trevor Croft
 * 
 * Permission is hereby granted, free of charge, to any person 
 * obtaining a copy of this software and associated documentation files 
 * (the "Software"), to deal in the Software without restriction, 
 * including without limitation the rights to use, copy, modify, merge, 
 * publish, distribute, sublicense, and/or sell copies of the Software, 
 * and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be 
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS 
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN 
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
 * SOFTWARE.
 */
package net.rptools.maptool.client.macro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jeta.forms.gui.common.parsers.TokenMgrError;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.macro.impl.AbstractRollMacro;
import net.rptools.maptool.client.macro.impl.AddTokenStateMacro;
import net.rptools.maptool.client.macro.impl.AliasMacro;
import net.rptools.maptool.client.macro.impl.ClearAliasesMacro;
import net.rptools.maptool.client.macro.impl.ClearMacro;
import net.rptools.maptool.client.macro.impl.EmitMacro;
import net.rptools.maptool.client.macro.impl.EmoteMacro;
import net.rptools.maptool.client.macro.impl.GotoMacro;
import net.rptools.maptool.client.macro.impl.HelpMacro;
import net.rptools.maptool.client.macro.impl.ImpersonateMacro;
import net.rptools.maptool.client.macro.impl.LoadAliasesMacro;
import net.rptools.maptool.client.macro.impl.LoadTokenStatesMacro;
import net.rptools.maptool.client.macro.impl.LookupTableMacro;
import net.rptools.maptool.client.macro.impl.RollAllMacro;
import net.rptools.maptool.client.macro.impl.RollGMMacro;
import net.rptools.maptool.client.macro.impl.RollMeMacro;
import net.rptools.maptool.client.macro.impl.RollSecretMacro;
import net.rptools.maptool.client.macro.impl.RunTokenMacroMacro;
import net.rptools.maptool.client.macro.impl.RunTokenSpeechMacro;
import net.rptools.maptool.client.macro.impl.SaveAliasesMacro;
import net.rptools.maptool.client.macro.impl.SaveTokenStatesMacro;
import net.rptools.maptool.client.macro.impl.SayMacro;
import net.rptools.maptool.client.macro.impl.SetTokenStateMacro;
import net.rptools.maptool.client.macro.impl.ThinkMacro;
import net.rptools.maptool.client.macro.impl.UndefinedMacro;
import net.rptools.maptool.client.macro.impl.WhisperMacro;

/**
 * @author drice
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class MacroManager {

	private static final int MAX_RECURSE_COUNT = 10;

	private static Macro UNDEFINED_MACRO = new UndefinedMacro();

	private static Map<String, Macro> MACROS = new HashMap<String, Macro>();

	private static Map<String, String> aliasMap = new HashMap<String, String>();

	static {
		registerMacro(new SayMacro());
		registerMacro(new HelpMacro());
		registerMacro(new GotoMacro());
		registerMacro(new ClearMacro());
		registerMacro(new RollMeMacro());
		registerMacro(new RollAllMacro());
		registerMacro(new RollGMMacro());
		registerMacro(new WhisperMacro());
		registerMacro(new EmoteMacro());
		registerMacro(new AliasMacro());
		registerMacro(new LoadAliasesMacro());
		registerMacro(new SaveAliasesMacro());
		registerMacro(new ClearAliasesMacro());
		registerMacro(new AddTokenStateMacro());
		registerMacro(new LoadTokenStatesMacro());
		registerMacro(new SaveTokenStatesMacro());
		registerMacro(new SetTokenStateMacro());
		registerMacro(new RollSecretMacro());
		registerMacro(new EmitMacro());
		registerMacro(new ThinkMacro());
		registerMacro(new ImpersonateMacro());
		registerMacro(new RunTokenMacroMacro());
		registerMacro(new RunTokenSpeechMacro());
		registerMacro(new LookupTableMacro());

		registerMacro(UNDEFINED_MACRO);
	}

	public static void setAlias(String key, String value) {
		aliasMap.put(key, value);
	}

	public static void removeAlias(String key) {
		aliasMap.remove(key);
	}

	public static void removeAllAliases() {
		aliasMap.clear();
	}
	
	public static Map<String, String> getAliasMap() {
		return Collections.unmodifiableMap(aliasMap);
	}
	
	public static Set<Macro> getRegisteredMacros() {
		Set<Macro> ret = new HashSet<Macro>();
		ret.addAll(MACROS.values());
		return ret;
	}

	public static Macro getRegisteredMacro(String name) {
		Macro ret = MACROS.get(name);
		if (ret == null)
			return UNDEFINED_MACRO;
		return ret;
	}

	public static void registerMacro(Macro macro) {
		MacroDefinition def = macro.getClass().getAnnotation(
				MacroDefinition.class);

		if (def == null)
			return;

		MACROS.put(def.name(), macro);
		for (String alias : def.aliases()) {
			MACROS.put(alias, macro);
		}
	}

	private static final Pattern MACRO_PAT = Pattern
			.compile("^(\\w+)\\s*(.*)$");
	
	public static void executeMacro(String command) {

		try {
			command = preprocess(command);
	
			int recurseCount = 0;
			while (recurseCount < MAX_RECURSE_COUNT) {
	
				recurseCount++;
	
				command = command.trim();
				if (command == null || command.length() == 0) {
					return;
				}
				
				if (command.charAt(0) == '/') {
					command = command.substring(1);
				} else {
					// Default to a say
					command = "s " + command;
				}
	
				// preprocess line
				command = AbstractRollMacro.inlineRoll(command);
				
				
				// Macro name is the first word
				Matcher m = MACRO_PAT.matcher(command);
				if (m.matches()) {
					String key = m.group(1);
					String details = m.group(2);
	
					Macro macro = getRegisteredMacro(key);
	
					if (macro != UNDEFINED_MACRO) {
						executeMacro(macro, details);
						return;
					}
	
					// Is it an alias ?
					String alias = aliasMap.get(key);
					if (alias == null) {
	
						executeMacro(UNDEFINED_MACRO, command);
						return;
					}
					
					command = resolveAlias(alias, details);
					continue;
				} else {
	
					// Undefined macro shows the bad command
					executeMacro(UNDEFINED_MACRO, command);
					return;
				}
			}
		} catch (Exception e) {
			MapTool.addLocalMessage("Could not execute the command: " + e.getMessage());
			return;
		}
		
		// We'll only get here if the recurseCount is exceeded
		MapTool.addLocalMessage("'" + command
				+ "': Too many resolves, perhaps an infinite loop?");
		
	}

	static String preprocess(String command) {
		
		command = command.replace("\n", "<br>");
		
		return command;
	}
	
	// Package level for testing
	static String resolveAlias(String aliasText, String details) {
		
		return performSubstitution(aliasText, details);
	}

	private static final Pattern SUBSTITUTION_PATTERN = Pattern.compile("\\$\\{([^\\}]+)\\}|\\$(\\w+)");
	// Package level for testing
	static String performSubstitution(String text, String details){
		
		List<String> detailList = split(details);

		StringBuffer buffer = new StringBuffer();
		Matcher matcher = SUBSTITUTION_PATTERN.matcher(text);
		while (matcher.find()) {
			
			String replacement = details;
			String replIndexStr = matcher.group(1);
			if (replIndexStr == null) {
				replIndexStr = matcher.group(2);
			}
			if (!"*".equals(replIndexStr)) {
				try {
					int replaceIndex = Integer.parseInt(replIndexStr);
					if (replaceIndex > detailList.size() || replaceIndex < 1) {
						replacement = "";
					} else {
						// 1-based
						replacement = detailList.get(replaceIndex-1);
					}
				} catch (NumberFormatException nfe) {
					
					// Try an alias lookup
					replacement = aliasMap.get(replIndexStr);
					if (replacement == null) {
						replacement = "(error: " + replIndexStr + " is not found)";
					}
				}
			}
		    matcher.appendReplacement(buffer, replacement);
		 }
		 matcher.appendTail(buffer);	
		 
		return buffer.toString();
	}
	
	// Package level for testing
	// TODO: This should probably go in a util class in rplib
	static List<String> split(String line) {
		
		List<String> list = new ArrayList<String>();
		StringBuilder currentWord = new StringBuilder();
		boolean isInQuote=false;
		char previousChar = 0;
		for (int i = 0; i < line.length(); i++) {
			
			char ch = line.charAt(i);
			
			try {
				// Word boundaries
				if (Character.isWhitespace(ch) && !isInQuote) {
					if (currentWord.length() > 0) {
						list.add(currentWord.toString());
					}
					currentWord.setLength(0);
					continue;
				}
				
				// Quoted boundary
				if (ch == '"' && previousChar != '\\') {
					
					if (isInQuote) {
						isInQuote = false;
						if (currentWord.length() > 0) {
							list.add(currentWord.toString());
							currentWord.setLength(0);
						}
					} else {
						isInQuote = true;
					}
					
					continue;
				}
				
				if (ch == '\\') {
					continue;
				}
				
				currentWord.append(ch);
				
			} finally {				
				previousChar = ch;
			}
		}
		
		if (currentWord.length() > 0) {
			list.add(currentWord.toString());
		}
		
		return list;
	}
	
	private static void executeMacro(Macro macro, String parameter) {
		macro.execute(parameter);
	}

}
