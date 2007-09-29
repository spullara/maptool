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

import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.ZonePoint;
import net.rptools.maptool.model.drawing.RadiusTemplate;

/**
 * This token template draws a radius around the token.
 * 
 * @author jgorrell
 * @version $Revision$ $Date$ $Author$
 */
public class RadiusLightTokenTemplate extends RadiusTemplate implements TokenTemplate {

  /*---------------------------------------------------------------------------------------------
   * Instance Variables
   *-------------------------------------------------------------------------------------------*/

  /**
   * Color used to draw the bright light. It must contain the proper alpha channel.
   */
  private Color brightColor;
  
  /**
   * Color used to draw the border around bright light. It is the same as the base 
   * color but is not transparent
   */
  private Color brightSolidColor;
  
  /**
   * The color used to paint the shadows. It must contain the proper alpha channel.
   */
  private Color shadowColor;
  
  /**
   * Color used to draw the border around shaodws. It is the same as the base 
   * color but is not transparent
   */
  private Color shadowSolidColor;
  
  /**
   * The radius where shadow illumination starts. It must be less than
   * or equal to the total radius.
   */
  private int shadowRadius;

  /**
   * The corner of the token where the light source is placed.
   */
  private String corner;
  
  /**
   * Flag that indicates tht the border should be painted around bright light.
   */
  private boolean brightBorder;
  
  /**
   * Flag that indicates tht the border should be painted around shadows.
   */
  private boolean shadowBorder;
  
  /*---------------------------------------------------------------------------------------------
   * Constructors
   *-------------------------------------------------------------------------------------------*/

  /**
   * Default constructor sets a radius of 20' and gets the current pen.
   */
  public RadiusLightTokenTemplate() {
    setVertex(new ZonePoint(0, 0));
    setRadius(4);
    setBrightColor(new Color(255, 255, 0, 255/6));
  }

  /*---------------------------------------------------------------------------------------------
   * TokenTemplate Interface Methods
   *-------------------------------------------------------------------------------------------*/

  /**
   * @see net.rptools.maptool.client.ui.token.TokenTemplate#paintTemplate(java.awt.Graphics2D, net.rptools.maptool.model.Token, java.awt.Rectangle, net.rptools.maptool.client.ui.zone.ZoneRenderer)
   */
  public void paintTemplate(Graphics2D aG, Token aToken, Rectangle aBounds, ZoneRenderer aRenderer) {
    
    // Offset for the corner
    ZonePoint v = getVertex();
    Quadrant c = Quadrant.valueOf(corner);
    int multiplier = 1;//TokenSize.Size.values()[aToken.getSize()].sizeFactor();
    if (multiplier < 1) multiplier = 1;
    switch (c) {
    case NORTH_EAST:
      v.x = multiplier * aRenderer.getZone().getGrid().getSize();
      v.y = 0;
      break;
    case SOUTH_WEST:
      v.x = 0;
      v.y = multiplier * aRenderer.getZone().getGrid().getSize();
      break;
    case SOUTH_EAST:
      v.x = multiplier * aRenderer.getZone().getGrid().getSize();
      v.y = multiplier * aRenderer.getZone().getGrid().getSize();
      break;
    case NORTH_WEST:
    default:
      v.x = 0;
      v.y = 0;
      break;
    }
    
    // Set scale and zone id
    setZoneId(aRenderer.getZone().getId());
    paint(aG, isBrightBorder() || isShadowBorder(), true);
  }

  /*---------------------------------------------------------------------------------------------
   * Overridden AbstractTemplate Methods
   *-------------------------------------------------------------------------------------------*/

  /**
   * @see net.rptools.maptool.model.drawing.AbstractTemplate#paintArea(java.awt.Graphics2D, int, int, int, int, int, int)
   */
  @Override
  protected void paintArea(Graphics2D aG, int aX, int aY, int xOff, int yOff, int aGridSize, int aDistance) {
    if (aDistance <= shadowRadius)
      aG.setColor(getBrightColor());
    else 
      aG.setColor(getShadowColor());
    super.paintArea(aG, aX, aY, xOff, yOff, aGridSize, aDistance);
  }
  
  /**
   * @see net.rptools.maptool.model.drawing.RadiusTemplate#paintBorder(java.awt.Graphics2D, int, int, int, int, int, int)
   */
  @Override
  protected void paintBorder(Graphics2D aG, int aX, int aY, int xOff, int yOff, int aGridSize, int aDistance) {
    if (aDistance <= shadowRadius) {
      if (!isBrightBorder()) return;
      aG.setColor(brightSolidColor);
      paintBorderAtRadius(aG, aX, aY, xOff, yOff, aGridSize, aDistance, getShadowRadius());
    } else {
      if (!isShadowBorder()) return;
      aG.setColor(shadowSolidColor);
      paintBorderAtRadius(aG, aX, aY, xOff, yOff, aGridSize, aDistance, getRadius());
    } // endif
  }
  
  /*---------------------------------------------------------------------------------------------
   * Instance Methods
   *-------------------------------------------------------------------------------------------*/

  /**
   * Get the shadowRadius for this RadiusTokenTemplate.
   *
   * @return Returns the current value of shadowRadius.
   */
  public int getShadowRadius() {
    return shadowRadius;
  }

  /**
   * Set the value of shadowRadius for this RadiusTokenTemplate.
   *
   * @param aShadowRadius The shadowRadius to set.
   */
  public void setShadowRadius(int aShadowRadius) {
    shadowRadius = aShadowRadius;
  }

  /**
   * Get the color for this RadiusTokenTemplate.
   *
   * @return Returns the current value of color.
   */
  public Color getBrightColor() {
    return brightColor;
  }

  /**
   * Set the value of color for this RadiusTokenTemplate.
   *
   * @param aColor The color to set.
   */
  public void setBrightColor(Color aColor) {
    brightColor = aColor;
    brightSolidColor = new Color(aColor.getRed(), aColor.getGreen(), aColor.getBlue());
    if (shadowColor == null) setShadowColor(new Color(aColor.getRed(), 
        aColor.getGreen(), aColor.getBlue(), aColor.getAlpha() / 2));
  }

  /**
   * Get the shadowColor for this RadiusTokenTemplate.
   *
   * @return Returns the current value of shadowColor.
   */
  public Color getShadowColor() {
    return shadowColor;
  }

  /**
   * Set the value of shadowColor for this RadiusTokenTemplate.
   *
   * @param aColor The shadowColor to set.
   */
  public void setShadowColor(Color aColor) {
    shadowColor = aColor;
    shadowSolidColor = new Color(aColor.getRed(), aColor.getGreen(), aColor.getBlue());
  }

  /**
   * Get the corner for this RadiusLightTokenTemplate.
   *
   * @return Returns the current value of corner.
   */
  public Quadrant getCorner() {
    return Quadrant.valueOf(corner);
  }

  /**
   * Set the value of corner for this RadiusLightTokenTemplate.
   *
   * @param aCorner The corner to set.
   */
  public void setCorner(Quadrant aCorner) {
    corner = aCorner.toString();
  }

  /**
   * Get the brightBorder for this RadiusLightTokenTemplate.
   *
   * @return Returns the current value of brightBorder.
   */
  public boolean isBrightBorder() {
    return brightBorder;
  }

  /**
   * Set the value of brightBorder for this RadiusLightTokenTemplate.
   *
   * @param aBrightBorder The brightBorder to set.
   */
  public void setBrightBorder(boolean aBrightBorder) {
    brightBorder = aBrightBorder;
  }

  /**
   * Get the shadowBorder for this RadiusLightTokenTemplate.
   *
   * @return Returns the current value of shadowBorder.
   */
  public boolean isShadowBorder() {
    return shadowBorder;
  }

  /**
   * Set the value of shadowBorder for this RadiusLightTokenTemplate.
   *
   * @param aShadowBorder The shadowBorder to set.
   */
  public void setShadowBorder(boolean aShadowBorder) {
    shadowBorder = aShadowBorder;
  }

  /**
   * Get the shadowSolidColor for this RadiusLightTokenTemplate.
   *
   * @return Returns the current value of shadowSolidColor.
   */
  public Color getShadowSolidColor() {
    return shadowSolidColor;
  }

  /**
   * Get the solidColor for this RadiusLightTokenTemplate.
   *
   * @return Returns the current value of solidColor.
   */
  public Color getBrightSolidColor() {
    return brightSolidColor;
  }
}
