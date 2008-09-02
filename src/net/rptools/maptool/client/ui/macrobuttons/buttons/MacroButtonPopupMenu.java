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
import net.rptools.maptool.client.ui.MacroButtonDialog;
import net.rptools.maptool.client.ui.MacroButtonHotKeyManager;
import net.rptools.maptool.model.Token;

public class MacroButtonPopupMenu extends JPopupMenu{
	
	private final AbstractMacroButton button;
	private int index;
	
	public MacroButtonPopupMenu(AbstractMacroButton parent, int index) {
		this.button = parent;
		this.index = index;
		add(new EditButtonAction());
		add(new JSeparator());
		add(new AddNewButtonAction());
		add(new DuplicateButtonAction());
		add(new JSeparator());
		add(new ResetButtonAction());
		add(new DeleteButtonAction());
		add(new JSeparator());
		add(new RunMacroForEachSelectedTokenAction());
	}

	private class AddNewButtonAction extends AbstractAction {
		public AddNewButtonAction() {
			putValue(Action.NAME, "New");
		}

		public void actionPerformed(ActionEvent event) {
			// TODO: refactor to put tab index from Tab enum
			if (index == 0) {
				MapTool.getFrame().addGlobalMacroButton();
			} else if (index == 1) {
				MapTool.getFrame().addCampaignMacroButton();
			}
		}
	}

	private class EditButtonAction extends AbstractAction {
		public EditButtonAction() {
			putValue(Action.NAME, "Edit");
		}

		public void actionPerformed(ActionEvent event) {
			new MacroButtonDialog().show(button);
			button.savePreferences();
		}
	}

	private class DeleteButtonAction extends AbstractAction {
		public DeleteButtonAction() {
			putValue(Action.NAME, "Delete");
		}

		public void actionPerformed(ActionEvent event) {
			// remove the hot key or the hot key will remain and you'll get an exception later
			// when you want to assign that hotkey to another button.
			button.getHotKeyManager().assignKeyStroke(MacroButtonHotKeyManager.HOTKEYS[0]);
			
			if (index == 0) {
				MacroButtonPrefs.delete((GlobalMacroButton) button);
				MapTool.getFrame().deleteGlobalMacroButton((GlobalMacroButton) button);
			} else if (index == 1) {
				MapTool.getCampaign().deleteMacroButton(button.getProperties());
				MapTool.getFrame().deleteCampaignMacroButton((CampaignMacroButton) button);
			}
		}
	}

	private class DuplicateButtonAction extends AbstractAction {
		public DuplicateButtonAction() {
			putValue(Action.NAME, "Duplicate");
		}

		public void actionPerformed(ActionEvent event) {
			if (index == 0) {
				MapTool.getFrame().addGlobalMacroButton(button.getProperties());
			} else if (index == 1) {
				MapTool.getFrame().addCampaignMacroButton(button.getProperties());
			}
		}
	}

	private class ResetButtonAction extends AbstractAction {
		public ResetButtonAction() {
			putValue(Action.NAME, "Reset");
		}

		public void actionPerformed(ActionEvent event) {
			button.reset();
			button.savePreferences();
		}
	}

	private class RunMacroForEachSelectedTokenAction extends AbstractAction {
		public RunMacroForEachSelectedTokenAction() {
			putValue(Action.NAME, "Run For Each Selected");
		}

		public void actionPerformed(ActionEvent event) {
			for (Token t : MapTool.getFrame().getCurrentZoneRenderer().getSelectedTokensList()) {
				MapTool.getFrame().getCommandPanel().quickCommit(("/im " + t.getId() + ": " + button.getCommand()), true);
			}
			
		}
	}
}