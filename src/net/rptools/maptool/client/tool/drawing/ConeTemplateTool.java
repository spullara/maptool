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

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import net.rptools.maptool.client.ScreenPoint;
import net.rptools.maptool.model.drawing.AbstractTemplate;
import net.rptools.maptool.model.drawing.ConeTemplate;
import net.rptools.maptool.model.drawing.RadiusTemplate;

/**
 * Draw a template for an effect with a cone area. Make the template show the
 * squares that are effected, not just draw a circle. Let the player choose the
 * vertex with the mouse and use the wheel to set the radius. Use control and
 * mouse position to direct the cone. This allows the user to move the entire
 * template where it is to be used before placing it which is very important
 * when casting a spell.
 * 
 * @author jgorrell
 * @version $Revision$ $Date$ $Author$
 */
public class ConeTemplateTool extends RadiusTemplateTool {

  /*---------------------------------------------------------------------------------------------
   * Constructor 
   *-------------------------------------------------------------------------------------------*/

  /**
   * Add the icon to the toggle button. 
   */
  public ConeTemplateTool() {
    try {
      setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream(
          "net/rptools/maptool/client/image/tool/temp-blue-cone.png"))));
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
    return new ConeTemplate();
  }

  /**
   * @see net.rptools.maptool.client.ui.Tool#getTooltip()
   */
  @Override
  public String getTooltip() {
    return "tool.cone.tooltip";
  }

  /**
   * @see net.rptools.maptool.client.ui.Tool#getInstructions()
   */
  @Override
  public String getInstructions() {
    return "tool.cone.instructions";
  }

  /*---------------------------------------------------------------------------------------------
   * MouseMotionListener Interface Methods
   *-------------------------------------------------------------------------------------------*/

  /**
   * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
   */
  public void mouseMoved(MouseEvent e) {
    if (!anchorSet) {
      setCellAtMouse(e, template.getVertex()); // Set the vertex
      controlOffset = null;  
    } else if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK) {
      handleControlOffset(e, template.getVertex());
    } else {
      template.setRadius(getRadiusAtMouse(e));
      ScreenPoint vertex = template.getVertex();
      ((ConeTemplate) template).setDirection(RadiusTemplate.Direction.findDirection(e.getX(), e.getY(), vertex.x, vertex.y));
      renderer.repaint();
      controlOffset = null;
    } // endif
  }
}
