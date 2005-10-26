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
package net.rptools.maptool.client.ui.zone;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.ClientStyle;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.util.ImageManager;

public class MapZoneRenderer extends ZoneRenderer {

    private BufferedImage backgroundImage;
    
    public MapZoneRenderer (Zone zone) {
        super(zone);
    }
    
    @Override
    public BufferedImage getMiniImage(int size) {
        
        // TODO: back buffer this
        // TODO: Don't use full size images
        BufferedImage bgImage = getBackgroundImage();
        
        Dimension imgSize = new Dimension(bgImage.getWidth(), bgImage.getHeight());
        SwingUtil.constrainTo(imgSize, size);
        
        BufferedImage image = new BufferedImage(imgSize.width, imgSize.height, Transparency.OPAQUE);

        BufferedImage fogImage = new BufferedImage(imgSize.width, imgSize.height, Transparency.BITMASK);

        // Fog
        if (zone.hasFog()) {

            Graphics2D fogG = fogImage.createGraphics();
    
            fogG.setColor(Color.black);
            fogG.fillRect(0, 0, fogImage.getWidth(), fogImage.getHeight());
            
            fogG.setComposite(AlphaComposite.Src);
            fogG.setColor(new Color(0, 0, 0, 0));
    
            Area area = zone.getExposedArea().createTransformedArea(AffineTransform.getScaleInstance(imgSize.width/(float)bgImage.getWidth(), imgSize.height/(float)bgImage.getHeight()));
            fogG.fill(area);
            
            fogG.dispose();
        }
        
        // Compose
        Graphics2D g = image.createGraphics();

        g.drawImage(bgImage, 0, 0, imgSize.width, imgSize.height, this);
        g.drawImage(fogImage, 0, 0, this);
        
        g.dispose();
    	return image;
    }
    
    private BufferedImage getBackgroundImage() {
        
        if (zone == null) { return null; }
        if (backgroundImage != ImageManager.UNKNOWN_IMAGE && backgroundImage != null) { return backgroundImage; }
        //System.out.println ("Loading map");
        
        Asset asset = AssetManager.getAsset(zone.getAssetID());
        if (asset == null) {

        	// Only request the asset once
        	if (backgroundImage == null) {
        		MapTool.serverCommand().getAsset(zone.getAssetID());
        	}
        	
            backgroundImage = ImageManager.UNKNOWN_IMAGE;
        } else {

        	backgroundImage = ImageManager.getImage(asset);
        }
        
        return backgroundImage;
    }
    
    protected void renderBoard(Graphics2D g) {

        BufferedImage mapImage = getBackgroundImage();

        Dimension size = getSize();
        
        // Scale
        float scale = scaleArray[scaleIndex];
        int w = (int)(mapImage.getWidth() * scale);
        int h = (int)(mapImage.getHeight() * scale);

        if (viewOffset.x > size.width - EDGE_LIMIT) {
            viewOffset.x = size.width - EDGE_LIMIT;
        }
        
        if (viewOffset.x + w < EDGE_LIMIT) {
            viewOffset.x = EDGE_LIMIT - w;
        }
        
        if (viewOffset.y > size.height - EDGE_LIMIT) {
            viewOffset.y = size.height - EDGE_LIMIT;
        }
        
        if (viewOffset.y + h < EDGE_LIMIT) {
            viewOffset.y = EDGE_LIMIT - h;
        }
        
        // Map
        g.drawImage(mapImage, viewOffset.x, viewOffset.y, w, h, this);
        //ClientStyle.boardBorder.paintAround(g, viewOffset.x, viewOffset.y, w, h);
    }
    
    protected void renderGrid(Graphics2D g) {
        
        BufferedImage mapImage = getBackgroundImage();
        float scale = scaleArray[scaleIndex];

        int w = (int)(mapImage.getWidth() * scale);
        int h = (int)(mapImage.getHeight() * scale);

        float gridSize = zone.getGridSize() * scale;

        // Render grid
        g.setColor(ClientStyle.gridColor);

        int x = viewOffset.x + (int) (zone.getGridOffsetX() * scaleArray[scaleIndex]);
        int y = viewOffset.y + (int) (zone.getGridOffsetY() * scaleArray[scaleIndex]);

        for (float row = 0; row < h + gridSize; row += gridSize) {
            
            int theY = Math.min(viewOffset.y + h, Math.max((int)row + y, viewOffset.y));
            int theX = Math.max(x, viewOffset.x);
            
            g.drawLine(theX, theY, theX + w, theY);
        }

        for (float col = 0; col < w + gridSize; col += gridSize) {
            
            int theX = Math.min(viewOffset.x + w, Math.max(x + (int)col, viewOffset.x));
            int theY = Math.max(y, viewOffset.y);

            g.drawLine(theX, theY, theX, theY + h);
        }
    }    
}
