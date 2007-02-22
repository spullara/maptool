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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.drawing.Drawable;
import net.rptools.maptool.model.drawing.DrawnElement;
import net.rptools.maptool.model.drawing.Pen;

/**
 */
public class ASyncPartitionedDrawableRenderer implements DrawableRenderer {

	private static final BufferedImage NO_IMAGE = new BufferedImage(1, 1, Transparency.OPAQUE);
	private static final int CHUNK_SIZE = 256;
	
	private Map<String, BufferedImage> chunkMap = new HashMap<String, BufferedImage>();
	
	private static BlockingQueue<QueuedRenderer> renderQueue = new LinkedBlockingQueue<QueuedRenderer>();
	
	private double lastDrawableCount;
	private double lastScale;
	private Rectangle lastViewport;
	 
	private int horizontalChunkCount;
	private int verticalChunkCount;
	
	static {
		new RenderThread().start();
	}
	
	public void flush() {
		for (BufferedImage image : chunkMap.values()) {
			releaseChunk(image);
		}
		chunkMap.clear();
		renderQueue.clear();
	}
	
	public void renderDrawables(Graphics g, List<DrawnElement> drawableList, Rectangle viewport, double scale) {

		// NOTHING TO DO
		if (drawableList == null || drawableList.size() == 0) {
			flush();
			return;
		}

		if (drawableList.size() != lastDrawableCount || lastScale != scale) {
			flush();
		}
		
		if (lastViewport == null || viewport.width != lastViewport.width || viewport.height != lastViewport.height) {
			horizontalChunkCount = (int)Math.ceil(viewport.width/(double)CHUNK_SIZE) + 1;
			verticalChunkCount = (int)Math.ceil(viewport.height/(double)CHUNK_SIZE) + 1;
		}

		int gridx = (int)Math.floor(-viewport.x / (double)CHUNK_SIZE);
		int gridy = (int)Math.floor(-viewport.y / (double)CHUNK_SIZE);

		Set<String> chunkCache = new HashSet<String>();
		chunkCache.addAll(chunkMap.keySet());
		for (int row = 0; row < verticalChunkCount; row++) {
			
			for (int col = 0; col < horizontalChunkCount; col++) {

				int x = col * CHUNK_SIZE - ((CHUNK_SIZE - viewport.x))%CHUNK_SIZE - (gridx < -1 ? CHUNK_SIZE : 0);
				int y = row * CHUNK_SIZE - ((CHUNK_SIZE - viewport.y))%CHUNK_SIZE - (gridy < -1 ? CHUNK_SIZE : 0);
				
				int cellX = gridx + col;
				int cellY = gridy + row;

				String key = getKey(cellX, cellY);
				BufferedImage chunk = chunkMap.get(key);
				if (chunk == null) {
					createChunk(drawableList, cellX, cellY, scale, viewport);
					
					chunk = NO_IMAGE;
					chunkMap.put(key, chunk);
				}
				if (chunk != null && chunk != NO_IMAGE) {
//					System.out.println("Drawing: " + key);
					g.drawImage(chunk, x, y, null);
				}
				chunkCache.remove(key);
				
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
		for (String key : chunkCache) {
//			System.out.println("Removing: " + key);
			releaseChunk(chunkMap.remove(key));
		}
		
		// REMEMBER
		lastViewport = viewport;
		lastDrawableCount = drawableList.size();
		lastScale = scale;
	}

	private void createChunk(List<DrawnElement> drawableList, int gridx, int gridy, double scale, Rectangle view) {
		renderQueue.add(new QueuedRenderer(drawableList, gridx, gridy, scale, view));
	}
	
	private static BufferedImage renderChunk(QueuedRenderer renderer) {

		int gridx = renderer.cellX;
		int gridy = renderer.cellY;
		List<DrawnElement> drawableList = renderer.drawableList;
		double scale = renderer.scale;
		
		int x = gridx * CHUNK_SIZE;
		int y = gridy * CHUNK_SIZE;

		BufferedImage image = null;
		Composite oldComposite = null;
		Graphics2D g = null;

		for (DrawnElement element : drawableList) {
			
			Drawable drawable = element.getDrawable();
			
			Rectangle2D drawnBounds = drawable.getBounds();
			Rectangle2D chunkBounds = new Rectangle((int)(gridx * (CHUNK_SIZE/scale)), (int)(gridy * (CHUNK_SIZE/scale)), (int)(CHUNK_SIZE/scale), (int)(CHUNK_SIZE/scale));
//			if (gridx == 0 && gridy == 1) {
//				System.out.println(drawnBounds.intersects(chunkBounds));
//				System.out.println(drawnBounds + " - " + chunkBounds);
//			}
			
			// TODO: handle pen size
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


			Pen pen = element.getPen();
			if (pen.getOpacity() != 1 && pen.getOpacity() != 0 /* handle legacy pens, besides, it doesn't make sense to have a non visible pen*/) {
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, pen.getOpacity()));
			}
//			if (gridx == 0 && gridy == 1) {
//				System.out.println("draw");
//			}
			drawable.draw(g, pen);
			g.setComposite(oldComposite);
		}
		
		if (g != null) {
			g.dispose();
		}
//		if (image != null && isEmpty(image)) {
//			releaseChunk(image);
//			image = null;
//		}
		if (image == null) {
			image = NO_IMAGE;
		}
		
		return image;
	}
	
	private static void releaseChunk(BufferedImage image) {
//		if (image == null || image == NO_IMAGE || chunkPool.size() >= maxChunkPoolSize) {
//			return;
//		}
//		clearImage(image);
//		chunkPool.add(image);
	}
	
	private static BufferedImage getNewChunk() {
		return new BufferedImage(CHUNK_SIZE, CHUNK_SIZE, Transparency.BITMASK);
	}
	
	private String getKey(int col, int row) {
		return col + "." + row;
	}
	
	private class QueuedRenderer {
		List<DrawnElement> drawableList;
		int cellX, cellY; 
		double scale;
		Rectangle view;
		
		public QueuedRenderer(List<DrawnElement> drawableList, int cellX, int cellY, double scale, Rectangle view) {
			this.drawableList = drawableList;
			this.cellX = cellX;
			this.cellY = cellY;
			this.scale = scale;
			this.view = view;
		}
		
		public boolean isValid() {
			return view.equals(lastViewport) && scale == lastScale;
		}
		
		public void render() {
			BufferedImage chunk = renderChunk(this);
			chunkMap.put(getKey(cellX, cellY), chunk);
		}
	}
	
	private static class RenderThread extends Thread {

		@Override
		public void run() {
			while (true) {
				try {
					QueuedRenderer renderer = renderQueue.take();
					if (!renderer.isValid()) {
						continue;
					}
					
					renderer.render();
					
					MapTool.getFrame().refresh();
				} catch (InterruptedException ie) {
					ie.printStackTrace();
					// Continue working
				}
			}
		}
	}
}