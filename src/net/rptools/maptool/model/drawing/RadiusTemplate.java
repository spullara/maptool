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

/**
 * The radius template draws a highlight over all the squares effected from a specific
 * spine.
 * 
 * @author jgorrell
 * @version $Revision$ $Date$ $Author$
 */
public class RadiusTemplate extends AbstractTemplate {

  /*---------------------------------------------------------------------------------------------
   * Overridden AbstractTemplate Methods
   *-------------------------------------------------------------------------------------------*/
  
  /**
   * @see net.rptools.maptool.model.drawing.AbstractTemplate#paintBorder(java.awt.Graphics2D, int, int, int, int, int, int)
   */
  @Override
  protected void paintBorder(Graphics2D g, int x, int y, int xOff, int yOff, int gridSize, int distance) {
    
    // At the border? It's different for templates.
    int radius = getRadius();
    float distances[][] = getDistances(radius);
    if (distance == radius) {
      
      // Paint lines between vertical boundaries if needed
      if (Math.round(distances[x + 1][y]) > radius) {
        paintFarVerticalBorder(g, xOff, yOff, gridSize, Quadrant.NORTH_EAST);
        paintFarVerticalBorder(g, xOff, yOff, gridSize, Quadrant.SOUTH_EAST);
        paintFarVerticalBorder(g, xOff, yOff, gridSize, Quadrant.NORTH_WEST);
        paintFarVerticalBorder(g, xOff, yOff, gridSize, Quadrant.SOUTH_WEST);
      } // endif
      
      // Paint lines between horizontal boundaries if needed
      if (Math.round(distances[x][y + 1]) > radius) {
        paintFarHorizontalBorder(g, xOff, yOff, gridSize, Quadrant.NORTH_EAST);
        paintFarHorizontalBorder(g, xOff, yOff, gridSize, Quadrant.SOUTH_EAST);
        paintFarHorizontalBorder(g, xOff, yOff, gridSize, Quadrant.NORTH_WEST);
        paintFarHorizontalBorder(g, xOff, yOff, gridSize, Quadrant.SOUTH_WEST);
      } // endif
    } // endif
  }

  /**
   * @see net.rptools.maptool.model.drawing.AbstractTemplate#paintArea(java.awt.Graphics2D, int, int, int, int, int, int)
   */
  @Override
  protected void paintArea(Graphics2D g, int x, int y, int xOff, int yOff, int gridSize, int distance) {

    // Only squares w/in the radius
    if (distance <= getRadius()) {
      
      // Paint the squares
      paintArea(g, xOff, yOff, gridSize, Quadrant.NORTH_EAST);
      paintArea(g, xOff, yOff, gridSize, Quadrant.SOUTH_EAST);
      paintArea(g, xOff, yOff, gridSize, Quadrant.NORTH_WEST);
      paintArea(g, xOff, yOff, gridSize, Quadrant.SOUTH_WEST);
    } // endif
  }
}
