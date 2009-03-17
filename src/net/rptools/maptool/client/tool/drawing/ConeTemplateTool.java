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
import net.rptools.maptool.language.I18N;
import net.rptools.maptool.model.ZonePoint;
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
  
  /**
   * @see net.rptools.maptool.client.tool.drawing.RadiusTemplateTool#setRadiusFromAnchor(java.awt.event.MouseEvent)
   */
  @Override
  protected void setRadiusFromAnchor(MouseEvent e) {
    super.setRadiusFromAnchor(e);
    
    // Set the direction based on the mouse location too
    ZonePoint vertex = template.getVertex();
    ZonePoint mouse = new ScreenPoint(e.getX(), e.getY()).convertToZone(renderer);
    ((ConeTemplate) template).setDirection(RadiusTemplate.Direction.findDirection(mouse.x, mouse.y, vertex.x, vertex.y));
  }
}