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
import javax.swing.DefaultListModel;
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
		
		pack();
	}
	
	public JTextField getUsernameTextField() {
		JTextField textfield = (JTextField) getComponent("usernameField");
		if (initialize(textfield)) {
			textfield.setText(prefs.getUsername());
		}
		return textfield;
	}
	
	public JTextField getPortTextField() {
		JTextField textfield = (JTextField) getComponent("portField");
		if (initialize(textfield)) {
			textfield.setText(Integer.toString(prefs.getPort()));
		}
		return textfield;
	}
	
	public JTextField getGMPasswordTextField() {
		JTextField textfield = (JTextField) getComponent("gmPasswordField");
		if (initialize(textfield)) {
			textfield.setText(prefs.getGMPassword());
		}
		return textfield;
	}
	
	public JTextField getPlayerPasswordTextField() {
		JTextField textfield = (JTextField) getComponent("playerPasswordField");
		if (initialize(textfield)) {
			textfield.setText(prefs.getPlayerPassword());
		}
		return textfield;
	}
	
	public JTextField getRPToolsNameTextField() {
		JTextField textfield = (JTextField) getComponent("rptoolsNameField");
		if (initialize(textfield)) {
		}
		return textfield;
	}
	
	public JButton getOKButton() {
		JButton button = (JButton) getComponent("okButton");
		if (initialize(button)) {
			button.addActionListener(new java.awt.event.ActionListener() { 
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
					StartServerDialogPreferences prefs = new StartServerDialogPreferences();
					prefs.setUsername(getUsernameTextField().getText());
					prefs.setPort(Integer.parseInt(getPortTextField().getText()));
					prefs.setGMPassword(getGMPasswordTextField().getText());
					prefs.setPlayerPassword(getPlayerPasswordTextField().getText());
					prefs.setRole(getRole());
					prefs.setServerName(getRPToolsNameTextField().getText());
				}
			});
			
			getRootPane().setDefaultButton(button);
		}
		return button;
	}

	public JButton getCancelButton() {
		JButton button = (JButton) getComponent("cancelButton");
		if (initialize(button)) {
			button.addActionListener(new ActionListener() { 
				public void actionPerformed(ActionEvent e) {    
					option = OPTION_CANCEL;
					setVisible(false);
				}
			});
		}
		return button;
	}
	
	public JComboBox getRoleCombo() {
		JComboBox combo = (JComboBox) getComponent("roleCombo");
		if (initialize(combo)) {
			DefaultComboBoxModel model = new DefaultComboBoxModel(new String[]{"Player", "GM"});
			combo.setModel(model);
		}
		return combo;
	}
	
	public JCheckBox getUseStrictOwnershipCheckbox() {
		JCheckBox checkbox = (JCheckBox) getComponent("strictOwnershipCheckbox");
		if (initialize(checkbox)) {
		}
		return checkbox;
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

	public void setVisible(boolean b) {
		
		if (b) {
			SwingUtil.centerOver(this, MapTool.getFrame());
		}
		super.setVisible(b);
	}
	
	public int getOption() {
		return option;
	}
	
	public JButton getTestRegisterButton() {
		JButton button = (JButton) getComponent("testConnectionButton");
		if (initialize(button)) {
			button.addActionListener(new ActionListener() {
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
		return button;
	}
	
}
