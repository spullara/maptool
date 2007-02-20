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
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.rptools.lib.image.ImageUtil;
import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.AppState;
import net.rptools.maptool.client.AppStyle;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ScreenPoint;
import net.rptools.maptool.client.ZoneActivityListener;
import net.rptools.maptool.model.ModelChangeEvent;
import net.rptools.maptool.model.ModelChangeListener;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.ZonePoint;
import net.rptools.maptool.util.ImageManager;


/**
 */
public class ZoneSelectionPanel extends JPanel implements ZoneActivityListener, ModelChangeListener  {

    private static final int PADDING = 7;
    private static final int SELECTED_SIZE = 75;
    private static final int UNSELECTED_SIZE = 30;
    
    private static boolean horizontal = false;
    private static boolean alignLeft = false;
    
    private BufferedImage backbuffer;
    
    private Map<Rectangle, ZoneRenderer> boundsMap;
    
    public ZoneSelectionPanel() {
     
        boundsMap = new HashMap<Rectangle, ZoneRenderer>();
        setOpaque(false);
        
        // TODO: make this not an aic
        addMouseListener(new MouseAdapter(){
           
            /* (non-Javadoc)
             * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
             */
            public void mouseClicked(MouseEvent e) {
                
                ZoneRenderer renderer = getRendererAt(e.getX(), e.getY()); 
                if (renderer != null) {

                	if (SwingUtilities.isLeftMouseButton(e)) {
                		
                		// Zone selection
                		if (MapTool.getFrame().getCurrentZoneRenderer() != renderer) {
                    		MapTool.getFrame().setCurrentZoneRenderer(renderer);
                    		
                    		if (AppState.isPlayerViewLinked()) {
                            	ZonePoint zp = new ScreenPoint(renderer.getWidth()/2, renderer.getHeight()/2).convertToZone(renderer);
                    			MapTool.serverCommand().enforceZone(renderer.getZone().getId());
                    			MapTool.serverCommand().enforceZoneView(renderer.getZone().getId(), zp.x, zp.y, renderer.getScaleIndex());
                    		}
                		} else {

                			// This doesn't work for unbounded yet
                			if (renderer.getZone().getMapType() == Zone.MapType.INFINITE) {
                				return;
                			}
                			
                			// Minimap interaction
                			Rectangle bounds = getBoundsFor(renderer);
                			
                			int miniX = e.getX() - bounds.x;
                			int miniY = e.getY() - bounds.y;
                			
                			int mapX = (int)(renderer.getZone().getWidth() * (miniX / (double)bounds.width));
                			int mapY = (int)(renderer.getZone().getHeight() * (miniY / (double)bounds.height));
                			
                			renderer.centerOn(new ZonePoint(mapX, mapY));
                		}
                	}
                }
            }
            
        });
    }
    
    /* (non-Javadoc)
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    protected void paintComponent(Graphics g) {
        
        Dimension mySize = getSize();
        boolean keepBackBuffer = true;
    	if (backbuffer == null || mySize.width != backbuffer.getWidth() || mySize.height != backbuffer.getHeight()) {

    		backbuffer = ImageUtil.createCompatibleImage(mySize.width, mySize.height, Transparency.BITMASK);
    		Graphics2D g2d = backbuffer.createGraphics();
    		g2d.setClip(0, 0, mySize.width, mySize.height);
    		
	        List<ZoneRenderer> rendererList = new ArrayList<ZoneRenderer>();
	        rendererList.addAll(MapTool.getFrame().getZoneRenderers());
	        Collections.sort(rendererList);
	        ZoneRenderer currentRenderer = MapTool.getFrame().getCurrentZoneRenderer();
	        
	        boundsMap.clear();
	        int x = PADDING;
	        int y = getSize().height - PADDING;
	        for (ZoneRenderer renderer : rendererList) {
	            
	        	if (!MapTool.getPlayer().isGM() && !renderer.getZone().isVisible()) {
	        		continue;
	        	}
	        	
	        	boolean isSelectedZone = renderer == currentRenderer;
	        	
	            // TODO: This is a naive solution.  In the future, actually render the zone
                int imgSize = isSelectedZone ? SELECTED_SIZE : UNSELECTED_SIZE;
	            BufferedImage img = renderer.getMiniImage(imgSize);
	            if (img == null || img == ImageManager.UNKNOWN_IMAGE) {
	                img = ImageManager.UNKNOWN_IMAGE;
                    keepBackBuffer = false;
                    
                    // Let's wake up when the image arrives
                    ImageManager.addObservers(renderer.getZone().getAssetID(), this);
	            }
	            
                // TODO: This is probably redundant now
	            Dimension size = new Dimension(img.getWidth(), img.getHeight());
	            SwingUtil.constrainTo(size, imgSize);
	
	            if (horizontal) {
	            	y = alignLeft ? PADDING : (mySize.height - size.height) -PADDING;
	            } else {
	            	x = alignLeft ? PADDING : (mySize.width - size.width) -PADDING;
	            }
	            
	            y -= size.height;
	            
	            g2d.drawImage(img, x, y, size.width, size.height, this);
	            if (!renderer.getZone().isVisible()) {
	            	g2d.drawImage(AppStyle.tokenInvisible, x + 3, y + 3, this);
	            }
	            
	            boundsMap.put(new Rectangle(x, y, size.width, size.height), renderer);
	            
	            if (isSelectedZone) {
	            	AppStyle.miniMapBorder.paintAround(g2d, x, y, size.width, size.height);
	            } else {
	            	g2d.setColor(Color.black);
	            	g2d.drawRect(x, y, size.width-1, size.height-1);
//	            	AppStyle.border.paintWithin(g2d, x, y, size.width, size.height);
	            }
	            
	            if (horizontal) {
	            	x += size.width + PADDING;
	            } else {
	            	y -= PADDING;
	            }
	            
	        }
            g2d.dispose();
        }

    	g.drawImage(backbuffer, 0, 0, null);
        if (!keepBackBuffer) {
            backbuffer = null;
        }
    }

    public Rectangle getBoundsFor(ZoneRenderer renderer) {
    	
    	for (Entry<Rectangle, ZoneRenderer> entry : boundsMap.entrySet()) {
    		if (entry.getValue() == renderer) {
    			return entry.getKey();
    		}
    	}
    	return null;
    }
    
    public ZoneRenderer getRendererAt(int x, int y) {
        
        for (Rectangle rect : boundsMap.keySet()) {
            if (rect.contains(x, y)) {
                return boundsMap.get(rect);
            }
        }
        return null;
    }
    
    public Dimension getPreferredSize() {
        return new Dimension(100, 100);
    }

    public void flush() {
    	backbuffer = null;
    }
    
    public void resize() {
    	
    	int zoneCount = MapTool.getFrame().getZoneRenderers().size();
    	int longSize = (zoneCount-1) * (UNSELECTED_SIZE + PADDING) + SELECTED_SIZE + PADDING*2;
    	int shortSize = SELECTED_SIZE + 2*PADDING;
    	
    	setSize((horizontal ? longSize : shortSize), (horizontal ? shortSize : longSize));
    }
    
    ////
    // Zone Listener
    public void zoneAdded(Zone zone) {
    	
    	zone.addModelChangeListener(this);
    	
    	flush();
    	resize();
    	getParent().doLayout();
    	repaint();
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
}
