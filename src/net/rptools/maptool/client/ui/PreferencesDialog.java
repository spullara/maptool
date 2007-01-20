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
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.AppListeners;
import net.rptools.maptool.client.AppPreferences;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.GridFactory;

import com.jeta.forms.components.panel.FormPanel;

public class PreferencesDialog extends JDialog {

	// Performance
	private JCheckBox useTranslucentFogCheckBox;

	// Interactions
	private JCheckBox newMapsHaveFOWCheckBox;
	private JCheckBox tokensStartSnapToGridCheckBox;
	private JCheckBox newMapsVisibleCheckBox;
	private JCheckBox newTokensVisibleCheckBox;
	private JCheckBox stampsStartSnapToGridCheckBox;
	private JCheckBox stampsStartFreeSizeCheckBox;
	private JCheckBox backgroundsStartSnapToGridCheckBox;
	private JCheckBox backgroundsStartFreeSizeCheckBox;

	// Defaults
	private JComboBox defaultGridTypeCombo;
	private JTextField defaultGridSizeTextField;

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
				AppListeners.firePreferencesUpdated();
			}
		});
		
		useTranslucentFogCheckBox = panel.getCheckBox("useTranslucentFogCheckBox");
		newMapsHaveFOWCheckBox = panel.getCheckBox("newMapsHaveFOWCheckBox");
		tokensStartSnapToGridCheckBox = panel.getCheckBox("tokensStartSnapToGridCheckBox");
		newMapsVisibleCheckBox = panel.getCheckBox("newMapsVisibleCheckBox");
		newTokensVisibleCheckBox = panel.getCheckBox("newTokensVisibleCheckBox");
		stampsStartFreeSizeCheckBox = panel.getCheckBox("stampsStartFreeSize");
		stampsStartSnapToGridCheckBox = panel.getCheckBox("stampsStartSnapToGrid");
		backgroundsStartFreeSizeCheckBox = panel.getCheckBox("backgroundsStartFreeSize");
		backgroundsStartSnapToGridCheckBox = panel.getCheckBox("backgroundsStartSnapToGrid");
		defaultGridTypeCombo = panel.getComboBox("defaultGridTypeCombo");
		defaultGridSizeTextField = panel.getTextField("defaultGridSize");
		fontSizeTextField = panel.getTextField("fontSize");
		
		setInitialState();

		// And keep it updated
		useTranslucentFogCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AppPreferences.setUseTranslucentFog(useTranslucentFogCheckBox.isSelected());
				
				MapTool.getFrame().refresh();
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
				AppPreferences.setStampsStartFreesize(stampsStartFreeSizeCheckBox.isSelected());
			}
		});
		stampsStartSnapToGridCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AppPreferences.setStampsStartSnapToGrid(stampsStartSnapToGridCheckBox.isSelected());
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
		

		DefaultComboBoxModel model = new DefaultComboBoxModel();
		model.addElement(GridFactory.SQUARE);
		model.addElement(GridFactory.HEX);
		model.setSelectedItem(AppPreferences.getDefaultGridType());
		defaultGridTypeCombo.setModel(model);
		defaultGridTypeCombo.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				AppPreferences.setDefaultGridType((String) defaultGridTypeCombo.getSelectedItem());
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
		
		useTranslucentFogCheckBox.setSelected(AppPreferences.getUseTranslucentFog());
		newMapsHaveFOWCheckBox.setSelected(AppPreferences.getNewMapsHaveFOW());
		tokensStartSnapToGridCheckBox.setSelected(AppPreferences.getTokensStartSnapToGrid());
		newMapsVisibleCheckBox.setSelected(AppPreferences.getNewMapsVisible());
		newTokensVisibleCheckBox.setSelected(AppPreferences.getNewTokensVisible());
		stampsStartFreeSizeCheckBox.setSelected(AppPreferences.getStampsStartFreesize());
		stampsStartSnapToGridCheckBox.setSelected(AppPreferences.getStampsStartSnapToGrid());
		backgroundsStartFreeSizeCheckBox.setSelected(AppPreferences.getBackgroundsStartFreesize());
		backgroundsStartSnapToGridCheckBox.setSelected(AppPreferences.getBackgroundsStartSnapToGrid());
		defaultGridSizeTextField.setText(Integer.toString(AppPreferences.getDefaultGridSize()));
		fontSizeTextField.setText(Integer.toString(AppPreferences.getFontSize()));
	}
}
