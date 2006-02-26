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

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.macro.Macro;
import net.rptools.maptool.client.macro.MacroDefinition;
import net.rptools.maptool.client.ui.token.LightDialog;
import net.rptools.maptool.client.ui.token.TokenStates;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;

/**
 * Set the token state on a named macro
 * 
 * @author jgorrell
 * @version $Revision$ $Date$ $Author$
 */
@MacroDefinition(
    name = "settokenstate",
    aliases = { "ts" },
    description = "Set a state value on a token."
)
public class SetTokenStateMacro implements Macro {

  /**
   * The element that contains the token name
   */
  public static final int TOKEN = 0;
  
  /**
   * The element that contains the state name
   */
  public static final int STATE = 1;
  
  /**
   * The element that contains the true/false value
   */
  public static final int VALUE = 2;
  
  /**
   * @see net.rptools.maptool.client.macro.Macro#execute(java.lang.String)
   */
  public void execute(String aMacro) {
    
    // Get the 2 strings
    aMacro = aMacro.trim();
    String[] tokens = aMacro.split("\\s");
    if (tokens.length < 2) {
      MapTool.addLocalMessage("A token state name and token name are required.");
      throw new IllegalArgumentException("A token state name and token name are required.");
    } // endif
    
    // Check the state
    String state = getState(tokens[STATE]);
    if (state == null) {
      MapTool.addLocalMessage("The name '" + tokens[STATE] + "' is an unknown token state.");
      throw new IllegalArgumentException("The name '" + tokens[STATE] + "' is an unknown token state.");
    } // endif
    
    // Check the token
    Zone zone = MapTool.getFrame().getCurrentZoneRenderer().getZone();
    Token token = zone.getTokenByName(tokens[TOKEN]);    
    if (!MapTool.getPlayer().isGM() && !zone.isTokenVisible(token)) token = null;
    if (token == null) {
      MapTool.addLocalMessage("The name '" + tokens[TOKEN] + "' is an unknown token name.");
      throw new IllegalArgumentException("The name '" + tokens[TOKEN] + "' is an unknown token name.");
    } // endif
    
    // Set the state
    if (TokenStates.getStates().contains(state)) {
      handleBooleanValue(state, token, tokens);
    } else if (state.equals("light")) {
      LightDialog.show(token, state);
    } // endif
  }
  
  /**
   * Handle setting a boolean value.
   * 
   * @param state The state being set.
   * @param token The token having its state modified.
   * @param tokens The tokens from the command line.
   */
  private void handleBooleanValue(String state, Token token, String[] tokens) {
    Object baseValue = token.getState(state);
    assert baseValue == null || baseValue instanceof Boolean : "The current value of the token state '" + state 
        + "' is not a Boolean value but a " + baseValue.getClass().getName();
    Boolean value = (Boolean)baseValue;
    if (value == null || value.equals(Boolean.FALSE)) 
      value = Boolean.TRUE;
    else 
      value = Boolean.FALSE;
    value = tokens.length < 3 ? value : Boolean.valueOf(tokens[VALUE]);
    token.setState(state, value);
    MapTool.addLocalMessage("Token '" + tokens[TOKEN] + "' is " + (value.booleanValue() ? "now" : "no longer") 
        + " marked '" + state + "'.");
  }
  
  /**
   * Find a state name by ignoring case
   * 
   * @param state Name entered on command line
   * @return The valid state name w/ correct case or <code>null</code> if 
   * no state with the passed name could be found.
   */
  public String getState(String state) {
    if (TokenStates.getOverlay(state) != null ||
        "light".equals(state)) return state;
    state = state.toLowerCase();
    String newState = null;
    for (String name : TokenStates.getStates()) {
      if (name.toLowerCase().equals(state)) {
        if (newState != null) {
          MapTool.addLocalMessage("The name '" + state + "' can be the state " + newState + " or " + name + ".");
          throw new IllegalArgumentException("The name '" + state + "' can be the state " + newState + " or " + name + ".");
        } // endif
        newState = name;
      } // endif
    } // endfor
    return newState;
  }
}
