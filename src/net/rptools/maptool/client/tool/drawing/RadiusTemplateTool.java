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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import net.rptools.maptool.client.CellPoint;
import net.rptools.maptool.client.ScreenPoint;
import net.rptools.maptool.client.ZonePoint;
import net.rptools.maptool.client.tool.ToolHelper;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.drawing.AbstractTemplate;
import net.rptools.maptool.model.drawing.Pen;
import net.rptools.maptool.model.drawing.RadiusTemplate;

/**
 * Draw a template for an effect with a radius. Make the template show the
 * squares that are effected, not just draw a circle. Let the player choose the
 * vertex with the mouse and use the wheel to set the radius. This allows the
 * user to move the entire template where it is to be used before placing it
 * which is very important when casting a spell.
 * 
 * @author jgorrell
 * @version $Revision$ $Date$ $Author$
 */
public class RadiusTemplateTool extends AbstractDrawingTool implements MouseMotionListener {

  /*---------------------------------------------------------------------------------------------
   * Instance Variables
   *-------------------------------------------------------------------------------------------*/

  /**
   * The vertex that the effect is drawn on. It is the upper left corner of a
   * specific grid location.
   */
  protected AbstractTemplate template = createBaseTemplate();

  /**
   * This flag controls the painting of the template.
   */
  protected boolean painting;

  /**
   * Has the anchoring point been set? When false, the anchor point is being
   * placed. When true, the area of effect is being drawn on the display.
   */
  protected boolean anchorSet;

  /**
   * The offset used to move the vertex when the control key is pressed. If this value is
   * <code>null</code> then this would be the first time that the control key had been
   * reported in the mouse event.
   */
  protected ScreenPoint controlOffset;
  
  /*---------------------------------------------------------------------------------------------
   * Class Variables
   *-------------------------------------------------------------------------------------------*/

  /**
   * The width of the cursor. Since the cursor is a cross, this it the width of
   * the horizontal bar and the height of the vertical bar. Always make it an
   * odd number to keep it aligned on the grid properly.
   */
  public static final int CURSOR_WIDTH = 25;

  /*---------------------------------------------------------------------------------------------
   * Constructor 
   *-------------------------------------------------------------------------------------------*/

  /**
   * Add the icon to the toggle button.
   */
  public RadiusTemplateTool() {
    try {
      setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream(
          "net/rptools/maptool/client/image/tool/RadiusTemplate.png"))));
    } catch (IOException ioe) {
      ioe.printStackTrace();
    } // endtry
  }

  /*---------------------------------------------------------------------------------------------
   * Instance Methods
   *-------------------------------------------------------------------------------------------*/

  /**
   * Create the base template for the tool.
   * 
   * @return The radius template that is to be drawn.
   */
  protected AbstractTemplate createBaseTemplate() {
    return new RadiusTemplate();
  }

  /**
   * Calculate the cell at the mouse point. If it is different from the current
   * point, make it the current point and repaint.
   * 
   * @param e The event to be checked.
   * @param point The current point.
   * @return Flag indicating that the value changed.
   */
  protected boolean setCellAtMouse(MouseEvent e, ScreenPoint point) {
    ScreenPoint working = getCellAtMouse(e);
    if (!working.equals(point)) {
      point.x = working.x;
      point.y = working.y;
      renderer.repaint();
      return true;
    } // endif
    return false;
  }

  /**
   * Calculate the cell closest to a mouse point. Cell coordinates are the 
   * upper left corner of the cell. 
   * 
   * @param e The event to be checked.
   * @return The cell at the mouse point in screen coordinates.
   */
  protected ScreenPoint getCellAtMouse(MouseEvent e) {

    // Find the upper left corner of the cell that the mouse is in.
    ScreenPoint working = renderer.getCellAt(new ScreenPoint(e.getX(), e.getY())).convertToScreen(renderer);

    // If the mouse is over half way to the next vertext, move it there
    // (both X & Y)
    int grid = (int) (renderer.getZone().getGridSize() * renderer.getScale());
    if (e.getX() - working.x >= grid / 2)
      working.x += grid;
    if (e.getY() - working.y >= grid / 2)
      working.y += grid;
    return working;
  }

  /**
   * Calculate the radius between two cells based on a mouse event.
   * 
   * @param e Mouse event being checked
   * @return The radius between the current mouse location and the vertex location.
   */
  protected int getRadiusAtMouse(MouseEvent e) {
    ScreenPoint working = getCellAtMouse(e);
    CellPoint workingCell = renderer.getCellAt(working);
    CellPoint vertexCell = renderer.getCellAt(template.getVertex());
    int x = Math.abs(workingCell.x - vertexCell.x);
    int y = Math.abs(workingCell.y - vertexCell.y);
    return AbstractTemplate.getDistance(x, y);
  }

  /**
   * Paint a cursor
   * 
   * @param g Where to paint.
   * @param color The color of the cursor
   * @param thickness The thickness of the cursor.
   * @param vertex The vertex holding the cursor.
   */
  protected void paintCursor(Graphics2D g, Color color, float thickness, ScreenPoint vertex) {
    int halfCursor = CURSOR_WIDTH / 2;
    g.setColor(color);
    g.setStroke(new BasicStroke(thickness));
    g.drawLine(vertex.x - halfCursor, vertex.y, vertex.x + halfCursor, vertex.y);
    g.drawLine(vertex.x, vertex.y - halfCursor, vertex.x, vertex.y + halfCursor);
  }

  /**
   * Get the pen set up to paint the overlay.
   * 
   * @return The pen used to paint the overlay.
   */
  protected Pen getPenForOverlay() {
    // Get the pen and modify to only show a cursor and the boundary
    Pen pen = getPen(); // new copy of pen, OK to modify
    pen.setBackgroundMode(Pen.MODE_SOLID);
    pen.setForegroundMode(Pen.MODE_SOLID);
    pen.setThickness(3);
    if (pen.isEraser()) {
      pen.setEraser(false);
      pen.setColor(Color.WHITE.getRGB());
    } // endif
    return pen;
  }

  /**
   * Paint the radius value in feet.
   * 
   * @param g Where to paint.
   * @param p Vertex where radius is painted.
   */
  protected void paintRadius(Graphics2D g, ScreenPoint p) {
    if (template.getRadius() > 0) {
      ScreenPoint centerText = new ScreenPoint(p.x, p.y); // Must copy point
      centerText.translate(CURSOR_WIDTH, -CURSOR_WIDTH);
      ToolHelper.drawMeasurement(g, template.getRadius() * renderer.getZone().getFeetPerCell(), centerText.x, centerText.y);
    } // endif
  }

  /**
   * Paint the template at the current scale.
   * 
   * @param g Where to paint.
   * @param pen Pen used to paint.
   */
  protected void paintTemplate(Graphics2D g, Pen pen) {
    template.setScale(renderer.getScale());
    template.draw(g, pen);
    template.setScale(1.0);
  }

  /**
   * New instance of the template, at the passed vertex
   * 
   * @param vertex The starting vertex for the new template or
   * <code>null</code> if we should use the current template's vertex.
   */
  protected void resetTool(ScreenPoint vertex) {
    anchorSet = false;
    if (vertex == null) {
      vertex = template.getVertex();
      vertex = new ScreenPoint(vertex.x, vertex.y); // Must create copy!
    } // endif
    template = createBaseTemplate();
    template.setVertex(vertex);
    template.setZoneId(renderer.getZone().getId());
    controlOffset = null;
    renderer.repaint();
  }

  /**
   * Handles setting the vertex when the control key is pressed during 
   * mouse movement. A change in the passed vertex causes the template
   * to repaint the zone.
   * 
   * @param e The mouse movement event.
   * @param vertex The vertex being modified. 
   */
  protected void handleControlOffset(MouseEvent e, ScreenPoint vertex) {
    ScreenPoint working = getCellAtMouse(e);
    if (controlOffset == null) {
      controlOffset = working;
      controlOffset.x = working.x - vertex.x;
      controlOffset.y = working.y - vertex.y;
    } else {
      working.x = working.x - controlOffset.x;
      working.y = working.y - controlOffset.y;
      if (!working.equals(vertex)) {
        vertex.x = working.x;
        vertex.y = working.y;
        renderer.repaint();
      } // endif
    } // endif
  }
  
  /*---------------------------------------------------------------------------------------------
   * MouseMotionListener Interface Methods
   *-------------------------------------------------------------------------------------------*/

  /**
   * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
   */
  public void mouseMoved(MouseEvent e) {
    ScreenPoint vertex = template.getVertex();
    if (!anchorSet) {
      setCellAtMouse(e, vertex);
      controlOffset = null;  
    } else if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK) {
      handleControlOffset(e, vertex);
    } else {
      template.setRadius(getRadiusAtMouse(e));
      renderer.repaint();
      controlOffset = null;  
    } // endif
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
    if (painting && renderer != null) {
      Pen pen = getPenForOverlay();
      paintTemplate(g, pen);
      paintCursor(g, new Color(pen.getColor()), pen.getThickness(), template.getVertex());
      paintRadius(g, template.getVertex());
    } // endif
  }

  /**
   * New instance of the template, at the current vertex
   * 
   * @see net.rptools.maptool.client.ui.Tool#resetTool()
   */
  @Override
  protected void resetTool() {
    resetTool(null);
  }
  
  /**
   * It is OK to modify the pen returned by this method
   * 
   * @see net.rptools.maptool.client.tool.drawing.AbstractDrawingTool#getPen()
   */
  protected Pen getPen() {

    // Just paint the foreground
    Pen pen = super.getPen();
    pen.setBackgroundMode(Pen.MODE_SOLID);
    return pen;
  }

  /**
   * @see net.rptools.maptool.client.ui.Tool#detachFrom(net.rptools.maptool.client.ui.zone.ZoneRenderer)
   */
  @Override
  protected void detachFrom(ZoneRenderer renderer) {
    super.detachFrom(renderer);
    template.setZoneId(null);
    renderer.repaint();
  }

  /**
   * @see net.rptools.maptool.client.ui.Tool#attachTo(net.rptools.maptool.client.ui.zone.ZoneRenderer)
   */
  @Override
  protected void attachTo(ZoneRenderer renderer) {
    template.setZoneId(renderer.getZone().getId());
    renderer.repaint();
    super.attachTo(renderer);
  }

  /**
   * @see net.rptools.maptool.client.ui.Tool#getTooltip()
   */
  @Override
  public String getTooltip() {
    return "tool.radiustemplate.tooltip";
  }

  /**
   * @see net.rptools.maptool.client.ui.Tool#getInstructions()
   */
  @Override
  public String getInstructions() {
    return "tool.radiustemplate.instructions";
  }

  /*---------------------------------------------------------------------------------------------
   * MouseListener Interface Methods
   *-------------------------------------------------------------------------------------------*/

  /**
   * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
   */
  public void mousePressed(MouseEvent e) {
    if (!painting)
      return;

    if (SwingUtilities.isLeftMouseButton(e)) {
	    // Need to set the anchor?
	    controlOffset = null;
	    if (!anchorSet) {
	      anchorSet = true;
	      return;
	    } // endif
	
	    // Need to finish the radius?
	    if (!painting || template.getRadius() < AbstractTemplate.MIN_RADIUS)
	      return;
	
	    // Set the eraser, set the drawable, reset the tool.
	    setIsEraser(SwingUtilities.isRightMouseButton(e));
	    template.setRadius(getRadiusAtMouse(e));
	    ScreenPoint vertex = template.getVertex();
	    ScreenPoint newPoint = new ScreenPoint(vertex.x, vertex.y);
	    ZonePoint zPoint = vertex.convertToZone(renderer);
	    vertex.x = zPoint.x;
	    vertex.y = zPoint.y;
	    completeDrawable(renderer.getZone().getId(), getPen(), template);
	    setIsEraser(false);
	    resetTool(newPoint);
    } else {
    	super.mousePressed(e);
    }
  }

  /**
   * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
   */
  public void mouseEntered(MouseEvent e) {
    painting = true;
    renderer.repaint();
  }

  /**
   * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
   */
  public void mouseExited(MouseEvent e) {
    painting = false;
    renderer.repaint();
  }
}
