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

public class AppPreferences {

    private static Preferences prefs = Preferences.userRoot().node(AppConstants.APP_NAME + "/prefs");        

    private static final String KEY_ASSET_ROOTS = "assetRoots";
    
    public static void clearAssetRoots() {
        prefs.put(KEY_ASSET_ROOTS, "");
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
            
            rootList.add(file);
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
