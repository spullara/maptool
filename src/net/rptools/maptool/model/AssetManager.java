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
package net.rptools.maptool.model;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import net.rptools.lib.FileUtil;
import net.rptools.lib.MD5Key;
import net.rptools.maptool.client.AppUtil;
import net.rptools.maptool.client.MapTool;

/**
 */
public class AssetManager {

	private static Map<MD5Key, Asset> assetMap = new HashMap<MD5Key, Asset>();
	private static File cacheDir;
	private static boolean usePersistentCache;
	
    private static Asset lastRetrievedAsset;

    private static Map<MD5Key, List<AssetAvailableListener>> assetListenerListMap  = new ConcurrentHashMap<MD5Key, List<AssetAvailableListener>>();
    
    public static final String NAME = "name";
    
    private static AssetLoader assetLoader = new AssetLoader();
    
	static {
		
		cacheDir = AppUtil.getAppHome("assetcache");
		if (cacheDir != null) {
			usePersistentCache = true;
		}
	}
	
	public static void updateRepositoryList() {
		assetLoader.removeAllRepositories();
		for (String repo : MapTool.getCampaign().getRemoteRepositoryList()) {
			assetLoader.addRepository(repo);
		}
	}
	
	public static boolean isAssetRequested(MD5Key key) {
		return assetLoader.isIdRequested(key);
	}
	
	public static void addAssetListener(MD5Key key, AssetAvailableListener listener) {

		List<AssetAvailableListener> listenerList = assetListenerListMap.get(key);
		if (listenerList == null) {
			listenerList = new LinkedList<AssetAvailableListener>();
			assetListenerListMap.put(key, listenerList);
		}
		
		listenerList.add(listener);
	}
	
    public static Asset getLastRetrievedAsset() {
        return lastRetrievedAsset;
    }
    
	public static boolean hasAsset(Asset asset) {
		return hasAsset(asset.getId());
	}
	
	public static boolean hasAsset(MD5Key key) {
		return assetMap.containsKey(key) || assetIsInPersistentCache(key) || assetHasLocalReference(key);
	}

    public static boolean hasAssetInMemory(MD5Key key) {
        return assetMap.containsKey(key);
    }
    
	public static void putAsset(Asset asset) {
		if (asset == null) {
			return;
		}
		
		assetMap.put(asset.getId(), asset);

		// Invalid images are represented by empty assets.  
		// Don't persist those
		if (asset.getImage().length > 0) {
			putInPersistentCache(asset);
		}

		// Clear the waiting status
		assetLoader.completeRequest(asset.getId());
		
		// Listeners
		List<AssetAvailableListener> listenerList = assetListenerListMap.get(asset.getId());
		if (listenerList != null) {
			for (AssetAvailableListener listener : listenerList) {
				listener.assetAvailable(asset.getId());
			}
			
			assetListenerListMap.remove(asset.getId());
		}
	}
	
	public static Asset getAsset(MD5Key id) {
		if (id == null) {
			return null;
		}
		
		Asset asset = assetMap.get(id);
		
		if (asset == null && usePersistentCache && assetIsInPersistentCache(id)) {
			
			asset = getFromPersistentCache(id);
		}
		if (asset == null && assetHasLocalReference(id)) {
			
			File imageFile = getLocalReference(id);

			if (imageFile != null) {

				try {
					String name = FileUtil.getNameWithoutExtension(imageFile);
					byte[] data = FileUtil.getBytes(imageFile);
	
					asset = new Asset(name, data);
					
					// Just to be sure the image didn't change
					if (!asset.getId().equals(id)) {
						throw new IOException ("Image reference did not match the requested image");
					}
					
					// Put it in the persistent cache so we'll find it faster next time
					putInPersistentCache(asset);
				} catch (IOException ioe) {
					// Log, but continue as if we didn't have a link
					ioe.printStackTrace();
				}
			}
		}
		if (asset == null && !isAssetRequested(id )) {
			requestAssetFromServer(id);
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

	private static void requestAssetFromServer(MD5Key id) {
		
		if (id != null) {
			assetLoader.requestAsset(id);
		}
	}
	
	private static Asset getFromPersistentCache(MD5Key id) {
		
		if (id == null || id.toString().length() == 0) {
			return null;
		}
		
		if (!assetIsInPersistentCache(id)) {
			return null;
		}
		
		File assetFile = getAssetCacheFile(id);
		
		try {
			byte[] data = FileUtil.loadFile(assetFile);
			Properties props = getAssetInfo(id);
			
			Asset asset = new Asset(props.getProperty(NAME), data);
			
			if ( !asset.getId().equals(id)) {
				System.err.println("MD5 for asset " + asset.getName() + " corrupted");
			}
			
			assetMap.put(id, asset);
			
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
			InputStream is = new FileInputStream(infoFile); 
			props.load(is);
			is.close();
			return props;
			
		} catch (Exception e) {
			return new Properties();
		}
	}
	
	private static void putInPersistentCache(final Asset asset) {
		
		if (!usePersistentCache) {
			return;
		}
		
		if (!assetIsInPersistentCache(asset)) {
			
			final File assetFile = getAssetCacheFile(asset);
			
                        new Thread() {
                            public void run() {
                                
                                try {
                                        assetFile.getParentFile().mkdirs();
                                        // Image
                                        OutputStream out = new FileOutputStream(assetFile);
                                        out.write(asset.getImage());
                                        out.close();

                                } catch (IOException ioe) {
                                        System.err.println("Could not persist asset: " + ioe);
                                        return;
                                }
                            }
                        }.start();
			
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
	
	private static File getLocalReference(MD5Key id) {

		File lnkFile = getAssetLinkFile(id);
		if (!lnkFile.exists()) {
			return null;
		}
		try {
			List<String> refList = FileUtil.getLines(lnkFile);
			
			for (String ref : refList) {
				File refFile = new File(ref);
				if (refFile.exists()) {
					return refFile;
				}
			}
			
		} catch (IOException ioe) {
			// Just so we know, but fall through to return false
			ioe.printStackTrace();
		}
		
		// Guess we don't have one
		return null;
	}
	
	/**
	 * Store a pointer to where we've seen this asset before.
	 */
	public static void rememberLocalImageReference(File image) throws IOException {
		
		MD5Key id = new MD5Key(new BufferedInputStream(new FileInputStream(image)));
		File lnkFile = getAssetLinkFile(id);

		// See if we know about this one already
		if (lnkFile.exists()) {
			
			List<String> referenceList = FileUtil.getLines(lnkFile);
			for (String ref : referenceList) {
				if (ref.equals(id.toString())) {
					
					// We already know about this one
					return;
				}
			}
		}
		
		// Keep track of this reference
		FileOutputStream out = new FileOutputStream(lnkFile, true); // For appending

		out.write((image.getAbsolutePath() + "\n").getBytes());
		
		out.close();
	}

	private static boolean assetHasLocalReference(MD5Key id) {
		
		return getLocalReference(id) != null;
	}
	
	private static boolean assetIsInPersistentCache(Asset asset) {
		return assetIsInPersistentCache(asset.getId());
	}
	
	private static boolean assetInfoIsInPersistentCache(Asset asset) {
		return getAssetInfoFile(asset.getId()).exists();
	}
	
	private static boolean assetIsInPersistentCache(MD5Key id) {

		return getAssetCacheFile(id).exists() && getAssetCacheFile(id).length() > 0;
	}
	
	public static File getAssetCacheFile(Asset asset) {
		return getAssetCacheFile(asset.getId());
	}

	public static File getAssetCacheFile(MD5Key id) {
		return new File (cacheDir.getAbsolutePath() + File.separator + id);
	}
	
	private static File getAssetInfoFile(Asset asset) {
		return getAssetInfoFile(asset.getId());
	}

	private static File getAssetInfoFile(MD5Key id) {
		return new File (cacheDir.getAbsolutePath() + File.separator + id + ".info");
	}

	private static File getAssetLinkFile(MD5Key id) {
		return new File (cacheDir.getAbsolutePath() + File.separator + id + ".lnk");
	}

	public static void searchForImageReferences(File rootDir, FilenameFilter fileFilter) {

		for (File file : rootDir.listFiles()) {
			
			if (file.isDirectory()) {
				searchForImageReferences(file, fileFilter);
				continue;
			}
			
			try {
				if (fileFilter.accept(rootDir, file.getName())) {
					if (MapTool.getFrame() != null) {
						MapTool.getFrame().setStatusMessage("Storing local image reference: " + file.getName());
					}
					rememberLocalImageReference(file);
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}

		// Done
		if (MapTool.getFrame() != null) {
			MapTool.getFrame().setStatusMessage("");
		}
	}

}
