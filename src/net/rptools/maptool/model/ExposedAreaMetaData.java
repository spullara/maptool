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
package net.rptools.maptool.model;

import java.awt.geom.Area;

public class ExposedAreaMetaData {
	private Area exposedAreaHistory;

	public ExposedAreaMetaData() {
		exposedAreaHistory = new Area();
	}

	public ExposedAreaMetaData(Area area) {
		exposedAreaHistory = new Area(area);
	}

	public Area getExposedAreaHistory() {
		if (exposedAreaHistory == null) {
			exposedAreaHistory = new Area();
		}
		return exposedAreaHistory;
	}

	public void addToExposedAreaHistory(Area newArea) {
		if (newArea != null && !newArea.isEmpty()) {
			exposedAreaHistory.add(newArea);
		}
	}

	public void removeExposedAreaHistory(Area newArea) {
		if (newArea != null && !newArea.isEmpty()) {
			exposedAreaHistory.subtract(newArea);
		}
	}

	public void clearExposedAreaHistory() {
		exposedAreaHistory = new Area();
	}
}
