/* --- Copyright 2006 Bluejay Software. All rights reserved --- */

package net.rptools.maptool.client.tool.drawing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

import net.rptools.lib.MD5Key;
import net.rptools.lib.image.ImageUtil;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.swing.TwoToneTextPane;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.drawing.AssetDrawable;
import net.rptools.maptool.model.drawing.Pen;

/**
 * A text tool that uses a text component to allow text to be entered on the display and then renders
 * it as an image.
 * 
 * @author jgorrell
 * @version $Revision$ $Date$ $Author$
 */
public class DrawnTextTool extends AbstractDrawingTool implements MouseMotionListener {

  /*---------------------------------------------------------------------------------------------
   * Instance Variables
   *-------------------------------------------------------------------------------------------*/
  
  /**
   * Flag used to indicate that the anchor has been set.
   */
  private boolean anchorSet;

  /**
   * The anchor point originally selected 
   */
  private Point anchor = new Point();
  
  /**
   * The bounds of the display rectangle
   */
  private Rectangle bounds = new Rectangle();
  
  /**
   * The text pane used to paint the text.
   */
  private TwoToneTextPane textPane;

  /*---------------------------------------------------------------------------------------------
   * Constructors
   *-------------------------------------------------------------------------------------------*/
  
  /**
   * A transparent color used in the background
   */
  private static final Color TRANSPARENT = new Color(0, 0, 0, 0);

  /*---------------------------------------------------------------------------------------------
   * Constructors
   *-------------------------------------------------------------------------------------------*/
  
  /**
   * Initialize the tool icon
   */
  public DrawnTextTool() {
    try {
      setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream(
          "net/rptools/maptool/client/image/Tool_Draw_Write.gif"))));
    } catch (IOException ioe) {
      ioe.printStackTrace();
    } // endtry
  }

  /*---------------------------------------------------------------------------------------------
   * Tool & AbstractDrawingTool Abstract Methods
   *-------------------------------------------------------------------------------------------*/
  
  /**
   * @see net.rptools.maptool.client.tool.drawing.AbstractDrawingTool#paintOverlay(net.rptools.maptool.client.ui.zone.ZoneRenderer, java.awt.Graphics2D)
   */
  @Override
  public void paintOverlay(ZoneRenderer aRenderer, Graphics2D aG) {
    if (!anchorSet) return;
    aG.setColor(Color.BLACK);
    aG.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
  }

  /**
   * @see net.rptools.maptool.client.ui.Tool#getTooltip()
   */
  @Override
  public String getTooltip() {
    return "tool.text.tooltip";
  }

  /**
   * @see net.rptools.maptool.client.ui.Tool#getInstructions()
   */
  @Override
  public String getInstructions() {
    return "tool.text.instructions";
  }

  /**
   * @see net.rptools.maptool.client.ui.Tool#resetTool()
   */
  @Override
  protected void resetTool() {
    anchorSet = false;
    if (textPane != null)
      zoneRenderer.remove(textPane);
    textPane = null;
    zoneRenderer.repaint();
  }

  /*---------------------------------------------------------------------------------------------
   * MouseListener Interface Methods
   *-------------------------------------------------------------------------------------------*/
  
  /**
   * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
   */
  public void mouseClicked(MouseEvent event) {
    // Do nothing
  }

  /**
   * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
   */
  public void mousePressed(MouseEvent event) {
    if (!anchorSet) {
      anchor.x = event.getX();
      anchor.y = event.getY();
      anchorSet = true;
    } else {
      setBounds(event);
      
      // Create a text component and place it on the renderer's component
      textPane = new TwoToneTextPane();
      textPane.setBounds(bounds);
      textPane.setOpaque(false);
      textPane.setBackground(TRANSPARENT);
      zoneRenderer.add(textPane);
      textPane.requestFocusInWindow();
      
      // Create a style for the component
      Pen pen = getPen();
      Style style = textPane.addStyle("default", null);
      style.addAttribute(StyleConstants.FontSize, 20);
      style.addAttribute(StyleConstants.FontFamily, "SanSerif");
      style.addAttribute(StyleConstants.Bold, true);
      style.addAttribute(StyleConstants.Foreground, new Color(pen.getColor()));
      style.addAttribute(StyleConstants.Background, new Color(pen.getBackgroundColor()));
      textPane.setLogicalStyle(style);
           
      // Make the enter key addthe text
      KeyStroke k = KeyStroke.getKeyStroke("ENTER");
      textPane.getKeymap().removeKeyStrokeBinding(k);
      textPane.getKeymap().addActionForKeyStroke(k, new AbstractAction() {
        public void actionPerformed(ActionEvent aE) {
          completeDrawable();
        }
      });
    }
  }

  /**
   * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
   */
  public void mouseReleased(MouseEvent aE) {
    // TODO Auto-generated method stub

  }

  /**
   * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
   */
  public void mouseEntered(MouseEvent aE) {
    // TODO Auto-generated method stub

  }

  /**
   * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
   */
  public void mouseExited(MouseEvent aE) {
    // TODO Auto-generated method stub

  }

  /*---------------------------------------------------------------------------------------------
   * MouseMotionListener Interface Methods
   *-------------------------------------------------------------------------------------------*/
  
  /**
   * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
   */
  public void mouseMoved(MouseEvent event) {
    if (!anchorSet) return;
    if (textPane != null) return;
    setBounds(event);
    zoneRenderer.repaint();
  }
  
  /**
   * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
   */
  public void mouseDragged(MouseEvent aE) {
    // TODO Auto-generated method stub
    
  }

  /**
   * @see net.rptools.maptool.client.ui.Tool#getKeyActionMap()
   */
  @Override
  protected Map<KeyStroke, Action> getKeyActionMap() {
    HashMap<KeyStroke, Action> map = new HashMap<KeyStroke, Action>();
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true), null);     
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), null);     
    return map;
  }
  
  /*---------------------------------------------------------------------------------------------
   * Instance Methods
   *-------------------------------------------------------------------------------------------*/
  
  /**
   * Set the bounds for the text area.
   * 
   * @param event The mouse event used in the calculation.
   */
  private void setBounds(MouseEvent event) {
    bounds.x = Math.min(anchor.x, event.getX());
    bounds.y = Math.min(anchor.y, event.getY());
    bounds.width = Math.abs(anchor.x - event.getX());
    bounds.height = Math.abs(anchor.y - event.getY());
  }
  
  /**
   * Finish drawing the text. 
   */
  private void completeDrawable() {
    
    // Create an image from the text pane component
    BufferedImage image = ImageUtil.createCompatibleImage(bounds.width, bounds.height, Transparency.TRANSLUCENT);
    Graphics2D g2d = (Graphics2D)image.getGraphics();
    textPane.setCaret(null);
    textPane.paint(g2d);
    
    // Create an asset from the image
    MD5Key assetId = null;
    try {
      Asset asset = new Asset(new GUID().toString(), ImageUtil.imageToBytes(image, "png"));
      assetId = asset.getId();
      if (!AssetManager.hasAsset(asset)) AssetManager.putAsset(asset);
      if (!MapTool.getCampaign().containsAsset(asset)) MapTool.serverCommand().putAsset(asset);
    } catch (IOException e) {
      e.printStackTrace();
    } // endtry
    
    // Cleanup
    textPane.setVisible(false);
    textPane.getParent().remove(textPane);
    textPane = null;
    
    // Tell everybody else
    completeDrawable(zoneRenderer.getZone().getId(), getPen(), new AssetDrawable(assetId, bounds, zoneRenderer.getZone().getId())); 
    resetTool();
  }
}
