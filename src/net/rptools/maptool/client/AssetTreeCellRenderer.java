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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.tree.DefaultTreeCellRenderer;

import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.AssetGroup;
import net.rptools.maptool.util.ImageManager;


/**
 */
public class AssetTreeCellRenderer extends DefaultTreeCellRenderer {

    private static final Border SELECTED_BORDER = BorderFactory.createLineBorder(Color.blue);
    private static final int PADDING = 2;
    
    private Asset currentAsset;
    
    /* (non-Javadoc)
     * @see javax.swing.tree.DefaultTreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
     */
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {

        setBorder(null);
        
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        if (value instanceof AssetGroup) {
            setText(((AssetGroup) value).getName());
            currentAsset = null;
        } else if (value instanceof Asset) {

        	currentAsset = (Asset) value;
        	
            setText("");
            setIconTextGap(0);
            if(sel) {
                setBorder(SELECTED_BORDER);
            }
            
            setPreferredSize(new Dimension(100, 100));
        }
        
        return this;
    }
    
    /* (non-Javadoc)
	 * @see javax.swing.tree.DefaultTreeCellRenderer#paint(java.awt.Graphics)
	 */
	public void paint(Graphics g) {
		super.paint(g);
		
		Dimension size = getSize();
		
		if (currentAsset != null) {

			BufferedImage img = ImageManager.getImage(currentAsset);
			Dimension mySize = getSize();
			
			int width = img.getWidth();
            int height = img.getHeight();

            int targetHeight = (mySize.height * 2);
            
            width = (int)(width * (targetHeight / (double)height));
            height = targetHeight;

            g.drawImage(img, PADDING, (mySize.height - targetHeight) / 2, width, height, this);
		}
	}
}
