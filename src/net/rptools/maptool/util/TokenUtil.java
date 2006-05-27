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

import java.awt.image.BufferedImage;

public class TokenUtil {

	public enum TokenType {
		TOP_DOWN,
		CIRCLE,
		SQUARE
	}
	
	public TokenType getTokenType(BufferedImage image) {
		
		int pixelCount = 0;
		
		for (int row = 0; row < image.getHeight(); row++) {
			for (int col = 0; col < image.getWidth(); col++) {
				int pixel = image.getRGB(col, row);
				if ((pixel&0xff) != 0) {
					// Alpha
					pixelCount ++;
				}
			}
		}
		
		int circlePixelCount = (int)(Math.PI * image.getWidth() * image.getHeight());
		int squarePixelCount = image.getWidth() * image.getHeight();
		int topDownPixelCount = circlePixelCount * 3 / 4; // arbitrary
		
		double circleResult = Math.abs(1-(pixelCount / circlePixelCount));
		double squareResult = Math.abs(1-(pixelCount / squarePixelCount));
		double topDownResult = Math.abs(1-(pixelCount / topDownPixelCount));
		
		if (circleResult < squareResult && circleResult < topDownResult) {
			return TokenType.CIRCLE;
		}
		if (squareResult < circleResult && squareResult < topDownResult) {
			return TokenType.SQUARE;
		}
		return TokenType.TOP_DOWN;
	}
}
