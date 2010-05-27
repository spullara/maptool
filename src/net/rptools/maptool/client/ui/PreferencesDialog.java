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
import java.text.ParseException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.AppPreferences;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.walker.WalkerMetric;
import net.rptools.maptool.model.Grid;
import net.rptools.maptool.model.GridFactory;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.util.StringUtil;

import com.jeta.forms.components.colors.JETAColorWell;
import com.jeta.forms.components.panel.FormPanel;

public class PreferencesDialog extends JDialog {

	/**
	 * @author frank
	 */
	private abstract class OpacityProxy implements ChangeListener {
		public void stateChanged(ChangeEvent ce) {
			JSpinner sp = (JSpinner) ce.getSource();
			int value = (Integer) sp.getValue();
			storeSpinnerValue( value );
			MapTool.getFrame().refresh();
		}
		protected abstract void storeSpinnerValue(int value);
	}

	// Interactions
	private final JCheckBox newMapsHaveFOWCheckBox;
	private final JCheckBox tokensPopupWarningWhenDeletedCheckBox;
	private final JCheckBox tokensStartSnapToGridCheckBox;
	private final JCheckBox newMapsVisibleCheckBox;
	private final JCheckBox newTokensVisibleCheckBox;
	private final JCheckBox tokensStartFreeSizeCheckBox;
	private final JCheckBox stampsStartSnapToGridCheckBox;
	private final JCheckBox stampsStartFreeSizeCheckBox;
	private final JCheckBox backgroundsStartSnapToGridCheckBox;
	private final JCheckBox backgroundsStartFreeSizeCheckBox;
	private final JComboBox duplicateTokenCombo;
	private final JComboBox tokenNamingCombo;
	private final JComboBox showNumberingCombo;
	private final JComboBox movementMetricCombo;
	private final JCheckBox showStatSheetCheckBox;

	private final JSpinner haloLineWidthSpinner;
	private final JSpinner haloOverlayOpacitySpinner;
	private final JSpinner auraOverlayOpacitySpinner;
	private final JSpinner lightOverlayOpacitySpinner;
	private final JSpinner fogOverlayOpacitySpinner;
	private final JCheckBox useHaloColorAsVisionOverlayCheckBox;
	private final JCheckBox autoRevealVisionOnGMMoveCheckBox;
	private final JCheckBox showSmiliesCheckBox;
	private final JCheckBox playSystemSoundCheckBox;
	private final JCheckBox playSystemSoundOnlyWhenNotFocusedCheckBox;

	private final JCheckBox facingFaceEdges;
	private final JCheckBox facingFaceVertices;

	private final JCheckBox showAvatarInChat;

	private final JCheckBox allowPlayerMacroEditsDefault;

	private final JCheckBox toolTipInlineRolls;
	private final JETAColorWell trustedOuputForeground;
	private final JETAColorWell trustedOuputBackground;
	private final JTextField chatAutosaveTime;
	private final JTextField chatFilenameFormat;
	private final JTextField typingNotificationDuration;


	// Defaults
	private final JComboBox defaultGridTypeCombo;
	private final JTextField defaultGridSizeTextField;
	private final JTextField defaultUnitsPerCellTextField;
	private final JTextField defaultVisionDistanceTextField;
	private final JTextField statsheetPortraitSize;

	private final JSpinner autoSaveSpinner;
	private final JCheckBox saveReminderCheckBox;

	private final JCheckBox showDialogOnNewToken;

	// Accessibility
	private final JTextField fontSizeTextField;
	private final JTextField toolTipInitialDelay;
	private final JTextField toolTipDismissDelay;

	//Application
	private final JCheckBox fitGMView;
	private final JCheckBox fillSelectionCheckBox;
	private final JCheckBox hideNPCs;
	private final JCheckBox ownerPermissions;
	private final JCheckBox lockMovement;


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

		showStatSheetCheckBox = panel.getCheckBox("showStatSheet");
		showNumberingCombo = panel.getComboBox("showNumberingCombo");
		saveReminderCheckBox = panel.getCheckBox("saveReminderCheckBox");
		fillSelectionCheckBox = panel.getCheckBox("fillSelectionCheckBox");
		autoSaveSpinner = panel.getSpinner("autoSaveSpinner");
		duplicateTokenCombo = panel.getComboBox("duplicateTokenCombo");
		tokenNamingCombo = panel.getComboBox("tokenNamingCombo");
		newMapsHaveFOWCheckBox = panel.getCheckBox("newMapsHaveFOWCheckBox");
		tokensPopupWarningWhenDeletedCheckBox = panel.getCheckBox("tokensPopupWarningWhenDeletedCheckBox");//new JCheckBox();//panel.getCheckBox("testCheckBox");
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
		haloOverlayOpacitySpinner = panel.getSpinner("haloOverlayOpacitySpinner");
		auraOverlayOpacitySpinner = panel.getSpinner("auraOverlayOpacitySpinner");
		lightOverlayOpacitySpinner = panel.getSpinner("lightOverlayOpacitySpinner");
		fogOverlayOpacitySpinner = panel.getSpinner("fogOverlayOpacitySpinner");

		useHaloColorAsVisionOverlayCheckBox = panel.getCheckBox("useHaloColorAsVisionOverlayCheckBox");
		autoRevealVisionOnGMMoveCheckBox = panel.getCheckBox("autoRevealVisionOnGMMoveCheckBox");
		showSmiliesCheckBox = panel.getCheckBox("showSmiliesCheckBox");
		playSystemSoundCheckBox = panel.getCheckBox("playSystemSounds");
		playSystemSoundOnlyWhenNotFocusedCheckBox = panel.getCheckBox("soundsOnlyWhenNotFocused");
		showAvatarInChat = panel.getCheckBox("showChatAvatar");
		showDialogOnNewToken = panel.getCheckBox("showDialogOnNewToken");
		movementMetricCombo = panel.getComboBox("movementMetric");
		allowPlayerMacroEditsDefault = panel.getCheckBox("allowPlayerMacroEditsDefault");
		toolTipInlineRolls = panel.getCheckBox("toolTipInlineRolls");
		trustedOuputForeground = (JETAColorWell) panel.getComponentByName("trustedOuputForeground");
		trustedOuputBackground = (JETAColorWell) panel.getComponentByName("trustedOuputBackground");
		toolTipInitialDelay = panel.getTextField("toolTipInitialDelay");
		toolTipDismissDelay = panel.getTextField("toolTipDismissDelay");
		facingFaceEdges = panel.getCheckBox("facingFaceEdges");
		facingFaceVertices = panel.getCheckBox("facingFaceVertices");
		chatAutosaveTime = panel.getTextField("chatAutosaveTime");
		chatFilenameFormat = panel.getTextField("chatFilenameFormat");
		fitGMView = panel.getCheckBox("fitGMView");
		hideNPCs = panel.getCheckBox("hideNPCs");
		ownerPermissions = panel.getCheckBox("ownerPermission");
		lockMovement = panel.getCheckBox("lockMovement");
		typingNotificationDuration = panel.getTextField("typingNotificationDuration");
		setInitialState();

		// And keep it updated

		facingFaceEdges.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AppPreferences.setFaceEdge(facingFaceEdges.isSelected());
				updateFacings();
			}
		});
		facingFaceVertices.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AppPreferences.setFaceVertex(facingFaceVertices.isSelected());
				updateFacings();
			}
		});

		toolTipInlineRolls.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AppPreferences.setUseToolTipForInlineRoll(toolTipInlineRolls.isSelected());
			}
		});

		toolTipInitialDelay.getDocument().addDocumentListener(new DocumentListener() {
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
					int value = StringUtil.parseInteger(toolTipInitialDelay.getText());
					AppPreferences.setToolTipInitialDelay(value);
					ToolTipManager.sharedInstance().setInitialDelay(value);
				} catch (ParseException nfe) {
					// Ignore it
				}
			}
		});


		typingNotificationDuration.getDocument().addDocumentListener(new DocumentListener() {

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
					int value = StringUtil.parseInteger(typingNotificationDuration.getText());
					AppPreferences.setTypingNotificationDuration(value);
				} catch (ParseException nfe){
					// ignore it
				}

			}


		});


		toolTipDismissDelay.getDocument().addDocumentListener(new DocumentListener() {
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
					int value = StringUtil.parseInteger(toolTipDismissDelay.getText());
					AppPreferences.setToolTipDismissDelay(value);
					ToolTipManager.sharedInstance().setDismissDelay(value);
				} catch (ParseException nfe) {
					// Ignore it
				}
			}
		});

		trustedOuputForeground.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AppPreferences.setTrustedPrefixFG(trustedOuputForeground.getColor());
				MapTool.getFrame().getCommandPanel().setTrustedMacroPrefixColors(AppPreferences.getTrustedPrefixFG(), AppPreferences.getTrustedPrefixBG());
			}
		});
		trustedOuputBackground.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AppPreferences.setTrustedPrefixBG(trustedOuputBackground.getColor());
				MapTool.getFrame().getCommandPanel().setTrustedMacroPrefixColors(AppPreferences.getTrustedPrefixFG(), AppPreferences.getTrustedPrefixBG());
			}
		});
		chatAutosaveTime.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int value = StringUtil.parseInteger(chatAutosaveTime.getText());
					AppPreferences.setChatAutosaveTime(value);
					// TODO FJE Change the timer.  Check campaign auto-save for details.
//					MapTool.getFrame().getCommandPanel().setTrustedMacroPrefixColors(AppPreferences.getTrustedPrefixFG(), AppPreferences.getTrustedPrefixBG());
				} catch (ParseException nfe) {
					// Ignore it
				}
			}
		});
		chatFilenameFormat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AppPreferences.setChatFilenameFormat(chatFilenameFormat.getText());
				// TODO FJE Change the filename format.  Might need synchronization?
//				MapTool.getFrame().getCommandPanel().setTrustedMacroPrefixColors(AppPreferences.getTrustedPrefixFG(), AppPreferences.getTrustedPrefixBG());
			}
		});

		allowPlayerMacroEditsDefault.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AppPreferences.setAllowPlayerMacroEditsDefault(allowPlayerMacroEditsDefault.isSelected());
			}
		});

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
		fillSelectionCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AppPreferences.setFillSelectionBox(fillSelectionCheckBox.isSelected());
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
		tokensPopupWarningWhenDeletedCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AppPreferences.setTokensWarnWhenDeleted(tokensPopupWarningWhenDeletedCheckBox.isSelected());
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
		showStatSheetCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AppPreferences.setShowStatSheet(showStatSheetCheckBox.isSelected());
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
					int value = StringUtil.parseInteger(defaultGridSizeTextField.getText());
					AppPreferences.setDefaultGridSize(value);
				} catch (ParseException nfe) {
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
					int value = StringUtil.parseInteger(defaultUnitsPerCellTextField.getText());
					AppPreferences.setDefaultUnitsPerCell(value);
				} catch (ParseException nfe) {
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
					int value = StringUtil.parseInteger(defaultVisionDistanceTextField.getText());
					AppPreferences.setDefaultVisionDistance(value);
				} catch (ParseException nfe) {
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
					int value = StringUtil.parseInteger(statsheetPortraitSize.getText());
					AppPreferences.setPortraitSize(value);
				} catch (ParseException nfe) {
					// Ignore it
				}
			}
		});
		haloLineWidthSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent ce) {
				AppPreferences.setHaloLineWidth((Integer)haloLineWidthSpinner.getValue());
			}
		});

		// Overlay opacity options in AppPreferences, with
		// error checking to ensure values are within the acceptable range
		// of 0 and 255.
		haloOverlayOpacitySpinner.addChangeListener(new OpacityProxy() {
			@Override
			protected void storeSpinnerValue(int value) {
				AppPreferences.setHaloOverlayOpacity(value);
			}
		});
		auraOverlayOpacitySpinner.addChangeListener(new OpacityProxy() {
			@Override
			protected void storeSpinnerValue(int value) {
				AppPreferences.setAuraOverlayOpacity(value);
			}
		});
		lightOverlayOpacitySpinner.addChangeListener(new OpacityProxy() {
			@Override
			protected void storeSpinnerValue(int value) {
				AppPreferences.setLightOverlayOpacity(value);
			}
		});
		fogOverlayOpacitySpinner.addChangeListener(new OpacityProxy() {
			@Override
			protected void storeSpinnerValue(int value) {
				AppPreferences.setFogOverlayOpacity(value);
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
					int value = StringUtil.parseInteger(fontSizeTextField.getText());
					AppPreferences.setFontSize(value);
				} catch (ParseException nfe) {
					// Ignore it
				}
			}
		});

		fitGMView.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AppPreferences.setFitGMView(fitGMView.isSelected());
			}
		});
		hideNPCs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AppPreferences.setInitHideNpcs(hideNPCs.isSelected());
			}
		});
		ownerPermissions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AppPreferences.setInitOwnerPermissions(ownerPermissions.isSelected());
			}
		});
		lockMovement.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AppPreferences.setInitLockMovement(lockMovement.isSelected());
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

	/**
	 * Used by the ActionListeners of the facing checkboxes to update
	 * the facings for all of the current zones.   Redundant to go
	 * through all zones because all zones using the same grid type
	 * share facings but it doesn't hurt anything and avoids having to
	 * track what grid types are being used.
	 */
	private void updateFacings() {
		List <Zone> zlist = MapTool.getServer().getCampaign().getZones();
		boolean faceEdges = AppPreferences.getFaceEdge();
		boolean faceVertices = AppPreferences.getFaceVertex();
		for (Zone z : zlist) {
			Grid g = z.getGrid();
			g.setFacings(faceEdges, faceVertices);
		}
	}

	private void setInitialState() {

		showDialogOnNewToken.setSelected(AppPreferences.getShowDialogOnNewToken());
		saveReminderCheckBox.setSelected(AppPreferences.getSaveReminder());
		fillSelectionCheckBox.setSelected(AppPreferences.getFillSelectionBox());
		autoSaveSpinner.setValue(AppPreferences.getAutoSaveIncrement());
		newMapsHaveFOWCheckBox.setSelected(AppPreferences.getNewMapsHaveFOW());
		tokensPopupWarningWhenDeletedCheckBox.setSelected(AppPreferences.getTokensWarnWhenDeleted());
		tokensStartSnapToGridCheckBox.setSelected(AppPreferences.getTokensStartSnapToGrid());
		newMapsVisibleCheckBox.setSelected(AppPreferences.getNewMapsVisible());
		newTokensVisibleCheckBox.setSelected(AppPreferences.getNewTokensVisible());
		stampsStartFreeSizeCheckBox.setSelected(AppPreferences.getObjectsStartFreesize());
		tokensStartFreeSizeCheckBox.setSelected(AppPreferences.getTokensStartFreesize());
		stampsStartSnapToGridCheckBox.setSelected(AppPreferences.getObjectsStartSnapToGrid());
		backgroundsStartFreeSizeCheckBox.setSelected(AppPreferences.getBackgroundsStartFreesize());
		showStatSheetCheckBox.setSelected(AppPreferences.getShowStatSheet());
		backgroundsStartSnapToGridCheckBox.setSelected(AppPreferences.getBackgroundsStartSnapToGrid());
		defaultGridSizeTextField.setText(Integer.toString(AppPreferences.getDefaultGridSize()));
		defaultUnitsPerCellTextField.setText(Integer.toString(AppPreferences.getDefaultUnitsPerCell()));
		defaultVisionDistanceTextField.setText(Integer.toString(AppPreferences.getDefaultVisionDistance()));
		statsheetPortraitSize.setText(Integer.toString(AppPreferences.getPortraitSize()));
		fontSizeTextField.setText(Integer.toString(AppPreferences.getFontSize()));
		haloLineWidthSpinner.setValue(AppPreferences.getHaloLineWidth());

		haloOverlayOpacitySpinner.setModel(new SpinnerNumberModel(AppPreferences.getHaloOverlayOpacity(), 0, 255, 1));
		auraOverlayOpacitySpinner.setModel(new SpinnerNumberModel(AppPreferences.getAuraOverlayOpacity(), 0, 255, 1));
		lightOverlayOpacitySpinner.setModel(new SpinnerNumberModel(AppPreferences.getLightOverlayOpacity(), 0, 255, 1));
		fogOverlayOpacitySpinner.setModel(new SpinnerNumberModel(AppPreferences.getFogOverlayOpacity(), 0, 255, 1));

		useHaloColorAsVisionOverlayCheckBox.setSelected(AppPreferences.getUseHaloColorOnVisionOverlay());
		autoRevealVisionOnGMMoveCheckBox.setSelected(AppPreferences.getAutoRevealVisionOnGMMovement());
		showSmiliesCheckBox.setSelected(AppPreferences.getShowSmilies());
		playSystemSoundCheckBox.setSelected(AppPreferences.getPlaySystemSounds());
		playSystemSoundOnlyWhenNotFocusedCheckBox.setSelected(AppPreferences.getPlaySystemSoundsOnlyWhenNotFocused());
		showAvatarInChat.setSelected(AppPreferences.getShowAvatarInChat());
		allowPlayerMacroEditsDefault.setSelected(AppPreferences.getAllowPlayerMacroEditsDefault());
		toolTipInlineRolls.setSelected(AppPreferences.getUseToolTipForInlineRoll());
		trustedOuputForeground.setColor(AppPreferences.getTrustedPrefixFG());
		trustedOuputBackground.setColor(AppPreferences.getTrustedPrefixBG());
		toolTipInitialDelay.setText(Integer.toString(AppPreferences.getToolTipInitialDelay()));
		toolTipDismissDelay.setText(Integer.toString(AppPreferences.getToolTipDismissDelay()));
		facingFaceEdges.setSelected(AppPreferences.getFaceEdge());
		facingFaceVertices.setSelected(AppPreferences.getFaceVertex());
		fitGMView.setSelected(AppPreferences.getFitGMView());
		hideNPCs.setSelected(AppPreferences.getInitHideNpcs());
		ownerPermissions.setSelected(AppPreferences.getInitOwnerPermissions());
		lockMovement.setSelected(AppPreferences.getInitLockMovement());
		typingNotificationDuration.setText(Integer.toString(AppPreferences.getTypingNotificationDuration()));
	}
}
