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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import net.rptools.clientserver.hessian.client.ClientConnection;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.drawing.Pen;
import net.rptools.maptool.server.MapToolServer;
import net.rptools.maptool.util.FileUtil;


/**
 */
public class ClientActions {

    public static final Action TOGGLE_GRID = new ClientAction() {

        {
            putValue(Action.NAME, "Toggle Grid");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_G, 0));
        }

        public void execute(ActionEvent e) {
            ZoneRenderer renderer = MapToolClient.getCurrentZoneRenderer();
            if (renderer != null) {
                renderer.toggleGrid();
            }
        }
    };

    public static final Action START_SERVER = new ClientAction() {

        {
            putValue(Action.NAME, "Start server");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
        }

        public void execute(ActionEvent e) {

            runBackground(new Runnable(){
                public void run() {

                	String portStr = JOptionPane.showInputDialog(MapToolClient.getInstance(), "What port to start the server on:", Integer.toString(MapToolServer.DEFAULT_PORT));
					
                	if (portStr == null || portStr.length() == 0) {
                		
                		new MainMenuDialog().setVisible(true);
                		return;
                	}
                	
                	try {
                		int port = Integer.parseInt(portStr);
                		
                		// TODO: include selection of campaign
                		MapToolClient.startServer(port);

                		// Connect to server
                        MapToolClient.getInstance().createConnection("localhost", port);
                		
                	} catch (NumberFormatException nfe) {
                		MapToolClient.showError("Invalid port number");
                		return;
                	} catch (UnknownHostException uh) {
                		MapToolClient.showError("Whoah, 'localhost' is not a valid address.  Weird.");
                		return;
                	} catch (IOException ioe) {
                		MapToolClient.showError("Could not connect to server: " + ioe);
                		return;
                	}
                	
                	// TODO: I don't like this here
                	CONNECT_TO_SERVER.setEnabled(false);
                	
				}
        	});
        }

    };

    public static final Action CONNECT_TO_SERVER = new ClientAction() {

        {
            putValue(Action.NAME, "Connect to server");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        }

        public void execute(ActionEvent e) {

            try {
            	String server = JOptionPane.showInputDialog(MapToolClient.getInstance(), "Server IP/Name [:port]");
            	
            	if (server == null || server.length() == 0) {
            		
            		new MainMenuDialog().setVisible(true);
            		return;
            	}
            	
            	// Parse
            	int port = MapToolServer.DEFAULT_PORT;
            	int index = server.indexOf(":");
            	if (index == 0) {
            		MapToolClient.showError("Must supply a server IP/Name");
                    new MainMenuDialog().setVisible(true);
            		return;
            	}
            	if (index > 0) {
            		
            		String portStr = server.substring(index + 1);
            		server = server.substring(0, index);
                    
                    try {
                        port = Integer.parseInt(portStr);
                    } catch (NumberFormatException nfe) {
                        MapToolClient.showError("Invalid port: " + portStr);
                        new MainMenuDialog().setVisible(true);
                        return;
                    }
            	}
            	
                MapToolClient.getInstance().createConnection(server, port);
            } catch (UnknownHostException e1) {
                // TODO Auto-generated catch block
                MapToolClient.showError("Unknown host");
                e1.printStackTrace();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                MapToolClient.showError("IO Error");
                e1.printStackTrace();
            }
            
            // TODO: I don't like this here
            START_SERVER.setEnabled(false);
        }

    };

    public static final Action LOAD_MAP = new ClientAction() {
        {
            putValue(Action.NAME, "Load Map");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_MASK));
        }

        public void execute(java.awt.event.ActionEvent e) {

            runBackground(new Runnable() {

                public void run() {
                    JFileChooser loadFileChooser = MapToolClient.getLoadFileChooser();

                    loadFileChooser.setDialogTitle("Load Map");
                    loadFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                    if (loadFileChooser.showOpenDialog(MapToolClient.getInstance()) == JFileChooser.CANCEL_OPTION) {
                    	return;
                    }
                    if (loadFileChooser.getSelectedFile() == null) {
                        return;
                    }

                    try {
                        byte[] imgData = FileUtil.loadFile(loadFileChooser.getSelectedFile());
                        Asset asset = new Asset(imgData);
                        MapToolClient.getCampaign().putAsset(asset);

                        // TODO: this needs to be abstracted into the client
                        if (MapToolClient.isConnected()) {
                            ClientConnection conn = MapToolClient.getInstance().getConnection();
                            
                            conn.callMethod(MapToolClient.COMMANDS.putAsset.name(), asset);
                        }

                        MapToolClient.addZone(asset.getId());
                    } catch (IOException ioe) {
                        MapToolClient.showError("Could not load image: " + ioe);
                        return;
                    }
                }

            });
        }
    };

    public static final Action ADD_ASSET_PANEL = new ClientAction() {
        
        {
            putValue(Action.NAME, "Add Asset Panel");
        }
        
        public void execute(ActionEvent e) {
            
            runBackground(new Runnable() {
                
                public void run() {

                    JFileChooser chooser = MapToolClient.getLoadFileChooser();
                    chooser.setDialogTitle("Load Asset Tree");
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                    if (chooser.showOpenDialog(MapToolClient.getInstance()) != JFileChooser.APPROVE_OPTION) {
                        return;
                    }
                    
                    MapToolClient.addAssetTree(new AssetTree(chooser.getSelectedFile()));
                }
                
            });
        }
    };
    
    public static final Action EXIT = new ClientAction () {
    	
        {
            putValue(Action.NAME, "Exit");
        }
    	
        public void execute(ActionEvent ae) {
        
        	System.exit(0);
        }
    };
    
    public static final Action CHOOSE_COLOR  = new ClientAction () {
        {
            putValue(Action.NAME, "Choose Color");
        }
        
        public void execute(ActionEvent ae) {
            Pen pen = MapToolClient.getInstance().getPen();
            Color oldColor = new Color(pen.getColor());
            Color newColor = JColorChooser.showDialog(null, "Choose drawing color", oldColor);
            
            if (newColor != null) {
                pen.setColor(newColor.getRGB());
            }
        }
    };
    
    public static final Action FOREGROUND_MODE = new ClientAction() {
        {
            putValue(Action.NAME, "Transparent Foreground");
        }
        
        public void execute(ActionEvent ae) {
            Pen pen = MapToolClient.getInstance().getPen();
            JCheckBoxMenuItem item = (JCheckBoxMenuItem) ae.getSource();
            
            if (item.isSelected()) {
                pen.setForegroundMode(Pen.MODE_TRANSPARENT);
            } else {
                pen.setForegroundMode(Pen.MODE_SOLID);
            }
        }
    };
    
    public static final Action CHOOSE_BACKGROUND_COLOR = new ClientAction() {
        {
            putValue(Action.NAME, "Choose Background Color");
        }
        
        public void execute(ActionEvent ae) {
            Pen pen = MapToolClient.getInstance().getPen();
            Color oldColor = new Color(pen.getBackgroundColor());
            Color newColor = JColorChooser.showDialog(null, "Choose drawing color", oldColor);
            
            if (newColor != null) {
                pen.setBackgroundColor(newColor.getRGB());
            }
        }
    };
    
    public static final Action BACKGROUND_MODE = new ClientAction() {
        {
            putValue(Action.NAME, "Transparent Background");
        }
        
        public void execute(ActionEvent ae) {
            Pen pen = MapToolClient.getInstance().getPen();
            JCheckBoxMenuItem item = (JCheckBoxMenuItem) ae.getSource();
            
            if (item.isSelected()) {
                pen.setBackgroundMode(Pen.MODE_TRANSPARENT);
            } else {
                pen.setBackgroundMode(Pen.MODE_SOLID);
            }
        }
    };
    
    
    public static final Action ERASER = new ClientAction() {
        {
            putValue(Action.NAME, "Eraser");
        }
        
        public void execute(ActionEvent ae) {
            Pen pen = MapToolClient.getInstance().getPen();
            JCheckBoxMenuItem item = (JCheckBoxMenuItem) ae.getSource();
            
            pen.setEraser(item.isSelected());
        }
    };
    
    private static abstract class ClientAction extends AbstractAction {

        public final void actionPerformed(ActionEvent e) {

            execute(e);
        }

        public abstract void execute(ActionEvent e);

        public void runBackground(Runnable r) {

            new Thread(r).start();
        }
    }
}
