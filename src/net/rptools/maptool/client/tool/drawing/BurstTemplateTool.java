/* The MIT License
 * 
 * Copyright (c) 2008 Jay Gorrell
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
package net.rptools.maptool.client.tool.drawing;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import net.rptools.maptool.client.ScreenPoint;
import net.rptools.maptool.model.CellPoint;
import net.rptools.maptool.model.ZonePoint;
import net.rptools.maptool.model.drawing.AbstractTemplate;
import net.rptools.maptool.model.drawing.BurstTemplate;

/**
 * Draw a template for an effect with a burst. Make the template show the
 * squares that are effected, not just draw a circle. Let the player choose the
 * base hex with the mouse and then click again to set the radius. The control key 
 * can be used to move the base hex.
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
            setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream(
                "net/rptools/maptool/client/image/tool/temp-blue-burst.png"))));
        } catch (IOException ioe) {
            ioe.printStackTrace();
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
     * @see net.rptools.maptool.client.tool.drawing.RadiusTemplateTool#getCellAtMouse(java.awt.event.MouseEvent)
     */
    protected ZonePoint getCellAtMouse(MouseEvent e) {
        ZonePoint mouse = new ScreenPoint(e.getX(), e.getY()).convertToZone(renderer);
        CellPoint cp = renderer.getZone().getGrid().convert(mouse);
        return renderer.getZone().getGrid().convert(cp);
    }

    /**
     * @see net.rptools.maptool.client.tool.drawing.RadiusTemplateTool#paintCursor(java.awt.Graphics2D, java.awt.Paint, float, net.rptools.maptool.model.ZonePoint)
     */
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

