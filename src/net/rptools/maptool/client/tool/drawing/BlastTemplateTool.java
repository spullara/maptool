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
        return radius;
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
