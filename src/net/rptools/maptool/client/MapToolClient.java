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
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.UIManager;

import net.rptools.clientserver.hessian.client.ClientConnection;
import net.rptools.maptool.client.swing.ColorPickerButton;
import net.rptools.maptool.client.swing.JSplitPaneEx;
import net.rptools.maptool.client.swing.OutlookPanel;
import net.rptools.maptool.client.swing.SwingUtil;
import net.rptools.maptool.client.tool.GridTool;
import net.rptools.maptool.client.tool.MeasuringTool;
import net.rptools.maptool.client.tool.PointerTool;
import net.rptools.maptool.client.tool.drawing.FreehandTool;
import net.rptools.maptool.client.tool.drawing.LineTool;
import net.rptools.maptool.client.tool.drawing.OvalFillTool;
import net.rptools.maptool.client.tool.drawing.OvalTool;
import net.rptools.maptool.client.tool.drawing.RectangleFillTool;
import net.rptools.maptool.client.tool.drawing.RectangleTool;
import net.rptools.maptool.model.Campaign;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.drawing.Pen;
import net.rptools.maptool.server.MapToolServer;

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
    	setZoneGridSize
    };
	
	private static final String WINDOW_TITLE = "MapTool";
	
	// TODO: parameterize this (or make it a preference)
	private static final int WINDOW_WIDTH = 800;
	private static final int WINDOW_HEIGHT = 600;

	// Singleton
	private static MapToolClient instance;
	
    private static MapToolServer server;

    private static Campaign campaign;
    
    private ClientConnection conn;
    private final ClientMethodHandler handler;
    
    private Pen pen = new Pen(Pen.DEFAULT);
    
    // Organization
    
	// Components
	private JFileChooser loadFileChooser;
	private JFileChooser saveFileChooser;
	private ToolboxBar toolboxPanel;
	private OutlookPanel assetPanel;
	private ZoneRenderer currentRenderer;

    private ZoneSelectionPanel zoneSelectionPanel;
    private PositionalPanel mainPanel;
    
    private List<ZoneRenderer> zoneRendererList;
    
	private JSplitPaneEx mainSplitPane;
	
	private ColorPickerButton foregroundColorPicker = new ColorPickerButton("Foreground color", Color.black);
	private ColorPickerButton backgroundColorPicker = new ColorPickerButton("Background color", Color.white);
	
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
		assetPanel = new OutlookPanel ();
        assetPanel.setMinimumSize(new Dimension(150, 200));
        zoneRendererList = new ArrayList<ZoneRenderer>();

        // TODO: Clean up this whole section
        mainPanel = new PositionalPanel();
        mainPanel.setOpaque(false);
        	
        zoneSelectionPanel = new ZoneSelectionPanel();
        zoneSelectionPanel.setSize(400, 50);

        mainPanel.add(zoneSelectionPanel, PositionalLayout.Position.S);
        
		setJMenuBar(createMenuBar());
		
		// Split left/right
		mainSplitPane = new JSplitPaneEx();
		mainSplitPane.setLeftComponent(assetPanel);
		mainSplitPane.setRightComponent(mainPanel);
		mainSplitPane.setInitialDividerPosition(150);
        mainSplitPane.setBorder(null);
		mainSplitPane.hideLeft();
        
		JPanel mainInnerPanel = new JPanel(new BorderLayout());
		mainInnerPanel.setBorder(BorderFactory.createLoweredBevelBorder());
		mainInnerPanel.add(BorderLayout.CENTER, mainSplitPane);
		
		// Put it all together
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, mainInnerPanel);
		add(BorderLayout.NORTH, toolboxPanel);

        //addInnerPanel(assetTreePanel);
		
		// Communications
        handler = new ClientMethodHandler(this);
        
	}
	
	public ZoneSelectionPanel getZoneSelectionPanel() {
		return zoneSelectionPanel;
	}
	
    public static void addAssetTree(AssetTree tree) {
        
        instance.assetPanel.addButton(tree.getTreeName(),  tree);
        if (instance.mainSplitPane.isLeftHidden()) {
            instance.mainSplitPane.showLeft();
        }
    }
    
    public static Campaign getCampaign() {
        if (campaign == null) {
            campaign = new Campaign();
        }
        return campaign;
    }
    
    public static void setCampaign(Campaign campaign) {
    	
    	MapToolClient.campaign = campaign;
    	ZoneRenderer currRenderer = null;
        
        for (Zone zone : campaign.getZones()) {
            
            ZoneRenderer renderer = new ZoneRenderer(zone);
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
		
		// TODO: Allow specifying a campaign
		// TODO: the client and server campaign MUST be different objects.  Figure out a better init method
		campaign = new Campaign();
		MapToolServer server = new MapToolServer (new Campaign(), port);
		ServerPanel serverPanel = new ServerPanel(server.getConnection());
		server.addObserver(serverPanel);

		serverPanel.setSize(175, 100);
		serverPanel.setLocation(0, 0);
		
	}
	
	public static void stopServer() {
		if (server == null) {
			return;
		}
		
		// TODO: server stop
	}

	// TODO: I don't like this method name, location, or anything about it.  It sux.  Fix it.
	public static void addZone(GUID backgroundAssetGUID) {
		
        Zone zone = new Zone(backgroundAssetGUID);
        MapToolClient.getCampaign().putZone(zone);
        
        // TODO: this needs to be abstracted into the client
        if (MapToolClient.isConnected()) {
            ClientConnection conn = MapToolClient.getInstance().getConnection();
            
            conn.callMethod(MapToolClient.COMMANDS.putZone.name(), zone);
        }
        
        MapToolClient.setCurrentZoneRenderer(new ZoneRenderer(zone));
	}
	
    public void createConnection(String host, int port) throws UnknownHostException, IOException {
        this.conn = new ClientConnection(host, port);
        this.conn.addMessageHandler(handler);
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
        fileMenu.add(actionMenu);
		fileMenu.add(new JMenuItem(ClientActions.EXIT));

		
//		// SERVER
//		JMenu serverMenu = new JMenu("Server");
//		JMenuItem connectToServerMenuItem = new JMenuItem(ClientActions.CONNECT_TO_SERVER);
//		JMenuItem startServerMenuItem = new JMenuItem(ClientActions.START_SERVER);
//		
//		serverMenu.add(connectToServerMenuItem);
//		serverMenu.add(startServerMenuItem);
		
        // VIEW
        JMenu viewMenu = new JMenu("View");
        viewMenu.add(new JMenuItem(ClientActions.TOGGLE_GRID));
        viewMenu.add(new JMenuItem(ClientActions.TOGGLE_ZONE_SELECTOR));
        
        // ASSEMBLE
		menuBar.add(fileMenu);
//		menuBar.add(serverMenu);
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
            instance.mainPanel.remove(instance.currentRenderer);
        }
        
        if (renderer != null) {
            instance.mainPanel.add(renderer, PositionalLayout.Position.CENTER);
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

        
		// TODO: Find a better way to show this on startup (something that is easier to re-use on disconnect)
		MainMenuDialog dialog = new MainMenuDialog();
		dialog.setVisible(true);
	}
}
