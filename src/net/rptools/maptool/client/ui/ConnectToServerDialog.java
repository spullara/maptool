/* The MIT License
 * 
 * Copyright (c) 2005 David Rice, Trevor Croft
 * 
 * Permission is hereby granted, free of charge, to any person 
 * obtaining a copy of this software and associated documentation files 
 * (the "Software"), to deal in the Software without restriction, 
 * including without limitation the rights to use, copy, modify, merge, 
 * publish, distribute, sublicense, and/or sell copies of the Software, 
 * and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be 
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS 
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN 
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
 * SOFTWARE.
 */
package net.rptools.maptool.client.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.InetAddress;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

import net.rptools.maptool.client.AppConstants;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.swing.AbeillePanel;
import net.rptools.maptool.client.swing.GenericDialog;
import net.tsc.servicediscovery.AnnouncementListener;
import net.tsc.servicediscovery.ServiceFinder;
/**
 * @author trevor
 */
public class ConnectToServerDialog extends AbeillePanel<ConnectToServerDialogPreferences> implements AnnouncementListener{

	private ServiceFinder finder;

	private boolean accepted;

	private GenericDialog dialog;

	private JList serverList;
	
	/**
	 * This is the default constructor
	 */
	public ConnectToServerDialog() {
		super("net/rptools/maptool/client/ui/forms/connectToServerDialog.jfrm");
		
		setPreferredSize(new Dimension(400, 400));
		
		panelInit();
	}
	
	public void showDialog() {
		dialog = new GenericDialog("Connect to Server", MapTool.getFrame(), this);

		bind(new ConnectToServerDialogPreferences());

		getRootPane().setDefaultButton(getOKButton());
		dialog.showDialog();
	}
	
	public JButton getOKButton() {
		return (JButton) getComponent("okButton");
	}

	@Override
	public void bind(ConnectToServerDialogPreferences model) {
		finder = new ServiceFinder(AppConstants.SERVICE_GROUP);
		finder.addAnnouncementListener(this);
		
		finder.find();

		super.bind(model);
	}

	@Override
	public void unbind() {
		// Shutting down
		finder.dispose();
		
		super.unbind();
	}
	
	public JButton getCancelButton() {
		return (JButton) getComponent("cancelButton");
	}
	
	public void initCancelButton() {
		getCancelButton().addActionListener(new java.awt.event.ActionListener() { 
			public void actionPerformed(java.awt.event.ActionEvent e) {    
				accepted = false;
				dialog.closeDialog();
			}
		});
	}

	public void initOKButton() {
		getOKButton().addActionListener(new java.awt.event.ActionListener() { 
			public void actionPerformed(java.awt.event.ActionEvent e) {    
				handleOK();
			}
		});
	}

	public boolean accepted() {
		return accepted;
	}

	public JComboBox getRoleComboBox() {
		return (JComboBox) getComponent("@role");
	}
	
	public void initRoleComboBox() {
		getRoleComboBox().setModel(new DefaultComboBoxModel(new String[]{"Player", "GM"}));
	}

	private JList getServerList() {
		if (serverList == null) {
			serverList = new JList(new DefaultListModel());
			serverList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			serverList.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						handleOK();
					}
				};
			});
		}
		return serverList;
	}

	
	public JButton getRescanButton() {
		return (JButton) getComponent("rescanButton");
	}
	
	public void initRescanButton() {
		getRescanButton().addActionListener(new ActionListener() {
				
			public void actionPerformed(ActionEvent e) {
				((DefaultListModel)getServerList().getModel()).clear();
				finder.find();
			}
		});
	}
	
	private void handleOK() {
//		if (usernameTextField.getText().length() == 0) {
//			MapTool.showError("Must supply a username");
//			return;
//		}					
//
//		switch (getTypeTabbedPane().getSelectedIndex()) {
//		// LAN
//		case 0:
//			if (getServerList().getSelectedIndex() < 0) {
//				MapTool.showError("Must select a server");
//				return;
//			}
//			
//			// OK
//			ServerInfo info = (ServerInfo) getServerList().getSelectedValue();
//			selectedPort = info.port;
//			selectedServerAddress = info.address.getHostAddress();
//			
//			break;
//			
//	    // Internet
//		case 2:
//			// TODO: put these into a validation method
//			if (portTextField.getText().length() == 0) {
//				MapTool.showError("Must supply a port");
//				return;
//			}
//			try {
//				Integer.parseInt(portTextField.getText());
//			} catch (NumberFormatException nfe) {
//				MapTool.showError("Port must be numeric");
//				return;
//			}
//
//			if (serverTextField.getText().length() == 0) {
//				MapTool.showError("Must supply a server");
//				return;
//			}					
//
//			// OK
//			selectedPort = Integer.parseInt(getPortTextField().getText());
//			selectedServerAddress = getServerTextField().getText();
//			break;
//			
//		// RPTools.net
//		case 1:
//			
//			if (serverNameTextField.getText().length() == 0) {
//				MapTool.showError("Must supply a server name");
//				return;
//			}
//			
//			// Do the lookup
//			String serverInfo = MapToolRegistry.findInstance(serverNameTextField.getText());
//			if (serverInfo == null || serverInfo.length() == 0) {
//				MapTool.showError("Could not find that server.");
//				return;
//			}
//			
//			String[] data = serverInfo.split(":");
//			selectedServerAddress = data[0];
//			selectedPort = Integer.parseInt(data[1]);
//			break;
//		}
//		
//		option = OPTION_OK;
//		setVisible(false);
//		
//		// Prefs
//		ConnectToServerDialogPreferences prefs = new ConnectToServerDialogPreferences();
//		prefs.setUsername(getUsername());
//		prefs.setHost(getServer());
//		prefs.setPort(getPort());
//		prefs.setRole(getRole());
//		prefs.setPassword(getPassword());
//		prefs.setTab(getTypeTabbedPane().getSelectedIndex());		
//		prefs.setServerName(getServerNameTextField().getText());
	}

	////
	// ANNOUNCEMENT LISTENER
	public void serviceAnnouncement(String type, InetAddress address, int port, byte[] data) {
		((DefaultListModel)getServerList().getModel()).addElement(new ServerInfo(new String(data), address, port));
	}

	private class ServerInfo {
		
		String id;
		InetAddress address;
		int port;
		
		public ServerInfo (String id, InetAddress address, int port) {
			this.id = id;
			this.address = address;
			this.port = port;
			
		}
		
		public String toString() {
			return id;
		}
	}

}
