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

import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.net.InetAddress;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolRegistry;
import net.rptools.maptool.server.MapToolServer;

import com.jeta.forms.components.panel.FormPanel;

public class ServerInfoDialog extends JDialog {

	private JTextField externalAddressLabel; 
	
	/**
	 * This is the default constructor
	 */
	public ServerInfoDialog(MapToolServer server) {
		super(MapTool.getFrame(), "Server Info", true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(275, 200);

		FormPanel panel = new FormPanel("net/rptools/maptool/client/ui/forms/serverInfoDialog.jfrm");
		
		JTextField nameLabel = panel.getTextField("name");
		JTextField localAddressLabel = panel.getTextField("localAddress");
		JTextField portLabel = panel.getTextField("port");
		externalAddressLabel = panel.getTextField("externalAddress");
		
		String name = server.getConfig().getServerName();
		if (name == null) {
			name = "---";
		}
		
		String localAddress = "Unknown";
		try {
			InetAddress localAddy = InetAddress.getLocalHost();
			localAddress = localAddy.getHostAddress();
		} catch (Exception e) {
			// none
		}
		
		String externalAddress = "Discovering ...";
		String port = MapTool.isPersonalServer() ? "---" : Integer.toString(server.getConfig().getPort());

		nameLabel.setText(name);
		localAddressLabel.setText(localAddress);
		externalAddressLabel.setText(externalAddress);
		portLabel.setText(port);
		
		JButton okButton = (JButton) panel.getButton("okButton");
		bindOKButtonActions(okButton);

		setLayout(new GridLayout());
		((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		add(panel);
		
		new Thread(new ExternalAddressFinder()).start();
	}
	
	@Override
	public void setVisible(boolean b) {

		if (b) {
			SwingUtil.centerOver(this, MapTool.getFrame());
		}
		super.setVisible(b);
	}

	/**
	 * This method initializes okButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private void bindOKButtonActions(JButton okButton) {

		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				setVisible(false);
			}
		});
	}
	
	private class ExternalAddressFinder implements Runnable {
		public void run() {
			
			String address = "Unknown";
			try {
				address = MapToolRegistry.getAddress();
			} catch (Exception e) {
				// Oh well, might not be connected
			}

			final String addy = address;
			EventQueue.invokeLater(new Runnable(){
				public void run() {
					externalAddressLabel.setText(addy);
				}
			});
		}
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
