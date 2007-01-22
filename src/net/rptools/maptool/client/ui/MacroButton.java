package net.rptools.maptool.client.ui;


import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.JComponent;

import net.rptools.maptool.client.AppConstants;
import net.rptools.maptool.client.MapTool;

public class MacroButton extends JButton {
	
	private String hotKey;
	private String command;
	private String label;
	private boolean autoExecute;
	private boolean includeLabel;  //include the macro label when printing output?
	private MacroButtonPrefs prefs;
	
	private MacroButtonHotKeyManager hotKeyManager = new MacroButtonHotKeyManager(this);
	
	private static MacroButtonDialog macroButtonDialog = new MacroButtonDialog();

	public MacroButton(int index, String command, boolean autoExecute, boolean includeLabel) {
		
		setHotKey(MacroButtonHotKeyManager.HOTKEYS[0]);
		setLabel(Integer.toString(index));
		setCommand(command);
		setAutoExecute(autoExecute);
		setIncludeLabel(includeLabel);
		addMouseListener(new MouseHandler());
		prefs = new MacroButtonPrefs(index, this);
		
	}

	public void setAutoExecute(boolean autoExecute) {
		this.autoExecute = autoExecute;
	}
	public boolean getAutoExecute() {
		return autoExecute;
	}
	
	public void setHotKey(String hotKey) {
		this.hotKey = hotKey;
	}
	
	public String getHotKey() {
		return hotKey;
	}
	
	public MacroButtonHotKeyManager getHotKeyManager() {
		return hotKeyManager;
	}
	
	public void setIncludeLabel(boolean includeLabel) {
		this.includeLabel = includeLabel;
	}
	public boolean getIncludeLabel() {
		return includeLabel;
	}	
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
		setBackground(command != null ? Color.orange : null);
	}
	
	public String getButtonText() {
		
		if( hotKey.equals(MacroButtonHotKeyManager.HOTKEYS[0]) )
			return label;
		else
			return label + " (" + hotKey + ")";
	}
	
	
	private class MouseHandler extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {

			if (SwingUtilities.isLeftMouseButton(e)) {
				executeButton();
			}
			if (SwingUtilities.isRightMouseButton(e)) {	
				macroButtonDialog.show(MacroButton.this);
				prefs.savePreferences();
			}
		}
	}
	
	
	public void executeButton() {
		
		if (command != null) {
			
			JTextArea commandArea = MapTool.getFrame().getCommandPanel().getCommandTextArea();

			if (includeLabel) {
				String commandToExecute = "/th " + label;
				commandArea.setText(commandToExecute);
		
				MapTool.getFrame().getCommandPanel().commitCommand();
					
			}
			
			String commandsToExecute[] = parseMultiLineCommand(command);
			
			for (int i = 0; i < commandsToExecute.length; i++) {
				commandArea.setText(commandsToExecute[i]);
				if (autoExecute) {	
					MapTool.getFrame().getCommandPanel().commitCommand();
				}
			}
			
			commandArea.requestFocusInWindow();
					
		}
	}
	
	
	private String[] parseMultiLineCommand(String multiLineCommand) {
		    
		// lookahead for new macro "/" after "\n" to prevent uneccessary splitting.
		String pattern = "\n(?=/)";
		String[] parsedCommand = multiLineCommand.split(pattern);
		
		return parsedCommand;
	}
	
	// Put this here until we have a better place
	private class MacroButtonPrefs {

		private int index;
		private MacroButton button;
	    private Preferences prefs;
	    
	    private static final String PREF_LABEL_KEY = "label";
	    private static final String PREF_COMMAND_KEY = "command";
	    private static final String PREF_AUTO_EXECUTE = "autoExecute";
	    private static final String PREF_INCLUDE_LABEL = "includeLabel";
	    private static final String PREF_HOTKEY_KEY = "hotKey";
	    
	    public MacroButtonPrefs(int index, MacroButton button) {
	        this.button = button;
	        this.index = index;
	        
	        prefs = Preferences.userRoot().node(AppConstants.APP_NAME + "/macros/" + index);        
	        
	        restorePreferences();
	    }
	    
	    private void restorePreferences() {
	        
	        String label = prefs.get(PREF_LABEL_KEY, Integer.toString(index));
	        String command = prefs.get(PREF_COMMAND_KEY, "");
	        boolean autoExecute = prefs.getBoolean(PREF_AUTO_EXECUTE, true);
	        boolean includeLabel = prefs.getBoolean(PREF_INCLUDE_LABEL, false);
	        String hotKey = prefs.get(PREF_HOTKEY_KEY, MacroButtonHotKeyManager.HOTKEYS[0]);
	        
	        button.command = command;
	        button.label = label;
	        button.hotKeyManager.assignKeyStroke(hotKey);
	        button.setText(getButtonText());
	        button.setAutoExecute(autoExecute);
	        button.setIncludeLabel(includeLabel);

	    }
	    
	    public void savePreferences() {
	        prefs.put(PREF_LABEL_KEY, button.label);
	        prefs.put(PREF_COMMAND_KEY, button.command);
	        prefs.putBoolean(PREF_AUTO_EXECUTE, button.autoExecute);
	        prefs.putBoolean(PREF_INCLUDE_LABEL, button.includeLabel);
	        prefs.put(PREF_HOTKEY_KEY, button.hotKey);
	    }
	    
	    ////
	    // PROPERTY CHANGE LISTENER
	    public void propertyChange(PropertyChangeEvent evt) {
	        savePreferences();
	    }
	}
}