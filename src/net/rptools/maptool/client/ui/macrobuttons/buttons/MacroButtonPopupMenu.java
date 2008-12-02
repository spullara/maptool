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
import javax.swing.JFileChooser;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.MacroButtonDialog;
import net.rptools.maptool.model.MacroButtonProperties;
import net.rptools.maptool.util.PersistenceUtil;

@SuppressWarnings("serial")
public class MacroButtonPopupMenu extends JPopupMenu{
	
	private final MacroButton button;
	private final String panelClass;
	
	public MacroButtonPopupMenu(MacroButton parent, String panelClass) {
		this.button = parent;
		this.panelClass = panelClass;
		add(new EditButtonAction());
		add(new JSeparator());
		add(new AddNewButtonAction());
		add(new DuplicateButtonAction());
		add(new JSeparator());
		add(new ResetButtonAction());
		add(new DeleteButtonAction());
		add(new JSeparator());
		add(new ExportMacroAction());
		add(new JSeparator());
		add(new RunMacroForEachSelectedTokenAction());
	}

	private class AddNewButtonAction extends AbstractAction {
		public AddNewButtonAction() {
			putValue(Action.NAME, "New");
		}

		public void actionPerformed(ActionEvent event) {
			// TODO: refactor to put tab index from Tab enum
			if (panelClass.equals("GlobalPanel")) {
				new MacroButtonProperties(panelClass, MacroButtonPrefs.getNextIndex(), button.getProperties().getGroup());
			} else if (panelClass.equals("CampaignPanel")) {
				new MacroButtonProperties(panelClass, MapTool.getCampaign().getMacroButtonNextIndex(), button.getProperties().getGroup());
			} else if (button.getToken()!= null){
				new MacroButtonProperties(button.getToken(), button.getToken().getMacroNextIndex(), button.getProperties().getGroup());
			}
		}
	}

	private class EditButtonAction extends AbstractAction {
		public EditButtonAction() {
			putValue(Action.NAME, "Edit");
		}

		public void actionPerformed(ActionEvent event) {
			if (button.getToken()== null){
				new MacroButtonDialog(false).show(button);
			} else {
				new MacroButtonDialog(true).show(button);
			}
		}
	}

	private class DeleteButtonAction extends AbstractAction {
		public DeleteButtonAction() {
			putValue(Action.NAME, "Delete");
		}

		public void actionPerformed(ActionEvent event) {
			// remove the hot key or the hot key will remain and you'll get an exception later
			// when you want to assign that hotkey to another button.
			button.clearHotkey();
			
			if (panelClass.equals("GlobalPanel")) {
				MacroButtonPrefs.delete(button.getProperties());
			} else if (panelClass.equals("CampaignPanel")) {
				MapTool.getCampaign().deleteMacroButton(button.getProperties());
			} else if (button.getToken()!= null){
				button.getToken().deleteMacroButtonProperty(button.getProperties());
			}
		}
	}

	private class DuplicateButtonAction extends AbstractAction {
		public DuplicateButtonAction() {
			putValue(Action.NAME, "Duplicate");
		}

		public void actionPerformed(ActionEvent event) {
			if (panelClass.equals("GlobalPanel")) {
				new MacroButtonProperties(panelClass, MacroButtonPrefs.getNextIndex(), button.getProperties());
			} else if (panelClass.equals("CampaignPanel")) {
				new MacroButtonProperties(panelClass, MapTool.getCampaign().getMacroButtonNextIndex(), button.getProperties());
			} else if (button.getToken() != null){
				new MacroButtonProperties(button.getToken(), button.getToken().getMacroNextIndex(), button.getProperties());
			}
		}
	}

	private class ResetButtonAction extends AbstractAction {
		public ResetButtonAction() {
			putValue(Action.NAME, "Reset");
		}

		public void actionPerformed(ActionEvent event) {
			button.getProperties().reset();
			button.getProperties().save();
		}
	}

	private class RunMacroForEachSelectedTokenAction extends AbstractAction {
		public RunMacroForEachSelectedTokenAction() {
			putValue(Action.NAME, "Run For Each Selected");
		}

		public void actionPerformed(ActionEvent event) {
			button.getProperties().executeMacro(true);
		}
	}
	
	private class ExportMacroAction extends AbstractAction {
			private ExportMacroAction() {
				putValue(Action.NAME, "Export Macro");
			}
			
			public void actionPerformed(ActionEvent event) {
				JFileChooser chooser = MapTool.getFrame().getSaveMacroFileChooser();
				
				if (chooser.showSaveDialog(MapTool.getFrame()) != JFileChooser.APPROVE_OPTION) {
					return;
				}
				
				final File selectedFile = chooser.getSelectedFile();
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						if (selectedFile.exists()) {
						    if (selectedFile.getName().endsWith(".mtmacro")) {
						        if (!MapTool.confirm("Export into macro file?")) {
						            return;
						        }
						    } else if (!MapTool.confirm("Overwrite existing file?")) {
								return;
							}
						}
						
						try {
							PersistenceUtil.saveMacro(button.getProperties(), selectedFile);				
							MapTool.showInformation("Macro Saved.");
						} catch (IOException ioe) {
							ioe.printStackTrace();
							MapTool.showError("Could not save macro: " + ioe);
						}
					}
				});				
			}
	}
}