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

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import net.rptools.maptool.client.tool.drawing.DrawableUndoManager;

public class AppMenuBar extends JMenuBar {

    public AppMenuBar() {

        add(createFileMenu());
        add(createEditMenu());
        add(createViewMenu());
        add(createServerMenu());
        add(createGMMenu());
    }

    protected JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new JMenuItem(AppActions.LOAD_MAP));
        fileMenu.add(new JMenuItem(AppActions.CREATE_INDEFINITE_MAP));
        fileMenu.addSeparator();
        fileMenu.add(new JMenuItem(AppActions.NEW_CAMPAIGN));
        fileMenu.add(new JMenuItem(AppActions.LOAD_CAMPAIGN));
        fileMenu.add(new JMenuItem(AppActions.SAVE_CAMPAIGN));
        fileMenu.addSeparator();
        fileMenu.add(createAssetMenu());
        fileMenu.addSeparator();
        fileMenu.add(new JMenuItem(AppActions.EXIT));

        return fileMenu;
    }
    
    protected JMenu createEditMenu() {
        
        JMenu editMenu = new JMenu("Edit");
        editMenu.add(new JMenuItem(DrawableUndoManager.getInstance().getUndoCommand()));
        editMenu.add(new JMenuItem(DrawableUndoManager.getInstance().getRedoCommand()));
        
        return editMenu;
    }
    
    protected JMenu createAssetMenu() {
        JMenu assetMenu = new JMenu("Assets");
        assetMenu.add(new JMenuItem(AppActions.ADD_ASSET_PANEL));

        return assetMenu;
    }
    
    protected JMenu createServerMenu() {
        JMenu serverMenu = new JMenu("Server");
        serverMenu.add(new JMenuItem(AppActions.START_SERVER));
        serverMenu.add(new JMenuItem(AppActions.CONNECT_TO_SERVER));
        
        return serverMenu;
    }
    
    protected JMenu createGMMenu() {
        JMenu gmMenu = new JMenu("GM");
        gmMenu.add(new JMenuItem(AppActions.ADJUST_GRID));
        
        return gmMenu;
    }
    
    protected JMenu createZoomMenu() {
        JMenu zoomMenu = new JMenu("Zoom");
        zoomMenu.add(new JMenuItem(AppActions.ZOOM_IN));
        zoomMenu.add(new JMenuItem(AppActions.ZOOM_OUT));
        zoomMenu.add(new JMenuItem(AppActions.ZOOM_RESET));

        return zoomMenu;
    }
    
    protected JMenu createViewMenu() {
        JMenu viewMenu = new JMenu("View");
        viewMenu.add(createZoomMenu());
        viewMenu.addSeparator();
        //viewMenu.add(new JMenuItem(ClientActions.TOGGLE_GRID));
        viewMenu.add(new JMenuItem(AppActions.TOGGLE_ZONE_SELECTOR));
        viewMenu.add(new JMenuItem(AppActions.TOGGLE_ASSET_PANEL));

        return viewMenu;
    }
}
