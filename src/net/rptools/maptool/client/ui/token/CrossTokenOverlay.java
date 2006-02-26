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

package net.rptools.maptool.client.ui.token;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Line2D;

import net.rptools.maptool.model.Token;

/**
 * Place a cross over a token.
 * 
 * @author jgorrell
 * @version $Revision$ $Date$ $Author$
 */
public class CrossTokenOverlay extends XTokenOverlay {

  /**
   * Default constructor needed for XML encoding/decoding
   */
  public CrossTokenOverlay() {
    this(TokenStates.DEFAULT_STATE_NAME, Color.RED, 5);
  }
  
  /**
   * Create a Cross token overlay with the given name.
   * 
   * @param aName Name of this token overlay.
   * @param aColor The color of this token overlay.
   * @param aWidth The width of the lines in this token overlay.
   */
  public CrossTokenOverlay(String aName, Color aColor, int aWidth) {
    super(aName, aColor, aWidth);
  }
  
  /**
   * @see net.rptools.maptool.client.ui.token.XTokenOverlay#paintOverlay(java.awt.Graphics2D, net.rptools.maptool.model.Token, java.awt.Rectangle)
   */
  @Override
  public void paintOverlay(Graphics2D g, Token aToken, Rectangle bounds) {
    Color tempColor = g.getColor();
    g.setColor(getColor());
    Stroke tempStroke = g.getStroke();
    g.setStroke(getStroke());
    g.draw(new Line2D.Double(0, (double)bounds.height / 2, bounds.width, (double)bounds.height / 2));
    g.draw(new Line2D.Double((double)bounds.width / 2, 0, (double)bounds.width / 2, bounds.height));
    g.setColor(tempColor);
    g.setStroke(tempStroke);
  }
}
