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

import net.rptools.lib.MD5Key;
import net.rptools.maptool.model.Token;

/**
 * Token overlay for bar meters.
 * 
 * @author Jay
 */
public class SingleImageBarTokenOverlay extends BarTokenOverlay {

    /**
     * ID of the bar image displayed in the overlay.
     */
    private MD5Key assetId;
    
    /**
     * Cached image for specific token sizes
     */
    private transient Map<Dimension, BufferedImage> imageCache;

    /**
     * Needed for serialization
     */
    public SingleImageBarTokenOverlay() {
        this(AbstractTokenOverlay.DEFAULT_STATE_NAME, null);
    }
    
    /**
     * Create the complete image overlay.
     * 
     * @param name Name of the new token overlay
     * @param theAssetId Id of the bar image
     */
    public SingleImageBarTokenOverlay(String name, MD5Key theAssetId) {
        super(name);
        assetId = theAssetId;
    }
    
    /**
     * @see net.rptools.maptool.client.ui.token.AbstractTokenOverlay#clone()
     */
    @Override
    public Object clone() {
        BarTokenOverlay overlay = new SingleImageBarTokenOverlay(getName(), assetId);
        overlay.setOrder(getOrder());
        overlay.setGroup(getGroup());
        overlay.setMouseover(isMouseover());
        overlay.setOpacity(getOpacity());
        overlay.setIncrements(getIncrements());
        overlay.setSide(getSide());
        overlay.setShowGM(isShowGM());
        overlay.setShowOwner(isShowOthers());
        overlay.setShowOthers(isShowOthers());
        return overlay;
    }

    /**
     * @see net.rptools.maptool.client.ui.token.BarTokenOverlay#paintOverlay(java.awt.Graphics2D, net.rptools.maptool.model.Token, java.awt.Rectangle, double)
     */
    @Override
    public void paintOverlay(Graphics2D g, Token token, Rectangle bounds, double value) {

        // Get the images
        Dimension d = bounds.getSize();
        BufferedImage image = null;
        if (imageCache != null)
          image = imageCache.get(d);
        if (image == null) {
            
            // Not in the cache, create it.
            image = getScaledImage(assetId, d);
            if (imageCache == null) imageCache = new HashMap<Dimension, BufferedImage>();
            imageCache.put(d, image);
        } // endif
        
        // Find the position of the images according to the size and side where they are placed
        int x = 0;
        int y = 0;
        switch (getSide()) {
        case RIGHT:
            x = d.width - image.getWidth();
            break;
        case BOTTOM:
            y = d.height - image.getHeight();
        } // endswitch
        Composite tempComposite = g.getComposite();        
        if (getOpacity() != 100)
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)getOpacity()/100));
        int width = (getSide() == Side.TOP || getSide() == Side.BOTTOM) ? calcBarSize(image.getWidth(), value) : image.getWidth();
        int height = (getSide() == Side.LEFT || getSide() == Side.RIGHT) ? calcBarSize(image.getHeight(), value) : image.getHeight();
        g.drawImage(image, x, y, x + width, y + height, 0, 0, width, height, null);
        g.setComposite(tempComposite);
    }

    /** @return Getter for assetId */
    public MD5Key getAssetId() {
        return assetId;
    }

    /** @param topAssetId Setter for assetId */
    public void setAssetId(MD5Key topAssetId) {
        this.assetId = topAssetId;
    }
}
