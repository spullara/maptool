package net.rptools.maptool.client.ui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import net.rptools.lib.net.FTPLocation;
import net.rptools.lib.net.LocalLocation;
import net.rptools.lib.net.Location;
import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.AppUtil;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.ExportInfo;

import com.jeta.forms.components.panel.FormPanel;

public class ExportDialog extends JDialog {

	private JTextField ftpUsernameTextField;
	private JTextField ftpHostnameTextField;
	private JPasswordField ftpPasswordField;
	private JTextField ftpPathTextField;
	private JTabbedPane tabbedPane;
	
	private JRadioButton viewGMRadio;
	private JRadioButton viewPlayerRadio;
	private JRadioButton typeApplicationRadio;
	private JRadioButton typeCurrentViewRadio;
	private JRadioButton typeFullMapRadio;

	private JTextField locationTextField;

	private JButton exportButton;
	private JButton cancelButton;
	private JButton browseButton;
	
	private FormPanel formPanel;
	
	private ExportInfo exportInfo;
	
	public ExportDialog(ExportInfo exportInfo) {
		super(MapTool.getFrame(), "Export Screenshot", true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		formPanel = new FormPanel("net/rptools/maptool/client/ui/forms/exportDialog.jfrm");

		getTabbedPane();
		
		getUsernameTextField();
		getHostnameTextField();
		getPasswordField();
		getPathTextField();

		getLocationTextField();
		
		getExportButton();
		getCancelButton();
		getBrowseButton();
		
		setLayout(new GridLayout());
		add(formPanel);
		
		if (!MapTool.getPlayer().isGM()) {
			getViewGMRadio().setEnabled(false);
			getViewPlayerRadio().setSelected(true);
		}

		initExportInfo(exportInfo);
		
		// Until full map export is available
		getTypeCurrentViewRadio().setSelected(true);
		
		getRootPane().setDefaultButton(getExportButton());
		pack();
	}

	private void initExportInfo(ExportInfo exportInfo) {

		if (exportInfo == null) {
			return;
		}
		
		// TYPE
		switch (exportInfo.getType()) {
		case ExportInfo.Type.CURRENT_VIEW:
			getTypeCurrentViewRadio().setSelected(true);
			break;
		case ExportInfo.Type.FULL_MAP:
			getTypeFullMapRadio().setSelected(true);
			break;
		}
		
		// VIEW
		switch (exportInfo.getView()) {
		case ExportInfo.View.GM:
			getViewGMRadio().setSelected(true);
			break;
		case ExportInfo.View.PLAYER:
			getViewPlayerRadio().setSelected(true);
			break;
		}
		
		// LOCATION
		Location location = exportInfo.getLocation();
		if (location instanceof FTPLocation) {
			FTPLocation ftpLocation = (FTPLocation) location;
			getUsernameTextField().setText(ftpLocation.getUsername());
			getHostnameTextField().setText(ftpLocation.getHostname());
			getPasswordField().setText(ftpLocation.getPassword());
			getPathTextField().setText(ftpLocation.getPath());
		}
		if (location instanceof LocalLocation) {
			LocalLocation localLocation = (LocalLocation) location;
			getLocationTextField().setText(localLocation.getFile().getAbsolutePath());
		}
	}
	
	public ExportInfo getExportInfo() {
		return exportInfo;
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
		}
		return tabbedPane;
	}
	
	public JRadioButton getViewGMRadio() {
		if (viewGMRadio == null) {
			viewGMRadio = formPanel.getRadioButton("viewGM");
		}
		return viewGMRadio;
	}
	
	public JRadioButton getViewPlayerRadio() {
		if (viewPlayerRadio == null) {
			viewPlayerRadio = formPanel.getRadioButton("viewPlayer");
		}
		return viewPlayerRadio;
	}
	
	public JRadioButton getTypeCurrentViewRadio() {
		if (typeCurrentViewRadio == null) {
			typeCurrentViewRadio = formPanel.getRadioButton("typeCurrentView");
		}
		return typeCurrentViewRadio;
	}
	
	public JRadioButton getTypeFullMapRadio() {
		if (typeFullMapRadio == null) {
			typeFullMapRadio = formPanel.getRadioButton("typeFullMap");
		}
		return typeFullMapRadio;
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
	
	public JTextField getLocationTextField() {
		if (locationTextField == null) {
			
			locationTextField = formPanel.getTextField("locationTextField");
		}
		
		return locationTextField;
	}
	
	public JButton getExportButton() {
		if (exportButton == null) {
			exportButton = (JButton) formPanel.getButton("exportButton");
			exportButton.addActionListener(new ActionListener() {
				// TODO: Pull this out of an aic
				public void actionPerformed(ActionEvent e) {
					
					exportInfo = new ExportInfo();
					
					// VIEW
					if (getViewGMRadio().isSelected()) {
						exportInfo.setView(ExportInfo.View.GM);
					} else if (getViewPlayerRadio().isSelected()) {
						exportInfo.setView(ExportInfo.View.PLAYER);
					} else {
						MapTool.showError("Must select a view");
						return;
					}
					
					// TYPE
					if (getTypeCurrentViewRadio().isSelected()) {
						exportInfo.setType(ExportInfo.Type.CURRENT_VIEW);
//					} else if (getTypeFullMapRadio().isSelected()) {
//						exportInfo.setType(ExportInfo.Type.FULL_MAP);
					} else {
						MapTool.showError("Must select a type");
						return;
					}
					
					// LOCATION
					// TODO: Show a progress dialog
					// TODO: Make this less fragile
					switch (getTabbedPane().getSelectedIndex()) {
					case 0:

						File file = new File(getLocationTextField().getText());
						
						if (!file.getName().toLowerCase().endsWith(".png")) {
							file = new File(file.getAbsolutePath() + ".png");
						}
						
						exportInfo.setLocation(new LocalLocation(file));
						break;
					case 1:
						String username = getUsernameTextField().getText();
						String password = new String(getPasswordField().getPassword());
						String host = getHostnameTextField().getText();
						String path = getPathTextField().getText();
						
						// PNG only supported for now
						if (!path.toLowerCase().endsWith(".png")) {
							path += ".png";
						}
						
						exportInfo.setLocation(new FTPLocation(username, password, host, path));
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
					exportInfo = null;
					setVisible(false);
				}
			});
		}
		
		return cancelButton;
	}

	public JButton getBrowseButton() {
		if (browseButton == null) {
			browseButton = (JButton) formPanel.getButton("browseButton");
			browseButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFileChooser chooser = new JFileChooser();
					if (exportInfo != null && exportInfo.getLocation() instanceof LocalLocation) {
						LocalLocation location = (LocalLocation) exportInfo.getLocation();
						chooser.setSelectedFile(location.getFile());
					}
					
					if (chooser.showOpenDialog(ExportDialog.this) == JFileChooser.APPROVE_OPTION) {
						getLocationTextField().setText(chooser.getSelectedFile().getAbsolutePath());
					}
				}
			});
		}
		
		return cancelButton;
	}
}
