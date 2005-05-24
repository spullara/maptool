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
package net.rptools.maptool.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 */
public class FileUtil {

    public static byte[] loadFile(File file) throws IOException{
        FileInputStream fis = null;
        try {
          fis = new FileInputStream(file);
          return getBytes(fis);
        } finally {
          if (fis != null) fis.close();
        }
    }
    
    public static byte[] loadResource(String resource) throws IOException {
        
        InputStream fis = null;
        try {
          fis = FileUtil.class.getClassLoader().getResourceAsStream(resource);
          return getBytes(fis);
        } finally {
          if (fis != null) fis.close();
        }
    }
    
    public static byte[] getBytes(URL url) throws IOException {
    	return getBytes(url.openConnection().getInputStream());
    }
    
    private static byte[] getBytes(InputStream inStream) throws IOException {

        if (inStream == null) {
            throw new IllegalArgumentException ("Input stream cannot be null");
        }
        
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        
        int b;
        byte[] buffer = new byte[4096];
        while ((b = inStream.read(buffer)) >= 0) {
            outStream.write(buffer, 0, b);
        }
        
        return outStream.toByteArray();
    }
}
