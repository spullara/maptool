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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import net.rptools.maptool.model.Token;

/**
 * Paints a single reduced alpha color over the token.
 * 
 * @author jgorrell
 * @version $Revision$ $Date$ $Author$
 */
public class ShadedTokenOverlay extends BooleanTokenOverlay {

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
   * Default constructor needed for XML encoding/decoding
   */
  public ShadedTokenOverlay() {
    this(BooleanTokenOverlay.DEFAULT_STATE_NAME, Color.RED);
  }

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
    color = aColor;
    setOpacity(25);
  }

  /*---------------------------------------------------------------------------------------------
   * TokenOverlay Abstract Method Implementations
   *-------------------------------------------------------------------------------------------*/

  /**
   * @see net.rptools.maptool.client.ui.token.BooleanTokenOverlay#paintOverlay(java.awt.Graphics2D, net.rptools.maptool.model.Token, Rectangle)
   */
  @Override
  public void paintOverlay(Graphics2D g, Token aToken, Rectangle bounds) {
    Color temp = g.getColor();
    g.setColor(color);
    Composite tempComposite = g.getComposite();
    if (getOpacity() != 100)
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)getOpacity()/100));
    g.fill(bounds);
    g.setColor(temp);
    g.setComposite(tempComposite);
  }

  /**
   * @see net.rptools.maptool.client.ui.token.BooleanTokenOverlay#clone()
   */
  @Override
  public Object clone() {
      BooleanTokenOverlay overlay = new ShadedTokenOverlay(getName(), getColor());
      overlay.setOrder(getOrder());
      overlay.setGroup(getGroup());
      overlay.setMouseover(isMouseover());
      overlay.setOpacity(getOpacity());
      overlay.setShowGM(isShowGM());
      overlay.setShowOwner(isShowOthers());
      overlay.setShowOthers(isShowOthers());
      return overlay;
  }
  
  /**
   * Get the color for this ShadedTokenOverlay.
   *
   * @return Returns the current value of color.
   */
  public Color getColor() {
    return color;
  }

  /**
   * Set the value of color for this ShadedTokenOverlay.
   *
   * @param aColor The color to set.
   */
  public void setColor(Color aColor) {
    color = aColor;
  }
}
