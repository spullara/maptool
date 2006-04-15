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
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;

import net.rptools.lib.EnvUtil;
import net.rptools.lib.FileUtil;
import net.rptools.maptool.client.ui.MapToolFrame;

/**
 * Executes only the first time the application is run.
 */
public class AppSetup {

    public static void install() {
        
        File appDir = AppUtil.getAppHome();
        
        // Only init once
        if (appDir.listFiles().length > 0) {
            return;
        }
        
        try {
            
            installDefaultTokens();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    public static void installDefaultTokens() throws IOException {

    	// Find the jar with the tokens
    	URL url = AppSetup.class.getClassLoader().getResource("package.xml");
    	if (url == null) {
    		return;
    	}
    	
    	String fileURL = URLDecoder.decode(url.toString());
    	fileURL = fileURL.substring("jar:file:".length());
    	fileURL = fileURL.substring(0, fileURL.indexOf("!"));
    	if (fileURL.startsWith("\\")) {
    		fileURL = fileURL.substring(1);
    	}

    	File sourceFile = new File(fileURL);
    	
        // Create the directory
        File unzipDir = new File(AppConstants.UNZIP_DIR.getAbsolutePath() + File.separator + "Default");
        unzipDir.mkdirs();

        FileUtil.unzipFile(sourceFile, unzipDir);
        
        // Don't need the manifest file
        FileUtil.delete(new File(unzipDir.getAbsolutePath() + File.separator + "META-INF"));

        // Add as a resource root
		AppPreferences.addAssetRoot(unzipDir);
    }
}
