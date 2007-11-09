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
package net.rptools.maptool.client.ui.zone;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import net.rptools.lib.GeometryUtil;
import net.rptools.maptool.util.GraphicsUtil;


public class AreaData {
	
	private static final int POINT_COUNT_THRESHOLD = 100;

	private Area area;

	private List<AreaMeta> metaList;
	
	public AreaData(Area area) {
		// Keep our own copy
		this.area = new Area(area);
	}
	
	public boolean contains(int x, int y) {
		for (AreaMeta meta : metaList) {
			if (meta.area.contains(x, y)) {
				return true;
			}
		}
		return false;
	}
	
	public List<AreaMeta> getAreaList() {
		return new ArrayList<AreaMeta>(metaList);
	}
	
	public List<AreaMeta> getAreaList(final Point centerPoint) {
		List<AreaMeta> areaMetaList = new ArrayList<AreaMeta>(metaList);
		
		Collections.sort(areaMetaList, new Comparator<AreaMeta>() {
			public int compare(AreaMeta o1, AreaMeta o2) {
				Double d1 = centerPoint.distance(o1.getCenterPoint());
				Double d2 = centerPoint.distance(o2.getCenterPoint());
				return d1.compareTo(d2);
			}
		});
		
		return areaMetaList;
	}
	
	public void digest() {

		if (metaList != null) {
			// Already digested
			return;
		}
		
		metaList = new ArrayList<AreaMeta>();

		List<Area> areaQueue = new LinkedList<Area>();
		List<Point> splitPoints = new LinkedList<Point>();
		areaQueue.add(area);
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

					splitPoints.clear();
					for (ListIterator<AreaMeta> metaIter = metaList.listIterator(); metaIter.hasNext();) {
						AreaMeta meta = metaIter.next();
						
						// Look for holes
						if (GraphicsUtil.intersects(areaMeta.area, meta.area) && meta.isHole()) {
							
							// This is a hole.  Holes are always created before their parent, so pull out the existing
							// area and remove it from the new area
							metaIter.remove();
							areaMeta.area.subtract(meta.area);

							// Split this hole to rid ourselves of holes
							// Because of the way areas are bounded, if we split
							// through the center point it will cut the hole into at least 2 pieces
							Rectangle bounds = meta.area.getBounds();
							splitPoints.add(new Point(bounds.x+bounds.width/2, bounds.y+bounds.height/2));
						}
					}
					
					if (splitPoints.size() > 0) {

						// Split on one hole (pick it arbitrarily), and resolve the new resulting areas.
						// If there are still holes remaining they will be caught here
						Point point = splitPoints.iterator().next();
							
						Rectangle bounds = areaMeta.area.getBounds();
						Area part1 = new Area(areaMeta.area);
						part1.intersect(new Area(new Rectangle(bounds.x, bounds.y, point.x - bounds.x, bounds.height)));
						areaQueue.add(part1);
						
						Area part2 = new Area(areaMeta.area);
						part2.intersect(new Area(new Rectangle(point.x, bounds.y, (bounds.x + bounds.width) - point.x, bounds.height)));
						areaQueue.add(part2);
						
					} else if (GeometryUtil.countAreaPoints(areaMeta.area) > POINT_COUNT_THRESHOLD) {

						Rectangle bounds = areaMeta.area.getBounds();
						
						int w = bounds.width > bounds.height ? bounds.width/2 : bounds.width;
						int h = bounds.width > bounds.height ? bounds.height : bounds.height/2;
						
						Area part1 = new Area(areaMeta.area);
						part1.intersect(new Area(new Rectangle(bounds.x, bounds.y, w, h)));
						areaQueue.add(part1);
						
						Area part2 = new Area(areaMeta.area);
						part2.intersect(new Area(new Rectangle((bounds.x+bounds.width)-w, (bounds.y+bounds.height)-h, w, h)));
						areaQueue.add(part2);

					} else {
						metaList.add(areaMeta);
					}
					
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
				// NOT SUPPORTED
//				case PathIterator.SEG_CUBICTO: coordCount = 3; break;
//				case PathIterator.SEG_QUADTO: coordCount = 2;break;
				}
				
			}
		}
		
		// No longer needed
		System.out.println("Size: " + metaList.size());
		area = null;
	}
}

