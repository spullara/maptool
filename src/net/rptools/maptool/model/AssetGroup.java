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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Model for arranging assets in a hierarchical way
 */
public class AssetGroup {

    private String name;
    
    private List<Asset> assetList = new ArrayList<Asset>();
    private List<AssetGroup> assetGroupList = new ArrayList<AssetGroup>();

    private static final Comparator GROUP_COMPARATOR = new AssetGroupComparator();
    
    public AssetGroup(String name) {
        
        assert name != null : "Name cannot be null";
        
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean hasChildGroups() {
        return assetGroupList.size() > 0;
    }
    
    public boolean hasAssets() {
        return assetList.size() > 0;
    }

    public int getChildGroupCount() {
        return assetGroupList.size();
    }
    
    public int getAssetCount() {
        return assetList.size();
    }
    
    public int indexOf(Asset asset) {
        return assetList.indexOf(asset);
    }
    
    public int indexOf(AssetGroup group) {
        return assetGroupList.indexOf(group);
    }
    
    /**
     */
    public List<AssetGroup> getChildGroups() {
        return Collections.unmodifiableList(assetGroupList);
    }
    
    /**
     */
    public List<Asset> getAssets() {
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
    
    private static class AssetGroupComparator implements Comparator {
        
        public int compare(Object o1,Object o2) {
            
            assert o1 instanceof AssetGroup : "List must only contain AssetGroup objects";
            assert o2 instanceof AssetGroup : "List must only contain AssetGroup objects";
            
            return ((AssetGroup) o1).getName().toUpperCase().compareTo(((AssetGroup) o2).getName().toUpperCase());
        }
    }
}
