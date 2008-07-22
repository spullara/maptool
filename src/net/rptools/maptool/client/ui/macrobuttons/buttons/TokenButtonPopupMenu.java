package net.rptools.maptool.client.ui.macrobuttons.buttons;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import java.awt.event.ActionEvent;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.token.EditTokenMacroDialog;
import net.rptools.maptool.model.Token;

public class TokenButtonPopupMenu extends JPopupMenu {
	
	private final TokenMacroButton button;

	public TokenButtonPopupMenu(TokenMacroButton button, int type) {
		this.button = button;

		if (type == 0) {
			add(new EditMacroAction());
			add(new DuplicateMacroAction());
			add(new JSeparator());
		}
		add(new DeleteMacroAction());
	}

	private class DeleteMacroAction extends AbstractAction {
		public DeleteMacroAction() {
			putValue(Action.NAME, "Delete Macro");
		}

		public void actionPerformed(ActionEvent event) {
			if (button.getToken() != null) {
				Token token = button.getToken();
				// this button belongs to a token
				token.deleteMacro(button.getMacro());
				if (MapTool.getFrame().getCommandPanel().getIdentity().equals(token.getName())) {
					// we are impersonating this token, we have to update the impersonate tab
					MapTool.getFrame().updateImpersonatePanel(token);
				}
			} else {
				// this button is a common macro button
				for (Token token : button.getTokenList()) {
					token.deleteMacro(button.getMacro());
				}
			}
			MapTool.getFrame().updateSelectionPanel();
		}
	}

	private class EditMacroAction extends AbstractAction {
		private EditMacroAction() {
			putValue(Action.NAME, "Edit Macro");
		}

		public void actionPerformed(ActionEvent event) {
			new EditTokenMacroDialog(button).showDialog();
		}
	}

	private class DuplicateMacroAction extends AbstractAction {
		private DuplicateMacroAction() {
			putValue(Action.NAME, "Duplicate Macro");
		}

		public void actionPerformed(ActionEvent event) {
			button.getToken().addMacro("(Copy) " + button.getMacro(), button.getCommand());
			MapTool.getFrame().updateSelectionPanel();
		}
	}
}
