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

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.AssetGroup;


/**
 */
public class AssetTreeModel implements TreeModel {

    private List<AssetGroup> rootAssetGroups = new ArrayList<AssetGroup>();

    private Object root = new String("");
    
    private List<TreeModelListener> listenerList = new ArrayList<TreeModelListener>();
    
    public AssetTreeModel() {
    }
    
    /* (non-Javadoc)
     * @see javax.swing.tree.TreeModel#getRoot()
     */
    public Object getRoot() {
        return root;
    }

    public void addRootGroup (AssetGroup group) {
    	rootAssetGroups.add(group);
    	refresh();
    }
    
    /* (non-Javadoc)
     * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
     */
    public Object getChild(Object parent, int index) {

    	if (parent == root) {
    		return rootAssetGroups.get(index);
    	}
    	
        AssetGroup group = (AssetGroup) parent;
        
        int childGroupCount = group.getChildGroupCount();
        
        List list = null;
        if (index >= childGroupCount) {
            index -= childGroupCount;
            list = group.getAssets();
        } else {
            list = group.getChildGroups();
        }
        
        return list.get(index);
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
     */
    public int getChildCount(Object parent) {
        
        if (parent == root) {
            return rootAssetGroups.size();
        }
        
        AssetGroup group = (AssetGroup) parent;
        
        return group.getChildGroupCount() + group.getAssetCount();
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
     */
    public boolean isLeaf(Object node) {
        return node instanceof Asset;
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath, java.lang.Object)
     */
    public void valueForPathChanged(TreePath path, Object newValue) {
        // Nothing to do right now
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object, java.lang.Object)
     */
    public int getIndexOfChild(Object parent, Object child) {

    	if (parent == root) {
    		return rootAssetGroups.indexOf(child);
    	}
    	
        AssetGroup group = (AssetGroup) parent;

        if (child instanceof AssetGroup) {
            return group.indexOf((AssetGroup) child);
        }
        
        int index = group.indexOf((Asset) child);
        
        return index >= 0 ? index + group.getChildGroupCount() : -1;
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
     */
    public void addTreeModelListener(TreeModelListener l) {
        listenerList.add(l);
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeModel#removeTreeModelListener(javax.swing.event.TreeModelListener)
     */
    public void removeTreeModelListener(TreeModelListener l) {
        listenerList.remove(l);
    }

    public void refresh() {

        // TODO: This closes the tree.  Don't like that.  Fix it.
        TreeModelEvent e = new TreeModelEvent(this, new Object[]{getRoot()}, 
                new int[]{0}, new Object[]{});
        for (TreeModelListener listener : listenerList) {
            listener.treeStructureChanged(e);
        }
    }
    

}
