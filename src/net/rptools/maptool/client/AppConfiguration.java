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

import java.io.IOException;
import java.util.Properties;

public class AppConfiguration {

	private Properties props;
	
	public AppConfiguration(String path) {
		
		props = new Properties();
		try {
			props.load(AppConfiguration.class.getClassLoader().getResourceAsStream(path));
		} catch (IOException ioe) {
			throw new IllegalArgumentException("Could not find configuration: " + path);
		}
	}
	
	public String getHelpURL() {
		return props.getProperty("helpURL", "http://rptools.net");
	}
	
	public String getTutorialsURL() {
		return props.getProperty("tutorialsURL", "http://rptools.net");
	}
	
	public String getForumURL() {
		return props.getProperty("forumsURL", "http://forums.rptools.net");
	}
}
