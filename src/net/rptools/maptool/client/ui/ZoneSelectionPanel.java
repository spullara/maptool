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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import net.rptools.common.swing.SwingUtil;
import net.rptools.common.util.ImageUtil;
import net.rptools.maptool.client.ClientStyle;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.TransferableHelper;
import net.rptools.maptool.client.ZoneListener;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.util.ImageManager;


/**
 */
public class ZoneSelectionPanel extends JPanel implements DropTargetListener, ZoneListener  {

    private static final int PADDING = 5;
    private static final int SELECTED_SIZE = 75;
    private static final int UNSELECTED_SIZE = 30;
    
    private static boolean horizontal = false;
    private static boolean alignLeft = false;
    
    private BufferedImage backbuffer;
    
    private Map<Rectangle, ZoneRenderer> boundsMap;
    
    public ZoneSelectionPanel() {
     
        boundsMap = new HashMap<Rectangle, ZoneRenderer>();
        setOpaque(false);
        
        // DnD
        new DropTarget(this, this);

        addMouseListener(new MouseAdapter(){
           
            /* (non-Javadoc)
             * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
             */
            public void mouseClicked(MouseEvent e) {
                
                ZoneRenderer renderer = getRendererAt(e.getX(), e.getY()); 
                if (renderer != null) {
                    
                    MapTool.getFrame().setCurrentZoneRenderer(renderer);
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

    		backbuffer = new BufferedImage(mySize.width, mySize.height, Transparency.BITMASK);
    		Graphics2D g2d = backbuffer.createGraphics();
    		g2d.setClip(0, 0, mySize.width, mySize.height);
    		
	        List<ZoneRenderer> rendererList = MapTool.getFrame().getZoneRenderers();
	        ZoneRenderer currentRenderer = MapTool.getFrame().getCurrentZoneRenderer();
	        
	        boundsMap.clear();
	        int x = PADDING;
	        int y = PADDING;
	        for (ZoneRenderer renderer : rendererList) {
	            
	        	boolean isSelectedZone = renderer == currentRenderer;
	        	
	            // TODO: This is a naive solution.  In the future, actually render the zone
	            BufferedImage img = renderer.getMiniImage();
	            if (img == null || img == ImageManager.UNKNOWN_IMAGE) {
	                img = ImageManager.UNKNOWN_IMAGE;
                    keepBackBuffer = false;
	            }
	            
	            int imgSize = isSelectedZone ? SELECTED_SIZE : UNSELECTED_SIZE;
	            Dimension size = new Dimension(img.getWidth(), img.getHeight());
	            SwingUtil.constrainTo(size, imgSize);
	
	            if (horizontal) {
	            	y = alignLeft ? PADDING : (mySize.height - size.height) -PADDING;
	            } else {
	            	x = alignLeft ? PADDING : (mySize.width - size.width) -PADDING;
	            }
	            
	            g2d.drawImage(img, x, y, size.width, size.height, this);
	            
	            boundsMap.put(new Rectangle(x, y, size.width, size.height), renderer);
	            
	            if (isSelectedZone) {
	            	ClientStyle.selectedBorder.paintAround(g2d, x, y, size.width, size.height);
	            } else {
	            	ClientStyle.border.paintWithin(g2d, x, y, size.width, size.height);
	            }
	            
	            if (horizontal) {
	            	x += size.width + PADDING;
	            } else {
	            	y += size.height + PADDING;
	            }
	            
	        }
            g2d.dispose();
        }

    	g.drawImage(backbuffer, 0, 0, null);
        if (!keepBackBuffer) {
            backbuffer = null;
        }
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
    	int longSize = (zoneCount-1) * (UNSELECTED_SIZE + PADDING/2) + SELECTED_SIZE + PADDING;
    	int shortSize = SELECTED_SIZE + 2*PADDING;
    	
    	setSize((horizontal ? longSize : shortSize), (horizontal ? shortSize : longSize));
    }
    
    ////
    // DROP TARGET LISTENER
    /* (non-Javadoc)
     * @see java.awt.dnd.DropTargetListener#dragEnter(java.awt.dnd.DropTargetDragEvent)
     */
    public void dragEnter(DropTargetDragEvent dtde) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see java.awt.dnd.DropTargetListener#dragExit(java.awt.dnd.DropTargetEvent)
     */
    public void dragExit(DropTargetEvent dte) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see java.awt.dnd.DropTargetListener#dragOver(java.awt.dnd.DropTargetDragEvent)
     */
    public void dragOver(DropTargetDragEvent dtde) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
     */
    public void drop(DropTargetDropEvent dtde) {

    	Asset asset = TransferableHelper.getAsset(dtde);
        dtde.dropComplete(asset != null);

        if (asset != null) {
        	
        	Zone zone = new Zone(asset.getId());
        	MapTool.addZone(zone);
        }
        
        repaint();
    }

    /* (non-Javadoc)
     * @see java.awt.dnd.DropTargetListener#dropActionChanged(java.awt.dnd.DropTargetDragEvent)
     */
    public void dropActionChanged(DropTargetDragEvent dtde) {
        // TODO Auto-generated method stub

    }
    
    ////
    // Zone Listener
    public void zoneAdded(Zone zone) {
    	flush();
    	resize();
    	getParent().doLayout();
    	repaint();
    	System.out.println ("Zone added");
    }

    public void zoneActivated(Zone zone) {
    	flush();
    	resize();
    	getParent().doLayout();
    	repaint();
    }
}
