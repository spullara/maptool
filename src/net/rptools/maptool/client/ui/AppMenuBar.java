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

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import net.rptools.maptool.client.AppActions;
import net.rptools.maptool.client.AppState;
import net.rptools.maptool.client.MapToolUtil;
import net.rptools.maptool.client.tool.drawing.DrawableUndoManager;
import net.rptools.maptool.language.I18N;

public class AppMenuBar extends JMenuBar {

    public AppMenuBar() {
        add(createFileMenu());
        add(createViewMenu());
        add(createMapMenu());
        add(createToolsMenu());
        add(createHelpMenu());
    }

    protected JMenu createFileMenu() {
        JMenu menu = I18N.createMenu("menu.file");

        menu.add(new JMenuItem(AppActions.NEW_CAMPAIGN));
        menu.add(new JMenuItem(AppActions.LOAD_CAMPAIGN));
        menu.add(new JMenuItem(AppActions.SAVE_CAMPAIGN));
        menu.add(new JMenuItem(AppActions.SAVE_CAMPAIGN_AS));
        menu.add(new JMenuItem(AppActions.SAVE_MESSAGE_HISTORY));
        menu.addSeparator();
        menu.add(new JMenuItem(AppActions.START_SERVER));
        menu.add(new JMenuItem(AppActions.SHOW_SERVER_INFO));
        menu.add(new JMenuItem(AppActions.CONNECT_TO_SERVER));
        menu.add(new JMenuItem(AppActions.DISCONNECT_FROM_SERVER));
        menu.addSeparator();
        menu.add(new JMenuItem(AppActions.EXIT));

        //        menu.add(createAssetMenu());

        return menu;
    }

    protected JMenu createViewMenu() {
        JMenu menu = I18N.createMenu("menu.view");
        menu.add(createZoomMenu());
        menu.add(new JMenuItem(AppActions.TOGGLE_SHOW_TOKEN_NAMES));
        menu.add(new JMenuItem(AppActions.TOGGLE_GRID));
        menu.add(new JMenuItem(AppActions.TOGGLE_ASSET_PANEL));
        menu.add(new JMenuItem(AppActions.TOGGLE_ZONE_SELECTOR));
        //        menu.add(new JMenuItem(AppActions.SHOW_STATUS_BAR));
        menu.addSeparator();
        //      menu.add(new JMenuItem(AppActions.FULLSCREEN_MODE));
        menu.add(new JMenuItem(AppActions.REFRESH_ASSET_PANEL));

        return menu;
    }

    protected JMenu createMapMenu() {
        JMenu menu = I18N.createMenu("menu.map");
        
        // MAP CREATION
        menu.add(new JMenuItem(AppActions.NEW_MAP));
        menu.add(createQuickMapMenu());
        
        // DRAWABLES
        menu.addSeparator();
        menu.add(new JMenuItem(DrawableUndoManager.getInstance().getUndoCommand()));
        menu.add(new JMenuItem(DrawableUndoManager.getInstance().getRedoCommand()));
        menu.add(new JMenuItem(DrawableUndoManager.getInstance().getClearCommand()));

        menu.addSeparator();
        
        // MAP TOGGLES
        menu.add(new JMenuItem(AppActions.TOGGLE_CURRENT_ZONE_VISIBILITY));
        menu.add(new JMenuItem(AppActions.TOGGLE_FOG));
        menu.add(new JCheckBoxMenuItem(AppActions.TOGGLE_NEW_ZONES_HAVE_FOW));
        menu.add(new JCheckBoxMenuItem(AppActions.TOGGLE_TOKENS_START_SNAP_TO_GRID){{setSelected(true);}}); // HACK: fix this

        menu.addSeparator();
        
        // GRID
        menu.add(new JMenuItem(AppActions.ADJUST_GRID));
        menu.add(new JMenuItem(AppActions.SET_ZONE_GRID_COLOR));
        
        // LATER: This needs to be genericized, but it seems to constant, and so short, that I 
        // didn't feel compelled to do that in this impl
        JMenu gridSizeMenu = I18N.createMenu("action.gridSize");
        JCheckBoxMenuItem gridSize1 = new JCheckBoxMenuItem(new AppActions.GridSizeAction(1));
        JCheckBoxMenuItem gridSize3 = new JCheckBoxMenuItem(new AppActions.GridSizeAction(3));
        JCheckBoxMenuItem gridSize5 = new JCheckBoxMenuItem(new AppActions.GridSizeAction(5));
        ButtonGroup sizeGroup = new ButtonGroup();
        sizeGroup.add(gridSize1);
        sizeGroup.add(gridSize3);
        sizeGroup.add(gridSize5);
        gridSize1.setSelected(true);

        gridSizeMenu.add(gridSize1);
        gridSizeMenu.add(gridSize3);
        gridSizeMenu.add(gridSize5);
        menu.add(gridSizeMenu);
        
        menu.addSeparator();
        menu.add(new JMenuItem(AppActions.REMOVE_ZONE));
        
        return menu;
    }
    
    protected JMenu createQuickMapMenu() {
    	JMenu menu = I18N.createMenu("Quick Map");
    	AppActions.QuickMapAction basicQuickMap = new AppActions.QuickMapAction("Grass", "net/rptools/lib/resource/image/texture/grass.png"); 
    	basicQuickMap.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl shift N"));

    	menu.add(new JMenuItem(basicQuickMap));
        menu.add(new JMenuItem(new AppActions.QuickMapAction("Sand", "net/rptools/lib/resource/image/texture/sand.jpg")));
        menu.add(new JMenuItem(new AppActions.QuickMapAction("Water", "net/rptools/lib/resource/image/texture/water.jpg")));
    	
    	return menu;
    }

    protected JMenu createToolsMenu() {
        JMenu menu = I18N.createMenu("menu.tools");
        menu.add(new JMenuItem(AppActions.CHAT_COMMAND));
        menu.add(new JMenuItem(AppActions.ENTER_COMMAND));
        menu.add(new JMenuItem(AppActions.ENFORCE_ZONE_VIEW));
        menu.add(new JMenuItem(AppActions.ENFORCE_ZONE));
        menu.add(new JCheckBoxMenuItem(AppActions.TOGGLE_LINK_PLAYER_VIEW));
        menu.add(new JMenuItem(AppActions.ADD_ASSET_PANEL));
        menu.addSeparator();
        menu.add(new JCheckBoxMenuItem(AppActions.TOGGLE_DROP_INVISIBLE));
        menu.add(new JCheckBoxMenuItem(AppActions.TOGGLE_NEW_ZONE_VISIBILITY));
        menu.addSeparator();
        JCheckBoxMenuItem item = new JCheckBoxMenuItem(AppActions.TOGGLE_DRAW_MEASUREMENTS);
        item.setSelected(true);
        menu.add(item);
        item = new JCheckBoxMenuItem(AppActions.TOGGLE_DOUBLE_WIDE);
        item.setSelected(AppState.useDoubleWideLine());
        menu.add(item);

        if (MapToolUtil.isDebugEnabled()) {
            menu.addSeparator();
            menu.add(new JMenuItem(AppActions.RANDOMLY_ADD_LAST_ASSET));
        }

        return menu;
    }

    protected JMenu createHelpMenu() {
        JMenu menu = I18N.createMenu("menu.help");
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
}
