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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Line2D;

import net.rptools.maptool.model.Token;

/**
 * Draw an X over a token.
 * 
 * @author jgorrell
 * @version $Revision$ $Date$ $Author$
 */
public class XTokenOverlay extends TokenOverlay {

  /**
   * Color for the X
   */
  private Color color;

  /**
   * Stroke used to draw the line
   */
  private BasicStroke stroke;

  /**
   * Default constructor needed for XML encoding/decoding
   */
  public XTokenOverlay() {
    this(TokenOverlay.DEFAULT_STATE_NAME, Color.RED, 5);
  }

  /**
   * Create a X token overlay with the given name.
   * 
   * @param aName Name of this token overlay.
   * @param aColor The color of this token overlay.
   * @param aWidth The width of the lines in this token overlay.
   */
  public XTokenOverlay(String aName, Color aColor, int aWidth) {
    super(aName);
    if (aColor == null)
      aColor = Color.RED;
    color = aColor;
    if (aWidth <= 0)
      aWidth = 3;
    stroke = new BasicStroke(aWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
  }

  /**
   * @see net.rptools.maptool.client.ui.token.TokenOverlay#paintOverlay(java.awt.Graphics2D, net.rptools.maptool.model.Token, Rectangle)
   */
  @Override
  public void paintOverlay(Graphics2D g, Token aToken, Rectangle bounds) {
    Color tempColor = g.getColor();
    g.setColor(color);
    Stroke tempStroke = g.getStroke();
    g.setStroke(stroke);
    g.draw(new Line2D.Double(0, 0, bounds.width, bounds.height));
    g.draw(new Line2D.Double(0, bounds.height, bounds.width, 0));
    g.setColor(tempColor);
    g.setStroke(tempStroke);
  }

  /**
   * @see net.rptools.maptool.client.ui.token.TokenOverlay#clone()
   */
  @Override
  public Object clone() {
      TokenOverlay overlay = new XTokenOverlay(getName(), getColor(), getWidth());
      overlay.setOrder(getOrder());
      return overlay;
  }
  
  /**
   * Get the color for this XTokenOverlay.
   *
   * @return Returns the current value of color.
   */
  public Color getColor() {
    return color;
  }

  /**
   * Get the stroke for this XTokenOverlay.
   *
   * @return Returns the current value of stroke.
   */
  protected BasicStroke getStroke() {
    return stroke;
  }
  
  /**
   * Set the value of color for this XTokenOverlay.
   *
   * @param aColor The color to set.
   */
  public void setColor(Color aColor) {
    color = aColor;
  }

  /**
   * Get the width for this XTokenOverlay.
   *
   * @return Returns the current value of width.
   */
  public int getWidth() {
    return (int) stroke.getLineWidth();
  }

  /**
   * Set the value of width for this XTokenOverlay.
   *
   * @param aWidth The width to set.
   */
  public void setWidth(int aWidth) {
    if (aWidth <= 0)
      aWidth = 3;
    stroke = new BasicStroke(aWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
  }
}
