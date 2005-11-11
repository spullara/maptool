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
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import net.rptools.lib.swing.AboutDialog;
import net.rptools.lib.swing.ColorPicker;
import net.rptools.lib.swing.FramePreferences;
import net.rptools.lib.swing.JSplitPaneEx;
import net.rptools.lib.swing.PositionalLayout;
import net.rptools.lib.swing.SwingUtil;
import net.rptools.lib.swing.TaskPanelGroup;
import net.rptools.lib.util.FileUtil;
import net.rptools.lib.util.ImageUtil;
import net.rptools.maptool.client.AppActions;
import net.rptools.maptool.client.AppConstants;
import net.rptools.maptool.client.AppListeners;
import net.rptools.maptool.client.AppPreferences;
import net.rptools.maptool.client.swing.ColorPickerButton;
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
import net.rptools.maptool.client.tool.drawing.OvalFillTool;
import net.rptools.maptool.client.tool.drawing.OvalTool;
import net.rptools.maptool.client.tool.drawing.PolygonExposeTool;
import net.rptools.maptool.client.tool.drawing.PolygonFillTool;
import net.rptools.maptool.client.tool.drawing.PolygonTool;
import net.rptools.maptool.client.tool.drawing.RadiusTemplateTool;
import net.rptools.maptool.client.tool.drawing.RectangleExposeTool;
import net.rptools.maptool.client.tool.drawing.RectangleFillTool;
import net.rptools.maptool.client.tool.drawing.RectangleTool;
import net.rptools.maptool.client.ui.assetpanel.AssetDirectory;
import net.rptools.maptool.client.ui.assetpanel.AssetPanel;
import net.rptools.maptool.client.ui.tokenpanel.TokenPanel;
import net.rptools.maptool.client.ui.zone.NewZoneDropPanel;
import net.rptools.maptool.client.ui.zone.NotificationOverlay;
import net.rptools.maptool.client.ui.zone.PointerOverlay;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.client.ui.zone.ZoneSelectionPanel;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.drawing.Pen;

/**
 */
public class MapToolClient extends JFrame {
    private static final long serialVersionUID = 3905523813025329458L;

	// TODO: parameterize this (or make it a preference)
	private static final int WINDOW_WIDTH = 800;
	private static final int WINDOW_HEIGHT = 600;

    private Pen pen = new Pen(Pen.DEFAULT);
    
	// Components
    private TaskPanelGroup taskPanel;
	private ZoneRenderer currentRenderer;
	private AssetPanel assetPanel;
	private PointerOverlay pointerOverlay;
	private CommandPanel commandPanel;
    private TokenPanel tokenPanel;
    private AboutDialog aboutDialog;
    private ColorPicker colorPicker;
    
    private ZoneSelectionPanel zoneSelectionPanel;
    private JPanel zoneRendererPanel;
    
    private List<ZoneRenderer> zoneRendererList;
    
	private JSplitPaneEx mainSplitPane;
	
	private ColorPickerButton foregroundColorPicker = new ColorPickerButton("Foreground color", Color.black);
	private ColorPickerButton backgroundColorPicker = new ColorPickerButton("Background color", Color.white);
    private PenWidthChooser widthChooser = new PenWidthChooser();

	private StatusPanel statusPanel;
	private ActivityMonitorPanel activityMonitor = new ActivityMonitorPanel();
	private ProgressStatusBar progressBar = new ProgressStatusBar();
    private ConnectionStatusPanel connectionStatusPanel = new ConnectionStatusPanel();
    
	private NewZoneDropPanel newZoneDropPanel;
    
    // TODO: I don't like this here, eventOverlay should be more abstracted
    private NotificationOverlay notificationOverlay = new NotificationOverlay();
	
	public MapToolClient() {
		
		// Set up the frame
		super (AppConstants.APP_NAME);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		SwingUtil.centerOnScreen(this);
        
		// Components
		assetPanel = new AssetPanel();
        tokenPanel = new TokenPanel();
        taskPanel = new TaskPanelGroup(5);
        zoneRendererList = new CopyOnWriteArrayList<ZoneRenderer>();
        pointerOverlay = new PointerOverlay();
        commandPanel = new CommandPanel();
        commandPanel.setSize(250, 100);
        colorPicker = new ColorPicker(this);
        colorPicker.setSize(100, 175);
        colorPicker.setVisible(false);
        
        try {
            String credits = new String(FileUtil.loadResource("net/rptools/maptool/client/credits.html"));
            String version = "DEVELOPMENT";
            if (getClass().getClassLoader().getResource("net/rptools/maptool/client/version.txt") != null) {
                version = new String(FileUtil.loadResource("net/rptools/maptool/client/version.txt"));
            }
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
        
        addWindowListener(new FramePreferences(AppConstants.APP_NAME, this));
        
        restorePreferences();
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
    
	public PointerOverlay getPointerOverlay() {
		return pointerOverlay;
	}
	
	public void setStatusMessage(final String message) {
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
			statusPanel.setStatus(message);
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
		
        // TODO: Put this back in
		//outlookPanel.setActive("Assets");
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
        Tool fillRectTool = new RectangleFillTool();
        Tool ovalTool = new OvalTool();
        Tool fillOvalTool = new OvalFillTool();
        Tool textTool = new TextTool();
        Tool fogRectTool = new RectangleExposeTool();
        Tool fogOvalTool = new OvalExposeTool();
        Tool fogPolyTool = new PolygonExposeTool();
        Tool polyTool = new PolygonTool();
        Tool fillPolyTool = new PolygonFillTool();
        Tool radiusTemplateTool = new RadiusTemplateTool();
        Tool coneTemplateTool = new ConeTemplateTool();
        Tool lineTemplateTool = new LineTemplateTool();
        
        ButtonGroup group = new ButtonGroup();
        group.add(pointerTool);
        group.add(measureTool);
        group.add(freehandTool);
        group.add(lineTool);
        group.add(rectTool);
        group.add(fillRectTool);
        group.add(ovalTool);
        group.add(fillOvalTool);
        group.add(polyTool);
        group.add(fillPolyTool);
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
        toolbar.add(fillPolyTool);
        toolbar.add(rectTool);
        toolbar.add(fillRectTool);
        toolbar.add(ovalTool);
        toolbar.add(fillOvalTool);
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
    
    public void clearZoneRendererList() {
        zoneRendererList.clear();
        zoneSelectionPanel.flush();
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
	
}
