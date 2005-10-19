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
package net.rptools.maptool.client.ui.tokenpanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.TreeCellRenderer;

import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.util.ImageManager;

public class TokenTreeCellRenderer extends JPanel implements TreeCellRenderer {

    private BufferedImage image;
    private JLabel tokenNameLabel = new JLabel();
    
    private Color selectedTextColor;
    private Color nonSelectedTextColor;
    private Color selectedBackgroundColor;
    private Color nonSelectedBackgroundColor;
    
    public TokenTreeCellRenderer() {
        setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        add(tokenNameLabel);
        
        selectedTextColor = UIManager.getColor("Tree.selectionForeground");
        nonSelectedTextColor = UIManager.getColor("Tree.textForeground");
        selectedBackgroundColor = UIManager.getColor("Tree.selectionBackground");
        nonSelectedBackgroundColor = UIManager.getColor("Tree.textBackground");
    }
    
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        
        if (value instanceof Token) {
            Token token = (Token) value;
            image = ImageManager.getImage(AssetManager.getAsset(token.getAssetID()));
            tokenNameLabel.setText(token.getName());

        } else {
            image = null;
            tokenNameLabel.setText("");
        }
        
        if (selected) {
            setBackground(selectedBackgroundColor);
            tokenNameLabel.setForeground(selectedTextColor);
        } else {
            setBackground(nonSelectedBackgroundColor);
            tokenNameLabel.setForeground(nonSelectedTextColor);
        }
        
        return this;
    } 

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        
        if (image != null) {
            
            Dimension imageSize = new Dimension(image.getWidth(), image.getHeight());
            SwingUtil.constrainTo(imageSize, getSize().height);
            g.drawImage(image, 0, 0, imageSize.width, imageSize.height, this);
        }
    }
}
