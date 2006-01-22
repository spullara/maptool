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
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.rptools.lib.service.EchoServer;
import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolRegistry;
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
	private JPasswordField gmPasswordTextField = null;
	private JPasswordField playerPasswordTextField = null;
	private JPanel jPanel = null;
	private JPanel jPanel1 = null;
	private JButton cancelButton = null;
	private JButton okButton = null;
	
	private int option;
	
	private JLabel jLabel1 = null;
	private JCheckBox playerPasswordCheckBox = null;
	private JCheckBox gmPasswordCheckBox = null;
	private JCheckBox strictTokenMovementCheckBox = null;
	private JLabel roleLabel = null;
	private JComboBox roleComboBox = null;
	private JPanel optionsPanel = null;
	private JCheckBox registerCheckBox = null;
	private JLabel channelLabel = null;
	private JLabel serverPasswordLabel = null;
	private JTextField serverNameTextField = null;
	private JTextField serverPasswordTextField = null;
	private JLabel spacerLabel = null;
	private JLabel spacerLabel1 = null;
	private JLabel spacerLabel2 = null;
	private JButton testRegisterButton = null;
	/**
	 * This is the default constructor
	 */
	public StartServerDialog() {
		super(MapTool.getFrame(), "Start Server", true);
		initialize();
		
		getRootPane().setDefaultButton(okButton);
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(348, 381);
		this.setTitle("Start Server");
		this.setContentPane(getJContentPane());
		
		// Prefs
		StartServerDialogPreferences prefs = new StartServerDialogPreferences();
		usernameTextField.setText(prefs.getUsername());
		portTextField.setText(Integer.toString(prefs.getPort()));
		gmPasswordCheckBox.setSelected(prefs.getUseGMPassword());
		gmPasswordTextField.setText(prefs.getGMPassword());
		playerPasswordCheckBox.setSelected(prefs.getUsePlayerPassword());
		playerPasswordTextField.setText(prefs.getPlayerPassword());
		strictTokenMovementCheckBox.setSelected(prefs.useStrictTokenMovement());
		getPlayerPasswordTextField().setEnabled(playerPasswordCheckBox.isSelected());
		getGmPasswordTextField().setEnabled(gmPasswordCheckBox.isSelected());
		roleComboBox.setSelectedIndex(prefs.getRole());
		getRegisterCheckBox().setSelected(prefs.registerServer());
		getServerNameTextField().setText(prefs.getServerName());
		getServerPasswordTextField().setText(prefs.getServerPassword());

		getServerNameTextField().setEnabled(prefs.registerServer());
		getServerPasswordTextField().setEnabled(prefs.registerServer());
		getTestRegisterButton().setEnabled(registerCheckBox.isSelected());
	}
	
	public int getRole() {
		// LATER: This is kinda hacky, it assumes the order of the 
		// options are the value of the role, which may not always be true
		return roleComboBox.getSelectedIndex();
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
			GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
			gridBagConstraints22.gridx = 2;
			gridBagConstraints22.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints22.gridy = 11;
			GridBagConstraints gridBagConstraints111 = new GridBagConstraints();
			gridBagConstraints111.gridx = 0;
			gridBagConstraints111.gridy = 3;
			spacerLabel2 = new JLabel();
			spacerLabel2.setText("   ");
			GridBagConstraints gridBagConstraints101 = new GridBagConstraints();
			gridBagConstraints101.gridx = 0;
			gridBagConstraints101.gridy = 12;
			spacerLabel1 = new JLabel();
			spacerLabel1.setText("    ");
			GridBagConstraints gridBagConstraints91 = new GridBagConstraints();
			gridBagConstraints91.gridx = 0;
			gridBagConstraints91.gridy = 6;
			spacerLabel = new JLabel();
			spacerLabel.setText("    ");
			GridBagConstraints gridBagConstraints81 = new GridBagConstraints();
			gridBagConstraints81.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints81.gridy = 10;
			gridBagConstraints81.weightx = 1.0;
			gridBagConstraints81.gridx = 2;
			GridBagConstraints gridBagConstraints71 = new GridBagConstraints();
			gridBagConstraints71.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints71.gridy = 9;
			gridBagConstraints71.weightx = 1.0;
			gridBagConstraints71.gridx = 2;
			GridBagConstraints gridBagConstraints51 = new GridBagConstraints();
			gridBagConstraints51.gridx = 0;
			gridBagConstraints51.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints51.insets = new java.awt.Insets(0,0,4,0);
			gridBagConstraints51.gridy = 10;
			serverPasswordLabel = new JLabel();
			serverPasswordLabel.setText("Password:");
			GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
			gridBagConstraints41.gridx = 0;
			gridBagConstraints41.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints41.insets = new java.awt.Insets(0,0,4,0);
			gridBagConstraints41.gridy = 9;
			channelLabel = new JLabel();
			channelLabel.setText("Server Name:");
			GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
			gridBagConstraints31.gridx = 0;
			gridBagConstraints31.gridwidth = 3;
			gridBagConstraints31.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints31.gridy = 7;
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.fill = java.awt.GridBagConstraints.NONE;
			gridBagConstraints21.gridy = 1;
			gridBagConstraints21.weightx = 1.0;
			gridBagConstraints21.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints21.gridx = 2;
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.gridx = 0;
			gridBagConstraints13.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints13.gridy = 1;
			roleLabel = new JLabel();
			roleLabel.setText("Role:");
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 1;
			gridBagConstraints3.gridy = 4;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 5;
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
			gridBagConstraints1.weightx = 0.0D;
			gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
			jLabel.setText("Username:");
			gridBagConstraints4.gridx = 2;
			gridBagConstraints4.gridy = 0;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.gridy = 4;
			gridBagConstraints5.anchor = java.awt.GridBagConstraints.WEST;
			jLabel2.setText("GM Password:");
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.insets = new java.awt.Insets(0,0,0,5);
			gridBagConstraints6.gridy = 5;
			gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;
			jLabel3.setText("Player Password:");
			gridBagConstraints7.gridx = 2;
			gridBagConstraints7.gridy = 4;
			gridBagConstraints7.weightx = 1.0;
			gridBagConstraints7.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints8.gridx = 2;
			gridBagConstraints8.gridy = 5;
			gridBagConstraints8.weightx = 1.0;
			gridBagConstraints8.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints9.gridx = 0;
			gridBagConstraints9.insets = new java.awt.Insets(5,0,0,0);
			gridBagConstraints9.gridy = 13;
			gridBagConstraints9.gridwidth = 3;
			gridBagConstraints9.weighty = 1.0D;
			gridBagConstraints9.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints10.gridx = 0;
			gridBagConstraints10.gridy = 14;
			gridBagConstraints10.gridwidth = 3;
			gridBagConstraints10.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints11.gridx = 2;
			gridBagConstraints11.gridy = 2;
			gridBagConstraints11.weightx = 1.0;
			gridBagConstraints11.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = 2;
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
			jContentPane.add(getPlayerPasswordCheckBox(), gridBagConstraints);
			jContentPane.add(getGmPasswordCheckBox(), gridBagConstraints3);
			jContentPane.add(roleLabel, gridBagConstraints13);
			jContentPane.add(getRoleComboBox(), gridBagConstraints21);
			jContentPane.add(getRegisterCheckBox(), gridBagConstraints31);
			jContentPane.add(channelLabel, gridBagConstraints41);
			jContentPane.add(serverPasswordLabel, gridBagConstraints51);
			jContentPane.add(getServerNameTextField(), gridBagConstraints71);
			jContentPane.add(getServerPasswordTextField(), gridBagConstraints81);
			jContentPane.add(spacerLabel, gridBagConstraints91);
			jContentPane.add(spacerLabel1, gridBagConstraints101);
			jContentPane.add(spacerLabel2, gridBagConstraints111);
			jContentPane.add(getTestRegisterButton(), gridBagConstraints22);
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
			gmPasswordTextField = new JPasswordField();
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
			playerPasswordTextField = new JPasswordField();
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
			jPanel.setLayout(new BoxLayout(getJPanel(), BoxLayout.Y_AXIS));
			jPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Options", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
			jPanel.add(getOptionsPanel(), null);
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
					
					// Prefs
					StartServerDialogPreferences prefs = new StartServerDialogPreferences();
					prefs.setUsername(getUsername());
					prefs.setPort(getPort());
					prefs.setUseGMPassword(getGmPasswordCheckBox().isSelected());
					prefs.setGMPassword(getGmPasswordTextField().getText());
					prefs.setUsePlayerPassword(getPlayerPasswordCheckBox().isSelected());
					prefs.setPlayerPassword(getPlayerPasswordTextField().getText());
					prefs.setStrictTokenMovement(getStrictTokenMovementCheckBox().isSelected());
					prefs.setRole(getRole());
					prefs.setRegisterServer(getRegisterCheckBox().isSelected());
					prefs.setServerName(getServerNameTextField().getText());
					prefs.setServerPassword(getServerPasswordTextField().getText());
				}
			});
		}
		return okButton;
	}
	
	public int getOption() {
		return option;
	}
	
	public boolean useStrictTokenMovement() {
		return getStrictTokenMovementCheckBox().isSelected();
	}
	
	public int getPort() {
		return Integer.parseInt(portTextField.getText());
	}
	
	public String getUsername() {
		return usernameTextField.getText();
	}

	public String getGMPassword() {
		return getGmPasswordCheckBox().isSelected() ? getGmPasswordTextField().getText() : "";
	}
	
	public String getPlayerPassword() {
		return getPlayerPasswordCheckBox().isSelected() ? getPlayerPasswordTextField().getText() : "";
	}
	
	public boolean registerServer() {
		return getRegisterCheckBox().isSelected();
	}
	
	public String getServerName() {
		return getServerNameTextField().getText();
	}
	
	public String getServerPassword() {
		return getServerPasswordTextField().getText();
	}
	
	/**
	 * This method initializes playerPasswordCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getPlayerPasswordCheckBox() {
		if (playerPasswordCheckBox == null) {
			playerPasswordCheckBox = new JCheckBox();
			playerPasswordCheckBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getPlayerPasswordTextField().setEnabled(playerPasswordCheckBox.isSelected());
				}
			});
		}
		return playerPasswordCheckBox;
	}
	
	/**
	 * This method initializes gmPasswordCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getGmPasswordCheckBox() {
		if (gmPasswordCheckBox == null) {
			gmPasswordCheckBox = new JCheckBox();
			gmPasswordCheckBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getGmPasswordTextField().setEnabled(gmPasswordCheckBox.isSelected());
				}
			});
		}
		return gmPasswordCheckBox;
	}

	/**
	 * This method initializes strictTokenMovementCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getStrictTokenMovementCheckBox() {
		if (strictTokenMovementCheckBox == null) {
			strictTokenMovementCheckBox = new JCheckBox();
			strictTokenMovementCheckBox.setText("Strict Token Management");
		}
		return strictTokenMovementCheckBox;
	}

	/**
	 * This method initializes roleComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getRoleComboBox() {
		if (roleComboBox == null) {
			roleComboBox = new JComboBox(new String[]{"Player", "GM"});
		}
		return roleComboBox;
	}

	/**
	 * This method initializes optionsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getOptionsPanel() {
		if (optionsPanel == null) {
			optionsPanel = new JPanel();
			optionsPanel.setLayout(new BoxLayout(getOptionsPanel(), BoxLayout.Y_AXIS));
			optionsPanel.add(getStrictTokenMovementCheckBox(), null);
		}
		return optionsPanel;
	}

	/**
	 * This method initializes registerCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getRegisterCheckBox() {
		if (registerCheckBox == null) {
			registerCheckBox = new JCheckBox();
			registerCheckBox.setText("Register with RPTools.net");
			registerCheckBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					getServerNameTextField().setEnabled(registerCheckBox.isSelected());
					getServerPasswordTextField().setEnabled(registerCheckBox.isSelected());
					getTestRegisterButton().setEnabled(registerCheckBox.isSelected());
				}
			});
		}
		return registerCheckBox;
	}

	/**
	 * This method initializes serverNameTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getServerNameTextField() {
		if (serverNameTextField == null) {
			serverNameTextField = new JTextField();
		}
		return serverNameTextField;
	}

	/**
	 * This method initializes serverPasswordTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getServerPasswordTextField() {
		if (serverPasswordTextField == null) {
			serverPasswordTextField = new JTextField();
		}
		return serverPasswordTextField;
	}

	/**
	 * This method initializes testRegisterButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getTestRegisterButton() {
		if (testRegisterButton == null) {
			testRegisterButton = new JButton();
			testRegisterButton.setText("Test Connection");
			testRegisterButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
					new Thread(new Runnable() {
						
						public void run() {
							
							EchoServer server = null;
							try {
								// Create a temporary server that will listen on the port we
								// want to start MapTool on.  This provides two things: First
								// it tells us we can open that port, second it creates a way
								// for the connection test service to call back and verify it is
								// the type of service we want.
								// LATER: Extend EchoServer to do something more than just parrot the input
								server = new EchoServer(getPort());
								server.start();
								
								if (MapToolRegistry.testConnection(getPort())) {
									MapTool.showInformation("Success!");
								} else {
									MapTool.showError("Could not see your computer from the internet");
								}
							} catch (Exception e) {
								e.printStackTrace();
								MapTool.showError("Unable to see your computer from the internet");
							} finally {
								// Need to make sure it dies so that it doesn't keep the port open ...
								// we're going to need it very soon !
								if (server != null) {
									server.stop();
								}
							}
						}
					}).start();
				}
			});
		}
		return testRegisterButton;
	}
	
   }  //  @jve:decl-index=0:visual-constraint="1,22"
