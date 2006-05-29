package net.rptools.maptool.client.ui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JTextField;

import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.Token;

import com.jeta.forms.components.panel.FormPanel;

public class TokenPropertiesDialog extends JDialog {

	private JTextField nameTextField;
	private JComboBox typeComboBox;
	
	private Token token;

	private boolean committed;
	
	public TokenPropertiesDialog(Token token) {
		super(MapTool.getFrame(), "Token Properties", true);
		
		this.token = token;

		FormPanel panel = new FormPanel("net/rptools/maptool/client/ui/forms/tokenProperties.jfrm");

		bindNameTextField(panel);
		bindTypeComboBox(panel);
		bindOKButton(panel);
		bindCancelButton(panel);
		
		setLayout(new GridLayout());
		((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		add(panel);

		pack();
		
		committed = false;
	}

	private void bindNameTextField(FormPanel panel) {

		nameTextField = panel.getTextField("name");
		nameTextField.setText(token.getName());
	}
	
	private void bindTypeComboBox(FormPanel panel) {
		
		typeComboBox = panel.getComboBox("type");
		typeComboBox.setModel(new DefaultComboBoxModel(Token.Type.values()));
		typeComboBox.setSelectedItem(token.getTokenType());
		
	}

	private void bindOKButton(FormPanel panel) {

		AbstractButton button = panel.getButton("okButton");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				commit();
				setVisible(false);
			}
		});
	}
	
	private void bindCancelButton(FormPanel panel) {

		AbstractButton button = panel.getButton("cancelButton");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
	}
	
	private void commit() {
		committed = true;
		
		token.setName(nameTextField.getText());
		token.setTokenType((Token.Type) typeComboBox.getSelectedItem());
	}
	
	public boolean isCommitted() {
		return committed;
	}
	
	@Override
	public void setVisible(boolean b) {

		if (b) {
			SwingUtil.centerOver(this, MapTool.getFrame());
		}
		super.setVisible(b);
	}
	
}
