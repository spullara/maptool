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

import java.awt.Toolkit;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import net.rptools.clientserver.ActivityListener;
import net.rptools.clientserver.hessian.client.ClientConnection;
import net.rptools.maptool.client.ui.MapToolClient;
import net.rptools.maptool.client.ui.ZoneRenderer;
import net.rptools.maptool.client.ui.ZoneRendererFactory;
import net.rptools.maptool.model.Campaign;
import net.rptools.maptool.model.Player;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.server.MapToolServer;
import net.rptools.maptool.server.ServerConfig;
import net.rptools.maptool.server.ServerPolicy;

import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;

/**
 */
public class MapTool {

    public static enum COMMANDS { 
    	setCampaign, 
    	putZone, 
    	removeZone, 
    	putAsset, 
    	getAsset,
    	removeAsset, 
    	putToken, 
    	removeToken, 
    	draw,
    	setZoneGridSize,
    	playerConnected,
    	playerDisconnected,
        message,
        undoDraw,
    };
	
	// Singleton
	private static MapToolClient clientFrame;
	
    private static MapToolServer server;

    private static Campaign campaign;
    
    private static PlayerList playerList;
    
    private static ClientConnection conn;
    private static ClientMethodHandler handler;
    
    private static List<String> messages = new ArrayList<String>();

    // Components
	private static JFileChooser loadFileChooser;
	private static JFileChooser saveFileChooser;

	public static void showError(String message) {
		JOptionPane.showMessageDialog(clientFrame, message, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
    private MapTool() {
        // Not instantiatable
    }
    
	private static void initialize() {
		
		// Components
		loadFileChooser = createLoadFileChooser();
		saveFileChooser = createSaveFileChooser();

        playerList = new PlayerList();
        handler = new ClientMethodHandler();
        
        clientFrame = new MapToolClient();
	}
	
    public static void startIndeterminateAction() {
    	clientFrame.startIndeterminateAction();
    }
    
    public static void endIndeterminateAction() {
    	clientFrame.endIndeterminateAction();
    }
    
    public static void startDeterminateAction(int totalWork) {
    	clientFrame.startDeterminateAction(totalWork);
    }
    
    public static void updateDeterminateActionProgress(int additionalWorkCompleted) {
    	clientFrame.updateDeterminateActionProgress(additionalWorkCompleted);
    }
    
    public static void endDeterminateAction() {
    	clientFrame.endDeterminateAction();
    }
    
    public static void addPlayer(Player player) {
		playerList.add(player);
	}
	
	public static void removePlayer(Player player) {
		playerList.remove(player);
	}
	
	
    public static List<String> getMessages() {
        return messages;
    }
    
    public static void addMessage(String message) {
        messages.add(message);
    }

    public static Campaign getCampaign() {
        if (campaign == null) {
            campaign = new Campaign();
        }
        return campaign;
    }
    
    public static void setCampaign(Campaign campaign) {
    	
    	// Load up the new
    	MapTool.campaign = campaign;
    	ZoneRenderer currRenderer = null;

        // Clean up
        clientFrame.setCurrentZoneRenderer(null);
        clientFrame.clearZoneRendererList();
        if (campaign == null) {
            return;
        }

        // Install new campaign
        for (Zone zone : campaign.getZones()) {
            
            ZoneRenderer renderer = ZoneRendererFactory.newRenderer(zone);
            clientFrame.addZoneRendererList(renderer);
            
            if (currRenderer == null){
                currRenderer = renderer;
            }
        }

    	clientFrame.setCurrentZoneRenderer(currRenderer);
    }
    
	public static void startServer(int port) throws IOException {
		
		if (server != null) {
			showError("Server is already started");
			return;
		}
		
		// TODO: the client and server campaign MUST be different objects.  Figure out a better init method
		server = new MapToolServer (new ServerConfig(), new ServerPolicy(), port);
		server.setCampaign(getCampaign());
        
        setCampaign(null);
	}
	
	public static void stopServer() {
		if (server == null) {
			return;
		}
		
		// TODO: server stop
	}

    public static PlayerList getPlayerList() {
        return playerList;
    }
    
	// TODO: I don't like this method name, location, or anything about it.  It sux.  Fix it.
	public static void addZone(Zone zone) {
		
        MapTool.getCampaign().putZone(zone);
        
        // TODO: this needs to be abstracted into the client
        if (MapTool.isConnected()) {
            conn.callMethod(MapTool.COMMANDS.putZone.name(), zone);
        }
        
        clientFrame.setCurrentZoneRenderer(ZoneRendererFactory.newRenderer(zone));
	}
	
    public static void createConnection(String host, int port, Player player) throws UnknownHostException, IOException {

    	conn = new MapToolClientConnection(host, port, player);
        conn.addMessageHandler(handler);
        conn.addActivityListener(clientFrame.getActivityMonitor());
        conn.addActivityListener(new ActivityProgressListener());
        conn.addDisconnectHandler(new ServerDisconnectHandler());
        
        conn.start();
    }
    
    public static void closeConnection() throws IOException {
        if (conn != null) {
            conn.close();
        }
    }
    
    public static ClientConnection getConnection() {
    	return conn;
    }
    
    public static boolean isConnected() {
        return conn != null;
    }

    public static void disconnect() {
        
        try {
            conn.close();
            conn = null;
            playerList.clear();
            
            if (server != null) {
                // TODO: implement this
                //instance.server.stop();
            }
        } catch (IOException ioe) {
            // This isn't critical, we're closing it anyway
            ioe.printStackTrace();
        }
    }
    
	public static MapToolClient getFrame() {
		return clientFrame;
	}
	
	public static JFileChooser getLoadFileChooser() {
		return loadFileChooser;
	}
	
	public static JFileChooser getSaveFileChooser() {
		return saveFileChooser;
	}
	
	private static JFileChooser createLoadFileChooser() {
		JFileChooser fileChooser = new JFileChooser();
		return fileChooser;
	}
	
	private static JFileChooser createSaveFileChooser() {
		JFileChooser fileChooser = new JFileChooser();
		return fileChooser;
	}
	
	public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
        } catch (Exception e) {
            System.err.println("Exception during look and feel setup: " + e);
        }
		
        // Draw frame contents on resize
        Toolkit.getDefaultToolkit().setDynamicLayout(true);        

        initialize();
        
		clientFrame.setVisible(true);
	}
	
	private static class ActivityProgressListener implements ActivityListener {
		/* (non-Javadoc)
		 * @see net.rptools.clientserver.ActivityListener#notify(net.rptools.clientserver.ActivityListener.Direction, net.rptools.clientserver.ActivityListener.State, int, int)
		 */
		public void notify(Direction direction, State state, int total, int current) {

			if (state == State.Start) {
				MapTool.startIndeterminateAction();
			} else if (state == State.Complete) {
                MapTool.endIndeterminateAction();
			}
		}
	}
}
