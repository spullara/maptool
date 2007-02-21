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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.rptools.lib.swing.ImageBorder;
import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.AppStyle;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ZoneActivityListener;
import net.rptools.maptool.model.ModelChangeEvent;
import net.rptools.maptool.model.ModelChangeListener;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.ZonePoint;
import net.rptools.maptool.util.ImageManager;


/**
 */
public class ZoneMiniMapPanel extends JPanel implements ZoneActivityListener, ModelChangeListener  {

	private static final int SIZE_WIDTH = 125;
	private static final int SIZE_HEIGHT = 100;
	
    private Rectangle bounds;
    private BufferedImage backBuffer;
    
    public ZoneMiniMapPanel() {
     
        addMouseListener(new MouseHandler());
    }
    
    /* (non-Javadoc)
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    protected void paintComponent(Graphics g) {
        
        Dimension mySize = getSize();
        g.setColor(Color.black);
        g.fillRect(0, 0, mySize.width, mySize.height);

        ZoneRenderer renderer = MapTool.getFrame().getCurrentZoneRenderer();
        if (renderer == null) {
        	return;
        }

        if (backBuffer == null || backBuffer.getWidth() != mySize.width || backBuffer.getHeight() != mySize.height) {

        	backBuffer = new BufferedImage(mySize.width, mySize.height, Transparency.OPAQUE);
        	
        	// TODO: This is a naive solution.  In the future, actually render the zone
	        BufferedImage img = renderer.getMiniImage(SIZE_WIDTH);
	        if (img == null || img == ImageManager.UNKNOWN_IMAGE) {
	            img = ImageManager.UNKNOWN_IMAGE;
	            
	            // Let's wake up when the image arrives
	            ImageManager.addObservers(renderer.getZone().getAssetID(), this);
	        }
	            
	        ImageBorder border = AppStyle.miniMapBorder;
	        
	        Dimension size = new Dimension(img.getWidth(), img.getHeight());
	        SwingUtil.constrainTo(size, mySize.width-border.getLeftMargin()-border.getRightMargin(), mySize.height-border.getTopMargin()-border.getBottomMargin());

	        int x = border.getLeftMargin() + (mySize.width-size.width-border.getLeftMargin()-border.getRightMargin())/2;
	        int y = border.getTopMargin() + (mySize.height-size.height-border.getTopMargin()-border.getBottomMargin())/2;
	        int w = size.width;
	        int h = size.height;

	        Graphics2D g2d = backBuffer.createGraphics();
	        g2d.setClip(0, 0, backBuffer.getWidth(), backBuffer.getHeight());
	        
	        bounds = new Rectangle(x, y, w, h);

	        g2d.drawImage(img, x, y, w, h, this);
	        
	    	border.paintWithin(g2d, 0, 0, mySize.width, mySize.height);
	    	
	    	g2d.dispose();
        }
        
        g.drawImage(backBuffer, 0, 0, this);
    }

    public Dimension getPreferredSize() {
    	ZoneRenderer renderer = MapTool.getFrame().getCurrentZoneRenderer();
        BufferedImage img = renderer.getMiniImage(SIZE_WIDTH);
        if (img == null || img == ImageManager.UNKNOWN_IMAGE) {
            img = ImageManager.UNKNOWN_IMAGE;
            
            // Let's wake up when the image arrives
            ImageManager.addObservers(renderer.getZone().getAssetID(), this);
        }
        
        ImageBorder border = AppStyle.miniMapBorder;
        
        Dimension size = new Dimension(img.getWidth(), img.getHeight());
        SwingUtil.constrainTo(size, SIZE_WIDTH, SIZE_HEIGHT);
        size.width += border.getLeftMargin() + border.getRightMargin();
        size.height += border.getTopMargin() + border.getBottomMargin();
        
        return size;
    }

    public void flush() {
    	backBuffer = null;
    }
    
    public void resize() {
    	
    	setSize(getPreferredSize());
    }
    
    ////
    // Zone Listener
    public void zoneAdded(Zone zone) {
    }

    public void zoneActivated(Zone zone) {
    	flush();
    	resize();
    	getParent().doLayout();
    	repaint();
    }
    
    ////
    // ModelChangeListener
    public void modelChanged(ModelChangeEvent event) {
    	flush();
    	repaint();
    }
    
    ////
    // IMAGE OBSERVER
    @Override
    public boolean imageUpdate(Image img, int infoflags, int x, int y, int w, int h) {
    	if (infoflags == ImageObserver.ALLBITS) {
    		flush();
    		resize();
    		getParent().doLayout();
    		repaint();
    	}
    	return super.imageUpdate(img, infoflags, x, y, w, h);
    }
    
    ////
    // MOUSE HANDLER
    private class MouseHandler extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            
	    	if (SwingUtilities.isLeftMouseButton(e)) {
	    		
    			// This doesn't work for unbounded yet
	    		ZoneRenderer renderer = MapTool.getFrame().getCurrentZoneRenderer();
    			if (renderer == null || renderer.getZone().getMapType() == Zone.MapType.INFINITE) {
    				return;
    			}
    			
    			// Minimap interaction
    			int miniX = e.getX() - bounds.x;
    			int miniY = e.getY() - bounds.y;
    			
    			int mapX = (int)(renderer.getZone().getWidth() * (miniX / (double)bounds.width));
    			int mapY = (int)(renderer.getZone().getHeight() * (miniY / (double)bounds.height));
    			
    			renderer.centerOn(new ZonePoint(mapX, mapY));
    		}
        }    	
    }
}
