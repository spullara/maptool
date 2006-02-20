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
