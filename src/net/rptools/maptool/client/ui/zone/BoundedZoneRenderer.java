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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.Scale;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.util.ImageManager;

public class BoundedZoneRenderer extends ZoneRenderer {

	private static final int MINI_MAP_SIZE = 100;
	
    private BufferedImage backgroundImage;
    private BufferedImage miniBackgroundImage;
    private Dimension bgImageSize;
    
    private boolean loaded = false;
    
    private Rectangle boardBounds = new Rectangle();
    
    public BoundedZoneRenderer (Zone zone) {
        super(zone);
        
        // Make sure we have requested the asset from the server
        getBackgroundImage();
    }
    
    @Override
    public boolean isLoading() {
    	return !loaded;
    }
    
    @Override
    public BufferedImage getMiniImage(int size) {
        
        // TODO: back buffer this
        // TODO: Don't use full size images
        if (miniBackgroundImage == null) {
        	
        	BufferedImage bgImage = getBackgroundImage();
        	if (bgImage == null || bgImage == ImageManager.UNKNOWN_IMAGE) {
        		return ImageManager.UNKNOWN_IMAGE;
        	}
        	
        	// Get a copy so that we don't have to keep the big boy around
    		// Keep track of a smaller version for when we aren't in focus
        	Dimension dim = new Dimension(backgroundImage.getWidth(), backgroundImage.getHeight());
        	SwingUtil.constrainTo(dim, MINI_MAP_SIZE);
        	
        	miniBackgroundImage = new BufferedImage(dim.width, dim.height, Transparency.OPAQUE);
        	Graphics2D g2d = miniBackgroundImage.createGraphics();
        	g2d.drawImage(bgImage, 0, 0, dim.width, dim.height, null);
        	g2d.dispose();
        	
        }
        
        Dimension imgSize = new Dimension(miniBackgroundImage.getWidth(), miniBackgroundImage.getHeight());
        SwingUtil.constrainTo(imgSize, size);

        BufferedImage miniMap = new BufferedImage(imgSize.width, imgSize.height, Transparency.OPAQUE);

        Graphics2D g = miniMap.createGraphics();
        try {
	
	        g.drawImage(miniBackgroundImage, 0, 0, imgSize.width, imgSize.height, this);
	
	        // Fog
	        if (zone.hasFog() && bgImageSize != null) {
	
	    		BufferedImage fogImage = new BufferedImage(imgSize.width, imgSize.height, Transparency.BITMASK);
	
	            Graphics2D fogG = fogImage.createGraphics();
	    
	            fogG.setColor(Color.black);
	            fogG.fillRect(0, 0, fogImage.getWidth(), fogImage.getHeight());
	            
	            fogG.setComposite(AlphaComposite.Src);
	            fogG.setColor(new Color(0, 0, 0, 0));
	    
	            Area area = zone.getExposedArea().createTransformedArea(AffineTransform.getScaleInstance(imgSize.width/(float)bgImageSize.width, imgSize.height/(float)bgImageSize.height));
	            fogG.fill(area);
	            
	            fogG.dispose();
	
	            g.drawImage(fogImage, 0, 0, this);
	        }
        } finally {
        	g.dispose();
        }
        
    	return miniMap;
    }
    
    @Override
    public void paintComponent(Graphics g) {
    	// Now that we have it, let's get the scale updated
    	BufferedImage backgroundImage = getBackgroundImage();
    	if (backgroundImage != ImageManager.UNKNOWN_IMAGE && !getZoneScale().isInitialized()) {
    		
    		Scale zoneScale = new Scale(backgroundImage.getWidth(), backgroundImage.getHeight());
        	zoneScale.initialize(getSize().width, getSize().height);
    		
        	setZoneScale(zoneScale);
        	updateFog();
    	}

    	super.paintComponent(g);
    }
    
    private BufferedImage getBackgroundImage() {
        
        if (zone == null) { return null; }
        if (backgroundImage != ImageManager.UNKNOWN_IMAGE && backgroundImage != null) { 
        	
        	loaded = true;
        	return backgroundImage; 
        }
        
        Asset asset = AssetManager.getAsset(zone.getAssetID());
        if (asset == null) {

        	// Only request the asset once
        	if (backgroundImage == null) {
        		MapTool.serverCommand().getAsset(zone.getAssetID()); 
        	}
        	
            backgroundImage = ImageManager.UNKNOWN_IMAGE;
        } else {

        	backgroundImage = ImageManager.getImage(asset, this);
        	if (bgImageSize == null && backgroundImage != ImageManager.UNKNOWN_IMAGE) {
        		bgImageSize = new Dimension(backgroundImage.getWidth(), backgroundImage.getHeight());
        		zone.setWidth(bgImageSize.width);
        		zone.setHeight(bgImageSize.height);
        	}
        }
        
        return backgroundImage;
    }
    
    @Override
    public void flush() {

    	ImageManager.flushImage(zone.getAssetID());
    	backgroundImage = null;
    	loaded = false;
    	
    	super.flush();
    }
    
    @Override
    protected void renderBorder(Graphics2D g2d) {
    	
    	Dimension size = getSize();
    	
    	g2d.setColor(Color.black);

    	// TODO: Optimize this
    	if (boardBounds.x > 0) {
    		// Left
        	g2d.fillRect(0, 0, boardBounds.x, size.height);
    	}
    	if (boardBounds.x + boardBounds.width < size.width) {
    		// Right
    		g2d.fillRect(boardBounds.x + boardBounds.width, 0, size.width - (boardBounds.x + boardBounds.width), size.height);
    	}
    	if (boardBounds.y > 0) {
    		// Top
    		g2d.fillRect(0, 0, size.width, boardBounds.y);
    	}
    	if (boardBounds.y + boardBounds.height < size.height) {
    		// Bottom
    		g2d.fillRect(0, boardBounds.y + boardBounds.height, size.width, size.height - (boardBounds.y + boardBounds.height));
    	}
    }
    
    protected void renderBoard(Graphics2D g) {

        BufferedImage mapImage = getBackgroundImage();

        Dimension size = getSize();
        
        // Scale
        float scale = getScale();
        int w = (int)(mapImage.getWidth() * scale);
        int h = (int)(mapImage.getHeight() * scale);
        int x = getViewOffsetX();
        int y = getViewOffsetY();
        
        if (x > size.width - EDGE_LIMIT) {
            x = size.width - EDGE_LIMIT;
        }
        
        if (x + w < EDGE_LIMIT) {
            x = EDGE_LIMIT - w;
        }
        
        if (y > size.height - EDGE_LIMIT) {
            y = size.height - EDGE_LIMIT;
        }
        
        if (y + h < EDGE_LIMIT) {
            y = EDGE_LIMIT - h;
        }
        
        // Map
        g.drawImage(mapImage, x, y, w, h, this);
        boardBounds.setBounds(x, y, w, h);
    }
}
