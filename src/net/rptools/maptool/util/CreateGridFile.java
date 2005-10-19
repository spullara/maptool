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
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import net.rptools.lib.util.ImageUtil;

/**
 * @author drice
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CreateGridFile {
    
    private static BufferedImage createImage(int width, int height, int gridSize, Color color, Color backgroundColor) {
        BufferedImage image = ImageUtil.createCompatibleImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        Graphics g = image.getGraphics();
        
        g.setColor(backgroundColor);
        g.fillRect(0, 0, width, height);
        
        g.setColor(color);
        drawGrid(g, width, height, gridSize);
        
        return image;
    }
    
    private static void drawGrid(Graphics g, int width, int height, int gridSize) {
        for (int x = 0; x < width; x += gridSize) {
            g.drawLine(x, 0, x, height - 1);
        }
        
        for (int y = 0; y < height; y += gridSize) {
            g.drawLine(0, y, width - 1, y);
        }
    }
    
    public static void main(String[] args) throws Exception {
        BufferedImage image = createImage(501, 501, 10, Color.RED, Color.WHITE);
        
        ImageIO.write(image, "png", new File("grid_10.png"));
    }

}
