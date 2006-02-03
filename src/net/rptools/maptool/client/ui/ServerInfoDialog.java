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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.net.InetAddress;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.rptools.maptool.server.MapToolServer;

public class ServerInfoDialog extends JDialog {

	private JPanel jContentPane = null;
	private JLabel nameLabel = null;
	private JLabel nameDetailLabel = null;
	private JLabel localAddressLabel = null;
	private JLabel localAddressDetailLabel = null;
	private JLabel externalAddressLabel = null;
	private JLabel externalAddressDetailLabel = null;
	private JLabel spacerLabel = null;
	private JPanel buttonPanel = null;
	private JButton okButton = null;
	/**
	 * This is the default constructor
	 */
	public ServerInfoDialog(MapToolServer server) {
		super();
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
		
		nameDetailLabel.setText(name);
		localAddressDetailLabel.setText(localAddress);
		externalAddressDetailLabel.setText(externalAddress);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 173);
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
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 0;
			gridBagConstraints7.gridwidth = 3;
			gridBagConstraints7.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints7.gridy = 4;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 2;
			gridBagConstraints6.weighty = 1.0;
			gridBagConstraints6.gridy = 3;
			spacerLabel = new JLabel();
			spacerLabel.setText("   ");
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 2;
			gridBagConstraints5.weightx = 1.0;
			gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.insets = new java.awt.Insets(0,0,5,0);
			gridBagConstraints5.gridy = 2;
			externalAddressDetailLabel = new JLabel();
			externalAddressDetailLabel.setText("   ");
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints4.insets = new java.awt.Insets(0,0,5,5);
			gridBagConstraints4.gridy = 2;
			externalAddressLabel = new JLabel();
			externalAddressLabel.setText("External Address:");
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 2;
			gridBagConstraints3.weightx = 1.0;
			gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.insets = new java.awt.Insets(0,0,5,0);
			gridBagConstraints3.gridy = 1;
			localAddressDetailLabel = new JLabel();
			localAddressDetailLabel.setText("   ");
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints2.insets = new java.awt.Insets(0,0,5,0);
			gridBagConstraints2.gridy = 1;
			localAddressLabel = new JLabel();
			localAddressLabel.setText("Local Address:");
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 2;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.insets = new java.awt.Insets(0,0,5,0);
			gridBagConstraints1.gridy = 0;
			nameDetailLabel = new JLabel();
			nameDetailLabel.setText("   ");
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints.insets = new java.awt.Insets(0,0,5,0);
			gridBagConstraints.gridy = 0;
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
			jContentPane.add(spacerLabel, gridBagConstraints6);
			jContentPane.add(getButtonPanel(), gridBagConstraints7);
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

}  //  @jve:decl-index=0:visual-constraint="10,10"
