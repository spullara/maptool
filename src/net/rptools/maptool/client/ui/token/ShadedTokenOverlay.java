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
import java.awt.Shape;

import net.rptools.maptool.model.Token;

/**
 * Paints a single reduced alpha color over the token.
 * 
 * @author jgorrell
 * @version $Revision$ $Date$ $Author$
 */
public class ShadedTokenOverlay extends TokenOverlay {
  
  /*---------------------------------------------------------------------------------------------
   * Instance Variables
   *-------------------------------------------------------------------------------------------*/
  
  /**
   * The color that is painted over the token.
   */
  private Color color; 
  
  /*---------------------------------------------------------------------------------------------
   * Constructors
   *-------------------------------------------------------------------------------------------*/
  
  /**
   * Create the new token overlay
   * 
   * @param aName Name of the new overlay.
   * @param aColor The color that is painted over the token. If the
   * alpha is 100%, it will be reduced to 25%.
   */
  public ShadedTokenOverlay(String aName, Color aColor) {
    super(aName);
    assert aColor != null : "A color is required but null was passed.";
    if (aColor.getAlpha() == 255) 
      aColor = new Color(aColor.getRed(), aColor.getGreen(), aColor.getBlue(), (int)(255 * 0.25));
    color = aColor;
  }
  
  /*---------------------------------------------------------------------------------------------
   * TokenOverlay Abstract Method Implementations
   *-------------------------------------------------------------------------------------------*/
  
  /**
   * @see net.rptools.maptool.client.ui.token.TokenOverlay#paintOverlay(java.awt.Graphics2D, net.rptools.maptool.model.Token)
   */
  @Override
  public void paintOverlay(Graphics2D g, Token aToken) {
    Shape bounds = g.getClip();
    Color temp = g.getColor();
    g.setColor(color);
    g.fill(bounds);
    g.setColor(temp);
  }
}
