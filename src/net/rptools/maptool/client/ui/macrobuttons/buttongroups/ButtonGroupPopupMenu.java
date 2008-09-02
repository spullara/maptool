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
