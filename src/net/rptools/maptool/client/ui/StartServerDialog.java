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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import net.rptools.lib.service.EchoServer;
import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolRegistry;
import net.rptools.maptool.client.swing.AbeilleDialog;
/**
 * @author trevor
 */
public class StartServerDialog extends AbeilleDialog {

	public static final int OPTION_OK = 0;
	public static final int OPTION_CANCEL = 1;
	
	private int option;

	private StartServerDialogPreferences prefs = new StartServerDialogPreferences();

	public StartServerDialog() {
		super("net/rptools/maptool/client/ui/forms/startServerDialog.jfrm", MapTool.getFrame(), "Start Server", true);
		setTitle("Start Server");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		init();
		
		pack();
	}
	
	public int getRole() {
		// LATER: This is kinda hacky, it assumes the order of the 
		// options are the value of the role, which may not always be true
		return getRoleCombo().getSelectedIndex();
	}
	
	public int getPort() {
		
		try {
			return Integer.parseInt(getPortTextField().getText());
		} catch (NumberFormatException nfe) {
			return -1;
		}
	}

	public int getOption() {
		return option;
	}

	public JTextField getUsernameTextField() {
		return (JTextField) getComponent("usernameField");
	}
	
	public JTextField getPortTextField() {
		return (JTextField) getComponent("portField");
	}

	public JTextField getGMPasswordTextField() {
		return (JTextField) getComponent("gmPasswordField");
	}
	
	public JTextField getPlayerPasswordTextField() {
		return (JTextField) getComponent("playerPasswordField");
	}
	
	public JTextField getRPToolsNameTextField() {
		return (JTextField) getComponent("rptoolsNameField");
	}

	public JButton getOKButton() {
		return (JButton) getComponent("okButton");
	}
	
	public JButton getCancelButton() {
		return (JButton) getComponent("cancelButton");
	}

	public JComboBox getRoleCombo() {
		return (JComboBox) getComponent("roleCombo");
	}
	
	public JCheckBox getUseStrictOwnershipCheckbox() {
		return (JCheckBox) getComponent("strictOwnershipCheckbox");
	}

	public JButton getTestConnectionButton() {
		return (JButton) getComponent("testConnectionButton");
	}

	public JCheckBox getRPToolsPrivateCheckbox() {
		return (JCheckBox) getComponent("rptoolsPrivateCheckbox");
	}
	
	public JCheckBox getPlayersCanRevealVisionCheckbox() {
		return (JCheckBox) getComponent("playersCanRevealCheckbox");
	}
	
	private void init() {
		
		initUsernameTextField();
		initPortTextField();
		initGMPasswordTextField();
		initPlayerPasswordTextField();
		initRPToolsNameTextField();
		initOKButton();
		initCancelButton();
		initRoleCombo();
		initUseStrictOwnershipCheckbox();
		initTestConnectionButton();
		initRPToolsPrivateCheckbox();
		initPlayersCanRevealVisionCheckbox();
	}
	
	private void initUsernameTextField() {
		getUsernameTextField().setText(prefs.getUsername());
	}

	private void initPortTextField() {
		getPortTextField().setText(Integer.toString(prefs.getPort()));
	}
	
	private void initGMPasswordTextField() {
		getGMPasswordTextField().setText(prefs.getGMPassword());
	}

	private void initPlayerPasswordTextField() {
		getPlayerPasswordTextField().setText(prefs.getPlayerPassword());
	}

	private void initRPToolsNameTextField() {
		getRPToolsNameTextField().setText(prefs.getRPToolsName());
	}
	
	private void initOKButton() {
		getOKButton().addActionListener(new java.awt.event.ActionListener() { 
			public void actionPerformed(java.awt.event.ActionEvent e) {    
				
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

				if (getUsernameTextField().getText().length() == 0) {
					MapTool.showError("Must supply a username");
					return;
				}
				
				option = OPTION_OK;
				setVisible(false);
				
				// Prefs
				prefs.setUsername(getUsernameTextField().getText());
				prefs.setPort(Integer.parseInt(getPortTextField().getText()));
				prefs.setGMPassword(getGMPasswordTextField().getText());
				prefs.setPlayerPassword(getPlayerPasswordTextField().getText());
				prefs.setRole(getRole());
				prefs.setRPToolsName(getRPToolsNameTextField().getText());
				prefs.setStrictTokenOwnership(getUseStrictOwnershipCheckbox().isSelected());
				prefs.setRPToolsPrivate(getRPToolsPrivateCheckbox().isSelected());
				prefs.setPlayersCanRevealVision(getPlayersCanRevealVisionCheckbox().isSelected());
			}
		});
		getRootPane().setDefaultButton(getOKButton());
	}
	
	private void initCancelButton() {
		getCancelButton().addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {    
				option = OPTION_CANCEL;
				setVisible(false);
			}
		});
	}

	private void initRoleCombo() {
		DefaultComboBoxModel model = new DefaultComboBoxModel(new String[]{"Player", "GM"});
		getRoleCombo().setModel(model);
		getRoleCombo().setSelectedIndex(prefs.getRole());
	}
	
	private void initUseStrictOwnershipCheckbox() {
		getUseStrictOwnershipCheckbox().setSelected(prefs.useStrictTokenOwnership());
	}
	
	private void initTestConnectionButton() {
		getTestConnectionButton().addActionListener(new ActionListener() {
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
								MapTool.showInformation("Success! I could successfully connect to your computer from the internet.");
							} else {
								MapTool.showError("Could not see your computer from the internet");
							}
						} catch (Exception e) {
							e.printStackTrace();
							MapTool.showError("Unable to see your computer from the internet.  Check your firewall port settings.");
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
	
	private void initRPToolsPrivateCheckbox() {
		getRPToolsPrivateCheckbox().setSelected(prefs.getRPToolsPrivate());
	}
	
	private void initPlayersCanRevealVisionCheckbox() {
		getPlayersCanRevealVisionCheckbox().setSelected(prefs.getPlayersCanRevealVision());
	}
	
	public void setVisible(boolean b) {
		
		if (b) {
			SwingUtil.centerOver(this, MapTool.getFrame());
		}
		super.setVisible(b);
	}
	
	
}
