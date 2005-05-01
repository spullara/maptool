/*
 */
package net.rptools.maptool.client;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import net.rptools.maptool.model.drawing.Drawable;
import net.rptools.maptool.model.drawing.DrawnElement;

/**
 */
public class DrawableRenderer {

	private int drawableCount = 0;
	private List<DrawableEntry> drawableEntries = new ArrayList<DrawableEntry>();
	
	private boolean repaint = true;

	public void renderDrawables(Graphics g, List<DrawnElement> drawableList, int offsetX, int offsetY, double scale) {

		if (drawableCount != drawableList.size()) {
			
			consolidateBounds(drawableList);
			validateEntries();
			
			for (DrawnElement element : drawableList) {
				
				Drawable drawable = element.getDrawable();
				
				DrawableEntry entry = getEntryFor(drawable.getBounds());
				
				Graphics g2 = entry.image.getGraphics();
				drawable.draw((Graphics2D) g2, element.getPen(), entry.bounds.x, entry.bounds.y);
				g2.dispose();
			}
		}
		drawableCount = drawableList.size();
		
		for (DrawableEntry entry : drawableEntries) {
			
			int x = (int)((entry.bounds.x * scale) + offsetX);
			int y = (int)((entry.bounds.y * scale) + offsetY);
			int width = (int)(entry.bounds.width * scale);
			int height = (int)(entry.bounds.height * scale);
			
			g.drawImage(entry.image, x, y, width, height, null);
		}
	}
	
	private DrawableEntry getEntryFor(Rectangle bounds) {
		
		for (int i = 0; i < drawableEntries.size(); i++) {
			
			DrawableEntry entry = drawableEntries.get(i);
			
			if (entry.bounds.contains(bounds)) {
				return entry;
			}
		}
		
		throw new IllegalStateException ("Could not find appropriate back buffer.");
	}

	private void validateEntries() {
		
		for (DrawableEntry entry : drawableEntries) {
			entry.validate();
		}
	}
	
	private synchronized void consolidateBounds(List<DrawnElement> drawableList) {

		// Make sure each drawable has a place to be drawn
		OUTTER:
		for (int i = 0; i < drawableList.size(); i++) {
			
			DrawnElement drawable = drawableList.get(i);
			Rectangle bounds = drawable.getDrawable().getBounds();
			
			for (int j = 0; j < drawableEntries.size(); j++) {

				DrawableEntry entry = drawableEntries.get(j);
				
				// If they are completely within an existing space, then we're done
				if (entry.bounds.contains(bounds)) {
					continue OUTTER;
				}
			}
			
			// Otherwise, add a new area
			drawableEntries.add(new DrawableEntry(bounds));
		}
	
		// Combine any areas that are now overlapping
		boolean changed = true;
		while (changed) {
			changed = false;
			
			for (int i = 0; i < drawableEntries.size(); i++) {
				
				DrawableEntry outterEntry = drawableEntries.get(i);
				
				// Combine with the rest of the list
				for (ListIterator<DrawableEntry> iter = drawableEntries.listIterator(i + 1); iter.hasNext();) {
					
					DrawableEntry innerEntry = iter.next();
					
					// OPTIMIZE: This could be optimized to delay image creation
					// until all bounds have been consolidated
					if (outterEntry.bounds.intersects(innerEntry.bounds)) {
						outterEntry = new DrawableEntry (outterEntry.bounds.union(innerEntry.bounds));
						iter.remove();
						
						changed = true;
					}
				}
				
				if (changed) {
					drawableEntries.set(i, outterEntry);
				}
			}
		}
	}
	
	private static class DrawableEntry {
		
		public Rectangle bounds;
		public BufferedImage image;
		
		public DrawableEntry (Rectangle bounds) {
			this.bounds = bounds;
		}
		
		void validate() {
			if (image == null) {
				image = new BufferedImage(bounds.width, bounds.height, Transparency.BITMASK);
			}
		}
	}
}
