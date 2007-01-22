package net.rptools.maptool.client.ui;

import java.awt.event.ActionEvent;
import java.util.*;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import net.rptools.maptool.client.MapTool;


//Does all the admin for setting a new hotkey to a macroButton.
// Because the hotkey String (ie "F3") is ammended to button.text, 
// we need to update the button text of buttons that lose a hot key
// by it being assigned to another button.  Otherwise, the button will
// still think it has a hotkey and will display as such, plus there will
// be two (or more) buttons displaying a hotkey in their text, but only 
// one will actually be mapped to it.
//
public class MacroButtonHotKeyManager {

	
	//Changing and adding more hotkeys should work smoothly, however hotkeys[0]
	//should be kept as the "no hotkey" option, regardless of the actual String used for it.
	public static String[] HOTKEYS = { "None", "F2", "F3", "F4", "F5", "F6", "F7", "F8",
		"F9", "F10", "F11", "F12"};
	
	private static Map<KeyStroke, MacroButton> buttonsByHotkey = new HashMap<KeyStroke, MacroButton>();
	private MacroButton macroButton;
	
	public MacroButtonHotKeyManager(MacroButton macroButton) {

		this.macroButton = macroButton;

	}
	

	public void assignKeyStroke(String hotKey) {
		
		// remove the old keystroke
		KeyStroke oldKeystroke = KeyStroke.getKeyStroke(macroButton.getHotKey());
		//...from the keystroke map
		macroButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).remove(oldKeystroke);
		//...from our map
		buttonsByHotkey.remove(oldKeystroke);	
		// assign the new hotKey
		macroButton.setHotKey(hotKey);
		
		// HOTKEYS[0] is no hotkey.
		if( !hotKey.equals(HOTKEYS[0])) {
			
			KeyStroke keystroke = KeyStroke.getKeyStroke(hotKey);
			
			// Check what button the hotkey is already assigned to
			MacroButton oldButton = buttonsByHotkey.get(keystroke);
			
			// if it is already assigned, then update the old mapped button
			if (oldButton != macroButton && oldButton != null) {
				
				// tell the old button it no longer has a hotkey
				oldButton.setHotKey(HOTKEYS[0]);
				// remove the hot key reference from the button's text
				oldButton.setText(oldButton.getButtonText());	
				//remove from the keystroke map
				oldButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).remove(keystroke);
				//remove from our map
				buttonsByHotkey.remove(keystroke);	
			}
			
			// Add the new button and keystroke to our map
			buttonsByHotkey.put(keystroke, macroButton);
			
			// Map the heystroke to the button
			macroButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keystroke,"execute");

			// Tell the hotkey how to execute the button.
			macroButton.getActionMap().put("execute", new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					executeButton(); 
				}
			});	
		
		}
	}	
	
	private void executeButton() {
		macroButton.executeButton();
	}
	
}
