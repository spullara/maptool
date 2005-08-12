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

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeSelectionModel;

import net.rptools.common.swing.PopupListener;
import net.rptools.maptool.client.ui.model.Directory;
import net.rptools.maptool.client.ui.model.ImageFileTreeModel;
import net.rptools.maptool.model.AssetGroup;



/**
 */
public class AssetTree extends JTree implements TreeSelectionListener {

    private AssetGroup selectedAssetGroup;
    private AssetPanel assetPanel;
	
    public AssetTree(AssetPanel assetPanel) {
        super(new ImageFileTreeModel());
        
		this.assetPanel = assetPanel;
		
        setCellRenderer(new AssetTreeCellRenderer());
        setRootVisible(false);
        
        addMouseListener(createPopupListener());
        addTreeSelectionListener(this);
        
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    }

    public void addRootGroup(Directory dir) {
    	
    	((ImageFileTreeModel) getModel()).addRootGroup(dir);
    }
    
    public AssetGroup getSelectedAssetGroup() {
        return selectedAssetGroup;
    }
    
    private MouseListener createPopupListener() {
        
        
        PopupListener listener = new PopupListener(createPopupMenu());
        
        return listener;
    }
    
    private JPopupMenu createPopupMenu() {
        
        JPopupMenu menu = new JPopupMenu ();
        menu.add(new JMenuItem("Nothing to see here"));
        
        return menu;
    }

    public void refresh() {
//        ((AssetTreeModel) getModel()).refresh();
    }
    
    ////
    // Tree Selection Listener
    /* (non-Javadoc)
     * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
     */
    public void valueChanged(TreeSelectionEvent e) {

        selectedAssetGroup = null;
        
        Object node = e.getPath().getLastPathComponent();
        
        if (node instanceof Directory) {
			
			assetPanel.setDirectory((Directory) node);
        }
    }
}
