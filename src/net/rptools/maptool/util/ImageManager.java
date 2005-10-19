/* The MIT License
 * 
 * Copyright (c) 2005 David Rice, Trevor Croft
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
package net.rptools.maptool.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.rptools.lib.util.ImageUtil;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.Asset;


/**
 */
public class ImageManager {

    private static Map<MD5Key, BufferedImage> imageMap = new HashMap<MD5Key, BufferedImage>();
    
    public static BufferedImage UNKNOWN_IMAGE; 

    private static BackgroundImageLoader loader = new BackgroundImageLoader();
    
    static {
        
        try {
            UNKNOWN_IMAGE = ImageUtil.getImage("net/rptools/maptool/client/image/unknown.png");
        } catch (IOException ioe) {
            ioe.printStackTrace();
            UNKNOWN_IMAGE = ImageUtil.createCompatibleImage(10, 10, 0);
        }
        
        new Thread(loader).start();
    }
    
    public static BufferedImage getImage(Asset asset) {
        
        BufferedImage image = imageMap.get(asset.getId());
        if (image != null) {
            return image;
        }
        
        // Use placeholder until image is actually loaded
        imageMap.put(asset.getId(), UNKNOWN_IMAGE);
        loader.addAsset(asset);
        
        return UNKNOWN_IMAGE;
    }

    private static class BackgroundImageLoader implements Runnable {

        private List<Asset> assetQueue = Collections.synchronizedList(new LinkedList<Asset> ());

        public void addAsset(Asset asset) {

            synchronized(assetQueue) {
                assetQueue.add(asset);
                assetQueue.notify();
            }
        }
        
        public void run() {
            
            while (true) {

                if (assetQueue.size() > 0) {
                    
                    Asset asset = assetQueue.remove(0);
                    
                    try {
                        BufferedImage image = ImageUtil.createCompatibleImage(ImageUtil.bytesToImage(asset.getImage()));
                        
                        if (image != null) {
                            
                            // Replace placeholder with actual image
                            imageMap.put(asset.getId(), image);
                            
                            // OPTIMIZE: target the specific location to be redrawn
                            // TODO: this class should really not known anything about the MapTool
                            MapTool.getFrame().repaint();
                        }
                        
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }

                    // Look for more
                    continue;
                }
                
                // Wait for the next assets to load
                synchronized (assetQueue) {
                    try {
                        assetQueue.wait();
                    } catch (InterruptedException ie) {
                        // Nothing to do
                    }
                }
            }
            
        }
    }
        
}
