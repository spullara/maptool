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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import net.rptools.lib.swing.ImagePanel;
import net.rptools.lib.swing.ImagePanel.SelectionMode;
import net.rptools.lib.swing.preference.SplitPanePreferences;
import net.rptools.maptool.client.AppConstants;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.model.Zone;

public class AssetPanel extends JComponent {

	private AssetTree assetTree;
	private ImagePanel imagePanel;
    
    private AssetPanelModel assetPanelModel;
	
    public AssetPanel(String controlName) {
        this(controlName, new AssetPanelModel());
    }
    public AssetPanel(String controlName, AssetPanelModel model) {
		
        assetPanelModel = model;
        model.addImageUpdateObserver(this);

        assetTree = new AssetTree(this);
		imagePanel = new ImagePanel();
		
		imagePanel.setShowCaptions(false);
		imagePanel.setSelectionMode(SelectionMode.SINGLE);
        
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setContinuousLayout(true);
        
		splitPane.setTopComponent(new JScrollPane(assetTree));
		splitPane.setBottomComponent(new JScrollPane(imagePanel));
		splitPane.setDividerLocation(100);
		
        new SplitPanePreferences(AppConstants.APP_NAME, controlName, splitPane);
        
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, splitPane);
	}

    public List<Object> getSelectedIds() {
        return imagePanel.getSelectedIds();
    }
    
    public void showImagePanelPopup(JPopupMenu menu, int x, int y) {
        
        menu.show(imagePanel, x, y);
    }
    
    // TODO: Find a way around this, it's ugly
    public Asset getAsset(int index) {
        return ((ImageFileImagePanelModel)imagePanel.getModel()).getAsset(index);        
    }
    
    public void addImagePanelMouseListener(MouseListener listener) {
        imagePanel.addMouseListener(listener);
    }
    
    public void removeImagePanelMouseListener(MouseListener listener) {
        imagePanel.removeMouseListener(listener);
    }
    
    public AssetPanelModel getModel() {
        return assetPanelModel;
    }
    
    public boolean isAssetRoot(Directory dir) {
        return ((ImageFileTreeModel) assetTree.getModel()).isRootGroup(dir);
    }
    
    public void removeAssetRoot(Directory dir) {
        assetPanelModel.removeRootGroup(dir);
    }
    
    public Directory getSelectedAssetRoot() {
        return assetTree.getSelectedAssetGroup();
    }
    
	public void addAssetRoot(Directory dir) {
		
		assetPanelModel.addRootGroup(dir);
	}
	
	public void setDirectory(Directory dir) {
		imagePanel.setModel(new ImageFileImagePanelModel(dir));
	}
  
    public AssetTree getAssetTree() {
      return assetTree;
    }
}
