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
package net.rptools.maptool.model;

/**
 * @author trevor
 */
public class TokenSize {

	// This is a enum hack since enums aren't serializable
	public static final int NORMAL = 0;
	public static final int SMALL = 1;
	public static final int LARGE = 2;
	
	private static final float [][] SIZE_TABLE = new float[][] {
		
		{1, 1}, // NORMAL - needs to be at index 0
		{.5F, .5F}, // SMALL
		{2, 2} // LARGE
	};

	// TODO: I don't like the static-ness of this, use some sort of enum or something
	public static int getWidth(Token token, int gridSize) {
		return getSize(token, gridSize, 0);
	}
	
	public static int getHeight(Token token, int gridSize) {
		return getSize(token, gridSize, 1);
	}
	
	private static int getSize(Token token, int gridSize, int direction) {
		
		if (token.isSnapToScale()) {
			return (int)(SIZE_TABLE[token.getSize()][direction] * gridSize);
		}

		return direction == 0 ? token.getSizeX() : token.getSizeY(); 
	}
}
