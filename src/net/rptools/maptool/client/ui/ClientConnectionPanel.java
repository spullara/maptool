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

import java.awt.event.MouseListener;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;

import net.rptools.lib.swing.PopupListener;
import net.rptools.maptool.client.AppActions;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.PlayerListModel;

/**
 */
public class ClientConnectionPanel extends JList {

	public ClientConnectionPanel () {
        setModel(new PlayerListModel(MapTool.getPlayerList()));
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setCellRenderer(new ConnectionCellRenderer());
        
        addMouseListener(createPopupListener());
	}
	
	private static class ConnectionCellRenderer extends DefaultListCellRenderer {
		
		
	}
	
    private MouseListener createPopupListener() {
        
        PopupListener listener = new PopupListener(createPopupMenu());
        
        return listener;
    }
    
    private JPopupMenu createPopupMenu() {
        
        JPopupMenu menu = new JPopupMenu ();
        menu.add(new JMenuItem(AppActions.BOOT_CONNECTED_PLAYER));
        
        return menu;
    }
    

}
