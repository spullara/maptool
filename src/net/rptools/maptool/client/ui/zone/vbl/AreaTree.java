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
package net.rptools.maptool.client.ui.zone.vbl;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import net.rptools.maptool.util.GraphicsUtil;

public class AreaTree {

	private List<AreaMeta> metaList = new ArrayList<AreaMeta>();
	
	public AreaTree(Area area) {
		
		digest(area);
	}

	private void digest(Area originalArea) {

		if (originalArea == null) {
			return;
		}

		List<Area> areaQueue = new LinkedList<Area>();
		areaQueue.add(originalArea);
		
		while (areaQueue.size() > 0) {
			Area area = areaQueue.remove(0);
			
			// Break the big area into independent areas
			float[] coords = new float[6];
			AreaMeta areaMeta = new AreaMeta();
			for (PathIterator iter = area.getPathIterator(null); !iter.isDone(); iter.next()) {
				
				int type = iter.currentSegment(coords);
				switch (type) {
				case PathIterator.SEG_CLOSE: {
					areaMeta.close();

					for (ListIterator<AreaMeta> metaIter = metaList.listIterator(); metaIter.hasNext();) {
						AreaMeta meta = metaIter.next();
						
						// Look for holes
						if (GraphicsUtil.intersects(areaMeta.area, meta.area) && meta.isHole()) {
							
							// This is a hole.  Holes are always created before their parent, so pull out the existing
							// area and remove it from the new area
							metaIter.remove();
							areaMeta.area.subtract(meta.area);

//							// Keep track of the hole for future reference
//							holeList.add(meta.area);
						}
					}
					
					metaList.add(areaMeta);
					break;
				}
				case PathIterator.SEG_LINETO: {
					areaMeta.addPoint(coords[0], coords[1]);
					break;
				}
				case PathIterator.SEG_MOVETO: {
					areaMeta = new AreaMeta();
					areaMeta.addPoint(coords[0], coords[1]);
					break;
				}
				}
				
			}
		}
	}
}
