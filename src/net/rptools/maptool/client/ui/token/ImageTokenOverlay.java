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
