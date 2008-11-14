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

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.jeta.forms.components.panel.FormPanel;
import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolUtil;
import net.rptools.maptool.client.ui.macrobuttons.buttons.MacroButton;
import net.rptools.maptool.model.MacroButtonProperties;

public class MacroButtonDialog extends JDialog {

	FormPanel panel;
	MacroButton button;
	MacroButtonProperties properties;
	boolean isTokenMacro = false;
	
	public MacroButtonDialog(boolean isTokenMacro) {
		super (MapTool.getFrame(), "", true);
		
		this.isTokenMacro = isTokenMacro;
		panel = new FormPanel("net/rptools/maptool/client/ui/forms/macroButtonDialog.jfrm");
		setContentPane(panel);

		installOKButton();
		installCancelButton();
		installHotKeyCombo();
		installColorCombo();
		installFontColorCombo();
		installFontSizeCombo();
		
		panel.getCheckBox("applyToTokensCheckBox").setEnabled(!isTokenMacro);
		panel.getComboBox("hotKey").setEnabled(!isTokenMacro);
		panel.getTextField("maxWidth").setEnabled(false); // can't get max-width to work, so temporarily disabling it.
		
		pack();
	}
	
	private void installHotKeyCombo() {
		String[] hotkeys = MacroButtonHotKeyManager.HOTKEYS;
		JComboBox combo = panel.getComboBox("hotKey");
		for( int i = 0; i < hotkeys.length; i++ )
			combo.insertItemAt(hotkeys[i], i);		
	}
	
	private void installColorCombo() { 
		JComboBox combo = panel.getComboBox("colorComboBox");
		combo.setModel(new DefaultComboBoxModel(MapToolUtil.getColorNames().toArray()));
		combo.insertItemAt("default", 0);
	}

	private void installFontColorCombo() { 
		JComboBox combo = panel.getComboBox("fontColorComboBox");
		combo.setModel(new DefaultComboBoxModel(MacroButtonProperties.getFontColors()));
		combo.insertItemAt("default", 0);
	}

	private void installFontSizeCombo() { 
		String[] fontSizes = { "0.75em", "0.80em", "0.85em", "0.90em", "0.95em", "1.00em", "1.05em", "1.10em", "1.15em", "1.20em", "1.25em"};
		JComboBox combo = panel.getComboBox("fontSizeComboBox");
		combo.setModel(new DefaultComboBoxModel(fontSizes));
	}

	private void installOKButton() {
		JButton button = (JButton) panel.getButton("okButton");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		getRootPane().setDefaultButton(button);
	}
	
	private void installCancelButton() {
		JButton button = (JButton) panel.getButton("cancelButton");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancel();
			}
		});
	}
	
	@Override
	public void setVisible(boolean b) {
		if (b) {
			SwingUtil.centerOver(this, MapTool.getFrame());
		}
		super.setVisible(b);
	}
	
	public void show(MacroButton button) {
		this.button = button;
		this.properties = button.getProperties();
		
		getColorComboBox().setSelectedItem(properties.getColorKey());
		getHotKeyCombo().setSelectedItem(properties.getHotKey());
		getLabelTextField().setText(properties.getLabel());
		getGroupTextField().setText(properties.getGroup());
		getSortbyTextField().setText(properties.getSortby());
		getCommandTextArea().setText(properties.getCommand());
		getAutoExecuteCheckBox().setSelected(properties.getAutoExecute());
		getIncludeLabelCheckBox().setSelected(properties.getIncludeLabel());
		getApplyToTokensCheckBox().setSelected(properties.getApplyToTokens());
		getFontColorComboBox().setSelectedItem(properties.getFontColorKey());
		getFontSizeComboBox().setSelectedItem(properties.getFontSize());
		getMinWidthTextField().setText(properties.getMinWidth());
		getMaxWidthTextField().setText(properties.getMaxWidth());
		
		setVisible(true);
	}
	
	private void save() {
		
		String hotKey = getHotKeyCombo().getSelectedItem().toString();
		button.getHotKeyManager().assignKeyStroke(hotKey);
		button.setColor(getColorComboBox().getSelectedItem().toString());
		button.setText(this.button.getButtonText());
		properties.setHotKey(hotKey);
		properties.setColorKey(getColorComboBox().getSelectedItem().toString());
		properties.setLabel(getLabelTextField().getText());
		properties.setGroup(getGroupTextField().getText());
		properties.setSortby(getSortbyTextField().getText());
		properties.setCommand(getCommandTextArea().getText());
		properties.setAutoExecute(getAutoExecuteCheckBox().isSelected());
		properties.setIncludeLabel(getIncludeLabelCheckBox().isSelected());
		properties.setApplyToTokens(getApplyToTokensCheckBox().isSelected());
		properties.setFontColorKey(getFontColorComboBox().getSelectedItem().toString());
		properties.setFontSize(getFontSizeComboBox().getSelectedItem().toString());
		properties.setMinWidth(getMinWidthTextField().getText());
		properties.setMaxWidth(getMaxWidthTextField().getText());
		properties.save();
		setVisible(false);
	}
	
	private void cancel() {
		setVisible(false);
	}

	private JCheckBox getAutoExecuteCheckBox() {
		return  panel.getCheckBox("autoExecuteCheckBox");
	}
	
	private JCheckBox getIncludeLabelCheckBox() {
		return  panel.getCheckBox("includeLabelCheckBox");
	}
	
	private JCheckBox getApplyToTokensCheckBox() {
		return  panel.getCheckBox("applyToTokensCheckBox");
	}
	
	private JComboBox getHotKeyCombo() {
		return panel.getComboBox("hotKey");
	}
	
	private JComboBox getColorComboBox() {
		return panel.getComboBox("colorComboBox");
	}
	
	private JTextField getLabelTextField() {
		return panel.getTextField("label");
	}
	
	private JTextField getGroupTextField() {
		return panel.getTextField("group");
	}
	
	private JTextField getSortbyTextField() {
		return panel.getTextField("sortby");
	}
	
	private JTextArea getCommandTextArea() {
		return (JTextArea) panel.getTextComponent("command");
	}

	private JComboBox getFontColorComboBox() {
		return panel.getComboBox("fontColorComboBox");
	}
	
	private JComboBox getFontSizeComboBox() {
		return panel.getComboBox("fontSizeComboBox");
	}
	
	private JTextField getMinWidthTextField() {
		return panel.getTextField("minWidth");
	}
	
	private JTextField getMaxWidthTextField() {
		return panel.getTextField("maxWidth");
	}
	
}
