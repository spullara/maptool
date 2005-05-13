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

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.swing.SwingUtil;
import net.rptools.maptool.server.MapToolServer;
/**
 * @author trevor
 */
public class StartServerDialog extends JDialog {

	public static final int OPTION_OK = 0;
	public static final int OPTION_CANCEL = 1;
	
	private javax.swing.JPanel jContentPane = null;
	private JLabel jLabel = null;
	private JTextField portTextField = null;
	private JTextField usernameTextField = null;
	private JLabel jLabel2 = null;
	private JLabel jLabel3 = null;
	private JTextField gmPasswordTextField = null;
	private JTextField playerPasswordTextField = null;
	private JPanel jPanel = null;
	private JPanel jPanel1 = null;
	private JButton cancelButton = null;
	private JButton okButton = null;
	
	private int option;
	
	private JLabel jLabel1 = null;
	/**
	 * This is the default constructor
	 */
	public StartServerDialog() {
		super(MapTool.getFrame(), "Start Server", true);
		initialize();
		
		portTextField.setText(Integer.toString(MapToolServer.DEFAULT_PORT));
		getRootPane().setDefaultButton(okButton);
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(303, 190);
		this.setContentPane(getJContentPane());
		
	}
	
	/* (non-Javadoc)
	 * @see java.awt.Component#setVisible(boolean)
	 */
	public void setVisible(boolean b) {
		
		if (b) {
			SwingUtil.centerOver(this, MapTool.getFrame());
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
			jLabel1 = new JLabel();
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			jLabel3 = new JLabel();
			jLabel2 = new JLabel();
			jLabel = new JLabel();
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			jContentPane = new javax.swing.JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(10,10,5,10));
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.weightx = 0.5D;
			gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
			jLabel.setText("Username:");
			gridBagConstraints4.gridx = 1;
			gridBagConstraints4.gridy = 0;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.gridy = 2;
			gridBagConstraints5.anchor = java.awt.GridBagConstraints.WEST;
			jLabel2.setText("GM Password:");
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.gridy = 3;
			gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;
			jLabel3.setText("Player Password:");
			gridBagConstraints7.gridx = 1;
			gridBagConstraints7.gridy = 2;
			gridBagConstraints7.weightx = 1.0;
			gridBagConstraints7.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints8.gridx = 1;
			gridBagConstraints8.gridy = 3;
			gridBagConstraints8.weightx = 1.0;
			gridBagConstraints8.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints9.gridx = 0;
			gridBagConstraints9.gridy = 4;
			gridBagConstraints9.gridwidth = 2;
			gridBagConstraints9.weighty = 1.0D;
			gridBagConstraints9.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints10.gridx = 0;
			gridBagConstraints10.gridy = 5;
			gridBagConstraints10.gridwidth = 2;
			gridBagConstraints10.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints11.gridx = 1;
			gridBagConstraints11.gridy = 1;
			gridBagConstraints11.weightx = 1.0;
			gridBagConstraints11.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = 1;
			gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
			jLabel1.setText("Port:");
			jContentPane.add(jLabel, gridBagConstraints1);
			jContentPane.add(getUsernameTextField(), gridBagConstraints4);
			jContentPane.add(jLabel2, gridBagConstraints5);
			jContentPane.add(jLabel3, gridBagConstraints6);
			jContentPane.add(getGmPasswordTextField(), gridBagConstraints7);
			jContentPane.add(getPlayerPasswordTextField(), gridBagConstraints8);
			jContentPane.add(getJPanel(), gridBagConstraints9);
			jContentPane.add(getJPanel1(), gridBagConstraints10);
			jContentPane.add(getPortTextField(), gridBagConstraints11);
			jContentPane.add(jLabel1, gridBagConstraints2);
		}
		return jContentPane;
	}
	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getPortTextField() {
		if (portTextField == null) {
			portTextField = new JTextField();
		}
		return portTextField;
	}
	/**
	 * This method initializes jTextField1	
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
	 * This method initializes jTextField2	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getGmPasswordTextField() {
		if (gmPasswordTextField == null) {
			gmPasswordTextField = new JTextField();
			gmPasswordTextField.setEnabled(false);
			gmPasswordTextField.setEditable(false);
		}
		return gmPasswordTextField;
	}
	/**
	 * This method initializes jTextField3	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getPlayerPasswordTextField() {
		if (playerPasswordTextField == null) {
			playerPasswordTextField = new JTextField();
			playerPasswordTextField.setEnabled(false);
			playerPasswordTextField.setEditable(false);
		}
		return playerPasswordTextField;
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
			FlowLayout flowLayout11 = new FlowLayout();
			jPanel1 = new JPanel();
			jPanel1.setLayout(flowLayout11);
			flowLayout11.setAlignment(java.awt.FlowLayout.RIGHT);
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
						MapTool.showError("Must supply a port");
						return;
					}
					try {
						Integer.parseInt(portTextField.getText());
					} catch (NumberFormatException nfe) {
						MapTool.showError("Port must be numeric");
						return;
					}

					if (usernameTextField.getText().length() == 0) {
						MapTool.showError("Must supply a username");
						return;
					}
					
					option = OPTION_OK;
					setVisible(false);
				}
			});
		}
		return okButton;
	}
	
	public int getOption() {
		return option;
	}
	
	public int getPort() {
		return Integer.parseInt(portTextField.getText());
	}
	
	public String getUsername() {
		return usernameTextField.getText();
	}
	
   }  //  @jve:decl-index=0:visual-constraint="7,8"
