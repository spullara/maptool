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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.rptools.lib.FileUtil;
import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.ClientStyle;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.util.ImageManager;

public class AdjustGridDialog extends JDialog {

    private static final int BORDER_SPACING = 10;
    
    private GridAdjustmentPanel adjustmentPanel;
    private Zone zone;
    private Point topLeft;
    private Point bottomRight;
    private Scale scale;
    
    public AdjustGridDialog() {
        super((JFrame)null, "Adjust Grid", true);
        setLayout(new BorderLayout());
        initialize();
    }
    
    protected void initialize() {

        adjustmentPanel = new GridAdjustmentPanel();
        

        add(BorderLayout.CENTER, adjustmentPanel);
    }

    public void adjust(Zone zone) {
        this.zone = zone;
        
        BufferedImage background = ImageManager.getImage(AssetManager.getAsset(zone.getAssetID()), (JPanel)getContentPane());
        topLeft = new Point(0, 0);
        bottomRight = new Point(background.getWidth(), background.getHeight());
        
        scale = new Scale();
        
        setVisible(true);
    }
    
    private class GridAdjustmentPanel extends JComponent {
       
        @Override
        protected void paintComponent(Graphics g) {
            
            Graphics2D g2d = (Graphics2D) g;
            Dimension size = getSize();

            // Background
            BufferedImage background = ImageManager.getImage(AssetManager.getAsset(zone.getAssetID()), (JPanel)getContentPane());
            Dimension imgSize = new Dimension(background.getWidth(), background.getHeight());
            SwingUtil.constrainTo(imgSize, size.width-BORDER_SPACING*2, size.height-BORDER_SPACING*2);
            g2d.drawImage(background, BORDER_SPACING, BORDER_SPACING, imgSize.width, imgSize.height, null);
            
            // Points
            Composite oldComposite = g2d.getComposite();
            
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, .25f));
            g2d.setColor(ClientStyle.selectionBoxFill);

            g2d.fillOval(topLeft.x-5+BORDER_SPACING, topLeft.y-5+BORDER_SPACING, 10, 10);
            g2d.fillOval(bottomRight.x-5+BORDER_SPACING, bottomRight.y-5+BORDER_SPACING, 10, 10);
            
            g2d.setComposite(oldComposite);

            Stroke oldStroke = g2d.getStroke();
            
            g2d.setColor(ClientStyle.selectionBoxOutline);
            g2d.setStroke(new BasicStroke(2));
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.drawOval(topLeft.x-5+BORDER_SPACING, topLeft.y-5+BORDER_SPACING, 10, 10);
            g2d.drawOval(bottomRight.x-5+BORDER_SPACING, bottomRight.y-5+BORDER_SPACING, 10, 10);

            g2d.setStroke(oldStroke);

            g2d.drawLine(topLeft.x-1+BORDER_SPACING, topLeft.y+BORDER_SPACING, topLeft.x+1+BORDER_SPACING, topLeft.y+BORDER_SPACING);
            g2d.drawLine(topLeft.x+BORDER_SPACING, topLeft.y-1+BORDER_SPACING, topLeft.x+BORDER_SPACING, topLeft.y+1+BORDER_SPACING);
            
            g2d.drawLine(bottomRight.x-1+BORDER_SPACING, bottomRight.y+BORDER_SPACING, bottomRight.x+1+BORDER_SPACING, bottomRight.y+BORDER_SPACING);
            g2d.drawLine(bottomRight.x+BORDER_SPACING, bottomRight.y-1+BORDER_SPACING, bottomRight.x+BORDER_SPACING, bottomRight.y+1+BORDER_SPACING);
            
            // Magnifier
            
        }
    }
    
    public static void main(String[] args) throws Exception {
        
        Asset asset = new Asset(FileUtil.loadResource("net/rptools/maptool/client/image/map.jpg"));
        AssetManager.putAsset(asset);
        ImageManager.getImage(asset, null);
        Thread.sleep(200);
        
        Zone z = new Zone(Zone.Type.MAP, asset.getId());
        
        AdjustGridDialog d = new AdjustGridDialog();
        d.setDefaultCloseOperation(JDialog.EXIT_ON_CLOSE);
        d.setSize(300, 400);
        
        d.adjust(z);
    }
}
