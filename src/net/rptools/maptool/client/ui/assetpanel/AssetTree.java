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
package net.rptools.maptool.client.ui.assetpanel;

import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeSelectionModel;

import net.rptools.lib.swing.PopupListener;
import net.rptools.maptool.client.AppActions;



/**
 */
public class AssetTree extends JTree implements TreeSelectionListener {

    private Directory selectedDirectory;
    private AssetPanel assetPanel;
	
    public AssetTree(AssetPanel assetPanel) {
        super(new ImageFileTreeModel());
        
		this.assetPanel = assetPanel;
		
        setCellRenderer(new AssetTreeCellRenderer());
        setRootVisible(false);
        
        addMouseListener(createPopupListener());
        addTreeSelectionListener(this);
        
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        getSelectionModel().addTreeSelectionListener(this);
    }

    public void removeRootGroup(Directory dir) {
        
        ((ImageFileTreeModel) getModel()).removeRootGroup(dir);
    }
    
    public void addRootGroup(Directory dir) {
    	
    	((ImageFileTreeModel) getModel()).addRootGroup(dir);
    }
    
    public Directory getSelectedAssetGroup() {
        return selectedDirectory;
    }
    
    private MouseListener createPopupListener() {
        
        
        PopupListener listener = new PopupListener(createPopupMenu());
        
        return listener;
    }
    
    private JPopupMenu createPopupMenu() {
        
        JPopupMenu menu = new JPopupMenu ();
        menu.add(new JMenuItem(AppActions.REMOVE_ASSET_ROOT));
        
        return menu;
    }

    public void refresh() {
        ((ImageFileTreeModel) getModel()).refresh();
    }
    
    ////
    // Tree Selection Listener
    /* (non-Javadoc)
     * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
     */
    public void valueChanged(TreeSelectionEvent e) {

        selectedDirectory = null;
        
        Object node = e.getPath().getLastPathComponent();
        
        if (node instanceof Directory) {

            selectedDirectory = ((Directory) node);
			assetPanel.setDirectory((Directory) node);
        }
    }
}
