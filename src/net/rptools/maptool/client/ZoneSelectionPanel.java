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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import net.rptools.clientserver.hessian.client.ClientConnection;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Token;


/**
 */
public class ZoneSelectionPanel extends JPanel implements DropTargetListener  {

    private static final int MAX_THUMB_WIDTH = 70;
    private static final int PADDING = 5;
    
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
        
        // Background
        g.setColor(new Color(1.0f, 1.0f, 1.0f, 0.5f));
        g.fillRect(0, 0, mySize.width, mySize.height);
        g.setColor(Color.black);
        g.drawRect(1, 1, mySize.width-2, mySize.height-2);
        g.setColor(Color.white);
        g.drawRect(0, 0, mySize.width-2, mySize.height-2);
        
        List<ZoneRenderer> rendererList = MapToolClient.getZoneRenderers();
        
        boundsMap.clear();
        int x = PADDING;
        for (ZoneRenderer renderer : rendererList) {
            
            // TODO: This is a naive solution.  In the future, actually render the zone
            BufferedImage img = renderer.getBackgroundImage();
            if (img == null) {
                continue;
            }
            
            int width = img.getWidth();
            int height = img.getHeight();

            int targetHeight = mySize.height - PADDING - PADDING;
            
            width = (int)(width * (targetHeight / (double)height));
            height = targetHeight;

            // TODO: handle "still too wide" case
            
            g.drawImage(img, x, PADDING, width, height, this);
            g.setColor(Color.black);
            g.drawRect(x, PADDING, width, height);
            
            boundsMap.put(new Rectangle(x, PADDING, width, height), renderer);
            
            x += width + PADDING;
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
        // TODO Auto-generated method stub
        Transferable transferable = dtde.getTransferable();
        if (!transferable.isDataFlavorSupported(TransferableAsset.dataFlavor) &&
        		!transferable.isDataFlavorSupported(TransferableAssetReference.dataFlavor)) {
            dtde.dropComplete(false);
            return;
        }

        dtde.dropComplete(true);
        
        try {
        	Asset asset = null;
        	if (transferable.isDataFlavorSupported(TransferableAsset.dataFlavor)) {
        		
        		// Add it to the system
        		asset = (Asset) transferable.getTransferData(TransferableAsset.dataFlavor);
        		MapToolClient.getCampaign().putAsset(asset);
                if (MapToolClient.isConnected()) {
                	
                	// TODO: abstract this
                    ClientConnection conn = MapToolClient.getInstance().getConnection();
                    
                    conn.callMethod(MapToolClient.COMMANDS.putAsset.name(), asset);
                }
        		
        	} else {
        		
        		asset = MapToolClient.getCampaign().getAsset((GUID) transferable.getTransferData(TransferableAssetReference.dataFlavor));
        	}

            MapToolClient.addZone(asset.getId());
            
            repaint();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (UnsupportedFlavorException ufe) {
            ufe.printStackTrace();
        }

    }

    /* (non-Javadoc)
     * @see java.awt.dnd.DropTargetListener#dropActionChanged(java.awt.dnd.DropTargetDragEvent)
     */
    public void dropActionChanged(DropTargetDragEvent dtde) {
        // TODO Auto-generated method stub

    }
}
