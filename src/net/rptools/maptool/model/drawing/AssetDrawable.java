/* --- Copyright 2006 Bluejay Software. All rights reserved --- */

package net.rptools.maptool.model.drawing;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;

import net.rptools.lib.MD5Key;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.util.ImageManager;

/**
 * This class allows an asset to be used as a drawable.
 * 
 * @author jgorrell
 * @version $Revision$ $Date$ $Author$
 */
public class AssetDrawable implements Drawable {

  /**
   * Id of the asset to be drawn
   */
  private MD5Key assetId;
  
  /**
   * Id of this drawable.
   */
  private GUID id = new GUID();
  
  /**
   * The id of the zone where this drawable is painted.
   */
  private GUID zoneId;
  
  /**
   * The bounds of the asset drawn
   */
  private Rectangle bounds;
  
  /**
   * Build a drawable that draws an asset.
   * 
   * @param anAssetId The id of the asset to be drawn.
   * @param theBounds The bounds used to paint the drawable.
   * @param aZoneId The id of the zone that draws this drawable.
   */
  public AssetDrawable(MD5Key anAssetId, Rectangle theBounds, GUID aZoneId) {
    assetId = anAssetId;
    bounds = theBounds;
    zoneId = aZoneId;
  }

  /**
   * @see net.rptools.maptool.model.drawing.Drawable#draw(java.awt.Graphics2D, net.rptools.maptool.model.drawing.Pen)
   */
  public void draw(Graphics2D aG, Pen aPen) {
    ZoneRenderer renderer = MapTool.getFrame().getZoneRenderer(zoneId);
    Image image = ImageManager.getImage(AssetManager.getAsset(assetId), renderer);
    aG.drawImage(image, bounds.x, bounds.y, renderer);
  }

  /**
   * @see net.rptools.maptool.model.drawing.Drawable#getBounds()
   */
  public Rectangle getBounds() {
    return bounds;
  }

  /**
   * @see net.rptools.maptool.model.drawing.Drawable#getId()
   */
  public GUID getId() {
    return id;
  }
}
