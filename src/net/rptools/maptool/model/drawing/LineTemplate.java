/* The MIT License
 * 
 * Copyright (c) 2005 David Rice, Trevor Croft, Jay Gorrell
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
import java.awt.Rectangle;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ScreenPoint;

/**
 * A drawing tool that will draw a line template between 2 vertices. 
 * @author jgorrell
 * @version $Revision$ $Date$ $Author$
 */
public class LineTemplate extends AbstractTemplate {

  /*---------------------------------------------------------------------------------------------
   * Instance Variables
   *-------------------------------------------------------------------------------------------*/

  /**
   * This vertex is used to determine the path.
   */
  private ScreenPoint pathVertex;

  /**
   * The calculated path for this line.
   */
  private ArrayList<ScreenPoint> path;

  /**
   * The pool of points.
   */
  private ArrayList<ScreenPoint> pool;

  /**
   * The line is drawn in this quadrant. A string is used as a hack
   * to get around the hessian library's problem w/ serialization of enums
   */
  private String quadrant = null;

  /*---------------------------------------------------------------------------------------------
   * Overridden AbstractTemplate Methods
   *-------------------------------------------------------------------------------------------*/

  /**
   * @see net.rptools.maptool.model.drawing.AbstractTemplate#paintArea(java.awt.Graphics2D, int, int, int, int, int, int)
   */
  @Override
  protected void paintArea(Graphics2D g, int x, int y, int xOff, int yOff, int gridSize, int distance) {
    paintArea(g, xOff, yOff, gridSize, getQuadrant());
  }

  /**
   * This method is cheating, the distance parameter was replaced with the offset into the path.
   * @see net.rptools.maptool.model.drawing.AbstractTemplate#paintBorder(java.awt.Graphics2D, int, int, int, int, int, int)
   */
  @Override
  protected void paintBorder(Graphics2D g, int x, int y, int xOff, int yOff, int gridSize, int pElement) {

    // Have to scan 3 points behind and ahead, since that is the maximum number of points
    // that can be added to the path from any single intersection.
    boolean[] noPaint = new boolean[4];
    for (int i = pElement - 3; i < pElement + 3; i++) {
      if (i < 0 || i >= path.size() || i == pElement)
        continue;
      ScreenPoint p = path.get(i);

      // Ignore diagonal cells and cells that are not adjacent
      int dx = p.x - x;
      int dy = p.y - y;
      if (Math.abs(dx) == Math.abs(dy) || Math.abs(dx) > 1 || Math.abs(dy) > 1)
        continue;

      // Remove the border between the 2 points
      noPaint[dx != 0 ? (dx < 0 ? 0 : 2) : (dy < 0 ? 3 : 1)] = true;
    } // endif

    // Paint the borders as needed
    if (!noPaint[0])
      paintCloseVerticalBorder(g, xOff, yOff, gridSize, getQuadrant());
    if (!noPaint[1])
      paintFarHorizontalBorder(g, xOff, yOff, gridSize, getQuadrant());
    if (!noPaint[2])
      paintFarVerticalBorder(g, xOff, yOff, gridSize, getQuadrant());
    if (!noPaint[3])
      paintCloseHorizontalBorder(g, xOff, yOff, gridSize, getQuadrant());
  }

  /**
   * @see net.rptools.maptool.model.drawing.AbstractTemplate#paint(java.awt.Graphics2D, boolean, boolean)
   */
  @Override
  protected void paint(Graphics2D g, boolean border, boolean area) {

    // Need to paint? We need a line and to translate the painting
    if (pathVertex == null)
      return;
    if (getRadius() == 0)
      return;
    if (calcPath() == null)
      return;

    // Paint each element in the path
    int gridSize = (int) (MapTool.getCampaign().getZone(getZoneId()).getGridSize() * getScale());
    ListIterator<ScreenPoint> i = path.listIterator();
    while (i.hasNext()) {
      ScreenPoint p = i.next();
      int xOff = p.x * gridSize;
      int yOff = p.y * gridSize;
      int distance = getDistance(p.x, p.y);

      // Paint what is needed.
      if (area) {
        paintArea(g, p.x, p.y, xOff, yOff, gridSize, distance);
      } // endif
      if (border) {
        paintBorder(g, p.x, p.y, xOff, yOff, gridSize, i.previousIndex());
      } // endif
    } // endfor
  }

  /**
   * @see net.rptools.maptool.model.drawing.AbstractTemplate#setVertex(ScreenPoint)
   */
  @Override
  public void setVertex(ScreenPoint vertex) {
    clearPath();
    super.setVertex(vertex);
  }

  /**
   * @see net.rptools.maptool.model.drawing.AbstractTemplate#setRadius(int)
   */
  @Override
  public void setRadius(int squares) {
    clearPath();
    super.setRadius(squares);
  }

  /*---------------------------------------------------------------------------------------------
   * Instance Methods
   *-------------------------------------------------------------------------------------------*/

  /**
   * Calculate the path 
   * 
   * @return The new path or <code>null</code> if there is no path.
   */
  protected List<ScreenPoint> calcPath() {
    if (getRadius() == 0)
      return null;
    if (pathVertex == null)
      return null;
    int radius = getRadius();

    // Is there a slope?
    ScreenPoint vertex = getVertex();
    if (vertex.equals(pathVertex))
      return null;
    double dx = pathVertex.x - vertex.x;
    double dy = pathVertex.y - vertex.y;

    // Start the line at 0,0
    clearPath();
    path = new ArrayList<ScreenPoint>();
    path.add(getPointFromPool(0, 0));
    if (dx != 0 && dy != 0) {

      // Calculate quadrant and the slope
      setQuadrant((dx < 0) ? (dy < 0 ? Quadrant.NORTH_WEST : Quadrant.SOUTH_WEST) : (dy < 0 ? Quadrant.NORTH_EAST
          : Quadrant.SOUTH_EAST));
      double m = Math.abs(dy / dx);

      // Find the path
      ScreenPoint p = path.get(path.size() - 1);
      while (getDistance(p.x, p.y) <= radius) {
        int x = p.x;
        int y = p.y;

        // Which border does the point exit the cell?
        double xValue = new BigDecimal((y + 1) / m, MathContext.DECIMAL32).doubleValue();
        double yValue = new BigDecimal((x + 1) * m, MathContext.DECIMAL32).doubleValue();
        if (xValue == x + 1 && yValue == y + 1) {

          // Special case, right on the diagonal
          path.add(getPointFromPool(x + 1, y));
          path.add(getPointFromPool(x, y + 1));
          path.add(getPointFromPool(x + 1, y + 1));
        } else if (Math.floor(xValue) == x) {
          path.add(getPointFromPool(x, y + 1));
        } else if (Math.floor(yValue) == y) {
          path.add(getPointFromPool(x + 1, y));
        } else {
          System.err.println("I can't do math: m=" + m + " x=" + x + " xValue=" + xValue + " y=" + y + " yValue=" + yValue);
        } // endif
        p = path.get(path.size() - 1);
      } // endwhile

      // Clear the last of the pool
      if (pool != null) {
        pool.clear();
        pool = null;
      } // endif
    } else {

      // Straight line
      int xInc = dx != 0 ? 1 : 0;
      int yInc = dy != 0 ? 1 : 0;
      int x = xInc;
      int y = yInc;
      while (getDistance(x, y) <= radius) {
        path.add(getPointFromPool(x, y));
        x += xInc;
        y += yInc;
      } // endwhile
    } // endif
    return path;
  }

  /**
   * Get a point from the pool or create a new one.
   * 
   * @param x The x coordinate of the new point.
   * @param y The y coordinate of the new point.
   * @return The new point.
   */
  private ScreenPoint getPointFromPool(int x, int y) {
    ScreenPoint p = null;
    if (pool != null) {
      p = pool.remove(pool.size() - 1);
      if (pool.isEmpty())
        pool = null;
    } // endif
    if (p == null) {
      p = new ScreenPoint(0, 0);
    } // endif
    p.x = x;
    p.y = y;
    return p;
  }

  /**
   * Get the pathVertex for this LineTemplate.
   *
   * @return Returns the current value of pathVertex.
   */
  public ScreenPoint getPathVertex() {
    return pathVertex;
  }

  /**
   * Set the value of pathVertex for this LineTemplate.
   *
   * @param pathVertex The pathVertex to set.
   */
  public void setPathVertex(ScreenPoint pathVertex) {
    clearPath();
    this.pathVertex = pathVertex;
  }

  /**
   * Clear the current path. This will cause it to be recalculated during the next draw.
   */
  public void clearPath() {
    pool = path;
    path = null;
  }

  /**
   * Get the quadrant for this LineTemplate.
   *
   * @return Returns the current value of quadrant.
   */
  public Quadrant getQuadrant() {
    if (quadrant != null)
      return Quadrant.valueOf(quadrant);
    return null;
  }

  /**
   * Set the value of quadrant for this LineTemplate.
   *
   * @param quadrant The quadrant to set.
   */
  public void setQuadrant(Quadrant quadrant) {
    if (quadrant != null)
      this.quadrant = quadrant.name();
    else
      this.quadrant = null;
  }
  
  /*---------------------------------------------------------------------------------------------
   * Drawable Interface Methods
   *-------------------------------------------------------------------------------------------*/
  
  /**
   * @see net.rptools.maptool.model.drawing.Drawable#getBounds()
   */
  public Rectangle getBounds() {
    int gridSize = (int)(MapTool.getCampaign().getZone(getZoneId()).getGridSize() * getScale());
    ScreenPoint v = getVertex();
    ScreenPoint pv = getPathVertex();
    int x = Math.min(v.x, pv.x) - BOUNDS_PADDING;
    if (getQuadrant() == Quadrant.NORTH_WEST || getQuadrant() == Quadrant.SOUTH_WEST)
      x -= gridSize * 2;
    int y = Math.min(v.y, pv.y) - BOUNDS_PADDING; 
    if (getQuadrant() == Quadrant.NORTH_WEST || getQuadrant() == Quadrant.NORTH_EAST)
      y -= gridSize * 2;
    int width = Math.abs(v.x - pv.x) + (gridSize + BOUNDS_PADDING) * 2; 
    int height = Math.abs(v.y - pv.y) + (gridSize + BOUNDS_PADDING) * 2;
    return new Rectangle(x, y, width, height);
  }
}
