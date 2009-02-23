/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
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
		return props.getProperty("helpURL", "http://www.rptools.net/");
	}
	
	public String getTutorialsURL() {
		return props.getProperty("tutorialsURL", "http://www.rptools.net/");
	}
	
	public String getForumURL() {
		return props.getProperty("forumsURL", "http://forums.rptools.net/");
	}
}
