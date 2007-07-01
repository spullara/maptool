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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.SwingUtilities;

import net.rptools.lib.image.ImageUtil;
import net.rptools.maptool.client.AppStyle;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;


/**
 */
public class GraphicsUtil {

	private static final int BOX_PADDINGX = 5;
	private static final int BOX_PADDINGY = 2;
	
	// TODO: Make this configurable
	private static final String LABEL_BOX_IMG = "net/rptools/maptool/client/image/labelbox.png";
	
	private static BufferedImage labelBoxLeftImage;
	private static BufferedImage labelBoxRightImage;
	private static BufferedImage labelBoxMiddleImage;
	private static int leftMargin = 4;
	private static int rightMargin = 4;
	
	static {
		try {
			BufferedImage image = ImageUtil.getCompatibleImage(LABEL_BOX_IMG);
			
			labelBoxLeftImage = image.getSubimage(0, 0, leftMargin, image.getHeight());
			labelBoxRightImage = image.getSubimage(image.getWidth()-rightMargin, 0, rightMargin, image.getHeight());
			labelBoxMiddleImage = image.getSubimage(leftMargin, 0, image.getWidth() - leftMargin - rightMargin, image.getHeight());
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	/**
	 * A multiline text wrapping popup.  
	 * @param string - the string to display in he popup
	 * @param maxWidth - the max width in pixels before wrapping the text
	 */
	public static Rectangle drawPopup(Graphics2D g, String string,
			int x, int y, int justification, int maxWidth) {
		
		return drawPopup(g, string, x, y, justification, Color.black, Color.white, maxWidth, 0.5f);
	}	
	
	public static Rectangle drawPopup(Graphics2D g, String string,
										int x, int y, int justification,
										Color background, Color foreground, 
										int maxWidth, float alpha) {
    	
        if (string == null) {
            string = "";
        }
        
		// TODO: expand to work for variable width fonts.
        Font oldFont = g.getFont();
        Font fixedWidthFont = new Font("Courier New", 0, 12);
        g.setFont(fixedWidthFont);
    	FontMetrics fm = g.getFontMetrics();
	    	
    	StringBuilder sb = new StringBuilder();
    	while (SwingUtilities.computeStringWidth(fm, sb.toString()) < maxWidth) {
    		sb.append("0");
    	}
        int maxChars = sb.length()-1;
            			
        string = StringUtil.wrapText( string, Math.min( maxChars, string.length() ) );
        
		String pattern = "\n";
		String[] stringByLine = string.split(pattern);
		int rows = stringByLine.length;
		
		String longestRow = new String();
		for (int i=0; i<rows; i++) {
			if (longestRow.length() < stringByLine[i].length() ) {
				longestRow = stringByLine[i];
			}
		}
		
		int strPixelHeight = fm.getHeight();
		int strPixelWidth = SwingUtilities.computeStringWidth(fm, longestRow);

		int width = strPixelWidth + BOX_PADDINGX*2;
		int height = strPixelHeight*rows + BOX_PADDINGY*2; 

		ZoneRenderer renderer = MapTool.getFrame().getCurrentZoneRenderer();
		
		y = Math.max(y - height, BOX_PADDINGY);	
		switch (justification) {
		case SwingUtilities.CENTER:
			x = x - strPixelWidth/2 - BOX_PADDINGX;
			break;
		case SwingUtilities.RIGHT:
			x = x - strPixelWidth - BOX_PADDINGX;
			break;
		case SwingUtilities.LEFT:
			break;
		}
		
		x = Math.max(x, BOX_PADDINGX);
		x = Math.min(x, renderer.getWidth() - width - BOX_PADDINGX);
		
		
		// Box
		Composite oldComposite = g.getComposite();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		
		Rectangle boxBounds = new Rectangle(x, y, width, height);
		g.setColor(background);
		g.fillRect(boxBounds.x, boxBounds.y, boxBounds.width, boxBounds.height);
    	AppStyle.border.paintWithin(g, boxBounds);
    	g.setComposite(oldComposite);
    	
		// Renderer message
		g.setColor(foreground);

		for (int i = 0; i < stringByLine.length; i++) {
			int textX = x + BOX_PADDINGX;
			int textY = y + BOX_PADDINGY + fm.getAscent() + strPixelHeight*i;
			g.drawString(stringByLine[i], textX, textY);
		}
		
		g.setFont(oldFont);
		
		return boxBounds;
	}
	
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
		g.drawImage(labelBoxLeftImage, x, y, labelBoxLeftImage.getWidth(), height, null);
		g.drawImage(labelBoxRightImage, x+width-rightMargin, y, rightMargin, height, null);
		g.drawImage(labelBoxMiddleImage, x+leftMargin, y, width-rightMargin-leftMargin, height, null);
		
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
	 * @return a lighter color, as opposed to a brighter color as in Color.brighter().
	 *  This prevents light colors from getting bleached out.
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
    
    public static Area createAreaBetween(Point a, Point b, int width) {
    	
    	// Find the angle that is perpendicular to the slope of the points
    	double rise = b.y - a.y;
    	double run = b.x - a.x;
    	
    	double theta1 = Math.atan2(rise, run) - Math.PI/2;
    	double theta2 = Math.atan2(rise, run) + Math.PI/2;

    	double ax1 = a.x + width * Math.cos(theta1);
    	double ay1 = a.y + width * Math.sin(theta1);
    	
    	double ax2 = a.x + width * Math.cos(theta2);
    	double ay2 = a.y + width * Math.sin(theta2);
    	
    	double bx1 = b.x + width * Math.cos(theta1);
    	double by1 = b.y + width * Math.sin(theta1);
    	
    	double bx2 = b.x + width * Math.cos(theta2);
    	double by2 = b.y + width * Math.sin(theta2);

    	GeneralPath path = new GeneralPath();
    	path.moveTo((float)ax1, (float)ay1);
    	path.lineTo((float)ax2, (float)ay2);
    	path.lineTo((float)bx2, (float)by2);
    	path.lineTo((float)bx1, (float)by1);
    	path.closePath();
    	
    	return new Area(path);
    }

    public static boolean intersects(Area lhs, Area rhs) {

    	if (lhs == null || lhs.isEmpty() || rhs == null || rhs.isEmpty()) {
    		return false;
    	}
    	
    	if (!lhs.getBounds().intersects(rhs.getBounds())) {
    		return false;
    	}
    	
    	Area newArea = new Area(lhs);
    	newArea.intersect(rhs);
    	
    	return !newArea.isEmpty();
    }
    
    public static Area createLineSegmentEllipse(int x1, int y1, int x2, int y2) {

    	int x = Math.min(x1, x2);
    	int y = Math.min(y1, y2);
    	
    	int w = Math.abs(x1 - x2);
    	int h = Math.abs(y1 - y2);
    	
    	// Operate from the center of the ellipse
    	x += w/2;
    	y += h/2;
    	
        // The Ellipse class uses curves, which doesn't work with the topology, so we have to create a geometric ellipse
        // out of line segments
        GeneralPath path = new GeneralPath();

        int a = w/2;
        int b = h/2;

        boolean firstMove = true;
        for (double t = -Math.PI; t <= Math.PI; t+= .2) { // TODO: Configure the number of points, the increment value is (2pi/steps)
        	
        	int px = (int)(x + a * Math.cos(t));
        	int py = (int)(y + b * Math.sin(t));

        	if (firstMove) {
        		path.moveTo(px, py);

        		firstMove = false;
        	} else {
        		path.lineTo(px, py);
        	}
        }
        path.closePath();
        return new Area(path);
    }
    
    
}
