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
package net.rptools.maptool.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.rptools.lib.MD5Key;
import net.rptools.lib.image.ImageUtil;
import net.rptools.maptool.model.Asset;


/**
 */
public class ImageManager {

    private static Map<MD5Key, BufferedImage> imageMap = new HashMap<MD5Key, BufferedImage>();
    
    public static BufferedImage UNKNOWN_IMAGE; 
    public static BufferedImage BROKEN_IMAGE;

    private static ExecutorService smallImageLoader = Executors.newFixedThreadPool(3);
    private static ExecutorService largeImageLoader = Executors.newFixedThreadPool(1);
    private static Map<MD5Key, Set<ImageObserver>> imageObserverMap = new ConcurrentHashMap<MD5Key, Set<ImageObserver>>();
    
    static {
        
        try {
            UNKNOWN_IMAGE = ImageUtil.getCompatibleImage("net/rptools/maptool/client/image/unknown.png");
        } catch (IOException ioe) {
            ioe.printStackTrace();
            UNKNOWN_IMAGE = ImageUtil.createCompatibleImage(10, 10, 0);
        }
        
        try {
            BROKEN_IMAGE = ImageUtil.getCompatibleImage("net/rptools/maptool/client/image/broken.png");
        } catch (IOException ioe) {
            ioe.printStackTrace();
            BROKEN_IMAGE = ImageUtil.createCompatibleImage(10, 10, 0);
        }
        
    }
    
    public static void flush() {
    	imageMap.clear();
    }
    
    public static BufferedImage getImageAndWait(Asset asset) {
    	return getImageAndWait(asset, null);
    }
    public static BufferedImage getImageAndWait(Asset asset, Map<String, Object> hintMap) {
      
    	// Null asset causes the loadLatch.await() to hang
    	if (asset == null) {
    		return UNKNOWN_IMAGE;
    	}

    	final CountDownLatch loadLatch = new CountDownLatch(1);
    	BufferedImage image = getImage(asset, new ImageObserver(){
    		public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
    			// If we're here then the image has just finished loading
    			// release the blocked thread
    			loadLatch.countDown();
    			return false;
    		}
    	});
    	
    	if (image == UNKNOWN_IMAGE) {
    		try {
    			synchronized (loadLatch) {
    				loadLatch.await();
    			}
    			
    			// This time we'll get the cached version
    			image = getImage(asset);
    		} catch (InterruptedException ie) {
    			image = BROKEN_IMAGE;
    		}
    	}
    	
    	return image;
    }
    public static BufferedImage getImage(Asset asset, ImageObserver... observers) {
    	return getImage(asset, null, observers);
    }
    public static BufferedImage getImage(Asset asset, Map<String, Object> hints, ImageObserver... observers) {
    	
    	if (asset == null) {
    		return UNKNOWN_IMAGE;
    	}
    	
        BufferedImage image = imageMap.get(asset.getId());
        
        // Another request for the same asset ?
        if (image == UNKNOWN_IMAGE) {
            addObservers(asset.getId(), observers);
        }
        
        // Cached
        if (image != null) {
            return image;
        }
        
        // Use placeholder until image is actually loaded
        imageMap.put(asset.getId(), UNKNOWN_IMAGE);
        
        addObservers(asset.getId(), observers);
        
        if (asset.getImage().length > 128 * 1024) {  // 128k is a good "small" size image
            largeImageLoader.execute(new BackgroundImageLoader(asset, hints));
        } else {
            smallImageLoader.execute(new BackgroundImageLoader(asset, hints));
        	
        }
        
        return UNKNOWN_IMAGE;
    }

    public static void flushImage(Asset asset) {
    	flushImage(asset.getId());
    }
    public static void flushImage(MD5Key assetId) {
    	// LATER: investigate how this effects images that are already in progress
    	imageMap.remove(assetId);
    }
    
    public static void addObservers(MD5Key assetId, ImageObserver... observers) {

        if (observers == null || observers.length == 0) {
            return;
        }
        
        Set<ImageObserver> observerSet = imageObserverMap.get(assetId);
        if (observerSet == null) {
            observerSet = new HashSet<ImageObserver>();
            imageObserverMap.put(assetId, observerSet);
        }
        
        for (ImageObserver observer : observers) {
            observerSet.add(observer);
        }
    }
    
    private static class BackgroundImageLoader implements Runnable {

        private Asset asset;
        private Map<String, Object> hints;
        
        public BackgroundImageLoader(Asset asset, Map<String, Object> hints) {
            this.asset = asset;
            this.hints = hints;
        }
        
        public void run() {
            
        	BufferedImage image = null;
            try {
                image = ImageUtil.createCompatibleImage(ImageUtil.bytesToImage(asset.getImage()), hints);
                
            } catch (IOException ioe) {
                ioe.printStackTrace();
                image = BROKEN_IMAGE;
            }

            // Replace placeholder with actual image
            imageMap.put(asset.getId(), image);
            
            // Notify observers
            Set<ImageObserver> observerSet = imageObserverMap.remove(asset.getId());
            if (observerSet != null) {
                for (ImageObserver observer : observerSet) {
                    observer.imageUpdate(image, ImageObserver.ALLBITS, 0, 0, image.getWidth(), image.getHeight());
                }
            }
        }
    }
        
}
