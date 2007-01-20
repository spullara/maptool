package net.rptools.maptool.client.ui.commandpanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.border.BevelBorder;

import net.rptools.maptool.client.AppActions;
import net.rptools.maptool.client.AppListeners;
import net.rptools.maptool.client.AppPreferences;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.PreferencesListener;
import net.rptools.maptool.client.macro.MacroManager;
import net.rptools.maptool.model.ObservableList;
import net.rptools.maptool.model.TextMessage;

public class CommandPanel extends JPanel implements Observer {

	private JTextArea commandTextArea;
	private MessagePanel messagePanel;
	private List<String> commandHistory = new LinkedList<String>();
	private int commandHistoryIndex;
	private TextColorWell textColorWell;
	
	public CommandPanel() {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createLineBorder(Color.gray));
		
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
			commandTextArea.setFont(new Font("helvetica", 0, AppPreferences.getFontSize()));
			
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
			
			// Resize on demand
			AppListeners.addPreferencesListener(new PreferencesListener() {
				public void preferencesUpdated() {
					commandTextArea.setFont(commandTextArea.getFont().deriveFont((float)AppPreferences.getFontSize()));
					doLayout();
				}
			});
		}

		return commandTextArea;
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
			if (commandHistoryIndex == commandHistory.size()) {
				commandTextArea.setText("");
				commandHistoryIndex = commandHistory.size();
			} else if (commandHistoryIndex >= commandHistory.size()) {
				commandHistoryIndex--;
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
			
			// Update whenever the preferences change
			AppListeners.addPreferencesListener(new PreferencesListener(){
				public void preferencesUpdated() {
					messagePanel.refreshRenderer();
				}
			});
		}

		return messagePanel;
	}

	public void addMessage(String message) {
		messagePanel.addMessage(message);
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
