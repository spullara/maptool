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
public class TwoImageBarTokenOverlay extends BarTokenOverlay {

    /**
     * ID of the base image displayed in the overlay.
     */
    private MD5Key bottomAssetId;
    
    /**
     * ID of the top image displayed in the overlay.
     */
    private MD5Key topAssetId;
    
    /**
     * Cached image for specific token sizes
     */
    private transient Map<Dimension, BufferedImage[]> imageCache;

    /**
     * Needed for serialization
     */
    public TwoImageBarTokenOverlay() {
        this(AbstractTokenOverlay.DEFAULT_STATE_NAME, null, null);
    }
    
    /**
     * Create the complete image overlay.
     * 
     * @param name Name of the new token overlay
     * @param theTopAssetId Id of the image displayed to show the bar
     * @param theBottomAssetId Id of the base image.
     */
    public TwoImageBarTokenOverlay(String name, MD5Key theTopAssetId, MD5Key theBottomAssetId) {
        super(name);
        topAssetId = theTopAssetId;
        bottomAssetId = theBottomAssetId;
    }
    
    /**
     * @see net.rptools.maptool.client.ui.token.AbstractTokenOverlay#clone()
     */
    @Override
    public Object clone() {
        BarTokenOverlay overlay = new TwoImageBarTokenOverlay(getName(), topAssetId, bottomAssetId);
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
        BufferedImage[] images = null;
        if (imageCache != null)
          images = imageCache.get(d);
        if (images == null) {
            
            // Not in the cache, create it.
            images = new BufferedImage[2];
            images[0] = getScaledImage(topAssetId, d);
            images[1] = getScaledImage(bottomAssetId, d);
            if (imageCache == null) imageCache = new HashMap<Dimension, BufferedImage[]>();
            imageCache.put(d, images);
        } // endif
        
        // Find the position of the images according to the size and side where they are placed
        int x = 0;
        int y = 0;
        switch (getSide()) {
        case RIGHT:
            x = d.width - images[0].getWidth();
            break;
        case BOTTOM:
            y = d.height - images[0].getHeight();
        } // endswitch
        Composite tempComposite = g.getComposite();        
        if (getOpacity() != 100)
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)getOpacity()/100));
        g.drawImage(images[1], x, y, null);
        int width = (getSide() == Side.TOP || getSide() == Side.BOTTOM) ? calcBarSize(images[0].getWidth(), value) : images[0].getWidth();
        int height = (getSide() == Side.LEFT || getSide() == Side.RIGHT) ? calcBarSize(images[0].getHeight(), value) : images[0].getHeight();
        g.drawImage(images[0], x, y, x + width, y + height, 0, 0, width, height, null);
        g.setComposite(tempComposite);
    }

    /** @return Getter for bottomAssetId */
    public MD5Key getBottomAssetId() {
        return bottomAssetId;
    }

    /** @param bottomAssetId Setter for bottomAssetId */
    public void setBottomAssetId(MD5Key bottomAssetId) {
        this.bottomAssetId = bottomAssetId;
    }

    /** @return Getter for topAssetId */
    public MD5Key getTopAssetId() {
        return topAssetId;
    }

    /** @param topAssetId Setter for topAssetId */
    public void setTopAssetId(MD5Key topAssetId) {
        this.topAssetId = topAssetId;
    }
}
