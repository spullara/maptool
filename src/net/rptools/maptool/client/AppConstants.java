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
import java.io.FilenameFilter;

import net.rptools.lib.swing.ImageBorder;
import net.rptools.maptool.model.Token;
import net.tsc.servicediscovery.ServiceGroup;

public class AppConstants {

    public static final String APP_NAME = "MapTool";

    public static final File UNZIP_DIR = AppUtil.getAppHome("resource");
 
	public static final ServiceGroup SERVICE_GROUP = new ServiceGroup("maptool");

	public static final ImageBorder GRAY_BORDER = new ImageBorder("net/rptools/maptool/client/image/border/gray");
	public static final ImageBorder SHADOW_BORDER = new ImageBorder("net/rptools/maptool/client/image/border/shadow");

	public static final int PORTRAIT_SIZE = 100;
	
	public static final FilenameFilter IMAGE_FILE_FILTER = new FilenameFilter() {
        public boolean accept(File dir,String name) {
            name = name.toLowerCase();
            return name.endsWith(".bmp") ||
                    name.endsWith(".png") ||
                    name.endsWith(".jpg") ||
                    name.endsWith(".jpeg") ||
                    name.endsWith(".gif") ||
                    
                    // RPTools Token format
                    name.endsWith(Token.FILE_EXTENSION)
                    ;
        }
    };
    
}
