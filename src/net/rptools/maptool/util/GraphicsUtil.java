/* The MIT License
 * 
 * Copyright (c) 2005 David Rice, Trevor Croft
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
package net.rptools.maptool.util;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.SwingUtilities;

import net.rptools.maptool.client.AppStyle;

/**
 */
public class GraphicsUtil {

	private static final int BOX_PADDINGX = 5;
	private static final int BOX_PADDINGY = 2;
	
    public static Rectangle drawBoxedString(Graphics2D g, String string, int centerX, int centerY) {
    	return drawBoxedString(g, string, centerX, centerY, SwingUtilities.CENTER);
    }
    
    public static Rectangle drawBoxedString(Graphics2D g, String string, int x, int y, int justification) {
    	return drawBoxedString(g, string, x, y, justification, Color.white, Color.black);
    }
    public static Rectangle drawBoxedString(Graphics2D g, String string, int x, int y, int justification, Color background, Color foreground) {
    	
        if (string == null) {
            string = "";
        }
        
    	// TODO: Put in justification
    	FontMetrics fm = g.getFontMetrics();
		int strWidth = SwingUtilities.computeStringWidth(fm, string);

		int width = strWidth + BOX_PADDINGX*2;
		int height = fm.getHeight() + BOX_PADDINGY*2; 
		
		y = y - fm.getHeight()/2 - BOX_PADDINGY;
		switch (justification) {
		case SwingUtilities.CENTER:
			x = x - strWidth/2 - BOX_PADDINGX;
			break;
		case SwingUtilities.RIGHT:
			x = x - strWidth - BOX_PADDINGX;
			break;
		case SwingUtilities.LEFT:
			break;
		}
		
		// Box
		Rectangle boxBounds = new Rectangle(x, y, width, height);
		g.setColor(background);
		g.fillRect(boxBounds.x, boxBounds.y, boxBounds.width, boxBounds.height);
		
    	AppStyle.border.paintWithin(g, boxBounds);
		
		// Renderer message
		g.setColor(foreground);
		int textX = x + BOX_PADDINGX;
		int textY = y + BOX_PADDINGY + fm.getAscent();

		g.drawString(string, textX, textY);
		
		return boxBounds;
    }
    
	public static Point getProjectedPoint(Point origin, Point target, int distance) {
		
		double x1 = origin.x;
		double x2 = target.x;
		
		double y1 = origin.y;
		double y2 = target.y;
		
		double angle = Math.atan2(y2 - y1, x2 - x1);
		
		double newX = x1 + distance * Math.cos(angle);
		double newY = y1 + distance * Math.sin(angle);
		
		return new Point((int)newX, (int)newY);
	}

	/**
	 * Returns a lighter color (as opposed to a brighter color as in the brighter() method
	 *  of the Color class) 
	 */
	public static Color lighter(Color c) {

		if (c == null)
			return null;
		else {
			int r = c.getRed();
			int g = c.getGreen();
			int b = c.getBlue();
			
			r += 64*(255 - r)/255;
			g += 64*(255 - g)/255;
			b += 64*(255 - b)/255;	
			
			return new Color(r, g, b);
		}
	}
    
}
