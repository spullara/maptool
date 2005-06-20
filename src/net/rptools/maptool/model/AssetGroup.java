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
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.rptools.common.util.FileUtil;
import net.rptools.maptool.client.MapTool;

/**
 * Model for arranging assets in a hierarchical way
 */
public class AssetGroup {

    private String name;
    private File location;
    
    private boolean loaded;
    
    // Asset refresh data
    private Map<File, Object[]> assetFiles = new HashMap<File, Object[]>();
    
    // Group refresh data
    private Map<File, AssetGroup> assetGroupFiles = new HashMap<File, AssetGroup>();
    
    private List<Asset> assetList = new ArrayList<Asset>();
    private List<AssetGroup> assetGroupList = new ArrayList<AssetGroup>();

    private static final Comparator<AssetGroup> GROUP_COMPARATOR = new AssetGroupComparator();
    
    private static final FilenameFilter IMAGE_FILE_FILTER = new FilenameFilter() {
        public boolean accept(File dir,String name) {
            name = name.toLowerCase();
            return name.endsWith(".bmp") ||
                    name.endsWith(".png") ||
                    name.endsWith(".jpg") ||
                    name.endsWith(".jpeg") ||
                    name.endsWith(".gif");
        }
    };
    
    private static final FilenameFilter DIRECTORY_FILE_FILTER = new FilenameFilter() {
        public boolean accept(File dir,String name) {
            return new File(dir.getPath() + File.separator + name).isDirectory();
        }
    };

    public AssetGroup(File location, String name) {
        
        assert name != null : "Name cannot be null";

        this.location = location;
        this.name = name;
        
        loaded = false;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean hasChildGroups() {
    	loadData();
        return assetGroupList.size() > 0;
    }
    
    public boolean hasAssets() {
    	loadData();
        return assetList.size() > 0;
    }

    public int getChildGroupCount() {
    	loadData();
        return assetGroupList.size();
    }
    
    public int getAssetCount() {
    	loadData();
        return assetList.size();
    }
    
    public int indexOf(Asset asset) {
    	loadData();
        return assetList.indexOf(asset);
    }
    
    public int indexOf(AssetGroup group) {
    	loadData();
        return assetGroupList.indexOf(group);
    }
    
    /**
     */
    public List<AssetGroup> getChildGroups() {
    	loadData();
        return Collections.unmodifiableList(assetGroupList);
    }
    
    /**
     */
    public List<Asset> getAssets() {
    	loadData();
        return Collections.unmodifiableList(assetList);
    }
    
    public void add(AssetGroup group) {
        assetGroupList.add(group);
        assetGroupFiles.put(group.location, group);
        
        // Keeps the groups ordered
        Collections.sort(assetGroupList, GROUP_COMPARATOR);
    }
    
    public void remove(AssetGroup group) {
        assetGroupList.remove(group);
        assetGroupFiles.remove(group.location);
    }
    
    public String toString() {
        return "AssetGroup[" + name + "]: " + assetList.size() + " assets and " + assetGroupList.size() + " groups";
    }
    
    /**
     * Release the assets and groups so that they can be garbage collected.
     */
    private void clear() {
      assetFiles.clear();
      assetGroupFiles.clear();
      assetList.clear();
      for (AssetGroup group : assetGroupList) group.clear();
    }
    
    private void loadData() {
        
    	if (!loaded) {

        // Copy the asset and group files map so that files that were deleted go away.
        Map<File, Object[]> tempAssetFiles = assetFiles;
        assetFiles = new HashMap<File, Object[]>();
        Map<File, AssetGroup> tempAssetGroupFiles = assetGroupFiles;
        assetGroupFiles = new HashMap<File, AssetGroup>();

        assetList.clear();
    		assetGroupList.clear();
			
    		try {
	    		MapTool.startIndeterminateAction();
	    		
	    		// Update images for this group
	            File[] imageFileArray = location.listFiles(IMAGE_FILE_FILTER);
	            for (File file : imageFileArray) {
                
                // Latest file already in the group?
                Object[] data = tempAssetFiles.get(file);
                if (data != null && ((Long)data[0]).longValue() == file.lastModified()) {
                  assetFiles.put(file, data);
                  tempAssetFiles.remove(file);
                  assetList.add((Asset)data[1]);
                  continue;
                }

                // Get the asset, is it already in the game?
	            	try {
	            		Asset asset = new Asset(FileUtil.loadFile(file));
                  if (AssetManager.hasAsset(asset.getId())) {
                    asset = AssetManager.getAsset(asset.getId());
                  }
                  
                  // Add the asset
                  assetFiles.put(file, new Object[] {new Long(file.lastModified()), asset});
                  assetList.add(asset);
	            	} catch (IOException ioe) {
	            		// TODO: Handle this better
	            		ioe.printStackTrace();
	            	}
	            }
	            
	            // Update subgroups
	            File[] subdirArray = location.listFiles(DIRECTORY_FILE_FILTER);
	            for (File subdir : subdirArray) {
	                
                // Get the group or create a new one
                AssetGroup subgroup = tempAssetGroupFiles.get(subdir);
                if (subgroup == null) {
                  subgroup = new AssetGroup(subdir, subdir.getName());
                } else {
                  tempAssetGroupFiles.remove(subdir);
                }
	                
                assetGroupFiles.put(subdir, subgroup);
	              assetGroupList.add(subgroup);
	            }
	            
	            Collections.sort(assetGroupList, GROUP_COMPARATOR);
	            
				//name = name + " (" + imageFileArray.length + ")";

    		} finally {
          // Cleanup
          tempAssetFiles.clear();
          for (AssetGroup group : tempAssetGroupFiles.values()) group.clear();
          tempAssetGroupFiles.clear();
    			MapTool.endIndeterminateAction();          
    		}
        	loaded = true;
    	}
    	
    }
    
    /**
     * This method will cause the assets to be updated the next time one is read.
     * The child groups are updated as well.
     */
    public void updateGroup() {
      loaded = false;
      for (AssetGroup group : assetGroupList) group.updateGroup();
    }
    
    private static class AssetGroupComparator implements Comparator<AssetGroup> {
        
        public int compare(AssetGroup o1,AssetGroup o2) {
            return o1.getName().toUpperCase().compareTo(o2.getName().toUpperCase());
        }
    }
}
