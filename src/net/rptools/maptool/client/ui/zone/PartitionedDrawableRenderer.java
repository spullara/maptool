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
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import net.rptools.maptool.model.drawing.Drawable;
import net.rptools.maptool.model.drawing.DrawnElement;
import net.rptools.maptool.model.drawing.LineSegment;
import net.rptools.maptool.model.drawing.Pen;

/**
 */
public class PartitionedDrawableRenderer implements DrawableRenderer {

	private static final BufferedImage NO_IMAGE = new BufferedImage(1, 1, Transparency.OPAQUE);
	private static final int CHUNK_SIZE = 256;
	private static final int MAX_EXPENSIVE_CHUNK_COUNT = (int)((10*1024*1024.0) / (CHUNK_SIZE * CHUNK_SIZE * 8)); // 10 megs 
	
	private Map<String, LRUEntry<BufferedImage>> chunkMap = new HashMap<String, LRUEntry<BufferedImage>>();
	
	private double lastDrawableCount;
	private double lastScale;
	private Rectangle lastViewport;
	 
	private int horizontalChunkCount;
	private int verticalChunkCount;
	
	public void flush() {
		chunkMap.clear();
	}
	
	public void renderDrawables(Graphics g, List<DrawnElement> drawableList, Rectangle viewport, double scale) {

		// NOTHING TO DO
		if (drawableList == null || drawableList.size() == 0) {
			flush();
			return;
		}

		// View changed ?
		if (drawableList.size() != lastDrawableCount || lastScale != scale) {
			flush();
		}
		
		if (lastViewport == null || viewport.width != lastViewport.width || viewport.height != lastViewport.height) {
			horizontalChunkCount = (int)Math.ceil(viewport.width/(double)CHUNK_SIZE) + 1;
			verticalChunkCount = (int)Math.ceil(viewport.height/(double)CHUNK_SIZE) + 1;
		}

		// Compute grid
		int gridx = (int)Math.floor(-viewport.x / (double)CHUNK_SIZE);
		int gridy = (int)Math.floor(-viewport.y / (double)CHUNK_SIZE); 

		// OK, weirdest hack ever.  Basically, when the viewport.x is exactly divisible by the chunk size, the gridx decrements
		// too early, creating a visual jump in the drawables.  I don't know the exact cause, but this seems to account for it
		// note that it only happens in the negative space.  Weird.
		gridx += (viewport.x > CHUNK_SIZE && (viewport.x%CHUNK_SIZE == 0) ? -1 : 0);
		gridy += (viewport.y > CHUNK_SIZE && (viewport.y%CHUNK_SIZE == 0) ? -1 : 0);
		
		Set<String> chunkCache = new HashSet<String>();
		chunkCache.addAll(chunkMap.keySet());
		for (int row = 0; row < verticalChunkCount; row++) {
			
			for (int col = 0; col < horizontalChunkCount; col++) {

				int cellX = gridx + col;
				int cellY = gridy + row;

				String key = getKey(cellX, cellY);
				LRUEntry<BufferedImage> chunk = chunkMap.get(key);
				if (chunk == null) {
					long start = System.currentTimeMillis();
					chunk = new LRUEntry<BufferedImage>(createChunk(drawableList, cellX, cellY, scale));
					long duration = System.currentTimeMillis() - start;

					if (chunkMap.size() >= MAX_EXPENSIVE_CHUNK_COUNT) {
						String oldestKey = null;
						LRUEntry<BufferedImage> oldest = null;
						for (Entry<String, LRUEntry<BufferedImage>> entry : chunkMap.entrySet()) {
							if (entry.getValue().getObject() != NO_IMAGE) {
								// Never take out a no-image section
								continue;
							}
							
							if (oldest == null || entry.getValue().getLastAccess() < oldest.getLastAccess()) {
								oldest = entry.getValue();
								oldestKey = entry.getKey();
							}
						}
						if (oldestKey != null) {
							chunkMap.remove(key);
						}
					}
					chunkMap.put(key, chunk);
				}

				if (chunk != null && chunk.getObject() != NO_IMAGE) {
					int x = col * CHUNK_SIZE - ((CHUNK_SIZE - viewport.x))%CHUNK_SIZE - (gridx < -1 ? CHUNK_SIZE : 0);
					int y = row * CHUNK_SIZE - ((CHUNK_SIZE - viewport.y))%CHUNK_SIZE - (gridy < -1 ? CHUNK_SIZE : 0);
					g.drawImage(chunk.getObject(), x, y, null);
				}
				chunkCache.remove(key);

				// Partition boundaries
//				if (col%2 == 0) {
//					if (row%2 == 0) {
//						g.setColor(Color.white);
//					} else {
//						g.setColor(Color.green);
//					}
//				} else {
//					if (row%2 == 0) {
//						g.setColor(Color.green);
//					} else {
//						g.setColor(Color.white);
//					}
//				}
//				g.drawRect(x, y, CHUNK_SIZE-1, CHUNK_SIZE-1);
//				g.drawString(key, x + CHUNK_SIZE/2, y + CHUNK_SIZE/2);
			}
		}
		
		// REMEMBER
		lastViewport = viewport;
		lastDrawableCount = drawableList.size();
		lastScale = scale;
	}

	private BufferedImage createChunk(List<DrawnElement> drawableList, int gridx, int gridy, double scale) {

		int x = gridx * CHUNK_SIZE;
		int y = gridy * CHUNK_SIZE;

		BufferedImage image = null;
		Composite oldComposite = null;
		Graphics2D g = null;

		int count = 0;
		for (DrawnElement element : drawableList) {
			
			Drawable drawable = element.getDrawable();
			
			Rectangle2D drawnBounds = new Rectangle(drawable.getBounds());
			Rectangle2D chunkBounds = new Rectangle((int)(gridx * (CHUNK_SIZE/scale)), (int)(gridy * (CHUNK_SIZE/scale)), (int)(CHUNK_SIZE/scale), (int)(CHUNK_SIZE/scale));
			
			// Handle pen size
			Pen pen = element.getPen();
			int penSize = (int)(pen.getThickness()/2 + 1);
			drawnBounds.setRect(drawnBounds.getX() - penSize, drawnBounds.getY() - penSize, drawnBounds.getWidth() + pen.getThickness(), drawnBounds.getHeight() + pen.getThickness());
			
			if (!drawnBounds.intersects(chunkBounds)) {
				continue;
			}
			
			if (drawable instanceof LineSegment) {
				count ++;
			}

			if (image == null) {
				image = getNewChunk();
				g = image.createGraphics();
				g.setClip(0, 0, CHUNK_SIZE, CHUNK_SIZE);
				oldComposite = g.getComposite();
				
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				AffineTransform af = new AffineTransform();
				af.translate(-x, -y);
				af.scale(scale, scale);
				g.setTransform(af);
			}


			if (pen.getOpacity() != 1 && pen.getOpacity() != 0 /* handle legacy pens, besides, it doesn't make sense to have a non visible pen*/) {
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, pen.getOpacity()));
			}

			drawable.draw(g, pen);
			if (drawable instanceof LineSegment) {
				Pen newPen = new Pen(pen);
				newPen.setThickness(1);

				// Show where the line segments are
//				newPen.setPaint(new DrawableColorPaint(Color.blue));
//				drawable.draw(g, newPen);
			}
			g.setComposite(oldComposite);
		}
		if (count > 0) {
//			System.out.println("Line segments " + gridx + "." + gridy + ": " + count + " - " + System.currentTimeMillis());
		}
		
		if (g != null) {
			g.dispose();
		}

		if (image == null) {
			image = NO_IMAGE;
		}
		
		return image;
	}
	
	private BufferedImage getNewChunk() {
		BufferedImage image = new BufferedImage(CHUNK_SIZE, CHUNK_SIZE, Transparency.BITMASK);
		image.setAccelerationPriority(1);
		return image;
	}
	
	private String getKey(int col, int row) {
		return col + "." + row;
	}

	// TODO: Put this somewhere accessible to all, or use a preexisting version
	private static class LRUEntry<E> {
		
		private long lastAccess;
		private E object;
		
		public LRUEntry(E object) {
			lastAccess = System.currentTimeMillis();
			this.object = object;
		}
		
		public long getLastAccess() {
			return lastAccess;
		}
		
		public E getObject() {
			lastAccess = System.currentTimeMillis();
			return object;
		}
	}
}
