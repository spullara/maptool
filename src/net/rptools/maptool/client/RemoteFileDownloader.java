/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *  
 *	http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package net.rptools.maptool.client;

import java.awt.Component;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.ProgressMonitor;

import net.rptools.lib.GUID;

public class RemoteFileDownloader {
	private URL url;
	private Component parentComponent;
	
	public RemoteFileDownloader(URL url) {
		this(url, null);
	}
	public RemoteFileDownloader(URL url, Component parentComponent) {
		if (url == null) {
			throw new IllegalArgumentException("URL cannot be null");
		}
		this.url = url;
		this.parentComponent = parentComponent;
	}
	
	/**
	 * Read the data at the given URL.  This method should not be called on the EDT.
	 * @return File pointer to the location of the data, file will be deleted at program end
	 */
	public File read() throws IOException {
		URLConnection conn = url.openConnection();
		
		conn.setConnectTimeout( 5000 );
        conn.setReadTimeout( 5000 );
 
        // Send the request.
        conn.connect( );
 
        int length  = conn.getContentLength( );

        String tempDir = System.getProperty("java.io.tmpdir");
        if (tempDir == null) {
        	tempDir = ".";
        }
        File tempFile = new File(tempDir + "/" + new GUID() + ".dat");
        tempFile.deleteOnExit();

    	InputStream in = null;
    	OutputStream out = null;

        ProgressMonitor monitor = new ProgressMonitor(parentComponent, "Downloading " + url, null, 0, length);
    	try {
    		in = conn.getInputStream();
    		out = new BufferedOutputStream(new FileOutputStream(tempFile));
    		
    		int buflen = 1024 * 30;
    		int bytesRead = 0;
            byte[] buf = new byte[buflen];;

            long start = System.currentTimeMillis();
            for ( int nRead = in.read(buf); nRead != -1; nRead = in.read(buf) ) {
            	if (monitor.isCanceled()) {
            		return null;
            	}

            	bytesRead += nRead;

            	out.write(buf, 0, nRead);
                
                monitor.setProgress(bytesRead);
//                monitor.setNote("Elapsed: " + ((System.currentTimeMillis() - start) / 1000) + " seconds");
            }
    		
    	} finally {
    		if (in != null) {
    			in.close();
    		}
    		if (out != null) {
    			out.close();
    		}
    		
    		monitor.close();
    	}
        
		return tempFile;
	}
	
	public static void main(String[] args) throws Exception {
		
		RemoteFileDownloader downloader = new RemoteFileDownloader(new URL("http://library.rptools.net/torstan.zip"));

		File tempFile = downloader.read();
		System.out.println(tempFile + " - " + tempFile.length());
	}
}
