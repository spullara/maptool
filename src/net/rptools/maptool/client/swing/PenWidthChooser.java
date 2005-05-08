/**
 * 
 */
package net.rptools.maptool.client.swing;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JList;

import net.rptools.maptool.model.drawing.Pen;

/**
 * Combo box showing the available pen widths and a preview of each.
 * 
 * @author Jay
 * @version $Revision$ $Date$ $Author:&
 */
public class PenWidthChooser extends JComboBox {

  /**
   * The renderer for this chooser.
   */
  private PenListRenderer renderer = new PenListRenderer();
  
  /**
   * The first width in the model
   */
  public static final float MIN_WIDTH = 6;
  
  /**
   * The largest width allowed in the model.
   */
  public static final float MAX_WIDTH = 18;
  
  /**
   * The amount that the width is incremented for each successive element in the model.
   */
  public static final float WIDTH_INCREMENT = 2;
  
  /**
   * The width that the Icon is painted.
   */
  public static final int ICON_WIDTH = 25;
  
  /**
   * The maximum number of eleemnts in the list before it scrolls
   */
  public static final int MAX_ROW_COUNT = 10;
  
  /**
   * Create the renderer and model for the combo box
   */
  public PenWidthChooser() {
    setRenderer(renderer);
    DefaultComboBoxModel model = new DefaultComboBoxModel();
    float selected = -1;
    float defaultThickness = Pen.DEFAULT.getThickness();
    for (float i = MIN_WIDTH; i <= MAX_WIDTH; i += WIDTH_INCREMENT) {
      model.addElement(i);
      renderer.icon.strokes.put(i, new BasicStroke(i, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
      if (i == defaultThickness || i < defaultThickness && i < defaultThickness + WIDTH_INCREMENT) 
        selected = i;
    } // endfor
    
    // Set up the component
    setModel(model);
    setMaximumSize(getPreferredSize());
    setMaximumRowCount(MAX_ROW_COUNT);
    setSelectedItem(Math.max(selected, MIN_WIDTH));
  }
  
  /**
   * Renderer for the items in the combo box
   * 
   * @author jgorrell
   * @version $Revision$ $Date$ $Author$
   */
  private class PenListRenderer extends DefaultListCellRenderer {
    
    /**
     * Icon used to draw the line sample
     */
    PenIcon icon = new PenIcon();
    
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

      // Sets the text, clears the icon
      super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus); 
      
      // Set the icon
      icon.width = (Float)value;
      setIcon(icon);
      return this;
    }
  }
  
  /**
   * Icon for the renderer
   * 
   * @author jgorrell
   * @version $Revision$ $Date$ $Author$
   */
  private class PenIcon implements Icon {

    /**
     * Pen used in drawing.
     */
    private float width = 0;
    
    /**
     * Strokes for this icon
     */
    private Map<Float, Stroke> strokes = new HashMap<Float, Stroke>();
    
    /**
     * @see javax.swing.Icon#getIconHeight()
     */
    public int getIconHeight() {
      return getHeight(); 
    }
    
    /**
     * @see javax.swing.Icon#getIconWidth()
     */
    public int getIconWidth() {
      return ICON_WIDTH;
    }
    
    /**
     * @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
     */
    public void paintIcon(Component c, Graphics g, int x, int y) {
      
      // Fill the background
      Graphics2D g2d = (Graphics2D)g;
      g2d.setColor(c.getBackground());
      g2d.fillRect(x, y, getIconWidth(), getIconHeight());
      
      // Draw a line centered in the foreground
      g2d.setColor(c.getForeground());
      g2d.setStroke(strokes.get(width));
      int yCentered = y + getIconHeight() / 2;
      g2d.drawLine(x, yCentered, x + getIconWidth(), yCentered);
    }
  }
}
