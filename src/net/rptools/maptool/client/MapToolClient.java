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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

import net.rptools.clientserver.ActivityListener;
import net.rptools.clientserver.ActivityListener.Direction;
import net.rptools.clientserver.ActivityListener.State;
import net.rptools.clientserver.hessian.client.ClientConnection;
import net.rptools.clientserver.simple.AbstractConnection;
import net.rptools.clientserver.simple.DisconnectHandler;
import net.rptools.maptool.client.swing.ColorPickerButton;
import net.rptools.maptool.client.swing.JSplitPaneEx;
import net.rptools.maptool.client.swing.MemoryStatusBar;
import net.rptools.maptool.client.swing.OutlookPanel;
import net.rptools.maptool.client.swing.ProgressStatusBar;
import net.rptools.maptool.client.swing.StatusPanel;
import net.rptools.maptool.client.swing.SwingUtil;
import net.rptools.maptool.client.tool.GridTool;
import net.rptools.maptool.client.tool.MeasuringTool;
import net.rptools.maptool.client.tool.DefaultTool;
import net.rptools.maptool.client.tool.PointerTool;
import net.rptools.maptool.client.tool.ZoomTool;
import net.rptools.maptool.client.tool.drawing.FreehandTool;
import net.rptools.maptool.client.tool.drawing.LineTool;
import net.rptools.maptool.client.tool.drawing.OvalFillTool;
import net.rptools.maptool.client.tool.drawing.OvalTool;
import net.rptools.maptool.client.tool.drawing.RectangleFillTool;
import net.rptools.maptool.client.tool.drawing.RectangleTool;
import net.rptools.maptool.model.AssetGroup;
import net.rptools.maptool.model.Campaign;
import net.rptools.maptool.model.Player;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.drawing.Pen;
import net.rptools.maptool.server.MapToolServer;
import net.rptools.maptool.server.ServerConfig;
import net.rptools.maptool.server.ServerPolicy;
import net.rptools.maptool.util.MD5Key;

import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;

/**
 */
public class MapToolClient extends JFrame {
    private static final long serialVersionUID = 3905523813025329458L;

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
    	playerDisconnected
    };
	
	private static final String WINDOW_TITLE = "MapTool";
	
	// TODO: parameterize this (or make it a preference)
	private static final int WINDOW_WIDTH = 800;
	private static final int WINDOW_HEIGHT = 600;

	// Singleton
	private static MapToolClient instance;
	
    private static MapToolServer server;

    private static Campaign campaign;
    
    private PlayerList playerList;
    
    private ClientConnection conn;
    private final ClientMethodHandler handler;
    
    private Pen pen = new Pen(Pen.DEFAULT);
    
    // Organization
    
	// Components
	private JFileChooser loadFileChooser;
	private JFileChooser saveFileChooser;
	private ToolboxBar toolboxPanel;
	private OutlookPanel outlookPanel;
	private ZoneRenderer currentRenderer;
	private AssetPanel assetPanel;

    private ZoneSelectionPanel zoneSelectionPanel;
    private JPanel mainPanel;
    
    private List<ZoneRenderer> zoneRendererList;
    
	private JSplitPaneEx mainSplitPane;
	
	private ColorPickerButton foregroundColorPicker = new ColorPickerButton("Foreground color", Color.black);
	private ColorPickerButton backgroundColorPicker = new ColorPickerButton("Background color", Color.white);

	private StatusPanel statusPanel;
	private ActivityMonitorPanel activityMonitor = new ActivityMonitorPanel();
	private ProgressStatusBar progressBar = new ProgressStatusBar();
	
	public static void showError(String message) {
		JOptionPane.showMessageDialog(instance, message, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	private MapToolClient() {
		
		// Set up the frame
		super (WINDOW_TITLE);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		SwingUtil.centerOnScreen(this);
        
		// Components
		loadFileChooser = createLoadFileChooser();
		saveFileChooser = createSaveFileChooser();
		toolboxPanel = createToolboxPanel();
		assetPanel = new AssetPanel();
		outlookPanel = new OutlookPanel ();
        outlookPanel.setMinimumSize(new Dimension(100, 200));
        zoneRendererList = new ArrayList<ZoneRenderer>();

        playerList = new PlayerList();
        outlookPanel.addButton("Connections", playerList);
        outlookPanel.addButton("Assets", assetPanel);
        
        statusPanel = new StatusPanel();
        statusPanel.addPanel(new MemoryStatusBar());
        statusPanel.addPanel(progressBar);
        statusPanel.addPanel(activityMonitor);
        
        // TODO: Clean up this whole section
        mainPanel = new JPanel(new BorderLayout());
        	
        zoneSelectionPanel = new ZoneSelectionPanel();

        mainPanel.add(BorderLayout.SOUTH, zoneSelectionPanel);
        
		setJMenuBar(createMenuBar());
		
		// Split left/right
		mainSplitPane = new JSplitPaneEx();
		mainSplitPane.setLeftComponent(outlookPanel);
		mainSplitPane.setRightComponent(mainPanel);
		mainSplitPane.setInitialDividerPosition(150);
        mainSplitPane.setBorder(null);
        
		JPanel mainInnerPanel = new JPanel(new BorderLayout());
		mainInnerPanel.setBorder(BorderFactory.createLoweredBevelBorder());
		mainInnerPanel.add(BorderLayout.CENTER, mainSplitPane);
		
		// Put it all together
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, mainInnerPanel);
		add(BorderLayout.NORTH, toolboxPanel);
		add(BorderLayout.SOUTH, statusPanel);

        //addInnerPanel(assetTreePanel);
		
		// Communications
        handler = new ClientMethodHandler(this);
        
	}
	
    public void startIndeterminateAction() {
    	progressBar.startIndeterminate();
    }
    
    public void endIndeterminateAction() {
    	progressBar.endIndeterminate();
    }
    
    public void startDeterminateAction(int totalWork) {
    	progressBar.startDeterminate(totalWork);
    }
    
    public void updateDeterminateActionProgress(int additionalWorkCompleted) {
    	progressBar.updateDeterminateProgress(additionalWorkCompleted);
    }
    
    public void endDeterminateAction() {
    	progressBar.endDeterminate();
    }
    
    public void addPlayer(Player player) {
		playerList.add(player);
	}
	
	public void removePlayer(Player player) {
		playerList.remove(player);
	}
	
	
	public ZoneSelectionPanel getZoneSelectionPanel() {
		return zoneSelectionPanel;
	}
	
    public static void toggleAssetTree() {
        
        if (instance.mainSplitPane.isLeftHidden()) {
            instance.mainSplitPane.showLeft();
        } else {
            instance.mainSplitPane.hideLeft();
        }
    }
    
    public static void addAssetRoot(File rootDir) {
        
    	instance.assetPanel.addAssetRoot(new AssetGroup(rootDir, rootDir.getName()));
    	
        if (instance.mainSplitPane.isLeftHidden()) {
            instance.mainSplitPane.showLeft();
        }
		
		instance.outlookPanel.setActive("Assets");
    }
    
    public static Campaign getCampaign() {
        if (campaign == null) {
            campaign = new Campaign();
        }
        return campaign;
    }
    
    public static void setCampaign(Campaign campaign) {
    	
    	// Clear out the old
    	instance.zoneRendererList.clear();
    	
    	// Load up the new
    	MapToolClient.campaign = campaign;
    	ZoneRenderer currRenderer = null;

        // Clean up
        setCurrentZoneRenderer(null);
        instance.zoneRendererList.clear();
        if (campaign == null) {
            return;
        }

        // Install new campaign
        for (Zone zone : campaign.getZones()) {
            
            ZoneRenderer renderer = ZoneRendererFactory.newRenderer(zone);
            instance.zoneRendererList.add(renderer);
            
            if (currRenderer == null){
                currRenderer = renderer;
            }
        }

    	setCurrentZoneRenderer(currRenderer);
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

	// TODO: I don't like this method name, location, or anything about it.  It sux.  Fix it.
	public static void addZone(Zone zone) {
		
        MapToolClient.getCampaign().putZone(zone);
        
        // TODO: this needs to be abstracted into the client
        if (MapToolClient.isConnected()) {
            ClientConnection conn = MapToolClient.getInstance().getConnection();
            
            conn.callMethod(MapToolClient.COMMANDS.putZone.name(), zone);
        }
        
        MapToolClient.setCurrentZoneRenderer(ZoneRendererFactory.newRenderer(zone));
	}
	
    public void createConnection(String host, int port, Player player) throws UnknownHostException, IOException {

    	this.conn = new MapToolClientConnection(host, port, player);
        this.conn.addMessageHandler(handler);
        this.conn.addActivityListener(activityMonitor);
        this.conn.addActivityListener(new ActivityProgressListener());
        
        this.conn.addDisconnectHandler(new DisconnectHandler() {
        	
        	// TODO: Put this in a better place
			public void handleDisconnect(AbstractConnection conn) {

				showError("Server has disconnected.");
			}
        });
        
        this.conn.start();
    }
    
    public void closeConnection() throws IOException {
        if (this.conn != null) {
            this.conn.close();
        }
    }
    
    public ClientConnection getConnection() {
    	return conn;
    }
    
    public static boolean isConnected() {
        return getInstance().getConnection() != null;
    }

	public static MapToolClient getInstance() {
		return instance;
	}
	
	public static JFileChooser getLoadFileChooser() {
		return instance.loadFileChooser;
	}
	
	public static JFileChooser getSaveFileChooser() {
		return instance.saveFileChooser;
	}
	
	private JFileChooser createLoadFileChooser() {
		JFileChooser fileChooser = new JFileChooser();
		return fileChooser;
	}
	
	private JFileChooser createSaveFileChooser() {
		JFileChooser fileChooser = new JFileChooser();
		return fileChooser;
	}
	
	private ToolboxBar createToolboxPanel() {
		ToolboxBar toolbox = new ToolboxBar();
		
		toolbox.addTool(new PointerTool());
		toolbox.addTool(new MeasuringTool());
        toolbox.addTool(new GridTool());

        toolbox.add(Box.createHorizontalStrut(15));

        toolbox.addTool(new FreehandTool());
        toolbox.addTool(new LineTool());
        toolbox.addTool(new RectangleTool());
        toolbox.addTool(new RectangleFillTool());
        toolbox.addTool(new OvalTool());
        toolbox.addTool(new OvalFillTool());
        
        toolbox.add(Box.createHorizontalStrut(15));
        
        toolbox.add(foregroundColorPicker);
        toolbox.add(backgroundColorPicker);

        return toolbox;
	}
	
	private JMenuBar createMenuBar() {
		
		JMenuBar menuBar = new JMenuBar();

        // ASSET
        JMenu actionMenu = new JMenu("Assets");
        actionMenu.add(new JMenuItem(ClientActions.ADD_ASSET_PANEL));
        
		// FILE
		JMenu fileMenu = new JMenu("File");
		fileMenu.add(new JMenuItem(ClientActions.LOAD_MAP));
		fileMenu.add(new JMenuItem(ClientActions.CREATE_INDEFINITE_MAP));
        fileMenu.addSeparator();
        fileMenu.add(new JMenuItem(ClientActions.NEW_CAMPAIGN));
		fileMenu.add(new JMenuItem(ClientActions.LOAD_CAMPAIGN));
		fileMenu.add(new JMenuItem(ClientActions.SAVE_CAMPAIGN));
		fileMenu.addSeparator();
        fileMenu.add(actionMenu);
        fileMenu.addSeparator();
		fileMenu.add(new JMenuItem(ClientActions.EXIT));

		// SERVER
		JMenu serverMenu = new JMenu("Server");
		serverMenu.add(new JMenuItem(ClientActions.START_SERVER));
		serverMenu.add(new JMenuItem(ClientActions.CONNECT_TO_SERVER));
		
        // VIEW
        JMenu zoomMenu = new JMenu("Zoom");
        zoomMenu.add(new JMenuItem(ClientActions.ZOOM_IN));
        zoomMenu.add(new JMenuItem(ClientActions.ZOOM_OUT));
        zoomMenu.add(new JMenuItem(ClientActions.ZOOM_RESET));

        JMenu viewMenu = new JMenu("View");
        viewMenu.add(zoomMenu);
        viewMenu.addSeparator();
        viewMenu.add(new JMenuItem(ClientActions.TOGGLE_GRID));
        viewMenu.add(new JMenuItem(ClientActions.TOGGLE_ZONE_SELECTOR));
        viewMenu.add(new JMenuItem(ClientActions.TOGGLE_ASSET_PANEL));

        
        // ASSEMBLE
		menuBar.add(fileMenu);
		menuBar.add(serverMenu);
        menuBar.add(viewMenu);

		return menuBar;
	}
	
    public Pen getPen() {
    	
    	pen.setColor(foregroundColorPicker.getSelectedColor().getRGB());
    	pen.setBackgroundColor(backgroundColorPicker.getSelectedColor().getRGB());
        return pen;
    }
	
    public static List<ZoneRenderer> getZoneRenderers() {
        // TODO: This should prob be immutable
        return instance.zoneRendererList;
    }
    
    public static ZoneRenderer getCurrentZoneRenderer() {
        return instance.currentRenderer;
    }
    
	public static void setCurrentZoneRenderer(ZoneRenderer renderer) {
        
        // Handle new renderers
        // TODO: should this be here ?
        if (renderer != null && !instance.zoneRendererList.contains(renderer)) {
            instance.zoneRendererList.add(renderer);
        }

        if (instance.currentRenderer != null) {
        	instance.currentRenderer.flush();
            instance.mainPanel.remove(instance.currentRenderer);
        }
        
        if (renderer != null) {
            instance.mainPanel.add(BorderLayout.CENTER, renderer);
            instance.mainPanel.doLayout();
        }
        
		instance.currentRenderer = renderer;
		instance.toolboxPanel.setTargetRenderer(renderer);
		
		instance.repaint();
	}
	
	public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
        } catch (Exception e) {
            System.err.println("Exception during look and feel setup: " + e);
        }
		
        // Draw frame contents on resize
        Toolkit.getDefaultToolkit().setDynamicLayout(true);        

        instance = new MapToolClient();
		instance.setVisible(true);
	}
	
	private class ActivityProgressListener implements ActivityListener {
		/* (non-Javadoc)
		 * @see net.rptools.clientserver.ActivityListener#notify(net.rptools.clientserver.ActivityListener.Direction, net.rptools.clientserver.ActivityListener.State, int, int)
		 */
		public void notify(Direction direction, State state, int total, int current) {

			if (state == State.Start) {
				MapToolClient.getInstance().startIndeterminateAction();
			} else if (state == State.Complete) {
				MapToolClient.getInstance().endIndeterminateAction();
			}
		}
	}
}
