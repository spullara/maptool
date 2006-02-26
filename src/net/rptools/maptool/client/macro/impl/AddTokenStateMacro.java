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

package net.rptools.maptool.client.macro.impl;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.macro.Macro;
import net.rptools.maptool.client.macro.MacroDefinition;
import net.rptools.maptool.client.ui.token.ColorDotTokenOverlay;
import net.rptools.maptool.client.ui.token.CrossTokenOverlay;
import net.rptools.maptool.client.ui.token.OTokenOverlay;
import net.rptools.maptool.client.ui.token.ShadedTokenOverlay;
import net.rptools.maptool.client.ui.token.TokenOverlay;
import net.rptools.maptool.client.ui.token.TokenStates;
import net.rptools.maptool.client.ui.token.XTokenOverlay;
import net.rptools.maptool.model.drawing.AbstractTemplate.Quadrant;

/**
 * Create a new token state.
 * 
 * @author jgorrell
 * @version $Revision$ $Date$ $Author$
 */
@MacroDefinition(
    name = "addtokenstate",
    aliases = { "tsa" },
    description = "Add a new token state that can be set on tokens."
)
public class AddTokenStateMacro implements Macro {

  /**
   * The element that contains the token state name
   */
  public static final int NAME = 0;
  
  /**
   * The element that contains the overlay name
   */
  public static final int OVERLAY = 1;
  
  /**
   * The element that contains the first parameter
   */
  public static final int PARAM_1 = 2;
  
  /**
   * The element that contains the second parameter
   */
  public static final int PARAM_2 = 3;
  
  /**
   * The map of color names to color values
   */
  public static final Map<String, Color> COLOR_MAP = new HashMap<String, Color>();
  
  /**
   * The map of color names to color values
   */
  public static final Map<String, Quadrant> CORNER_MAP = new HashMap<String, Quadrant>();
  
  /**
   * Set up the color and corner maps
   */
  static {
    COLOR_MAP.put("black", Color.BLACK);
    COLOR_MAP.put("blue", Color.BLUE);
    COLOR_MAP.put("cyan", Color.CYAN);
    COLOR_MAP.put("darkgray", Color.DARK_GRAY);
    COLOR_MAP.put("gray", Color.GRAY);
    COLOR_MAP.put("green", Color.GREEN);
    COLOR_MAP.put("lightgray", Color.LIGHT_GRAY);
    COLOR_MAP.put("magenta", Color.MAGENTA);
    COLOR_MAP.put("orange", Color.ORANGE);
    COLOR_MAP.put("pink", Color.PINK);
    COLOR_MAP.put("red", Color.RED);
    COLOR_MAP.put("white", Color.WHITE);
    COLOR_MAP.put("yellow", Color.YELLOW);
    CORNER_MAP.put("nw", Quadrant.NORTH_WEST);
    CORNER_MAP.put("ne", Quadrant.NORTH_EAST);
    CORNER_MAP.put("sw", Quadrant.SOUTH_WEST);
    CORNER_MAP.put("se", Quadrant.SOUTH_EAST);
  }
  
  /**
   * @see net.rptools.maptool.client.macro.Macro#execute(java.lang.String)
   */
  public void execute(String aMacro) {
    
    // Split the command line into an array and get the tokens
    String[] tokens = aMacro.split("\\s");
    if (tokens.length < 2) {
      MapTool.addLocalMessage("A token state name and overlay name are required.");
      throw new IllegalArgumentException("A token state name and overlay name are required.");
    } // endif
    String name = tokens[NAME];
    String overlay = tokens[OVERLAY].toLowerCase();
    String param1 = tokens.length > 2 ? tokens[PARAM_1] : null;
    String param2 = tokens.length > 3 ? tokens[PARAM_2] : null;
    
    // Check for a duplicate name
    if (TokenStates.getOverlay(name) != null) {
      MapTool.addLocalMessage("A token state with the name '" + name + "' already exists.");
      throw new IllegalArgumentException("A token state with the name '" + name + "' already exists.");
    } // endif
    
    // The second token is the overlay name, the rest of the tokens describe its properties
    TokenOverlay tokenOverlay = null;
    if (overlay.equals("dot")) {
      tokenOverlay = createDotOverlay(name, param1, param2);
    } else if (overlay.equals("circle")) {
      tokenOverlay = createCircleOverlay(name, param1, param2);
    } else if (overlay.equals("shade")) {
      tokenOverlay = createShadedOverlay(name, param1);
    } else if (overlay.equals("x")) {
      tokenOverlay = createXOverlay(name, param1, param2);
    } else if (overlay.equals("cross")) {
      tokenOverlay = createCrossOverlay(name, param1, param2);
    } else {
      MapTool.addLocalMessage("There is no overlay type with the name '" + overlay + "'. Valid types are " +
          "dot, circle, shade, X, and cross");
      throw new IllegalArgumentException("There is no overlay type with the name '" + overlay + "'.");
    } // endif
    TokenStates.putOverlay(tokenOverlay);
    MapTool.addLocalMessage("Token state '" + tokenOverlay.getName() + "' was added");
  }

  /**
   * Create a shaded overlay.
   * 
   * @param name Name of the new overlay.
   * @param color The color paramter value
   * @return The new token overlay.
   */
  private TokenOverlay createShadedOverlay(String name, String color) {
    Color shadeColor = findColor(color);
    return new ShadedTokenOverlay(name, shadeColor);
  }
  
  /**
   * Create a circle overlay.
   * 
   * @param name Name of the new overlay.
   * @param color The color paramter value
   * @param width The width parameter value
   * @return The new token overlay.
   */
  private TokenOverlay createCircleOverlay(String name, String color, String width) {
    Color circleColor = findColor(color);
    int lineWidth = findInteger(width, 5);
    return new OTokenOverlay(name, circleColor, lineWidth);
  }
  
  /**
   * Create a circle overlay.
   * 
   * @param name Name of the new overlay.
   * @param color The color paramter value
   * @param width The width parameter value
   * @return The new token overlay.
   */
  private TokenOverlay createXOverlay(String name, String color, String width) {
    Color circleColor = findColor(color);
    int lineWidth = findInteger(width, 5);
    return new XTokenOverlay(name, circleColor, lineWidth);
  }
  
  /**
   * Create a circle overlay.
   * 
   * @param name Name of the new overlay.
   * @param color The color paramter value
   * @param width The width parameter value
   * @return The new token overlay.
   */
  private TokenOverlay createCrossOverlay(String name, String color, String width) {
    Color circleColor = findColor(color);
    int lineWidth = findInteger(width, 5);
    return new CrossTokenOverlay(name, circleColor, lineWidth);
  }
  
  /**
   * Create a dot overlay.
   * 
   * @param name Name of the new overlay.
   * @param color The color paramter value
   * @param corner The corner parameter value
   * @return The new token overlay.
   */
  private TokenOverlay createDotOverlay(String name, String color, String corner) {
    Color dotColor = findColor(color);
    Quadrant dotCorner = findCorner(corner);
    return new ColorDotTokenOverlay(name, dotColor, dotCorner);
  }
  
  /**
   * Find the color for the passed name.
   * 
   * @param name The name or hex value of a color.
   * @return The color decoded from the name or the <code>Color.RED</code>
   * if the passed name was <code>null</code>;
   */
  private Color findColor(String name) {
    if (name == null) return Color.RED;
    try {
      return Color.decode(name);
    } catch (NumberFormatException e) {
      if (!COLOR_MAP.containsKey(name.toLowerCase())) {
        MapTool.addLocalMessage("An invalid color '" + name + "' was passed as a paramter. Valid values are " +
            "hex or integer numbers or the name of a common color (black, blue, cyan, darkgray, gray, green, " +
            "lightgray, magenta, orange, pink, red, white, yellow");
        throw new IllegalArgumentException("An invalid color '" + name + "' was passed as a paramter.");
      } // endif
      return COLOR_MAP.get(name);
    } // endtry
  }
  
  /**
   * Find the color for the passed name.
   * 
   * @param name The decimal value of the integer.
   * @param defaultValue The default value for the number.
   * @return The number parsed from the name or the <code>defaultValue</code>
   * if the passed name was <code>null</code>;
   */
  private int findInteger(String name, int defaultValue) {
    if (name == null) return defaultValue;
    try {
      return Integer.parseInt(name);
    } catch (NumberFormatException e) {
      MapTool.addLocalMessage("An invalid number '" + name + "' was passed as a paramter.");
      throw new IllegalArgumentException("An invalid number '" + name + "' was passed as a paramter.");
    } // endtry
  }
  
  /**
   * Find a corner for the passed name
   * 
   * @param name The name or abbreviation of a quadrant value
   * @return The quadrant representing the passed corner name of the 
   * <code>Quadrant.SOUTH_EAST</code> if the passed name was <code>null</code>.
   */
  private Quadrant findCorner(String name) {
    try {
      if (name == null) return Quadrant.SOUTH_EAST;
      return Quadrant.valueOf(name.toUpperCase());
    } catch (IllegalArgumentException e) {
      if (!CORNER_MAP.containsKey(name.toLowerCase())) {
        MapTool.addLocalMessage("An invalid corner name '" + name + "' was passed as a paramter. Valid values are " +
            "nw, ne, sw, se");
        throw new IllegalArgumentException("An invalid corner '" + name + "' was passed as a paramter.");
      } // endif
      return CORNER_MAP.get(name);
    } // endtry
  }
}
