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

import net.rptools.maptool.client.ui.zone.ZoneRenderer;


public class ZonePoint extends AbstractPoint {

    public ZonePoint(int x, int y) {
        super(x, y);
    }
    
    /**
     * Translate the point from zone x,y to screen x,y
     */
    public ScreenPoint convertToScreen(ZoneRenderer renderer) {
        
        double scale = renderer.getScale();
        
        int sX = x;
        int sY = y;
        
        sX = (int)(sX * scale);
        sY = (int)(sY * scale);
        
        // Translate
        sX += renderer.getViewOffsetX();
        sY += renderer.getViewOffsetY();
        
        return new ScreenPoint(sX, sY);
    }
    
    public static ZonePoint fromScreenPoint(ZoneRenderer renderer, int x, int y) {
        
        ScreenPoint sp = new ScreenPoint(x, y);
        return sp.convertToZone(renderer);
    }

    public CellPoint convertToCell(ZoneRenderer renderer) {
    	double calcX = x / (float)renderer.getZone().getGridSize();
    	double calcY = y / (float)renderer.getZone().getGridSize();
    	
    	int newX = (int)(x >= 0 ? calcX : calcX-1);
    	int newY = (int)(y >= 0 ? calcY : calcY-1);
    	
//    	System.out.format("%f, %f => %d, %d\n", calcX, calcY, newX, newY);
        return new CellPoint(newX, newY);
    }
    
    public String toString() {
        return "ZonePoint" + super.toString();
    }
}
