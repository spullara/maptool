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

import java.awt.BasicStroke;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

import net.rptools.lib.GeometryUtil;
import net.rptools.maptool.util.GraphicsUtil;

public class VisibleAreaSegment implements Comparable {
	private final Point2D origin;
	private final List<AreaFace> faceList = new LinkedList<AreaFace>();
	private Point2D centerPoint;
	private Area pathArea;

	public VisibleAreaSegment(Point2D origin) {
		this.origin = origin;
	}

	public void addAtEnd(AreaFace face) {
		faceList.add(face);
	}

	public void addAtFront(AreaFace face) {
		faceList.add(0, face);
	}

	public double getDistanceFromOrigin() {
		return GeometryUtil.getDistance(getCenterPoint(), origin);
	}

	public Point2D getCenterPoint() {
		if (centerPoint == null) {
			Area path = getPath();
			Rectangle2D bounds = path.getBounds2D();
			centerPoint = new Point2D.Double(bounds.getX() + bounds.getWidth() / 2.0, bounds.getY() + bounds.getHeight() / 2.0);
		}
		return centerPoint;
	}

	public Area getPath() {
		if (pathArea == null) {
			List<Point2D> pathPoints = new LinkedList<Point2D>();

			for (AreaFace face : faceList) {
				// Initial point
				if (pathPoints.size() == 0) {
					pathPoints.add(face.getP1());
				}
				pathPoints.add(face.getP2());
			}
			GeneralPath path = null;
			for (Point2D p : pathPoints) {
				if (path == null) {
					path = new GeneralPath();
					path.moveTo((float) p.getX(), (float) p.getY());
					continue;
				}
				path.lineTo((float) p.getX(), (float) p.getY());
			}
			BasicStroke stroke = new BasicStroke(1);
			pathArea = new Area(stroke.createStrokedShape(path));
		}
		return pathArea;
	}

	public Area getArea() {
		if (faceList.isEmpty()) {
			return new Area();
		}
		List<Point2D> pathPoints = new LinkedList<Point2D>();

		for (AreaFace face : faceList) {
			// Initial point
			if (pathPoints.size() == 0) {
				pathPoints.add(face.getP1());
				pathPoints.add(0, GraphicsUtil.getProjectedPoint(origin, face.getP1(), Integer.MAX_VALUE / 2));
			}
			// Add to the path
			pathPoints.add(face.getP2());
			pathPoints.add(0, GraphicsUtil.getProjectedPoint(origin, face.getP2(), Integer.MAX_VALUE / 2));
		}
//		System.out.println("Skipped: " + skipCount);

		GeneralPath path = null;
		for (Point2D p : pathPoints) {
			if (path == null) {
				path = new GeneralPath();
				path.moveTo((float) p.getX(), (float) p.getY());
				continue;
			}
			path.lineTo((float) p.getX(), (float) p.getY());
		}
		return new Area(path);
	}

	////
	// COMPARABLE
	public int compareTo(Object o) {
		if (!(o instanceof VisibleAreaSegment)) {
			return 0;
		}
		double odist = ((VisibleAreaSegment) o).getDistanceFromOrigin();
		return getDistanceFromOrigin() < odist ? -1 : getDistanceFromOrigin() == odist ? 0 : 1;
	}
}
