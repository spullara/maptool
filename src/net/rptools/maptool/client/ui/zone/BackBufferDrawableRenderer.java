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
import java.util.List;

import net.rptools.maptool.model.drawing.Drawable;
import net.rptools.maptool.model.drawing.DrawnElement;
import net.rptools.maptool.model.drawing.Pen;

/**
 */
public class BackBufferDrawableRenderer implements DrawableRenderer {

	private BufferedImage backBuffer;
	private Rectangle lastViewport;
	private double lastScale;
	private int lastDrawableListSize;
	
	public void flush() {
		backBuffer = null;
		lastViewport = null;
	}
	
	public void renderDrawables(Graphics g, List<DrawnElement> drawableList, Rectangle viewport, double scale) {

		// NOTHING TO DO
		if (drawableList == null || drawableList.size() == 0) {
			flush();
			return;
		}

		boolean newBackbuffer = false;
		
		// CREATE BACKBUFFER
		if (backBuffer == null || (lastViewport == null || (lastViewport.width != viewport.width || lastViewport.height != viewport.height))) {

			backBuffer = new BufferedImage(viewport.width, viewport.height, Transparency.TRANSLUCENT);
			newBackbuffer = true;
		}
		
		// SCENERY CHANGE
		if (newBackbuffer || lastDrawableListSize != drawableList.size() || lastViewport.x != viewport.x || lastViewport.y != viewport.y || lastScale != scale) {
			if (!newBackbuffer) {
				clearBackbuffer();
			}
			drawDrawables(drawableList, viewport, scale);
		}
		
		// RENDER
		g.drawImage(backBuffer, 0, 0, null);
		
		// REMEMBER
		lastViewport = viewport;
		lastScale = scale;
		lastDrawableListSize = drawableList.size();
	}

	private void clearBackbuffer() {
        Graphics2D g2d = backBuffer.createGraphics();
        g2d.setBackground(new Color(0, 0, 0, 0)	);
		g2d.clearRect(0, 0, backBuffer.getWidth(), backBuffer.getHeight());
		g2d.dispose();
	}
	
	private void drawDrawables(List<DrawnElement> drawableList, Rectangle viewport, double scale) {

		Graphics2D g = backBuffer.createGraphics();
		g.setClip(0, 0, backBuffer.getWidth(), backBuffer.getHeight());
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		AffineTransform af = new AffineTransform();
		af.scale(scale, scale);
		af.translate(viewport.x/scale, viewport.y/scale);
		g.setTransform(af);

		Composite oldComposite = g.getComposite();
		for (DrawnElement element : drawableList) {
			
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
