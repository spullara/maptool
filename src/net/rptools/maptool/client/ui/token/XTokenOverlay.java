/* --- Copyright 2005 Bluejay Software. All rights reserved --- */

package net.rptools.maptool.client.ui.token;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Line2D;

import net.rptools.maptool.model.Token;

/**
 * @author jgorrell
 * @version $Revision$ $Date$ $Author$
 */

public class XTokenOverlay extends TokenOverlay {

  /**
   * Color for the X
   */
  private Color color;
  
  /**
   * Stroke used to draw the line
   */
  private BasicStroke stroke;
  
  /**
   * Create a X token overlay with the given name.
   * 
   * @param aName Name of this token overlay.
   * @param aColor The color of this token overlay.
   * @param aWidth The width of the lines in this token overlay.
   */
  public XTokenOverlay(String aName, Color aColor, int aWidth) {
    super(aName);
    if (aColor == null) aColor = Color.RED;
    color = aColor;
    if (aWidth <= 0) aWidth = 3;
    stroke = new BasicStroke(aWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
  }

  /**
   * @see net.rptools.maptool.client.ui.token.TokenOverlay#paintOverlay(java.awt.Graphics2D, net.rptools.maptool.model.Token)
   */
  @Override
  public void paintOverlay(Graphics2D g, Token aToken) {
    Color tempColor = g.getColor();
    g.setColor(color);
    Stroke tempStroke = g.getStroke();
    g.setStroke(stroke);
    Rectangle b = g.getClipBounds();
    g.draw(new Line2D.Double(0, 0, b.width, b.height));
    g.draw(new Line2D.Double(0, b.height, b.width, 0));
    g.setColor(tempColor);
    g.setStroke(tempStroke);
  }

  /**
   * Get the color for this XTokenOverlay.
   *
   * @return Returns the current value of color.
   */
  protected Color getColor() {
    return color;
  }

  /**
   * Get the stroke for this XTokenOverlay.
   *
   * @return Returns the current value of stroke.
   */
  protected BasicStroke getStroke() {
    return stroke;
  }

}
