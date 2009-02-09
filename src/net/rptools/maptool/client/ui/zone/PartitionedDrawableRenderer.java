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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import net.rptools.maptool.model.drawing.Drawable;
import net.rptools.maptool.model.drawing.DrawnElement;
import net.rptools.maptool.model.drawing.LineSegment;
import net.rptools.maptool.model.drawing.LineTemplate;
import net.rptools.maptool.model.drawing.Pen;

/**
 */
public class PartitionedDrawableRenderer implements DrawableRenderer {

	private static final int CHUNK_SIZE = 256;

	private Set<String> noImageSet = new HashSet<String>();
	private List<Tuple> chunkList = new LinkedList<Tuple>();
	private int maxChunks;
	
	private double lastDrawableCount;
	private double lastScale;
	private Rectangle lastViewport;
	 
	private int horizontalChunkCount;
	private int verticalChunkCount;
	
	public void flush() {
		chunkList.clear();
		noImageSet.clear();
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

			maxChunks = (horizontalChunkCount * verticalChunkCount * 2);
		}

		// Compute grid
		int gridx = (int)Math.floor(-viewport.x / (double)CHUNK_SIZE);
		int gridy = (int)Math.floor(-viewport.y / (double)CHUNK_SIZE); 

		// OK, weirdest hack ever.  Basically, when the viewport.x is exactly divisible by the chunk size, the gridx decrements
		// too early, creating a visual jump in the drawables.  I don't know the exact cause, but this seems to account for it
		// note that it only happens in the negative space.  Weird.
		gridx += (viewport.x > CHUNK_SIZE && (viewport.x%CHUNK_SIZE == 0) ? -1 : 0);
		gridy += (viewport.y > CHUNK_SIZE && (viewport.y%CHUNK_SIZE == 0) ? -1 : 0);
		
		for (int row = 0; row < verticalChunkCount; row++) {
			
			for (int col = 0; col < horizontalChunkCount; col++) {

				int cellX = gridx + col;
				int cellY = gridy + row;

				String key = getKey(cellX, cellY);
				if (noImageSet.contains(key)) {
					continue;
				}
				
				Tuple chunk = findChunk(chunkList, key);
				if (chunk == null) {

					chunk = new Tuple(key, createChunk(drawableList, cellX, cellY, scale));
					
					if (chunk.image == null) {
						noImageSet.add(key);
						continue;
					}
				}
				
				// Most recently used is at the front
				chunkList.add(0, chunk);

				// Trim to the right size
				while (chunkList.size() > maxChunks) {
					Tuple removedTuple = chunkList.remove(chunkList.size()-1);
				}
				
				int x = col * CHUNK_SIZE - ((CHUNK_SIZE - viewport.x))%CHUNK_SIZE - (gridx < -1 ? CHUNK_SIZE : 0);
				int y = row * CHUNK_SIZE - ((CHUNK_SIZE - viewport.y))%CHUNK_SIZE - (gridy < -1 ? CHUNK_SIZE : 0);
				g.drawImage(chunk.image, x, y, null);
				
				// DEBUG: Partition boundaries
				if (false) { // Show partition boundaries
					if (col%2 == 0) {
						if (row%2 == 0) {
							g.setColor(Color.white);
						} else {
							g.setColor(Color.green);
						}
					} else {
						if (row%2 == 0) {
							g.setColor(Color.green);
						} else {
							g.setColor(Color.white);
						}
					}
					g.drawRect(x, y, CHUNK_SIZE-1, CHUNK_SIZE-1);
					g.drawString(key, x + CHUNK_SIZE/2, y + CHUNK_SIZE/2);
				}
			}
		}
		
		// REMEMBER
		lastViewport = viewport;
		lastDrawableCount = drawableList.size();
		lastScale = scale;
	}
	
	private Tuple findChunk(List<Tuple> list, String key) {

		ListIterator<Tuple> iter = list.listIterator();
		while (iter.hasNext()) {
			Tuple tuple = iter.next();
			if (tuple.equals(key)) {
				iter.remove();
				return tuple;
			}
		}
		
		return null;
	}

	private BufferedImage createChunk(List<DrawnElement> drawableList, int gridx, int gridy, double scale) {

		int x = gridx * CHUNK_SIZE;
		int y = gridy * CHUNK_SIZE;

		BufferedImage image = null;
		Composite oldComposite = null;
		Graphics2D g = null;

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

//			g.setColor(Color.red);
//			g.draw(drawnBounds);

			drawable.draw(g, pen);
			g.setComposite(oldComposite);
		}
		
		if (g != null) {
			g.dispose();
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
	
	private static class Tuple {
		String key;
		BufferedImage image;
		
		public Tuple(String key, BufferedImage image) {
			this.key = key;
			this.image = image;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof String) {
				return key.equals(obj.toString());
			}
			return super.equals(obj);
		}
	}
}
