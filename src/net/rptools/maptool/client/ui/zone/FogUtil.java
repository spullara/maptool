package net.rptools.maptool.client.ui.zone;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.CellPoint;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Grid;
import net.rptools.maptool.model.Path;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Vision;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.ZonePoint;
import net.rptools.maptool.util.GraphicsUtil;

public class FogUtil {

	public static Area calculateVisibility(int x, int y, Area vision, Area topology) {

		vision = new Area(vision);
		vision.transform(AffineTransform.getTranslateInstance(x, y));
		
		// sanity check
		if (topology.contains(x, y)) {
			return null;
		}
		
		// Trim back to minimize the amount of work to do 
		// Strategy: create an inverse of the area of the vision (since none of the points outside the vision matter
		// and subtract that from the actual topology.  Note that since there is no Area inverse, just make a massive
		// rectangle
		topology = new Area(topology);
		
		Area outsideArea = new Area(new Rectangle(-10000000, -10000000, 20000000, 200000000));
		outsideArea.subtract(vision);
		topology.subtract(outsideArea);
		
		List<AreaPoint> pointList = new ArrayList<AreaPoint>();
		
		double[] coords = new double[6];

		Point origin = new Point(x, y);
		
		Point firstPoint = null;
		Point firstOutsidePoint = null;
		
		Point lastPoint = null;
		Point lastOutsidePoint = null;
		Point originPoint = new Point(x, y);
		for (PathIterator iter = topology.getPathIterator(null); !iter.isDone(); iter.next()) {
			
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
				Point outsidePoint = GraphicsUtil.getProjectedPoint(origin, point, 100000);
				
				if (firstPoint == null) {
					firstPoint = point;
					firstOutsidePoint = outsidePoint;
				}

				if (lastPoint != null) {
					if (type != PathIterator.SEG_MOVETO) {
						pointList.add(new AreaPoint(lastPoint, point, outsidePoint, lastOutsidePoint, getDistance(originPoint, point)));
					} else {
						// Close the last shape
						if (lastPoint != null) {
							pointList.add(new AreaPoint(firstPoint, lastPoint, lastOutsidePoint, firstOutsidePoint, getDistance(originPoint, point)));
						}
						
						firstPoint = point;
						firstOutsidePoint = outsidePoint;
					}
				}

				lastPoint = point;
				lastOutsidePoint = outsidePoint;
			}
			
		}
		
		// Close the area
		if (lastPoint != null) {
			pointList.add(new AreaPoint(firstPoint, lastPoint, lastOutsidePoint, firstOutsidePoint, getDistance(originPoint, lastPoint)));
		}

		Collections.sort(pointList, new Comparator<AreaPoint>() {
			public int compare(AreaPoint o1, AreaPoint o2) {
				
				return o1.distance < o2.distance ? -1 : o2.distance < o1.distance ? 1 : 0;
			}
		});
		
		int skip = 0;
		for (AreaPoint point : pointList) {
			if (!vision.contains(point.p1) && !vision.contains(point.p2)) {
				Area face = GraphicsUtil.createAreaBetween(point.p1, point.p2, 1);
				face.intersect(vision);
				if (face.isEmpty()) {
					skip ++;
					continue;
				}
			}
			Area blockedArea = createBlockArea(point.p1, point.p2, point.p3, point.p4);
			vision.subtract(blockedArea);
		}

		// For simplicity, this catches some of the edge cases
		vision.subtract(topology);
//		System.out.println("Skip: " + skip);
		return vision;
	}	
	
	private static double getDistance(Point p1, Point p2) {
		double a = p2.x - p1.x;
		double b = p2.y - p1.y;
		return Math.abs(Math.sqrt(a+b));
	}
	
	private static List<Area> skippedAreaList = new ArrayList<Area>();
	private static Set<Line2D> frontFaceSet = new HashSet<Line2D>();
	public static Area calculateVisibility3(int x, int y, Area vision, AreaData topology) {
		skippedAreaList.clear();
		frontFaceSet.clear();
		
		vision = new Area(vision);
		vision.transform(AffineTransform.getTranslateInstance(x, y));
		
		// sanity check
		if (topology.contains(x, y)) {
			return null;
		}
		
		Point origin = new Point(x, y);
		
		Area clearedArea = new Area();
		
		int blockCount = 0;
		for (AreaMeta areaMeta : topology.getAreaList(origin)) {
		
			if (clearedArea.contains(areaMeta.area.getBounds())) {
				skippedAreaList.add(areaMeta.area);
				continue;
			}
			
			Set<String> frontFaces = areaMeta.getFrontFaces(origin);
			
			// Simple method to clear some nasty artifacts
//			vision.subtract(area);
			
			List<AreaPoint> pointList = new ArrayList<AreaPoint>();
			
			double[] coords = new double[6];
	
			Point firstPoint = null;
			Point firstOutsidePoint = null;
			
			Point lastPoint = null;
			Point lastOutsidePoint = null;
			Point originPoint = new Point(x, y);
			for (PathIterator iter = areaMeta.area.getPathIterator(null); !iter.isDone(); iter.next()) {
				
				int type = iter.currentSegment(coords);
				if (type != PathIterator.SEG_LINETO && type != PathIterator.SEG_MOVETO) {
					continue;
				}
				
				Point point = new Point((int)coords[0], (int)coords[1]);
				Point outsidePoint = GraphicsUtil.getProjectedPoint(origin, point, 100000);
				
				if (firstPoint == null) {
					firstPoint = point;
					firstOutsidePoint = outsidePoint;
				}

				if (lastPoint != null) {
					if (type != PathIterator.SEG_MOVETO) {
						pointList.add(new AreaPoint(lastPoint, point, outsidePoint, lastOutsidePoint, getDistance(originPoint, point)));
					} else {
						// Close the last shape
						if (lastPoint != null) {
							pointList.add(new AreaPoint(firstPoint, lastPoint, lastOutsidePoint, firstOutsidePoint, getDistance(originPoint, point)));
						}
						
						firstPoint = point;
						firstOutsidePoint = outsidePoint;
					}
				}

				lastPoint = point;
				lastOutsidePoint = outsidePoint;
			}
			
			// Close the area
			if (lastPoint != null) {
				pointList.add(new AreaPoint(firstPoint, lastPoint, lastOutsidePoint, firstOutsidePoint, getDistance(originPoint, lastPoint)));
			}
			
			int removeCount = 0;
			for (ListIterator<AreaPoint> pointIter = pointList.listIterator(); pointIter.hasNext();) {
				AreaPoint point = pointIter.next();
				if (!frontFaces.contains(getLineSegmentId(point.p1, point.p2))) {
					removeCount ++;
					pointIter.remove();
				} else {
					frontFaceSet.add(new Line2D.Double(point.p1, point.p2));
				}
			}
//			System.out.println("Remove: " + removeCount);
	
			Collections.sort(pointList, new Comparator<AreaPoint>() {
				public int compare(AreaPoint o1, AreaPoint o2) {
					
					return o1.distance < o2.distance ? -1 : o2.distance < o1.distance ? 1 : 0;
				}
			});
			
			for (AreaPoint point : pointList) {
				Rectangle r = new Rectangle();
				r.x = Math.min(point.p1.x, point.p2.x)-1;
				r.y = Math.min(point.p1.y, point.p2.y)-1;
				r.width = Math.max(point.p1.x, point.p2.x) - r.x+2;
				r.height = Math.max(point.p1.y, point.p2.y) - r.y+2;
	
				if (clearedArea.contains(r)) {
					
					continue;
				}
				Area blockedArea = createBlockArea(point.p1, point.p2, point.p3, point.p4);
				clearedArea.add(blockedArea);
				blockCount++;
			}
		}
		System.out.println("Areas: " + blockCount);

		// For simplicity, this catches some of the edge cases
		vision.subtract(clearedArea);

		
		return vision;
	}	

	private static class AreaPoint {
		Point p1;
		Point p2;
		Point p3;
		Point p4;

		double distance;
		
		public AreaPoint(Point p1, Point p2, Point p3, Point p4, double distance) {
			this.p1 = p1;
			this.p2 = p2;
			this.p3 = p3;
			this.p4 = p4;
			this.distance = distance;
		}
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
	
	private static int countAreaPoints(Area area) {
		
		int count = 0;
		for (PathIterator iter = area.getPathIterator(null); !iter.isDone(); iter.next()) {
			count++;
		}
		return count;
	}
	
	public static void exposeVisibleArea(ZoneRenderer renderer, Set<GUID> tokenSet) {

		Zone zone = renderer.getZone();
		for (GUID tokenGUID : tokenSet) {
			Token token = zone.getToken(tokenGUID);
			if (token == null) {
				continue;
			}
			
			if (!token.hasVision()) {
				continue;
			}
			
			Rectangle size = token.getBounds(zone);
			
			Area visionArea = new Area();
			for (Vision vision : token.getVisionList()) {
				int x = token.getX();
				int y = token.getY();

				if (!vision.isEnabled()) {
					continue;
				}
				
				Point p = calculateVisionCenter(token, vision, renderer, size.x, size.y, size.width, size.height);
    			
				Area currVisionArea = FogUtil.calculateVisibility(p.x, p.y, vision.getArea(zone, token), zone.getTopology());
				if (currVisionArea != null) {
					visionArea.add(currVisionArea);
				}
			}

			zone.exposeArea(visionArea);
			MapTool.serverCommand().exposeFoW(zone.getId(), visionArea);
		}
	}
	
	public static void exposePCArea(ZoneRenderer renderer) {

		Zone zone = renderer.getZone();
		
		Area visionArea = new Area();
		for (Token token : zone.getPlayerTokens()) {
			
			if (!token.hasVision()) {
				continue;
			}
			
			Dimension size = token.getSize(zone.getGrid());
			
			for (Vision vision : token.getVisionList()) {
				int x = token.getX();
				int y = token.getY();

				if (!vision.isEnabled()) {
					continue;
				}
				
				Point p = calculateVisionCenter(token, vision, renderer, x, y, size.width, size.height);
    			
				Area currVisionArea = FogUtil.calculateVisibility(p.x, p.y, vision.getArea(zone, token), zone.getTopology());
				if (currVisionArea != null) {
					visionArea.add(currVisionArea);
				}
			}

		}
		zone.setFogArea(visionArea);
		MapTool.serverCommand().setFoW(zone.getId(), visionArea);
	}
	
	public static void exposeLastPath(ZoneRenderer renderer, Set<GUID> tokenSet) {

		Zone zone = renderer.getZone();
		for (GUID tokenGUID : tokenSet) {
			Token token = zone.getToken(tokenGUID);
			if (token == null) {
				continue;
			}
			
			if (!token.hasVision()) {
				continue;
			}
			
			Dimension size = token.getSize(zone.getGrid());
			
			Path<CellPoint> lastPath = (Path<CellPoint>) token.getLastPath();
			if (lastPath == null) {
				continue;
			}
			
			Grid grid = zone.getGrid();
			Area visionArea = new Area();
			for (CellPoint cell : lastPath.getCellPath()) {
				
				ZonePoint zp = grid.convert(cell); 
				int x = zp.x;
				int y = zp.y;
				
				for (Vision vision : token.getVisionList()) {

					if (!vision.isEnabled()) {
						continue;
					}

					Point p = calculateVisionCenter(token, vision, renderer, x, y, size.width, size.height);
	    			
					Area currVisionArea = FogUtil.calculateVisibility(p.x, p.y, vision.getArea(zone, token), zone.getTopology());
					if (currVisionArea != null) {
						visionArea.add(currVisionArea);
					}
				}
			}

			zone.exposeArea(visionArea);
			MapTool.serverCommand().exposeFoW(zone.getId(), visionArea);
		}
		
	}
	
	/**
	 * Find the center point of a vision
	 * TODO: This is a horrible horrible method.  the API is just plain disgusting.  But it'll work to consolidate
	 * all the places this has to be done until we can encapsulate it into the vision itself
	 */
	public static Point calculateVisionCenter(Token token, Vision vision, ZoneRenderer renderer, int x, int y, int width, int height) {
		Grid grid = renderer.getZone().getGrid();
		return new Point(x + width/2, y + height/2);
	}

	private static double getAngle(Point2D origin, Point2D target) {
		
		double angle = Math.toDegrees(Math.atan2((origin.getY() - target.getY()),(target.getX() - origin.getX())));
		if (angle < 0) {
			angle += 360;
		}
		
		return angle;
	}
	
	private static double getAngleDelta(double sourceAngle, double targetAngle) {
		
		// Normalize
		targetAngle -= sourceAngle;
		
		if (targetAngle > 180) {
			targetAngle -= 360;
		}
		if (targetAngle < -180) {
			targetAngle += 360; 
		}
		
		return targetAngle;
	}

	private static Line2D findClosestLine(Point2D origin, PointNode pointList) {
		
		Line2D line = null;
		double distance = 0;

		PointNode node = pointList;
		do {
			Line2D newLine = new Line2D.Double(node.previous.point, node.point);
			double newDistance = getDistanceToCenter(origin, newLine);
			if (line == null || newDistance < distance) {
				line = newLine;
				distance = newDistance;
			}
			
			node = node.next;
		} while (node != pointList);

		return line;
	}
	
	private static double getDistanceToCenter(Point2D p, Line2D line) {
		
		Point2D midPoint = new Point2D.Double((line.getP1().getX() + line.getP2().getX())/2, (line.getP1().getY() + line.getP2().getY())/2);

		return Math.hypot(midPoint.getX()-p.getX(), midPoint.getY()-p.getY());
	}

	private static Point2D getCloserPoint(Point2D origin, Line2D line) {
		
		double dist1 = Math.hypot(origin.getX() - line.getP1().getX(), origin.getY() - line.getP1().getY());
		double dist2 = Math.hypot(origin.getX() - line.getP2().getX(), origin.getY() - line.getP2().getY());
		
		return dist1 < dist2 ? line.getP1() : line.getP2();
	}

	private static Set<String> getFrontFaces(PointNode nodeList, Point2D origin) {
		
		Set<String> frontFaces = new HashSet<String>();
		
		Line2D closestLine = findClosestLine(origin, nodeList);
		Point2D closestPoint = getCloserPoint(origin, closestLine);
		PointNode closestNode = nodeList;
		do {
			if (closestNode.point.equals(closestPoint)) {
				break;
			}
			closestNode = closestNode.next;
			
		} while (closestNode != nodeList);
		Point2D secondPoint = closestLine.getP1().equals(closestPoint) ? closestLine.getP2() : closestLine.getP1();
		Point2D thirdPoint = secondPoint.equals(closestNode.next.point) ? closestNode.previous.point : closestNode.next.point;
		
		// Determine whether the first line segment is visible
		Line2D l1 = new Line2D.Double(origin, secondPoint);
		Line2D l2 = new Line2D.Double(closestNode.point, thirdPoint);
		boolean frontFace = !(l1.intersectsLine(l2));
		if (frontFace) {
			frontFaces.add(getLineSegmentId(closestPoint, secondPoint));
		}
		
		
		Point2D startPoint = closestNode.previous.point.equals(secondPoint) ? secondPoint : closestNode.point;
		Point2D endPoint = closestNode.point.equals(startPoint) ? secondPoint : closestNode.point;
		double originAngle = getAngle(origin, startPoint);
		double pointAngle = getAngle(startPoint, endPoint);
		int lastDirection = getAngleDelta(originAngle, pointAngle) > 0 ? 1 : -1;

//		System.out.format("%s: %.2f %s, %.2f %s => %.2f : %d : %s\n", frontFace, originAngle, startPoint.toString(), pointAngle, endPoint.toString(), getAngleDelta(originAngle, pointAngle), lastDirection, (closestNode.previous.point.equals(secondPoint) ? "second" : "closest").toString());
		PointNode node = secondPoint.equals(closestNode.next.point) ? closestNode.next : closestNode;
		do {
		
			Point2D point = node.point;
			Point2D nextPoint = node.next.point;
			
			originAngle = getAngle(origin, point);
			pointAngle = getAngle(origin, nextPoint);

//			System.out.println(point + ":" + originAngle + ", " + nextPoint + ":"+ pointAngle + ", " + getAngleDelta(originAngle, pointAngle));
			if (getAngleDelta(originAngle, pointAngle) > 0) {
				if (lastDirection < 0) {
					frontFace = !frontFace;
					lastDirection = 1;
				}
			} else {
				if (lastDirection > 0) {
					frontFace = !frontFace;
					lastDirection = -1;
				}
			}

			if (frontFace) {
				frontFaces.add(getLineSegmentId(nextPoint, point));
			}
			
			node = node.next;
			
		} while (!node.point.equals(closestPoint));
		
		return frontFaces;
	}	
	
	private static String getLineSegmentId(Point2D p1, Point2D p2) {

		int x1 = (int)Math.min(p1.getX(), p2.getX());
		int x2 = (int)Math.max(p1.getX(), p2.getX());
		
		int y1 = (int)Math.min(p1.getY(), p2.getY());
		int y2 = (int)Math.max(p1.getY(), p2.getY());
		
		return String.format("%d.%d-%d.%d", x1, x2, y1, y2);
	}

	
	public static class AreaData {

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
		
		private void digest() {

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
							
						} else if (countAreaPoints(areaMeta.area) > 25) {

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
	
	private static class AreaMeta {

		Area area;
		PointNode pointNodeList;
		Point2D centerPoint;
		
		// Only used during construction
		GeneralPath path; 
		PointNode lastPointNode;
		
		public AreaMeta() {
		}
		
		public Point2D getCenterPoint() {
			if (centerPoint == null) {
				centerPoint = new Point2D.Double(area.getBounds().x + area.getBounds().width/2, area.getBounds().y + area.getBounds().height/2);
			}
			return centerPoint;
		}
		
		public Set<String> getFrontFaces(Point2D origin) {
			
			return FogUtil.getFrontFaces(pointNodeList, origin);
		}
		
		public boolean isHole() {
			
			double angle = 0;
			

			PointNode currNode = pointNodeList.next;
			while (currNode != pointNodeList) {
				angle += getAngleDelta(getAngle(currNode.previous.point, currNode.point), getAngle(currNode.point, currNode.next.point));
				currNode = currNode.next;
			}
			
			return angle < 0;
		}
		
		public void addPoint(float x, float y) {
			PointNode pointNode = new PointNode(new Point2D.Double(x, y));
			
			if (path == null) {
				path = new GeneralPath();
				path.moveTo(x, y);
				
				pointNodeList = pointNode;
			} else {
				path.lineTo(x, y);
				
				lastPointNode.next = pointNode;
				pointNode.previous = lastPointNode;
			}
			
			lastPointNode = pointNode;
		}
		
		public void close() {
			area = new Area(path);

			// Close the circle
			lastPointNode.next = pointNodeList;
			pointNodeList.previous = lastPointNode;
			lastPointNode = null;
			
			path = null;
		}
	}

	private static class PointNode {
		PointNode previous;
		PointNode next;
		
		Point2D point;
		
		public PointNode(Point2D point) {
			this.point = point;
		}
	}
	
	
	
	public static void main(String[] args) {
		
		System.out.println("Creating topology");
		final int topSize = 10000;
		final Area topology = new Area();
		Random r = new Random(12345);
		for (int i = 0; i < 100; i++) {
			int x = r.nextInt(topSize);
			int y = r.nextInt(topSize);
			int w = r.nextInt(500) + 50;
			int h = r.nextInt(500) + 50;
			
			topology.add(new Area(new Rectangle(x, y, w, h)));
		}
		
		// Make sure the the center point is not contained inside the blocked area
		topology.subtract(new Area(new Rectangle(topSize/2-200, topSize/2-200, 400, 400)));
		
		final Area vision = new Area(new Rectangle(-Integer.MAX_VALUE/2, -Integer.MAX_VALUE/2, Integer.MAX_VALUE, Integer.MAX_VALUE));
		
		int pointCount = 0;
		for (PathIterator iter = topology.getPathIterator(null); !iter.isDone(); iter.next()) {
			pointCount++;
		}
		
		System.out.println("Starting test " + pointCount + " points");
		final AreaData data = new AreaData(topology);
		data.digest();
		
		// Make sure all classes are loaded
		calculateVisibility3(topSize/2, topSize/2, vision, data);

		
		Area area1 = new Area();
//		JOptionPane.showMessageDialog(new JFrame(), "Hello");
		long start = System.currentTimeMillis();
		for (int i = 0; i < 1; i++) {
			area1 = calculateVisibility3(topSize/2, topSize/2, vision, data);
		}

		final Area a1 = area1;
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setBounds(0, 0, 400, 200);
		f.setLayout(new GridLayout());
		f.add(new JPanel() {
			BufferedImage topImage = null;
			Area theArea = null;
			{
				addMouseMotionListener(new MouseMotionAdapter() {
					@Override
					public void mouseDragged(MouseEvent e) {
						
						Dimension size = getSize();
						int x = (int)((e.getX() - (size.width/2)) / (size.width/2.0/topSize));
						int y = (int)(e.getY() / (size.height/2.0/topSize)/2);
						
						long start = System.currentTimeMillis();
						theArea = calculateVisibility3(x, y, vision, data);
						System.out.println("Calc: " + (System.currentTimeMillis() - start));
						repaint();
					}
				});
				addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						
						Dimension size = getSize();
						int x = (int)((e.getX() - (size.width/2)) / (size.width/2.0/topSize));
						int y = (int)(e.getY() / (size.height/2.0/topSize)/2);
						
						long start = System.currentTimeMillis();
						theArea = calculateVisibility3(x, y, vision, data);
						System.out.println("Calc: " + (System.currentTimeMillis() - start));
						repaint();
					}
				});
			}
			@Override
			protected void paintComponent(Graphics g) {

				Dimension size = getSize();
				g.setColor(Color.white);
				g.fillRect(0, 0, size.width, size.height);
				
				Graphics2D g2d = (Graphics2D)g;
				
				AffineTransform at = AffineTransform.getScaleInstance((size.width/2)/(double)topSize, (size.height)/(double)topSize);
				if (topImage == null) {
					Area top = topology.createTransformedArea(at);
					topImage = new BufferedImage(size.width/2, size.height, BufferedImage.OPAQUE);
					
					Graphics2D g2 = topImage.createGraphics();
					g2.setColor(Color.white);
					g2.fillRect(0, 0, size.width/2, size.height);
					
					g2.setColor(Color.green);
					g2.fill(top);
					g2.dispose();
				}
				
				g.setColor (Color.black);
				g.drawLine(size.width/2, 0, size.width/2, size.height);

//				g.setClip(new Rectangle(0, 0, size.width/2, size.height));
//				g.setColor(Color.green);
//				g2d.fill(top);
//				
//				g.setColor(Color.lightGray);
//				g2d.fill(a1.createTransformedArea(at));
				
				g.setClip(new Rectangle(size.width/2, 0, size.width/2, size.height));
				g2d.translate(200, 0);
				g.setColor(Color.green);
				g2d.drawImage(topImage, 0, 0, this);
				g.setColor(Color.gray);
				if (theArea != null) {
					g2d.fill(theArea.createTransformedArea(at));
				}
				
				for (AreaMeta areaMeta : data.getAreaList(new Point(0, 0))) {
					g.setColor(Color.red);
					g2d.draw(areaMeta.area.createTransformedArea(at));
				}
//				g.setColor(Color.red);
//				System.out.println("Size: " + data.metaList.size() + " - " + skippedAreaList.size());
//				for (Area area : skippedAreaList) {
//					g2d.fill(area.createTransformedArea(at));
//				}
				g2d.translate(-200, 0);
			}
		});
		f.setVisible(true);
		
	}

	public static void main2(String[] args) {
		
		final Area topology = new Area();
//		topology.add(new Area(new Rectangle(100, 100, 200, 200)));
//		topology.add(new Area(new Rectangle(350, 350, 50, 50)));
//		topology.subtract(new Area(new Rectangle(110, 110, 50, 50)));
//		topology.subtract(new Area(new Rectangle(200, 200, 50, 50)));
//		topology.add(new Area(new Rectangle(115, 115, 10, 10)));
		
		final Area vision = new Area(new Rectangle(-Integer.MAX_VALUE/2, -Integer.MAX_VALUE/2, Integer.MAX_VALUE, Integer.MAX_VALUE));
		
		int pointCount = 0;
		for (PathIterator iter = topology.getPathIterator(null); !iter.isDone(); iter.next()) {
			pointCount++;
		}
		
		final AreaData data = new AreaData(topology);
		data.digest();
		
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setBounds(100, 100, 500, 500);
		f.setLayout(new GridLayout());
		f.add(new JPanel() {
			Area theArea = null;
			int x = 200;
			int y = 200;
			{
				addMouseMotionListener(new MouseMotionAdapter() {
					@Override
					public void mouseDragged(MouseEvent e) {
						
						Dimension size = getSize();
						x = e.getX();
						y = e.getY();
						
						long start = System.currentTimeMillis();
						theArea = calculateVisibility3(x, y, vision, data);
//						System.out.println("Calc: " + (System.currentTimeMillis() - start));
						repaint();
					}
				});
				addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						
						Dimension size = getSize();
						x = e.getX();
						y = e.getY();
						
						long start = System.currentTimeMillis();
						theArea = calculateVisibility3(x, y, vision, data);
//						System.out.println("Calc: " + (System.currentTimeMillis() - start));
						repaint();
					}
				});
			}
			@Override
			protected void paintComponent(Graphics g) {

				Dimension size = getSize();
				g.setColor(Color.white);
				g.fillRect(0, 0, size.width, size.height);
				
				Graphics2D g2d = (Graphics2D)g;

				g.setColor(Color.gray);
				g2d.fill(topology);
				
				g.setColor(Color.lightGray);
				if (theArea != null) {
					g2d.fill(theArea);
				}
				
				g.setColor(Color.black);
				g.drawLine(x, y-4, x, y+4);
				g.drawLine(x-4, y, x+4, y);

				g.setColor(Color.red);
//				System.out.println("Size: " + data.metaList.size() + " - " + skippedAreaList.size());
//				for (Area area : skippedAreaList) {
//					g2d.fill(area);
//				}
				
				for (AreaMeta areaMeta : data.getAreaList(new Point(0, 0))) {
					g.setColor(Color.green);
					g2d.fill(areaMeta.area);
					g.setColor(Color.red);
					g2d.draw(areaMeta.area);
				}
				
				g.setColor(Color.orange);
				for (Line2D line : frontFaceSet) {
					g2d.draw(line);
				}
			}
		});
		f.setVisible(true);
		
	}
}
