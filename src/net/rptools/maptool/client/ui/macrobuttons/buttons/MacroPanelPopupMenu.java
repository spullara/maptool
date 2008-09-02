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
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import java.awt.event.ActionEvent;

import net.rptools.maptool.client.MapTool;

public class MacroPanelPopupMenu extends JPopupMenu{
	
	//private final JComponent parent;
	private int index;
	
	//TODO: replace index with Tab.TABNAME.index
	public MacroPanelPopupMenu(JComponent parent, int index) {
		//this.parent = parent;
		this.index = index;
		add(new AddNewButtonAction());
	}

	private class AddNewButtonAction extends AbstractAction {
		public AddNewButtonAction() {
			putValue(Action.NAME, "New Button");
		}

		public void actionPerformed(ActionEvent event) {
			// add a new global macro button
			if (index == 0) {
				MapTool.getFrame().addGlobalMacroButton();
			} else if (index == 1) {
				MapTool.getFrame().addCampaignMacroButton();
			}
		}
	}
}
