package net.rptools.maptool.client.ui;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import net.rptools.maptool.client.AppConstants;
import net.rptools.maptool.client.MapTool;

public class MacroButton extends JButton {
	
	private String command;
	private boolean autoExecute;
	private MacroButtonPrefs prefs;
	
	private static MacroButtonDialog macroButtonDialog = new MacroButtonDialog();

	public MacroButton(int index, String command, boolean autoExecute) {
		setCommand(command);
		setAutoExecute(autoExecute);
		addMouseListener(new MouseHandler());
		prefs = new MacroButtonPrefs(index, this);
	}

	public void setAutoExecute(boolean autoExecute) {
		this.autoExecute = autoExecute;
	}
	public boolean getAutoExecute() {
		return autoExecute;
	}
	
	public String getCommand() {
		return command;
	}
	
	public void setCommand(String command) {
		this.command = command;
		setBackground(command != null ? Color.orange : null);
	}
	
	private class MouseHandler extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {

			if (SwingUtilities.isLeftMouseButton(e)) {
				if (command != null) {
					JTextArea commandArea = MapTool.getFrame().getCommandPanel().getCommandTextArea();

					String commandToExecute = command;
					
					commandArea.setText(commandToExecute);
					commandArea.requestFocusInWindow();
					
					if (autoExecute) {
						MapTool.getFrame().getCommandPanel().commitCommand();
					}
				}
			}
			if (SwingUtilities.isRightMouseButton(e)) {
				
				macroButtonDialog.show(MacroButton.this);
				prefs.savePreferences();
			}
		}
	}
	
	// Put this here until we have a better place
	private class MacroButtonPrefs {

		private int index;
		private MacroButton button;
	    private Preferences prefs;
	    
	    private static final String PREF_LABEL_KEY = "label";
	    private static final String PREF_COMMAND_KEY = "command";
	    private static final String PREF_AUTO_EXECUTE = "autoExecute";
	    
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
	        
	        button.command = command;
	        button.setText(label);
	        button.setAutoExecute(autoExecute);
	    }
	    
	    public void savePreferences() {
	        prefs.put(PREF_LABEL_KEY, button.getText());
	        prefs.put(PREF_COMMAND_KEY, button.command);
	        prefs.putBoolean(PREF_AUTO_EXECUTE, button.autoExecute);
	    }
	    
	    ////
	    // PROPERTY CHANGE LISTENER
	    public void propertyChange(PropertyChangeEvent evt) {
	        savePreferences();
	    }
	}
}