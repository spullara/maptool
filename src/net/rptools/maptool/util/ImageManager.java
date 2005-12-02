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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JComponent;

import net.rptools.lib.MD5Key;
import net.rptools.lib.image.ImageUtil;
import net.rptools.maptool.model.Asset;


/**
 */
public class ImageManager {

    private static Map<MD5Key, BufferedImage> imageMap = new HashMap<MD5Key, BufferedImage>();
    
    public static BufferedImage UNKNOWN_IMAGE; 
    public static BufferedImage BROKEN_IMAGE;

    private static ExecutorService imageLoader = Executors.newFixedThreadPool(3);
    private static Map<MD5Key, Set<JComponent>> imageObserverMap = new ConcurrentHashMap<MD5Key, Set<JComponent>>();
    
    static {
        
        try {
            UNKNOWN_IMAGE = ImageUtil.getImage("net/rptools/maptool/client/image/unknown.png");
        } catch (IOException ioe) {
            ioe.printStackTrace();
            UNKNOWN_IMAGE = ImageUtil.createCompatibleImage(10, 10, 0);
        }
        
        try {
            BROKEN_IMAGE = ImageUtil.getImage("net/rptools/maptool/client/image/broken.png");
        } catch (IOException ioe) {
            ioe.printStackTrace();
            BROKEN_IMAGE = ImageUtil.createCompatibleImage(10, 10, 0);
        }
        
    }
    
    public static BufferedImage getImage(Asset asset, JComponent observer) {

        BufferedImage image = imageMap.get(asset.getId());
        
        // Another request for the same asset ?
        if (image == UNKNOWN_IMAGE) {
            addObserver(asset.getId(), observer);
        }
        
        // Cached
        if (image != null) {
            return image;
        }
        
        // Use placeholder until image is actually loaded
        imageMap.put(asset.getId(), UNKNOWN_IMAGE);
        
        addObserver(asset.getId(), observer);
        imageLoader.execute(new BackgroundImageLoader(asset));
        
        return UNKNOWN_IMAGE;
    }

    public static void addObserver(MD5Key assetId, JComponent observer) {

        if (observer == null) {
            return;
        }
        
        Set<JComponent> observerSet = imageObserverMap.get(assetId);
        if (observerSet == null) {
            observerSet = new HashSet<JComponent>();
            imageObserverMap.put(assetId, observerSet);
        }
        observerSet.add(observer);
    }
    
    private static class BackgroundImageLoader implements Runnable {

        private Asset asset;
        public BackgroundImageLoader(Asset asset) {
            this.asset = asset;
        }
        
        public void run() {
            
            try {
                BufferedImage image = ImageUtil.createCompatibleImage(ImageUtil.bytesToImage(asset.getImage()));
                
                if (image != null) {
                    
                    // Replace placeholder with actual image
                    imageMap.put(asset.getId(), image);
                }
                
            } catch (IOException ioe) {
                ioe.printStackTrace();
                imageMap.put(asset.getId(), BROKEN_IMAGE);
            }
            
            // Notify observers
            Set<JComponent> observerSet = imageObserverMap.remove(asset.getId());
            if (observerSet != null) {
                for (JComponent observer : observerSet) {
                    observer.repaint();
                }
            }
        }
    }
        
}
