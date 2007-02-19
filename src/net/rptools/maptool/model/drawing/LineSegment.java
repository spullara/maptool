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
package net.rptools.maptool.model.drawing;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;


/**
 * @author drice
 */
public class LineSegment extends AbstractDrawing {
	
    private List<Point> points = new ArrayList<Point>();

    /** Manipulate the points by calling {@link #getPoints} and then adding {@link Point} objects
     * to the returned {@link List}.
     */
    public List<Point> getPoints() {
        return points;
    }

    @Override
    protected void draw(Graphics2D g) {
        Point previousPoint = null;
        for (Point point : points) {
            if (previousPoint != null) {
                g.drawLine(previousPoint.x, previousPoint.y, point.x, point.y);
            }
            previousPoint = point;
        }
    }

    protected void drawBackground(Graphics2D g) {
        // do nothing
    }
    
    /* (non-Javadoc)
	 * @see net.rptools.maptool.model.drawing.Drawable#getBounds()
	 */
	public Rectangle getBounds() {

		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int maxY = Integer.MIN_VALUE;
		
		for (Point point : points) {
			
			if (point.x < minX) {minX = point.x;}
			if (point.x > maxX) {maxX = point.x;}
			if (point.y < minY) {minY = point.y;}
			if (point.y > maxY) {maxY = point.y;}
		}
		
		// Special casing
		Rectangle bounds = new Rectangle(minX, minY, maxX - minX, maxY - minY);
		if (bounds.width < 1) {
			bounds.width = 1;
		}
		if (bounds.height < 1) {
			bounds.height = 1;
		}
		
		return bounds;
	}
}
