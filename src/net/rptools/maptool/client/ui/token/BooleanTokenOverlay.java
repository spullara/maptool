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

import java.awt.Graphics2D;
import java.awt.Rectangle;

import net.rptools.maptool.client.functions.AbstractTokenAccessorFunction;
import net.rptools.maptool.model.Token;

/**
 * An overlay that may be applied to a token to show state.
 * 
 * @author jgorrell
 * @version $Revision$ $Date$ $Author$
 */
public abstract class BooleanTokenOverlay extends AbstractTokenOverlay {

  /*---------------------------------------------------------------------------------------------
   * Constructors
   *-------------------------------------------------------------------------------------------*/

  /**
   * Create an overlay with the passed name.
   * 
   * @param aName Name of the new overlay.
   */
  protected BooleanTokenOverlay(String aName) {
      super(aName);
  }

  /*---------------------------------------------------------------------------------------------
   * AbstractTokenOverlay Methods
   *-------------------------------------------------------------------------------------------*/

  /**
   * @see net.rptools.maptool.client.ui.token.AbstractTokenOverlay#paintOverlay(java.awt.Graphics2D, net.rptools.maptool.model.Token, java.awt.Rectangle, java.lang.Object)
   */
  @Override
  public void paintOverlay(Graphics2D g, Token token, Rectangle bounds, Object value) {
      if (AbstractTokenAccessorFunction.getBooleanValue(value))
        paintOverlay(g, token, bounds);
  }
  
  /*---------------------------------------------------------------------------------------------
   * Abstract Methods
   *-------------------------------------------------------------------------------------------*/

  /**
   * Paint the overlay for the passed token.
   * 
   * @param g Graphics used to paint. It is already translated so that 0,0 is
   * the upper left corner of the token. It is also clipped so that the overlay can not
   * draw out of the token's bounding box.
   * @param token The token being painted.
   * @param bounds The bounds of the actual token. This will be different than the clip
   * since the clip also has to take into account the edge of the window. If you draw 
   * based on the clip it will be off for partial token painting.
   */
  public abstract void paintOverlay(Graphics2D g, Token token, Rectangle bounds);
}
