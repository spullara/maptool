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
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;

import net.rptools.maptool.client.AppActions;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.swing.ColorPickerButton;
import net.rptools.maptool.client.swing.JSplitPaneEx;
import net.rptools.maptool.client.swing.MemoryStatusBar;
import net.rptools.maptool.client.swing.OutlookPanel;
import net.rptools.maptool.client.swing.PenWidthChooser;
import net.rptools.maptool.client.swing.ProgressStatusBar;
import net.rptools.maptool.client.swing.StatusPanel;
import net.rptools.maptool.client.swing.SwingUtil;
import net.rptools.maptool.client.tool.MeasuringTool;
import net.rptools.maptool.client.tool.PointerTool;
import net.rptools.maptool.client.tool.drawing.FreehandTool;
import net.rptools.maptool.client.tool.drawing.LineTool;
import net.rptools.maptool.client.tool.drawing.OvalFillTool;
import net.rptools.maptool.client.tool.drawing.OvalTool;
import net.rptools.maptool.client.tool.drawing.RectangleFillTool;
import net.rptools.maptool.client.tool.drawing.RectangleTool;
import net.rptools.maptool.client.ui.model.PlayerListModel;
import net.rptools.maptool.model.AssetGroup;
import net.rptools.maptool.model.drawing.Pen;

/**
 */
public class MapToolClient extends JFrame {
    private static final long serialVersionUID = 3905523813025329458L;

	private static final String WINDOW_TITLE = "MapTool";
	
	// TODO: parameterize this (or make it a preference)
	private static final int WINDOW_WIDTH = 800;
	private static final int WINDOW_HEIGHT = 600;

    private Pen pen = new Pen(Pen.DEFAULT);
    
	// Components
	private ToolboxBar toolboxPanel;
	private OutlookPanel outlookPanel;
	private ZoneRenderer currentRenderer;
	private AssetPanel assetPanel;
	private PointerOverlay pointerOverlay;

    private ZoneSelectionPanel zoneSelectionPanel;
    private JPanel mainPanel;
    
    private List<ZoneRenderer> zoneRendererList;
    
	private JSplitPaneEx mainSplitPane;
	
	private ColorPickerButton foregroundColorPicker = new ColorPickerButton("Foreground color", Color.black);
	private ColorPickerButton backgroundColorPicker = new ColorPickerButton("Background color", Color.white);
    private PenWidthChooser widthChooser = new PenWidthChooser();

	private StatusPanel statusPanel;
	private ActivityMonitorPanel activityMonitor = new ActivityMonitorPanel();
	private ProgressStatusBar progressBar = new ProgressStatusBar();
    
    private List<String> messages = new ArrayList<String>();

	public MapToolClient() {
		
		// Set up the frame
		super (WINDOW_TITLE);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		SwingUtil.centerOnScreen(this);
        
		// Components
		toolboxPanel = createToolboxPanel();
		assetPanel = new AssetPanel();
		outlookPanel = new OutlookPanel ();
        outlookPanel.setMinimumSize(new Dimension(100, 200));
        zoneRendererList = new ArrayList<ZoneRenderer>();
        pointerOverlay = new PointerOverlay();

        outlookPanel.addButton("Connections", createPlayerList());
        outlookPanel.addButton("Assets", assetPanel);
        
        statusPanel = new StatusPanel();
        statusPanel.addPanel(new MemoryStatusBar());
        statusPanel.addPanel(progressBar);
        statusPanel.addPanel(activityMonitor);
        
        mainPanel = new JPanel(new BorderLayout());
        zoneSelectionPanel = new ZoneSelectionPanel();
        mainPanel.add(BorderLayout.SOUTH, zoneSelectionPanel);
        
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
		add(BorderLayout.NORTH, toolboxPanel);
		add(BorderLayout.SOUTH, statusPanel);
	}
    
	public PointerOverlay getPointerOverlay() {
		return pointerOverlay;
	}
	
    protected JComponent createPlayerList() {
        
        JList list = new JList();
        list.setModel(new PlayerListModel(MapTool.getPlayerList()));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        return list;
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
    
    public List<String> getMessages() {
        return messages;
    }
    
    public void addMessage(String message) {
        messages.add(message);
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
    
    public AssetPanel getAssetPanel() {
      return assetPanel;
    }
    
    public void addAssetRoot(File rootDir) {
        
    	assetPanel.addAssetRoot(new AssetGroup(rootDir, rootDir.getName()));
    	
        if (mainSplitPane.isLeftHidden()) {
            mainSplitPane.showLeft();
        }
		
		outlookPanel.setActive("Assets");
    }
    
    public ToolboxBar getToolbox () {
    	return toolboxPanel;
    }
    
	private ToolboxBar createToolboxPanel() {
		ToolboxBar toolbox = new ToolboxBar();
		
		toolbox.addTool(new PointerTool());
		toolbox.addTool(new MeasuringTool());

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
        toolbox.add(Box.createHorizontalStrut(3));
        toolbox.add(widthChooser);

        toolbox.add(Box.createHorizontalStrut(15));
        
        toolbox.add(new JToggleButton(AppActions.TOGGLE_GRID));
        
        return toolbox;
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
    
    public void addZoneRendererList(ZoneRenderer renderer) {
        zoneRendererList.add(renderer);
    }
    
    public void clearZoneRendererList() {
        zoneRendererList.clear();
    }
	public void setCurrentZoneRenderer(ZoneRenderer renderer) {
        
        // Handle new renderers
        // TODO: should this be here ?
        if (renderer != null && !zoneRendererList.contains(renderer)) {
            zoneRendererList.add(renderer);
        }

        if (currentRenderer != null) {
        	currentRenderer.flush();
            mainPanel.remove(currentRenderer);
        }
        
        if (renderer != null) {
            mainPanel.add(BorderLayout.CENTER, renderer);
            mainPanel.doLayout();
        }
        
		currentRenderer = renderer;
		toolboxPanel.setTargetRenderer(renderer);
		
		repaint();
	}
	
}
