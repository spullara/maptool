package net.rptools.maptool.client.ui.macrobuttonpanel;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import java.awt.event.ActionEvent;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.Token;

public class ButtonGroupPopupMenu extends JPopupMenu {
	
	private ButtonGroup buttonGroup;

	public ButtonGroupPopupMenu(ButtonGroup buttonGroup) {
		this.buttonGroup = buttonGroup;

		add(new AddMacroAction());
	}
	
	private class AddMacroAction extends AbstractAction {
		public AddMacroAction() {
			putValue(Action.NAME, "Add");
		}

		public void actionPerformed(ActionEvent event) {
			String key = JOptionPane.showInputDialog("Enter the macro name");
			String command = JOptionPane.showInputDialog("Enter the macro command");
			
			if (key.equals("") || command.equals("")) {
				return;
			}
			
			if (buttonGroup.getToken() != null) {
				// buttongroup belongs to a token
				buttonGroup.getToken().addMacro(key, command);
			} else {
				// buttongroup is the common group
				for (Token token : buttonGroup.getTokenList()) {
					token.addMacro(key, command);
				}
			}
			MapTool.getFrame().getMacroTabbedPane().updateSelectionTab();
		}
	}
}
