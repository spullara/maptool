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
package net.rptools.maptool.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import net.rptools.lib.FileUtil;
import net.rptools.lib.MD5Key;
import net.rptools.maptool.client.AppUtil;

/**
 */
public class AssetManager {

	private static Map<MD5Key, Asset> assetMap = new HashMap<MD5Key, Asset>();
	private static File cacheDir;
	private static boolean usePersistentCache;
	
    private static Asset lastRetrievedAsset;

    private static String NAME = "name";
    
	static {
		
		cacheDir = AppUtil.getAppHome("assetcache");
		if (cacheDir != null) {
			usePersistentCache = true;
		}
	}

    public static Asset getLastRetrievedAsset() {
        return lastRetrievedAsset;
    }
    
	public static boolean hasAsset(Asset asset) {
		return hasAsset(asset.getId());
	}
	
	public static boolean hasAsset(MD5Key key) {
		return assetMap.containsKey(key) || assetIsInPersistentCache(key);
	}

    public static boolean hasAssetInMemory(MD5Key key) {
        return assetMap.containsKey(key);
    }
    
	public static void putAsset(Asset asset) {
		if (asset == null) {
			return;
		}
		
		assetMap.put(asset.getId(), asset);
		
		putInPersistentCache(asset);
	}
	
	public static Asset getAsset(MD5Key id) {
		
		Asset asset = assetMap.get(id);
		
		if (asset == null && usePersistentCache) {
			
			asset = getFromPersistentCache(id);
		}
		
        lastRetrievedAsset = asset;
		return asset;
	}

	public static void removeAsset(MD5Key id) {
		assetMap.remove(id);
	}

	public static void setUsePersistentCache(boolean enable) {
		if (enable && cacheDir == null) {
			throw new IllegalArgumentException ("Could not enable persistent cache: no such directory");
		}

		usePersistentCache = enable;
	}
	
	private static Asset getFromPersistentCache(MD5Key id) {
		
		if (!assetIsInPersistentCache(id)) {
			return null;
		}
		
		File assetFile = getAssetCacheFile(id);
		
		try {
			byte[] data = FileUtil.loadFile(assetFile);
			Properties props = getAssetInfo(id);
			
			Asset asset = new Asset(props.getProperty(NAME), data);
			
			assetMap.put(asset.getId(), asset);
			
			return asset;
		} catch (IOException ioe) {
			System.err.println("Could not load asset from persistent cache: " + ioe);
			return null;
		}
		
	}
	
	public static Asset createAsset(File file) throws IOException {
		return  new Asset(FileUtil.getNameWithoutExtension(file), FileUtil.loadFile(file));
    }
	    
	public static Properties getAssetInfo(MD5Key id) {
		
		File infoFile = getAssetInfoFile(id);
		try {

			Properties props = new Properties();
			props.load(new FileInputStream(infoFile));
			return props;
			
		} catch (Exception e) {
			return new Properties();
		}
	}
	
	private static void putInPersistentCache(Asset asset) {
		
		if (!usePersistentCache) {
			return;
		}
		
		if (!assetIsInPersistentCache(asset)) {
			
			File assetFile = getAssetCacheFile(asset);
			
			try {
				// Image
				OutputStream out = new FileOutputStream(assetFile);
				out.write(asset.getImage());
				out.close();

			} catch (IOException ioe) {
				System.err.println("Could not persist asset: " + ioe);
				return;
			}
			
		}
		if (!assetInfoIsInPersistentCache(asset)) {

			File infoFile = getAssetInfoFile(asset);
			
			try {
				// Info
				OutputStream out = new FileOutputStream(infoFile);
				Properties props = new Properties();
				props.put (NAME, asset.getName() != null ? asset.getName() : "");
				props.store(out, "Asset Info");
				out.close();
				
			} catch (IOException ioe) {
				System.err.println("Could not persist asset: " + ioe);
				return;
			}
			
		}
	}
	
	private static boolean assetIsInPersistentCache(Asset asset) {
		return assetIsInPersistentCache(asset.getId());
	}
	
	private static boolean assetInfoIsInPersistentCache(Asset asset) {
		return getAssetInfoFile(asset.getId()).exists();
	}
	
	private static boolean assetIsInPersistentCache(MD5Key id) {

		return getAssetCacheFile(id).exists();
	}
	
	private static File getAssetCacheFile(Asset asset) {
		return getAssetCacheFile(asset.getId());
	}

	private static File getAssetCacheFile(MD5Key id) {
		return new File (cacheDir.getAbsolutePath() + File.separator + id);
	}
	
	private static File getAssetInfoFile(Asset asset) {
		return getAssetInfoFile(asset.getId());
	}

	private static File getAssetInfoFile(MD5Key id) {
		return new File (cacheDir.getAbsolutePath() + File.separator + id + ".info");
	}
}
