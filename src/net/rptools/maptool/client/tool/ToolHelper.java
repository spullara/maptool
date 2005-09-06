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

import net.rptools.maptool.client.AbstractPoint;
import net.rptools.maptool.client.ScreenPoint;
import net.rptools.maptool.client.ui.ZoneRenderer;
import net.rptools.maptool.util.GraphicsUtil;

/**
 * @author trevor
 */
public class ToolHelper {

	public static void drawBoxedMeasurement(ZoneRenderer renderer,
			Graphics2D g, ScreenPoint startPoint, ScreenPoint endPoint) {

		// Calculations
		int left = Math.min(startPoint.x, endPoint.x);
		int top = Math.min(startPoint.y, endPoint.y);
		int right = Math.max(startPoint.x, endPoint.x);
		int bottom = Math.max(startPoint.y, endPoint.y);

		// HORIZONTAL Measure
		g.setColor(Color.black);
		g.drawLine(left, top - 15, right, top - 15);
		g.drawLine(left, top - 20, left, top - 10);
		g.drawLine(right, top - 20, right, top - 10);

		String displayString = String.format("%1.1f", euclideanDistance(
				renderer, new ScreenPoint(left, top), new ScreenPoint(right, top)));
		GraphicsUtil.drawBoxedString(g, displayString, left + (right - left) / 2,
				top - 15);

		// VETICAL Measure
		g.drawLine(right + 15, top, right + 15, bottom);
		g.drawLine(right + 10, top, right + 20, top);
		g.drawLine(right + 10, bottom, right + 20, bottom);
		
		displayString = String.format("%1.1f", euclideanDistance(
				renderer, new ScreenPoint(right, top), new ScreenPoint(right, bottom)));
		GraphicsUtil.drawBoxedString(g, displayString, right + 18, bottom
				+ (top - bottom) / 2);
	}

	public static void drawMeasurement(ZoneRenderer renderer, Graphics2D g,
			ScreenPoint startPoint, ScreenPoint endPoint) {

		int left = Math.min(startPoint.x, endPoint.x);
		int top = Math.min(startPoint.y, endPoint.y);
		int right = Math.max(startPoint.x, endPoint.x);
		int bottom = Math.max(startPoint.y, endPoint.y);

		int centerX = left + (right - left) / 2;
		int centerY = bottom + (top - bottom) / 2;

		String displayString = String.format("%1.1f", euclideanDistance(
				renderer, startPoint, endPoint));

		GraphicsUtil.drawBoxedString(g, displayString, centerX, centerY);
	}

	private static double euclideanDistance(ZoneRenderer renderer, AbstractPoint p1,
			AbstractPoint p2) {
		int a = p2.x - p1.x;
		int b = p2.y - p1.y;

		return Math.sqrt(a * a + b * b) * renderer.getZone().getFeetPerCell()
				/ renderer.getScaledGridSize();
	}
}
