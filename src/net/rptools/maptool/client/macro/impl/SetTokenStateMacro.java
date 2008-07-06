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

import java.util.HashSet;
import java.util.Set;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.macro.Macro;
import net.rptools.maptool.client.macro.MacroDefinition;
import net.rptools.maptool.client.ui.token.LightDialog;
import net.rptools.maptool.model.GUID;
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
    aliases = { "sts" },
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
	  Set<GUID> selectedTokenSet; // The tokens to set the state of
	  String stateName;           // The name of the state to set
	  String value;				  // The value to set

	  // Get the strings	  
	  if (aMacro.length() == 0) {
	      MapTool.addLocalMessage("A token state name and token name, or a selected token and state name are required.");
	      return;
	  }
	  String[] args = aMacro.trim().split("\\s");

	  // If we don't have 2 or more arguments then try to apply states to the selected tokens
	  if (args.length < 2) {
		  selectedTokenSet = MapTool.getFrame().getCurrentZoneRenderer().getSelectedTokenSet();
		  if (selectedTokenSet.size() == 0) {
		      MapTool.addLocalMessage("A token state name and token name, or a selected token and state name are required.");
		      return;
		  }
		  stateName = args[0];
		  value = null;
	  } else {
		  Zone zone = MapTool.getFrame().getCurrentZoneRenderer().getZone();
		  Token token = zone.getTokenByName(args[0]);
		  // Give the player the benefit of the doubt. If they specified a token that is invisible
		  // then try the name as a state. This will also stop players that are trying to "guess" token names
		  // and trying to change state figuring out that there is a token there because they are
		  // getting a different error message (benefit of the doubt only goes so far ;) )
		  if (!MapTool.getPlayer().isGM() && (!zone.isTokenVisible(token) || token.getLayer() == Zone.Layer.GM)) {
			  token = null;
		  }
		  
		  if (token == null) { // Doesn't match a token? No problem lets grab selected tokens and see if it matches a state.
			  selectedTokenSet = MapTool.getFrame().getCurrentZoneRenderer().getSelectedTokenSet();
			  if (selectedTokenSet.size() == 0) {
			      MapTool.addLocalMessage("A token state name and token name, or a selected token and state name are required.");
			      return;
			  }  else {
				  stateName = args[0];
				  value = args[1];
			  }
		  } else {
			  // First argument refers to a token and not a token state
			  selectedTokenSet = new HashSet<GUID>();
			  selectedTokenSet.add(token.getId());			  
			  stateName = args[1];
			  if (args.length > 2) {
				  value = args[2];
			  } else {
				  value = null;  
			  }
		  }
	  
	  }
 
	  // Ok now that we have figured out the target of our manipulations its time to figure out exactly what we are trying to do to it
	  String state = getState(stateName);
	  if (state == null) {
	      MapTool.addLocalMessage("Uknown token state => '" + stateName + "'.");
	      return;
	  }
	  
	  // Set the state for all the tokens
	  if (MapTool.getCampaign().getTokenStatesMap().containsKey(state)) {
		  for (GUID tokenId : selectedTokenSet) {
			  Token tok = MapTool.getFrame().getCurrentZoneRenderer().getZone().getToken(tokenId);
			  handleBooleanValue(tok, state, value);
		  }
	  } else if (state.equals("light")) {
		  for (GUID tokenId : selectedTokenSet) {
			  Token tok = MapTool.getFrame().getCurrentZoneRenderer().getZone().getToken(tokenId);
			  LightDialog.show(tok, state);
		  }
	  }
		  
  }
	
	  
  /**
   * Handle setting a boolean value.
   * 
   * @param token The token having its state modified
   * @param state The state being set.
   * @param value The value to set, or <code>null</code> to toggle value.
   */
  private void handleBooleanValue(Token token, String state, String value) {
	  
	  Object baseValue = token.getState(state);
	  assert baseValue == null || baseValue instanceof Boolean : "The current value of token sate '" + state
	  	+ "' is not a Boolean value but a " + baseValue.getClass().getName();
	  
	  Boolean oldValue = (Boolean)baseValue;
	  Boolean newValue;
	  
	  if (value == null) {
		  if (oldValue == null || oldValue.equals(Boolean.FALSE)) {
			  newValue = Boolean.TRUE;
		  } else {
			  newValue = Boolean.FALSE;
		  }
	  } else {
		  newValue = Boolean.valueOf(value);
	  }
	  
	  token.setState(state, newValue);
	  MapTool.addLocalMessage("Token '" + token.getName() + "' is " + (newValue.booleanValue() ? "now" : "no longer")
			  + " marked '" + state + "'.");
	  MapTool.serverCommand().putToken(MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(),
				token);
	  
  }
  
  
  /**
   * Find a state name by ignoring case
   * 
   * @param state Name entered on command line
   * @return The valid state name w/ correct case or <code>null</code> if 
   * no state with the passed name could be found.
   */
public String getState(String state) {
	if (MapTool.getCampaign().getTokenStatesMap().get(state) != null || "light".equals(state))
		return state;
	String newState = null;
	for (String name : MapTool.getCampaign().getTokenStatesMap().keySet()) {
		if (name.equalsIgnoreCase(state)) {
			if (newState != null) {
				MapTool.addLocalMessage("The name '" + state
						+ "' can be the state " + newState + " or " + name	+ ".");
				return null;
			} // endif
			newState = name;
		} // endif
	} // endfor
	return newState;
  }
}
