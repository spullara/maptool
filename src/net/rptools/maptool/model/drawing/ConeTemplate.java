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

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ScreenPoint;

/**
 * The cone template draws a highlight over all the squares effected from a specific
 * spine. There are 8 different directions from each spine.
 * 
 * @author jgorrell
 * @version $Revision$ $Date$ $Author$
 */
public class ConeTemplate extends RadiusTemplate {
  
  /*---------------------------------------------------------------------------------------------
   * Instance Variables
   *-------------------------------------------------------------------------------------------*/

  /**
   * The dirction to paint. The ne,se,nw,sw paint a quadrant and the n,w,e,w paint
   * along the spine of the selected vertex. Saved as a string as a hack to get around 
   * the hessian library's problem w/ serializing enumerations.
   */
  private String direction = Direction.SOUTH_EAST.name();
  
  /*---------------------------------------------------------------------------------------------
  * Instance Methods
  *-------------------------------------------------------------------------------------------*/
  
  /**
   * Get the direction for this ConeTemplate.
   *
   * @return Returns the current value of direction.
   */
  public Direction getDirection() {
    if (direction == null)
      return null;
    return Direction.valueOf(direction);
  }

  /**
   * Set the value of direction for this ConeTemplate.
   *
   * @param direction The direction to draw the cone from the center vertex.
   */
  public void setDirection(Direction direction) {
    if (direction != null)
      this.direction = direction.name();
    else
      direction = null;
  }

  /*---------------------------------------------------------------------------------------------
   * Overridden AbstractTemplate Methods
   *-------------------------------------------------------------------------------------------*/
  
  /**
   * @see net.rptools.maptool.model.drawing.AbstractTemplate#paintBorder(java.awt.Graphics2D, int, int, int, int, int, int)
   */
  @Override
  protected void paintBorder(Graphics2D g, int x, int y, int xOff, int yOff, int gridSize, int distance) {
    
    // At the border?
    int radius = getRadius();
    if (distance == radius) {
      
      // Paint lines between vertical boundaries if needed
      if (getDistance(x + 1, y) > radius) {
        if (getDirection() == Direction.SOUTH_EAST || (getDirection() == Direction.SOUTH && y >= x)
            || (getDirection() == Direction.EAST && x >= y))
          paintFarVerticalBorder(g, xOff, yOff, gridSize, Quadrant.SOUTH_EAST);
        if (getDirection() == Direction.NORTH_EAST || (getDirection() == Direction.NORTH && y >= x)
            || (getDirection() == Direction.EAST && x >= y))
          paintFarVerticalBorder(g, xOff, yOff, gridSize, Quadrant.NORTH_EAST);
        if (getDirection() == Direction.SOUTH_WEST || (getDirection() == Direction.SOUTH && y >= x)
            || (getDirection() == Direction.WEST && x >= y))
          paintFarVerticalBorder(g, xOff, yOff, gridSize, Quadrant.SOUTH_WEST);
        if (getDirection() == Direction.NORTH_WEST || (getDirection() == Direction.NORTH && y >= x)
            || (getDirection() == Direction.WEST && x >= y))
          paintFarVerticalBorder(g, xOff, yOff, gridSize, Quadrant.NORTH_WEST);
      } // endif
      
      // Paint lines between horizontal boundaries if needed
      if (getDistance(x, y + 1) > radius) {
        if (getDirection() == Direction.SOUTH_EAST || (getDirection() == Direction.SOUTH && y >= x)
            || (getDirection() == Direction.EAST && x >= y))
          paintFarHorizontalBorder(g, xOff, yOff, gridSize, Quadrant.SOUTH_EAST);
        if (getDirection() == Direction.SOUTH_WEST
            || (getDirection() == Direction.SOUTH && y >= x)
            || (getDirection() == Direction.WEST && x >= y))
          paintFarHorizontalBorder(g, xOff, yOff, gridSize, Quadrant.SOUTH_WEST);
        if (getDirection() == Direction.NORTH_EAST
            || (getDirection() == Direction.NORTH && y >= x)
            || (getDirection() == Direction.EAST && x >= y))
          paintFarHorizontalBorder(g, xOff, yOff, gridSize, Quadrant.NORTH_EAST);
        if (getDirection() == Direction.NORTH_WEST
            || (getDirection() == Direction.NORTH && y >= x)
            || (getDirection() == Direction.WEST && x >= y))
          paintFarHorizontalBorder(g, xOff, yOff, gridSize, Quadrant.NORTH_WEST);
      } // endif
    } // endif

    // Handle the edges
    if (getDirection().ordinal() % 2 == 0) {
      if (x == 0) {
        if (getDirection() == Direction.SOUTH_EAST || getDirection() == Direction.SOUTH_WEST)
          paintCloseVerticalBorder(g, xOff, yOff, gridSize, Quadrant.SOUTH_EAST);
        if (getDirection() == Direction.NORTH_EAST || getDirection() == Direction.NORTH_WEST)
          paintCloseVerticalBorder(g, xOff, yOff, gridSize, Quadrant.NORTH_EAST);
      } // endif
      if (y == 0) {
        if (getDirection() == Direction.SOUTH_EAST || getDirection() == Direction.NORTH_EAST)
          paintCloseHorizontalBorder(g, xOff, yOff, gridSize, Quadrant.NORTH_EAST);
        if (getDirection() == Direction.SOUTH_WEST || getDirection() == Direction.NORTH_WEST)
          paintCloseHorizontalBorder(g, xOff, yOff, gridSize, Quadrant.NORTH_WEST);
      } // endif
    } else if (getDirection().ordinal() % 2 == 1 && x == y && distance <= radius) {
      if (getDirection() == Direction.SOUTH) {
        paintFarVerticalBorder(g, xOff, yOff, gridSize, Quadrant.SOUTH_EAST);
        paintFarVerticalBorder(g, xOff, yOff, gridSize, Quadrant.SOUTH_WEST);
        paintCloseHorizontalBorder(g, xOff, yOff, gridSize, Quadrant.SOUTH_EAST);
        paintCloseHorizontalBorder(g, xOff, yOff, gridSize, Quadrant.SOUTH_WEST);
      } // endif
      if (getDirection() == Direction.NORTH) {
        paintFarVerticalBorder(g, xOff, yOff, gridSize, Quadrant.NORTH_EAST);
        paintFarVerticalBorder(g, xOff, yOff, gridSize, Quadrant.NORTH_WEST);
        paintCloseHorizontalBorder(g, xOff, yOff, gridSize, Quadrant.NORTH_EAST);
        paintCloseHorizontalBorder(g, xOff, yOff, gridSize, Quadrant.NORTH_WEST);
      } // endif
      if (getDirection() == Direction.EAST) {
        paintCloseVerticalBorder(g, xOff, yOff, gridSize, Quadrant.SOUTH_EAST);
        paintCloseVerticalBorder(g, xOff, yOff, gridSize, Quadrant.NORTH_EAST);
        paintFarHorizontalBorder(g, xOff, yOff, gridSize, Quadrant.SOUTH_EAST);
        paintFarHorizontalBorder(g, xOff, yOff, gridSize, Quadrant.NORTH_EAST);
      } // endif
      if (getDirection() == Direction.WEST) {
        paintCloseVerticalBorder(g, xOff, yOff, gridSize, Quadrant.SOUTH_WEST);
        paintCloseVerticalBorder(g, xOff, yOff, gridSize, Quadrant.NORTH_WEST);
        paintFarHorizontalBorder(g, xOff, yOff, gridSize, Quadrant.SOUTH_WEST);
        paintFarHorizontalBorder(g, xOff, yOff, gridSize, Quadrant.NORTH_WEST);
      } // endif
    } // endif
  }
  
  /**
   * @see net.rptools.maptool.model.drawing.AbstractTemplate#paintArea(java.awt.Graphics2D, int, int, int, int, int, int)
   */
  @Override
  protected void paintArea(Graphics2D g, int x, int y, int xOff, int yOff, int gridSize, int distance) {

    // Drawing along the spines only?
    if ((getDirection() == Direction.EAST || getDirection() == Direction.WEST) && y > x) return;
    if ((getDirection() == Direction.NORTH || getDirection() == Direction.SOUTH) && x > y) return;

    // Only squares w/in the radius
    if (distance <= getRadius()) {
            
      // Paint the squares
      if (getDirection() == Direction.SOUTH_EAST || getDirection() == Direction.SOUTH || getDirection() == Direction.EAST)
        paintArea(g, xOff, yOff, gridSize, Quadrant.SOUTH_EAST);
      if (getDirection() == Direction.NORTH_EAST || getDirection() == Direction.NORTH || getDirection() == Direction.EAST) 
        paintArea(g, xOff, yOff, gridSize, Quadrant.NORTH_EAST);
      if (getDirection() == Direction.SOUTH_WEST || getDirection() == Direction.SOUTH|| getDirection() == Direction.WEST) 
        paintArea(g, xOff, yOff, gridSize, Quadrant.SOUTH_WEST);
      if (getDirection() == Direction.NORTH_WEST || getDirection() == Direction.NORTH || getDirection() == Direction.WEST) 
        paintArea(g, xOff, yOff, gridSize, Quadrant.NORTH_WEST);
    } // endif
  }

  /*---------------------------------------------------------------------------------------------
   * Drawable Interface Methods
   *-------------------------------------------------------------------------------------------*/
  
  /**
   * @see net.rptools.maptool.model.drawing.Drawable#getBounds()
   */
  public Rectangle getBounds() {
    int gridSize = (int)(MapTool.getCampaign().getZone(getZoneId()).getGridSize() * getScale());
    int quadrantSize = getRadius() * gridSize + BOUNDS_PADDING;
    
    // Find the x,y loc
    ScreenPoint vertex = getVertex();
    int x = vertex.x;
    if (getDirection() == Direction.NORTH_WEST || getDirection() == Direction.WEST || getDirection() == Direction.SOUTH_WEST
        || getDirection() == Direction.NORTH || getDirection() == Direction.SOUTH)
      x -= quadrantSize;
    int y = vertex.y;
    if (getDirection() == Direction.NORTH_WEST || getDirection() == Direction.NORTH || getDirection() == Direction.NORTH_EAST
        || getDirection() == Direction.EAST || getDirection() == Direction.WEST)
      y -= quadrantSize;
    
    // Find the width,height
    int width = quadrantSize + BOUNDS_PADDING;
    if (getDirection() == Direction.NORTH || getDirection() == Direction.SOUTH)
      width += quadrantSize;
    int height = quadrantSize + BOUNDS_PADDING;
    if (getDirection() == Direction.EAST || getDirection() == Direction.WEST)
      height += quadrantSize;
    return new Rectangle(x, y, width, height);
  }
}
