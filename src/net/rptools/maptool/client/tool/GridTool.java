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
package net.rptools.maptool.client.tool;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import net.rptools.maptool.client.MapToolClient;
import net.rptools.maptool.client.Tool;
import net.rptools.maptool.client.ZoneRenderer;
import net.rptools.maptool.client.swing.SwingUtil;
import net.rptools.maptool.model.Zone;


/**
 */
public class GridTool extends Tool implements MouseListener, MouseMotionListener, MouseWheelListener {
    private static final long serialVersionUID = 3760846783148208951L;

    private int dragStartX;
	private int dragStartY;

	public GridTool () {
        try {
            setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("net/rptools/maptool/client/image/grid.jpg"))));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /* (non-Javadoc)
	 * @see maptool.client.Tool#attachTo(maptool.client.ZoneRenderer)
	 */
	protected void attachTo(ZoneRenderer renderer) {
		renderer.setGridVisible(true);
	}
	
	/* (non-Javadoc)
	 * @see maptool.client.Tool#detachFrom(maptool.client.ZoneRenderer)
	 */
	protected void detachFrom(ZoneRenderer renderer) {
		renderer.setGridVisible(false);
		
		// Commit the grid size change
		if (MapToolClient.isConnected()) {
			Zone zone = renderer.getZone();
	        MapToolClient.getInstance().getConnection().callMethod(MapToolClient.COMMANDS.setZoneGridSize.name(), zone.getId(), zone.getGridOffsetX(), zone.getGridOffsetY(), zone.getGridSize());
		}
	}

    ////
    // MOUSE LISTENER
	public void mouseClicked(java.awt.event.MouseEvent e) {}
	
	public void mouseEntered(java.awt.event.MouseEvent e){}
	
	public void mouseExited(java.awt.event.MouseEvent e){}
	
	public void mousePressed(java.awt.event.MouseEvent e){
		
		dragStartX = e.getX();
		dragStartY = e.getY();
	}
	
	public void mouseReleased(java.awt.event.MouseEvent e){}
	
    ////
    // MOUSE MOTION LISTENER
    public void mouseDragged(java.awt.event.MouseEvent e){
        
        int dx = e.getX() - dragStartX;
        int dy = e.getY() - dragStartY;

        dragStartX = e.getX();
        dragStartY = e.getY();

        ZoneRenderer renderer = (ZoneRenderer) e.getSource();
        
        if (SwingUtil.isControlDown(e)) {
            renderer.moveViewBy(dx, dy);
        } else {
            renderer.moveGridBy(dx, dy);
        }
    }
    
    public void mouseMoved(java.awt.event.MouseEvent e){}
    
    ////
    // MOUSE WHEEL LISTENER
    /* (non-Javadoc)
     * @see java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.MouseWheelEvent)
     */
    public void mouseWheelMoved(MouseWheelEvent e) {

        ZoneRenderer renderer = (ZoneRenderer) e.getSource();
        
        if (SwingUtil.isControlDown(e)) {
        
            if (e.getWheelRotation() > 0) {
                
                renderer.zoomOut(e.getX(), e.getY());
            } else {
                
                renderer.zoomIn(e.getX(), e.getY());
            }
        } else {

            
            if (e.getWheelRotation() > 0) {
                
                renderer.adjustGridSize(1);
            } else {
                
                renderer.adjustGridSize(-1);
            }
        }
    }
}
