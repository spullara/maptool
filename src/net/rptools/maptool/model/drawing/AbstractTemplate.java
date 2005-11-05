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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ScreenPoint;
import net.rptools.maptool.model.GUID;

/**
 * Base class for the radius, line, and cone templates.
 * 
 * @author jgorrell
 * @version $Revision$ $Date$ $Author$
 */
public abstract class AbstractTemplate extends AbstractDrawing {

  /*---------------------------------------------------------------------------------------------
   * Instance Variables
   *-------------------------------------------------------------------------------------------*/
  
  /**
   * The current width of this template in squares.
   */
  private int radius;
  
  /**
   * The location of the vertex where painting starts.
   */
  private ScreenPoint vertex = new ScreenPoint(0, 0);
  
  /**
   * The id of the zone where this drawable is painted.
   */
  private GUID zoneId;
  
  /**
   * The scale used for determining sizes as an overlay
   */
  private double scale = 1.0;
  
  /*---------------------------------------------------------------------------------------------
   * Class Variables
   *-------------------------------------------------------------------------------------------*/
  
  /**
   * The distances for all squares in the lower right quadrant.
   */
  private static float[][] distances = { { 1.0F } };
  
  /**
   * Maximum radius value allowed.
   */
  public static final int MAX_RADIUS = 30;
  
  /**
   * Minimum radius value allowed.
   */
  public static final int MIN_RADIUS = 1;
  
  /**
   * Extra padding added to insure the wide lines do not get clipped.
   */
  public static final int BOUNDS_PADDING = 10;
  
  /**
   * The directions that can be drawn. All is for a radius and the other values are for cones.
   */
  public static enum Direction { 
    /** Draw a Radius */
    ALL, 
    
    // Draw a cone in the indicated direction. Order is important!
    /** Draw a cone directly to the west (left) of the selection point. */  
    WEST, 
    /** Draw a cone directly to the north west (upper left quadrant) of the selection point. */  
    NORTH_WEST, 
    /** Draw a cone directly to the north (up) of the selection point. */  
    NORTH, 
    /** Draw a cone directly to the north east (upper right quadrant) of the selection point. */  
    NORTH_EAST, 
    /** Draw a cone directly to the east (right) of the selection point. */  
    EAST, 
    /** Draw a cone directly to the south east (lower right quadrant) of the selection point. */  
    SOUTH_EAST, 
    /** Draw a cone directly to the south (down) of the selection point. */  
    SOUTH, 
    /** Draw a cone directly to the south west (lower left quadrant) of the selection point. */  
    SOUTH_WEST;
      
      /**
       * Find the direction to draw a cone from two points. The first
       * point would be the mouse location and the second would be the
       * vertex of the cone.
       * 
       * @param x1 Mouse X coordinate. 
       * @param y1 Mouse Y coordinate.
       * @param x2 Vertex X coordinate.
       * @param y2 Vertex Y coordinate.
       * @return The direction from the vertex (point 2) to the mouse (point 1).
       */
      public static Direction findDirection(int x1, int y1, int x2, int y2) {
        double dX = x1 - x2;
        double dY = y1 - y2;
        double angle = Math.atan2(dY, dX);
        int value = (int)Math.floor(((angle/Math.PI + 1.0) / 2.0 ) * 16.0);
        if (value >= 15) value = 0;
        return values()[((value + 1) / 2) + 1];
      }
  }
  
  /**
   * The quadrants for drawing.
   */
  public static enum Quadrant { 
    /** Draw in the north east (upper right) quadrant. */  
    NORTH_EAST, 
    /** Draw in the north west (upper left) quadrant. */  
    NORTH_WEST, 
    /** Draw in the south east (lower right) quadrant. */  
    SOUTH_EAST, 
    /** Draw in the south west (lower left) quadrant. */  
    SOUTH_WEST }
  
  /*---------------------------------------------------------------------------------------------
   * Instance Methods
   *-------------------------------------------------------------------------------------------*/
  
  /**
   * Set the radius of the template in squares.
   * 
   * @param squares The number of squares in the radius for this template.
   */
  public void setRadius(int squares) {
    radius = Math.min(MAX_RADIUS, Math.max(MIN_RADIUS, squares));
    if (radius >= distances.length)
      setQuadrant(radius + 1); // Make extra room for boundary conditions
  }

  /**
   * Get the radius for this RadiusTemplate.
   *
   * @return Returns the current value of radius in squares.
   */
  public int getRadius() {
    return radius;
  }

  /**
   * Get the vertex for this RadiusTemplate.
   *
   * @return Returns the current value of vertex.
   */
  public ScreenPoint getVertex() {
    return vertex;
  }

  /**
   * Set the value of vertex for this RadiusTemplate.
   *
   * @param vertex The vertex to set.
   */
  public void setVertex(ScreenPoint vertex) {
    this.vertex = vertex;
  }

  /**
   * Get the zoneId for this RadiusTemplate.
   *
   * @return Returns the current value of zoneId.
   */
  public GUID getZoneId() {
    return zoneId;
  }

  /**
   * Set the value of zoneId for this RadiusTemplate.
   *
   * @param zoneId The zoneId to set.
   */
  public void setZoneId(GUID zoneId) {
    this.zoneId = zoneId;
  }

  /**
   * Get the scale for this RadiusTemplate.
   *
   * @return Returns the current value of scale.
   */
  public double getScale() {
    return scale;
  }

  /**
   * Set the value of scale for this RadiusTemplate.
   *
   * @param scale The scale to set.
   */
  public void setScale(double scale) {
    this.scale = scale;
  }

  /**
   * Paint the border or area of the template
   * 
   * @param g Where to paint
   * @param border Paint the border?
   * @param area Paint the area?
   */
  protected void paint(Graphics2D g, boolean border, boolean area) {
    if (radius == 0) return;

    // Find the proper distance
    int gridSize = (int)(MapTool.getCampaign().getZone(zoneId).getGridSize() * scale);
    for (int y = 0; y < radius; y++) {
      for (int x = 0; x < radius; x++) {
       
        // Get the offset to the corner of the square
        int xOff = x * gridSize;
        int yOff = y * gridSize;
        
        // Template specific painting
        if (border)
          paintBorder(g, x, y, xOff, yOff, gridSize, Math.round(distances[x][y]));
        if (area)
          paintArea(g, x, y, xOff, yOff, gridSize, Math.round(distances[x][y]));
      } // endfor
    } // endfor
  }

  /**
   * Paint the close horizontal line of a cell's border.
   * All directions are relavant to the vertex.
   * 
   * @param g The painter.
   * @param xOff X Offset to cell from vertex in screen coordinates.
   * @param yOff Y Offset to cell from vertex in screen coordinates.
   * @param gridSize Size of a cell in screen coordinates.
   * @param q The quadrant the cell is in relatve to the vertex.
   */
  protected void paintCloseHorizontalBorder(Graphics2D g, int xOff, int yOff, int gridSize, Quadrant q) {
    int x = vertex.x + getXMult(q) * xOff;
    int y = vertex.y + getYMult(q) * yOff;
    g.drawLine(x, y, x + getXMult(q) * gridSize, y);
  }
  
  /**
   * Paint the close vertical line of a cell's border.
   * All directions are relavant to the vertex.
   * 
   * @param g The painter.
   * @param xOff X Offset to cell from vertex in screen coordinates.
   * @param yOff Y Offset to cell from vertex in screen coordinates.
   * @param gridSize Size of a cell in screen coordinates.
   * @param q The quadrant the cell is in relatve to the vertex.
   */
  protected void paintCloseVerticalBorder(Graphics2D g, int xOff, int yOff, int gridSize, Quadrant q) {
    int x = vertex.x + getXMult(q) * xOff;
    int y = vertex.y + getYMult(q) * yOff;
    g.drawLine(x, y, x, y + getYMult(q) * gridSize);
  }
  
  /**
   * Fill the area of a cell.
   * 
   * @param g The painter.
   * @param xOff X Offset to cell from vertex in screen coordinates.
   * @param yOff Y Offset to cell from vertex in screen coordinates.
   * @param gridSize Size of a cell in screen coordinates.
   * @param q The quadrant the cell is in relatve to the vertex.
   */
  protected void paintArea(Graphics2D g, int xOff, int yOff, int gridSize, Quadrant q) {
    int x = vertex.x + getXMult(q) * xOff + ((getXMult(q) - 1) / 2) * gridSize;
    int y = vertex.y + getYMult(q) * yOff + ((getYMult(q) - 1) / 2) * gridSize;
    g.fillRect(x, y, gridSize, gridSize);
  }
  
  /**
   * Paint the far horizontal line of a cell's border.
   * All directions are relavant to the vertex.
   * 
   * @param g The painter.
   * @param xOff X Offset to cell from vertex in screen coordinates.
   * @param yOff Y Offset to cell from vertex in screen coordinates.
   * @param gridSize Size of a cell in screen coordinates.
   * @param q The quadrant the cell is in relatve to the vertex.
   */
  protected void paintFarHorizontalBorder(Graphics2D g, int xOff, int yOff, int gridSize, Quadrant q) {
    int x = vertex.x + getXMult(q) * xOff;
    int y = vertex.y + getYMult(q) * yOff + getYMult(q) * gridSize;
    g.drawLine(x, y, x + getXMult(q) * gridSize, y);
  }
  
  /**
   * Paint the far vertical line of a cell's border.
   * All directions are relavant to the vertex.
   * 
   * @param g The painter.
   * @param xOff X Offset to cell from vertex in screen coordinates.
   * @param yOff Y Offset to cell from vertex in screen coordinates.
   * @param gridSize Size of a cell in screen coordinates.
   * @param q The quadrant the cell is in relatve to the vertex.
   */
  protected void paintFarVerticalBorder(Graphics2D g, int xOff, int yOff, int gridSize, Quadrant q) {
    int x = vertex.x + getXMult(q) * xOff + getXMult(q) * gridSize;
    int y = vertex.y + getYMult(q) * yOff;
    g.drawLine(x, y, x, y + getYMult(q) * gridSize);
  }
  
  /**
   * Get the multiplier in the X direction.
   * 
   * @param q Quadrant being accessed
   * @return -1 for west and +1 for east
   */
  protected int getXMult(Quadrant q) {
    return ((q == Quadrant.NORTH_WEST || q == Quadrant.SOUTH_WEST) ? -1 : +1);
  }

  /**
   * Get the multiplier in the X direction.
   * 
   * @param q Quadrant being accessed
   * @return -1 for north and +1 for south
   */
  protected int getYMult(Quadrant q) {
    return ((q == Quadrant.NORTH_WEST || q == Quadrant.NORTH_EAST) ? -1 : +1);
  }

  /*---------------------------------------------------------------------------------------------
   * Class Methods 
   *-------------------------------------------------------------------------------------------*/

  /**
   * Get the quadrant distances.
   * 
   * @return The distances structure defines distances from a quadrant of a particular
   * vertex.
   */
  public static synchronized float[][] getDistances() {
    return distances;
  }
  
  /**
   * Make sure the distances has enough data for a specific radius in squares.
   *  
   * @param squares The radius that must be supported.
   */
  public static synchronized void setQuadrant(int squares) {

    // If the number of squares gets larger, make a new quadrant
    int oldSquares = distances.length;
    if (squares <= oldSquares) return;
    float[][] newQuadrant = new float[squares][];
    
    // Fill the new quadrant, start with the existing data
    for (int y = 0; y < squares; y++) {
      newQuadrant[y] = new float[squares];
      if (y < oldSquares)
        System.arraycopy(distances[y], 0, newQuadrant[y], 0, oldSquares);
      
      // Fill in the empty positions
      for (int x = (y < oldSquares ? oldSquares : 0); x < squares; x++) {
        float xDistance = (x == 0) ? Float.MAX_VALUE : newQuadrant[y][x - 1] + 1.0F;
        float yDistance = (y == 0) ? Float.MAX_VALUE : newQuadrant[y - 1][x] + 1.0F;
        float dDistance = (x == 0 || y == 0 ) ? Float.MAX_VALUE : newQuadrant[y - 1][x - 1] + 1.5F;
        newQuadrant[y][x] = Math.min(Math.min(xDistance, yDistance) , dDistance);
      } // endfor
    } // endfor
    distances = newQuadrant;
  }
  
  /*---------------------------------------------------------------------------------------------
   * Ovveridden AbstractDrawing Methods
   *-------------------------------------------------------------------------------------------*/
  
  /**
   * @see net.rptools.maptool.model.drawing.AbstractDrawing#draw(java.awt.Graphics2D)
   */
  @Override
  protected void draw(Graphics2D g) {
    paint(g, true, false);
  }

  /**
   * @see net.rptools.maptool.model.drawing.AbstractDrawing#drawBackground(java.awt.Graphics2D)
   */
  @Override
  protected void drawBackground(Graphics2D g) {    
    
    // Semi transparent
    Color c = g.getColor();
    c = new Color(c.getRed(), c.getGreen(), c.getBlue(), 256/6);
    g.setColor(c);    
    paint(g, false, true);
  }

  /*---------------------------------------------------------------------------------------------
  * Drawable Interface Methods
  *-------------------------------------------------------------------------------------------*/
  
  /**
   * @see net.rptools.maptool.model.drawing.Drawable#getBounds()
   */
  public Rectangle getBounds() {
    int gridSize = (int)(MapTool.getCampaign().getZone(zoneId).getGridSize() * scale);
    int quadrantSize = radius * gridSize + BOUNDS_PADDING;
    return new Rectangle(vertex.x - quadrantSize, vertex.y - quadrantSize, quadrantSize * 2, quadrantSize * 2);
  }
  
  /*---------------------------------------------------------------------------------------------
  * Abstract Methods
  *-------------------------------------------------------------------------------------------*/

  /**
   * Paint the border of the template. Note that all coordinates are for the 
   * south east quadrant, just change the signs of the x/y and xOff/yOff offsets
   * to get to the other quadrants. 
   * 
   * @param g Where to paint
   * @param x Distance from vertex along X axis in cell coordinates.
   * @param y Distance from vertex along Y axis in cell coordinates.
   * @param xOff Distance from vertex along X axis in screen coordinates.
   * @param yOff Distance from vertex along Y axis in screen coordinates.
   * @param gridSize The size of one side of the grid in screen coordinates.
   * @param distance The distance in cells from the vertex to the cell which
   * is offset from the vertex by <code>x</code> & <code>y</code>.
   */
  protected abstract void paintBorder(Graphics2D g, int x, int y, int xOff, int yOff, int gridSize, int distance);

  /**
   * Paint the border of the template. Note that all coordinates are for the 
   * south east quadrant, just change the signs of the x/y and xOff/yOff offsets
   * to get to the other quadrants. 
   * 
   * @param g Where to paint
   * @param x Distance from vertex along X axis in cell coordinates.
   * @param y Distance from vertex along Y axis in cell coordinates.
   * @param xOff Distance from vertex along X axis in screen coordinates.
   * @param yOff Distance from vertex along Y axis in screen coordinates.
   * @param gridSize The size of one side of the grid in screen coordinates.
   * @param distance The distance in cells from the vertex to the cell which
   * is offset from the vertex by <code>x</code> & <code>y</code>.
   */
  protected abstract void paintArea(Graphics2D g, int x, int y, int xOff, int yOff, int gridSize, int distance);
}
