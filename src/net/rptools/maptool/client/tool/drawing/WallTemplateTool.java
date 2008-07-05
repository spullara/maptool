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

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import net.rptools.maptool.client.ScreenPoint;
import net.rptools.maptool.model.CellPoint;
import net.rptools.maptool.model.ZonePoint;
import net.rptools.maptool.model.drawing.AbstractTemplate;
import net.rptools.maptool.model.drawing.LineTemplate;
import net.rptools.maptool.model.drawing.WallTemplate;

/**
 * A tool to draw a wall template for 4e D&D
 * 
 * @author Jay
 */
public class WallTemplateTool extends BurstTemplateTool {

    /*---------------------------------------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------------------------------------*/

    /**
     * Set the icon for the base tool.
     */
    public WallTemplateTool() {
        try {
            setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream(
                "net/rptools/maptool/client/image/tool/temp-blue-wall.png"))));
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
        return new WallTemplate();
    }

    /**
     * @see net.rptools.maptool.client.ui.Tool#getTooltip()
     */
    @Override
    public String getTooltip() {
      return "tool.walltemplate.tooltip";
    }

    /**
     * @see net.rptools.maptool.client.ui.Tool#getInstructions()
     */
    @Override
    public String getInstructions() {
      return "tool.walltemplate.instructions";
    }
    
    /**
     * @see net.rptools.maptool.client.tool.drawing.RadiusTemplateTool#mousePressed(java.awt.event.MouseEvent)
     */
    @Override
    public void mousePressed(MouseEvent e) {
      if (!painting)
        return;

      // Set up the path when the anchor is pressed.
      if (SwingUtilities.isLeftMouseButton(e) && !anchorSet) {
        LineTemplate lt = ((LineTemplate)template);
        lt.clearPath();
        ArrayList<CellPoint> path = new ArrayList<CellPoint>();
        path.add(lt.getPointFromPool(0, 0));
        lt.setPath(path);
      } // endif
      super.mousePressed(e);
    }
    
    /**
     * @see net.rptools.maptool.client.tool.drawing.RadiusTemplateTool#handleMouseMovement(java.awt.event.MouseEvent)
     */
    @Override
    protected void handleMouseMovement(MouseEvent e) {

        // Set the anchor
        ZonePoint vertex = template.getVertex();
        if (!anchorSet) {
          setCellAtMouse(e, vertex);
          controlOffset = null;
          
        // Move the anchor if control pressed.
        } else if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK) {
          handleControlOffset(e, vertex);
          
        // Add or delete a new cell
        } else {
            
          // Get mouse point as an offset from the vertex
          LineTemplate lt = ((LineTemplate)template);
          ZonePoint mouse = new ScreenPoint(e.getX(), e.getY()).convertToZone(renderer);
          CellPoint mousePoint = renderer.getZone().getGrid().convert(mouse);
          CellPoint vertexPoint = renderer.getZone().getGrid().convert(lt.getVertex());
          mousePoint.x = mousePoint.x - vertexPoint.x; 
          mousePoint.y = mousePoint.y - vertexPoint.y;
          
          // Compare to the second to last point, if == delete last point
          List<CellPoint> path = lt.getPath();
          if (path.size() > 1 && path.get(path.size() - 2).equals(mousePoint)) {
            lt.addPointToPool(path.remove(path.size() - 1));
          } else {
            CellPoint lastPoint = path.get(path.size() - 1);
            int dx = mousePoint.x - lastPoint.x;
            int dy = mousePoint.y - lastPoint.y;
            if (Math.abs(dx) == 1 && dy == 0 || Math.abs(dy) == 1 && dx == 0) {
              path.add(mousePoint);
            } // endif
          } // endif
          renderer.repaint();
          controlOffset = null;  
        } // endif
    }
    
    /**
     * @see net.rptools.maptool.client.tool.drawing.RadiusTemplateTool#resetTool()
     */
    @Override
    protected void resetTool() {
        super.resetTool();
        ((WallTemplate)template).clearPath();
    }
}
