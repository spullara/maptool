/* The MIT License
 * 
 * Copyright (c) 2008 Jay Gorrell
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

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import net.rptools.lib.MD5Key;
import net.rptools.lib.image.ImageUtil;
import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.util.ImageManager;

/**
 * This is a token overlay that shows an image over the entire token
 * 
 * @author Jay
 */
public class ImageTokenOverlay extends TokenOverlay {

    /**
     * If of the image displayed in the overlay.
     */
    private MD5Key assetId;
    
    /**
     * Cached image for specific token sizes
     */
    private transient Map<Dimension, BufferedImage> imageCache;

    /**
     * Logger instance for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(ImageTokenOverlay.class.getName());
    
    /**
     * Needed for serialization
     */
    public ImageTokenOverlay() {
        this(TokenOverlay.DEFAULT_STATE_NAME, null);
    }
    
    /**
     * Create the complete image overlay.
     * 
     * @param name Name of the new token overlay
     * @param anAssetId Id of the image displayed in the new token overlay.
     */
    public ImageTokenOverlay(String name, MD5Key anAssetId) {
        super(name);
        assetId = anAssetId;
    }
    
    /**
     * @see net.rptools.maptool.client.ui.token.TokenOverlay#clone()
     */
    @Override
    public Object clone() {
        TokenOverlay overlay = new ImageTokenOverlay(getName(), assetId);
        overlay.setOrder(getOrder());
        overlay.setGroup(getGroup());
        overlay.setMouseover(isMouseover());
        overlay.setOpacity(getOpacity());
        return overlay;
    }

    /**
     * @see net.rptools.maptool.client.ui.token.TokenOverlay#paintOverlay(java.awt.Graphics2D, net.rptools.maptool.model.Token, java.awt.Rectangle)
     */
    @Override
    public void paintOverlay(Graphics2D g, Token token, Rectangle bounds) {
        
        // Get the image
        Rectangle iBounds = getImageBounds(bounds, token);
        Dimension d = iBounds.getSize();
        BufferedImage image = null;
        if (imageCache != null)
          image = imageCache.get(d);
        if (image == null) {
            
            // Not in the cache, create it.
            Asset asset = AssetManager.getAsset(assetId);
            if (asset == null) {
                LOGGER.warning("Unable to locate and asset with ID: " + assetId);
                return;
            } // endif
            image = ImageManager.getImageAndWait(asset);
            Dimension size = new Dimension(image.getWidth(), image.getHeight());
            SwingUtil.constrainTo(size, d.width, d.height);
            image = ImageUtil.createCompatibleImage(image, size.width, size.height, null);
            if (imageCache == null) imageCache = new HashMap<Dimension, BufferedImage>();
            imageCache.put(d, image);
        } // endif
        
        // Paint it at the right location
        int width = image.getWidth();
        int height = image.getHeight();
        int x = iBounds.x + (d.width - width) / 2;
        int y = iBounds.y + (d.height - height) / 2;
        Composite tempComposite = g.getComposite();        
        if (getOpacity() != 100)
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)getOpacity()/100));
        g.drawImage(image, x, y, null);
        g.setComposite(tempComposite);
    }

    /** @return Getter for assetId */
    public MD5Key getAssetId() {
        return assetId;
    }

    /**
     * Calculate the image bounds from the token bounds
     * 
     * @param bounds Bounds of the token passed to the overlay.
     * @param token Token being decorated.
     * @return The bounds w/in the token where the image is painted.
     */
    protected Rectangle getImageBounds(Rectangle bounds, Token token) {
        return bounds;
    }
}
