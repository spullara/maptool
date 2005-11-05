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

package net.rptools.maptool.client.tool.drawing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import net.rptools.maptool.client.ScreenPoint;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.drawing.AbstractTemplate;
import net.rptools.maptool.model.drawing.LineTemplate;
import net.rptools.maptool.model.drawing.Pen;
import net.rptools.maptool.model.drawing.AbstractTemplate.Quadrant;

/**
 * Draw the effected area of a spell area type of line.
 * 
 * @author jgorrell
 * @version $Revision$ $Date$ $Author$
 */
public class LineTemplateTool extends RadiusTemplateTool {
  
  /*---------------------------------------------------------------------------------------------
   * Constructor 
   *-------------------------------------------------------------------------------------------*/
  
  /**
   * Add the icon to the toggle button.
   * TODO: Create an icon that doesn't look suspicously like the text 'Line'
   */
  public LineTemplateTool() {
    try {
      setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("net/rptools/maptool/client/image/Tool_Draw_Line_Template.GIF"))));
    } catch (IOException ioe) {
      ioe.printStackTrace();
    } // endtry
  }
  
  /*---------------------------------------------------------------------------------------------
   * Overidden RadiusTemplateTool Methods
   *-------------------------------------------------------------------------------------------*/
  
  /**
   * @see net.rptools.maptool.client.tool.drawing.RadiusTemplateTool#createBaseTemplate()
   */
  @Override
  protected AbstractTemplate createBaseTemplate() {
    return new LineTemplate();
  }
  
  /*---------------------------------------------------------------------------------------------
   * Overridden AbstractDrawingTool Methods
   *-------------------------------------------------------------------------------------------*/
  
  /**
   * @see net.rptools.maptool.client.ui.zone.ZoneOverlay#paintOverlay(net.rptools.maptool.client.ui.zone.ZoneRenderer, java.awt.Graphics2D)
   */
  @Override
  public void paintOverlay(ZoneRenderer renderer, Graphics2D g) {
    if (painting && zoneRenderer != null) {
      Pen pen = getPenForOverlay();
      ScreenPoint vertex = template.getVertex();
      ScreenPoint pathVertex = ((LineTemplate)template).getPathVertex();
      paintTemplate(g, pen);
      paintCursor(g, new Color(pen.getColor()), pen.getThickness(), vertex);
      if (pathVertex != null) {
        paintCursor(g, new Color(pen.getBackgroundColor()), pen.getThickness(), pathVertex);
        paintRadius(g, vertex);
      } // endif
    }  // endif
  }
  
  /*---------------------------------------------------------------------------------------------
   * MouseMotionListener Interface Methods
   *-------------------------------------------------------------------------------------------*/
  
  /**
   * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
   */
  public void mouseMoved(MouseEvent e) {
    LineTemplate lt = (LineTemplate)template;
    ScreenPoint pathVertex = lt.getPathVertex();
    boolean control = (e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) > 0;
    if (control) {
      
      // The path vertex remains null until it is set the first time.
      if (pathVertex == null) {
        pathVertex = new ScreenPoint(lt.getVertex().x, lt.getVertex().y);
        lt.setPathVertex(pathVertex);
      } // endif
      if (pathVertex != null && setCellAtMouse(e, pathVertex)) lt.clearPath();
    } else {
      setCellAtMouse(e, template.getVertex());
    }

    // Quadrant change?
    if (pathVertex != null) {
        ScreenPoint vertex = template.getVertex();
        int dx = e.getX() - vertex.x;
        int dy = e.getY() - vertex.y;
        AbstractTemplate.Quadrant quadrant = (dx < 0) ? (dy < 0 ? Quadrant.NORTH_WEST : Quadrant.SOUTH_WEST) 
                : (dy < 0 ? Quadrant.NORTH_EAST : Quadrant.SOUTH_EAST);
        if (quadrant != lt.getQuadrant()) {
            lt.setQuadrant(quadrant);
            zoneRenderer.repaint();
        }
    }

  }
  
  /*---------------------------------------------------------------------------------------------
   * MouseWheelListener Interface Methods
   *-------------------------------------------------------------------------------------------*/
  
  /**
   * @see java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.MouseWheelEvent)
   */
  public void mouseWheelMoved(MouseWheelEvent e) {
    if (((LineTemplate)template).getPathVertex() != null) {
      template.setRadius(template.getRadius() - e.getWheelRotation());
      zoneRenderer.repaint();
    } // endif
  }
}
