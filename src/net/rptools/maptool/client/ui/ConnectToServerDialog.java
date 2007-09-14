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
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import net.rptools.maptool.client.AppConstants;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolRegistry;
import net.rptools.maptool.client.swing.AbeillePanel;
import net.rptools.maptool.client.swing.GenericDialog;
import net.tsc.servicediscovery.AnnouncementListener;
import net.tsc.servicediscovery.ServiceFinder;
/**
 * @author trevor
 */
public class ConnectToServerDialog extends AbeillePanel<ConnectToServerDialogPreferences> implements AnnouncementListener{

	private static ServiceFinder finder;
	static {
		finder = new ServiceFinder(AppConstants.SERVICE_GROUP);
	}

	private boolean accepted;

	private GenericDialog dialog;

	private JList localServerList;
	private JList remoteServerList;
	
	private int port;
	private String hostname;
	
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
		
		finder.addAnnouncementListener(this);
		finder.find();

		System.out.println(MapToolRegistry.findAllInstances());

		super.bind(model);
	}

	@Override
	public void unbind() {
		// Shutting down
		finder.removeAnnouncementListener(this);
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

	private JList getLocalServerList() {
		if (localServerList == null) {
			localServerList = new JList(new DefaultListModel());
			localServerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			localServerList.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						handleOK();
					}
				};
			});
		}
		return localServerList;
	}

	private JList getRemoteServerList() {
		if (remoteServerList == null) {
			remoteServerList = new JList(new DefaultListModel());
			remoteServerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			remoteServerList.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						handleOK();
					}
				};
			});
		}
		return remoteServerList;
	}

	
	public JButton getRescanButton() {
		return (JButton) getComponent("rescanButton");
	}
	
	public void initRescanButton() {
		getRescanButton().addActionListener(new ActionListener() {
				
			public void actionPerformed(ActionEvent e) {
				((DefaultListModel)getLocalServerList().getModel()).clear();
				finder.find();
			}
		});
	}
	
	public JTextField getUsernameTextField() {
		return (JTextField) getComponent("@username");
	}

	public JTextField getPortTextField() {
		return (JTextField) getComponent("@port");
	}

	public JTextField getHostTextField() {
		return (JTextField) getComponent("@host");
	}

	public JTextField getServerNameTextField() {
		return (JTextField) getComponent("@serverName");
	}

	public JTabbedPane getTabPane() {
		return (JTabbedPane) getComponent("tabPane");
	}
	
	private void handleOK() {
		if (getUsernameTextField().getText().length() == 0) {
			MapTool.showError("Must supply a username");
			return;
		}					

		JComponent selectedPanel = (JComponent) getTabPane().getSelectedComponent();
		if ("lanPanel".equals(selectedPanel.getName())) {
			
			if (getLocalServerList().getSelectedIndex() < 0) {
				MapTool.showError("Must select a server");
				return;
			}
			
			// OK
			ServerInfo info = (ServerInfo) getLocalServerList().getSelectedValue();
			port = info.port;
			hostname = info.address.getHostAddress();
			
		}
		if ("directPanel".equals(selectedPanel.getName())) {

			// TODO: put these into a validation method
			if (getPortTextField().getText().length() == 0) {
				MapTool.showError("Must supply a port");
				return;
			}
			try {
				Integer.parseInt(getPortTextField().getText());
			} catch (NumberFormatException nfe) {
				MapTool.showError("Port must be numeric");
				return;
			}

			if (getHostTextField().getText().length() == 0) {
				MapTool.showError("Must supply a server");
				return;
			}					

			// OK
			port = Integer.parseInt(getPortTextField().getText());
			hostname = getServerNameTextField().getText();
		}
		if ("rptoolsPanel".equals(selectedPanel.getName())) {
			if (getServerNameTextField().getText().length() == 0) {
				MapTool.showError("Must supply a server name");
				return;
			}
			
			// Do the lookup
			String serverInfo = MapToolRegistry.findInstance(getServerNameTextField().getText());
			if (serverInfo == null || serverInfo.length() == 0) {
				MapTool.showError("Could not find that server.");
				return;
			}
			
			String[] data = serverInfo.split(":");
			hostname = data[0];
			port = Integer.parseInt(data[1]);
		}
		
		System.out.println(hostname + " - " + port);
		
//		if (commit()) {
//			dialog.closeDialog();
//		}
	}
	
	@Override
	public boolean commit() {
		
		ConnectToServerDialogPreferences prefs = new ConnectToServerDialogPreferences();
		
		// Not bindable .. yet
		prefs.setTab(getTabPane().getSelectedIndex());

		return super.commit();
	}

	////
	// ANNOUNCEMENT LISTENER
	public void serviceAnnouncement(String type, InetAddress address, int port, byte[] data) {
		((DefaultListModel)getLocalServerList().getModel()).addElement(new ServerInfo(new String(data), address, port));
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
