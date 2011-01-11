/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package net.rptools.maptool.client.tool.drawing;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ScreenPoint;
import net.rptools.maptool.model.CellPoint;
import net.rptools.maptool.model.ZonePoint;
import net.rptools.maptool.model.drawing.AbstractTemplate;
import net.rptools.maptool.model.drawing.BurstTemplate;

/**
 * Draw a template for an effect with a burst. Make the template show the squares that are effected, not just draw a
 * circle. Let the player choose the base hex with the mouse and then click again to set the radius. The control key can
 * be used to move the base hex.
 * 
 * @author jgorrell
 * @version $Revision: $ $Date: $ $Author: $
 */
public class BurstTemplateTool extends RadiusTemplateTool {
	/*---------------------------------------------------------------------------------------------
	 * Instance Variables
	 *-------------------------------------------------------------------------------------------*/

	/*---------------------------------------------------------------------------------------------
	 * Constructors
	 *-------------------------------------------------------------------------------------------*/

	/**
	 * Set the icon for the base tool.
	 */
	public BurstTemplateTool() {
		try {
			setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("net/rptools/maptool/client/image/tool/temp-blue-burst.png"))));
		} catch (IOException ioe) {
			MapTool.showError("Cannot read image 'temp-blue-burst.png'", ioe);
		} // endtry
	}

	/*---------------------------------------------------------------------------------------------
	 * Overridden RadiusTemplateTool methods
	 *-------------------------------------------------------------------------------------------*/

	/**
	 * @see net.rptools.maptool.client.tool.drawing.RadiusTemplateTool#createBaseTemplate()
	 */
	@Override
	protected AbstractTemplate createBaseTemplate() {
		return new BurstTemplate();
	}

	/**
	 * This seems to be redundant and doesn't account for moving the mouse pointer to the nearest vertex, only
	 * truncating to the nearest top/left vertex.
	 * 
	 * @see net.rptools.maptool.client.tool.drawing.RadiusTemplateTool#getCellAtMouse(java.awt.event.MouseEvent)
	 */
	@Override
	protected ZonePoint getCellAtMouse(MouseEvent e) {
		ZonePoint mouse = new ScreenPoint(e.getX(), e.getY()).convertToZone(renderer);
		CellPoint cp = renderer.getZone().getGrid().convert(mouse);
		return renderer.getZone().getGrid().convert(cp);
	}

	/**
	 * @see net.rptools.maptool.client.tool.drawing.RadiusTemplateTool#paintCursor(java.awt.Graphics2D, java.awt.Paint,
	 *      float, net.rptools.maptool.model.ZonePoint)
	 */
	@Override
	protected void paintCursor(Graphics2D g, Paint paint, float thickness, ZonePoint vertex) {
		g.setPaint(paint);
		g.setStroke(new BasicStroke(thickness));
		int grid = renderer.getZone().getGrid().getSize();
		g.drawRect(vertex.x, vertex.y, grid, grid);
	}

	/**
	 * @see net.rptools.maptool.client.tool.drawing.RadiusTemplateTool#getRadiusAtMouse(java.awt.event.MouseEvent)
	 */
	@Override
	protected int getRadiusAtMouse(MouseEvent e) {
		return super.getRadiusAtMouse(e);
	}

	/**
	 * @see net.rptools.maptool.client.ui.Tool#getTooltip()
	 */
	@Override
	public String getTooltip() {
		return "tool.bursttemplate.tooltip";
	}

	/**
	 * @see net.rptools.maptool.client.ui.Tool#getInstructions()
	 */
	@Override
	public String getInstructions() {
		return "tool.bursttemplate.instructions";
	}
}
