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

import java.awt.geom.Point2D;

import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.AbstractPoint;
import net.rptools.maptool.model.ZonePoint;


public class ScreenPoint extends Point2D.Double {

    public ScreenPoint(double x, double y) {
        super(x, y);
    }
    
    /**
     * Translate the point from screen x,y to zone x,y
     */
    public ZonePoint convertToZone(ZoneRenderer renderer) {

        double scale = renderer.getScale();

        double zX = x;
        double zY = y;
        
        // Translate
        zX -= renderer.getViewOffsetX();
        zY -= renderer.getViewOffsetY();
        
        // Scale
        zX = (int)Math.floor(zX / scale);
        zY = (int)Math.floor(zY / scale);
            
//        System.out.println("s:" + scale + " x:" + x + " zx:" + zX + " c:" + (zX / scale) + " - " + Math.floor(zX / scale));

        return new ZonePoint((int)zX, (int)zY);
    }
    
    public static ScreenPoint fromZonePoint(ZoneRenderer renderer, ZonePoint zp) {
    	return fromZonePoint(renderer, zp.x, zp.y);
    }
    
    public static ScreenPoint fromZonePoint(ZoneRenderer renderer, double x, double y) {

        double scale = renderer.getScale();
        
        double sX = x;
        double sY = y;
        
        sX = sX * scale;
        sY = sY * scale;
        
        // Translate
        sX += renderer.getViewOffsetX();
        sY += renderer.getViewOffsetY();

        return new ScreenPoint(sX, sY);
    }
    
    public static ScreenPoint fromZonePointRnd(ZoneRenderer renderer, double x, double y) {
    	ScreenPoint sp = fromZonePoint(renderer, x, y);
    	sp.x = Math.round(sp.x);
    	sp.y = Math.round(sp.y);
    	
    	return sp;
    }
    
    public static ScreenPoint fromZonePointHigh(ZoneRenderer renderer, double x, double y) {
    	ScreenPoint sp = fromZonePoint(renderer, x, y);
    	sp.x = Math.ceil(sp.x);
    	sp.y = Math.ceil(sp.y);
    	
    	return sp;
    }
    
    public static ScreenPoint fromZonePointLow(ZoneRenderer renderer, double x, double y) {
    	ScreenPoint sp = fromZonePoint(renderer, x, y);
    	sp.x = Math.floor(sp.x);
    	sp.y = Math.floor(sp.y);
    	
    	return sp;
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

    public void translate(int dx, int dy) {
        x += dx;
        y += dy;
    }
}
