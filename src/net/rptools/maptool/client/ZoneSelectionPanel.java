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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import net.rptools.clientserver.hessian.client.ClientConnection;
import net.rptools.maptool.client.swing.ImageBorder;
import net.rptools.maptool.client.swing.SwingUtil;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.util.FileUtil;
import net.rptools.maptool.util.ImageUtil;


/**
 */
public class ZoneSelectionPanel extends JPanel implements DropTargetListener  {

    private static final int MAX_THUMB_WIDTH = 70;
    private static final int PADDING = 10;
    
    private Map<Rectangle, ZoneRenderer> boundsMap;
    private BufferedImage backBuffer;
    private int lastZoneCount = -1;
    private ZoneRenderer lastSelectedRenderer;
    
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
                    
                    MapToolClient.setCurrentZoneRenderer(renderer);
                }
            }
            
        });
    }
    
    /* (non-Javadoc)
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    protected void paintComponent(Graphics g) {
        
        Dimension mySize = getSize();
        List<ZoneRenderer> rendererList = MapToolClient.getZoneRenderers();
        ZoneRenderer currentRenderer = MapToolClient.getCurrentZoneRenderer();
        
        if (backBuffer == null || rendererList.size() != lastZoneCount ||
        		backBuffer.getWidth() != mySize.width ||
        		backBuffer.getHeight() != mySize.height ||
        		lastSelectedRenderer != currentRenderer) {
        	
        	lastZoneCount = rendererList.size();
        	lastSelectedRenderer = currentRenderer;

	        if (backBuffer == null) {
	        	backBuffer = getGraphicsConfiguration().createCompatibleImage(mySize.width, mySize.height, Transparency.TRANSLUCENT);
	        }
	        
	        Graphics backG = null;
	        try {
	        	backG = backBuffer.getGraphics();
	        	
		        // Background
	        	ImageUtil.clearImage(backBuffer);
	        	backG.setColor(new Color(1.0f, 1.0f, 1.0f, 0.5f));
	        	backG.fillRect(0, 0, mySize.width, mySize.height);

	        	ClientStyle.border.paintWithin((Graphics2D) backG, 0, 0, getSize().width, getSize().height);
		        
		        boundsMap.clear();
		        int x = PADDING;
		        for (ZoneRenderer renderer : rendererList) {
		            
		            // TODO: This is a naive solution.  In the future, actually render the zone
		            BufferedImage img = renderer.getBackgroundImage();
		            if (img == null) {
		            	// Force a redraw later
		            	lastZoneCount = -1;
		                continue;
		            }
		            
		            int width = img.getWidth();
		            int height = img.getHeight();
		
		            int targetHeight = mySize.height - PADDING - PADDING;
		            
		            width = (int)(width * (targetHeight / (double)height));
		            height = targetHeight;
		
		            // TODO: handle "still too wide" case
		            
		            backG.drawImage(img, x, PADDING, width, height, this);
		            
		            if (renderer == currentRenderer) {
		            	ClientStyle.selectedBorder.paintAround((Graphics2D)backG, x, PADDING, width, height);
		            }
		            
		            boundsMap.put(new Rectangle(x, PADDING, width, height), renderer);
		            
		            x += width + PADDING;
		        } 
		        
	        } finally {
	        	if (backG != null) {
	        		backG.dispose();
	        	}
	        }
        }
        
    	g.drawImage(backBuffer, 0, 0, this);
    }

    public ZoneRenderer getRendererAt(int x, int y) {
        
        for (Rectangle rect : boundsMap.keySet()) {
            if (rect.contains(x, y)) {
                return boundsMap.get(rect);
            }
        }
        return null;
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
        	
        	MapToolClient.addZone(asset.getId());
        }
        
        repaint();
    }

    /* (non-Javadoc)
     * @see java.awt.dnd.DropTargetListener#dropActionChanged(java.awt.dnd.DropTargetDragEvent)
     */
    public void dropActionChanged(DropTargetDragEvent dtde) {
        // TODO Auto-generated method stub

    }
}
