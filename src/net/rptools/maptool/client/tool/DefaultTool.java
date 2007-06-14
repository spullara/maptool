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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Set;

import javax.swing.SwingUtilities;

import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.AppState;
import net.rptools.maptool.client.AppUtil;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ScreenPoint;
import net.rptools.maptool.client.ui.Tool;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.CellPoint;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.ZonePoint;
import net.rptools.maptool.util.TokenUtil;

/**
 */
public abstract class DefaultTool extends Tool implements MouseListener, MouseMotionListener, MouseWheelListener {
    private static final long serialVersionUID = 3258411729238372921L;

    private boolean isDraggingMap;
	private int dragStartX;
	private int dragStartY;
	
	protected int mouseX;
	protected int mouseY;
	
    // This is to manage overflowing of map move events (keep things snappy)
    private long lastMoveRedraw;
    private int mapDX, mapDY;
    private static final int REDRAW_DELAY = 25; // millis

    protected ZoneRenderer renderer;
    
    
    @Override
    protected void attachTo(ZoneRenderer renderer) {
    	this.renderer = renderer;
    }
    
    @Override
    protected void detachFrom(ZoneRenderer renderer) {
    	this.renderer = null;
    }
    
    public boolean isDraggingMap() {
    	return isDraggingMap;
    }
    
    protected void repaintZone() {
    	renderer.repaint();
    }
    
    protected Zone getZone() {
    	return renderer.getZone();
    }
    
    ////
	// Mouse
	public void mousePressed(MouseEvent e) {
 
		// Potential map dragging
		dragStartX = e.getX();
		dragStartY = e.getY();

	}
	
	public void mouseReleased(MouseEvent e) {

        if (isDraggingMap && SwingUtilities.isRightMouseButton(e)) {
            if (AppState.isPlayerViewLinked()) {
            	ZonePoint zp = new ScreenPoint(renderer.getWidth()/2, renderer.getHeight()/2).convertToZone(renderer);
    			MapTool.serverCommand().enforceZoneView(renderer.getZone().getId(), zp.x, zp.y, renderer.getScaleIndex());
            }           
        }

        // Cleanup
    	isDraggingMap = false;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e) {
	}	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent e) {
		
	}
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent e) {
	}
	
	////
	// MouseMotion
	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved(MouseEvent e) {

		if (renderer == null) {
			return;
		}
		
		mouseX = e.getX();
		mouseY = e.getY();

		CellPoint cp = renderer.getZone().getGrid().convert(new ScreenPoint(e.getX(), e.getY()).convertToZone(renderer));
		if (cp != null) {	
			MapTool.getFrame().getCoordinateStatusBar().update(cp.x, cp.y);
		} else {
		    MapTool.getFrame().getCoordinateStatusBar().clear();
        }
	}
	
	public void mouseDragged(MouseEvent e) {

		CellPoint cellUnderMouse = renderer.getCellAt(new ScreenPoint(e.getX(), e.getY()));
		if (cellUnderMouse != null) {
			MapTool.getFrame().getCoordinateStatusBar().update(cellUnderMouse.x, cellUnderMouse.y);
		} else {
		    MapTool.getFrame().getCoordinateStatusBar().clear();
        }
		
		// MAP MOVEMENT
		if (SwingUtilities.isRightMouseButton(e)) {

			isDraggingMap = true;

			mapDX += e.getX() - dragStartX;
			mapDY += e.getY() - dragStartY;
	
			dragStartX = e.getX();
			dragStartY = e.getY();
			
            long now = System.currentTimeMillis();
            if (now - lastMoveRedraw > REDRAW_DELAY) {
                // TODO: does it matter to capture the last map move in the series ?
                // TODO: This should probably be genericized an put into ZoneRenderer
                // to prevent over zealous repainting
                renderer.moveViewBy(mapDX, mapDY);
                mapDX = 0;
                mapDY = 0;
                lastMoveRedraw = now;
            }
		}
	}	

	
	////
	// Mouse Wheel
	public void mouseWheelMoved(MouseWheelEvent e) {

		// QUICK ROTATE
		if (SwingUtil.isShiftDown(e)) {

			Set<GUID> tokenGUIDSet = renderer.getSelectedTokenSet();
			if (tokenGUIDSet.size() == 0) {
				return;
			}

			for (GUID tokenGUID : tokenGUIDSet) {
				Token token = renderer.getZone().getToken(tokenGUID);
				if (token == null) {
					continue;
				}
				
				if (!AppUtil.playerOwns(token)) {
					continue;
				}
				
				Integer facing = token.getFacing();
				if (facing == null) {
					facing = -90; // natural alignment
				}

				if (SwingUtil.isControlDown(e)) {

					facing += e.getWheelRotation() > 0 ? 5 : -5;
				} else {
					int[] facingArray = renderer.getZone().getGrid().getFacingAngles();
					int facingIndex = TokenUtil.getIndexNearestTo(facingArray, facing);
					
					facingIndex += e.getWheelRotation() > 0 ? 1 : -1;
					if (facingIndex < 0) {
						facingIndex = facingArray.length - 1;
					}
					if (facingIndex == facingArray.length) {
						facingIndex = 0;
					}
					
					facing = facingArray[facingIndex];
				}
				
				token.setFacing(facing);
				
				renderer.flush(token);
				MapTool.serverCommand().putToken(renderer.getZone().getId(), token);
			}

			renderer.repaint();
			return;
		}
		
		// ZOOM
		if (e.getWheelRotation() > 0) {
			
			renderer.zoomOut(e.getX(), e.getY());
		} else {
			
			renderer.zoomIn(e.getX(), e.getY());
		}
        if (AppState.isPlayerViewLinked()) {
        	ZonePoint zp = new ScreenPoint(renderer.getWidth()/2, renderer.getHeight()/2).convertToZone(renderer);
			MapTool.serverCommand().enforceZoneView(renderer.getZone().getId(), zp.x, zp.y, renderer.getScaleIndex());
        }
		
	}	



  @Override
  protected void resetTool() {
	  MapTool.getFrame().getToolbox().setSelectedTool(PointerTool.class);
  }
  
}
