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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.AppConstants;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolRegistry;
import net.rptools.maptool.client.swing.AbeillePanel;
import net.rptools.maptool.client.swing.GenericDialog;
import net.tsc.servicediscovery.AnnouncementListener;
import net.tsc.servicediscovery.ServiceFinder;

import org.jdesktop.swingworker.SwingWorker;

import yasb.Binder;
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
	
	@Override
	protected void preModelBind() {
		Binder.setFormat(getPortTextField(), new DecimalFormat("####"));
	}
	
	public int getPort() {
		return port;
	}

	public String getServer() {
		return hostname;
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

		updateLocalServerList();
		updateRemoteServerList();
		
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

	public void initLocalServerList() {
		getLocalServerList().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		getLocalServerList().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					handleOK();
				}
			};
		});
		
	}
	public JList getLocalServerList() {
		return (JList)getComponent("localServerList");
	}

	private void updateLocalServerList() {
		finder.find();
	}
	
	private void updateRemoteServerList() {
		new SwingWorker<Object, Object>() {
			
			RemoteServerTableModel model = null;
			
			@Override
			protected Object doInBackground() throws Exception {
				model = new RemoteServerTableModel(MapToolRegistry.findAllInstances());
				return null;
			}
			@Override
			protected void done() {
				getRemoteServerTable().setModel(model);
				TableColumn column = getRemoteServerTable().getColumnModel().getColumn(1);
				column.setPreferredWidth(70);
				column.setMaxWidth(70);
				column.setCellRenderer(new DefaultTableCellRenderer() {
					{
						setHorizontalAlignment(RIGHT);
					}
				});
			}
		}.execute();
	}
	
	public void initRemoteServerTable() {
		getRemoteServerTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		getRemoteServerTable().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					getServerNameTextField().setText(getRemoteServerTable().getModel().getValueAt(getRemoteServerTable().getSelectedRow(), 0).toString());
					handleOK();
				}
			};
		});
		getRemoteServerTable().setModel(new RemoteServerTableModel(new ArrayList<String>()));
	}
	
	public JTable getRemoteServerTable() {
		return (JTable) getComponent("aliasTable");
	}

	
	public JButton getRescanButton() {
		return (JButton) getComponent("rescanButton");
	}
	
	public JButton getRefreshButton() {
		return (JButton) getComponent("refreshButton");
	}
	
	public void initRescanButton() {
		getRescanButton().addActionListener(new ActionListener() {
				
			public void actionPerformed(ActionEvent e) {
				((DefaultListModel)getLocalServerList().getModel()).clear();
				finder.find();
			}
		});
	}
	
	public void initRefreshButton() {
		getRefreshButton().addActionListener(new ActionListener() {
				
			public void actionPerformed(ActionEvent e) {
				updateRemoteServerList();
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
		if (SwingUtil.hasComponent(selectedPanel, "lanPanel")) {
			
			if (getLocalServerList().getSelectedIndex() < 0) {
				MapTool.showError("Must select a server");
				return;
			}
			
			// OK
			ServerInfo info = (ServerInfo) getLocalServerList().getSelectedValue();
			port = info.port;
			hostname = info.address.getHostAddress();
			
		}
		if (SwingUtil.hasComponent(selectedPanel, "directPanel")) {

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
			hostname = getHostTextField().getText();
		}
		if (SwingUtil.hasComponent(selectedPanel, "rptoolsPanel")) {
			if (getServerNameTextField().getText().length() == 0) {
				MapTool.showError("Must supply a server name");
				return;
			}
			
			// Do the lookup
			String serverName = getServerNameTextField().getText();
			String serverInfo = MapToolRegistry.findInstance(serverName);
			if (serverInfo == null || serverInfo.length() == 0) {
				MapTool.showError("Could not find server: " + serverName);
				return;
			}
			
			String[] data = serverInfo.split(":");
			hostname = data[0];
			port = Integer.parseInt(data[1]);
		}
		
		if (commit()) {
			accepted = true;
			dialog.closeDialog();
		}
	}
	
	@Override
	public boolean commit() {
		
		ConnectToServerDialogPreferences prefs = new ConnectToServerDialogPreferences();
		
		// Not bindable .. yet
		prefs.setTab(getTabPane().getSelectedIndex());

		return super.commit();
	}

	private static class RemoteServerTableModel extends AbstractTableModel {

		private List<String[]> data;
		
		public RemoteServerTableModel(List<String> encodedData) {

			// Simple but sufficient
			Collections.sort(encodedData, String.CASE_INSENSITIVE_ORDER);
			
			data = new ArrayList<String[]>(encodedData.size());
			for (String line : encodedData) {
				String[] row = line.split(":");
				
				if (row.length == 1) {
					row = new String[]{row[0], "Pre 1.3"};
				}
				
				data.add(row);
			}
			
		}

		@Override
		public String getColumnName(int column) {
			switch(column) {
			case 0: return "Server Name";
			case 1: return "Version";
			}
			return "";
		}
		
		public int getColumnCount() {
			return 2;
		}
		public int getRowCount() {
			return data.size();
		}
		public Object getValueAt(int rowIndex, int columnIndex) {
			
			String[] row = data.get(rowIndex);
			
			return row[columnIndex];
		}
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
