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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * This singleton keeps track of all of the available token states
 * and maps token overlays to each state.
 * 
 * @author jgorrell
 * @version $Revision$ $Date$ $Author$
 */
public class TokenStates {

    /**
     * The map of state names to their token overlays
     */
    private static Map<String, TokenOverlay> states = new LinkedHashMap<String, TokenOverlay>();

    /**
     * Set up the token states
     */
    static {
        putOverlay(new XTokenOverlay("Dead", Color.RED, 5));
        putOverlay(new XTokenOverlay("Disabled", Color.GRAY, 5));
        putOverlay(new ShadedTokenOverlay("Hidden", Color.BLACK));
        putOverlay(new OTokenOverlay("Prone", Color.BLUE, 5));
        putOverlay(new OTokenOverlay("Incapacitated", Color.RED, 5));
        putOverlay(new ColorDotTokenOverlay("Other", Color.RED, 3));
    }

    /**
     * Add a token overlay as a state.
     * 
     * @param overlay The overlay being added.
     */
    public static void putOverlay(TokenOverlay overlay) {
        states.put(overlay.getName(), overlay);
    }

    /**
     * Get the overlay for a particular state.
     * 
     * @param state Find the overlay for this state.
     * @return The overlay for the passed state or <code>null</code> if there
     * wasn't an overlay for the passed state.
     */
    public static TokenOverlay getOverlay(String state) {
        return states.get(state);
    }

    /**
     * Get the list of states defined.
     * 
     * @return A set containing the states that have been defined. 
     */
    public static Set<String> getStates() {
        return states.keySet();
    }
}
