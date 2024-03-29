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
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import net.rptools.maptool.model.Token;

/**
 * Paint a dot so that it doesn't overlay any other states being displayed in the same grid.
 * 
 * @author Jay
 */
public class FlowColorDotTokenOverlay extends XTokenOverlay {

    /**
     * Size of the grid used to place a token with this state.
     */
    private int grid;

    /**
     * Flow used to define position of states
     */
    private transient TokenOverlayFlow flow;
    
    /**
     * Default constructor needed for XML encoding/decoding
     */
    public FlowColorDotTokenOverlay() {
      this(BooleanTokenOverlay.DEFAULT_STATE_NAME, Color.RED, -1);
    }

    /**
     * Create a new dot token overlay
     * 
     * @param aName Name of the token overlay
     * @param aColor Color of the dot
     * @param aGrid Size of the overlay grid for this state. All states with the 
     * same grid size share the same overlay.
     */
    public FlowColorDotTokenOverlay(String aName, Color aColor, int aGrid) {
      super(aName, aColor, 0);
      grid = aGrid;
    }

    /**
     * @see net.rptools.maptool.client.ui.token.BooleanTokenOverlay#clone()
     */
    @Override
    public Object clone() {
        BooleanTokenOverlay overlay = new FlowColorDotTokenOverlay(getName(), getColor(), grid);
        overlay.setOrder(getOrder());
        overlay.setGroup(getGroup());
        overlay.setMouseover(isMouseover());
        overlay.setOpacity(getOpacity());
        overlay.setShowGM(isShowGM());
        overlay.setShowOwner(isShowOwner());
        overlay.setShowOthers(isShowOthers());
        return overlay;
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
     * @see net.rptools.maptool.client.ui.token.BooleanTokenOverlay#paintOverlay(java.awt.Graphics2D, net.rptools.maptool.model.Token, Rectangle)
     */
    @Override
    public void paintOverlay(Graphics2D g, Token aToken, Rectangle bounds) {
        Color tempColor = g.getColor();
        Stroke tempStroke = g.getStroke();
        Composite tempComposite = g.getComposite();
        try {
            g.setColor(getColor());
            g.setStroke(getStroke());
            if (getOpacity() != 100)
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)getOpacity()/100));
            Shape s = getShape(bounds, aToken);
            g.fill(s);
        } finally {
            g.setColor(tempColor);
            g.setStroke(tempStroke);
            g.setComposite(tempComposite);
        }
    }
    
    /**
     * Return an ellipse.
     * 
     * @param bounds Bounds of the token
     * @param token Token being rendered.
     * @return An ellipse that fits inside of the bounding box returned by the flow.
     */
    protected Shape getShape(Rectangle bounds, Token token) {
        Rectangle2D r = getFlow().getStateBounds2D(bounds, token, getName());
        return new Ellipse2D.Double(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

    /** @return Getter for grid */
    public int getGrid() {
        return grid;
    }
}
