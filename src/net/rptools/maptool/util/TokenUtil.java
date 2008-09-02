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

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;

import net.rptools.maptool.model.Token;

public class TokenUtil {

	public static int getIndexNearestTo(int[] array, int value) {
		
    	int delta = -1;
    	int closest = -1;
    	for (int i = 0; i < array.length; i++) {
    		int currDelta = Math.abs(value - array[i]);
    		if (delta < 0 || currDelta < delta) {
    			closest = i;
    			delta = currDelta;
    		}
    	}
    	return closest;
	}
	
	public static Token.TokenShape guessTokenType(Image image) {
		
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
            return Token.TokenShape.TOP_DOWN;
        }

        if ((pg.getStatus() & ImageObserver.ABORT) != 0) 
        {
            System.err.println("image fetch aborted or errored");
            return Token.TokenShape.TOP_DOWN;
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
	
	public static Token.TokenShape guessTokenType(BufferedImage image) {
		
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
	
	private static Token.TokenShape guessTokenType(Dimension size, int pixelCount) {
		
		double circlePixelCount = (int)(Math.PI * (size.width/2) * (size.height/2));
		double squarePixelCount = size.width * size.height;
		double topDownPixelCount = circlePixelCount * 3 / 4; // arbitrary
		
		double circleResult = Math.abs(1-(pixelCount / circlePixelCount));
		double squareResult = Math.abs(1-(pixelCount / squarePixelCount));
		double topDownResult = Math.abs(1-(pixelCount / topDownPixelCount));
		
		if (circleResult < squareResult && circleResult < topDownResult) {
			return Token.TokenShape.CIRCLE;
		}
		if (squareResult < circleResult && squareResult < topDownResult) {
			return Token.TokenShape.SQUARE;
		}
		return Token.TokenShape.TOP_DOWN;
	}
}
