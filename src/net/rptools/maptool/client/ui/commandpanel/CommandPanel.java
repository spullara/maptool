package net.rptools.maptool.client.ui.commandpanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.AppActions;
import net.rptools.maptool.client.AppConstants;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.macro.MacroManager;
import net.rptools.maptool.model.ObservableList;
import net.rptools.maptool.model.TextMessage;

import com.jeta.forms.components.panel.FormPanel;

public class CommandPanel extends JPanel implements Observer {

	private JTextArea commandTextArea;
	private MessagePanel messagePanel;
	private List<String> commandHistory = new LinkedList<String>();
	private int commandHistoryIndex;
	private TextColorWell textColorWell;
	
	private MacroButtonDialog macroButtonDialog = new MacroButtonDialog();
	
	public CommandPanel() {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createLineBorder(Color.gray));
		
		add(BorderLayout.NORTH, createTopPanel());
		add(BorderLayout.SOUTH, createSouthPanel());
		add(BorderLayout.CENTER, getMessagePanel());
	}

	private JComponent createSouthPanel() {
		
		JPanel panel = new JPanel (new BorderLayout());
	
		panel.add(BorderLayout.WEST, createTextPropertiesPanel());
		panel.add(BorderLayout.CENTER, new JScrollPane(getCommandTextArea(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
		
		return panel;
	}
	
	private JComponent createTextPropertiesPanel() {
		JPanel panel = new JPanel();

		panel.add(getTextColorWell());
		
		return panel;
	}
	
	public String getMessageHistory() {
		return messagePanel.getMessagesText();
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(50, 50);
	}

	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}
	
	public JTextArea getCommandTextArea() {
		if (commandTextArea == null) {
			commandTextArea = new JTextArea(){
				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					
					Dimension size = getSize();
					g.setColor(Color.gray);
					g.drawLine(0, 0, size.width, 0);
				}
			};
			commandTextArea.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
			commandTextArea.setRows(3);
			commandTextArea.setWrapStyleWord(true);
			commandTextArea.setLineWrap(true);
			
			ActionMap actions = commandTextArea.getActionMap();
			actions.put(AppActions.COMMIT_COMMAND_ID,
							AppActions.COMMIT_COMMAND);
			actions.put(AppActions.ENTER_COMMAND_ID, AppActions.ENTER_COMMAND);
			actions.put(AppActions.CANCEL_COMMAND_ID, AppActions.CANCEL_COMMAND);
			actions.put(AppActions.COMMAND_UP_ID, new CommandHistoryUpAction());
			actions.put(AppActions.COMMAND_DOWN_ID, new CommandHistoryDownAction());
			
			InputMap inputs = commandTextArea.getInputMap();
			inputs.put(KeyStroke.getKeyStroke("ESCAPE"),
					AppActions.CANCEL_COMMAND_ID);
			inputs.put(KeyStroke.getKeyStroke("ENTER"),
					AppActions.COMMIT_COMMAND_ID);
			inputs.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), AppActions.COMMAND_UP_ID);
			inputs.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), AppActions.COMMAND_DOWN_ID);
		}

		return commandTextArea;
	}

	private JPanel createTopPanel() {
		JPanel panel = new JPanel(new GridBagLayout());

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 1;
		constraints.anchor = GridBagConstraints.WEST;
		
		panel.add(createMacroButtonPanel(), constraints);
		
		constraints.weightx = 0;
		constraints.gridx = 1;
		JLabel spacer = new JLabel();
		spacer.setMinimumSize(new Dimension(20, 10));
		panel.add(spacer, constraints);
		
		constraints.gridx = 2;
		constraints.insets = new Insets(0, 0, 0, 10);
		panel.add(createCloseButton(), constraints);
		
		return panel;
	}
	
	private JPanel createMacroButtonPanel() {
		JPanel panel = new JPanel();
		
		for (int i = 1; i < 40; i++) {
			panel.add(new MacroButton(i, null));
		}
		
		return panel;
	}
	
	private JButton createCloseButton() {
		JButton button = new JButton("X");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MapTool.getFrame().hideCommandPanel();
			}
		});
		
		return button;
	}
	
	/**
	 * Execute the command in the command field.
	 */
	public void commitCommand() {
		String text = commandTextArea.getText().trim();
		if (text.length() == 0) {
			return;
		}

		// Command history
		// Don't store up a bunch of repeats
		if (commandHistory.size() == 0 || !text.equals(commandHistory.get(commandHistory.size()-1))) {
			commandHistory.add(text);
		}
		commandHistoryIndex = commandHistory.size();
		
		if (text.charAt(0) != '/') {
			// Assume a "SAY"
			text = "/s " + text;
		}
		MacroManager.executeMacro(text);
		commandTextArea.setText("");
	}
	
	public void clearMessagePanel() {
		messagePanel.clearMessages();
	}

	/**
	 * Cancel the current command in the command field.
	 */
	public void cancelCommand() {
		commandTextArea.setText("");
		validate();
		
		MapTool.getFrame().hideCommandPanel();
	}
	
	public void startMacro() {
		MapTool.getFrame().showCommandPanel();
		
		commandTextArea.requestFocusInWindow();
		commandTextArea.setText("/");
	}

	public void startChat() {
		MapTool.getFrame().showCommandPanel();
		
		commandTextArea.requestFocusInWindow();
	}

	public TextColorWell getTextColorWell() {
		if (textColorWell == null) {
			textColorWell = new TextColorWell();
		}
		
		return textColorWell;
	}
	
	private class CommandHistoryUpAction extends AbstractAction {
		
		public void actionPerformed(ActionEvent e) {
			if (commandHistory.size() == 0) {
				return;
			}
			commandHistoryIndex --;
			if (commandHistoryIndex < 0) {
				commandHistoryIndex = 0;
			}

			commandTextArea.setText(commandHistory.get(commandHistoryIndex));
		}
	}
	
	private class CommandHistoryDownAction extends AbstractAction {
		
		public void actionPerformed(ActionEvent e) {
			if (commandHistory.size() == 0) {
				return;
			}
			commandHistoryIndex ++;
			if (commandHistoryIndex >= commandHistory.size()) {
				commandTextArea.setText("");
				commandHistoryIndex = commandHistory.size();
			} else {
				commandTextArea.setText(commandHistory.get(commandHistoryIndex));
			}
		}
	}
	
	@Override
	public void requestFocus() {
		commandTextArea.requestFocus();
	}
	
	private MessagePanel getMessagePanel() {
		if (messagePanel == null) {
			messagePanel = new MessagePanel();
			messagePanel.addFocusListener(new FocusAdapter() {
				public void focusGained(java.awt.event.FocusEvent e) {
					commandTextArea.requestFocusInWindow();
				}
			});
		}

		return messagePanel;
	}

	public void addMessage(String message) {
		messagePanel.addMessage(message);
	}
	
	public class MacroButton extends JButton {
		
		private String command;
		private MacroButtonPrefs prefs;
		
		public MacroButton(int index, String command) {
			setCommand(command);
			addMouseListener(new MouseHandler());
			prefs = new MacroButtonPrefs(index, this);
		}
		
		public void setCommand(String command) {
			this.command = command;
			setBackground(command != null ? Color.orange : null);
			
			String tooltip = "Left click to execute, Right click to set, User '/' at the end of command to execute immediately";
			setToolTipText(command != null ? command : tooltip);
		}
		
		private class MouseHandler extends MouseAdapter {
			@Override
			public void mousePressed(MouseEvent e) {

				if (SwingUtilities.isLeftMouseButton(e)) {
					if (command != null) {
						JTextArea commandArea = MapTool.getFrame().getCommandPanel().getCommandTextArea();

						String commandToExecute = command;
						
						boolean commitCommand = command.endsWith("/");
						if (commitCommand) {
							// Strip the execute directive
							commandToExecute = command.substring(0, command.length()-1);
						}
						
						commandArea.setText(commandToExecute);
						commandArea.requestFocusInWindow();
						
						if (commitCommand) {
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
		    
		    public MacroButtonPrefs(int index, MacroButton button) {
		        this.button = button;
		        this.index = index;
		        
		        prefs = Preferences.userRoot().node(AppConstants.APP_NAME + "/macros/" + index);        
		        
		        restorePreferences();
		    }
		    
		    private void restorePreferences() {
		        
		        String label = prefs.get(PREF_LABEL_KEY, Integer.toString(index));
		        String command = prefs.get(PREF_COMMAND_KEY, "");

		        button.command = command;
		        button.setText(label);
		    }
		    
		    public void savePreferences() {
		    	System.out.println("Saving: " + button.command);
		        prefs.put(PREF_LABEL_KEY, button.getText());
		        prefs.put(PREF_COMMAND_KEY, button.command);
		    }
		    
		    ////
		    // PROPERTY CHANGE LISTENER
		    public void propertyChange(PropertyChangeEvent evt) {
		        savePreferences();
		    }
		}
	}
	
	private static class MacroButtonDialog extends JDialog {

		FormPanel panel;
		MacroButton button;
		
		public MacroButtonDialog() {
			super (MapTool.getFrame(), "", true);
			
			panel = new FormPanel("net/rptools/maptool/client/ui/forms/macroButtonDialog.jfrm");
			setContentPane(panel);
			
			installOKButton();
			installCancelButton();
			
			pack();
		}

		private void installOKButton() {
			JButton button = (JButton) panel.getButton("okButton");
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					save();
				}
			});
			getRootPane().setDefaultButton(button);
		}
		
		private void installCancelButton() {
			JButton button = (JButton) panel.getButton("cancelButton");
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					cancel();
				}
			});
		}
		
		@Override
		public void setVisible(boolean b) {
			if (b) {
				SwingUtil.centerOver(this, MapTool.getFrame());
			}
			super.setVisible(b);
		}
		
		public void show(MacroButton button) {
			this.button = button;
			
			getLabelTextField().setText(button.getText());
			getCommandTextArea().setText(button.command);
			
			setVisible(true);
		}
		
		private void save() {
			button.setText(getLabelTextField().getText());
			button.command = getCommandTextArea().getText();
			setVisible(false);
		}
		
		private void cancel() {
			setVisible(false);
		}
		
		private JTextField getLabelTextField() {
			return panel.getTextField("label");
		}
		
		private JTextArea getCommandTextArea() {
			return (JTextArea) panel.getTextComponent("command");
		}
	}
	
	public static class TextColorWell extends JPanel {

		private Color color = Color.black;
		
		public TextColorWell() {
			setMinimumSize(new Dimension(15, 15));
			setPreferredSize(new Dimension(15, 15));
			setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
			
			addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					Color newColor = JColorChooser.showDialog(TextColorWell.this, "Text Color", color);
					if (newColor != null) {
						color = newColor;
						repaint();
					}
				}
			});
		}
	
		public Color getColor() {
			return color;
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			g.setColor(color);
			g.fillRect(0, 0, getSize().width, getSize().height);
		}
	}
	
	////
	// OBSERVER
	public void update(Observable o, Object arg) {
	    ObservableList<TextMessage> textList = MapTool.getMessageList();   
	    ObservableList.Event event = (ObservableList.Event)arg; 
	    switch (event) {
	    case append:
	      addMessage(textList.get(textList.size() - 1).getMessage());
	      break;
	    case add:
	    case remove:
	      //resetMessagePanel();
	      break;
	    case clear:
	      clearMessagePanel();
	      break;
	    default:
	      throw new IllegalArgumentException("Unknown event: " + event);
	    } // endswitch
	}	
	
}
