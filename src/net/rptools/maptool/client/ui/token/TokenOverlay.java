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
import java.util.Comparator;

import net.rptools.maptool.model.Token;

/**
 * An overlay that may be applied to a token to show state.
 * 
 * @author jgorrell
 * @version $Revision$ $Date$ $Author$
 */
public abstract class TokenOverlay implements Cloneable {

  /*---------------------------------------------------------------------------------------------
   * Instance Variables
   *-------------------------------------------------------------------------------------------*/

  /**
   * The name of this overlay. Normally this is the name of a state.
   */
  private String name;

  /**
   * Order of the states as displayed on the states menu.
   */
  private int order;
  
  /**
   * The group that this token overlay belongs to. It may be <code>null</code>.
   */
  private String group;
  
  /**
   * Flag indicating that this token overlay is only displayed on mouseover
   */
  private boolean mouseover;
  
  /**
   * The opacity of the painting. Must be a value between 0 & 100
   */
  private int opacity = 100;
  
  /*---------------------------------------------------------------------------------------------
   * Class Variables
   *-------------------------------------------------------------------------------------------*/
  
  /**
   * A default state name used in default constructors.
   */
  public static final String DEFAULT_STATE_NAME = "defaultStateName";

  /**
   * This comparator is used to order the states.
   */
  public static final Comparator<TokenOverlay> COMPARATOR = new Comparator<TokenOverlay>() {
      public int compare(TokenOverlay o1, TokenOverlay o2) { return o1.getOrder() - o2.getOrder(); }
  };
  
  /*---------------------------------------------------------------------------------------------
   * Constructors
   *-------------------------------------------------------------------------------------------*/

  /**
   * Create an overlay with the passed name.
   * 
   * @param aName Name of the new overlay.
   */
  protected TokenOverlay(String aName) {
    assert aName != null : "A name is required but null was passed.";
    name = aName;
  }

  /*---------------------------------------------------------------------------------------------
   * Instance Methods
   *-------------------------------------------------------------------------------------------*/

  /**
   * Get the name for this TokenOverlay.
   *
   * @return Returns the current value of name.
   */
  public String getName() {
    return name;
  }

  /**
   * Set the value of name for this TokenOverlay.
   *
   * @param aName The name to set.
   */
  public void setName(String aName) {
    name = aName;
  }

  /** @return Getter for order */
  public int getOrder() {
      return order;
  }

  /** @param order Setter for the order to set */
  public void setOrder(int order) {
      this.order = order;
  }

  /** @return Getter for group */
  public String getGroup() {
      return group;
  }

  /** @param group Setter for group */
  public void setGroup(String group) {
      this.group = group;
  }

  /** @return Getter for mouseover */
  public boolean isMouseover() {
      return mouseover;
  }

  /** @param mouseover Setter for mouseover */
  public void setMouseover(boolean mouseover) {
      this.mouseover = mouseover;
  }

  /** @return Getter for opacity */
  public int getOpacity() {
      if (opacity == 0) opacity = 100;
      return opacity;
  }

  /** @param opacity Setter for opacity */
  public void setOpacity(int opacity) {
      this.opacity = opacity;
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
  
  /**
   * @see java.lang.Object#clone()
   */
  public abstract Object clone();
}
