package net.rptools.maptool.client.ui.macrobuttons.buttons;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import net.rptools.maptool.client.AppConstants;
import net.rptools.maptool.client.ui.MacroButtonHotKeyManager;
import net.rptools.maptool.model.MacroButtonProperties;

/**
 * Class that is responsible for storing global macro buttons' data in
 * the registry (on Windows that is, dunno where it stores on other platforms, duh.)
 * so it persists across sessions.
 */
public class MacroButtonPrefs {

	private GlobalMacroButton button;
	private Preferences prefs;

	private int index;
	private static int maxIndex = 0;

	private static final String PREF_COLOR_KEY = "color";
	private static final String PREF_LABEL_KEY = "label";
	private static final String PREF_COMMAND_KEY = "command";
	private static final String PREF_AUTO_EXECUTE = "autoExecute";
	private static final String PREF_INCLUDE_LABEL = "includeLabel";
	private static final String PREF_HOTKEY_KEY = "hotKey";

	private static final String FORMAT_STRING = "%010d";

	public MacroButtonPrefs(GlobalMacroButton button) {
		this.button = button;
		this.index = button.getIndex();
		
		// use zero padding to ensure proper ordering in the registry (otherwise 10 will come before 2 etc.)
		String paddedIndex = String.format(FORMAT_STRING, index); 
		
		//prefs = Preferences.userRoot().node(AppConstants.APP_NAME + "/macros/" + paddedIndex);
		prefs = Preferences.userRoot().node(AppConstants.APP_NAME + "/macros");
		
		// if we are creating a new button, it won't have an entry in the registry
		// that's ok if are creating one with default values
		// but if are creating a new one by providing a button properties (like in duplication) 
		// we have to save the properties
		try {
			if (!prefs.nodeExists(paddedIndex)) {
					prefs = prefs.node(paddedIndex);
					savePreferences();
			} else {
				prefs = prefs.node(paddedIndex);
			}
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		
		restorePreferences();
	}

	private void restorePreferences() {

		String colorKey = prefs.get(PREF_COLOR_KEY, "");
		String label = prefs.get(PREF_LABEL_KEY, Integer.toString(index));
		String command = prefs.get(PREF_COMMAND_KEY, "");
		boolean autoExecute = prefs.getBoolean(PREF_AUTO_EXECUTE, true);
		boolean includeLabel = prefs.getBoolean(PREF_INCLUDE_LABEL, false);
		String hotKey = prefs.get(PREF_HOTKEY_KEY, MacroButtonHotKeyManager.HOTKEYS[0]);

		button.setCommand(command);
		button.setMacroLabel(label);
		button.getHotKeyManager().assignKeyStroke(hotKey);
		button.setColor(colorKey);
		button.setText(button.getButtonText());
		button.setAutoExecute(autoExecute);
		button.setIncludeLabel(includeLabel);

	}

	public void savePreferences() {
		prefs.put(PREF_COLOR_KEY, button.getColor());
		prefs.put(PREF_LABEL_KEY, button.getMacroLabel());
		prefs.put(PREF_COMMAND_KEY, button.getCommand());
		prefs.putBoolean(PREF_AUTO_EXECUTE, button.getAutoExecute());
		prefs.putBoolean(PREF_INCLUDE_LABEL, button.getIncludeLabel());
		prefs.put(PREF_HOTKEY_KEY, button.getHotKey());
	}

	////
	// PROPERTY CHANGE LISTENER
	public void propertyChange(PropertyChangeEvent evt) {
		savePreferences();
	}
	
	public static List<MacroButtonProperties> getButtonProperties()
	{
		List<MacroButtonProperties> buttonProperties = new ArrayList<MacroButtonProperties>();
		Preferences prefsRoot = Preferences.userRoot().node(AppConstants.APP_NAME + "/macros");
		
		try {
			for (String buttonNode : prefsRoot.childrenNames()) {
				Preferences buttonPref = prefsRoot.node(buttonNode);

				// not elegant by any definition but works using already existing system
				// the downside is the number of buttons the user can create is limited with Integer.MAX_VALUE
				// after 2.147.. billion button creation the program will explode. if you were to create a new button
				// every second it would take 63 years for this to overflow :)
				// TODO: change this if you like
				int index = Integer.parseInt(buttonNode);
				if (index > maxIndex) {
					maxIndex = index;
				}
				
				if (buttonNode.length() <= 3) {
					// we have an old style button pref. copy it to the new format
					// but check whether we have a new style pref first
					if (!prefsRoot.nodeExists(String.format(FORMAT_STRING, Integer.parseInt(buttonNode)))) {
						// ok, there is no new style pref, it is safe to copy
						// but don't import empty ones (there is no need)
						if (!buttonPref.get(PREF_COMMAND_KEY, "").equals("")) {
							Preferences newPrefs = prefsRoot.node(String.format(FORMAT_STRING, Integer.parseInt(buttonNode)));

							newPrefs.put(PREF_COLOR_KEY, buttonPref.get(PREF_COLOR_KEY, ""));
							newPrefs.put(PREF_LABEL_KEY, buttonPref.get(PREF_LABEL_KEY, Integer.toString(index)));
							newPrefs.put(PREF_COMMAND_KEY, buttonPref.get(PREF_COMMAND_KEY, ""));
							newPrefs.putBoolean(PREF_AUTO_EXECUTE, buttonPref.getBoolean(PREF_AUTO_EXECUTE, true));
							newPrefs.putBoolean(PREF_INCLUDE_LABEL, buttonPref.getBoolean(PREF_INCLUDE_LABEL, true));
							newPrefs.put(PREF_HOTKEY_KEY, buttonPref.get(PREF_HOTKEY_KEY, MacroButtonHotKeyManager.HOTKEYS[0]));

							String colorKey = buttonPref.get(PREF_COLOR_KEY, "");
							String label = buttonPref.get(PREF_LABEL_KEY, Integer.toString(index));
							String command = buttonPref.get(PREF_COMMAND_KEY, "");
							boolean autoExecute = buttonPref.getBoolean(PREF_AUTO_EXECUTE, true);
							boolean includeLabel = buttonPref.getBoolean(PREF_INCLUDE_LABEL, false);
							String hotKey = buttonPref.get(PREF_HOTKEY_KEY, MacroButtonHotKeyManager.HOTKEYS[0]);

							buttonProperties.add(new MacroButtonProperties(index, colorKey, hotKey, command, label, autoExecute, includeLabel));
						}
					}
					
					// set old pref to be removed (regardless it was copied or not)
					buttonPref.removeNode();
					continue;
				}

				String colorKey = buttonPref.get(PREF_COLOR_KEY, "");
				String label = buttonPref.get(PREF_LABEL_KEY, Integer.toString(index));
				String command = buttonPref.get(PREF_COMMAND_KEY, "");
				boolean autoExecute = buttonPref.getBoolean(PREF_AUTO_EXECUTE, true);
				boolean includeLabel = buttonPref.getBoolean(PREF_INCLUDE_LABEL, false);
				String hotKey = buttonPref.get(PREF_HOTKEY_KEY, MacroButtonHotKeyManager.HOTKEYS[0]);

				buttonProperties.add(new MacroButtonProperties(index, colorKey, hotKey, command, label, autoExecute, includeLabel));
			}
		} catch (BackingStoreException e) {
			// exception due to prefsRoot.childrenNames()
			e.printStackTrace();
		}
		
		return buttonProperties;
	}
	
	public static int getNextIndex() {
		return ++maxIndex;
	}
	
	public static void delete(GlobalMacroButton button) {
		int index = button.getIndex();
		String paddedIndex = String.format(FORMAT_STRING, index);
		Preferences prefs = Preferences.userRoot().node(AppConstants.APP_NAME + "/macros/" + paddedIndex);
		try {
			prefs.removeNode();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}
}
