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
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import net.rptools.maptool.client.AppState;
import net.rptools.maptool.client.ScreenPoint;
import net.rptools.maptool.client.ZonePoint;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.drawing.AbstractTemplate;
import net.rptools.maptool.model.drawing.Drawable;
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
   * Instance Variables
   *-------------------------------------------------------------------------------------------*/

  /**
   * Has the anchoring point been set? When false, the anchor point is being
   * placed. When true, the area of effect is being drawn on the display.
   */
  private boolean pathAnchorSet;

  /*---------------------------------------------------------------------------------------------
   * Constructor 
   *-------------------------------------------------------------------------------------------*/

  /**
   * Add the icon to the toggle button.
   */
  public LineTemplateTool() {
    try {
      setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream(
          "net/rptools/maptool/client/image/tool/LineTemplate.png"))));
    } catch (IOException ioe) {
      ioe.printStackTrace();
    } // endtry
  }

  /*---------------------------------------------------------------------------------------------
   * Overidden RadiusTemplateTool Methods
   *-------------------------------------------------------------------------------------------*/

  /**
   * @see net.rptools.maptool.client.ui.Tool#getInstructions()
   */
  @Override
  public String getInstructions() {
    return "tool.linetemplate.instructions";
  }

  /**
   * @see net.rptools.maptool.client.tool.drawing.RadiusTemplateTool#createBaseTemplate()
   */
  @Override
  protected AbstractTemplate createBaseTemplate() {
    return new LineTemplate();
  }

  /**
   * @see net.rptools.maptool.client.tool.drawing.RadiusTemplateTool#resetTool(net.rptools.maptool.client.ScreenPoint)
   */
  @Override
  protected void resetTool(ScreenPoint aVertex) {
    super.resetTool(aVertex);
    pathAnchorSet = false;
    ((LineTemplate) template).setDoubleWide(AppState.useDoubleWideLine());
  }
  
  /**
   * @see net.rptools.maptool.client.tool.drawing.RadiusTemplateTool#getTooltip()
   */
  @Override
  public String getTooltip() {
    return "tool.linetemplate.tooltip";
  }
  
  /*---------------------------------------------------------------------------------------------
   * Overridden AbstractDrawingTool Methods
   *-------------------------------------------------------------------------------------------*/

  /**
   * @see net.rptools.maptool.client.ui.zone.ZoneOverlay#paintOverlay(net.rptools.maptool.client.ui.zone.ZoneRenderer,
   *      java.awt.Graphics2D)
   */
  @Override
  public void paintOverlay(ZoneRenderer renderer, Graphics2D g) {
    if (painting && zoneRenderer != null) {
      Pen pen = getPenForOverlay();
      ScreenPoint vertex = template.getVertex();
      ScreenPoint pathVertex = ((LineTemplate) template).getPathVertex();
      paintTemplate(g, pen);
      paintCursor(g, new Color(pen.getColor()), pen.getThickness(), vertex);
      if (pathVertex != null) {
        paintCursor(g, new Color(pen.getColor()), pen.getThickness(), pathVertex);
        paintRadius(g, vertex);
      } // endif
    } // endif
  }

  /**
   * @see net.rptools.maptool.client.tool.drawing.RadiusTemplateTool#getRadiusAtMouse(java.awt.event.MouseEvent)
   */
  @Override
  protected int getRadiusAtMouse(MouseEvent aE) {
    int radius = super.getRadiusAtMouse(aE);
    return Math.max(0, radius - 1);
  }
  
  /**
   * @see net.rptools.maptool.client.tool.drawing.AbstractDrawingTool#completeDrawable(net.rptools.maptool.model.GUID, net.rptools.maptool.model.drawing.Pen, net.rptools.maptool.model.drawing.Drawable)
   */
  @Override
  protected void completeDrawable(GUID aZoneId, Pen aPen, Drawable aDrawable) {
    
    // Need to convert the pathVertex in the line template before we complete the template
    LineTemplate template = (LineTemplate)aDrawable;
    ScreenPoint vertex = template.getPathVertex();
    ZonePoint zPoint = vertex.convertToZone(zoneRenderer);
    vertex.x = zPoint.x;
    vertex.y = zPoint.y;
    super.completeDrawable(aZoneId, aPen, aDrawable);
  }
  
  /**
   * @see net.rptools.maptool.client.tool.drawing.RadiusTemplateTool#mousePressed(java.awt.event.MouseEvent)
   */
  @Override
  public void mousePressed(MouseEvent aE) {
    if (!painting)
      return;
    
    // Need to set the anchor?
    controlOffset = null;
    if (!anchorSet) {
      anchorSet = true;
      return;
    } // endif
    
    if (!pathAnchorSet) {
      pathAnchorSet = true;
      return;
    } // endif

    // Let the radius code finish the template
    super.mousePressed(aE);
  }

  /*---------------------------------------------------------------------------------------------
   * MouseMotionListener Interface Methods
   *-------------------------------------------------------------------------------------------*/

  /**
   * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
   */
  public void mouseMoved(MouseEvent e) {
    
    // Setting anchor point
    LineTemplate lt = (LineTemplate) template;
    ScreenPoint pathVertex = lt.getPathVertex();
    if (!anchorSet) {
      setCellAtMouse(e, template.getVertex());
      controlOffset = null;
    } else if (!pathAnchorSet && (e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK) {
      handleControlOffset(e, template.getVertex());
    } else if (!pathAnchorSet) {
      template.setRadius(getRadiusAtMouse(e));
      controlOffset = null;
      
      // The path vertex remains null until it is set the first time.
      if (pathVertex == null) {
        pathVertex = new ScreenPoint(lt.getVertex().x, lt.getVertex().y);
        lt.setPathVertex(pathVertex);
      } // endif
      if (pathVertex != null && setCellAtMouse(e, pathVertex)) 
        lt.clearPath();
    } else if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK) {
      handleControlOffset(e, pathVertex);
    } else {
      template.setRadius(getRadiusAtMouse(e));
      zoneRenderer.repaint();
      controlOffset = null;
      return;
    } // endif
    
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
      } // endif
    } // endif
  }
  
  /*---------------------------------------------------------------------------------------------
   * Instance Methods
   *-------------------------------------------------------------------------------------------*/
  
}
