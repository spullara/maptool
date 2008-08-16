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

import net.rptools.lib.MD5Key;
import net.rptools.maptool.model.Token;

/**
 * An overlay that allows multiple images to be placed on the token so that they
 * do not interfere with any tokens on the same grid.
 * 
 * @author Jay
 */
public class FlowImageTokenOverlay extends ImageTokenOverlay {

    /**
     * Size of the grid used to place a token with this state.
     */
    private int grid;

    /**
     * Flow used to define position of states
     */
    private transient TokenOverlayFlow flow;
    
    /**
     * Needed for serialization
     */
    public FlowImageTokenOverlay() {
        this(TokenOverlay.DEFAULT_STATE_NAME, null, -1);
    }
    
    /**
     * Create the image overlay flow for the name, asset and grid
     * 
     * @param name Name of the new state
     * @param assetId Asset displayed for the state
     * @param aGrid Size of the overlay grid for this state. All states with the 
     * same grid size share the same overlay.
     */
    public FlowImageTokenOverlay(String name, MD5Key assetId, int aGrid) {
        super(name, assetId);
        grid = aGrid;
    }
    
    /**
     * Get the flow used to position the states.
     * 
     * @return Flow used to position the states
     */
    protected TokenOverlayFlow getFlow() {
        if (flow == null && grid > 0)
            flow = TokenOverlayFlow.getInstance(grid);
        return flow;
    }
    
    /**
     * @see net.rptools.maptool.client.ui.token.ImageTokenOverlay#getImageBounds(java.awt.Rectangle, Token)
     */
    @Override
    protected Rectangle getImageBounds(Rectangle bounds, Token token) {
        return getFlow().getStateBounds(bounds, token, getName());
    }

    /**
     * @see net.rptools.maptool.client.ui.token.TokenOverlay#clone()
     */
    @Override
    public Object clone() {
        TokenOverlay overlay = new FlowImageTokenOverlay(getName(), getAssetId(), grid);
        overlay.setOrder(getOrder());
        overlay.setGroup(getGroup());
        overlay.setMouseover(isMouseover());
        overlay.setOpacity(getOpacity());
        return overlay;
    }

    /** @return Getter for grid */
    public int getGrid() {
        return grid;
    }
}
