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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;

import net.rptools.common.util.FileUtil;
import net.rptools.common.util.ImageUtil;
import net.rptools.maptool.client.tool.GridTool;
import net.rptools.maptool.client.ui.AssetPanel;
import net.rptools.maptool.client.ui.ConnectToServerDialog;
import net.rptools.maptool.client.ui.StartServerDialog;
import net.rptools.maptool.client.ui.ZoneRenderer;
import net.rptools.maptool.client.ui.ZoneSelectionPanel;
import net.rptools.maptool.client.ui.model.Directory;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.model.Campaign;
import net.rptools.maptool.model.Player;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.util.PersistenceUtil;


/**
 */
public class AppActions {

    public static final Action REMOVE_ASSET_ROOT = new ClientAction() {
        
        {
            putValue(Action.NAME, "Remove");
        }
        
        public void execute(ActionEvent e) {
            
            AssetPanel assetPanel = MapTool.getFrame().getAssetPanel(); 
            Directory dir = assetPanel.getSelectedAssetRoot();
            
            if (dir == null) {
                MapTool.showError("Select an asset group first");
                return;
            }
            
            AppPreferences.removeAssetRoot(dir.getPath());
            assetPanel.removeAssetRoot(dir);
        }
        
    };
    
    public static final Action TYPE_COMMAND = new ClientAction() {
        
        {
            putValue(Action.NAME, "Type Command");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, 0));
        }
        
        public void execute(ActionEvent e) {
            
            MapTool.getFrame().getChatPanel().startCommand();
        }
        
    };
    
    public static final Action RANDOMLY_ADD_LAST_ASSET = new ClientAction() {
        
        {
            putValue(Action.NAME, "Random Dup Last Asset");
        }
        
        public void execute(ActionEvent e) {
            
            Asset asset = AssetManager.getLastRetrievedAsset();
            for (int i = 0; i < 100; i++) {
                
                Token token = new Token(asset.getId());
                token.setX(MapToolUtil.getRandomNumber(100 * 5));
                token.setY(MapToolUtil.getRandomNumber(100 * 5));
                MapTool.getFrame().getCurrentZoneRenderer().getZone().putToken(token);
            }
            MapTool.getFrame().getCurrentZoneRenderer().repaint();
        }
        
    };
    
	public static final Action ADJUST_GRID = new ClientAction() {
		
		{
			putValue(Action.NAME, "Adjust Grid");
		}
		
		public void execute(ActionEvent e) {
			
			if(MapTool.getPlayer().getRole() != Player.Role.GM) {
				return;
			}
			
			if (MapTool.getFrame().getCurrentZoneRenderer().getZone().getType() == Zone.Type.INFINITE) {
				MapTool.showError("Cannot adjust grid on infinite maps.");
				return;
			}
			
			MapTool.getFrame().getToolbox().setSelectedTool(new GridTool());
		}
		
	};
	
    public static final Action TOGGLE_GRID = new ClientAction() {

        {
            //putValue(Action.NAME, "Toggle Grid");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_MASK + InputEvent.SHIFT_MASK));
            try {
            	putValue(Action.SMALL_ICON, new ImageIcon(ImageUtil.getImage("net/rptools/maptool/client/image/grid.gif")));
            } catch (IOException ioe) {
            	ioe.printStackTrace();
            }
        }

        public void execute(ActionEvent e) {
            ZoneRenderer renderer = MapTool.getFrame().getCurrentZoneRenderer();
            if (renderer != null) {
                renderer.toggleGrid();
            }
        }
    };

    public static final Action NEW_CAMPAIGN = new ClientAction() {

        {
            putValue(Action.NAME, "New Campaign");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK + InputEvent.SHIFT_MASK));
        }

        public void execute(ActionEvent e) {
            
            if (MapTool.isConnected()) {
                
                MapTool.showError("You are connected to a server.  Please disconnect first.");
                return;
            }
            
            MapTool.setCampaign(new Campaign());
        }
    };

    public static final Action ZOOM_IN = new ClientAction() {

        {
            putValue(Action.NAME, "Zoom In");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, 0));
        }

        public void execute(ActionEvent e) {
            ZoneRenderer renderer = MapTool.getFrame().getCurrentZoneRenderer();
            if (renderer != null) {
            	Dimension size = renderer.getSize();
                renderer.zoomIn(size.width/2, size.height/2);
            }
        }
    };

    public static final Action ZOOM_OUT = new ClientAction() {

        {
            putValue(Action.NAME, "Zoom Out");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0));
        }

        public void execute(ActionEvent e) {
            ZoneRenderer renderer = MapTool.getFrame().getCurrentZoneRenderer();
            if (renderer != null) {
            	Dimension size = renderer.getSize();
                renderer.zoomOut(size.width/2, size.height/2);
            }
        }
    };

    public static final Action ZOOM_RESET = new ClientAction() {

        {
            putValue(Action.NAME, "Zoom 1:1");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, KeyEvent.SHIFT_MASK));
        }

        public void execute(ActionEvent e) {
            ZoneRenderer renderer = MapTool.getFrame().getCurrentZoneRenderer();
            if (renderer != null) {
                renderer.zoomReset();
            }
        }
    };

    public static final Action TOGGLE_ZONE_SELECTOR = new ClientAction() {

        {
            putValue(Action.NAME, "Toggle Zone Selector");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK + InputEvent.SHIFT_MASK));
        }

        public void execute(ActionEvent e) {
        	
        	ZoneSelectionPanel panel = MapTool.getFrame().getZoneSelectionPanel();
        	
        	panel.setVisible(!panel.isVisible());
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

                	if (MapTool.isConnected()) {
                		MapTool.showError("Already connected.");
                		return;
                	}
                	
                	StartServerDialog dialog = new StartServerDialog();
                	
                	dialog.setVisible(true);

                	if (dialog.getOption() == StartServerDialog.OPTION_CANCEL) {
                		return;
                	}
                	
                	
                	try {
                		int port = dialog.getPort();
                		
                		MapTool.startServer(port);

                		// Connect to server
                        MapTool.createConnection("localhost", port, new Player(dialog.getUsername(), Player.Role.GM));
                	} catch (UnknownHostException uh) {
                		MapTool.showError("Whoah, 'localhost' is not a valid address.  Weird.");
                		return;
                	} catch (IOException ioe) {
                		MapTool.showError("Could not connect to server: " + ioe);
                		return;
                	}
                	
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
            	
            	if (MapTool.isConnected()) {
            		MapTool.showError("Already connected.");
            		return;
            	}
            	
            	ConnectToServerDialog dialog = new ConnectToServerDialog();
            	
            	dialog.setVisible(true);
            	
            	if (dialog.getOption() == ConnectToServerDialog.OPTION_CANCEL) {
            		
            		return;
            	}
            	
                MapTool.createConnection(dialog.getServer(), dialog.getPort(), new Player(dialog.getUsername(), dialog.getRole()));
            } catch (UnknownHostException e1) {
                // TODO Auto-generated catch block
                MapTool.showError("Unknown host");
                e1.printStackTrace();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                MapTool.showError("IO Error: " + e1);
                e1.printStackTrace();
            }
            
        }

    };

    public static final Action LOAD_CAMPAIGN = new ClientAction () {
    	
        {
            putValue(Action.NAME, "Load Campaign");
        }
    	
        public void execute(ActionEvent ae) {
        
        	JFileChooser chooser = MapTool.getLoadFileChooser();
        	chooser.setDialogTitle("Load Campaign");
        	
        	if (chooser.showOpenDialog(MapTool.getFrame()) == JFileChooser.APPROVE_OPTION) {
        		
        		try {
        			Campaign campaign = PersistenceUtil.loadCampaign(chooser.getSelectedFile());
        			
        			if (campaign != null) {
        				
        				MapTool.setCampaign(campaign);
        				
                        MapTool.serverCommand().setCampaign(campaign);
        			}
        			
        		} catch (IOException ioe) {
        			MapTool.showError("Could not load campaign: " + ioe);
        		}
        	}
        }
    };
    
    public static final Action SAVE_CAMPAIGN = new ClientAction () {
    	
        {
            putValue(Action.NAME, "Save Campaign");
        }
    	
        public void execute(ActionEvent ae) {
        
        	Campaign campaign = MapTool.getCampaign();
        	
        	// TODO: this should eventually just remember the last place it was saved
        	JFileChooser chooser = MapTool.getSaveFileChooser();
        	chooser.setDialogTitle("Save Campaign");
        	
        	if (chooser.showSaveDialog(MapTool.getFrame()) == JFileChooser.APPROVE_OPTION) {
        		
        		try {
        			PersistenceUtil.saveCampaign(campaign, chooser.getSelectedFile());
        		} catch (IOException ioe) {
        			MapTool.showError("Could not save campaign: " + ioe);
        		}
        	}
        }
    };
    
    public static final Action CREATE_UNBOUNDED_MAP = new ClientAction() {
        {
            putValue(Action.NAME, "Create Unbounded Map");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_MASK));
        }

        public void execute(java.awt.event.ActionEvent e) {

            runBackground(new Runnable() {
            	
				public void run() {

					Zone zone = new Zone();
					zone.setType(Zone.Type.INFINITE);
					
                    MapTool.addZone(zone);
				}
            });
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
                    JFileChooser loadFileChooser = MapTool.getLoadFileChooser();

                    loadFileChooser.setDialogTitle("Load Map");
                    loadFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                    if (loadFileChooser.showOpenDialog(MapTool.getFrame()) == JFileChooser.CANCEL_OPTION) {
                    	return;
                    }
                    if (loadFileChooser.getSelectedFile() == null) {
                        return;
                    }

                    try {
                        byte[] imgData = FileUtil.loadFile(loadFileChooser.getSelectedFile());
                        Asset asset = new Asset(imgData);
                        AssetManager.putAsset(asset);

                        MapTool.serverCommand().putAsset(asset);

                        Zone zone = new Zone(asset.getId());
                        MapTool.addZone(zone);
                    } catch (IOException ioe) {
                        MapTool.showError("Could not load image: " + ioe);
                        return;
                    }
                }

            });
        }
    };

    public static final Action TOGGLE_ASSET_PANEL = new ClientAction() {
        
        {
            putValue(Action.NAME, "Toggle Asset Panel");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK + InputEvent.SHIFT_MASK));
        }
        
        /* (non-Javadoc)
         * @see net.rptools.maptool.client.ClientActions.ClientAction#execute(java.awt.event.ActionEvent)
         */
        public void execute(ActionEvent e) {

            MapTool.getFrame().toggleAssetTree();
        }
    };
    
    public static final Action ADD_ASSET_PANEL = new ClientAction() {
        
        {
            putValue(Action.NAME, "Add Asset Panel");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
        }
        
        public void execute(ActionEvent e) {
            
            runBackground(new Runnable() {
                
                public void run() {

                    JFileChooser chooser = MapTool.getLoadFileChooser();
                    chooser.setDialogTitle("Load Asset Tree");
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                    if (chooser.showOpenDialog(MapTool.getFrame()) != JFileChooser.APPROVE_OPTION) {
                        return;
                    }
                    
                    File root = chooser.getSelectedFile();
                    MapTool.getFrame().addAssetRoot(root);
                    AppPreferences.addAssetRoot(root);
                }
                
            });
        }
    };
    
    public static final Action REFRESH_ASSET_PANEL = new ClientAction() {
      
      {
        putValue(Action.NAME, "Refresh");
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("F5"));
      }
      
      public void execute(ActionEvent e) {
        MapTool.getFrame().getAssetPanel().getAssetTree().refresh();
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
    
    private static abstract class ClientAction extends AbstractAction {

        public final void actionPerformed(ActionEvent e) {

    		execute(e);
        }

        public abstract void execute(ActionEvent e);

        public void runBackground(final Runnable r) {

            new Thread(){
            	
            	public void run() {
            		
            		try {
            			MapTool.startIndeterminateAction();
            			r.run();
            		} finally {
            			MapTool.endIndeterminateAction();
            		}
            	}
            }.start();
        }
    }
}
