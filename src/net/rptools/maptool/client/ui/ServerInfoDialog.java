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

import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.net.InetAddress;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;

import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolRegistry;
import net.rptools.maptool.server.MapToolServer;

import com.jeta.forms.components.panel.FormPanel;

public class ServerInfoDialog extends JDialog {

	private JLabel externalAddressLabel; 
	
	/**
	 * This is the default constructor
	 */
	public ServerInfoDialog(MapToolServer server) {
		super(MapTool.getFrame(), "Server Info", true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(250, 200);

		FormPanel panel = new FormPanel("net/rptools/maptool/client/ui/forms/serverInfoDialog.jfrm");
		
		JLabel nameLabel = panel.getLabel("name");
		JLabel localAddressLabel = panel.getLabel("localAddress");
		JLabel portLabel = panel.getLabel("port");
		externalAddressLabel = panel.getLabel("externalAddress");
		
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
