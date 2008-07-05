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

import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import net.rptools.maptool.client.ScreenPoint;
import net.rptools.maptool.model.ZonePoint;
import net.rptools.maptool.model.drawing.AbstractTemplate;
import net.rptools.maptool.model.drawing.BlastTemplate;
import net.rptools.maptool.model.drawing.RadiusTemplate;
import net.rptools.maptool.model.drawing.AbstractTemplate.Direction;

/**
 * Draws a square blast template next to a base cell.
 * 
 * @author Jay
 */
public class BlastTemplateTool extends BurstTemplateTool {

    /*---------------------------------------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------------------------------------*/

    /**
     * Set the icon for the base tool.
     */
    public BlastTemplateTool() {
        try {
            setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream(
                "net/rptools/maptool/client/image/tool/temp-blue-square.png"))));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } // endtry
    }
    
    /*---------------------------------------------------------------------------------------------
     * Overridden RadiusTemplateTool methods
     *-------------------------------------------------------------------------------------------*/

    /**
     * @see net.rptools.maptool.client.tool.drawing.BurstTemplateTool#createBaseTemplate()
     */
    @Override
    protected AbstractTemplate createBaseTemplate() {
        return new BlastTemplate();
    }

    /**
     * @see net.rptools.maptool.client.ui.Tool#getTooltip()
     */
    @Override
    public String getTooltip() {
      return "tool.blasttemplate.tooltip";
    }

    /**
     * @see net.rptools.maptool.client.ui.Tool#getInstructions()
     */
    @Override
    public String getInstructions() {
      return "tool.blasttemplate.instructions";
    }
    
    /**
     * @see net.rptools.maptool.client.tool.drawing.BurstTemplateTool#getRadiusAtMouse(java.awt.event.MouseEvent)
     */
    @Override
    protected int getRadiusAtMouse(MouseEvent e) {
        int radius = super.getRadiusAtMouse(e) + 1; 
        return radius + (radius % 2 == 0 ? + 1 : 0); // Force to be odd.
    }
    
    /**
     * @see net.rptools.maptool.client.tool.drawing.RadiusTemplateTool#setRadiusFromAnchor(java.awt.event.MouseEvent)
     */
    @Override
    protected void setRadiusFromAnchor(MouseEvent e) {
        super.setRadiusFromAnchor(e);
        
        // Also determine direction
        ZonePoint vertex = template.getVertex();
        ZonePoint mouse = new ScreenPoint(e.getX(), e.getY()).convertToZone(renderer);
        Direction dir = RadiusTemplate.Direction.findDirection(mouse.x, mouse.y, vertex.x, vertex.y);
//        if (template.getRadius() % 2 == 0 && dir.ordinal() % 2 == 1) {
//            dir = Direction.values()[dir == Direction.SOUTH_WEST ? 1 : dir.ordinal() + 1];
//        }
        ((BlastTemplate)template).setDirection(dir);
    }
}
