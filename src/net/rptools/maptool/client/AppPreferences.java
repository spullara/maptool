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
package net.rptools.maptool.client;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import net.rptools.maptool.model.GridFactory;


public class AppPreferences {

    private static Preferences prefs = Preferences.userRoot().node(AppConstants.APP_NAME + "/prefs");        

    private static final String KEY_ASSET_ROOTS = "assetRoots";
    private static final String KEY_SAVE_DIR = "saveDir";
    private static final String KEY_LOAD_DIR = "loadDir";
    
    private static final String KEY_USE_TRANSLUCENT_FOG = "useTranslucentFog";
    private static final boolean DEFAULT_USE_TRANSLUCENT_FOG = false;
    
    private static final String KEY_NEW_MAPS_HAVE_FOW = "newMapsHaveFow";
    private static final boolean DEFAULT_NEW_MAPS_HAVE_FOW = false;
    
    private static final String KEY_NEW_TOKENS_VISIBLE = "newTokensVisible";
    private static final boolean DEFAULT_NEW_TOKENS_VISIBLE = true;
    
    private static final String KEY_NEW_MAPS_VISIBLE = "newMapsVisible";
    private static final boolean DEFAULT_NEW_MAPS_VISIBLE = true;
    
    private static final String KEY_TOKENS_START_SNAP_TO_GRID = "newTokensStartSnapToGrid";
    private static final boolean DEFAULT_TOKENS_START_SNAP_TO_GRID = true;

    private static final String KEY_STAMPS_START_SNAP_TO_GRID = "newStampsStartSnapToGrid";
    private static final boolean DEFAULT_STAMPS_START_SNAP_TO_GRID = false;
    
    private static final String KEY_STAMPS_START_FREESIZE = "newStampsStartFreesize";
    private static final boolean DEFAULT_STAMPS_START_FREESIZE = true;

    private static final String KEY_BACKGROUNDS_START_SNAP_TO_GRID = "newBackgroundsStartSnapToGrid";
    private static final boolean DEFAULT_BACKGROUNDS_START_SNAP_TO_GRID = false;
    
    private static final String KEY_BACKGROUNDS_START_FREESIZE = "newBackgroundsStartFreesize";
    private static final boolean DEFAULT_BACKGROUNDS_START_FREESIZE = true;

    private static final String KEY_DEFAULT_GRID_TYPE = "defaultGridType";
    private static final String DEFAULT_DEFAULT_GRID_TYPE = GridFactory.SQUARE;
    
    private static final String KEY_DEFAULT_GRID_SIZE = "defaultGridSize";
    private static final int DEFAULT_DEFAULT_GRID_SIZE = 40;
    
    public static void setDefaultGridSize(int size) {
    	prefs.putInt(KEY_DEFAULT_GRID_SIZE, size);
    }
    
    public static int getDefaultGridSize() {
    	return prefs.getInt(KEY_DEFAULT_GRID_SIZE, DEFAULT_DEFAULT_GRID_SIZE);
    }
    
    public static void setUseTranslucentFog(boolean flag) {
    	prefs.putBoolean(KEY_USE_TRANSLUCENT_FOG, flag);
    }
    
    public static boolean getUseTranslucentFog() {
    	return prefs.getBoolean(KEY_USE_TRANSLUCENT_FOG, DEFAULT_USE_TRANSLUCENT_FOG);
    }
    
    public static void setNewMapsHaveFOW(boolean flag) {
    	prefs.putBoolean(KEY_NEW_MAPS_HAVE_FOW, flag);
    }
    
    public static boolean getNewMapsHaveFOW() {
    	return prefs.getBoolean(KEY_NEW_MAPS_HAVE_FOW, DEFAULT_NEW_MAPS_HAVE_FOW);
    }
    
    public static void setNewTokensVisible(boolean flag) {
    	prefs.putBoolean(KEY_NEW_TOKENS_VISIBLE, flag);
    }
    
    public static boolean getNewTokensVisible() {
    	return prefs.getBoolean(KEY_NEW_TOKENS_VISIBLE, DEFAULT_NEW_TOKENS_VISIBLE);
    }
    
    public static void setNewMapsVisible(boolean flag) {
    	prefs.putBoolean(KEY_NEW_MAPS_VISIBLE, flag);
    }
    
    public static boolean getNewMapsVisible() {
    	return prefs.getBoolean(KEY_NEW_MAPS_VISIBLE, DEFAULT_NEW_MAPS_VISIBLE);
    }
    
    public static void setTokensStartSnapToGrid(boolean flag) {
    	prefs.putBoolean(KEY_TOKENS_START_SNAP_TO_GRID, flag);
    }
    
    public static boolean getTokensStartSnapToGrid() {
    	return prefs.getBoolean(KEY_TOKENS_START_SNAP_TO_GRID, DEFAULT_TOKENS_START_SNAP_TO_GRID);
    }
    
    public static void setStampsStartSnapToGrid(boolean flag) {
    	prefs.putBoolean(KEY_STAMPS_START_SNAP_TO_GRID, flag);
    }
    
    public static boolean getStampsStartSnapToGrid() {
    	return prefs.getBoolean(KEY_STAMPS_START_SNAP_TO_GRID, DEFAULT_STAMPS_START_SNAP_TO_GRID);
    }
    
    public static void setStampsStartFreesize(boolean flag) {
    	prefs.putBoolean(KEY_STAMPS_START_FREESIZE, flag);
    }
    
    public static boolean getStampsStartFreesize() {
    	return prefs.getBoolean(KEY_STAMPS_START_FREESIZE, DEFAULT_STAMPS_START_FREESIZE);
    }
    
    public static void setBackgroundsStartSnapToGrid(boolean flag) {
    	prefs.putBoolean(KEY_BACKGROUNDS_START_SNAP_TO_GRID, flag);
    }
    
    public static boolean getBackgroundsStartSnapToGrid() {
    	return prefs.getBoolean(KEY_BACKGROUNDS_START_SNAP_TO_GRID, DEFAULT_BACKGROUNDS_START_SNAP_TO_GRID);
    }
    
    public static void setBackgroundsStartFreesize(boolean flag) {
    	prefs.putBoolean(KEY_BACKGROUNDS_START_FREESIZE, flag);
    }
    
    public static boolean getBackgroundsStartFreesize() {
    	return prefs.getBoolean(KEY_BACKGROUNDS_START_FREESIZE, DEFAULT_BACKGROUNDS_START_FREESIZE);
    }
    
    public static String getDefaultGridType() {
    	return prefs.get(KEY_DEFAULT_GRID_TYPE, DEFAULT_DEFAULT_GRID_TYPE);
    }
    
    public static void setDefaultGridType(String type) {
    	prefs.put(KEY_DEFAULT_GRID_TYPE, type);
    }
    
    public static void clearAssetRoots() {
        prefs.put(KEY_ASSET_ROOTS, "");
    }
    
    public static void setSaveDir(File file) {
    	prefs.put(KEY_SAVE_DIR, file.toString());
    }
    
    public static File getSaveDir() {
    	String filePath = prefs.get(KEY_SAVE_DIR, null);
    	return filePath != null ? new File(filePath) : new File("/");
    }
    
    public static void setLoadDir(File file) {
    	prefs.put(KEY_LOAD_DIR, file.toString());
    }
    
    public static File getLoadDir() {
    	String filePath = prefs.get(KEY_LOAD_DIR, null);
    	return filePath != null ? new File(filePath) : new File("/");
    }
    
    public static void addAssetRoot(File root) {
        String list = prefs.get(KEY_ASSET_ROOTS, "");
        if (list.length() > 0) {
            list += ";";
        }
        
        list += root.getPath();
        
        prefs.put(KEY_ASSET_ROOTS, list);
    }
    
    public static List<File> getAssetRoots() {
        
        String list = prefs.get(KEY_ASSET_ROOTS, "");
        String[] roots = list.split(";");
        
        // TODO: This should really be a set to remove dups
        List<File> rootList = new ArrayList<File>();
        for (String root : roots) {
            File file = new File(root);

            // LATER: Should this actually remove it from the pref list ? 
            if (!file.exists()) {
                continue;
            }

            if (!rootList.contains(file)) {
            	
                rootList.add(file);
            }
        }
        
        return rootList;
    }
    
    public static void removeAssetRoot(File root) {
        
        List<File> rootList = getAssetRoots();
        rootList.remove(root);
        
        clearAssetRoots();
        
        for (File dir : rootList) {
            addAssetRoot(dir);
        }
    }
}
