/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package net.rptools.maptool.client.ui;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import net.rptools.lib.FileUtil;
import net.rptools.maptool.client.AppActions;
import net.rptools.maptool.client.AppConstants;
import net.rptools.maptool.client.AppSetup;
import net.rptools.maptool.client.AppState;
import net.rptools.maptool.client.AppUtil;
import net.rptools.maptool.client.MRUCampaignManager;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.tool.drawing.DrawableUndoManager;
import net.rptools.maptool.client.ui.MapToolFrame.MTFrame;
import net.rptools.maptool.language.I18N;
import net.rptools.maptool.model.Zone;

public class AppMenuBar extends JMenuBar {
	
	private static MRUCampaignManager mruManager;

    public AppMenuBar() {
        add(createFileMenu());
        add(createEditMenu());
        add(createMapMenu());
        add(createViewMenu());
        add(createToolsMenu());
        add(createWindowMenu());
        add(createHelpMenu());
    }
    
    // This is a hack to allow the menubar shortcut keys to still work even
    // when it isn't showin (fullscreen mode)
    @Override
    public boolean isShowing() {
    	return MapTool.getFrame() != null && MapTool.getFrame().isFullScreen() ? true : super.isShowing();
    }

    protected JMenu createFileMenu() {
        JMenu fileMenu = I18N.createMenu("menu.file");
        
        // MAP CREATION
    	fileMenu.add(new JMenuItem(AppActions.NEW_CAMPAIGN));
    	fileMenu.add(new JMenuItem(AppActions.LOAD_CAMPAIGN));
        fileMenu.add(new JMenuItem(AppActions.SAVE_CAMPAIGN));
        fileMenu.add(new JMenuItem(AppActions.SAVE_CAMPAIGN_AS));
        fileMenu.add(new JMenuItem(AppActions.SAVE_MESSAGE_HISTORY));
        fileMenu.addSeparator();
        fileMenu.add(createExportMenu());
        fileMenu.addSeparator();
        fileMenu.add(new JMenuItem(AppActions.ADD_ASSET_PANEL));
        fileMenu.addSeparator();
        fileMenu.add(new JMenuItem(AppActions.START_SERVER));
        fileMenu.add(new JMenuItem(AppActions.CONNECT_TO_SERVER));
        fileMenu.add(new JMenuItem(AppActions.DISCONNECT_FROM_SERVER));
        fileMenu.add(new JMenuItem(AppActions.SHOW_SERVER_INFO));
        fileMenu.addSeparator();
        fileMenu.add(createRecentCampaignMenu());
        fileMenu.addSeparator();
        fileMenu.add(new JMenuItem(AppActions.EXIT));
                
        return fileMenu;
    }
    
    protected JMenu createExportMenu() {
    	JMenu menu = new JMenu("Export");

    	menu.add(new JMenuItem(AppActions.EXPORT_SCREENSHOT_LAST_LOCATION));
        menu.add(new JMenuItem(AppActions.EXPORT_SCREENSHOT));

        menu.addSeparator();
        
        menu.add(new JMenuItem(AppActions.EXPORT_CAMPAIGN_REPO));
        
        return menu;
    }
    
    protected JMenu createMapMenu() {
    	JMenu menu = I18N.createMenu("menu.map");
    	
        menu.add(new JMenuItem(AppActions.NEW_MAP));
        menu.add(createQuickMapMenu());
    	
        menu.addSeparator();
        
        // MAP TOGGLES
        menu.add(new RPCheckBoxMenuItem(AppActions.TOGGLE_CURRENT_ZONE_VISIBILITY));
        menu.add(new RPCheckBoxMenuItem(AppActions.TOGGLE_FOG));
        menu.add(createVisionTypeMenu());

        menu.addSeparator();
        
        menu.add(new JMenuItem(AppActions.ADJUST_GRID));
        menu.add(new JMenuItem(AppActions.RENAME_ZONE));
        menu.add(new JMenuItem(AppActions.COPY_ZONE));
        menu.add(new JMenuItem(AppActions.REMOVE_ZONE));

        return menu;
    }
    
    protected JMenu createVisionTypeMenu() {
    	JMenu menu = I18N.createMenu("menu.vision");
    	
    	menu.add(new RPCheckBoxMenuItem(new AppActions.SetVisionType(Zone.VisionType.OFF)));
    	menu.add(new RPCheckBoxMenuItem(new AppActions.SetVisionType(Zone.VisionType.DAY)));
    	menu.add(new RPCheckBoxMenuItem(new AppActions.SetVisionType(Zone.VisionType.NIGHT)));
    	
    	return menu;
    }
    
    protected JMenu createEditMenu() {
        JMenu menu = I18N.createMenu("menu.edit");
        
        menu.add(new JMenuItem(DrawableUndoManager.getInstance().getUndoCommand()));
        menu.add(new JMenuItem(DrawableUndoManager.getInstance().getRedoCommand()));
        menu.add(new JMenuItem(DrawableUndoManager.getInstance().getClearCommand()));

        
        menu.addSeparator();
        
        menu.add(new JMenuItem(AppActions.CAMPAIGN_PROPERTIES));
        menu.add(new JMenuItem(AppActions.SHOW_PREFERENCES));

        return menu;
    }    

    protected JMenu createViewMenu() {
        JMenu menu = I18N.createMenu("menu.view");
        menu.add(new RPCheckBoxMenuItem(AppActions.TOGGLE_SHOW_PLAYER_VIEW));

        menu.addSeparator();

        menu.add(createZoomMenu());
        menu.add(new JMenuItem(AppActions.TOGGLE_SHOW_TOKEN_NAMES));

        JCheckBoxMenuItem item = new RPCheckBoxMenuItem(AppActions.TOGGLE_SHOW_MOVEMENT_MEASUREMENTS);
        item.setSelected(AppState.getShowMovementMeasurements());
        menu.add(item);

        item = new RPCheckBoxMenuItem(AppActions.TOGGLE_SHOW_LIGHT_RADIUS);
        item.setSelected(AppState.isShowLightRadius());
        menu.add(item);

        item = new RPCheckBoxMenuItem(AppActions.TOGGLE_SHOW_LIGHT_SOURCES);
        item.setSelected(AppState.isShowLightSources());
        menu.add(item);
        
//        menu.add(new RPCheckBoxMenuItem(AppActions.TOGGLE_ZONE_SELECTOR));
        menu.add(new RPCheckBoxMenuItem(AppActions.TOGGLE_GRID));
        menu.add(new RPCheckBoxMenuItem(AppActions.TOGGLE_COORDINATES));
        // LATER: This needs to be genericized, but it seems to constant, and so short, that I 
        // didn't feel compelled to do that in this impl
        JMenu gridSizeMenu = I18N.createMenu("action.gridSize");
        JCheckBoxMenuItem gridSize1 = new RPCheckBoxMenuItem(new AppActions.GridSizeAction(1));
        JCheckBoxMenuItem gridSize2 = new RPCheckBoxMenuItem(new AppActions.GridSizeAction(2));
        JCheckBoxMenuItem gridSize3 = new RPCheckBoxMenuItem(new AppActions.GridSizeAction(3));
        JCheckBoxMenuItem gridSize5 = new RPCheckBoxMenuItem(new AppActions.GridSizeAction(5));

        ButtonGroup sizeGroup = new ButtonGroup();
        sizeGroup.add(gridSize1);
        sizeGroup.add(gridSize2);
        sizeGroup.add(gridSize3);
        sizeGroup.add(gridSize5);

        gridSizeMenu.add(gridSize1);
        gridSizeMenu.add(gridSize2);
        gridSizeMenu.add(gridSize3);
        gridSizeMenu.add(gridSize5);
        menu.add(gridSizeMenu);
        
        menu.addSeparator();
        menu.add(new RPCheckBoxMenuItem(AppActions.TOGGLE_DRAW_MEASUREMENTS));
        menu.add(new RPCheckBoxMenuItem(AppActions.TOGGLE_DOUBLE_WIDE));

        menu.addSeparator();
        menu.add(new JMenuItem(AppActions.SHOW_FULLSCREEN));


        return menu;
    }

    protected JMenu createQuickMapMenu() {
    	JMenu menu = I18N.createMenu("menu.QuickMap");
    	
    	File textureDir = AppUtil.getAppHome("resource/Default/Textures");
    	
    	// Make sure the images exist
    	if (textureDir.listFiles().length == 0) {
    		try {
    			AppSetup.installDefaultTokens();
    		} catch (IOException ioe) {
    			ioe.printStackTrace();
    			menu.add(new JMenuItem("Error loading quickmaps"));
    			return menu;
    		}
    	}
    	
    	for (File file : textureDir.listFiles(AppConstants.IMAGE_FILE_FILTER)) {
    		
            menu.add(new JMenuItem(new AppActions.QuickMapAction(FileUtil.getNameWithoutExtension(file), file)));
    	}
    	
//    	basicQuickMap.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl shift N"));

    	return menu;
    }

    protected JMenu createToolsMenu() {
        JMenu menu = I18N.createMenu("menu.tools");
        menu.add(new JMenuItem(AppActions.CHAT_COMMAND));
        menu.add(new JMenuItem(AppActions.ENTER_COMMAND));
        menu.add(new JMenuItem(AppActions.ENFORCE_ZONE_VIEW));
        menu.add(new JMenuItem(AppActions.ENFORCE_ZONE));
        menu.add(new RPCheckBoxMenuItem(AppActions.TOGGLE_LINK_PLAYER_VIEW));
        menu.add(new RPCheckBoxMenuItem(AppActions.TOGGLE_MOVEMENT_LOCK));
        
        menu.add(new JSeparator());
        
        menu.add(new RPCheckBoxMenuItem(AppActions.TOGGLE_COLLECT_PROFILING_DATA));
        
        return menu;
    }

    protected JMenu createHelpMenu() {
        JMenu menu = I18N.createMenu("menu.help");
        menu.add(new JMenuItem(AppActions.ADD_DEFAULT_TABLES));
        menu.add(new JMenuItem(AppActions.RESTORE_DEFAULT_IMAGES));
        menu.add(new JMenuItem(AppActions.SHOW_DOCUMENTATION));
        menu.add(new JMenuItem(AppActions.SHOW_TUTORIALS));
        menu.add(new JMenuItem(AppActions.SHOW_FORUMS));
        menu.addSeparator();
        menu.add(new JMenuItem(AppActions.SHOW_ABOUT));
        return menu;
    }

    protected JMenu createZoomMenu() {
        JMenu menu = I18N.createMenu("menu.zoom");
        menu.add(new JMenuItem(AppActions.ZOOM_IN));
        menu.add(new JMenuItem(AppActions.ZOOM_OUT));
        menu.add(new JMenuItem(AppActions.ZOOM_RESET));

        return menu;
    }
    
    protected JMenu createWindowMenu() {
    	JMenu menu = I18N.createMenu("menu.window");
    	
        menu.add(new AbstractAction() {
        	{
        		putValue(Action.NAME, "Restore layout");
        	}
        	public void actionPerformed(ActionEvent e) {
        		MapTool.getFrame().getDockingManager().resetToDefault();
        		
        	}
        });

    	menu.addSeparator();

    	for(MTFrame frame : MapToolFrame.MTFrame.values()) {
    		JMenuItem menuItem = new RPCheckBoxMenuItem(new AppActions.ToggleWindowAction(frame));
    		menu.add(menuItem);
    	}

        menu.addSeparator();
        menu.add(new JMenuItem(AppActions.SHOW_TRANSFER_WINDOW));

        return menu;
    }
    
    protected JMenu createRecentCampaignMenu() {
    	mruManager = new MRUCampaignManager(new JMenu(AppActions.MRU_LIST));
    	return mruManager.getMRUMenu();
    }
    
    public static MRUCampaignManager getMruManager() {
    	return mruManager;
    }
}
