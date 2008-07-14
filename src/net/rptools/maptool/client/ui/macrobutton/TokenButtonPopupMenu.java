package net.rptools.maptool.client.ui.macrobutton;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPopupMenu;
import java.awt.event.ActionEvent;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.Token;

public class TokenButtonPopupMenu extends JPopupMenu {
	
	private final TokenMacroButton button;

	public TokenButtonPopupMenu(TokenMacroButton button) {
		this.button = button;

		add(new DeleteMacroAction());
	}

	private class DeleteMacroAction extends AbstractAction {
		public DeleteMacroAction() {
			putValue(Action.NAME, "Delete");
		}

		public void actionPerformed(ActionEvent event) {
			if (button.getToken() != null) {
				// this button belongs to a token
				button.getToken().deleteMacro(button.getMacro());
			} else {
				// this button is a common macro button
				for (Token token : button.getTokenList()) {
					token.deleteMacro(button.getMacro());
				}
			}
			MapTool.getFrame().getMacroTabbedPane().updateSelectionTab();
		}
	}
}
