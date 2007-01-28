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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import net.rptools.maptool.client.AppActions;
import net.rptools.maptool.client.AppState;
import net.rptools.maptool.client.MRUCampaignManager;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolUtil;
import net.rptools.maptool.client.tool.drawing.DrawableUndoManager;
import net.rptools.maptool.client.ui.MapToolFrame.MTFrame;
import net.rptools.maptool.language.I18N;

public class AppMenuBar extends JMenuBar {
	
	public static MRUCampaignManager mruManager;

    public AppMenuBar() {
        add(createFileMenu());
        add(createEditMenu());
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
        
    	fileMenu.add(new JMenuItem(AppActions.NEW_CAMPAIGN));
    	fileMenu.add(new JMenuItem(AppActions.LOAD_CAMPAIGN));
        fileMenu.add(new JMenuItem(AppActions.SAVE_CAMPAIGN));
        fileMenu.add(new JMenuItem(AppActions.SAVE_CAMPAIGN_AS));
        fileMenu.add(new JMenuItem(AppActions.SAVE_MESSAGE_HISTORY));
        fileMenu.addSeparator();
        fileMenu.add(new JMenuItem(AppActions.EXPORT_SCREENSHOT_LAST_LOCATION));
        fileMenu.add(new JMenuItem(AppActions.EXPORT_SCREENSHOT));
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
    
    protected JMenu createEditMenu() {
        JMenu menu = I18N.createMenu("menu.edit");
        
        // MAP CREATION
        menu.add(new JMenuItem(AppActions.NEW_MAP));
        menu.add(createQuickMapMenu());
        
        // DRAWABLES
        menu.addSeparator();
        menu.add(new JMenuItem(DrawableUndoManager.getInstance().getUndoCommand()));
        menu.add(new JMenuItem(DrawableUndoManager.getInstance().getRedoCommand()));
        menu.add(new JMenuItem(DrawableUndoManager.getInstance().getClearCommand()));

        // MAP TOGGLES
        menu.addSeparator();
        menu.add(new RPCheckBoxMenuItem(AppActions.TOGGLE_CURRENT_ZONE_VISIBILITY));
        menu.add(new RPCheckBoxMenuItem(AppActions.TOGGLE_FOG));

        // GRID
        menu.addSeparator();
        menu.add(new JMenuItem(AppActions.ADJUST_GRID));
        
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
        menu.add(new JMenuItem(AppActions.REMOVE_ZONE));
        
        menu.addSeparator();
        
        menu.add(new JMenuItem(AppActions.SHOW_PREFERENCES));

        return menu;
    }    

    protected JMenu createViewMenu() {
        JMenu menu = I18N.createMenu("menu.view");
        menu.add(createZoomMenu());
        menu.add(new JMenuItem(AppActions.TOGGLE_SHOW_TOKEN_NAMES));

        JCheckBoxMenuItem item = new RPCheckBoxMenuItem(AppActions.TOGGLE_SHOW_MOVEMENT_MEASUREMENTS);
        item.setSelected(AppState.getShowMovementMeasurements());
        menu.add(item);

        item = new RPCheckBoxMenuItem(AppActions.TOGGLE_SHOW_LIGHT_RADIUS);
        item.setSelected(AppState.isShowLightRadius());
        menu.add(item);

        menu.add(new RPCheckBoxMenuItem(AppActions.TOGGLE_GRID));
        menu.add(new RPCheckBoxMenuItem(AppActions.TOGGLE_ASSET_PANEL));
        menu.add(new RPCheckBoxMenuItem(AppActions.TOGGLE_ZONE_SELECTOR));
        menu.add(new RPCheckBoxMenuItem(AppActions.TOGGLE_COMMAND_PANEL));
        
        menu.addSeparator();

        menu.add(new JMenuItem(AppActions.SHOW_FULLSCREEN));


        return menu;
    }

    protected JMenu createQuickMapMenu() {
    	JMenu menu = I18N.createMenu("menu.QuickMap");
    	AppActions.QuickMapAction basicQuickMap = new AppActions.QuickMapAction("Grass", "net/rptools/lib/resource/image/texture/grass.png"); 
    	basicQuickMap.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl shift N"));

    	menu.add(new JMenuItem(basicQuickMap));
        menu.add(new JMenuItem(new AppActions.QuickMapAction("Sand", "net/rptools/lib/resource/image/texture/sand.jpg")));
        menu.add(new JMenuItem(new AppActions.QuickMapAction("Mud", "net/rptools/lib/resource/image/texture/crackedmud.jpg")));
        menu.add(new JMenuItem(new AppActions.QuickMapAction("Ocean", "net/rptools/lib/resource/image/texture/ocean.jpg")));
        menu.add(new JMenuItem(new AppActions.QuickMapAction("Starfield", "net/rptools/lib/resource/image/texture/starfield.jpg")));
        menu.add(new JMenuItem(new AppActions.QuickMapAction("Cobblestone", "net/rptools/lib/resource/image/texture/cobblestone.jpg")));
    	
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
        menu.add(new RPCheckBoxMenuItem(AppActions.TOGGLE_SHOW_PLAYER_VIEW));
        
        menu.addSeparator();

        menu.add(new RPCheckBoxMenuItem(AppActions.TOGGLE_DRAW_MEASUREMENTS));
        menu.add(new RPCheckBoxMenuItem(AppActions.TOGGLE_DOUBLE_WIDE));

        if (MapToolUtil.isDebugEnabled()) {
            menu.addSeparator();
            menu.add(new JMenuItem(AppActions.RANDOMLY_ADD_LAST_ASSET));
        }

        return menu;
    }

    protected JMenu createHelpMenu() {
        JMenu menu = I18N.createMenu("menu.help");
        menu.add(new JMenuItem(AppActions.RESTORE_DEFAULT_IMAGES));
        menu.add(new AbstractAction() {
        	{
        		putValue(Action.NAME, "Restore layout");
        	}
        	public void actionPerformed(ActionEvent e) {
        		MapTool.getFrame().getDockingManager().resetToDefault();
        		
        	}
        });
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
    	
    	for(MTFrame frame : MapToolFrame.MTFrame.values()) {
    		JMenuItem menuItem = new RPCheckBoxMenuItem(new AppActions.ToggleWindowAction(frame));
    		menu.add(menuItem);
    	}
    	
    	return menu;
    }
    
    protected JMenu createRecentCampaignMenu() {
    	JMenu menu = I18N.createMenu("menu.recent");
    	mruManager = new MRUCampaignManager(menu);
    	return mruManager.GetMRUMenu();
    }
}
