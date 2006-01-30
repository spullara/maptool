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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import net.rptools.lib.FileUtil;
import net.rptools.lib.image.ImageUtil;
import net.rptools.lib.swing.AboutDialog;
import net.rptools.lib.swing.ColorPicker;
import net.rptools.lib.swing.JSplitPaneEx;
import net.rptools.lib.swing.PositionalLayout;
import net.rptools.lib.swing.SwingUtil;
import net.rptools.lib.swing.TaskPanelGroup;
import net.rptools.lib.swing.preference.FramePreferences;
import net.rptools.lib.swing.preference.SplitPanePreferences;
import net.rptools.lib.swing.preference.TaskPanelGroupPreferences;
import net.rptools.maptool.client.AppActions;
import net.rptools.maptool.client.AppConstants;
import net.rptools.maptool.client.AppListeners;
import net.rptools.maptool.client.AppPreferences;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.swing.MemoryStatusBar;
import net.rptools.maptool.client.swing.PenWidthChooser;
import net.rptools.maptool.client.swing.ProgressStatusBar;
import net.rptools.maptool.client.swing.StatusPanel;
import net.rptools.maptool.client.tool.MeasureTool;
import net.rptools.maptool.client.tool.PointerTool;
import net.rptools.maptool.client.tool.TextTool;
import net.rptools.maptool.client.tool.drawing.ConeTemplateTool;
import net.rptools.maptool.client.tool.drawing.FreehandTool;
import net.rptools.maptool.client.tool.drawing.LineTemplateTool;
import net.rptools.maptool.client.tool.drawing.LineTool;
import net.rptools.maptool.client.tool.drawing.OvalExposeTool;
import net.rptools.maptool.client.tool.drawing.OvalTool;
import net.rptools.maptool.client.tool.drawing.PolygonExposeTool;
import net.rptools.maptool.client.tool.drawing.PolygonTool;
import net.rptools.maptool.client.tool.drawing.RadiusTemplateTool;
import net.rptools.maptool.client.tool.drawing.RectangleExposeTool;
import net.rptools.maptool.client.tool.drawing.RectangleTool;
import net.rptools.maptool.client.ui.assetpanel.AssetDirectory;
import net.rptools.maptool.client.ui.assetpanel.AssetPanel;
import net.rptools.maptool.client.ui.tokenpanel.TokenPanel;
import net.rptools.maptool.client.ui.zone.NewZoneDropPanel;
import net.rptools.maptool.client.ui.zone.NotificationOverlay;
import net.rptools.maptool.client.ui.zone.PointerOverlay;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.client.ui.zone.ZoneSelectionPanel;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.drawing.Pen;

/**
 */
public class MapToolFrame extends JFrame implements WindowListener {
    private static final long serialVersionUID = 3905523813025329458L;

	// TODO: parameterize this (or make it a preference)
	private static final int WINDOW_WIDTH = 800;
	private static final int WINDOW_HEIGHT = 600;

    private Pen pen = new Pen(Pen.DEFAULT);
    
    /**
     * Are the drawing measurements being painted?
     */
    private boolean paintDrawingMeasurement = true;
    
	// Components
    private TaskPanelGroup taskPanel;
	private ZoneRenderer currentRenderer;
	private AssetPanel assetPanel;
	private PointerOverlay pointerOverlay;
	private CommandPanel commandPanel;
    private TokenPanel tokenPanel;
    private AboutDialog aboutDialog;
    private ColorPicker colorPicker;
    private NewMapDialog newMapDialog;
    
    private ZoneSelectionPanel zoneSelectionPanel;
    private JPanel zoneRendererPanel;
    
    private List<ZoneRenderer> zoneRendererList;
    
	private JSplitPaneEx mainSplitPane;
	
    private PenWidthChooser widthChooser = new PenWidthChooser();

	private StatusPanel statusPanel;
	private ActivityMonitorPanel activityMonitor = new ActivityMonitorPanel();
	private ProgressStatusBar progressBar = new ProgressStatusBar();
    private ConnectionStatusPanel connectionStatusPanel = new ConnectionStatusPanel();
    
	private NewZoneDropPanel newZoneDropPanel;
    
    // TODO: I don't like this here, eventOverlay should be more abstracted
    private NotificationOverlay notificationOverlay = new NotificationOverlay();
	
	public MapToolFrame() {
		
		// Set up the frame
		super (AppConstants.APP_NAME);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(this);
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		SwingUtil.centerOnScreen(this);
        
		// Components
		assetPanel = createAssetPanel();
        tokenPanel = new TokenPanel();
        taskPanel = new TaskPanelGroup(5);
        new TaskPanelGroupPreferences(AppConstants.APP_NAME, "TaskPanel", taskPanel);
        
        zoneRendererList = new CopyOnWriteArrayList<ZoneRenderer>();
        pointerOverlay = new PointerOverlay();
        commandPanel = new CommandPanel();
        commandPanel.setSize(250, 100);
        colorPicker = new ColorPicker(this);
        colorPicker.setSize(colorPicker.getMinimumSize());
        colorPicker.setVisible(false);
        
        try {
            String credits = new String(FileUtil.loadResource("net/rptools/maptool/client/credits.html"));
            String version = MapTool.getVersion();
            credits = credits.replace("%VERSION%", version);
            Image logo = ImageUtil.getImage("net/rptools/lib/image/rptools-logo.png");
        	
            aboutDialog = new AboutDialog(this, logo, credits);
        } catch (IOException ioe) {
        	// This won't happen
        }

        taskPanel.add("Image Explorer", assetPanel);
        taskPanel.add("Tokens", tokenPanel);
        taskPanel.add("Connections", new JScrollPane(createPlayerList()));
        
        statusPanel = new StatusPanel();
        statusPanel.addPanel(new MemoryStatusBar());
        statusPanel.addPanel(progressBar);
        statusPanel.addPanel(connectionStatusPanel);
        statusPanel.addPanel(activityMonitor);
        
        zoneSelectionPanel = new ZoneSelectionPanel();
        zoneSelectionPanel.setSize(100, 100);
        AppListeners.addZoneListener(zoneSelectionPanel);

        newZoneDropPanel = new NewZoneDropPanel();
        
        zoneRendererPanel = new JPanel(new PositionalLayout(5));
        zoneRendererPanel.setBackground(Color.black);
        zoneRendererPanel.add(newZoneDropPanel, PositionalLayout.Position.CENTER);
        zoneRendererPanel.add(commandPanel, PositionalLayout.Position.SW);
        zoneRendererPanel.add(zoneSelectionPanel, PositionalLayout.Position.SE);
        zoneRendererPanel.add(colorPicker, PositionalLayout.Position.NE);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(BorderLayout.CENTER, zoneRendererPanel);
        
		// Split left/right
		mainSplitPane = new JSplitPaneEx();
		mainSplitPane.setLeftComponent(taskPanel);
		mainSplitPane.setRightComponent(mainPanel);
		mainSplitPane.setInitialDividerPosition(150);
        mainSplitPane.setBorder(null);
        
		JPanel mainInnerPanel = new JPanel(new BorderLayout());
		mainInnerPanel.setBorder(BorderFactory.createLoweredBevelBorder());
		mainInnerPanel.add(BorderLayout.CENTER, mainSplitPane);
		
		// Put it all together
        setJMenuBar(new AppMenuBar());
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, mainInnerPanel);
		add(BorderLayout.NORTH, createToolboxPanel());
		add(BorderLayout.SOUTH, statusPanel);
        
        new FramePreferences(AppConstants.APP_NAME, "mainFrame", this);
        new SplitPanePreferences(AppConstants.APP_NAME, "mainSplitPane", mainSplitPane);
        
        restorePreferences();
	}
    
	public NewMapDialog getNewMapDialog() {
        if (newMapDialog == null) {
            newMapDialog = new NewMapDialog(this);
        }
		return newMapDialog;
	}
	
    public ColorPicker getColorPicker() {
        return colorPicker;
    }
    
	public void showAboutDialog() {
		aboutDialog.setVisible(true);
	}
	
    public ConnectionStatusPanel getConnectionStatusPanel() {
        return connectionStatusPanel;
    }
    
    public NotificationOverlay getNotificationOverlay() {
        return notificationOverlay;
    }
    
    private void restorePreferences() {
        
        List<File> assetRootList = AppPreferences.getAssetRoots();
        for (File file : assetRootList) {
            addAssetRoot(file);
        }
    }
    
    private AssetPanel createAssetPanel() {
        final AssetPanel panel = new AssetPanel("mainAssetPanel");
        panel.addImagePanelMouseListener(new MouseAdapter(){
            @Override
            public void mouseReleased(MouseEvent e) {
                // TODO use for real popup logic
                if (SwingUtilities.isRightMouseButton(e)) {

                    List<Object> idList = panel.getSelectedIds();
                    if (idList == null || idList.size() == 0) {
                        return;
                    }
                    
                    final int index = (Integer) idList.get(0);
                    
                    JPopupMenu menu = new JPopupMenu();
                    menu.add(new JMenuItem(new AbstractAction() {
                        {
                            putValue(NAME, "New Bounded Map");
                        }

                        public void actionPerformed(ActionEvent e) {

                            createZone(panel.getAsset(index), Zone.Type.MAP);
                        }
                    }));
                    menu.add(new JMenuItem(new AbstractAction() {
                        {
                            putValue(NAME, "New Unbounded Map");
                        }
                        public void actionPerformed(ActionEvent e) {
                            createZone(panel.getAsset(index), Zone.Type.INFINITE);
                        }
                    }));
                    
                    panel.showImagePanelPopup(menu, e.getX(), e.getY());
                }
            }
            
            private void createZone(Asset asset, int type) {
                
                if (!AssetManager.hasAsset(asset)) {
                    
                    AssetManager.putAsset(asset);
                    MapTool.serverCommand().putAsset(asset);
                }

                Zone zone = new Zone(type, asset.getId());
                
                MapTool.addZone(zone);
            }
        });
        
        return panel;
    }
    
	public PointerOverlay getPointerOverlay() {
		return pointerOverlay;
	}
	
	public void setStatusMessage(final String message) {
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
			statusPanel.setStatus("  " + message);
			}
		});
	}
	
    protected JComponent createPlayerList() {
        
    	ClientConnectionPanel panel = new ClientConnectionPanel();
        
        return panel;
    }
    
    public ActivityMonitorPanel getActivityMonitor() {
        return activityMonitor;
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
    
	public ZoneSelectionPanel getZoneSelectionPanel() {
		return zoneSelectionPanel;
	}
    
    ///////////////////////////////////////////////////////////////////////////
    // static methods
    ///////////////////////////////////////////////////////////////////////////
    
    public void toggleAssetTree() {
        
        if (mainSplitPane.isLeftHidden()) {
            mainSplitPane.showLeft();
        } else {
            mainSplitPane.hideLeft();
        }
    }
    
    public CommandPanel getCommandPanel() {
    	return commandPanel;
    }
    
    public AssetPanel getAssetPanel() {
      return assetPanel;
    }
    
    public void addAssetRoot(File rootDir) {
        
    	assetPanel.addAssetRoot(new AssetDirectory(rootDir, AppConstants.IMAGE_FILE_FILTER));
    	
        if (mainSplitPane.isLeftHidden()) {
            mainSplitPane.showLeft();
        }
    }
    
	private JComponent createToolboxPanel() {

        JPanel panel = new JPanel(new GridBagLayout());
        
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setRollover(true);

        // Tools
        Tool pointerTool = new PointerTool();
        Tool measureTool = new MeasureTool();
        Tool freehandTool = new FreehandTool();
        Tool lineTool = new LineTool();
        Tool rectTool = new RectangleTool();
        Tool ovalTool = new OvalTool();
        Tool textTool = new TextTool();
        Tool fogRectTool = new RectangleExposeTool();
        Tool fogOvalTool = new OvalExposeTool();
        Tool fogPolyTool = new PolygonExposeTool();
        Tool polyTool = new PolygonTool();
        Tool radiusTemplateTool = new RadiusTemplateTool();
        Tool coneTemplateTool = new ConeTemplateTool();
        Tool lineTemplateTool = new LineTemplateTool();
        
        ButtonGroup group = new ButtonGroup();
        group.add(pointerTool);
        group.add(measureTool);
        group.add(freehandTool);
        group.add(lineTool);
        group.add(rectTool);
        group.add(ovalTool);
        group.add(polyTool);
        group.add(textTool);
        group.add(fogRectTool);
        group.add(fogOvalTool);
        group.add(fogPolyTool);
        group.add(radiusTemplateTool);
        group.add(coneTemplateTool);
        group.add(lineTemplateTool);

        // Initialy selected
        pointerTool.setSelected(true);
        
        // Organize
        toolbar.add(pointerTool);
        toolbar.add(measureTool);

        toolbar.add(Box.createHorizontalStrut(15));
        
        toolbar.add(freehandTool);
        toolbar.add(lineTool);
        toolbar.add(polyTool);
        toolbar.add(rectTool);
        toolbar.add(ovalTool);
        toolbar.add(textTool);
        toolbar.add(radiusTemplateTool);
        toolbar.add(coneTemplateTool);
        toolbar.add(lineTemplateTool);

        toolbar.add(Box.createHorizontalStrut(15));
        
        toolbar.add(fogRectTool);
        toolbar.add(fogOvalTool);
        toolbar.add(fogPolyTool);
        
        toolbar.add(Box.createHorizontalStrut(15));

        toolbar.add(widthChooser);

        toolbar.add(Box.createHorizontalStrut(15));
        
        toolbar.add(new JToggleButton(AppActions.TOGGLE_GRID){{setText("");}});
        toolbar.add(new JToggleButton(AppActions.TOGGLE_SHOW_TOKEN_NAMES){{setText("");}});
        
        GridBagConstraints constraints = new GridBagConstraints();
        panel.add(toolbar, constraints);
        
        constraints.weightx = 1;
        constraints.gridx = 1;
        panel.add(new JLabel(), constraints);
        return panel;
	}
	
    public Pen getPen() {
    	
    	pen.setColor(colorPicker.getForegroundColor().getRGB());
    	pen.setBackgroundColor(colorPicker.getBackgroundColor().getRGB());
        pen.setThickness((Integer)widthChooser.getSelectedItem());
        return pen;
    }
	
    public List<ZoneRenderer> getZoneRenderers() {
        // TODO: This should prob be immutable
        return zoneRendererList;
    }
    
    public ZoneRenderer getCurrentZoneRenderer() {
        return currentRenderer;
    }
    
    public void addZoneRenderer(ZoneRenderer renderer) {
        zoneRendererList.add(renderer);
    }
    
    public void removeZoneRenderer(ZoneRenderer renderer) {
    	
    	boolean isCurrent = renderer == getCurrentZoneRenderer();
    	
    	zoneRendererList.remove(renderer);
    	if (isCurrent) {
    		setCurrentZoneRenderer(zoneRendererList.size() > 0 ? zoneRendererList.get(0) : null);
    	}
    	
    	zoneSelectionPanel.flush();
    	zoneSelectionPanel.repaint();
    }
    
    public void clearZoneRendererList() {
        zoneRendererList.clear();
        zoneSelectionPanel.flush();
    	zoneSelectionPanel.repaint();
    }
	public void setCurrentZoneRenderer(ZoneRenderer renderer) {
        
        // Handle new renderers
        // TODO: should this be here ?
        if (renderer != null && !zoneRendererList.contains(renderer)) {
            zoneRendererList.add(renderer);
        }

        // Handle first renderer
        if (newZoneDropPanel != null) {
        	zoneRendererPanel.remove(newZoneDropPanel);
        	newZoneDropPanel = null;
        }
        
        if (currentRenderer != null) {
        	currentRenderer.flush();
            zoneRendererPanel.remove(currentRenderer);
        }
        
        if (renderer != null) {
            zoneRendererPanel.add(renderer, PositionalLayout.Position.CENTER);
            zoneRendererPanel.doLayout();
        }
        
		currentRenderer = renderer;
		Toolbox.setTargetRenderer(renderer);

        tokenPanel.setZoneRenderer(renderer);
        
		if (renderer != null) {
			AppListeners.fireZoneActivated(renderer.getZone());
		}
		
		AppActions.updateActions();
		repaint();
	}
	
	public ZoneRenderer getZoneRenderer(Zone zone) {
		
		for (ZoneRenderer renderer : zoneRendererList) {
			
			if (zone == renderer.getZone()) {
				return renderer;
			}
		}
		
		return null;
	}

	public ZoneRenderer getZoneRenderer(GUID zoneGUID) {
		
		for (ZoneRenderer renderer : zoneRendererList) {
			
			if (zoneGUID.equals(renderer.getZone().getId())) {
				return renderer;
			}
		}
		
		return null;
	}

  /**
   * Get the paintDrawingMeasurements for this MapToolClient.
   *
   * @return Returns the current value of paintDrawingMeasurements.
   */
  public boolean isPaintDrawingMeasurement() {
    return paintDrawingMeasurement;
  }

  /**
   * Set the value of paintDrawingMeasurements for this MapToolClient.
   *
   * @param aPaintDrawingMeasurements The paintDrawingMeasurements to set.
   */
  public void setPaintDrawingMeasurement(boolean aPaintDrawingMeasurements) {
    paintDrawingMeasurement = aPaintDrawingMeasurements;
  }
	
  ////
  // WINDOW LISTENER
  public void windowOpened(WindowEvent e){}
  public void windowClosing(WindowEvent e){
	  
	  if (MapTool.isHostingServer()) {
		  if (!MapTool.confirm("You are hosting a server.  Shutting down will disconnect all players.  Are you sure?")) {
			  return;
		  }
	  }

	  MapTool.disconnect();
	  
	  // We're done
	  System.exit(0);
  }
  public void windowClosed(WindowEvent e){}
  public void windowIconified(WindowEvent e){}
  public void windowDeiconified(WindowEvent e){}
  public void windowActivated(WindowEvent e){}
  public void windowDeactivated(WindowEvent e){}
  
}
