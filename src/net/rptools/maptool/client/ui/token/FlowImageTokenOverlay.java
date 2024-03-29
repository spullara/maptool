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
        this(BooleanTokenOverlay.DEFAULT_STATE_NAME, null, -1);
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
     * @see net.rptools.maptool.client.ui.token.BooleanTokenOverlay#clone()
     */
    @Override
    public Object clone() {
        BooleanTokenOverlay overlay = new FlowImageTokenOverlay(getName(), getAssetId(), grid);
        overlay.setOrder(getOrder());
        overlay.setGroup(getGroup());
        overlay.setMouseover(isMouseover());
        overlay.setOpacity(getOpacity());
        overlay.setShowGM(isShowGM());
        overlay.setShowOwner(isShowOwner());
        overlay.setShowOthers(isShowOthers());
        return overlay;
    }

    /** @return Getter for grid */
    public int getGrid() {
        return grid;
    }
}
