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
package net.rptools.maptool.client.ui.token;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;

import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.drawing.AbstractTemplate.Quadrant;

/**
 * Token overlay that draws a colored dot in one of the corners.
 * 
 * @author giliath
 * @version $Revision$ $Date$ $Author$
 */
public class ColorDotTokenOverlay extends XTokenOverlay {

  /**
   * The corner where the dot is placed
   */
  private Quadrant corner = Quadrant.SOUTH_EAST;

  /**
   * Default constructor needed for XML encoding/decoding
   */
  public ColorDotTokenOverlay() {
    this(TokenStates.DEFAULT_STATE_NAME, Color.RED, Quadrant.SOUTH_EAST);
  }

  /**
   * Create a new dot token overlay
   * 
   * @param aName Name of the token overlay
   * @param aColor Color of the dot
   * @param aCorner Corner containing the dot
   */
  public ColorDotTokenOverlay(String aName, Color aColor, Quadrant aCorner) {
    super(aName, aColor, 0);
    if (aCorner != null)
      corner = aCorner;
  }

  /**
   * @see net.rptools.maptool.client.ui.token.TokenOverlay#paintOverlay(java.awt.Graphics2D, net.rptools.maptool.model.Token, Rectangle)
   */
  @Override
  public void paintOverlay(Graphics2D g, Token aToken, Rectangle bounds) {
    Color tempColor = g.getColor();
    Stroke tempStroke = g.getStroke();
    try {
      g.setColor(getColor());
      g.setStroke(getStroke());

      double size = bounds.width * 0.2;
      double offset = bounds.width * 0.8;
      double x = 0;
      double y = 0;
      switch (corner) {
      case SOUTH_EAST:
        x = y = offset;
        break;
      case SOUTH_WEST:
        y = offset;
        break;
      case NORTH_EAST:
        x = offset;
        break;
      case NORTH_WEST:
        break;
      } // endswitch
      Shape s = new Ellipse2D.Double(x, y, size, size);
      g.fill(s);
    } finally {
      g.setColor(tempColor);
      g.setStroke(tempStroke);
    }
  }
}
