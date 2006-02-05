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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.net.InetAddress;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolRegistry;
import net.rptools.maptool.server.MapToolServer;

public class ServerInfoDialog extends JDialog {

	private JPanel jContentPane = null;
	private JLabel nameLabel = null;
	private JLabel nameDetailLabel = null;
	private JLabel localAddressLabel = null;
	private JLabel localAddressDetailLabel = null;
	private JLabel externalAddressLabel = null;
	private JLabel externalAddressDetailLabel = null;
	private JPanel buttonPanel = null;
	private JButton okButton = null;
	private JLabel titleLabel = null;
	private JLabel spacerLabel2 = null;
	/**
	 * This is the default constructor
	 */
	public ServerInfoDialog(MapToolServer server) {
		super(MapTool.getFrame(), "Server Info", true);
		initialize();
		
		String name = server.getConfig().getServerName();
		if (name == null) {
			name = "None";
		}
		
		String localAddress = "Unknown";
		try {
			InetAddress localAddy = InetAddress.getLocalHost();
			localAddress = localAddy.getHostAddress();
		} catch (Exception e) {
			// none
		}
		
		String externalAddress = "Discovering ...";
		new Thread(new ExternalAddressFinder()).start();
		
		nameDetailLabel.setText(name);
		localAddressDetailLabel.setText(localAddress);
		externalAddressDetailLabel.setText(externalAddress);
	}
	
	@Override
	public void setVisible(boolean b) {

		if (b) {
			SwingUtil.centerOver(this, MapTool.getFrame());
		}
		super.setVisible(b);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 221);
		this.setTitle("Server Info");
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.gridx = 2;
			gridBagConstraints21.gridy = 1;
			spacerLabel2 = new JLabel();
			spacerLabel2.setText("   ");
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.gridwidth = 3;
			gridBagConstraints11.insets = new java.awt.Insets(0,0,5,0);
			gridBagConstraints11.gridy = 0;
			titleLabel = new JLabel();
			titleLabel.setText("Server Info");
			titleLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 0;
			gridBagConstraints7.gridwidth = 3;
			gridBagConstraints7.anchor = java.awt.GridBagConstraints.SOUTHEAST;
			gridBagConstraints7.weighty = 1.0;
			gridBagConstraints7.gridy = 6;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 2;
			gridBagConstraints5.weightx = 1.0;
			gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.insets = new java.awt.Insets(0,0,5,0);
			gridBagConstraints5.gridy = 4;
			externalAddressDetailLabel = new JLabel();
			externalAddressDetailLabel.setText("   ");
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints4.insets = new java.awt.Insets(0,0,5,5);
			gridBagConstraints4.gridy = 4;
			externalAddressLabel = new JLabel();
			externalAddressLabel.setText("External Address:");
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 2;
			gridBagConstraints3.weightx = 1.0;
			gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.insets = new java.awt.Insets(0,0,5,0);
			gridBagConstraints3.gridy = 3;
			localAddressDetailLabel = new JLabel();
			localAddressDetailLabel.setText("   ");
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints2.insets = new java.awt.Insets(0,0,5,0);
			gridBagConstraints2.gridy = 3;
			localAddressLabel = new JLabel();
			localAddressLabel.setText("Local Address:");
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 2;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.insets = new java.awt.Insets(0,0,5,0);
			gridBagConstraints1.gridy = 2;
			nameDetailLabel = new JLabel();
			nameDetailLabel.setText("   ");
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints.insets = new java.awt.Insets(0,0,5,0);
			gridBagConstraints.gridy = 2;
			nameLabel = new JLabel();
			nameLabel.setText("Name:");
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(10,10,10,10));
			jContentPane.add(nameLabel, gridBagConstraints);
			jContentPane.add(nameDetailLabel, gridBagConstraints1);
			jContentPane.add(localAddressLabel, gridBagConstraints2);
			jContentPane.add(localAddressDetailLabel, gridBagConstraints3);
			jContentPane.add(externalAddressLabel, gridBagConstraints4);
			jContentPane.add(externalAddressDetailLabel, gridBagConstraints5);
			jContentPane.add(getButtonPanel(), gridBagConstraints7);
			jContentPane.add(titleLabel, gridBagConstraints11);
			jContentPane.add(spacerLabel2, gridBagConstraints21);
		}
		return jContentPane;
	}

	/**
	 * This method initializes buttonPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.add(getOkButton(), null);
		}
		return buttonPanel;
	}

	/**
	 * This method initializes okButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.setText("OK");
			okButton.addActionListener(new ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setVisible(false);
				}
			});
		}
		return okButton;
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
					externalAddressDetailLabel.setText(addy);
				}
			});
		}
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
