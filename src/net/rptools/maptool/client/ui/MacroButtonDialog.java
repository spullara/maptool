package net.rptools.maptool.client.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.MapTool;

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
		
		pack();
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
		
		getLabelTextField().setText(button.getText());
		getCommandTextArea().setText(button.getCommand());
		getAutoExecuteCheckBox().setSelected(button.getAutoExecute());
		
		setVisible(true);
	}
	
	private void save() {
		button.setText(getLabelTextField().getText());
		button.setCommand(getCommandTextArea().getText());
		button.setAutoExecute(getAutoExecuteCheckBox().isSelected());
		setVisible(false);
	}
	
	private void cancel() {
		setVisible(false);
	}

	private JCheckBox getAutoExecuteCheckBox() {
		return  panel.getCheckBox("autoExecuteCheckBox");
	}
	
	private JTextField getLabelTextField() {
		return panel.getTextField("label");
	}
	
	private JTextArea getCommandTextArea() {
		return (JTextArea) panel.getTextComponent("command");
	}
}
