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

import java.awt.BorderLayout;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.AssetGroup;
import net.rptools.maptool.util.FileUtil;


/**
 */
public class AssetTreeModel implements TreeModel {

    private AssetGroup rootAssetGroup;
    private File rootDir;
    
    private static final FilenameFilter IMAGE_FILE_FILTER = new FilenameFilter() {
        public boolean accept(File dir,String name) {
            name = name.toLowerCase();
            return name.endsWith(".bmp") ||
                    name.endsWith(".png") ||
                    name.endsWith(".jpg") ||
                    name.endsWith(".jpeg") ||
                    name.endsWith(".gif");
        }
    };
    
    private static final FilenameFilter DIRECTORY_FILE_FILTER = new FilenameFilter() {
        public boolean accept(File dir,String name) {
            return new File(dir.getPath() + File.separator + name).isDirectory();
        }
    };
    private List<TreeModelListener> listenerList = new ArrayList<TreeModelListener>();
    
    public AssetTreeModel(File rootDir) {
        this.rootDir = rootDir;
        
        try {
            loadData();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            rootAssetGroup = new AssetGroup("Could not load assets");
        }
    }
    
    /* (non-Javadoc)
     * @see javax.swing.tree.TreeModel#getRoot()
     */
    public Object getRoot() {
        return rootAssetGroup;
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
     */
    public Object getChild(Object parent, int index) {

        if (!(parent instanceof AssetGroup)) {
            return null;
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
        
        if (!(parent instanceof AssetGroup)) {
            return 0;
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
        
        assert parent instanceof AssetGroup : "Invalid parent type: " + parent.getClass().getName();
        
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
    
    private void loadData() throws IOException {
        
        // Init condition ?
        if (rootAssetGroup == null) {
            rootAssetGroup = new AssetGroup(rootDir.getName());
        }
        
        // TODO: make sure root still exists
        loadData(rootDir, rootAssetGroup);
    }
    
    private void loadData(File dir, AssetGroup group) throws IOException {
        
        // Update images for this group
        File[] imageFileArray = dir.listFiles(IMAGE_FILE_FILTER);
        for (File file : imageFileArray) {
            
            // TODO: Check that group already has it
            // TODO: don't create new assets for images that are already in the game
            group.add(new Asset(FileUtil.loadFile(file)));
            
            // TODO: remove images that are no longer in the group
        }
        
        // Update subgroups
        File[] subdirArray = dir.listFiles(DIRECTORY_FILE_FILTER);
        for (File subdir : subdirArray) {
            
            // TODO: re-use existing asset groups
            // TODO: keep track of pathing information for change polling
            AssetGroup subgroup = new AssetGroup(subdir.getName());
            
            group.add(subgroup);
            
            loadData(subdir, subgroup);
        }
    }

    public static void main(String[] args) throws IOException {
        
        AssetTreeModel model = new AssetTreeModel(new File("C:/Documents and Settings/tcroft/Desktop/pics"));
        
        JFrame frame = new JFrame("test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(150, 200);
        frame.setLocation(400,300);

        JTree tree = new JTree(model);
        tree.setCellRenderer(new AssetTreeCellRenderer());
        
        frame.getContentPane().add(BorderLayout.CENTER, new JScrollPane(tree));
        
        frame.setVisible(true);
        
        model.refresh();
    }
}
