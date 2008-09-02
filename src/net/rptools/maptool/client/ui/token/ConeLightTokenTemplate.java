/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package net.rptools.maptool.client.ui.token;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.ZonePoint;
import net.rptools.maptool.model.drawing.ConeTemplate;

/**
 * A token template for a cone light source
 * 
 * @author jgorrell
 * @version $Revision$ $Date$ $Author$
 */
public class ConeLightTokenTemplate extends ConeTemplate implements TokenTemplate {

  /*---------------------------------------------------------------------------------------------
   * Instance Variables
   *-------------------------------------------------------------------------------------------*/

  /**
   * Color used to draw the bright light
   */
  private Color brightColor;
  
  /**
   * Color used to draw the border around bright light. It is the same as the base 
   * color but is not transparent
   */
  private Color brightSolidColor;
  
  /**
   * The color used to paint the shadows
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
  public ConeLightTokenTemplate() {
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
    switch (c) {
    case NORTH_EAST:
      v.x = aRenderer.getZone().getGrid().getSize();
      v.y = 0;
      break;
    case SOUTH_WEST:
      v.x = 0;
      v.y = aRenderer.getZone().getGrid().getSize();
      break;
    case SOUTH_EAST:
      v.x = aRenderer.getZone().getGrid().getSize();
      v.y = aRenderer.getZone().getGrid().getSize();
      break;
    case NORTH_WEST:
    default:
      v.x = 0;
      v.y = 0;
      break;
    } // endswitch
    
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
      aG.setColor(brightSolidColor);
      if (isBrightBorder())
        paintBorderAtRadius(aG, aX, aY, xOff, yOff, aGridSize, aDistance, getShadowRadius());
    } else {
      aG.setColor(shadowSolidColor);
      if (isShadowBorder())
        paintBorderAtRadius(aG, aX, aY, xOff, yOff, aGridSize, aDistance, getRadius());
    } // endif
    paintEdges(aG, aX, aY, xOff, yOff, aGridSize, aDistance);
  }
  
  /*---------------------------------------------------------------------------------------------
   * InstanceMethods Methods
   *-------------------------------------------------------------------------------------------*/

  /**
   * Get the shadowRadius for this ConeTokenTemplate.
   *
   * @return Returns the current value of shadowRadius.
   */
  public int getShadowRadius() {
    return shadowRadius;
  }

  /**
   * Set the value of shadowRadius for this ConeTokenTemplate.
   *
   * @param aShadowRadius The shadowRadius to set.
   */
  public void setShadowRadius(int aShadowRadius) {
    shadowRadius = aShadowRadius;
  }

  /**
   * Get the color for this ConeTokenTemplate.
   *
   * @return Returns the current value of color.
   */
  public Color getBrightColor() {
    return brightColor;
  }

  /**
   * Set the value of color for this ConeTokenTemplate.
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
   * Get the shadowColor for this ConeTokenTemplate.
   *
   * @return Returns the current value of shadowColor.
   */
  public Color getShadowColor() {
    return shadowColor;
  }

  /**
   * Set the value of shadowColor for this ConeTokenTemplate.
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
   * Get the shadowSolidColor for this ConeLightTokenTemplate.
   *
   * @return Returns the current value of shadowSolidColor.
   */
  public Color getShadowSolidColor() {
    return shadowSolidColor;
  }

  /**
   * Get the solidColor for this ConeLightTokenTemplate.
   *
   * @return Returns the current value of solidColor.
   */
  public Color getBrightSolidColor() {
    return brightSolidColor;
  }
}
