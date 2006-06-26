package net.rptools.maptool.client.ui;

import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import net.rptools.lib.net.FTPLocation;
import net.rptools.lib.net.LocalLocation;
import net.rptools.lib.net.Location;
import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.MapTool;

import com.jeta.forms.components.panel.FormPanel;

public class ExportDialog extends JDialog {

	private JTextField ftpUsernameTextField;
	private JTextField ftpHostnameTextField;
	private JPasswordField ftpPasswordField;
	private JTextField ftpPathTextField;
	private JTabbedPane tabbedPane;

	private JFileChooser fileChooser;

	private JButton exportButton;
	private JButton cancelButton;
	
	private FormPanel formPanel;
	
	private Location location;
	
	public ExportDialog(Location location) {
		super(MapTool.getFrame(), "Export Screenshot", true);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		formPanel = new FormPanel("net/rptools/maptool/client/ui/forms/exportDialog.jfrm");

		getTabbedPane();
		
		getUsernameTextField();
		getHostnameTextField();
		getPasswordField();
		getPathTextField();

		getFileChooser();
		
		getExportButton();
		getCancelButton();
		
		setLayout(new GridLayout());
		add(formPanel);
		
		pack();
	}

	public Location getExportLocation() {
		return location;
	}
	
	@Override
	public void setVisible(boolean b) {

		if (b) {
			SwingUtil.centerOver(this, MapTool.getFrame());
		}
		super.setVisible(b);
	}
	
	public JTabbedPane getTabbedPane() {
		if (tabbedPane == null) {
			tabbedPane = formPanel.getTabbedPane("tabs");
			tabbedPane.setSelectedIndex(1);
			tabbedPane.setEnabledAt(0, false);
		}
		return tabbedPane;
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
			ftpPasswordField = (JPasswordField) formPanel.getComponentByName("password");
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
			fileChooser = (JFileChooser) formPanel.getComponentByName("filechooser");
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setMultiSelectionEnabled(false);
		}
		
		return fileChooser;
	}
	
	public JButton getExportButton() {
		if (exportButton == null) {
			exportButton = (JButton) formPanel.getButton("exportButton");
			exportButton.addActionListener(new ActionListener() {
				// TODO: Pull this out of an aic
				public void actionPerformed(ActionEvent e) {
					
					// TODO: Show a progress dialog
					// TODO: Make this less fragile
					switch (getTabbedPane().getSelectedIndex()) {
					case 0:
						getFileChooser().approveSelection();

						File file = getFileChooser().getSelectedFile();
						if (file != null) {
							location = new LocalLocation(file);
						}
						break;
					case 1:
						String username = getUsernameTextField().getText();
						String password = new String(getPasswordField().getPassword());
						String host = getHostnameTextField().getText();
						String path = getPathTextField().getText();
						
						location = new FTPLocation(username, password, host, path);
						break;
					}

					setVisible(false);
				}
				
			});
		}
		
		return exportButton;
	}
	
	public JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = (JButton) formPanel.getButton("cancelButton");
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
				}
			});
		}
		
		return cancelButton;
	}
}
