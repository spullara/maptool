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
import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.util.FileUtil;

/**
 * Model for arranging assets in a hierarchical way
 */
public class AssetGroup {

    private String name;
    private File location;
    
    private boolean loaded;
    
    private List<Asset> assetList = new ArrayList<Asset>();
    private List<AssetGroup> assetGroupList = new ArrayList<AssetGroup>();

    private static final Comparator GROUP_COMPARATOR = new AssetGroupComparator();
    
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
        
        // Keeps the groups ordered
        Collections.sort(assetGroupList, GROUP_COMPARATOR);
    }
    
    public void remove(AssetGroup group) {
        assetGroupList.remove(group);
    }
    
    public void add(Asset asset) {
        assetList.add(asset);
    }
    
    public void remove(Asset asset) {
        assetList.remove(asset.getId());
    }

    public String toString() {
        return "AssetGroup[" + name + "]";
    }
    
    private void loadData() {
    	
    	if (!loaded) {

    		assetList.clear();
    		assetGroupList.clear();
			
    		try {
	    		MapTool.startIndeterminateAction();
	    		
	    		// Update images for this group
	            File[] imageFileArray = location.listFiles(IMAGE_FILE_FILTER);
	            for (File file : imageFileArray) {
	                
	                // TODO: Check that group already has it
	                // TODO: don't create new assets for images that are already in the game
	            	try {
	            		assetList.add(new Asset(FileUtil.loadFile(file)));
	            	} catch (IOException ioe) {
	            		// TODO: Handle this better
	            		ioe.printStackTrace();
	            	}
	            }
	            
	            // Update subgroups
	            File[] subdirArray = location.listFiles(DIRECTORY_FILE_FILTER);
	            for (File subdir : subdirArray) {
	                
	                // TODO: re-use existing asset groups
	                // TODO: keep track of pathing information for change polling
	                AssetGroup subgroup = new AssetGroup(subdir, subdir.getName());
	                
	                assetGroupList.add(subgroup);
	            }    		
	            
	            Collections.sort(assetGroupList, GROUP_COMPARATOR);
	            
				//name = name + " (" + imageFileArray.length + ")";

    		} finally {
    			MapTool.endIndeterminateAction();
    		}
        	loaded = true;
    	}
    	
    }
    
    private static class AssetGroupComparator implements Comparator {
        
        public int compare(Object o1,Object o2) {
            
            assert o1 instanceof AssetGroup : "List must only contain AssetGroup objects";
            assert o2 instanceof AssetGroup : "List must only contain AssetGroup objects";
            
            return ((AssetGroup) o1).getName().toUpperCase().compareTo(((AssetGroup) o2).getName().toUpperCase());
        }
    }
}
