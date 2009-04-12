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

package net.rptools.maptool.model.drawing;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Area;

import javax.swing.CellRendererPane;

import net.rptools.maptool.client.swing.TwoToneTextPane;
import net.rptools.maptool.client.tool.drawing.DrawnTextTool;

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
	
	public Area getArea() {
		// TODO Auto-generated method stub
		return null;
	}
}
