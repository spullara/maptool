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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import net.rptools.maptool.model.AssetGroup;


/**
 */
public class ImageFileTreeModel implements TreeModel {

    private List<Directory> rootDirectories = new ArrayList<Directory>();

    private Object root = new String("");
    
    private List<TreeModelListener> listenerList = new ArrayList<TreeModelListener>();
    
    public ImageFileTreeModel() {
    }
    
    public boolean isRootGroup(Directory dir) {
        return rootDirectories.contains(dir);
    }
    
    /* (non-Javadoc)
     * @see javax.swing.tree.TreeModel#getRoot()
     */
    public Object getRoot() {
        return root;
    }

    public boolean containsRootGroup(Directory dir) {
    	
    	for (Directory directory : rootDirectories) {
    		if (directory.getPath().equals(dir.getPath())) {
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    public void addRootGroup (Directory directory) {
    	rootDirectories.add(directory);
    	Collections.sort(rootDirectories, Directory.COMPARATOR);
        fireStructureChangedEvent(new TreeModelEvent(this, new Object[]{getRoot()}, 
          new int[] { rootDirectories.size() - 1 }, new Object[] {directory}));
    }
    
    public void removeRootGroup (Directory directory) {
        rootDirectories.remove(directory);
        fireStructureChangedEvent(new TreeModelEvent(this, new Object[]{getRoot()}));
    }
    
    /* (non-Javadoc)
     * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
     */
    public Object getChild(Object parent, int index) {

    	if (parent == root) {
    		return rootDirectories.get(index);
    	}
    	
        Directory dir = (Directory) parent;
        
        return dir.getSubDirs().get(index);
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
     */
    public int getChildCount(Object parent) {
        
        if (parent == root) {
            return rootDirectories.size();
        }
        
        Directory dir = (Directory) parent;
        
        return dir.getSubDirs().size();
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
     */
    public boolean isLeaf(Object node) {
		// No leaves here
        return false;
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
    		return rootDirectories.indexOf(child);
    	}
    	
        Directory dir = (Directory) parent;

        return dir.getSubDirs().indexOf(child);
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
    }

    private void fireStructureChangedEvent(TreeModelEvent e) {
      TreeModelListener[] listeners = listenerList.toArray(new TreeModelListener[listenerList.size()]);
      for (TreeModelListener listener : listeners) {
        listener.treeStructureChanged(e);
      }
    }
    
    private void fireNodesInsertedEvent(TreeModelEvent e) {
      TreeModelListener[] listeners = listenerList.toArray(new TreeModelListener[listenerList.size()]);
      for (TreeModelListener listener : listeners) {
        listener.treeNodesInserted(e);
      }
    }

    

}
