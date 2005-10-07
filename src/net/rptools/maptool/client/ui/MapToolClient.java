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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import net.rptools.common.swing.FramePreferences;
import net.rptools.common.swing.JSplitPaneEx;
import net.rptools.common.swing.MultiSelectToggleButton;
import net.rptools.common.swing.OutlookPanel;
import net.rptools.common.swing.PositionalLayout;
import net.rptools.common.swing.SwingUtil;
import net.rptools.common.swing.ToggleGroup;
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
import net.rptools.maptool.client.tool.drawing.FreehandTool;
import net.rptools.maptool.client.tool.drawing.LineTool;
import net.rptools.maptool.client.tool.drawing.OvalExposeTool;
import net.rptools.maptool.client.tool.drawing.OvalFillTool;
import net.rptools.maptool.client.tool.drawing.OvalTool;
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
	private OutlookPanel outlookPanel;
	private ZoneRenderer currentRenderer;
	private AssetPanel assetPanel;
	private PointerOverlay pointerOverlay;
	private ChatPanel chatPanel;
    private TokenPanel tokenPanel;
    
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
		outlookPanel = new OutlookPanel ();
        outlookPanel.setMinimumSize(new Dimension(100, 200));
        zoneRendererList = new CopyOnWriteArrayList<ZoneRenderer>();
        pointerOverlay = new PointerOverlay();
        chatPanel = new ChatPanel();
        chatPanel.setSize(250, 100);

        outlookPanel.addButton("Assets", assetPanel);
        outlookPanel.addButton("Tokens", tokenPanel);
        outlookPanel.addButton("Connections", new JScrollPane(createPlayerList()));
        
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
        zoneRendererPanel.add(chatPanel, PositionalLayout.Position.SW);
        zoneRendererPanel.add(zoneSelectionPanel, PositionalLayout.Position.SE);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(BorderLayout.CENTER, zoneRendererPanel);
        
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
        setJMenuBar(new AppMenuBar());
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, mainInnerPanel);
		add(BorderLayout.NORTH, createToolboxPanel());
		add(BorderLayout.SOUTH, statusPanel);
        
        addWindowListener(new FramePreferences(AppConstants.APP_NAME, this));
        
        restorePreferences();
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
    
    public ChatPanel getChatPanel() {
    	return chatPanel;
    }
    
    public AssetPanel getAssetPanel() {
      return assetPanel;
    }
    
    public void addAssetRoot(File rootDir) {
        
    	assetPanel.addAssetRoot(new AssetDirectory(rootDir, AppConstants.IMAGE_FILE_FILTER));
    	
        if (mainSplitPane.isLeftHidden()) {
            mainSplitPane.showLeft();
        }
		
		outlookPanel.setActive("Assets");
    }
    
	private JComponent createToolboxPanel() {

        JPanel panel = new JPanel(new GridBagLayout());
        
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setRollover(true);

        // SELECT
        MultiSelectToggleButton selectToolSelect = new MultiSelectToggleButton();
        selectToolSelect.addToggleButton(new PointerTool());
        selectToolSelect.selectButton(0); // Default Tool
        
        // MEASURE
        MultiSelectToggleButton measureToolSelect = new MultiSelectToggleButton();
        measureToolSelect.addToggleButton(new MeasureTool());

        // LINE
        MultiSelectToggleButton lineToolSelect = new MultiSelectToggleButton();
        lineToolSelect.addToggleButton(new FreehandTool());
        lineToolSelect.addToggleButton(new LineTool());
        
        // Rect
        MultiSelectToggleButton rectToolSelect = new MultiSelectToggleButton();
        rectToolSelect.addToggleButton(new RectangleTool());
        rectToolSelect.addToggleButton(new RectangleFillTool());

        // Oval
        MultiSelectToggleButton ovalToolSelect = new MultiSelectToggleButton();
        ovalToolSelect.addToggleButton(new OvalTool());
        ovalToolSelect.addToggleButton(new OvalFillTool());

        // Fog
        MultiSelectToggleButton fogToolSelect = new MultiSelectToggleButton();
        fogToolSelect.addToggleButton(new RectangleExposeTool());
        fogToolSelect.addToggleButton(new OvalExposeTool());
        
        ToggleGroup group = new ToggleGroup();
        group.addToggle(selectToolSelect);
        group.addToggle(measureToolSelect);
        group.addToggle(lineToolSelect);
        group.addToggle(rectToolSelect);
        group.addToggle(ovalToolSelect);
        group.addToggle(fogToolSelect);

        // Organize
        toolbar.add(selectToolSelect);
        toolbar.add(measureToolSelect);

        toolbar.add(Box.createHorizontalStrut(15));
        
        toolbar.add(lineToolSelect);
        toolbar.add(rectToolSelect);
        toolbar.add(ovalToolSelect);

        toolbar.add(Box.createHorizontalStrut(15));
        
        toolbar.add(fogToolSelect);
        
        toolbar.add(Box.createHorizontalStrut(15));

        toolbar.add(foregroundColorPicker);
        toolbar.add(backgroundColorPicker);
        toolbar.add(Box.createHorizontalStrut(3));
        toolbar.add(widthChooser);

        toolbar.add(Box.createHorizontalStrut(15));
        
        toolbar.add(new JToggleButton(AppActions.TOGGLE_GRID){{setText("");}});
        toolbar.add(new JToggleButton(AppActions.TOGGLE_SHOW_TOKEN_NAMES){{setText("");}});
        toolbar.add(new JToggleButton(AppActions.TOGGLE_DROP_INVISIBLE){{setText("");}});
        toolbar.add(new JToggleButton(AppActions.TOGGLE_NEW_ZONE_VISIBILITY){{setText("");}});
        
        GridBagConstraints constraints = new GridBagConstraints();
        panel.add(toolbar, constraints);
        
        constraints.weightx = 1;
        constraints.gridx = 1;
        panel.add(new JLabel(), constraints);
        return panel;
	}
	
    public Pen getPen() {
    	
    	pen.setColor(foregroundColorPicker.getSelectedColor().getRGB());
    	pen.setBackgroundColor(backgroundColorPicker.getSelectedColor().getRGB());
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
