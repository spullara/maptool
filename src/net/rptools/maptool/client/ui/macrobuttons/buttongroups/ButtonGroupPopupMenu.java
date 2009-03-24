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

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import net.rptools.maptool.client.AppUtil;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.macrobuttons.buttons.MacroButtonPrefs;
import net.rptools.maptool.client.ui.macrobuttons.panels.CampaignPanel;
import net.rptools.maptool.client.ui.macrobuttons.panels.GlobalPanel;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.MacroButtonProperties;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.util.PersistenceUtil;

@SuppressWarnings("serial")
public class ButtonGroupPopupMenu extends JPopupMenu {
	
	private AreaGroup areaGroup;
	private String macroGroup;
	private String panelClass;
	private GUID tokenId;
	
	public ButtonGroupPopupMenu(String panelClass, AreaGroup areaGroup, String macroGroup, Token token) {
		this.areaGroup = areaGroup;
		this.macroGroup = macroGroup;
		this.panelClass = panelClass;
		if (token == null){
			this.tokenId=null;
		} else {
			this.tokenId = token.getId();
		}
		if(panelClass.equals("SelectionPanel")) {
			if(areaGroup != null) {
				if(areaGroup.getGroupLabel().equals("Common Macros")) {
					addCommonActions();
				} else {
					addActions();
				}
			}
		} else if(panelClass.equals("CampaignPanel")) {
			addCampaignActions();
		} else {
			addActions();
		}
	}
	
	private void addActions() {
		add(new AddMacroAction());
		add(new JSeparator());
		add(new ImportMacroAction());
		add(new JSeparator());
		add(new ImportMacroSetAction());
		add(new ExportMacroSetAction());
		add(new JSeparator());
		add(new ClearGroupAction());
		if(!this.panelClass.equals("SelectionPanel")) {
			add(new JSeparator());
			add(new ClearPanelAction());
		}
	}

	private void addCommonActions() {
		add(new AddMacroAction("Add New Macro to Selected"));
		add(new JSeparator());
		add(new ImportMacroAction("Import Macro to Selected"));
		add(new JSeparator());
		add(new ImportMacroSetAction("Import Macro Set to Selected"));
		add(new ExportMacroSetAction("Export Common Macro Set"));
	}
	
	private void addCampaignActions() {
		if(MapTool.getPlayer().isGM()) {
			add(new AddMacroAction());
			add(new JSeparator());
			add(new ImportMacroAction());
			add(new JSeparator());
			add(new ImportMacroSetAction());
			add(new ExportMacroSetAction());
			add(new JSeparator());
			add(new ClearGroupAction());
			add(new JSeparator());
			add(new ClearPanelAction());
		}
	}

	private class AddMacroAction extends AbstractAction {
		public AddMacroAction() {
			putValue(Action.NAME, "Add New Macro");
		}

		public AddMacroAction(String name) {
			putValue(Action.NAME, name);
		}

		public void actionPerformed(ActionEvent event) {
			if (panelClass.equals("GlobalPanel")) {
				new MacroButtonProperties(panelClass, MacroButtonPrefs.getNextIndex(), macroGroup);
			} else if (panelClass.equals("CampaignPanel")) {
				new MacroButtonProperties(panelClass, MapTool.getCampaign().getMacroButtonNextIndex(), macroGroup);
			} else if(panelClass.equals("SelectionPanel")) {
				if(areaGroup != null) {
					if(areaGroup.getGroupLabel().equals("Common Macros")) {
						for(Token nextToken : MapTool.getFrame().getCurrentZoneRenderer().getSelectedTokensList()) {
							new MacroButtonProperties(nextToken, nextToken.getMacroNextIndex(), macroGroup);
						}
					} else if(tokenId != null) {
						Token token = MapTool.getFrame().getCurrentZoneRenderer().getZone().getToken(tokenId);
						new MacroButtonProperties(token, token.getMacroNextIndex(), macroGroup);
					}
				}
			} else if (tokenId != null){
				Token token = MapTool.getFrame().getCurrentZoneRenderer().getZone().getToken(tokenId);
				new MacroButtonProperties(token, token.getMacroNextIndex(), macroGroup);
			}
		}
	}
	
	private class ImportMacroAction extends AbstractAction {
			public ImportMacroAction() {
				putValue(Action.NAME, "Import Macro");
			}
			
			public ImportMacroAction(String name) {
				putValue(Action.NAME, name);
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
							Boolean alreadyExists = false;
							if (panelClass.equals("GlobalPanel")) {
								for(MacroButtonProperties nextMacro : MacroButtonPrefs.getButtonProperties()) {
									if(newButtonProps.hashCodeForComparison() == nextMacro.hashCodeForComparison()) {
										alreadyExists = true;
									}
								}
								if(alreadyExists) {
									alreadyExists = confirmImport(newButtonProps, "within the Global panel");
								}
								if(!alreadyExists) {
								new MacroButtonProperties(panelClass, MacroButtonPrefs.getNextIndex(), newButtonProps);
								}
							} else if (panelClass.equals("CampaignPanel")) {
								for(MacroButtonProperties nextMacro : MapTool.getCampaign().getMacroButtonPropertiesArray()) {
									if(newButtonProps.hashCodeForComparison() == nextMacro.hashCodeForComparison()) {
										alreadyExists = true;
									}
								}
								if(alreadyExists) {
									alreadyExists = confirmImport(newButtonProps, "within the Campaign panel");
								}
								if(!alreadyExists) {
								new MacroButtonProperties(panelClass, MapTool.getCampaign().getMacroButtonNextIndex(), newButtonProps);
								}
							} else if(panelClass.equals("SelectionPanel")) {
								if(areaGroup != null) {
									if(areaGroup.getGroupLabel().equals("Common Macros")) {
										for(Token nextToken : MapTool.getFrame().getCurrentZoneRenderer().getSelectedTokensList()) {
											alreadyExists = false;
											for(MacroButtonProperties nextMacro : nextToken.getMacroList(true)) {
												if(newButtonProps.hashCodeForComparison() == nextMacro.hashCodeForComparison()) {
													alreadyExists = true;
												}
											}
											if(alreadyExists) {
												alreadyExists = confirmImport(newButtonProps, "within the common selection macros");
											}
											if(!alreadyExists) {
												new MacroButtonProperties(nextToken, nextToken.getMacroNextIndex(), newButtonProps);
											}
										}
							} else if (tokenId != null){
								Token token = MapTool.getFrame().getCurrentZoneRenderer().getZone().getToken(tokenId);
										for(MacroButtonProperties nextMacro : token.getMacroList(true)) {
											if(newButtonProps.hashCodeForComparison() == nextMacro.hashCodeForComparison()) {
												alreadyExists = true;
											}
										}
										if(alreadyExists) {
											String tokenName = token.getName();
											if(MapTool.getPlayer().isGM()) {
												if(token.getGMName() != null) {
													if(!token.getGMName().equals("")) {
														tokenName = tokenName + "(" + token.getGMName() + ")";
													}
												}
											}
											alreadyExists = confirmImport(newButtonProps, "on " + tokenName);
										}
										if(!alreadyExists) {
								new MacroButtonProperties(token, token.getMacroNextIndex(), newButtonProps);
							}
									}
								}
							} else if (tokenId != null){
								Token token = MapTool.getFrame().getCurrentZoneRenderer().getZone().getToken(tokenId);
								for(MacroButtonProperties nextMacro : token.getMacroList(true)) {
									if(newButtonProps.hashCodeForComparison() == nextMacro.hashCodeForComparison()) {
										alreadyExists = true;
									}
								}
								if(alreadyExists) {
									String tokenName = token.getName();
									if(MapTool.getPlayer().isGM()) {
										if(token.getGMName() != null) {
											if(!token.getGMName().equals("")) {
												tokenName = tokenName + "(" + token.getGMName() + ")";
											}
										}
									}
									alreadyExists = confirmImport(newButtonProps, "on " + tokenName);
								}
								if(!alreadyExists) {
									new MacroButtonProperties(token, token.getMacroNextIndex(), newButtonProps);
								}
							}
						} catch (IOException ioe) {
							ioe.printStackTrace();
							MapTool.showError("Could not load macro: " + ioe);
						}
					}
				});
			}
		}
	
	private class ImportMacroSetAction extends AbstractAction {
		public ImportMacroSetAction() {
			putValue(Action.NAME, "Import Macro Set");
		}
		
		public ImportMacroSetAction(String name) {
			putValue(Action.NAME, name);
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
						Boolean alreadyExists = false;
						for(MacroButtonProperties nextProps : newButtonProps) {
							alreadyExists = false;
							if (panelClass.equals("GlobalPanel")) {
								for(MacroButtonProperties nextMacro : MacroButtonPrefs.getButtonProperties()) {
									if(nextProps.hashCodeForComparison() == nextMacro.hashCodeForComparison()) {
										alreadyExists = true;
									}
								}
								if(alreadyExists) {
									alreadyExists = confirmImport(nextProps, "within the Global panel");
								}
								if(!alreadyExists) {
								new MacroButtonProperties(panelClass, MacroButtonPrefs.getNextIndex(), nextProps);
								}
							} else if (panelClass.equals("CampaignPanel")) {
								for(MacroButtonProperties nextMacro : MapTool.getCampaign().getMacroButtonPropertiesArray()) {
									if(nextProps.hashCodeForComparison() == nextMacro.hashCodeForComparison()) {
										alreadyExists = true;
									}
								}
								if(alreadyExists) {
									alreadyExists = confirmImport(nextProps, "within the Campaign panel");
								}
								if(!alreadyExists) {
								new MacroButtonProperties(panelClass, MapTool.getCampaign().getMacroButtonNextIndex(), nextProps);
								}
							} else if(panelClass.equals("SelectionPanel")) {
								if(areaGroup != null) {
									if(areaGroup.getGroupLabel().equals("Common Macros")) {
										for(Token nextToken : MapTool.getFrame().getCurrentZoneRenderer().getSelectedTokensList()) {
											alreadyExists = false;
											for(MacroButtonProperties nextMacro : nextToken.getMacroList(true)) {
												if(nextProps.hashCodeForComparison() == nextMacro.hashCodeForComparison()) {
													alreadyExists = true;
												}
											}
											if(alreadyExists) {
												alreadyExists = confirmImport(nextProps, "within the common selection macros");
											}
											if(!alreadyExists) {
												new MacroButtonProperties(nextToken, nextToken.getMacroNextIndex(), nextProps);
											}
										}
									} else if(tokenId != null){
										Token token = MapTool.getFrame().getCurrentZoneRenderer().getZone().getToken(tokenId);
										for(MacroButtonProperties nextMacro : token.getMacroList(true)) {
											if(nextProps.hashCodeForComparison() == nextMacro.hashCodeForComparison()) {
												alreadyExists = true;
											}
										}
										if(alreadyExists) {
											String tokenName = token.getName();
											if(MapTool.getPlayer().isGM()) {
												if(token.getGMName() != null) {
													if(!token.getGMName().equals("")) {
														tokenName = tokenName + "(" + token.getGMName() + ")";
													}
												}
											}
											alreadyExists = confirmImport(nextProps, "on " + tokenName);
										}
										if(!alreadyExists) {
											new MacroButtonProperties(token, token.getMacroNextIndex(), nextProps);
										}									
									}
								}
							} else if (tokenId != null){
								Token token = MapTool.getFrame().getCurrentZoneRenderer().getZone().getToken(tokenId);
								for(MacroButtonProperties nextMacro : token.getMacroList(true)) {
									if(nextProps.hashCodeForComparison() == nextMacro.hashCodeForComparison()) {
										alreadyExists = true;
									}
								}
								if(alreadyExists) {
									String tokenName = token.getName();
									if(MapTool.getPlayer().isGM()) {
										if(token.getGMName() != null) {
											if(!token.getGMName().equals("")) {
												tokenName = tokenName + "(" + token.getGMName() + ")";
											}
										}
									}
									alreadyExists = confirmImport(nextProps, "on " + tokenName);
								}
								if(!alreadyExists) {
								new MacroButtonProperties(token, token.getMacroNextIndex(), nextProps);
							}
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
	
	private class ExportMacroSetAction extends AbstractAction {
		public ExportMacroSetAction() {
			putValue(Action.NAME, "Export Macro Set");
		}
		
		public ExportMacroSetAction(String name) {
			putValue(Action.NAME, name);
		}
		
		public void actionPerformed(ActionEvent event) {
			
			JFileChooser chooser = MapTool.getFrame().getSaveMacroSetFileChooser();

			if (chooser.showSaveDialog(MapTool.getFrame()) != JFileChooser.APPROVE_OPTION) {
				return;
			}

			final File selectedFile = chooser.getSelectedFile();
			
			if (selectedFile.exists()) {
			    if (selectedFile.getName().endsWith(".mtmacset")) {
			        if (!MapTool.confirm("Export into macro set file?")) {
			            return;
			        }
			    } else if (!MapTool.confirm("Overwrite existing file?")) {
					return;
				}
			}
			
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						if (panelClass.equals("GlobalPanel")) {
							PersistenceUtil.saveMacroSet(MacroButtonPrefs.getButtonProperties(), selectedFile);
						} else if (panelClass.equals("CampaignPanel")) {
							PersistenceUtil.saveMacroSet(MapTool.getCampaign().getMacroButtonPropertiesArray(), selectedFile);
						} else if(panelClass.equals("SelectionPanel")) {
							if(areaGroup != null) {
								if(areaGroup.getGroupLabel().equals("Common Macros")) {
									Boolean checkComparisons = MapTool.confirm("<html><body>Do you want to " +
											"check comparison criteria for each common macro before adding it " +
											"to the export list?<br><br>If you select \"No\", MapTool will not " +
											"prompt you if any of a particular macro's commonality criteria " +
											"are deselected.  A macro with deselected macro criteria may be " +
											"exported with missing information.  For example, if a macro with " +
											"\"Command\" comparison deselected is added to the export list, " +
											"that macro will be exported without a command.</body></html>");
									List<MacroButtonProperties> commonMacros = MapTool.getFrame().getSelectionPanel().getCommonMacros();
									List<MacroButtonProperties> exportList = new ArrayList<MacroButtonProperties>();
									Boolean trusted = true;
									Boolean allowExport = true;
									for(MacroButtonProperties nextMacro : commonMacros) {
										trusted = true;
										allowExport = true;
										for(Token nextToken : MapTool.getFrame().getCurrentZoneRenderer().getSelectedTokensList()) {
											if(!AppUtil.playerOwns(nextToken)) {
												trusted = false;
											}
											if(nextToken.getMacroList(trusted).size() > 0) {
												for(MacroButtonProperties nextCompMacro : nextToken.getMacroList(trusted)) {
													if(nextCompMacro.hashCodeForComparison() == nextMacro.hashCodeForComparison() 
															&& (!MapTool.getPlayer().isGM() || (!MapTool.getPlayer().isGM() && !nextCompMacro.getAllowPlayerEdits()))) {
														allowExport = false;
													}
												}
											} else {
												allowExport = false;
											}
										}
										if(checkComparisons) {
											if(confirmCommonExport(nextMacro)) {
												if(trusted && allowExport) {
													exportList.add(nextMacro);
												} else {
													MapTool.showWarning("The macro \"" + nextMacro.getLabel() + "\" will not be exported.  " +
															"Either it has been flagged by the GM as not player editable or you do " +
															"not have ownership privileges over the source.");
												}
											} else {
												return;
											}
										} else {
											if(trusted && allowExport) {
												exportList.add(nextMacro);
											} else {
												MapTool.showWarning("The macro \"" + nextMacro.getLabel() + "\" will not be exported.  " +
														"Either it has been flagged by the GM as not player editable or you do " +
														"not have ownership privileges over the source.");
											}
										}
									}
									PersistenceUtil.saveMacroSet(exportList, selectedFile);
						} else if (tokenId != null){
							Token token = MapTool.getFrame().getCurrentZoneRenderer().getZone().getToken(tokenId);
									Boolean trusted = AppUtil.playerOwns(token);
									List<MacroButtonProperties> exportList = new ArrayList<MacroButtonProperties>();
									for(MacroButtonProperties nextMacro : token.getMacroList(trusted)) {
										if(MapTool.getPlayer().isGM() || (!MapTool.getPlayer().isGM() && nextMacro.getAllowPlayerEdits())) {
											exportList.add(nextMacro);
										} else {
											MapTool.showWarning("The macro \"" + nextMacro.getLabel() + "\" will not be exported.  " +
													"Either it has been flagged by the GM as not player editable or you do " +
													"not have ownership privileges over the source.");
										}
									}
									PersistenceUtil.saveMacroSet(exportList, selectedFile);
								}
							}
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
	
	private class ClearGroupAction extends AbstractAction {
		public ClearGroupAction() {
			putValue(Action.NAME, "Clear Group");
		}
		
		public void actionPerformed(ActionEvent event) {			
			if (panelClass.equals("GlobalPanel")) {
				GlobalPanel.deleteButtonGroup(macroGroup);
			} else if (panelClass.equals("CampaignPanel")) {
				CampaignPanel.deleteButtonGroup(macroGroup);
			} else if (tokenId != null){
				MapTool.showInformation("Macro group to be cleared: " + macroGroup);
				MapTool.getFrame().getCurrentZoneRenderer().getZone().getToken(tokenId).deleteMacroGroup(macroGroup, true);				
			}
		}
	}
	
	private class ClearPanelAction extends AbstractAction {
		public ClearPanelAction() {
			putValue(Action.NAME, "Clear Panel");
		}
		
		public void actionPerformed(ActionEvent event) {
			if (panelClass.equals("GlobalPanel")) {
				GlobalPanel.clearPanel();
			} else if (panelClass.equals("CampaignPanel")) {
				CampaignPanel.clearPanel();
			} else if (tokenId != null) {
				MapTool.getFrame().getCurrentZoneRenderer().getZone().getToken(tokenId).deleteAllMacros(true);
			}
		}
	}
	
	private Boolean confirmCommonExport(MacroButtonProperties buttonMacro) {
		Boolean failComparison = false;
		String comparisonResults = "";
		if(!buttonMacro.getCompareGroup()) {
			failComparison = true;
			comparisonResults = comparisonResults + "<li>Group</li>";
		}
		if(!buttonMacro.getCompareSortPrefix()) {
			failComparison = true;
			comparisonResults = comparisonResults + "<li>Sort Prefix</li>";
		}
		if(!buttonMacro.getCompareCommand()) {
			failComparison = true;
			comparisonResults = comparisonResults + "<li>Command</li>";
		}
		if(!buttonMacro.getCompareIncludeLabel()) {
			failComparison = true;
			comparisonResults = comparisonResults + "<li>Include Label</li>";
		}
		if(!buttonMacro.getCompareAutoExecute()) {
			failComparison = true;
			comparisonResults = comparisonResults + "<li>Auto Execute</li>";
		}
		if(!buttonMacro.getApplyToTokens()) {
			failComparison = true;
			comparisonResults = comparisonResults + "<li>Apply to Selected Tokens</li>";
		}
		if(failComparison) {
		failComparison = MapTool.confirm("<html><body>Macro \"" + buttonMacro.getLabel() + 
				"\" has the following comparison criteria deselected: <br><ul>" + 
				comparisonResults + "</ul><br>Do you wish to add this macro to the export " +
				"list?<br><br>Selecting \"Yes\" will result in the macro being exported " +
				"without the information listed.</body></html>");
		}
		return failComparison;
	}
	
	private Boolean confirmImport(MacroButtonProperties importMacro, String location) {
		return !MapTool.confirm("<html><body>The import macro \"" + importMacro.getLabel() + 
				"\" appears to match a macro that already exists " + location + ".  Do you " +
				"wish to import the macro again?<br><br>Select \"Yes\" to import the macro " +
				"anyway.  Select \"No\" to cancel import of the macro.<br><br>If you believe " +
				"the macros are not the same, select \"No\" and verify the commonality " +
				"criteria of the existing macro on the \"Options\" tab of its edit dialog." +
				"</body></html>");
	}
}
