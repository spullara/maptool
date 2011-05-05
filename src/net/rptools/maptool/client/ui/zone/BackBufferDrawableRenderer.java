/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
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
