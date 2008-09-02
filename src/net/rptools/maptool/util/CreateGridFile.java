/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package net.rptools.maptool.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import net.rptools.lib.image.ImageUtil;

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
