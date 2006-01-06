/* --- Copyright 2005 Bluejay Software. All rights reserved --- */

package net.rptools.maptool.client.ui.token;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;

import net.rptools.maptool.model.Token;

/**
 * @author jgorrell
 * @version $Revision$ $Date$ $Author$
 */

public class OTokenOverlay extends XTokenOverlay {

    /**
     * Create an O token overlay with the given name.
     * 
     * @param aName Name of this token overlay.
     * @param aColor The color of this token overlay.
     * @param aWidth The width of the lines in this token overlay.
     */
    public OTokenOverlay(String aName, Color aColor, int aWidth) {
        super(aName, aColor, aWidth);
    }

    /**
     * @see net.rptools.maptool.client.ui.token.TokenOverlay#paintOverlay(java.awt.Graphics2D, net.rptools.maptool.model.Token)
     */
    @Override
    public void paintOverlay(Graphics2D g, Token aToken) {
        Color tempColor = g.getColor();
        g.setColor(getColor());
        Stroke tempStroke = g.getStroke();
        g.setStroke(getStroke());
        Rectangle b = g.getClipBounds();
        double offset = getStroke().getLineWidth() / 2.0;
        g.draw(new Ellipse2D.Double(0 + offset, 0 + offset, b.width - offset * 2, b.height - offset * 2));
        g.setColor(tempColor);
        g.setStroke(tempStroke);
    }
}
