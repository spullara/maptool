/* Copyright 2008 Jay Gorrell
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.rptools.maptool.client.ui.token;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import net.rptools.maptool.model.Token;

/**
 *
 * @author Jay
 */
public class TwoToneBarTokenOverlay extends DrawnBarTokenOverlay {

    /**
     * Background color of the bar.
     */
    private Color bgColor;
    
    /**
     * Construct a complete bar
     * 
     * @param name Name of the bar
     * @param aBarColor The color of the bar.
     * @param aBgColor The background color.
     * @param thickness The thickness of the bar and background.
     */
    public TwoToneBarTokenOverlay(String name, Color aBarColor, Color aBgColor, int thickness) {
        super(name, aBarColor, thickness);
        bgColor = aBgColor;
    }
    
    /**
     * Default constructor for serialization
     */
    public TwoToneBarTokenOverlay() {
        this(AbstractTokenOverlay.DEFAULT_STATE_NAME, Color.RED, Color.BLACK, 5);
    }

    /** @return Getter for bgColor */
    public Color getBgColor() {
        return bgColor;
    }

    /** @param bgColor Setter for bgColor */
    public void setBgColor(Color bgColor) {
        this.bgColor = bgColor;
    }

    /**
     * @see net.rptools.maptool.client.ui.token.BarTokenOverlay#paintOverlay(java.awt.Graphics2D, net.rptools.maptool.model.Token, java.awt.Rectangle, double)
     */
    @Override
    public void paintOverlay(Graphics2D g, Token token, Rectangle bounds, double value) {
        int width = (getSide() == Side.TOP || getSide() == Side.BOTTOM) ? bounds.width : getThickness();
        int height = (getSide() == Side.LEFT || getSide() == Side.RIGHT) ? bounds.height : getThickness();
        int x = 0;
        int y = 0;
        switch (getSide()) {
        case RIGHT:
            x = bounds.width - width;
            break;
        case BOTTOM:
            y = bounds.height - height;
        } // endswitch
        Color tempColor = g.getColor();
        g.setColor(bgColor);
        g.fillRect(x, y, width, height);

        // Draw the bar
        int borderSize = getThickness() > 5 ? 2 : 1;
        x += borderSize;
        y += borderSize;
        width -= borderSize * 2;
        height -= borderSize * 2;
        if (getSide() == Side.TOP || getSide() == Side.BOTTOM) {
            width = calcBarSize(width, value);
        } else {
            height = calcBarSize(height, value);
            y += bounds.height - borderSize * 2 - height;
        }
        g.setColor(getBarColor());
        g.fillRect(x, y, width, height);
        g.setColor(tempColor);
    }

    /**
     * @see net.rptools.maptool.client.ui.token.AbstractTokenOverlay#clone()
     */
    @Override
    public Object clone() {
        BarTokenOverlay overlay = new TwoToneBarTokenOverlay(getName(), getBarColor(), bgColor, getThickness());
        overlay.setOrder(getOrder());
        overlay.setGroup(getGroup());
        overlay.setMouseover(isMouseover());
        overlay.setOpacity(getOpacity());
        overlay.setIncrements(getIncrements());
        overlay.setSide(getSide());
        overlay.setShowGM(isShowGM());
        overlay.setShowOwner(isShowOwner());
        overlay.setShowOthers(isShowOthers());
        return overlay;
    }
}
