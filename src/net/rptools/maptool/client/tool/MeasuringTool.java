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

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import net.rptools.maptool.client.Tool;
import net.rptools.maptool.client.ZoneOverlay;
import net.rptools.maptool.client.ZoneRenderer;
import net.rptools.maptool.model.ZoneMeasurement;



/**
 */
public class MeasuringTool extends Tool implements MouseListener, MouseMotionListener, ZoneOverlay {
    
    private boolean isDragging;
	private int dragStartX;
	private int dragStartY;
	
	private int currX;
	private int currY;
	
	public MeasuringTool () {
		try {
			setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("net/rptools/maptool/client/image/Tool_Measure.gif"))));
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	
	////
	// Mouse
	
	public void mousePressed(MouseEvent e) {

        isDragging = false;
        
		if (SwingUtilities.isLeftMouseButton(e)) {
			
			dragStartX = e.getX();
			dragStartY = e.getY();
			
			currX = e.getX();
			currY = e.getY();
		}
	}
	
	public void mouseReleased(MouseEvent e) {

		// Mouse wheel can work again
		ZoneRenderer renderer = (ZoneRenderer) e.getSource();
		renderer.setMouseWheelEnabled(true);
		
		isDragging = false;
		
		renderer.repaint();
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
	// MouseMotion
	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}
	public void mouseDragged(MouseEvent e) {
		
		
		ZoneRenderer renderer = (ZoneRenderer) e.getSource();
		if (SwingUtilities.isLeftMouseButton(e)) {

			// Don't allow scale changes
			renderer.setMouseWheelEnabled(false);
			
			isDragging = true;

			currX = e.getX();
			currY = e.getY();
			
			renderer.repaint();
			
		}
	}	
	
	////
	// ZONE OVERLAY
	
	/* (non-Javadoc)
	 * @see net.rptools.maptool.client.ZoneOverlay#paintOverlay(net.rptools.maptool.client.ZoneRenderer, java.awt.Graphics2D)
	 */
	public void paintOverlay(ZoneRenderer renderer, Graphics2D g) {

		if (isDragging) {

			g.setColor(Color.BLACK);
			g.drawLine(dragStartX, dragStartY, currX, currY);
			
			Point startCell = renderer.getCellAt(dragStartX, dragStartY);
			Point endCell = renderer.getCellAt(currX, currY);

			String distString = new ZoneMeasurement(renderer.getZone().getFeetPerCell(), renderer.getZone().isRoundDistance()).formatDistanceBetween(startCell, endCell);

			// Calc Locations
			FontMetrics fm = g.getFontMetrics();
			int centerX = dragStartX - ((dragStartX - currX)/2);
			int centerY = dragStartY - ((dragStartY - currY)/2);
			
			ToolHelper.drawBoxedString(g, distString, centerX, centerY);
		}
	}
    
}
