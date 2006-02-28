package net.rptools.maptool.client.ui.commandpanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.Timer;

import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.AppActions;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.macro.MacroManager;
import net.rptools.maptool.model.ObservableList;
import net.rptools.maptool.model.TextMessage;

public class CommandPanel extends JPanel implements Observer, MouseListener, MouseMotionListener {

	private JTextField commandTextField;
	private boolean mouseIsOver;
	private MessagePanel messagePanel;
	private Timer closeTimer;
	private List<String> commandHistory = new LinkedList<String>();
	private int commandHistoryIndex;
	private JCheckBox stickyCheckBox = new JCheckBox();
	
	public CommandPanel() {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createLineBorder(Color.gray));
		
		add(BorderLayout.NORTH, createTopPanel());
		add(BorderLayout.SOUTH, getCommandTextField());
		add(BorderLayout.CENTER, getMessagePanel());
		
		SwingUtil.addMouseListenerToHierarchy(this, this);
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
	
	public JTextField getCommandTextField() {
		if (commandTextField == null) {
			commandTextField = new JTextField(){
				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					
					Dimension size = getSize();
					g.setColor(Color.gray);
					g.drawLine(0, 0, size.width, 0);
				}
			};
			commandTextField.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

			ActionMap actions = commandTextField.getActionMap();
			actions.put(AppActions.COMMIT_COMMAND_ID,
							AppActions.COMMIT_COMMAND);
			actions.put(AppActions.ENTER_COMMAND_ID, AppActions.ENTER_COMMAND);
			actions.put(AppActions.CANCEL_COMMAND_ID, AppActions.CANCEL_COMMAND);
			actions.put(AppActions.COMMAND_UP_ID, new CommandHistoryUpAction());
			actions.put(AppActions.COMMAND_DOWN_ID, new CommandHistoryDownAction());
			
			InputMap inputs = commandTextField.getInputMap();
			inputs.put(KeyStroke.getKeyStroke("ESCAPE"),
					AppActions.CANCEL_COMMAND_ID);
			inputs.put(KeyStroke.getKeyStroke("ENTER"),
					AppActions.COMMIT_COMMAND_ID);
			inputs.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), AppActions.COMMAND_UP_ID);
			inputs.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), AppActions.COMMAND_DOWN_ID);
		}

		return commandTextField;
	}

	private JPanel createTopPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 2)){
			@Override
			protected void paintComponent(Graphics g) {
				g.setColor(Color.gray);
				
				Dimension size = getSize();
				g.drawLine(0, size.height-1, size.width, size.height-1);
			}
		};
		
		panel.add(stickyCheckBox);
		
		return panel;
	}
	
	/**
	 * Execute the command in the command field.
	 */
	public void commitCommand() {
		String text = commandTextField.getText().trim();
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
		cancelCommand();
	}
	
	public void clearMessagePanel() {
		messagePanel.clearMessages();
	}

	/**
	 * Cancel the current command in the command field.
	 */
	public void cancelCommand() {
		commandTextField.setText("");
		validate();
		
		if (!mouseIsOver) {
			MapTool.getFrame().hideCommandPanel();
		}
	}
	
	public void startMacro() {
		MapTool.getFrame().showCommandPanel();
		
		commandTextField.requestFocus();
		commandTextField.setText("/");
	}

	public void startChat() {
		MapTool.getFrame().showCommandPanel();
		
		commandTextField.requestFocus();
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

			commandTextField.setText(commandHistory.get(commandHistoryIndex));
		}
	}
	
	private class CommandHistoryDownAction extends AbstractAction {
		
		public void actionPerformed(ActionEvent e) {
			if (commandHistory.size() == 0) {
				return;
			}
			commandHistoryIndex ++;
			if (commandHistoryIndex >= commandHistory.size()) {
				commandTextField.setText("");
				commandHistoryIndex = commandHistory.size();
			} else {
				commandTextField.setText(commandHistory.get(commandHistoryIndex));
			}
		}
	}
	
	@Override
	public void requestFocus() {
		commandTextField.requestFocus();
	}
	
	private MessagePanel getMessagePanel() {
		if (messagePanel == null) {
			messagePanel = new MessagePanel();
		}

		return messagePanel;
	}

	public void addMessage(String message) {
		messagePanel.addMessage(message);
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
	
	////
	// MOUSE LISTENER
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {
		mouseIsOver = true;
	}
	public void mouseExited(MouseEvent e) {
		mouseIsOver = false;

		if (stickyCheckBox.isSelected()) {
			return;
		}
		
		if (closeTimer != null) {
			closeTimer.stop();
		}
		closeTimer = new Timer(500, new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if (!mouseIsOver && getCommandTextField().getText().length() == 0) {
					cancelCommand();
				}
				
				if (closeTimer != null) {
					closeTimer.stop();
					closeTimer = null;
				}
			}
		});
		closeTimer.start();
	}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	
	////
	// MOUSE MOTION LISTENER
	public void mouseDragged(MouseEvent e) {}
	public void mouseMoved(MouseEvent e) {
		mouseIsOver = true;
	}
}
