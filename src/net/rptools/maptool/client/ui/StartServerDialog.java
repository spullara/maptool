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
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import yasb.Binder;

import net.rptools.lib.service.EchoServer;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolRegistry;
import net.rptools.maptool.client.swing.AbeillePanel;
import net.rptools.maptool.client.swing.GenericDialog;
import net.rptools.maptool.model.Player;
/**
 * @author trevor
 */
public class StartServerDialog extends AbeillePanel<StartServerDialogPreferences> {

	public static final int OPTION_OK = 0;
	public static final int OPTION_CANCEL = 1;
	
	private int option = OPTION_CANCEL;

	private StartServerDialogPreferences prefs;
	private GenericDialog dialog;

	public StartServerDialog() {
		super("net/rptools/maptool/client/ui/forms/startServerDialog.jfrm");

		panelInit();
	}
	
	public void showDialog() {
		dialog = new GenericDialog("Start Server", MapTool.getFrame(), this);

		prefs = new StartServerDialogPreferences();

		bind(prefs);

		getRootPane().setDefaultButton(getOKButton());
		dialog.showDialog();
	}

	public JTextField getPortTextField() {
		return (JTextField) getComponent("@port");
	}

	public JTextField getUsernameTextField() {
		return (JTextField) getComponent("@username");
	}

	public JButton getOKButton() {
		return (JButton) getComponent("okButton");
	}
	
	public JButton getCancelButton() {
		return (JButton) getComponent("cancelButton");
	}

	public JComboBox getRoleCombo() {
		return (JComboBox) getComponent("@role");
	}
	
	public JButton getTestConnectionButton() {
		return (JButton) getComponent("testConnectionButton");
	}

	@Override
	protected void preModelBind() {
		Binder.setFormat(getPortTextField(), new DecimalFormat("####"));
	}
	
	public void initOKButton() {
		getOKButton().addActionListener(new java.awt.event.ActionListener() { 
			public void actionPerformed(java.awt.event.ActionEvent e) {    
				
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
				
				if (commit()) {
					option = OPTION_OK;
					dialog.closeDialog();
				}
				
			}
		});
	}
	
	public void initCancelButton() {
		getCancelButton().addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {    
				option = OPTION_CANCEL;
				dialog.closeDialog();
			}
		});
	}

	public void initTestConnectionButton() {
		getTestConnectionButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					public void run() {
						
						
						EchoServer server = null;
						try {
							int port = Integer.parseInt(getPortTextField().getText());

							// Create a temporary server that will listen on the port we
							// want to start MapTool on.  This provides two things: First
							// it tells us we can open that port, second it creates a way
							// for the connection test service to call back and verify it is
							// the type of service we want.
							// LATER: Extend EchoServer to do something more than just parrot the input
							server = new EchoServer(port);
							server.start();
							
							if (MapToolRegistry.testConnection(port)) {
								MapTool.showInformation("Success! I could successfully connect to your computer from the internet.");
							} else {
								MapTool.showError("Could not see your computer from the internet.<br><br>It could be a port forwarding issue, see http://portforward.com for instructions on how to set up port forwarding");
							}
						} catch (NumberFormatException nfe) {
							MapTool.showError("Port must be a number");
							return;
						} catch (Exception e) {
							e.printStackTrace();
							MapTool.showError("Unable to see your computer from the internet.  <br><br>It could be a port forwarding issue, see http://portforward.com for instructions on how to set up port forwarding.");
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
}
