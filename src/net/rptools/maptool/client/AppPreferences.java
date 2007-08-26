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

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.prefs.Preferences;

import net.rptools.maptool.model.GridFactory;
import net.rptools.maptool.model.Token;


public class AppPreferences {

    private static Preferences prefs = Preferences.userRoot().node(AppConstants.APP_NAME + "/prefs");        

    private static final String KEY_ASSET_ROOTS = "assetRoots";
    private static final String KEY_SAVE_DIR = "saveDir";
    private static final String KEY_LOAD_DIR = "loadDir";
    private static final String KEY_MRU_CAMPAIGNS = "mruCampaigns";
    private static final String KEY_SAVED_PAINT_TEXTURES = "savedTextures";
    
    private static final String KEY_SAVE_REMINDER = "autoSaveIncrement";
    private static final boolean DEFAULT_SAVE_REMINDER = true;
    
    private static final String KEY_TOKEN_NUMBER_DISPLAY = "tokenNumberDisplayg";
    private static final String DEFAULT_TOKEN_NUMBER_DISPLAY = Token.NUM_ON_NAME;
    
    private static final String KEY_AUTO_SAVE_INCREMENT = "autoSaveIncrement";
    private static final int DEFAULT_AUTO_SAVE_INCREMENT = 5; // Minutes
    
    private static final String KEY_DUPLICATE_TOKEN_NUMBER = "duplicateTokenNumber";
    private static final String DEFAULT_DUPLICATE_TOKEN_NUMBER = Token.NUM_INCREMENT;
    
    private static final String KEY_NEW_TOKEN_NAMING = "newTokenNaming";
    private static final String DEFAULT_NEW_TOKEN_NAMING = Token.NAME_USE_FILENAME;
    
    private static final String KEY_USE_HALO_COLOR_ON_VISION_OVERLAY = "useHaloColorForVisionOverlay";
    private static final boolean DEFAULT_USE_HALO_COLOR_ON_VISION_OVERLAY = false;
    
    private static final String KEY_VISION_OVERLAY_OPACITY = "visionOverlayOpacity";
    private static final int DEFAULT_VISION_OVERLAY_OPACITY = 60;
    
    private static final String KEY_HALO_LINE_WIDTH = "haloLineWidth";
    private static final int DEFAULT_HALO_LINE_WIDTH = 2;
    
    private static final String KEY_AUTO_REVEAL_VISION_ON_GM_MOVEMENT = "autoRevealVisionOnGMMove";
    private static final boolean DEFAULT_AUTO_REVEAL_VISION_ON_GM_MOVEMENT = false;
    
    private static final String KEY_USE_TRANSLUCENT_FOG = "useTranslucentFog";
    private static final boolean DEFAULT_USE_TRANSLUCENT_FOG = true;
    
    private static final String KEY_NEW_MAPS_HAVE_FOW = "newMapsHaveFow";
    private static final boolean DEFAULT_NEW_MAPS_HAVE_FOW = false;
    
    private static final String KEY_NEW_TOKENS_VISIBLE = "newTokensVisible";
    private static final boolean DEFAULT_NEW_TOKENS_VISIBLE = true;
    
    private static final String KEY_NEW_MAPS_VISIBLE = "newMapsVisible";
    private static final boolean DEFAULT_NEW_MAPS_VISIBLE = true;
    
    private static final String KEY_NEW_OBJECTS_VISIBLE = "newObjectsVisible";
    private static final boolean DEFAULT_NEW_OBJECTS_VISIBLE = true;
    
    private static final String KEY_NEW_BACKGROUNDS_VISIBLE = "newBackgroundsVisible";
    private static final boolean DEFAULT_NEW_BACKGROUNDS_VISIBLE = true;
    
    private static final String KEY_TOKENS_START_FREESIZE = "newTokensStartFreesize";
    private static final boolean DEFAULT_TOKENS_START_FREESIZE = false;

    private static final String KEY_TOKENS_START_SNAP_TO_GRID = "newTokensStartSnapToGrid";
    private static final boolean DEFAULT_TOKENS_START_SNAP_TO_GRID = true;

    private static final String KEY_OBJECTS_START_SNAP_TO_GRID = "newStampsStartSnapToGrid";
    private static final boolean DEFAULT_OBJECTS_START_SNAP_TO_GRID = false;
    
    private static final String KEY_OBJECTS_START_FREESIZE = "newStampsStartFreesize";
    private static final boolean DEFAULT_OBJECTS_START_FREESIZE = true;

    private static final String KEY_BACKGROUNDS_START_SNAP_TO_GRID = "newBackgroundsStartSnapToGrid";
    private static final boolean DEFAULT_BACKGROUNDS_START_SNAP_TO_GRID = false;
    
    private static final String KEY_BACKGROUNDS_START_FREESIZE = "newBackgroundsStartFreesize";
    private static final boolean DEFAULT_BACKGROUNDS_START_FREESIZE = true;

    private static final String KEY_DEFAULT_GRID_TYPE = "defaultGridType";
    private static final String DEFAULT_DEFAULT_GRID_TYPE = GridFactory.SQUARE;
    
    private static final String KEY_DEFAULT_GRID_SIZE = "defaultGridSize";
    private static final int DEFAULT_DEFAULT_GRID_SIZE = 50;
    
    private static final String KEY_DEFAULT_GRID_COLOR = "defaultGridColor";
    private static final int DEFAULT_DEFAULT_GRID_COLOR = Color.black.getRGB();
    
    private static final String KEY_DEFAULT_UNITS_PER_CELL = "unitsPerCell";
    private static final int DEFAULT_DEFAULT_UNITES_PER_CELL = 5;
    
    private static final String KEY_FONT_SIZE = "fontSize";
    private static final int DEFAULT_FONT_SIZE = 12;
    
    private static final String KEY_CHAT_COLOR = "chatColor";
    private static final Color DEFAULT_CHAT_COLOR = Color.black;
    
    private static final String KEY_PLAY_SYSTEM_SOUNDS = "playSystemSounds";
    private static final boolean DEFAULT_PLAY_SYSTEM_SOUNDS = true;

    private static final String KEY_SOUNDS_ONLY_WHEN_NOT_FOCUSED = "playSystemSoundsOnlyWhenNotFocused";
    private static final boolean DEFAULT_SOUNDS_ONLY_WHEN_NOT_FOCUSED = false;

    private static final String KEY_SHOW_AVATAR_IN_CHAT = "showAvatarInChat";
    private static final boolean DEFAULT_SHOW_AVATAR_IN_CHAT = true;
    
    public static void setShowAvatarInChat(boolean show) {
    	prefs.putBoolean(KEY_SHOW_AVATAR_IN_CHAT, show);
    }
    
    public static boolean getShowAvatarInChat() {
    	return prefs.getBoolean(KEY_SHOW_AVATAR_IN_CHAT, DEFAULT_SHOW_AVATAR_IN_CHAT);
    }
    
    public static void setPlaySystemSounds(boolean play) {
    	prefs.putBoolean(KEY_PLAY_SYSTEM_SOUNDS, play);
    }
    
    public static boolean getPlaySystemSounds() {
    	return prefs.getBoolean(KEY_PLAY_SYSTEM_SOUNDS, DEFAULT_PLAY_SYSTEM_SOUNDS);
    }
    
    public static void setPlaySystemSoundsOnlyWhenNotFocused(boolean play) {
    	prefs.putBoolean(KEY_SOUNDS_ONLY_WHEN_NOT_FOCUSED, play);
    }
    
    public static boolean getPlaySystemSoundsOnlyWhenNotFocused() {
    	return prefs.getBoolean(KEY_SOUNDS_ONLY_WHEN_NOT_FOCUSED, DEFAULT_SOUNDS_ONLY_WHEN_NOT_FOCUSED);
    }
    
    public static void setChatColor(Color color){
    	prefs.putInt(KEY_CHAT_COLOR, color.getRGB());
    }

    public static Color getChatColor() {
		return new Color (prefs.getInt(KEY_CHAT_COLOR, DEFAULT_CHAT_COLOR.getRGB()));
	}
    
    public static void setSaveReminder( boolean reminder ) {
    	prefs.putBoolean(KEY_SAVE_REMINDER, reminder);
    }
    
    public static boolean getSaveReminder() {
    	return prefs.getBoolean(KEY_SAVE_REMINDER, DEFAULT_SAVE_REMINDER);
    }
    
    public static void setAutoSaveIncrement( int increment ) {
    	prefs.putInt(KEY_AUTO_SAVE_INCREMENT, increment);
    }
    
    public static int getAutoSaveIncrement() {
    	return prefs.getInt(KEY_AUTO_SAVE_INCREMENT, DEFAULT_AUTO_SAVE_INCREMENT);
    }
    
    public static void setTokenNumberDisplay (String display) {
    	prefs.put(KEY_TOKEN_NUMBER_DISPLAY, display);
    }
    
    public static String getTokenNumberDisplay () {
        return prefs.get(KEY_TOKEN_NUMBER_DISPLAY, DEFAULT_TOKEN_NUMBER_DISPLAY);
    }
    
    public static void setDuplicateTokenNumber (String numbering) {
    	prefs.put(KEY_DUPLICATE_TOKEN_NUMBER, numbering);
    }
    
    public static String getDuplicateTokenNumber () {
        return prefs.get(KEY_DUPLICATE_TOKEN_NUMBER, DEFAULT_DUPLICATE_TOKEN_NUMBER);
    }
    
    public static void setNewTokenNaming (String naming) {
    	prefs.put(KEY_NEW_TOKEN_NAMING, naming);
    }
    
    public static String getNewTokenNaming () {
        return prefs.get(KEY_NEW_TOKEN_NAMING, DEFAULT_NEW_TOKEN_NAMING);
    }
    
    public static void setUseHaloColorOnVisionOverlay (boolean flag) {
        prefs.putBoolean(KEY_USE_HALO_COLOR_ON_VISION_OVERLAY, flag);
    }
    
    public static boolean getUseHaloColorOnVisionOverlay () {
        return prefs.getBoolean(KEY_USE_HALO_COLOR_ON_VISION_OVERLAY, DEFAULT_USE_HALO_COLOR_ON_VISION_OVERLAY);
    }
    
    public static void setAutoRevealVisionOnGMMovement (boolean flag) {
        prefs.putBoolean(KEY_AUTO_REVEAL_VISION_ON_GM_MOVEMENT, flag);
    }
    
    public static boolean getAutoRevealVisionOnGMMovement () {
        return prefs.getBoolean(KEY_AUTO_REVEAL_VISION_ON_GM_MOVEMENT, DEFAULT_AUTO_REVEAL_VISION_ON_GM_MOVEMENT);
    }    
    
    public static void setVisionOverlayOpacity(int size) {
        prefs.putInt(KEY_VISION_OVERLAY_OPACITY, size);
    }
    
    public static int getVisionOverlayOpacity() {
        return prefs.getInt(KEY_VISION_OVERLAY_OPACITY, DEFAULT_VISION_OVERLAY_OPACITY);
    }
    
    public static void setHaloLineWidth(int size) {
        prefs.putInt(KEY_HALO_LINE_WIDTH, size);
    }
    
    public static int getHaloLineWidth() {
        return prefs.getInt(KEY_HALO_LINE_WIDTH, DEFAULT_HALO_LINE_WIDTH);
    }

    public static void setFontSize(int size) {
    	prefs.putInt(KEY_FONT_SIZE, size);
    }
    
    public static int getFontSize() {
    	return prefs.getInt(KEY_FONT_SIZE, DEFAULT_FONT_SIZE);
    }
    
    public static void setDefaultGridColor(Color color) {
    	prefs.putInt(KEY_DEFAULT_GRID_COLOR, color.getRGB());
    }
    
    public static Color getDefaultGridColor() {
    	return new Color(prefs.getInt(KEY_DEFAULT_GRID_COLOR, DEFAULT_DEFAULT_GRID_COLOR));
    }
    
    public static void setDefaultGridSize(int size) {
    	prefs.putInt(KEY_DEFAULT_GRID_SIZE, size);
    }
    
    public static int getDefaultGridSize() {
    	return prefs.getInt(KEY_DEFAULT_GRID_SIZE, DEFAULT_DEFAULT_GRID_SIZE);
    }
    
    public static void setDefaultUnitsPerCell(int size) {
    	prefs.putInt(KEY_DEFAULT_UNITS_PER_CELL, size);
    }
    
    public static int getDefaultUnitsPerCell() {
    	return prefs.getInt(KEY_DEFAULT_UNITS_PER_CELL, DEFAULT_DEFAULT_UNITES_PER_CELL);
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
    
    public static void setNewObjectsVisible(boolean flag) {
    	prefs.putBoolean(KEY_NEW_OBJECTS_VISIBLE, flag);
    }
    
    public static boolean getNewObjectsVisible() {
    	return prefs.getBoolean(KEY_NEW_OBJECTS_VISIBLE, DEFAULT_NEW_OBJECTS_VISIBLE);
    }
    
    public static void setNewBackgroundsVisible(boolean flag) {
    	prefs.putBoolean(KEY_NEW_BACKGROUNDS_VISIBLE, flag);
    }
    
    public static boolean getNewBackgroundsVisible() {
    	return prefs.getBoolean(KEY_NEW_BACKGROUNDS_VISIBLE, DEFAULT_NEW_BACKGROUNDS_VISIBLE);
    }
    
    public static void setTokensStartSnapToGrid(boolean flag) {
    	prefs.putBoolean(KEY_TOKENS_START_SNAP_TO_GRID, flag);
    }
    
    public static boolean getTokensStartSnapToGrid() {
    	return prefs.getBoolean(KEY_TOKENS_START_SNAP_TO_GRID, DEFAULT_TOKENS_START_SNAP_TO_GRID);
    }
    
    public static void setObjectsStartSnapToGrid(boolean flag) {
    	prefs.putBoolean(KEY_OBJECTS_START_SNAP_TO_GRID, flag);
    }
    
    public static boolean getObjectsStartSnapToGrid() {
    	return prefs.getBoolean(KEY_OBJECTS_START_SNAP_TO_GRID, DEFAULT_OBJECTS_START_SNAP_TO_GRID);
    }

    public static void setTokensStartFreesize(boolean flag) {
    	prefs.putBoolean(KEY_TOKENS_START_FREESIZE, flag);
    }
    
    public static boolean getTokensStartFreesize() {
    	return prefs.getBoolean(KEY_TOKENS_START_FREESIZE, DEFAULT_TOKENS_START_FREESIZE);
    }
    
    public static void setObjectsStartFreesize(boolean flag) {
    	prefs.putBoolean(KEY_OBJECTS_START_FREESIZE, flag);
    }
    
    public static boolean getObjectsStartFreesize() {
    	return prefs.getBoolean(KEY_OBJECTS_START_FREESIZE, DEFAULT_OBJECTS_START_FREESIZE);
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
    
    
    public static void setMruCampaigns (List<File> mruCampaigns) {
    	
    	StringBuilder combined = new StringBuilder("");
		for (ListIterator<File> iter = mruCampaigns.listIterator(); iter.hasNext();) {
			combined.append(iter.next().getPath());
			combined.append(File.pathSeparator);
		}
		prefs.put(KEY_MRU_CAMPAIGNS, combined.toString());
    }
    
    
    public static List<File> getMruCampaigns() {

        List<File> mruCampaigns = new ArrayList<File>();
        String combined = prefs.get(KEY_MRU_CAMPAIGNS, null);
        if (combined != null) {
            String[] all = combined.split(File.pathSeparator);
            for (int i = 0; i < all.length; i++)
                mruCampaigns.add(new File(all[i]));
        }
        return mruCampaigns;
    }

    
    public static void setSavedPaintTextures (List<File> savedTextures) {
    	
    	StringBuilder combined = new StringBuilder("");
		for (ListIterator<File> iter = savedTextures.listIterator(); iter.hasNext();) {
			combined.append(iter.next().getPath());
			combined.append(File.pathSeparator);
		}
		prefs.put(KEY_SAVED_PAINT_TEXTURES, combined.toString());
    }
    
    
    public static List<File> getSavedPaintTextures() {

        List<File> savedTextures = new ArrayList<File>();
        String combined = prefs.get(KEY_SAVED_PAINT_TEXTURES, null);
        if (combined != null) {
            String[] all = combined.split(File.pathSeparator);
            for (int i = 0; i < all.length; i++)
            	savedTextures.add(new File(all[i]));
        }
        return savedTextures;
    }
    
    
}
