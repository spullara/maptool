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
import java.awt.event.MouseListener;
import java.util.List;

import net.rptools.maptool.client.tool.ToolHelper;
import net.rptools.maptool.client.ui.ZoneRenderer;
import net.rptools.maptool.model.drawing.LineSegment;
import net.rptools.maptool.model.drawing.Pen;


/**
 * Tool for drawing freehand lines.
 */
public abstract class AbstractLineTool extends AbstractDrawingTool implements MouseListener {

    private int currentX;
    private int currentY;

    private LineSegment line;
    protected boolean drawMeasurementDisabled;
    
    protected int getCurrentX() {
        return currentX;
    }
    
    protected int getCurrentY() {
        return currentY;
    }
    
    protected LineSegment getLine() {
        return this.line;
    }

    protected void startLine(int x, int y) {
        line = new LineSegment();
        addPoint(x, y);
    }

    protected Point addPoint(int x, int y) {
      if (line == null) return null; // Escape has been pressed
        Point ret = null;
    	
        if (x != currentX || y != currentY) {
            ret = new Point(x, y);
            line.getPoints().add(ret);
            currentX = x;
            currentY = y;
        }
        
        zoneRenderer.repaint();
        
        return ret;
    }
    
    protected void removePoint(Point p) {
      if (line == null) return; // Escape has been pressed
        line.getPoints().remove(p);
    }
    
    protected void stopLine(int x, int y) {
      if (line == null) return; // Escape has been pressed
        addPoint(x, y);
        
        for (Point p : line.getPoints()) {
            convertScreenToZone(p);
        }
        
        completeDrawable(zoneRenderer.getZone().getId(), getPen(), line);
        
        line = null;
        currentX = -1;
        currentY = -1;
    }
    
	public void paintOverlay(ZoneRenderer renderer, Graphics2D g) {
		if (line != null) {
            Pen pen = getPen();
            pen.setThickness((float)(pen.getThickness() * renderer.getScale()));
            
            if (pen.isEraser()) {
                pen = new Pen(pen);
                pen.setEraser(false);
                pen.setColor(Color.white.getRGB());
            }

            line.draw(g, pen, 0, 0);
            List<Point> pointList = line.getPoints();
            if (!drawMeasurementDisabled && pointList.size() > 1) {
            	ToolHelper.drawMeasurement(renderer, g, pointList.get(pointList.size()-2), pointList.get(pointList.size()-1), false);
            }
        }
	}
  
  /**
   * @see net.rptools.maptool.client.ui.Tool#resetTool()
   */
  @Override
  protected void resetTool() {
    line = null;
    currentX = -1;
    currentY = -1;
    zoneRenderer.repaint();
  }
}
