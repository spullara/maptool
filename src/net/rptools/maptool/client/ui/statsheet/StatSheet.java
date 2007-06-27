package net.rptools.maptool.client.ui.statsheet;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.SwingUtilities;

import net.rptools.lib.image.ImageUtil;
import net.rptools.lib.swing.SwingUtil;

public class StatSheet {

	private static final Font FONT_PROPERTY_KEY = new Font("Helvetica", Font.BOLD, 12);
	private static final Font FONT_PROPERTY_VALUE = new Font("Helvetica", 0, 12);

	private BufferedImage backgroundImage;
	private Rectangle bounds;

	public StatSheet(String background, Rectangle bounds) throws IOException {
		this.bounds = bounds;
		backgroundImage = ImageUtil.getCompatibleImage(background);
	}
	
	public StatSheet(BufferedImage backgroundImage, Rectangle bounds) {
		this.bounds = bounds;
		this.backgroundImage = backgroundImage;
	}

	public int getWidth() {
		return backgroundImage.getWidth();
	}
	
	public int getHeight() {
		return backgroundImage.getHeight();
	}
	
	/**
	 * Renders the card at 0, 0 (this means the caller must position the graphics position before calling) 
	 * @param propertyMap What to show, presumably a LinkedHashMap to preserve order
	 */
	public void render(Graphics2D g, Map<String, String> propertyMap) {
		Font oldFont = g.getFont();
		Object oldAA = SwingUtil.useAntiAliasing(g);
		
		g.drawImage(backgroundImage, 0, 0, null);
		
		int cols = (int)Math.ceil(Math.sqrt(propertyMap.size()));
		int rows = (int)Math.ceil(propertyMap.size() / (double)cols);
		
		int rowHeight = bounds.height / rows;
		int colWidth = bounds.width / cols;
		
		int row = 0;
		int col = 0;
		for (Entry<String, String> entry : propertyMap.entrySet()) {
			
			// Key
			g.setFont(FONT_PROPERTY_KEY);
			FontMetrics fm = g.getFontMetrics();
			g.drawString(entry.getKey(), bounds.x + col*colWidth, bounds.y+fm.getAscent()+ row*rowHeight);
			
			// Value
			g.setFont(FONT_PROPERTY_VALUE);
			fm = g.getFontMetrics();
			int strWidth = SwingUtilities.computeStringWidth(fm, entry.getValue());
			g.drawString(entry.getValue(), bounds.x+ col*colWidth + (colWidth-strWidth)/2, bounds.y+fm.getHeight() + rowHeight*row + (rowHeight-fm.getHeight()*2)/2 + fm.getAscent());
			
			col++;
			if (col == cols) {
				col = 0;
				row++;
			}
		}
		
		g.setFont(oldFont);
		SwingUtil.restoreAntiAliasing(g, oldAA);
	}
}
