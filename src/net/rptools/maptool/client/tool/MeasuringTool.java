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
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import net.rptools.common.util.ImageUtil;
import net.rptools.maptool.client.ui.ZoneRenderer;
import net.rptools.maptool.model.ZoneMeasurement;
import net.rptools.maptool.util.GraphicsUtil;



/**
 */
public class MeasuringTool extends DefaultTool {
    
    private boolean isDragging;
	private int dragStartX;
	private int dragStartY;
	
	private int currX;
	private int currY;
	
	public MeasuringTool () {
		try {
			setIcon(new ImageIcon(ImageUtil.getImage("net/rptools/maptool/client/image/Tool_Measure.gif")));
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
		} else {
			super.mousePressed(e);
		}
	}
	
	public void mouseReleased(MouseEvent e) {

		// Mouse wheel can work again
		ZoneRenderer renderer = (ZoneRenderer) e.getSource();
		
		isDragging = false;
		
		renderer.repaint();
	}
	
	////
	// MouseMotion
	public void mouseDragged(MouseEvent e) {
		
		
		ZoneRenderer renderer = (ZoneRenderer) e.getSource();
		if (SwingUtilities.isLeftMouseButton(e)) {

			isDragging = true;

			currX = e.getX();
			currY = e.getY();
			
			renderer.repaint();
			
		} else {
			super.mouseDragged(e);
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

			String distString = new ZoneMeasurement(renderer.getZone().getFeetPerCell(), true).formatDistanceBetween(startCell, endCell);

			// Calc Locations
			FontMetrics fm = g.getFontMetrics();
			int centerX = dragStartX - ((dragStartX - currX)/2);
			int centerY = dragStartY - ((dragStartY - currY)/2);
			
			GraphicsUtil.drawBoxedString(g, distString, centerX, centerY);
		}
	}
    
}
