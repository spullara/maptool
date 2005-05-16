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
package net.rptools.maptool.client.tool;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import net.rptools.maptool.client.ui.ZoneRenderer;
import net.rptools.maptool.model.ZoneMeasurement;
import net.rptools.maptool.util.GraphicsUtil;

/**
 * @author trevor
 */
public class ToolHelper {

	public static void drawBoxedMeasurement(ZoneRenderer renderer, Graphics2D g, Point startPoint, Point endPoint, boolean roundDistance) {
    	
        // Calculations
        int left = Math.min((int)startPoint.getX(), (int)endPoint.getX());
        int top = Math.min((int)startPoint.getY(), (int)endPoint.getY());
        int right = Math.max((int)startPoint.getX(), (int)endPoint.getX());
        int bottom = Math.max((int)startPoint.getY(), (int)endPoint.getY());

        ZoneMeasurement measurement = new ZoneMeasurement(renderer.getZone().getFeetPerCell(), roundDistance);
        
        // HORIZONTAL Measure
        g.setColor(Color.black);
        g.drawLine(left, top - 15, right, top - 15);
        g.drawLine(left, top - 20, left, top - 10);
        g.drawLine(right, top - 20, right, top - 10);
        String distance = measurement.formatDistanceBetween(renderer.getCellAt(left, top), renderer.getCellAt(right, top));
        GraphicsUtil.drawBoxedString(g, distance, left + (right - left)/2, top - 15);
        
        // VETICAL Measure
        g.drawLine(right + 15, top, right + 15, bottom);
        g.drawLine(right + 10, top, right + 20, top);
        g.drawLine(right + 10, bottom, right + 20, bottom);
        distance = measurement.formatDistanceBetween(renderer.getCellAt(right, top), renderer.getCellAt(right, bottom));
        GraphicsUtil.drawBoxedString(g, distance, right + 18, bottom + (top - bottom)/2);    
    }
    
	public static void drawMeasurement(ZoneRenderer renderer, Graphics2D g, Point startPoint, Point endPoint, boolean roundDistance) {
		
        int left = Math.min(startPoint.x, endPoint.x);
        int top = Math.min(startPoint.y, endPoint.y);
        int right = Math.max(startPoint.x, endPoint.x);
        int bottom = Math.max(startPoint.y, endPoint.y);
        
        Point cellStart = renderer.getCellAt(startPoint.x, startPoint.y);
        Point cellEnd   = renderer.getCellAt(endPoint.x, endPoint.y);

        int centerX = left + (right - left)/2;
        int centerY = bottom + (top - bottom)/2;
        
        ZoneMeasurement measurement = new ZoneMeasurement(renderer.getZone().getFeetPerCell(), roundDistance);
        GraphicsUtil.drawBoxedString(g, measurement.formatDistanceBetween(cellStart, cellEnd), centerX, centerY);
	}
	

}
