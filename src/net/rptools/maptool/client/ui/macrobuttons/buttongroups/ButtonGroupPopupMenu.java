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
import javax.swing.JFileChooser;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.macrobuttons.buttons.MacroButtonPrefs;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.MacroButtonProperties;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.util.PersistenceUtil;

public class ButtonGroupPopupMenu extends JPopupMenu {
	
	private String macroGroup;
	private String panelClass;
	private GUID tokenId;
	
	public ButtonGroupPopupMenu(String panelClass, String macroGroup, Token token) {
		this.macroGroup = macroGroup;
		this.panelClass = panelClass;
		if (token == null){
			this.tokenId=null;
		} else {
			this.tokenId = token.getId();
		}
		add(new AddMacroAction());
		add(new JSeparator());
		add(new ImportMacroAction());
		add(new JSeparator());
		add(new ImportMacroSetAction());
		add(new ExportMacroSetAction());
	}

	private class AddMacroAction extends AbstractAction {
		public AddMacroAction() {
			putValue(Action.NAME, "Add New Macro");
		}

		public void actionPerformed(ActionEvent event) {
			if (panelClass.equals("GlobalPanel")) {
				new MacroButtonProperties(panelClass, MacroButtonPrefs.getNextIndex(), macroGroup);
			} else if (panelClass.equals("CampaignPanel")) {
				new MacroButtonProperties(panelClass, MapTool.getCampaign().getMacroButtonNextIndex(), macroGroup);
			} else if (tokenId != null){
				Token token = MapTool.getFrame().getCurrentZoneRenderer().getZone().getToken(tokenId);
				new MacroButtonProperties(token, token.getMacroNextIndex(), macroGroup);
			}
		}
	}
	
	@SuppressWarnings("serial")
	private class ImportMacroAction extends AbstractAction {
			public ImportMacroAction() {
				putValue(Action.NAME, "Import Macro");
			}
			
			public void actionPerformed(ActionEvent event) {
				
				JFileChooser chooser = MapTool.getFrame().getLoadMacroFileChooser();
	
				if (chooser.showOpenDialog(MapTool.getFrame()) != JFileChooser.APPROVE_OPTION) {
					return;
				}
	
				final File selectedFile = chooser.getSelectedFile();
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							MacroButtonProperties newButtonProps = PersistenceUtil.loadMacro(selectedFile);
							if (panelClass.equals("GlobalPanel")) {
								new MacroButtonProperties(panelClass, MacroButtonPrefs.getNextIndex(), newButtonProps);
							} else if (panelClass.equals("CampaignPanel")) {
								new MacroButtonProperties(panelClass, MapTool.getCampaign().getMacroButtonNextIndex(), newButtonProps);
							} else if (tokenId != null){
								Token token = MapTool.getFrame().getCurrentZoneRenderer().getZone().getToken(tokenId);
								new MacroButtonProperties(token, token.getMacroNextIndex(), newButtonProps);
							}
						} catch (IOException ioe) {
							ioe.printStackTrace();
							MapTool.showError("Could not load macro: " + ioe);
						}
					}
				});
			}
		}
	
	@SuppressWarnings("serial")
	private class ImportMacroSetAction extends AbstractAction {
		public ImportMacroSetAction() {
			putValue(Action.NAME, "Import Macro Set");
		}
		
		public void actionPerformed(ActionEvent event) {
			
			JFileChooser chooser = MapTool.getFrame().getLoadMacroSetFileChooser();

			if (chooser.showOpenDialog(MapTool.getFrame()) != JFileChooser.APPROVE_OPTION) {
				return;
			}

			final File selectedFile = chooser.getSelectedFile();
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						List<MacroButtonProperties> newButtonProps = PersistenceUtil.loadMacroSet(selectedFile);
						for(MacroButtonProperties nextProps : newButtonProps) {
							if (panelClass.equals("GlobalPanel")) {
								new MacroButtonProperties(panelClass, MacroButtonPrefs.getNextIndex(), nextProps);
							} else if (panelClass.equals("CampaignPanel")) {
								new MacroButtonProperties(panelClass, MapTool.getCampaign().getMacroButtonNextIndex(), nextProps);
							} else if (tokenId != null){
								Token token = MapTool.getFrame().getCurrentZoneRenderer().getZone().getToken(tokenId);
								new MacroButtonProperties(token, token.getMacroNextIndex(), nextProps);
							}
						}
					} catch (IOException ioe) {
						ioe.printStackTrace();
						MapTool.showError("Could not load macro set: " + ioe);
					}
				}
			});
		}
	}
	
	@SuppressWarnings("serial")
	private class ExportMacroSetAction extends AbstractAction {
		public ExportMacroSetAction() {
			putValue(Action.NAME, "Export Macro Set");
		}
		
		public void actionPerformed(ActionEvent event) {
			
			JFileChooser chooser = MapTool.getFrame().getSaveMacroSetFileChooser();

			if (chooser.showOpenDialog(MapTool.getFrame()) != JFileChooser.APPROVE_OPTION) {
				return;
			}

			final File selectedFile = chooser.getSelectedFile();
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						if (panelClass.equals("GlobalPanel")) {
							PersistenceUtil.saveMacroSet(MacroButtonPrefs.getButtonProperties(), selectedFile);
						} else if (panelClass.equals("CampaignPanel")) {
							PersistenceUtil.saveMacroSet(MapTool.getCampaign().getMacroButtonPropertiesArray(), selectedFile);
						} else if (tokenId != null){
							Token token = MapTool.getFrame().getCurrentZoneRenderer().getZone().getToken(tokenId);
							PersistenceUtil.saveMacroSet(token.getMacroList(true), selectedFile);
						}						
					} catch (IOException ioe) {
						ioe.printStackTrace();
						MapTool.showError("Could not save macro set: " + ioe);
					}
				}
			});
		}
	}
}
