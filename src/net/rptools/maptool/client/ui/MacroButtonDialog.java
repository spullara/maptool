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
import net.rptools.maptool.client.ui.macrobuttons.buttons.AbstractMacroButton;

public class MacroButtonDialog extends JDialog {

	FormPanel panel;
	AbstractMacroButton button;
	private boolean updated = false;
	
	public MacroButtonDialog() {
		super (MapTool.getFrame(), "", true);
		
		panel = new FormPanel("net/rptools/maptool/client/ui/forms/macroButtonDialog.jfrm");
		setContentPane(panel);
		
		installOKButton();
		installCancelButton();
		installHotKeyCombo();
		installColorCombo();
		
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
	
	public void show(AbstractMacroButton button) {
		this.button = button;
		
		getColorComboBox().setSelectedItem(button.getColor());
		getHotKeyCombo().setSelectedItem(button.getHotKey());
		getLabelTextField().setText(button.getMacroLabel());
		getSortbyTextField().setText(button.getSortby());
		getCommandTextArea().setText(button.getCommand());
		getAutoExecuteCheckBox().setSelected(button.getAutoExecute());
		getIncludeLabelCheckBox().setSelected(button.getIncludeLabel());
		getApplyToTokensCheckBox().setSelected(button.getApplyToTokens());
		
		setVisible(true);
	}
	
	private void save() {
		
		String hotKey = getHotKeyCombo().getSelectedItem().toString();
		button.getHotKeyManager().assignKeyStroke(hotKey);
		button.setColor(getColorComboBox().getSelectedItem().toString());
		button.setMacroLabel(getLabelTextField().getText());
		button.setSortby(getSortbyTextField().getText());
		button.setText(this.button.getButtonText());
		button.setCommand(getCommandTextArea().getText());
		button.setAutoExecute(getAutoExecuteCheckBox().isSelected());
		button.setIncludeLabel(getIncludeLabelCheckBox().isSelected());
		button.setApplyToTokens(getApplyToTokensCheckBox().isSelected());
		updated = true;
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
	
	private JTextField getSortbyTextField() {
		return panel.getTextField("sortby");
	}
	
	private JTextArea getCommandTextArea() {
		return (JTextArea) panel.getTextComponent("command");
	}
	
	public boolean wasUpdated() { 
		return updated;
	}
}
