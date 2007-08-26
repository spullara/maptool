package net.rptools.maptool.client.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import net.rptools.maptool.client.AppConstants;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolUtil;
import net.rptools.maptool.util.GraphicsUtil;

public class MacroButton extends JButton {
	
	private String colorKey;
	private String hotKey;
	private String command;
	private String label;
	private boolean autoExecute;
	private boolean includeLabel;  //include the macro label when printing output?
	private MacroButtonPrefs prefs;
	
	private MacroButtonHotKeyManager hotKeyManager = new MacroButtonHotKeyManager(this);
	
	private static MacroButtonDialog macroButtonDialog = new MacroButtonDialog();

	public MacroButton(int index, String command, boolean autoExecute, boolean includeLabel) {

		setColor("");
		setHotKey(MacroButtonHotKeyManager.HOTKEYS[0]);
		setMacroLabel(Integer.toString(index));
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
	
	public String getMacroLabel() {
		return label;
	}
	public void setMacroLabel(String label) {
		this.label = label;
	}
	
	public String getCommand() {
		return command;
	}
	
	public void setCommand(String command) {
		this.command = command;
	}
	

	public void setColor(String colorKey) {
	
		this.colorKey = colorKey;

		//If the key doesn't correspond to one of our colors, then use the default
		if (!MapToolUtil.isValidColor(colorKey))
			setBackground(null);
		else {
			// because the text is dark, we need to lighten the darker colors
			setBackground(GraphicsUtil.lighter(MapToolUtil.getColor(colorKey)));
		}
	}
	
	public String getColor() {
		if (!MapToolUtil.isValidColor(colorKey))
			return "default";
		else
			return colorKey;
	}
	

	/**
	 *  Get the text for the macro button by filtering out
	 *   label macro (if any), and add hotkey hint (if any)
	 */
	public String getButtonText() {
	
		String buttonLabel;
		final Pattern MACRO_LABEL = Pattern.compile("^(/\\w+\\s+)(.*)$");
		Matcher m = MACRO_LABEL.matcher(label);
		if(m.matches())
			buttonLabel = m.group(2);
		else
			buttonLabel = label;
		
		// if there is no hotkey (HOTKEY[0]) then no need to add hint
		if( hotKey.equals(MacroButtonHotKeyManager.HOTKEYS[0]) )
			return buttonLabel;
		else
			return "<html>" + buttonLabel + "<font size=-3> (" + hotKey + ")</font></html>";
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
			
			JTextPane commandArea = MapTool.getFrame().getCommandPanel().getCommandTextArea();
			String oldText = commandArea.getText();

			if (includeLabel) {
				String commandToExecute = label;
				commandArea.setText(commandToExecute);
		
				MapTool.getFrame().getCommandPanel().commitCommand();
					
			}
			
			String commandsToExecute[] = parseMultiLineCommand(command);
			
			for (int i = 0; i < commandsToExecute.length; i++) {
				// If we aren't auto execute, then append the text instead of replace it
				commandArea.setText((!autoExecute ? oldText + " " : "") + commandsToExecute[i]);
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
	    
	    private static final String PREF_COLOR_KEY = "color";
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
	        
	    	String colorKey = prefs.get(PREF_COLOR_KEY, "");
	        String label = prefs.get(PREF_LABEL_KEY, Integer.toString(index));
	        String command = prefs.get(PREF_COMMAND_KEY, "");
	        boolean autoExecute = prefs.getBoolean(PREF_AUTO_EXECUTE, true);
	        boolean includeLabel = prefs.getBoolean(PREF_INCLUDE_LABEL, false);
	        String hotKey = prefs.get(PREF_HOTKEY_KEY, MacroButtonHotKeyManager.HOTKEYS[0]);
	        
	        button.command = command;
	        button.label = label;
	        button.hotKeyManager.assignKeyStroke(hotKey);
	        button.setColor(colorKey);
	        button.setText(getButtonText());
	        button.setAutoExecute(autoExecute);
	        button.setIncludeLabel(includeLabel);

	    }
	    
	    public void savePreferences() {
	    	prefs.put(PREF_COLOR_KEY, colorKey);
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