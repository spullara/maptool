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

package net.rptools.maptool.client.ui.token;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import net.rptools.maptool.language.I18N;
import net.rptools.maptool.model.drawing.AbstractTemplate.Direction;
import net.rptools.maptool.model.drawing.AbstractTemplate.Quadrant;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Dialog used to select properties of the light state.
 * 
 * @author jgorrell
 * @version $Revision$ $Date$ $Author$
 */

public class LightDialog extends JDialog {

  /*---------------------------------------------------------------------------------------------
   * Instance Variables
   *-------------------------------------------------------------------------------------------*/

  /**
   * The corner of the token holding the light.
   */
  private JComboBox corner;
  
  /**
   * The shape of the light
   */
  private JComboBox shape;
  
  /**
   * The radius of the light
   */
  private JTextField radius;
  
  /**
   * The shadow illumination
   */
  private JTextField shadow;
  
  /**
   * The template returned by this dialog
   */
  private TokenTemplate template;
  
  /*---------------------------------------------------------------------------------------------
   * Class Variables
   *-------------------------------------------------------------------------------------------*/

  /**
   * Model used to select a token corner.
   */
  private static final Quadrant[] CORNERS = { 
    Quadrant.NORTH_WEST, Quadrant.NORTH_EAST, Quadrant.SOUTH_WEST, Quadrant.SOUTH_EAST 
  };
  
  /**
   * Prefix for all of the cone shapes
   */
  private static final String CONE = "Cone - ";
  
  /**
   * Model used to list the various shapes.
   */
  private static final String[] SHAPES = {
    "Radius", CONE + Direction.NORTH, CONE + Direction.NORTH_EAST, CONE + Direction.EAST,
    CONE + Direction.SOUTH_EAST, CONE + Direction.SOUTH, CONE + Direction.SOUTH_WEST,
    CONE + Direction.WEST, CONE + Direction.NORTH_WEST
  };
  
  /**
   * Ok button key
   */
  private static final String OK_BUTTON = "lightDialog.ok";
  
  /**
   * Cancel button key
   */
  private static final String CANCEL_BUTTON = "lightDialog.cancel";
  
  /**
   * Create a new dialog 
   * 
   * @param frame Frame that is displaying this dialog
   */
  public LightDialog(JFrame frame) {
    super(frame);
    setModal(true);
    
    FormLayout layout = new FormLayout("12px pref 4px fill:pref:grow 12px", 
        "12px pref 6px pref 6px pref 6px pref 12px pref 12px pref 12px");
    PanelBuilder builder = new PanelBuilder(layout);
    CellConstraints lcc = new CellConstraints();
    CellConstraints fcc = new CellConstraints();
    corner = new JComboBox(new DefaultComboBoxModel(CORNERS));
    builder.addLabel("&Token Corner", lcc.xy  (2, 2), corner, fcc.xy(4, 2));
    shape = new JComboBox(new DefaultComboBoxModel(SHAPES));
    builder.addLabel("&Shape", lcc.xy(2, 4), shape, fcc.xy(4, 4));
    radius = new JTextField();
    builder.addLabel("&Light Range (cells)", lcc.xy(2, 6), radius, fcc.xy(4, 6));
    shadow = new JTextField();
    builder.addLabel("&Shadow Range (cells)", lcc.xy(2, 8), shadow, fcc.xy(4, 8));
    builder.add(new JSeparator(), lcc.xywh(2, 10, 3, 1));
    builder.add(ButtonBarFactory.buildCenteredBar(new JButton(new ButtonAction(OK_BUTTON)), 
        new JButton(new ButtonAction(CANCEL_BUTTON))), lcc.xywh(2, 12, 3, 1));
    getContentPane().add(builder.getPanel());
    pack();
  }
  
  /**
   * Handle button presses
   * 
   * @author jgorrell
   * @version $Revision$ $Date$ $Author$
   */
  private class ButtonAction extends AbstractAction {
    
    /**
     * Create a button
     * 
     * @param key Key used to create the button.
     */
    private ButtonAction(String key) {
      putValue(ACTION_COMMAND_KEY, key);
      I18N.setAction(key, this);
    }
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent aE) {

      // Determine shape
      template = null;
      if (aE.getActionCommand().equals(OK_BUTTON)) {
        
        // Get ranges
        int lightRange = readRadius(radius, "Light Radius", true);
        int shadowRange = readRadius(shadow, "Shadow Radius", false);
        
        // Get corner
        Quadrant c = (Quadrant)corner.getSelectedItem();
        
        // Get type
        if (shape.getSelectedIndex() == 0) {
          RadiusLightTokenTemplate tt = new RadiusLightTokenTemplate();
          tt.setRadius(shadowRange + lightRange);
          tt.setShadowRadius(lightRange);
          tt.setCorner(c);
          template = tt;
        } else {
          ConeLightTokenTemplate tt = new ConeLightTokenTemplate();
          tt.setRadius(shadowRange + lightRange);
          tt.setShadowRadius(lightRange);
          Direction dir = Direction.valueOf(((String)shape.getSelectedItem()).substring(CONE.length()));
          tt.setDirection(dir);
          tt.setCorner(c);
          template = tt;
        } // endif
      } // endif
      setVisible(false);
    }
    
    /**
     * Get the radius from a text field
     * 
     * @param field Field being read.
     * @param fieldName The name of the field placed in messages.
     * @param required Is the field requires?
     * @return The value of the field as an integer
     */
    private int readRadius(JTextField field, String fieldName, boolean required) {
      
      // Read the text, make sure it exists
      String text = field.getText();
      if (text == null || (text = text.trim()).length() == 0) {
        if (required) {
          JOptionPane.showMessageDialog(field, fieldName + " must be set.", "Problem!", JOptionPane.ERROR_MESSAGE);
          throw new IllegalArgumentException(fieldName + " must be set.");
        } else {
          return 0;
        } // endif
      } // endif
      
      // Parse it into a number
      try {
        int ret = Integer.parseInt(text);
        if (ret < 0 || required && ret == 0) {
          JOptionPane.showMessageDialog(field, fieldName + " has a value that is too small.", "Problem!", JOptionPane.ERROR_MESSAGE);
          throw new IllegalArgumentException(fieldName + " has a value that is too small.");
        } // endif
        return ret;
      } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(field, fieldName + " does not contain a number.", "Problem!", JOptionPane.ERROR_MESSAGE);
        throw new IllegalArgumentException(fieldName + " is not a number.");
      } // endtry
    }
  }

  /**
   * Get the template for this LightDialog.
   *
   * @return Returns the current value of template.
   */
  public TokenTemplate getTemplate() {
    return template;
  }
  
  /**
   * Set the default values from a token template.
   * 
   * @param tt Get the default values from this template.
   */
  public void setDefaults(TokenTemplate tt) {
    if (tt instanceof RadiusLightTokenTemplate) {
      RadiusLightTokenTemplate rt = (RadiusLightTokenTemplate)tt;
      shape.setSelectedIndex(0);
      corner.setSelectedItem(rt.getCorner());
      radius.setText(Integer.toString(rt.getShadowRadius()));
      shadow.setText(Integer.toString(rt.getRadius() - rt.getShadowRadius()));
    } else if (tt instanceof ConeLightTokenTemplate) {      
      ConeLightTokenTemplate ct = (ConeLightTokenTemplate)tt;
      shape.setSelectedItem(CONE + ct.getDirection());
      corner.setSelectedItem(ct.getCorner());
      radius.setText(Integer.toString(ct.getShadowRadius()));
      shadow.setText(Integer.toString(ct.getRadius() - ct.getShadowRadius()));
    } else {
      shape.setSelectedIndex(0);
      corner.setSelectedIndex(0);
      radius.setText("4");
      shadow.setText("4");
    } // endif
  }
}
