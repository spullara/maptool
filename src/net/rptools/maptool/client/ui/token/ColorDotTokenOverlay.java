package net.rptools.maptool.client.ui.token;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;

import net.rptools.maptool.model.Token;

public class ColorDotTokenOverlay extends XTokenOverlay {

    public ColorDotTokenOverlay(String aName, Color aColor, int aWidth) {
        super(aName, aColor, aWidth);
        // TODO Auto-generated constructor stub
    }

    /**
     * @see net.rptools.maptool.client.ui.token.TokenOverlay#paintOverlay(java.awt.Graphics2D, net.rptools.maptool.model.Token, Rectangle)
     */
    @Override
    public void paintOverlay(Graphics2D g, Token aToken, Rectangle bounds) {
        Color tempColor = g.getColor();
        Stroke tempStroke = g.getStroke();
        try {
            g.setColor(getColor());
            g.setStroke(getStroke());
            
            //double offset = getStroke().getLineWidth() / 2.0;
            double x = 0 + bounds.width * 0.8, y = x;
            double w = bounds.width * 0.2, h = w;
            
            Shape s = new Ellipse2D.Double(x, y, w, h); 
            
            g.fill(s);
            g.setColor(Color.BLACK);
            g.draw(s);
        } finally {
            g.setColor(tempColor);
            g.setStroke(tempStroke);
        }
    }

}
