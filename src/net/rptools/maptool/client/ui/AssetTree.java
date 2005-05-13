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

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeSelectionModel;

import net.rptools.maptool.client.swing.PopupListener;
import net.rptools.maptool.client.ui.model.AssetTreeModel;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.AssetGroup;



/**
 */
public class AssetTree extends JTree implements TreeSelectionListener, DragGestureListener, DragSourceListener {

    private AssetGroup selectedAssetGroup;
    private Asset selectedAsset;
    private AssetPanel assetPanel;
	
    public AssetTree(AssetPanel assetPanel) {
        super(new AssetTreeModel());
        
		this.assetPanel = assetPanel;
		
        setCellRenderer(new AssetTreeCellRenderer());
        setRootVisible(false);
        
        addMouseListener(createPopupListener());
        addTreeSelectionListener(this);
        
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        
        DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY, this);        
    }

    public void addRootGroup(AssetGroup group) {
    	
    	((AssetTreeModel) getModel()).addRootGroup(group);
    }
    
    public AssetGroup getSelectedAssetGroup() {
        return selectedAssetGroup;
    }
    
    public Asset getSelectedAsset() {
        return selectedAsset;
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
        ((AssetTreeModel) getModel()).refresh();
    }
    
    ////
    // Tree Selection Listener
    /* (non-Javadoc)
     * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
     */
    public void valueChanged(TreeSelectionEvent e) {

        selectedAsset = null;
        selectedAssetGroup = null;
        
        Object node = e.getPath().getLastPathComponent();
        
        if (node instanceof Asset) {
            selectedAsset = (Asset) node;
        }
        
        if (node instanceof AssetGroup) {
            selectedAssetGroup = (AssetGroup) node;
			
			assetPanel.setAssetGroup(selectedAssetGroup);
        }
    }
    
    ////
    // DRAG GESTURE LISTENER
    /* (non-Javadoc)
     * @see java.awt.dnd.DragGestureListener#dragGestureRecognized(java.awt.dnd.DragGestureEvent)
     */
    public void dragGestureRecognized(DragGestureEvent dge) {
        
//        if (selectedAsset == null) {
//            return;
//        }
//        
//        Image img = ImageManager.getImage(selectedAsset);
//        
//        Transferable transferable = new TransferableAsset(selectedAsset);
//        
//        dge.startDrag(Toolkit.getDefaultToolkit().createCustomCursor(img, new Point(0, 0), "Thumbnail"), transferable, this);
    }
    
    ////
    // DRAG SOURCE LISTENER
    /* (non-Javadoc)
     * @see java.awt.dnd.DragSourceListener#dragDropEnd(java.awt.dnd.DragSourceDropEvent)
     */
    public void dragDropEnd(DragSourceDropEvent dsde) {
    }
    /* (non-Javadoc)
     * @see java.awt.dnd.DragSourceListener#dragEnter(java.awt.dnd.DragSourceDragEvent)
     */
    public void dragEnter(DragSourceDragEvent dsde) {
        // TODO Auto-generated method stub

    }
    /* (non-Javadoc)
     * @see java.awt.dnd.DragSourceListener#dragExit(java.awt.dnd.DragSourceEvent)
     */
    public void dragExit(DragSourceEvent dse) {
        // TODO Auto-generated method stub

    }
    /* (non-Javadoc)
     * @see java.awt.dnd.DragSourceListener#dragOver(java.awt.dnd.DragSourceDragEvent)
     */
    public void dragOver(DragSourceDragEvent dsde) {
        // TODO Auto-generated method stub

    }
    /* (non-Javadoc)
     * @see java.awt.dnd.DragSourceListener#dropActionChanged(java.awt.dnd.DragSourceDragEvent)
     */
    public void dropActionChanged(DragSourceDragEvent dsde) {
        // TODO Auto-generated method stub

    }
}
