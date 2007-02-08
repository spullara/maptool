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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.rptools.maptool.model.drawing.Drawable;
import net.rptools.maptool.model.drawing.DrawnElement;
import net.rptools.maptool.model.drawing.Pen;

/**
 */
public class PartitionedDrawableRenderer implements DrawableRenderer {

	private static final BufferedImage NO_IMAGE = new BufferedImage(1, 1, Transparency.OPAQUE);
	private static final int CHUNK_SIZE = 1024;
	
	private Map<String, BufferedImage> chunkMap = new HashMap<String, BufferedImage>();
	private static List<BufferedImage> chunkPool = new LinkedList<BufferedImage>();
	
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

		if (drawableList.size() != lastDrawableCount || lastScale != scale) {
			for (BufferedImage image : chunkMap.values()) {
				clearImage(image);
				chunkPool.add(image);
			}
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
//					System.out.println("Creating: " + key);
					chunk = createChunk(drawableList, cellX, cellY, scale);
					chunkMap.put(key, chunk);
				}
				if (chunk != null && chunk != NO_IMAGE) {
//					System.out.println("Drawing: " + key);
					g.drawImage(chunk, x, y, null);
				}
				chunkCache.remove(key);
//				
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
			BufferedImage image = chunkMap.remove(key);
			if (image != NO_IMAGE) {
				clearImage(image);
				chunkPool.add(image);
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

		BufferedImage image = getNewChunk();
		Graphics2D g = image.createGraphics();
		Composite oldComposite = g.getComposite();
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		AffineTransform af = new AffineTransform();
		af.translate(-x, -y);
		af.scale(scale, scale);
		g.setTransform(af);

		for (DrawnElement element : drawableList) {
			
			Drawable drawable = element.getDrawable();

			Pen pen = element.getPen();
			if (pen.getOpacity() != 1 && pen.getOpacity() != 0 /* handle legacy pens, besides, it doesn't make sense to have a non visible pen*/) {
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, pen.getOpacity()));
			}
			drawable.draw(g, pen);
			g.setComposite(oldComposite);
		}
		
		g.dispose();
		
//		if (isEmpty(image)) {
//			chunkPool.add(image);
//			image = NO_IMAGE;
//		}
		
		return image;
	}
	
	private BufferedImage getNewChunk() {
		if (chunkPool.size() > 0) {
			System.out.println("Using pooled: " + chunkPool.size());
			return chunkPool.remove(0);
		}
		
		System.out.println("Creating new");
		return new BufferedImage(CHUNK_SIZE, CHUNK_SIZE, Transparency.TRANSLUCENT);
	}
	
	private String getKey(int col, int row) {
		return col + "." + row;
	}
	
	private boolean isEmpty(BufferedImage image) {
		
		for (int row = 0; row < image.getHeight(); row++) {
			for (int col = 0; col < image.getWidth(); col++) {
				if (image.getRGB(col, row) > 0) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	private void clearImage(BufferedImage backBuffer) {
        Graphics2D g2d = backBuffer.createGraphics();
        g2d.setBackground(new Color(0, 0, 0, 0)	);
		g2d.clearRect(0, 0, backBuffer.getWidth(), backBuffer.getHeight());
		g2d.dispose();
	}
	
}
