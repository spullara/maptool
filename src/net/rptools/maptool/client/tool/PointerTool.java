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

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import net.rptools.clientserver.hessian.client.ClientConnection;
import net.rptools.maptool.client.MapToolClient;
import net.rptools.maptool.client.Tool;
import net.rptools.maptool.client.ZoneRenderer;
import net.rptools.maptool.model.Token;



/**
 */
public class PointerTool extends Tool implements MouseListener, MouseWheelListener, MouseMotionListener {
    private static final long serialVersionUID = 3258411729238372921L;
    
    private boolean isDraggingMap;
	private int dragStartX;
	private int dragStartY;
	
	public PointerTool () {
		try {
			setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("net/rptools/maptool/client/image/Tool_Draw_Select.jpg"))));
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	////
	// Mouse
	
	public void mousePressed(MouseEvent e) {

        isDraggingMap = false;
        
		if (SwingUtilities.isLeftMouseButton(e)) {
			
			ZoneRenderer renderer = (ZoneRenderer) e.getSource();
			renderer.clearSelectedTokens();
			
			// Token
			Token token = renderer.getTokenAt (e.getX(), e.getY());
			if (token != null) {
				renderer.selectToken(token);
				return;
			}

			// Then it's a movement
            renderer.clearSelectedTokens();
			isDraggingMap = true; // possibly dragging the map
			dragStartX = e.getX();
			dragStartY = e.getY();
		}
	}
	
	public void mouseReleased(MouseEvent e) {
		
		if (!SwingUtilities.isRightMouseButton(e)) {
			return;
		}
		
		isDraggingMap = false;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
	
	}	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}
	
	////
	// Mouse Wheel
	public void mouseWheelMoved(MouseWheelEvent e) {

        ZoneRenderer renderer = (ZoneRenderer) e.getSource();

		if (e.getWheelRotation() > 0) {
			
			renderer.zoomOut(e.getX(), e.getY());
		} else {
			
			renderer.zoomIn(e.getX(), e.getY());
		}
		
	}	
	
	////
	// MouseMotion
	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}
	public void mouseDragged(MouseEvent e) {
		
		ZoneRenderer renderer = (ZoneRenderer) e.getSource();
		if (SwingUtilities.isLeftMouseButton(e) && !isDraggingMap) {
			
			// Might be dragging a token
			Set<Token> selectedTokenSet = renderer.getSelectedTokenSet();
			if (selectedTokenSet.size() > 0) {
				
				for (Token token : selectedTokenSet) {

					Point cell = renderer.getCellAt(e.getX(), e.getY());
					if (cell != null) {
						
						if (token.getX() == cell.x && token.getY() == cell.y) {
							continue;
						}
						token.setX(cell.x);
						token.setY(cell.y);

						// TODO: this needs to be better abstracted
						if (MapToolClient.isConnected()) {
							ClientConnection conn = MapToolClient.getInstance().getConnection();
							
							conn.callMethod(MapToolClient.COMMANDS.putToken.name(), renderer.getZone().getId(), token);
						}

						renderer.getZone().putToken(token);
						renderer.repaint();
					}
				}
			}
			
			return;
		}
		
		if (SwingUtilities.isLeftMouseButton(e)) {
			// Map movement
			int dx = e.getX() - dragStartX;
			int dy = e.getY() - dragStartY;
	
			dragStartX = e.getX();
			dragStartY = e.getY();
			
			renderer.moveViewBy(dx, dy);
		}
	}	
}
