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
package net.rptools.maptool.client.ui.zone.vbl;

import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;

public class AreaIsland implements AreaContainer {

	private AreaMeta meta;
	private Set<AreaOcean> oceanSet = new HashSet<AreaOcean>();
	
	
	public AreaIsland(AreaMeta meta) {
		this.meta = meta;
	}

	public Set<VisibleAreaSegment> getVisibleAreaSegments(Point2D origin) {

		return meta.getVisibleAreas(origin);
	}
	
	public AreaOcean getDeepestOceanAt(Point2D point) {

		if (!meta.area.contains(point)) {
			return null;
		}
		
		for (AreaOcean ocean : oceanSet) {
			AreaOcean deepOcean = ocean.getDeepestOceanAt(point);
			if (deepOcean != null) {
				return deepOcean;
			}
		}
		
		// If we don't have an ocean that contains the point then 
		// the point is not technically in an ocean
		return null;
	}
	
	public Set<AreaOcean> getOceans() {
		return new HashSet<AreaOcean>(oceanSet);
	}
	
	public void addOcean(AreaOcean ocean) {
		oceanSet.add(ocean);
	}
	
	////
	// AREA CONTAINER
	public Area getBounds() {
		return meta.area;
	}
}
