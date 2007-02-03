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
package net.rptools.maptool.client.ui.zone;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.drawing.Drawable;
import net.rptools.maptool.model.drawing.DrawnElement;
import net.rptools.maptool.model.drawing.Pen;

/**
 */
public class DrawableRenderer {

	private Map<Zone.Layer, BufferedImage> backBufferMap = new HashMap<Zone.Layer, BufferedImage>();
	private Map<Zone.Layer, Rectangle> lastViewportMap = new HashMap<Zone.Layer, Rectangle>();
	private Map<Zone.Layer, Double> lastScaleMap = new HashMap<Zone.Layer, Double>();
	private Map<Zone.Layer, Integer> lastDrawableListSizeMap = new HashMap<Zone.Layer, Integer>();
	
	public void flush() {
		backBufferMap.clear();
		lastViewportMap.clear();
		lastDrawableListSizeMap.clear();
	}
	
	private void flush(Zone.Layer layer) {
		backBufferMap.remove(layer);
		lastViewportMap.remove(layer);
		lastDrawableListSizeMap.remove(layer);
	}
	
	public void renderDrawables(Graphics g, Zone zone, Zone.Layer layer, Rectangle viewport, double scale) {

		List<DrawnElement> drawableList = zone.getDrawnElements(layer);
		
		// NOTHING TO DO
		if (drawableList == null || drawableList.size() == 0) {
			flush(layer);
			return;
		}

		Rectangle lastViewport = lastViewportMap.get(layer);
		Double lastScale = lastScaleMap.get(layer);
		
		boolean viewChanged = lastViewport == null || (lastViewport.width != viewport.width || lastViewport.height != viewport.height);
		if (viewChanged) {
			backBufferMap.clear();
		}
		
		boolean newBackbuffer = false;
		BufferedImage backBuffer = backBufferMap.get(layer);

		// CREATE BACKBUFFER
		if (backBuffer == null || viewChanged) {
			backBuffer = new BufferedImage(viewport.width, viewport.height, Transparency.TRANSLUCENT);
			backBufferMap.put(layer, backBuffer);
			
			newBackbuffer = true;
		}
		
		// SCENERY CHANGE
		Integer lastDrawableListSizeInt = lastDrawableListSizeMap.get(layer);
		int lastDrawableListSize = lastDrawableListSizeInt != null ? lastDrawableListSizeInt : -1;
		if (newBackbuffer || lastDrawableListSize != drawableList.size() || lastViewport.x != viewport.x || lastViewport.y != viewport.y || lastScale != scale) {
			if (!newBackbuffer) {
				clearBackbuffer(backBuffer);
			}
			
			drawDrawables(backBuffer, drawableList, viewport, scale);
		}
		
		// RENDER
		g.drawImage(backBuffer, 0, 0, null);
		
		// REMEMBER
		lastViewportMap.put(layer, viewport);
		lastScaleMap.put(layer, scale);
		lastDrawableListSizeMap.put(layer, drawableList.size());
	}

	private void clearBackbuffer(BufferedImage backBuffer) {
        Graphics2D g2d = backBuffer.createGraphics();
        g2d.setBackground(new Color(0, 0, 0, 0)	);
		g2d.clearRect(0, 0, backBuffer.getWidth(), backBuffer.getHeight());
		g2d.dispose();
	}
	
	private void drawDrawables(BufferedImage backBuffer, List<DrawnElement> drawableList, Rectangle viewport, double scale) {
		
		List<DrawnElement> drawableListCopy = new ArrayList<DrawnElement>(drawableList.size());
		drawableListCopy.addAll(drawableList);
		
		Graphics2D g = backBuffer.createGraphics();
		g.setClip(0, 0, backBuffer.getWidth(), backBuffer.getHeight());
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		AffineTransform af = new AffineTransform();
		af.scale(scale, scale);
		af.translate(viewport.x/scale, viewport.y/scale);
		g.setTransform(af);

		Composite oldComposite = g.getComposite();
		for (DrawnElement element : drawableListCopy) {
			
			Drawable drawable = element.getDrawable();
//			if (!drawable.getBounds().intersects(viewport)) {
//				// Not onscreen
//				continue;
//			}
			
			Pen pen = element.getPen();
			if (pen.getOpacity() != 1 && pen.getOpacity() != 0 /* handle legacy pens, besides, it doesn't make sense to have a non visible pen*/) {
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, pen.getOpacity()));
			}
			drawable.draw(g, pen);
			g.setComposite(oldComposite);
		}
		
		g.dispose();
		
	}
}
