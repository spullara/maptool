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
package net.rptools.maptool.client;

import javax.swing.JDialog;

import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import javax.swing.JButton;

import net.rptools.maptool.client.swing.SwingUtil;
import net.rptools.maptool.server.MapToolServer;
import javax.swing.JComboBox;
/**
 * @author trevor
 */
public class ConnectToServerDialog extends JDialog {

	public static final int OPTION_OK = 0;
	public static final int OPTION_CANCEL = 1;

	private javax.swing.JPanel jContentPane = null;
	private JLabel jLabel = null;
	private JTextField usernameTextField = null;
	private JLabel jLabel1 = null;
	private JLabel jLabel2 = null;
	private JPanel jPanel = null;
	private JPanel jPanel1 = null;
	private JButton cancelButton = null;
	private JButton okButton = null;
	private JTextField serverTextField = null;
	private JTextField portTextField = null;
	
	private int option;
	
	private JLabel jLabel3 = null;
	private JComboBox roleComboBox = null;
	/**
	 * This is the default constructor
	 */
	public ConnectToServerDialog() {
		super(MapToolClient.getInstance(), "Connect to Server", true);
		initialize();
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 186);
		this.setContentPane(getJContentPane());
		
		portTextField.setText(Integer.toString(MapToolServer.DEFAULT_PORT));
		getRootPane().setDefaultButton(okButton);
	}
	/* (non-Javadoc)
	 * @see java.awt.Component#setVisible(boolean)
	 */
	public void setVisible(boolean b) {
		
		if (b) {
			SwingUtil.centerOver(this, MapToolClient.getInstance());
		}
		super.setVisible(b);
	}
	
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJContentPane() {
		if(jContentPane == null) {
			jLabel3 = new JLabel();
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			jLabel2 = new JLabel();
			jLabel1 = new JLabel();
			jLabel = new JLabel();
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
			jContentPane = new javax.swing.JPanel();
			jContentPane.setLayout(new GridBagLayout());
			gridBagConstraints12.gridx = 0;
			gridBagConstraints12.gridy = 0;
			gridBagConstraints12.weightx = 0.5D;
			gridBagConstraints12.anchor = java.awt.GridBagConstraints.WEST;
			jLabel.setText("Username:");
			gridBagConstraints13.gridx = 1;
			gridBagConstraints13.gridy = 0;
			gridBagConstraints13.weightx = 1.0;
			gridBagConstraints13.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints14.gridx = 0;
			gridBagConstraints14.gridy = 1;
			gridBagConstraints14.anchor = java.awt.GridBagConstraints.WEST;
			jLabel1.setText("Server:");
			gridBagConstraints15.gridx = 0;
			gridBagConstraints15.gridy = 2;
			gridBagConstraints15.anchor = java.awt.GridBagConstraints.WEST;
			jLabel2.setText("Port:");
			jContentPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(10,10,5,10));
			gridBagConstraints16.gridx = 0;
			gridBagConstraints16.gridy = 6;
			gridBagConstraints16.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints16.gridwidth = 2;
			gridBagConstraints16.weighty = 1.0D;
			gridBagConstraints17.gridx = 0;
			gridBagConstraints17.gridy = 7;
			gridBagConstraints17.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints17.gridwidth = 2;
			gridBagConstraints19.gridx = 1;
			gridBagConstraints19.gridy = 1;
			gridBagConstraints19.weightx = 1.0;
			gridBagConstraints19.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints20.gridx = 1;
			gridBagConstraints20.gridy = 2;
			gridBagConstraints20.weightx = 1.0;
			gridBagConstraints20.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.gridy = 5;
			gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
			jLabel3.setText("Role:");
			gridBagConstraints4.gridx = 1;
			gridBagConstraints4.gridy = 5;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.fill = java.awt.GridBagConstraints.NONE;
			gridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;
			jContentPane.add(jLabel, gridBagConstraints12);
			jContentPane.add(getUsernameTextField(), gridBagConstraints13);
			jContentPane.add(jLabel1, gridBagConstraints14);
			jContentPane.add(jLabel2, gridBagConstraints15);
			jContentPane.add(getJPanel(), gridBagConstraints16);
			jContentPane.add(getJPanel1(), gridBagConstraints17);
			jContentPane.add(getServerTextField(), gridBagConstraints19);
			jContentPane.add(getPortTextField(), gridBagConstraints20);
			jContentPane.add(jLabel3, gridBagConstraints3);
			jContentPane.add(getRoleComboBox(), gridBagConstraints4);
		}
		return jContentPane;
	}
	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getUsernameTextField() {
		if (usernameTextField == null) {
			usernameTextField = new JTextField();
		}
		return usernameTextField;
	}
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
		}
		return jPanel;
	}
	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			FlowLayout flowLayout18 = new FlowLayout();
			jPanel1 = new JPanel();
			jPanel1.setLayout(flowLayout18);
			flowLayout18.setAlignment(java.awt.FlowLayout.RIGHT);
			jPanel1.add(getOkButton(), null);
			jPanel1.add(getCancelButton(), null);
		}
		return jPanel1;
	}
	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setText("Cancel");
			cancelButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					option = OPTION_CANCEL;
					setVisible(false);
				}
			});
		}
		return cancelButton;
	}
	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.setText("Ok");
			okButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					
					// TODO: put these into a validation method
					if (portTextField.getText().length() == 0) {
						MapToolClient.showError("Must supply a port");
						return;
					}
					try {
						Integer.parseInt(portTextField.getText());
					} catch (NumberFormatException nfe) {
						MapToolClient.showError("Port must be numeric");
						return;
					}

					if (usernameTextField.getText().length() == 0) {
						MapToolClient.showError("Must supply a username");
						return;
					}					

					if (serverTextField.getText().length() == 0) {
						MapToolClient.showError("Must supply a server");
						return;
					}					

					option = OPTION_OK;
					setVisible(false);
				}
			});
		}
		return okButton;
	}
	/**
	 * This method initializes jTextField1	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getServerTextField() {
		if (serverTextField == null) {
			serverTextField = new JTextField();
		}
		return serverTextField;
	}
	/**
	 * This method initializes jTextField2	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getPortTextField() {
		if (portTextField == null) {
			portTextField = new JTextField();
		}
		return portTextField;
	}
	
	public int getOption() {
		return option;
	}
	
	public String getUsername() {
		return usernameTextField.getText();
	}
	
	public String getServer() {
		return serverTextField.getText();
	}
	
	public int getPort() {
		return Integer.parseInt(portTextField.getText());
	}
	
	public int getRole() {
		// LATER: This is kinda hacky, it assumes the order of the 
		// options are the value of the role, which may not always be true
		return roleComboBox.getSelectedIndex();
	}
	
	/**
	 * This method initializes jComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */    
	private JComboBox getRoleComboBox() {
		if (roleComboBox == null) {
			roleComboBox = new JComboBox(new String[]{"Player", "GM"});
		}
		return roleComboBox;
	}
        }  //  @jve:decl-index=0:visual-constraint="10,10"
