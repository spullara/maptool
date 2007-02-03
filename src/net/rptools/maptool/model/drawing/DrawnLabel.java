/* The MIT License
 * 
 * Copyright (c) 2005 David Rice, Trevor Croft, Jay Gorrell
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

package net.rptools.maptool.model.drawing;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.CellRendererPane;

import net.rptools.maptool.client.swing.TwoToneTextPane;
import net.rptools.maptool.client.tool.drawing.DrawnTextTool;
import net.rptools.maptool.model.GUID;

/**
 * @author jgorrell
 * @version $Revision$ $Date$ $Author$
 */
public class DrawnLabel extends AbstractDrawing {

	/**
	 * The bounds of the display rectangle
	 */
	private Rectangle bounds = new Rectangle();

	/**
	 * Text being painted.
	 */
	private String text;

	/**
	 * The font used to paint the text.
	 */
	private String font;

	/**
	 * The pane used to render the text
	 */
	private transient CellRendererPane renderer;

	/**
	 * The text pane used to paint the text.
	 */
	private transient TwoToneTextPane textPane;

	/**
	 * Create a new drawn label.
	 * 
	 * @param theText Text to be drawn
	 * @param theBounds The bounds containing the text.
	 * @param aFont The font used to draw the text as a string that can
	 * be passed to {@link Font#decode(java.lang.String)}.
	 */
	public DrawnLabel(String theText, Rectangle theBounds, String aFont) {
		text = theText;
		bounds = theBounds;
		font = aFont;
	}

	/**
	 * @see net.rptools.maptool.model.drawing.Drawable#draw(java.awt.Graphics2D, net.rptools.maptool.model.drawing.Pen)
	 */
	public void draw(Graphics2D aG) {
		if (renderer == null) {
			renderer = new CellRendererPane();
			textPane = DrawnTextTool.createTextPane(bounds, null, font);
			textPane.setText(text);
		}
		renderer.paintComponent(aG, textPane, null, bounds);
	}

	@Override
	protected void drawBackground(Graphics2D g) {
	}
	
	/**
	 * @see net.rptools.maptool.model.drawing.Drawable#getBounds()
	 */
	public Rectangle getBounds() {
		return bounds;
	}
}
