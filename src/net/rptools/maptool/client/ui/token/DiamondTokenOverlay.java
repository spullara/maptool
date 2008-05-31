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
 * Place a diamond over a token.
 * 
 * @author pwright
 * @version $Revision$ $Date$ $Author$
 */
public class DiamondTokenOverlay extends XTokenOverlay {

  /**
   * Default constructor needed for XML encoding/decoding
   */
  public DiamondTokenOverlay() {
    this(TokenStates.DEFAULT_STATE_NAME, Color.RED, 5);
  }
  
  /**
   * Create a Diamond token overlay with the given name.
   * 
   * @param aName Name of this token overlay.
   * @param aColor The color of this token overlay.
   * @param aWidth The width of the lines in this token overlay.
   */
  public DiamondTokenOverlay(String aName, Color aColor, int aWidth) {
    super(aName, aColor, aWidth);
  }
  
  /**
   * @see net.rptools.maptool.client.ui.token.XTokenOverlay#paintOverlay(java.awt.Graphics2D, net.rptools.maptool.model.Token, java.awt.Rectangle)
   */
  @Override
  public void paintOverlay(Graphics2D g, Token aToken, Rectangle bounds) {
	  Double hc = (double)bounds.width/2;
	  Double vc = (double)bounds.height/2;
    Color tempColor = g.getColor();
    g.setColor(getColor());
    Stroke tempStroke = g.getStroke();
    g.setStroke(getStroke());
    g.draw(new Line2D.Double(0, vc, hc, 0));
    g.draw(new Line2D.Double(hc, 0, bounds.width, vc));
    g.draw(new Line2D.Double( bounds.width, vc, hc, bounds.height));
    g.draw(new Line2D.Double(hc, bounds.height, 0, vc));
    g.setColor(tempColor);
    g.setStroke(tempStroke);
  }
}
