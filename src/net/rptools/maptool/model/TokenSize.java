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

import java.awt.Dimension;

import net.rptools.lib.swing.SwingUtil;

/**
 * @author trevor
 */
public class TokenSize {
    
    public static enum Size {
        Fine(0, 0.5f),
        Diminutive(1, 0.5f),
        Tiny(2, 0.5f),
        Small(3, 0.75f),
        Medium(4, 1),
        Large(5, 2),
        Huge(6, 3),
        Gargantuan(7, 4),
        Colossal(8, 6);
        
        private final int value;
        private final float sizeFactor;
        
        private Size(int value, float sizeFactor) {
            this.value = value;
            this.sizeFactor = sizeFactor;
        }
        
        public int value() { return value; }
        public float sizeFactor() { return sizeFactor; }
    }

	// This is a enum hack since enums aren't serializable
    public static int getWidth(Token token, int gridSize) {
        if (!token.isSnapToScale()) return token.getWidth();
        
        return getSize(token, gridSize).width;
    }
    
    public static int getHeight(Token token, int gridSize) {
        if (!token.isSnapToScale()) return token.getWidth();
        
        return getSize(token, gridSize).height;
    }
    
    private static Dimension getSize(Token token, int gridSize) {
        
        //Size size = getSizeInstance(token.getSize());
        Dimension size = new Dimension(token.getWidth(), token.getHeight());
        int destSize = (int) (getSizeInstance(token.getSize()).sizeFactor() * gridSize); 
        SwingUtil.constrainTo(size, destSize);
        
        return size;
    }
    
    public static Size getSizeInstance(int size) {
        Size[] sizes = Size.values();
        for (int i = 0; i < sizes.length; i++) {
            if (sizes[i].value() == size)
                return sizes[i];
        }
        
        return Size.Medium;
    }
}
