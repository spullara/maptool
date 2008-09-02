/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
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
    private transient int lastPointCount = -1;
    private transient Rectangle cachedBounds;

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
            if (previousPoint != null && !previousPoint.equals(point)) {
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

		if (lastPointCount == points.size()) {
			return cachedBounds;
		}

		Rectangle bounds = new Rectangle(points.get(0));
		for (Point point : points) {
			
			bounds.add(point);
		}
		
		// Special casing
		if (bounds.width < 1) {
			bounds.width = 1;
		}
		if (bounds.height < 1) {
			bounds.height = 1;
		}

		cachedBounds = bounds;
		lastPointCount = points.size();
		return bounds;
	}
}
