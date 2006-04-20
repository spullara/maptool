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
import net.rptools.maptool.model.AbstractPoint;
import net.rptools.maptool.model.ZonePoint;


public class ScreenPoint extends AbstractPoint {

    public ScreenPoint(int x, int y) {
        super(x, y);
    }
    
    /**
     * Translate the point from screen x,y to zone x,y
     */
    public ZonePoint convertToZone(ZoneRenderer renderer) {

        double scale = renderer.getScale();

        int zX = x;
        int zY = y;
        
        // Translate
        zX -= renderer.getViewOffsetX();
        zY -= renderer.getViewOffsetY();
        
        // Scale
        zX = (int)(zX / scale);
        zY = (int)(zY / scale);
        
        return new ZonePoint(zX, zY);
    }
    
    public static ScreenPoint fromZonePoint(ZoneRenderer renderer, ZonePoint zp) {
    	return fromZonePoint(renderer, zp.x, zp.y);
    }
    
    public static ScreenPoint fromZonePoint(ZoneRenderer renderer, int x, int y) {
        
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
    
    public String toString() {
        return "ScreenPoint" + super.toString();
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object pt) {
      if (!(pt instanceof ScreenPoint)) return false;
      ScreenPoint spt = (ScreenPoint)pt;
      return spt.x == x && spt.y == y;
    }
}
