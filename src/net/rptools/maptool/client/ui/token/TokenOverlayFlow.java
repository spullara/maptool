/* The MIT License
 * 
 * Copyright (c) 2008 Jay Gorrell
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

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Token;

/**
 * This supports token states flowing from one box to the next when multiple states are set on the same token.
 * 
 * @author Jay
 */
public class TokenOverlayFlow {

    /*---------------------------------------------------------------------------------------------
     * Instance Variables
     *-------------------------------------------------------------------------------------------*/
    
    /**
     * The number of cells in the X & Y directions on the token.
     */
    private int gridSize;
    
    /**
     * Offsets for the placement of each state in percentage of the token size. They are calculated from the grid size.
     */
    private double[] offsets;
    
    /**
     * The size of a cell in percentage of the token size. Calculated from the grid size.
     */
    private double size;
    
    /**
     * This map contains the list of states for each token in the order they are drawn. It's done
     * this way so that the states don't jump around as they are added or removed. 
     */
    private Map<GUID, List<String>> savedStates = new HashMap<GUID, List<String>>();
    
    /*---------------------------------------------------------------------------------------------
     * Class Variables
     *-------------------------------------------------------------------------------------------*/
    
    /**
     * Flows are shared by multiple token overlay types. This map contains all of the available 
     * flow instances. 
     */
    private static Map<Integer, TokenOverlayFlow> instances = new HashMap<Integer, TokenOverlayFlow>(); 
        
    /*---------------------------------------------------------------------------------------------
     * Constructor
     *-------------------------------------------------------------------------------------------*/
    
    /**
     * Internal constructor to make sure only one of each grid size is created.
     * 
     * @param aGrid The size of the grid placed over the token.
     */
    private TokenOverlayFlow(int aGrid) {
        gridSize = aGrid;
        size = 1.0D/gridSize;
        offsets = new double[gridSize];
        for (int i = 0; i < offsets.length; i++)
            offsets[i] = i * size;
    }

    /*---------------------------------------------------------------------------------------------
     * Instance Methods
     *-------------------------------------------------------------------------------------------*/
    
    /**
     * Calculate the bounds to paint the passed state. It takes into account states that have already
     * been set w/o changing the order. It also removes any unused states.
     * 
     * @param bounds The token's bounds. All states are drawn inside this area.
     * @param token Rendering the states for this token.
     * @param state The state being rendered.
     * @return The bounds used to paint the state.
     */
    public Rectangle2D getStateBounds2D(Rectangle bounds, Token token, String state) {
        
        // Find the list of states already drawn on the token
        List<String> states = savedStates.get(token.getId());
        if (states == null) {
            states = new LinkedList<String>();
            savedStates.put(token.getId(), states);
        } // endif
        
        // Find the state in the list, make sure that all the states before it still exist.
        ListIterator<String> i = states.listIterator();
        boolean found = false;
        while (i.hasNext()) {
            String savedState = i.next();
            if (!found && savedState.equals(state)) {
                found = true;
            } else {
                Boolean stateValue = (Boolean)token.getState(savedState);
                if (stateValue == null || !stateValue.booleanValue())
                    i.remove();
            } // endif
        } // endwhile
        
        // Find the index of the state, then convert it into row & column
        int index = states.size();
        if (found) {
            index = states.indexOf(state);
        } else {
            states.add(state);
        } // endif
        if (index >= gridSize * gridSize) {
            System.err.println("Overlapping states in grid size " + gridSize + " at " + index);
            index = index % (gridSize * gridSize);
        } // endif
        int row = gridSize - 1 - (index / gridSize); // Start at bottom
        int col = gridSize - 1 - (index % gridSize); // Start at right
        
        // Build the rectangle from the passed bounds
        return new Rectangle2D.Double(offsets[col] * bounds.width + bounds.x,
                offsets[row] * bounds.height + bounds.y,
                size * bounds.width, size * bounds.height);
    }

    /**
     * Calculate the bounds to paint the passed state. It takes into account states that have already
     * been set w/o changing the order. It also removes any unused states.
     * 
     * @param bounds The token's bounds. All states are drawn inside this area.
     * @param token Rendering the states for this token.
     * @param state The state being rendered.
     * @return The bounds used to paint the state.
     */
    public Rectangle getStateBounds(Rectangle bounds, Token token, String state) {
        Rectangle2D r = getStateBounds2D(bounds, token, state);
        return new Rectangle((int)Math.round(r.getX()), (int)Math.round(r.getY()),
                             (int)Math.round(r.getWidth()), (int)Math.round(r.getHeight()));
    }
    
    /*---------------------------------------------------------------------------------------------
     * Class Methods
     *-------------------------------------------------------------------------------------------*/
    
    /**
     * Get the one and only instance of an overlay flow for a particular grid size.
     * 
     * @param grid The size of the grid placement for the placement of states. 
     * @return  The flow for the passed grid size
     */
    public static TokenOverlayFlow getInstance(int grid) {
        Integer key = Integer.valueOf(grid);
        TokenOverlayFlow instance = instances.get(key);
        if (instance == null) {
            instance = new TokenOverlayFlow(grid);
            instances.put(key, instance);
        } // endif
        return instance;
    }
}
