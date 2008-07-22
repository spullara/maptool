package net.rptools.maptool.client.ui.macrobuttons.buttongroups;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPopupMenu;
import java.awt.event.ActionEvent;

import net.rptools.maptool.client.ui.token.EditTokenMacroDialog;

public class ButtonGroupPopupMenu extends JPopupMenu {
	
	private AbstractButtonGroup buttonGroup;

	public ButtonGroupPopupMenu(AbstractButtonGroup buttonGroup) {
		this.buttonGroup = buttonGroup;

		add(new AddMacroAction());
	}
	
	private class AddMacroAction extends AbstractAction {
		public AddMacroAction() {
			putValue(Action.NAME, "Add New Macro");
		}

		public void actionPerformed(ActionEvent event) {
			/*String key = JOptionPane.showInputDialog("Enter the macro name");
			String command = JOptionPane.showInputDialog("Enter the macro command");

			if (key.equals("") || command.equals("")) {
				return;
			}
			*/
			if (buttonGroup.getToken() != null) {
				// buttongroup belongs to a token
				//Token token = buttonGroup.getToken();
				//token.addMacro(key, command);
				new EditTokenMacroDialog(buttonGroup.getToken()).showDialog();
			} else {
				// buttongroup is the common group
				/*for (Token token : buttonGroup.getTokenList()) {
					token.addMacro(key, command);
				}*/
				new EditTokenMacroDialog(buttonGroup.getTokenList()).showDialog();
			}
			//MapTool.getFrame().updateSelectionPanel();
		}
	}
}
