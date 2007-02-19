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
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collections;
import java.util.List;

import net.rptools.maptool.client.ScreenPoint;
import net.rptools.maptool.client.tool.ToolHelper;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.ZonePoint;
import net.rptools.maptool.model.drawing.Drawable;
import net.rptools.maptool.model.drawing.DrawableColorPaint;
import net.rptools.maptool.model.drawing.LineSegment;
import net.rptools.maptool.model.drawing.Pen;
import net.rptools.maptool.model.drawing.ShapeDrawable;


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

    protected void startLine(MouseEvent e) {
        line = new LineSegment();
        addPoint(e);
    }

    protected Point addPoint(MouseEvent e) {
    	
    	ScreenPoint sp = getPoint(e);
    	
        if (line == null) return null; // Escape has been pressed
        Point ret = new Point(sp.x, sp.y);
    	

        line.getPoints().add(ret);
        currentX = sp.x;
        currentY = sp.y;
    
        renderer.repaint();
        
        return ret;
    }

    protected void removePoint(Point p) {
        if (line == null) return; // Escape has been pressed
        
        // Remove most recently added
        // TODO: optimize this
        Collections.reverse(line.getPoints());
        line.getPoints().remove(p);
        Collections.reverse(line.getPoints());
    }
    
    protected void stopLine(MouseEvent e) {
        if (line == null) return; // Escape has been pressed
        addPoint(e);
        
        for (Point p : line.getPoints()) {

			ZonePoint zp = new ScreenPoint(p.x, p.y).convertToZone(renderer);

            p.x = zp.x;
            p.y = zp.y;
        }
        
        Drawable drawable = line;
        if (isBackgroundFill(e) && line.getPoints().size() > 3) { // TODO: There's a bug where the last point is duplicated, hence 3 points
        	drawable = new ShapeDrawable(getPolygon(line));
        }

        completeDrawable(renderer.getZone().getId(), getPen(), drawable);
        
        line = null;
        currentX = -1;
        currentY = -1;
    }
    
    protected Polygon getPolygon(LineSegment line) {
        Polygon polygon = new Polygon();
        for (Point point : line.getPoints()) {
            polygon.addPoint(point.x, point.y);
        }
        
        return polygon;
    }

    public void paintOverlay(ZoneRenderer renderer, Graphics2D g) {
		if (line != null) {
            Pen pen = getPen();
            pen.setThickness((float)(pen.getThickness() * renderer.getScale()));
            pen.setForegroundMode(Pen.MODE_SOLID);
            
            if (pen.isEraser()) {
                pen = new Pen(pen);
                pen.setEraser(false);
                pen.setPaint(new DrawableColorPaint(Color.white));
            }

            line.draw(g, pen);
            List<Point> pointList = line.getPoints();
            if (!drawMeasurementDisabled && pointList.size() > 1) {
                
                Point start = pointList.get(pointList.size()-2);
                Point end = pointList.get(pointList.size()-1);
                
            	ToolHelper.drawMeasurement(renderer, g, new ScreenPoint(start.x, start.y), new ScreenPoint(end.x, end.y));
            }
        }
	}
  
  /**
   * @see net.rptools.maptool.client.ui.Tool#resetTool()
   */
  @Override
  protected void resetTool() {
	  
	  if (line != null) {
	    line = null;
	    currentX = -1;
	    currentY = -1;
	    renderer.repaint();
	  } else {
		  super.resetTool();
	  }
  }
}
