/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
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
        super(assetPanel.getModel().getImageFileTreeModel());
        
		this.assetPanel = assetPanel;

        setCellRenderer(new AssetTreeCellRenderer());
        
        addMouseListener(createPopupListener());
        addTreeSelectionListener(this);
        
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        getSelectionModel().addTreeSelectionListener(this);
    }

    @Override
    public int getRowHeight() {
    	return -1;
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

        // Keep memory tight
        // TODO: make this an option
        if (selectedDirectory != null) {
            selectedDirectory.refresh();
        }
        
        selectedDirectory = null;
        
        Object node = e.getPath().getLastPathComponent();
        
        if (node instanceof Directory) {

            selectedDirectory = ((Directory) node);
			assetPanel.setDirectory((Directory) node);
        }
    }
}
