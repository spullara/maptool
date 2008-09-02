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
package net.rptools.maptool.client.swing;

import java.awt.Rectangle;
import java.util.StringTokenizer;

// This should really be in rplib
public class ResourceLoader {

	/**
	 * Rectangles are in the form x, y, width, height
	 */
	public static Rectangle loadRectangle(String rectString) {
		
		StringTokenizer strtok = new StringTokenizer(rectString, ",");
		if (strtok.countTokens() != 4) {
			throw new IllegalArgumentException("Could not load rectangle: '" + rectString + "', must be in the form x, y, w, h");
		}

		int x = Integer.parseInt(strtok.nextToken().trim());
		int y = Integer.parseInt(strtok.nextToken().trim());
		int w = Integer.parseInt(strtok.nextToken().trim());
		int h = Integer.parseInt(strtok.nextToken().trim());

		return new Rectangle(x, y, w, h);
	}
}
