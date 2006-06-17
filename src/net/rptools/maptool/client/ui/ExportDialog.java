package net.rptools.maptool.client.ui;

import java.net.URL;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.MapTool;

import com.jeta.forms.components.panel.FormPanel;

public class ExportDialog extends JDialog {

	private JTextField ftpUsernameTextField;
	private JTextField ftpHostnameTextField;
	private JPasswordField ftpPasswordField;
	private JTextField ftpPathTextField;

	private JFileChooser fileChooser;

	private JButton exportButton;
	private JButton cancelButton;
	
	private FormPanel formPanel;
	
	public ExportDialog(URL location) {
		super(MapTool.getFrame(), "Export Screenshot", true);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(275, 200);

		formPanel = new FormPanel("net/rptools/maptool/client/ui/forms/exportDialog.jfrm");

		getUsernameTextField();
		getHostnameTextField();
		getPasswordField();
		getPathTextField();

		getFileChooser();
		
		getExportButton();
		getCancelButton();
	}
	
	@Override
	public void setVisible(boolean b) {

		if (b) {
			SwingUtil.centerOver(this, MapTool.getFrame());
		}
		super.setVisible(b);
	}

	public JTextField getUsernameTextField() {
		if (ftpUsernameTextField == null) {
			ftpUsernameTextField = formPanel.getTextField("username");
		}
		
		return ftpUsernameTextField;
	}

	public JTextField getHostnameTextField() {
		if (ftpHostnameTextField == null) {
			ftpHostnameTextField = formPanel.getTextField("host");
		}
		
		return ftpHostnameTextField;
	}
	
	public JPasswordField getPasswordField() {
		if (ftpPasswordField == null) {
			ftpPasswordField = (JPasswordField) formPanel.get("password");
		}
		
		return ftpPasswordField;
	}
	
	public JTextField getPathTextField() {
		if (ftpPathTextField == null) {
			ftpPathTextField = formPanel.getTextField("path");
		}
		
		return ftpPathTextField;
	}
	
	public JFileChooser getFileChooser() {
		if (fileChooser == null) {
			fileChooser = (JFileChooser) formPanel.get("filechooser");
		}
		
		return fileChooser;
	}
	
	public JButton getExportButton() {
		if (exportButton == null) {
			exportButton = (JButton) formPanel.getButton("exportButton");
		}
		
		return exportButton;
	}
	
	public JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = (JButton) formPanel.getButton("cancelButton");
		}
		
		return cancelButton;
	}
}
