package net.rptools.maptool.client.ui.commandpanel;

import java.awt.BorderLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import net.rptools.maptool.client.AppActions;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.macro.MacroManager;
import net.rptools.maptool.model.ObservableList;
import net.rptools.maptool.model.TextMessage;

public class CommandPanel extends JPanel implements Observer {

	private JTextField commandTextField;

	private MessagePanel messagePanel;

	public CommandPanel() {
		setLayout(new BorderLayout());

		add(BorderLayout.SOUTH, getCommandTextField());
		add(BorderLayout.CENTER, new JScrollPane(getMessagePanel(),
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
	}

	public JTextField getCommandTextField() {
		if (commandTextField == null) {
			commandTextField = new JTextField();

			ActionMap actions = commandTextField.getActionMap();
			actions
					.put(AppActions.COMMIT_COMMAND_ID,
							AppActions.COMMIT_COMMAND);
			actions.put(AppActions.ENTER_COMMAND_ID, AppActions.ENTER_COMMAND);

			InputMap inputs = commandTextField.getInputMap();
			inputs.put(KeyStroke.getKeyStroke("ESCAPE"),
					AppActions.CANCEL_COMMAND_ID);
			inputs.put(KeyStroke.getKeyStroke("ENTER"),
					AppActions.COMMIT_COMMAND_ID);
		}

		return commandTextField;
	}

	/**
	 * Execute the command in the command field.
	 */
	public void commitCommand() {
		String text = commandTextField.getText().trim();
		if (text.charAt(0) == '/')
			text = text.substring(1);
		MacroManager.executeMacro(text);
		cancelCommand();
	}

	/**
	 * Cancel the current command in the command field.
	 */
	public void cancelCommand() {
		commandTextField.setText("");
		validate();
		//scrollToEnd();
	}
	
	public void startCommand() {
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
	      //clearMessagePanel();
	      break;
	    default:
	      throw new IllegalArgumentException("Unknown event: " + event);
	    } // endswitch
	}	
}
