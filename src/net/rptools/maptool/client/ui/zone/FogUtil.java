package net.rptools.maptool.client.ui.zone;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;

public class FogUtil {

	public static Area calculateVisibility(int x, int y, Area vision, Area topology) {

		vision = new Area(vision);
		vision.transform(AffineTransform.getTranslateInstance(x, y));
		
		// sanity check
		if (topology.contains(x, y)) {
			return null;
		}
		
		// Trim back to minimize the amount of work to do 
		topology = new Area(topology);
		
		Area outsideArea = new Area(new Rectangle(-1000000, -1000000, 2000000, 20000000));
		outsideArea.subtract(new Area(vision.getBounds()));
		topology.subtract(outsideArea);
		
		// For simplicity, this catches some of the edge cases
		vision.subtract(topology);
		
		double[] coords = new double[6];

		Point origin = new Point(x, y);
		
		Point firstPoint = null;
		Point firstOutsidePoint = null;
		
		Point lastPoint = null;
		Point lastOutsidePoint = null;
		boolean lastPointInVision = false;
		int count = 0;
		for (PathIterator iter = topology.getPathIterator(null); !iter.isDone(); iter.next()) {
			count ++;
			
			int type = iter.currentSegment(coords);
			int coordCount = 0;
			switch (type) {
			case PathIterator.SEG_CLOSE: coordCount = 0; break;
			case PathIterator.SEG_CUBICTO: coordCount = 3; break;
			case PathIterator.SEG_LINETO: coordCount = 1; break;
			case PathIterator.SEG_MOVETO: coordCount = 1;break;
			case PathIterator.SEG_QUADTO: coordCount = 2;break;
			}
			
			for (int i = 0; i < coordCount; i++) {

				Point point = new Point((int)coords[i*2], (int)coords[i*2+1]);
				Point outsidePoint = getProjectedPoint(origin, point, 100000);
				
				boolean pointInVision = vision.contains(point);

				if (firstPoint == null) {
					firstPoint = point;
					firstOutsidePoint = outsidePoint;
				}

				if (lastPoint != null) {
					if (type != PathIterator.SEG_MOVETO) {
//						if (lastPointInVision || pointInVision) {
							Area blockedArea = createBlockArea(lastPoint, point, outsidePoint, lastOutsidePoint);
							vision.subtract(blockedArea);
//						}
					} else {
						// Close the last shape
						if (lastPoint != null) {
							
							Area blockedArea = createBlockArea(firstPoint, lastPoint, lastOutsidePoint, firstOutsidePoint);
							vision.subtract(blockedArea);
						}
						
						firstPoint = point;
						firstOutsidePoint = outsidePoint;
					}
				}

				lastPoint = point;
				lastOutsidePoint = outsidePoint;
				lastPointInVision = pointInVision;
			}
			
		}
		System.out.println("Count: " + count);
		
		// Close the area
		if (lastPoint != null) {
			Area blockedArea = createBlockArea(firstPoint, lastPoint, lastOutsidePoint, firstOutsidePoint);
			vision.subtract(blockedArea);
		}

		return vision;
	}
	
	private static Area createBlockArea(Point p1, Point p2, Point p3, Point p4) {
		GeneralPath path = new GeneralPath();
		path.moveTo(p1.x, p1.y);
		path.lineTo(p2.x, p2.y);
		path.lineTo(p3.x, p3.y);
		path.lineTo(p4.x, p4.y);
		path.lineTo(p1.x, p1.y);
		path.closePath();
		
		return new Area(path);
	}
	
	private static Point getProjectedPoint(Point origin, Point target, int distance) {
		
		double x1 = origin.x;
		double x2 = target.x;
		
		double y1 = origin.y;
		double y2 = target.y;
		
		double angle = Math.atan2(y2 - y1, x2 - x1);
		
		double newX = x1 + distance * Math.cos(angle);
		double newY = y1 + distance * Math.sin(angle);
		
		return new Point((int)newX, (int)newY);
	}
	
}
