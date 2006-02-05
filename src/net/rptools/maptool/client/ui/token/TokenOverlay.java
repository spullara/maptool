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

import java.awt.Graphics2D;
import java.awt.Rectangle;

import net.rptools.maptool.model.Token;

/**
 * An overlay that may be applied to a token to show state.
 * 
 * @author jgorrell
 * @version $Revision$ $Date$ $Author$
 */
public abstract class TokenOverlay {

    /*---------------------------------------------------------------------------------------------
     * Instance Variables
     *-------------------------------------------------------------------------------------------*/

    /**
     * The name of this overlay. Normally this is the name of a state.
     */
    private String name;

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
