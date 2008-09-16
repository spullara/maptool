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
package net.rptools.maptool.client.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.AppPreferences;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.walker.WalkerMetric;
import net.rptools.maptool.model.GridFactory;
import net.rptools.maptool.model.Token;

import com.jeta.forms.components.panel.FormPanel;

public class PreferencesDialog extends JDialog {

	// Interactions
	private JCheckBox newMapsHaveFOWCheckBox;
	private JCheckBox tokensStartSnapToGridCheckBox;
	private JCheckBox newMapsVisibleCheckBox;
	private JCheckBox newTokensVisibleCheckBox;
	private JCheckBox tokensStartFreeSizeCheckBox;
	private JCheckBox stampsStartSnapToGridCheckBox;
	private JCheckBox stampsStartFreeSizeCheckBox;
	private JCheckBox backgroundsStartSnapToGridCheckBox;
	private JCheckBox backgroundsStartFreeSizeCheckBox;
	private JComboBox duplicateTokenCombo;
	private JComboBox tokenNamingCombo;
	private JComboBox showNumberingCombo;
	private JComboBox movementMetricCombo;
    
    private JSpinner haloLineWidthSpinner;
    private JSpinner visionOverlayOpacitySpinner;
    private JCheckBox useHaloColorAsVisionOverlayCheckBox;
    private JCheckBox autoRevealVisionOnGMMoveCheckBox;
    private JCheckBox showSmiliesCheckBox;
    private JCheckBox playSystemSoundCheckBox;
    private JCheckBox playSystemSoundOnlyWhenNotFocusedCheckBox;

    private JCheckBox showAvatarInChat;
    
	// Defaults
	private JComboBox defaultGridTypeCombo;
	private JTextField defaultGridSizeTextField;
	private JTextField defaultUnitsPerCellTextField;
	private JTextField defaultVisionDistanceTextField;
	private JTextField statsheetPortraitSize;
	
	private JSpinner autoSaveSpinner;
	private JCheckBox saveReminderCheckBox;
	
	private JCheckBox showDialogOnNewToken;

	// Accessibility
	private JTextField fontSizeTextField;

	public PreferencesDialog() {
		super (MapTool.getFrame(), "Preferences", true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		FormPanel panel = new FormPanel("net/rptools/maptool/client/ui/forms/preferencesDialog.jfrm");

		JButton okButton = (JButton)panel.getButton("okButton");
		getRootPane().setDefaultButton(okButton);
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				setVisible(false);
				dispose();
				MapTool.getEventDispatcher().fireEvent(MapTool.PreferencesEvent.Changed);
			}
		});
		
		showNumberingCombo = panel.getComboBox("showNumberingCombo");
		saveReminderCheckBox = panel.getCheckBox("saveReminderCheckBox");
		autoSaveSpinner = panel.getSpinner("autoSaveSpinner");
		duplicateTokenCombo = panel.getComboBox("duplicateTokenCombo");
		tokenNamingCombo = panel.getComboBox("tokenNamingCombo");
		newMapsHaveFOWCheckBox = panel.getCheckBox("newMapsHaveFOWCheckBox");
		tokensStartSnapToGridCheckBox = panel.getCheckBox("tokensStartSnapToGridCheckBox");
		newMapsVisibleCheckBox = panel.getCheckBox("newMapsVisibleCheckBox");
		newTokensVisibleCheckBox = panel.getCheckBox("newTokensVisibleCheckBox");
		stampsStartFreeSizeCheckBox = panel.getCheckBox("stampsStartFreeSize");
		tokensStartFreeSizeCheckBox = panel.getCheckBox("tokensStartFreeSize");
		stampsStartSnapToGridCheckBox = panel.getCheckBox("stampsStartSnapToGrid");
		backgroundsStartFreeSizeCheckBox = panel.getCheckBox("backgroundsStartFreeSize");
		backgroundsStartSnapToGridCheckBox = panel.getCheckBox("backgroundsStartSnapToGrid");
		defaultGridTypeCombo = panel.getComboBox("defaultGridTypeCombo");
		defaultGridSizeTextField = panel.getTextField("defaultGridSize");
		defaultUnitsPerCellTextField = panel.getTextField("defaultUnitsPerCell");
		defaultVisionDistanceTextField = panel.getTextField("defaultVisionDistance");
		statsheetPortraitSize = panel.getTextField("statsheetPortraitSize");
		fontSizeTextField = panel.getTextField("fontSize");
		haloLineWidthSpinner = panel.getSpinner("haloLineWidthSpinner");
		visionOverlayOpacitySpinner = panel.getSpinner("visionOverlayOpacitySpinner");
		useHaloColorAsVisionOverlayCheckBox = panel.getCheckBox("useHaloColorAsVisionOverlayCheckBox");
		autoRevealVisionOnGMMoveCheckBox = panel.getCheckBox("autoRevealVisionOnGMMoveCheckBox");
		showSmiliesCheckBox = panel.getCheckBox("showSmiliesCheckBox");
		playSystemSoundCheckBox = panel.getCheckBox("playSystemSounds");
		playSystemSoundOnlyWhenNotFocusedCheckBox = panel.getCheckBox("soundsOnlyWhenNotFocused");
		showAvatarInChat = panel.getCheckBox("showChatAvatar");
		showDialogOnNewToken = panel.getCheckBox("showDialogOnNewToken");
		movementMetricCombo = panel.getComboBox("movementMetric");

		setInitialState();

		// And keep it updated
		showAvatarInChat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AppPreferences.setShowAvatarInChat(showAvatarInChat.isSelected());
			}
		});
		saveReminderCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AppPreferences.setSaveReminder(saveReminderCheckBox.isSelected());
			}
		});
		showDialogOnNewToken.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AppPreferences.setShowDialogOnNewToken(showDialogOnNewToken.isSelected());
			}
		});
        autoSaveSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent ce) {
				int newInterval = (Integer)autoSaveSpinner.getValue();
				AppPreferences.setAutoSaveIncrement(newInterval);
				MapTool.getAutoSaveManager().restart();
			}
		});
		newMapsHaveFOWCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AppPreferences.setNewMapsHaveFOW(newMapsHaveFOWCheckBox.isSelected());
			}
		});
		tokensStartSnapToGridCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AppPreferences.setTokensStartSnapToGrid(tokensStartSnapToGridCheckBox.isSelected());
			}
		});
		newMapsVisibleCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AppPreferences.setNewMapsVisible(newMapsVisibleCheckBox.isSelected());
			}
		});
		newTokensVisibleCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AppPreferences.setNewTokensVisible(newTokensVisibleCheckBox.isSelected());
			}
		});
		stampsStartFreeSizeCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AppPreferences.setObjectsStartFreesize(stampsStartFreeSizeCheckBox.isSelected());
			}
		});
		tokensStartFreeSizeCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AppPreferences.setTokensStartFreesize(tokensStartFreeSizeCheckBox.isSelected());
			}
		});
		stampsStartSnapToGridCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AppPreferences.setObjectsStartSnapToGrid(stampsStartSnapToGridCheckBox.isSelected());
			}
		});
		backgroundsStartFreeSizeCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AppPreferences.setBackgroundsStartFreesize(backgroundsStartFreeSizeCheckBox.isSelected());
			}
		});
		backgroundsStartSnapToGridCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AppPreferences.setBackgroundsStartSnapToGrid(backgroundsStartSnapToGridCheckBox.isSelected());
			}
		});
		defaultGridSizeTextField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				updateValue();
			}
			public void insertUpdate(DocumentEvent e) {
				updateValue();
			}
			public void removeUpdate(DocumentEvent e) {
				updateValue();
			}
			
			private void updateValue() {
				try {
					int value = Integer.parseInt(defaultGridSizeTextField.getText());
					AppPreferences.setDefaultGridSize(value);
				} catch (NumberFormatException nfe) {
					// Ignore it
				}
			}
		});
		
		defaultUnitsPerCellTextField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				updateValue();
			}
			public void insertUpdate(DocumentEvent e) {
				updateValue();
			}
			public void removeUpdate(DocumentEvent e) {
				updateValue();
			}
			
			private void updateValue() {
				try {
					int value = Integer.parseInt(defaultUnitsPerCellTextField.getText());
					AppPreferences.setDefaultUnitsPerCell(value);
				} catch (NumberFormatException nfe) {
					// Ignore it
				}
			}
		});
		defaultVisionDistanceTextField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				updateValue();
			}
			public void insertUpdate(DocumentEvent e) {
				updateValue();
			}
			public void removeUpdate(DocumentEvent e) {
				updateValue();
			}
			
			private void updateValue() {
				try {
					int value = Integer.parseInt(defaultVisionDistanceTextField.getText());
					AppPreferences.setDefaultVisionDistance(value);
				} catch (NumberFormatException nfe) {
					// Ignore it
				}
			}
		});
		statsheetPortraitSize.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				updateValue();
			}
			public void insertUpdate(DocumentEvent e) {
				updateValue();
			}
			public void removeUpdate(DocumentEvent e) {
				updateValue();
			}
			
			private void updateValue() {
				try {
					int value = Integer.parseInt(statsheetPortraitSize.getText());
					AppPreferences.setPortraitSize(value);
				} catch (NumberFormatException nfe) {
					// Ignore it
				}
			}
		});
        haloLineWidthSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                AppPreferences.setHaloLineWidth((Integer)haloLineWidthSpinner.getValue());
            }
        });        
        visionOverlayOpacitySpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                AppPreferences.setVisionOverlayOpacity((Integer)visionOverlayOpacitySpinner.getValue());
                MapTool.getFrame().refresh();
            }
        });        
        useHaloColorAsVisionOverlayCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AppPreferences.setUseHaloColorOnVisionOverlay(useHaloColorAsVisionOverlayCheckBox.isSelected());
            }
        });        
        autoRevealVisionOnGMMoveCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AppPreferences.setAutoRevealVisionOnGMMovement(autoRevealVisionOnGMMoveCheckBox.isSelected());
            }
        });
        showSmiliesCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AppPreferences.setShowSmilies(showSmiliesCheckBox.isSelected());
            }
        });
        playSystemSoundCheckBox.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		AppPreferences.setPlaySystemSounds(playSystemSoundCheckBox.isSelected());
        	}
        });
		
        playSystemSoundOnlyWhenNotFocusedCheckBox.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		AppPreferences.setPlaySystemSoundsOnlyWhenNotFocused(playSystemSoundOnlyWhenNotFocusedCheckBox.isSelected());
        	}
        });
		
		fontSizeTextField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				updateValue();
			}
			public void insertUpdate(DocumentEvent e) {
				updateValue();
			}
			public void removeUpdate(DocumentEvent e) {
				updateValue();
			}
			
			private void updateValue() {
				try {
					int value = Integer.parseInt(fontSizeTextField.getText());
					AppPreferences.setFontSize(value);
				} catch (NumberFormatException nfe) {
					// Ignore it
				}
			}
		});
		

		DefaultComboBoxModel gridTypeModel = new DefaultComboBoxModel();
		gridTypeModel.addElement(GridFactory.SQUARE);
		gridTypeModel.addElement(GridFactory.HEX_HORI);
		gridTypeModel.addElement(GridFactory.HEX_VERT);
		gridTypeModel.setSelectedItem(AppPreferences.getDefaultGridType());
		defaultGridTypeCombo.setModel(gridTypeModel);
		defaultGridTypeCombo.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				AppPreferences.setDefaultGridType((String) defaultGridTypeCombo.getSelectedItem());
			}
		});
		
		DefaultComboBoxModel tokenNumModel = new DefaultComboBoxModel();
		tokenNumModel.addElement(Token.NUM_INCREMENT);
		tokenNumModel.addElement(Token.NUM_RANDOM);
		tokenNumModel.setSelectedItem(AppPreferences.getDuplicateTokenNumber());
		duplicateTokenCombo.setModel(tokenNumModel);
		duplicateTokenCombo.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				AppPreferences.setDuplicateTokenNumber((String) duplicateTokenCombo.getSelectedItem());
			}
		});
		
		DefaultComboBoxModel tokenNameModel = new DefaultComboBoxModel();
		tokenNameModel.addElement(Token.NAME_USE_FILENAME);
		tokenNameModel.addElement(Token.NAME_USE_CREATURE);
		tokenNameModel.setSelectedItem(AppPreferences.getNewTokenNaming());
		tokenNamingCombo.setModel(tokenNameModel);
		tokenNamingCombo.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				AppPreferences.setNewTokenNaming((String) tokenNamingCombo.getSelectedItem());
			}
		});
		
		DefaultComboBoxModel showNumModel = new DefaultComboBoxModel();
		showNumModel.addElement(Token.NUM_ON_NAME);
		showNumModel.addElement(Token.NUM_ON_GM);
		showNumModel.addElement(Token.NUM_ON_BOTH);
		showNumModel.setSelectedItem(AppPreferences.getTokenNumberDisplay());
		showNumberingCombo.setModel(showNumModel);
		showNumberingCombo.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				AppPreferences.setTokenNumberDisplay((String) showNumberingCombo.getSelectedItem());
			}
		});
		
		DefaultComboBoxModel movementMetricModel = new DefaultComboBoxModel();
		movementMetricModel.addElement(WalkerMetric.ONE_TWO_ONE);
		movementMetricModel.addElement(WalkerMetric.ONE_ONE_ONE);
		movementMetricModel.addElement(WalkerMetric.MANHATTAN);
		movementMetricModel.addElement(WalkerMetric.NO_DIAGONALS);
		movementMetricModel.setSelectedItem(AppPreferences.getMovementMetric());
		
		movementMetricCombo.setModel(movementMetricModel);
		movementMetricCombo.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				AppPreferences.setMovementMetric((WalkerMetric)movementMetricCombo.getSelectedItem());
			}
		});
		
		add(panel);
		
		pack();
	}
	
	@Override
	public void setVisible(boolean b) {

		if (b) {
			SwingUtil.centerOver(this, MapTool.getFrame());
		}
		super.setVisible(b);
	}

	private void setInitialState() {
		
		showDialogOnNewToken.setSelected(AppPreferences.getShowDialogOnNewToken());
		saveReminderCheckBox.setSelected(AppPreferences.getSaveReminder());
		autoSaveSpinner.setValue(AppPreferences.getAutoSaveIncrement());
		newMapsHaveFOWCheckBox.setSelected(AppPreferences.getNewMapsHaveFOW());
		tokensStartSnapToGridCheckBox.setSelected(AppPreferences.getTokensStartSnapToGrid());
		newMapsVisibleCheckBox.setSelected(AppPreferences.getNewMapsVisible());
		newTokensVisibleCheckBox.setSelected(AppPreferences.getNewTokensVisible());
		stampsStartFreeSizeCheckBox.setSelected(AppPreferences.getObjectsStartFreesize());
		tokensStartFreeSizeCheckBox.setSelected(AppPreferences.getTokensStartFreesize());
		stampsStartSnapToGridCheckBox.setSelected(AppPreferences.getObjectsStartSnapToGrid());
		backgroundsStartFreeSizeCheckBox.setSelected(AppPreferences.getBackgroundsStartFreesize());
		backgroundsStartSnapToGridCheckBox.setSelected(AppPreferences.getBackgroundsStartSnapToGrid());
		defaultGridSizeTextField.setText(Integer.toString(AppPreferences.getDefaultGridSize()));
		defaultUnitsPerCellTextField.setText(Integer.toString(AppPreferences.getDefaultUnitsPerCell()));
		defaultVisionDistanceTextField.setText(Integer.toString(AppPreferences.getDefaultVisionDistance()));
		statsheetPortraitSize.setText(Integer.toString(AppPreferences.getPortraitSize()));
		fontSizeTextField.setText(Integer.toString(AppPreferences.getFontSize()));
		haloLineWidthSpinner.setValue(AppPreferences.getHaloLineWidth());
		visionOverlayOpacitySpinner.setValue(AppPreferences.getVisionOverlayOpacity());
		useHaloColorAsVisionOverlayCheckBox.setSelected(AppPreferences.getUseHaloColorOnVisionOverlay());
		autoRevealVisionOnGMMoveCheckBox.setSelected(AppPreferences.getAutoRevealVisionOnGMMovement());
		showSmiliesCheckBox.setSelected(AppPreferences.getShowSmilies());
		playSystemSoundCheckBox.setSelected(AppPreferences.getPlaySystemSounds());
		playSystemSoundOnlyWhenNotFocusedCheckBox.setSelected(AppPreferences.getPlaySystemSoundsOnlyWhenNotFocused());
		showAvatarInChat.setSelected(AppPreferences.getShowAvatarInChat());
	}
}
