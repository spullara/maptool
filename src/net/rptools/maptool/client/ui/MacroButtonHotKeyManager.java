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
package net.rptools.maptool.client.ui;

import java.util.HashMap;
import java.util.Map;

import javax.swing.KeyStroke;

import net.rptools.maptool.client.ui.macrobutton.AbstractMacroButton;
import net.rptools.maptool.client.MapTool;

/**
 * @author tylere
 */
public class MacroButtonHotKeyManager {

	
	//Changing and adding more hotkeys should work smoothly, however hotkeys[0]
	//should be kept as the "no hotkey" option, regardless of the actual String used for it.
	//HOTKEYS strings must follow the syntax required by getKeyStroke(String s)
	public static final String[] HOTKEYS = { "None", "F2", "F3", "F4", "F5", "F6", "F7", "F8",
		"F9", "F10", "F11", "F12", "alt F1", "alt F2", "alt F3", "alt F5", "alt F6",
		"alt F7", "alt F8", "alt F9", "alt F10", "alt F11", "alt F12", "ctrl F1", "ctrl F2",
		"ctrl F3", "ctrl F4", "ctrl F5", "ctrl F6", "ctrl F7", "ctrl F8", "ctrl F9",
		"ctrl F10", "ctrl F11", "ctrl F12", "shift F1", "shift F2", "shift F3", "shift F4",
		"shift F5", "shift F6", "shift F7", "shift F8", "shift F9", "shift F10",
		"shift F11", "shift F12"};
	
	// our own map is required to allow us to search which button has an associated keystroke
	private static Map<KeyStroke, AbstractMacroButton> buttonsByKeyStroke = new HashMap<KeyStroke, AbstractMacroButton>();
	private AbstractMacroButton macroButton;

	public MacroButtonHotKeyManager(AbstractMacroButton macroButton) {
		this.macroButton = macroButton;
	}

	public void assignKeyStroke(String hotKey) {
		
		// remove the old keystroke from our map
		KeyStroke oldKeystroke = KeyStroke.getKeyStroke(macroButton.getHotKey());
		buttonsByKeyStroke.remove(oldKeystroke);	
		// assign the new hotKey
		macroButton.setHotKey(hotKey);
		
		// HOTKEYS[0] is no hotkey.
		if( !hotKey.equals(HOTKEYS[0])) {
			
			KeyStroke keystroke = KeyStroke.getKeyStroke(hotKey);
			
			// Check what button the hotkey is already assigned to
			AbstractMacroButton oldButton = buttonsByKeyStroke.get(keystroke);
			
			// if it is already assigned, then update the old mapped button
			if (oldButton != macroButton && oldButton != null) {
				
				// tell the old button it no longer has a hotkey
				oldButton.setHotKey(HOTKEYS[0]);
				// remove the hot key reference from the button's text
				oldButton.setText(oldButton.getButtonText());	
				//remove from our map
				buttonsByKeyStroke.remove(keystroke);
				// need to save settings
				oldButton.savePreferences();
			}
			
			// Add the new button and keystroke to our map
			buttonsByKeyStroke.put(keystroke, macroButton);
			
			// keep macrotabbedpane's keystrokes in sync
			if (MapTool.getFrame() != null) {
				MapTool.getFrame().getMacroTabbedPane().updateKeyStrokes();
			}
		
		}
	}	
	
	public static Map<KeyStroke, AbstractMacroButton> getKeyStrokeMap() {
		return buttonsByKeyStroke;
	}
	
	// when the user loads a campaign the saved macro keystrokes should be restored
	// and the possible conflicts with the global macro keystrokes should be resolved.
	public static void clearKeyStrokes() {
		buttonsByKeyStroke.clear();
	}
}
