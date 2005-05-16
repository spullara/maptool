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
import java.awt.Rectangle;

import javax.swing.SwingUtilities;

import net.rptools.maptool.client.ClientStyle;

/**
 */
public class GraphicsUtil {

	private static final int BOX_PADDINGX = 5;
	private static final int BOX_PADDINGY = 2;
	
    public static void drawBoxedString(Graphics2D g, String string, int centerX, int centerY) {
    	drawBoxedString(g, string, centerX, centerY, SwingUtilities.CENTER);
    }
    
    public static void drawBoxedString(Graphics2D g, String string, int centerX, int centerY, int justification) {
    	
    	// TODO: Put in justification
    	FontMetrics fm = g.getFontMetrics();
		int strWidth = SwingUtilities.computeStringWidth(fm, string);

		// Box
		Rectangle boxBounds = new Rectangle(centerX - strWidth/2 - BOX_PADDINGX, centerY - fm.getHeight()/2 - BOX_PADDINGY, strWidth + BOX_PADDINGX*2, fm.getHeight() + BOX_PADDINGY*2);
		g.setColor(Color.white);
		g.fillRect(boxBounds.x, boxBounds.y, boxBounds.width, boxBounds.height);
		
    	ClientStyle.border.paintWithin(g, boxBounds);
		
		// Renderer distance
		g.setColor(Color.black);
		int textX = centerX - (strWidth / 2);
		int textY = centerY - (fm.getHeight() / 2) + fm.getAscent();
		
		g.drawString(string, textX, textY);
    }
    
}
