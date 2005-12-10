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
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import net.rptools.clientserver.ActivityListener;
import net.rptools.clientserver.hessian.client.ClientConnection;
import net.rptools.lib.FileUtil;
import net.rptools.maptool.client.ui.ConnectionStatusPanel;
import net.rptools.maptool.client.ui.MapToolClient;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.client.ui.zone.ZoneRendererFactory;
import net.rptools.maptool.language.I18N;
import net.rptools.maptool.model.Campaign;
import net.rptools.maptool.model.MessageChannel;
import net.rptools.maptool.model.ObservableList;
import net.rptools.maptool.model.Player;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.server.MapToolServer;
import net.rptools.maptool.server.ServerCommand;
import net.rptools.maptool.server.ServerConfig;
import net.rptools.maptool.server.ServerPolicy;

import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;

/**
 */
public class MapTool {

	private static MapToolClient clientFrame;
    private static MapToolServer server;
    private static ServerCommand serverCommand;

    private static String version;
    
    private static Campaign campaign;
    
    private static ObservableList<Player> playerList;
    private static ObservableList<String> messageList;
    
    private static Player player;
    
    private static ClientConnection conn;
    private static ClientMethodHandler handler;
    
    // Components
	private static JFileChooser loadFileChooser;
	private static JFileChooser saveFileChooser;

	public static void showError(String message) {
		JOptionPane.showMessageDialog(clientFrame, I18N.getText(message), "Error", JOptionPane.ERROR_MESSAGE);
	}
	
    private MapTool() {
        // Not instantiatable
    }
    
	private static void initialize() {
		
		// Components
		loadFileChooser = createLoadFileChooser();
		saveFileChooser = createSaveFileChooser();

        playerList = new ObservableList<Player>();
        messageList = new ObservableList<String>(Collections.synchronizedList(new ArrayList<String>()));
        
        handler = new ClientMethodHandler();
        
        clientFrame = new MapToolClient();
        
        serverCommand = new ServerCommandClientImpl();
        
        player = new Player("", 0);
        
        AppActions.updateActions();
	}
	
	public static String getVersion() {
		if (version == null) {
            version = "DEVELOPMENT";
            try {
	            if (MapTool.class.getClassLoader().getResource("net/rptools/maptool/client/version.txt") != null) {
	                version = new String(FileUtil.loadResource("net/rptools/maptool/client/version.txt"));
	            }
            } catch (IOException ioe) {
            	version = "CAN'T FIND VERSION FILE";
            }
		}
		
		return version;
	}
	
    public static ServerCommand serverCommand() {
        return serverCommand;
    }
    
    public static void startIndeterminateAction() {
    	//clientFrame.startIndeterminateAction();
    }
    
    public static void endIndeterminateAction() {
    	//clientFrame.endIndeterminateAction();
    }
    
    public static void startDeterminateAction(int totalWork) {
    	//clientFrame.startDeterminateAction(totalWork);
    }
    
    public static void updateDeterminateActionProgress(int additionalWorkCompleted) {
    	//clientFrame.updateDeterminateActionProgress(additionalWorkCompleted);
    }
    
    public static void endDeterminateAction() {
    	//clientFrame.endDeterminateAction();
    }
    
    public static void addPlayer(Player player) {
        if (!playerList.contains(player)) {
            playerList.add(player);
            
            // LATER: Make this non-anonymous
            playerList.sort (new Comparator<Player>() {
                public int compare(Player arg0, Player arg1) {
                    return arg0.getName().compareToIgnoreCase(arg1.getName());
                }
            });
            
            if (!player.equals(MapTool.getPlayer())) {	
            	getFrame().getNotificationOverlay().addEvent(player.getName() + " has connected");
            }
        }
	}
	
    public Player getPlayer(String name) {
        
        for (int i = 0; i < playerList.size(); i++) {
            if (playerList.get(i).getName().equals(name)) {
                return playerList.get(i);
            }
        }
        return null;
    }

    public static void removePlayer(Player player) {
    	
    	if (player == null) {
    		return;
    	}
    	
		playerList.remove(player);

		if (MapTool.getPlayer() != null && !player.equals(MapTool.getPlayer())) {
			getFrame().getNotificationOverlay().addEvent(player.getName() + " has disconnected");
		}
	}
	
	
    public static ObservableList<String> getMessageList () {
        return messageList;
    }
    
    /**
     * These are the messages that originate from the server
     * @param channel
     * @param message
     */
    public static void addServerMessage(String channel, String message) {

        // Filter
        // LATER: Come up with a better solution
        if (MessageChannel.GM.equals(channel) && !getPlayer().isGM()) {
            return;
        }
        
        messageList.add(message);
    }

    /**
     * These are the messages that are generated locally
     * @param channel
     * @param message
     */
    public static void addMessage(String channel, String message) {
        messageList.add(message);
        
        if (isConnected() && !MessageChannel.ME.equals(channel)) {
            serverCommand().message(channel, message);
        }
    }
    
    /**
     * Add a message only this client can see.  This is a shortcut for addMessage(ME, ...)
     * @param message
     */
    public static void addLocalMessage(String message) {
        addMessage(MessageChannel.ME, message);
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
            clientFrame.addZoneRenderer(renderer);
            
            if (currRenderer == null && (getPlayer().isGM() || zone.isVisible())){
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

    public static ObservableList<Player> getPlayerList() {
        return playerList;
    }
    
	public static void addZone(Zone zone) {
		
        getCampaign().putZone(zone);

        serverCommand().putZone(zone);
        
        AppListeners.fireZoneAdded(zone);
        
        // Show the new zone
        clientFrame.setCurrentZoneRenderer(ZoneRendererFactory.newRenderer(zone));
	}
	
	public static Player getPlayer() {
		return player;
	}
	
    public static void createConnection(String host, int port, Player player) throws UnknownHostException, IOException {

    	MapTool.player = player;
    	
    	ClientConnection clientConn = new MapToolConnection(host, port, player);
    	
	    	
    	clientConn.addMessageHandler(handler);
    	clientConn.addActivityListener(clientFrame.getActivityMonitor());
    	clientConn.addActivityListener(new ActivityProgressListener());
    	clientConn.addDisconnectHandler(new ServerDisconnectHandler());
        
    	clientConn.start();

    	// LATER: I really, really, really don't like this startup pattern
        if (clientConn.isAlive()) {
        	conn = clientConn;
    	}
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
    
    public static boolean isHostingServer() {
        return server != null;
    }

    public static void disconnect() {

        if (conn == null || !conn.isAlive()) {
            return;
        }
        
        try {
            conn.close();
            conn = null;
            playerList.clear();
            
            setCampaign(new Campaign());
            
        } catch (IOException ioe) {
            // This isn't critical, we're closing it anyway
            ioe.printStackTrace();
        }
        MapTool.getFrame().getConnectionStatusPanel().setStatus(ConnectionStatusPanel.Status.disconnected);
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
