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
package net.rptools.maptool.client.ui.statsheet;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.SwingUtilities;

import net.rptools.lib.image.ImageUtil;
import net.rptools.lib.swing.SwingUtil;

public class StatSheet {

	private static final Font PROP_KEY_FONT = new Font("Helvetica", Font.BOLD, 12);
	private static final Font PROP_VALUE_FONT = new Font("Helvetica", 0, 12);
	private static final Color PROP_KEY_COLOR = Color.black;
	private static final Color PROP_VALUE_COLOR = Color.red;

	private BufferedImage backgroundImage;
	private Rectangle bounds;
	private Font attributeFont;
	private Color attributeColor;
	private Font valueFont;
	private Color valueColor;
	
	public StatSheet(BufferedImage backgroundImage, Rectangle bounds, Font attributeFont, Color attributeColor, Font valueFont, Color valueColor) {
		this.bounds = bounds;
		this.backgroundImage = backgroundImage;

		this.attributeFont = attributeFont != null ? attributeFont : PROP_KEY_FONT;
		this.attributeColor = attributeColor != null ? attributeColor : PROP_KEY_COLOR;
		
		this.valueFont = valueFont != null ? valueFont : PROP_VALUE_FONT;
		this.valueColor = valueColor != null ? valueColor : PROP_VALUE_COLOR;
		
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
		g.setColor(Color.black);
		
		int cols = 2;
//		int cols = (int)Math.ceil(Math.sqrt(propertyMap.size()));
		int rows = (int)Math.ceil(propertyMap.size() / (double)cols);
		
		int rowHeight = bounds.height / rows;
		int colWidth = bounds.width / cols;
		
		int row = 0;
		int col = 0;
		for (Entry<String, String> entry : propertyMap.entrySet()) {

			FontMetrics fm = g.getFontMetrics();

			int x = bounds.x + col*colWidth;
			int y = bounds.y+fm.getAscent()+ row*rowHeight;
			
			// Key
			g.setFont(attributeFont);
			g.setColor(attributeColor);
			g.drawString(entry.getKey(), x, y);

			int strWidth = SwingUtilities.computeStringWidth(fm, entry.getKey());
			x += strWidth + 2;

			g.drawString(":", x, y);
			
			x += SwingUtilities.computeStringWidth(fm, ":") + 7;
			
			// Value
			g.setFont(valueFont);
			g.setColor(valueColor);
			fm = g.getFontMetrics();
			g.drawString(entry.getValue(), x, y);
			
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
