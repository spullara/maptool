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

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import net.rptools.maptool.client.swing.ImagePanel;
import net.rptools.maptool.model.AssetGroup;

public class AssetPanel extends JComponent {

	private AssetTree assetTree;
	private ImagePanel imagePanel;
	
	public AssetPanel() {
		
		assetTree = new AssetTree(this);
		imagePanel = new ImagePanel();
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setContinuousLayout(true);
        
		splitPane.setTopComponent(new JScrollPane(assetTree));
		splitPane.setBottomComponent(new JScrollPane(imagePanel));
		splitPane.setDividerLocation(100);
		
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, splitPane);
	}
	
	public void addAssetRoot(AssetGroup assetGroup) {
		
		assetTree.addRootGroup(assetGroup);
	}
	
	public void setAssetGroup(AssetGroup assetGroup) {
		imagePanel.setModel(new AssetGroupImagePanelModel(assetGroup));
	}
}
