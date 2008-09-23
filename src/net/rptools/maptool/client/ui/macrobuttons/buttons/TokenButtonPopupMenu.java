/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
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
	    		MapTool.serverCommand().putToken(
	    				MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(), button.getToken());

				/* TODO Remove if (MapTool.getFrame().getCommandPanel().getIdentity().equals(token.getName())) {
					// we are impersonating this token, we have to update the impersonate tab
					MapTool.getFrame().updateImpersonatePanel(token);
				}*/
			} else {
				// this button is a common macro button
				for (Token token : button.getTokenList()) {
					token.deleteMacro(button.getMacro());
		    		MapTool.serverCommand().putToken(
		    				MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(), button.getToken());

				}
			}
			// TODO Remove MapTool.getFrame().updateSelectionPanel();
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
			// TODO Remove MapTool.getFrame().updateSelectionPanel();
    		MapTool.serverCommand().putToken(
    				MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(), button.getToken());
		}
	}
}
