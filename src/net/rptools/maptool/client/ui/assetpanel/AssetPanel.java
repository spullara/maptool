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
	
	public AssetPanel(String controlName) {
		
		assetTree = new AssetTree(this);
		imagePanel = new ImagePanel();
		
		imagePanel.setShowCaptions(false);
		imagePanel.setSelectionMode(SelectionMode.SINGLE);
		// TODO: Make this not an aic
		imagePanel.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO use for real popup logic
				if (SwingUtilities.isRightMouseButton(e)) {

					List<Object> idList = imagePanel.getSelectedIds();
					if (idList == null || idList.size() == 0) {
						return;
					}
					
					final int index = (Integer) idList.get(0);
					
					JPopupMenu menu = new JPopupMenu();
					menu.add(new JMenuItem(new AbstractAction() {
						{
							putValue(NAME, "New Bounded Map");
						}

						public void actionPerformed(ActionEvent e) {

							// TODO: Combine this code with the code for unbounded
							Asset asset = ((ImageFileImagePanelModel)imagePanel.getModel()).getAsset(index);
							if (!AssetManager.hasAsset(asset)) {
								
								AssetManager.putAsset(asset);
								MapTool.serverCommand().putAsset(asset);
							}
							
							Zone zone = new Zone(Zone.Type.MAP, asset.getId());
							zone.setType(Zone.Type.MAP);
							
		                    MapTool.addZone(zone);
						}
					}));
					menu.add(new JMenuItem(new AbstractAction() {
						{
							putValue(NAME, "New Unbounded Map");
						}
						public void actionPerformed(ActionEvent e) {

							Asset asset = ((ImageFileImagePanelModel)imagePanel.getModel()).getAsset(index);
							if (!AssetManager.hasAsset(asset)) {
								
								AssetManager.putAsset(asset);
								MapTool.serverCommand().putAsset(asset);
							}

							Zone zone = new Zone(Zone.Type.INFINITE, asset.getId());
							zone.setType(Zone.Type.INFINITE);
							
		                    MapTool.addZone(zone);
						}
					}));
					
					menu.show(imagePanel, e.getX(), e.getY());
				}
			}
		});
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setContinuousLayout(true);
        
		splitPane.setTopComponent(new JScrollPane(assetTree));
		splitPane.setBottomComponent(new JScrollPane(imagePanel));
		splitPane.setDividerLocation(100);
		
        new SplitPanePreferences(AppConstants.APP_NAME, controlName, splitPane);
        
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, splitPane);
	}
	
    public boolean isAssetRoot(Directory dir) {
        return ((ImageFileTreeModel) assetTree.getModel()).isRootGroup(dir);
    }
    
    public void removeAssetRoot(Directory dir) {
        assetTree.removeRootGroup(dir);
    }
    
    public Directory getSelectedAssetRoot() {
        return assetTree.getSelectedAssetGroup();
    }
    
	public void addAssetRoot(Directory dir) {
		
		assetTree.addRootGroup(dir);
	}
	
	public void setDirectory(Directory dir) {
		imagePanel.setModel(new ImageFileImagePanelModel(dir));
	}
  
  public AssetTree getAssetTree() {
    return assetTree;
  }
}
