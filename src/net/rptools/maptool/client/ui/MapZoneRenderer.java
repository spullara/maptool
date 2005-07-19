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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import net.rptools.maptool.client.ClientStyle;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.util.ImageManager;

public class MapZoneRenderer extends ZoneRenderer {

    private int     width;
    private int     height;

    public MapZoneRenderer (Zone zone) {
        super(zone);
    }
    
    
    public BufferedImage getBackgroundImage() {
        
        if (backgroundImage != null) { return backgroundImage; }
        if (zone == null) { return null; }
        
        Asset asset = AssetManager.getAsset(zone.getAssetID());
        if (asset == null) {

            MapTool.serverCommand().getAsset(zone.getAssetID());
        	
            return ImageManager.UNKNOWN_IMAGE;
        } 

        backgroundImage = ImageManager.getImage(asset);
        
        width = backgroundImage.getWidth(this);
        height = backgroundImage.getHeight(this);

        return backgroundImage;
    }
    
    public Point getCellAt(int x,int y) {
        
        Point p = super.getCellAt(x, y);
        
        if (p.x < 0 || p.y < 0 || p.x > (width / zone.getGridSize()) || p.y > (height / zone.getGridSize())) {
            p = null;
        }
        
        return p;
    }
    
    protected void renderBorder(Graphics2D g) {
        
        Dimension size = getSize();
        
        // Scale
        float scale = scaleArray[scaleIndex];
        int w = (int)(width * scale);
        int h = (int)(height * scale);

        // Border
//        if (offsetX > 0) {
//            g.setColor(Color.black);
//            g.fillRect(0, 0, offsetX, size.height);
//        }
//        if (offsetY > 0) {
//            g.setColor(Color.black);
//            g.fillRect(0, 0, size.width, offsetY);
//        }
//        if (w + offsetX < size.width) {
//            g.setColor(Color.black);
//            g.fillRect(w + offsetX, 0, size.width, size.height);
//        }
//        if (h + offsetY < size.height) {
//            g.setColor(Color.black);
//            g.fillRect(0, h + offsetY, size.width, size.height);
//        }
        
        ClientStyle.boardBorder.paintAround((Graphics2D) g, offsetX, offsetY, w, h);
    }
    
    protected void renderBoard(Graphics2D g) {

        Dimension size = getSize();
        
        // Scale
        float scale = scaleArray[scaleIndex];
        int w = (int)(width * scale);
        int h = (int)(height * scale);

        if (offsetX > size.width - EDGE_LIMIT) {
            offsetX = size.width - EDGE_LIMIT;
        }
        
        if (offsetX + w < EDGE_LIMIT) {
            offsetX = EDGE_LIMIT - w;
        }
        
        if (offsetY > size.height - EDGE_LIMIT) {
            offsetY = size.height - EDGE_LIMIT;
        }
        
        if (offsetY + h < EDGE_LIMIT) {
            offsetY = EDGE_LIMIT - h;
        }
        
        // Map
        g.drawImage(backgroundImage, offsetX, offsetY, w, h, this);
    }
    
    protected void renderGrid(Graphics2D g) {
        
        float scale = scaleArray[scaleIndex];
        int w = (int)(width * scale);
        int h = (int)(height * scale);

        float gridSize = zone.getGridSize() * scale;

        // Render grid
        g.setColor(gridColor);

        int x = offsetX + (int) (zone.getGridOffsetX() * scaleArray[scaleIndex]);
        int y = offsetY + (int) (zone.getGridOffsetY() * scaleArray[scaleIndex]);

        for (float row = 0; row < h + gridSize; row += gridSize) {
            
            int theY = Math.min(offsetY + h, Math.max((int)row + y, offsetY));
            int theX = Math.max(x, offsetX);
            
            g.drawLine(theX, theY, theX + w, theY);
        }

        for (float col = 0; col < w + gridSize; col += gridSize) {
            
            int theX = Math.min(offsetX + w, Math.max(x + (int)col, offsetX));
            int theY = Math.max(y, offsetY);

            g.drawLine(theX, theY, theX, theY + h);
        }
    }    
}
