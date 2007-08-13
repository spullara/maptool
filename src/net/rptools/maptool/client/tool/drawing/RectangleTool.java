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
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import net.rptools.maptool.client.ScreenPoint;
import net.rptools.maptool.client.tool.ToolHelper;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.ZonePoint;
import net.rptools.maptool.model.drawing.DrawableColorPaint;
import net.rptools.maptool.model.drawing.Pen;
import net.rptools.maptool.model.drawing.Rectangle;


/**
 * @author drice
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RectangleTool extends AbstractDrawingTool implements MouseMotionListener {
    private static final long serialVersionUID = 3258413928311830323L;

    protected Rectangle rectangle;
    
    public RectangleTool() {
        try {
            setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("net/rptools/maptool/client/image/tool/top.png"))));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    @Override
    public String getInstructions() {
    	return "tool.rect.instructions";
    }
    
    @Override
    public String getTooltip() {
        return "Draw a rectangle";
    }

    public void paintOverlay(ZoneRenderer renderer, Graphics2D g) {
        if (rectangle != null) {
        	
        	Pen pen = getPen();
            pen.setForegroundMode(Pen.MODE_SOLID);
        	
            if (pen.isEraser()) {
                pen = new Pen(pen);
                pen.setEraser(false);
                pen.setPaint(new DrawableColorPaint(Color.white));
                pen.setBackgroundPaint(new DrawableColorPaint(Color.white));
            }
        	
            paintTransformed(g, renderer, rectangle, pen);
            
            Point start = rectangle.getStartPoint();
            Point end = rectangle.getEndPoint();
            
            ToolHelper.drawBoxedMeasurement(renderer, g, ScreenPoint.fromZonePoint(renderer, start.x, start.y), ScreenPoint.fromZonePoint(renderer, end.x, end.y));
        }
    }

    public void mousePressed(MouseEvent e) {

    	ZonePoint zp = getPoint(e);
    	
    	if (SwingUtilities.isLeftMouseButton(e)) {
	        if (rectangle == null) {
	            rectangle = new Rectangle(zp.x, zp.y, zp.x, zp.y);
	        } else {
	            rectangle.getEndPoint().x = zp.x;
	            rectangle.getEndPoint().y = zp.y;
	            
	        	if (isSnapToGrid(e)) {
	        		// Width is always one pixel shy, let's fudge it a bit
	        		rectangle.getEndPoint().x ++;
	        		rectangle.getEndPoint().y ++;
	        	}
	            
	            completeDrawable(renderer.getZone().getId(), getPen(), rectangle);
	            rectangle = null;
	        }
        
	        setIsEraser(isEraser(e));
    	}
    	
    	super.mousePressed(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    	
    	if (rectangle == null) {
    		super.mouseDragged(e);
    	}
    }
    
    public void mouseMoved(MouseEvent e) {
    	super.mouseMoved(e);

    	if (rectangle != null) {
        	ZonePoint p = getPoint(e);
	
	        if (rectangle != null) {
	            rectangle.getEndPoint().x = p.x;
	            rectangle.getEndPoint().y = p.y;
	        }
	        
	        renderer.repaint();
    	}
    }

  /**
   * Stop drawing a rectangle and repaint the zone.
   */
  public void resetTool() {
	  if (rectangle != null) {
	    rectangle = null;
	    renderer.repaint();
	  } else {
		  super.resetTool();
	  }
  }
}
