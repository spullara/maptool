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
package net.rptools.maptool.client.tool.drawing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import net.rptools.maptool.client.MapToolClient;
import net.rptools.maptool.client.ZoneRenderer;
import net.rptools.maptool.client.tool.ToolHelper;
import net.rptools.maptool.model.drawing.DrawnElement;
import net.rptools.maptool.model.drawing.Pen;
import net.rptools.maptool.model.drawing.Rectangle;
import net.rptools.maptool.server.MapToolServer;


/**
 * @author drice
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RectangleTool extends AbstractDrawingTool implements MouseMotionListener {
    private static final long serialVersionUID = 3258413928311830323L;

    private int currentX;
    private int currentY;

    protected Rectangle rectangle;
    
    public RectangleTool() {
        try {
            setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("net/rptools/maptool/client/image/Tool_Draw_Rect.gif"))));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void paintOverlay(ZoneRenderer renderer, Graphics2D g) {
        if (rectangle != null) {
        	
        	Pen pen = getPen();
        	
            if (pen.isEraser()) {
                pen = new Pen(pen);
                pen.setEraser(false);
                pen.setColor(Color.white.getRGB());
                pen.setBackgroundColor(Color.white.getRGB());
            }
        	
            rectangle.draw(g, pen, 0, 0);
            ToolHelper.drawBoxedMeasurement(renderer, g, rectangle.getStartPoint(), rectangle.getEndPoint(), false);
        }
    }

    public void mouseClicked(MouseEvent e) { }

    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        
        if (rectangle == null) {
            rectangle = new Rectangle(x, y, x, y);
        } else {
            rectangle.getEndPoint().x = x;
            rectangle.getEndPoint().y = y;
            
            convertScreenToZone(rectangle.getStartPoint());
            convertScreenToZone(rectangle.getEndPoint());
            
            completeDrawable(zoneRenderer.getZone().getId(), getPen(), rectangle);
            rectangle = null;
        }
        
        setIsEraser(SwingUtilities.isRightMouseButton(e));
    }

    public void mouseReleased(MouseEvent e) { 
    	
    }

    public void mouseEntered(MouseEvent e) { }

    public void mouseExited(MouseEvent e) { }

    public void mouseDragged(MouseEvent e) { }

    public void mouseMoved(MouseEvent e) {
    	
    	if (rectangle != null) {
	        int x = e.getX();
	        int y = e.getY();
	
	        if (rectangle != null) {
	            rectangle.getEndPoint().x = x;
	            rectangle.getEndPoint().y = y;
	        }
	        
	        zoneRenderer.repaint();
    	}
    }

    /* (non-Javadoc)
	 * @see net.rptools.maptool.client.tool.drawing.AbstractDrawingTool#getPen()
	 */
	protected Pen getPen() {
		Pen pen = super.getPen();
        pen.setBackgroundMode(Pen.MODE_TRANSPARENT);

		return pen;
	}

  /**
   * Stop drawing a rectangle and repaint the zone.
   */
  public void resetTool() {
    rectangle = null;
    zoneRenderer.repaint();
  }
}
