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
import net.rptools.maptool.model.drawing.AbstractTemplate.Quadrant;

/**
 * Place an image in a given corner.
 * 
 * @author Jay
 */
public class CornerImageTokenOverlay extends ImageTokenOverlay {

    /**
     * The corner where the image is placed
     */
    private Quadrant corner = Quadrant.SOUTH_EAST;

    /**
     * Needed for serialization
     */
    public CornerImageTokenOverlay() {
        this(TokenOverlay.DEFAULT_STATE_NAME, null, Quadrant.SOUTH_EAST);
    }
    
    /**
     * Create the complete image overlay.
     * 
     * @param name Name of the new token overlay
     * @param anAssetId Id of the image displayed in the new token overlay.
     * @param aCorner Corner that contains the image.
     */
    public CornerImageTokenOverlay(String name, MD5Key anAssetId, Quadrant aCorner) {
        super(name, anAssetId);
        corner = aCorner;
    }
    
    /**
     * @see net.rptools.maptool.client.ui.token.TokenOverlay#clone()
     */
    @Override
    public Object clone() {
        return new CornerImageTokenOverlay(getName(), getAssetId(), corner);
    }

    /**
     * @see net.rptools.maptool.client.ui.token.ImageTokenOverlay#getImageBounds(java.awt.Rectangle, Token)
     */
    @Override
    protected Rectangle getImageBounds(Rectangle bounds, Token token) {
        int x = (bounds.width + 1) / 2; 
        int y = (bounds.height + 1) / 2;
        switch (corner) {
        case SOUTH_EAST:
          break;
        case SOUTH_WEST:
          x = 0;
          break;
        case NORTH_EAST:
          y = 0;
          break;
        case NORTH_WEST:
          x = y = 0;
          break;
        } // endswitch
        return new Rectangle(x, y, bounds.width / 2, bounds.height / 2);
    }
}
