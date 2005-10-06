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
package net.rptools.maptool.client;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import net.rptools.common.swing.ImageBorder;
import net.rptools.common.util.ImageUtil;

/**
 * @author trevor
 */
public class ClientStyle {

	public static ImageBorder border = ImageBorder.GRAY;
	public static ImageBorder selectedBorder = ImageBorder.RED;
	public static ImageBorder boardBorder = ImageBorder.WOOD;

	public static BufferedImage tokenInvisible;

    public static BufferedImage cellPathImage;
    public static BufferedImage cellWaypointImage;
	
    public static Color gridColor = Color.darkGray;
    
    public static Color selectionBoxOutline = Color.black;
    public static Color selectionBoxFill = Color.blue;
    
	static {
		
		try {
			// Set defaults
			tokenInvisible = ImageUtil.getImage("net/rptools/maptool/client/image/icon_invisible.png");
            cellPathImage  = ImageUtil.getCompatibleImage("net/rptools/maptool/client/image/blueDot.png");
            cellWaypointImage  = ImageUtil.getCompatibleImage("net/rptools/maptool/client/image/redDot.png");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
