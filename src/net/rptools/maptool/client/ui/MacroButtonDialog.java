package net.rptools.maptool.client.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolUtil;

import com.jeta.forms.components.panel.FormPanel;

public class MacroButtonDialog extends JDialog {

	FormPanel panel;
	MacroButton button;
	
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
		JComboBox combo = (JComboBox)panel.getComboBox("hotKey");
		for( int i = 0; i < hotkeys.length; i++ )
			combo.insertItemAt(hotkeys[i], i);		
	}
	
	private void installColorCombo() { 
		JComboBox combo = (JComboBox)panel.getComboBox("colorComboBox");
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
	
	public void show(MacroButton button) {
		this.button = button;
		
		getColorComboBox().setSelectedItem(button.getColor());
		getHotKeyCombo().setSelectedItem(button.getHotKey());
		getLabelTextField().setText(button.getMacroLabel());
		getCommandTextArea().setText(button.getCommand());
		getAutoExecuteCheckBox().setSelected(button.getAutoExecute());
		getIncludeLabelCheckBox().setSelected(button.getIncludeLabel());
		
		setVisible(true);
	}
	
	private void save() {
		
		String hotKey = getHotKeyCombo().getSelectedItem().toString();
		button.getHotKeyManager().assignKeyStroke(hotKey);
		button.setColor(getColorComboBox().getSelectedItem().toString());
		button.setMacroLabel(getLabelTextField().getText());
		button.setText(this.button.getButtonText());
		button.setCommand(getCommandTextArea().getText());
		button.setAutoExecute(getAutoExecuteCheckBox().isSelected());
		button.setIncludeLabel(getIncludeLabelCheckBox().isSelected());
		
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
	
	private JComboBox getHotKeyCombo() {
		return panel.getComboBox("hotKey");
	}
	
	private JComboBox getColorComboBox() {
		return panel.getComboBox("colorComboBox");
	}
	
	private JTextField getLabelTextField() {
		return panel.getTextField("label");
	}
	
	private JTextArea getCommandTextArea() {
		return (JTextArea) panel.getTextComponent("command");
	}
}
