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

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;

import net.rptools.maptool.model.Token;

public class TokenUtil {

	public static Token.Type guessTokenType(Image image) {
		
		if (image instanceof BufferedImage) {
			return guessTokenType((BufferedImage) image);
		}
		
		int pixelCount = 0;
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        int [] pixelArray = new int [ width * height ];
        PixelGrabber pg = new PixelGrabber( image, 0, 0, width, height, pixelArray, 0, width );
        try 
        {
            pg.grabPixels();
        } 
        catch (InterruptedException e) 
        {
            System.err.println("interrupted waiting for pixels!");
            return Token.Type.TOP_DOWN;
        }

        if ((pg.getStatus() & ImageObserver.ABORT) != 0) 
        {
            System.err.println("image fetch aborted or errored");
            return Token.Type.TOP_DOWN;
        }
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Get the next pixel
                int pixel = pixelArray [ y*width + x ];
				if ((pixel&0xff000000) != 0) {
					pixelCount ++;
				}
            }
        }
        
        return guessTokenType(new Dimension(image.getWidth(null) ,image.getHeight(null)), pixelCount);
	}
	
	public static Token.Type guessTokenType(BufferedImage image) {
		
		int pixelCount = 0;
		
		for (int row = 0; row < image.getHeight(); row++) {
			for (int col = 0; col < image.getWidth(); col++) {
				int pixel = image.getRGB(col, row);
				if ((pixel&0xff000000) != 0) {
					pixelCount ++;
				}
			}
		}
		
		return guessTokenType(new Dimension(image.getWidth(), image.getHeight()), pixelCount);
	}
	
	private static Token.Type guessTokenType(Dimension size, int pixelCount) {
		
		double circlePixelCount = (int)(Math.PI * (size.width/2) * (size.height/2));
		double squarePixelCount = size.width * size.height;
		double topDownPixelCount = circlePixelCount * 3 / 4; // arbitrary
		
		double circleResult = Math.abs(1-(pixelCount / circlePixelCount));
		double squareResult = Math.abs(1-(pixelCount / squarePixelCount));
		double topDownResult = Math.abs(1-(pixelCount / topDownPixelCount));
		
		if (circleResult < squareResult && circleResult < topDownResult) {
			return Token.Type.CIRCLE;
		}
		if (squareResult < circleResult && squareResult < topDownResult) {
			return Token.Type.SQUARE;
		}
		return Token.Type.TOP_DOWN;
	}
}
