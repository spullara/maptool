package net.rptools.maptool.client.ui.zone;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.CellPoint;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Grid;
import net.rptools.maptool.model.Path;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.TokenSize;
import net.rptools.maptool.model.Vision;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.ZonePoint;
import net.rptools.maptool.util.GraphicsUtil;

public class FogUtil {

	public static Area calculateVisibility(int x, int y, Area vision, Area topology) {

		if (vision == null) {
			return null;
		}
		
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
						Area blockedArea = createBlockArea(lastPoint, point, outsidePoint, lastOutsidePoint);
						pointList.add(new AreaPoint(lastPoint, point, blockedArea, getDistance(originPoint, point)));
					} else {
						// Close the last shape
						if (lastPoint != null) {
							
							Area blockedArea = createBlockArea(firstPoint, lastPoint, lastOutsidePoint, firstOutsidePoint);
							pointList.add(new AreaPoint(lastPoint, point, blockedArea, getDistance(originPoint, point)));
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
			Area blockedArea = createBlockArea(firstPoint, lastPoint, lastOutsidePoint, firstOutsidePoint);
			pointList.add(new AreaPoint(firstPoint, lastPoint, blockedArea, getDistance(originPoint, lastPoint)));
		}

		Collections.sort(pointList, new Comparator<AreaPoint>() {
			public int compare(AreaPoint o1, AreaPoint o2) {
				
				return o1.distance < o2.distance ? -1 : o2.distance < o1.distance ? 1 : 0;
			}
		});
		
		int skip = 0;
		for (AreaPoint point : pointList) {
			Area face = GraphicsUtil.createAreaBetween(point.p1, point.p2, 1);
			face.intersect(vision);
			if (face.isEmpty()) {
				skip++;
				continue;
			}
			
			vision.subtract(point.quad);
		}

		// For simplicity, this catches some of the edge cases
		vision.subtract(topology);

//		System.out.println(pointList.size() + " - " + skip);
		
		return vision;
	}	
	
	public static Area calculateVisibility3(int x, int y, Area vision, Area topology) {

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
		outsideArea.subtract(new Area(vision.getBounds()));
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
						pointList.add(new AreaPoint(lastPoint, point, outsidePoint, lastOutsidePoint, null, getDistance(originPoint, point)));
					} else {
						// Close the last shape
						if (lastPoint != null) {
							pointList.add(new AreaPoint(firstPoint, lastPoint, lastOutsidePoint, firstOutsidePoint, null, getDistance(originPoint, point)));
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
			pointList.add(new AreaPoint(firstPoint, lastPoint, lastOutsidePoint, firstOutsidePoint, null, getDistance(originPoint, lastPoint)));
		}

		Collections.sort(pointList, new Comparator<AreaPoint>() {
			public int compare(AreaPoint o1, AreaPoint o2) {
				
				return o1.distance < o2.distance ? -1 : o2.distance < o1.distance ? 1 : 0;
			}
		});
		
		int skip = 0;
		for (AreaPoint point : pointList) {
			Area face = GraphicsUtil.createAreaBetween(point.p1, point.p2, 1);
			face.intersect(vision);
			if (face.isEmpty()) {
				skip ++;
				continue;
			}
			Area blockedArea = createBlockArea(point.p1, point.p2, point.p3, point.p4);
			vision.subtract(blockedArea);
		}

		// For simplicity, this catches some of the edge cases
		vision.subtract(topology);

		return vision;
	}	
	
	private static double getDistance(Point p1, Point p2) {
		double a = p2.x - p1.x;
		double b = p2.y - p1.y;
		return Math.abs(Math.sqrt(a+b));
	}
	
	private static class AreaPoint {
		Point p1;
		Point p2;
		Point p3;
		Point p4;
		Area quad;
		double distance;
		
		public AreaPoint(Point p1, Point p2, Area quad, double distance) {
			this(p1, p2, null, null, quad, distance);
		}
		public AreaPoint(Point p1, Point p2, Point p3, Point p4, Area quad, double distance) {
			this.p1 = p1;
			this.p2 = p2;
			this.p3 = p3;
			this.p4 = p4;
			this.quad = quad;
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
	
	public static void exposeVisibleArea(Zone zone, Set<GUID> tokenSet) {
		
		for (GUID tokenGUID : tokenSet) {
			Token token = zone.getToken(tokenGUID);
			if (token == null) {
				continue;
			}
			
			if (!token.hasVision()) {
				continue;
			}
			
			int width = TokenSize.getWidth(token, zone.getGrid());
			int height = TokenSize.getHeight(token, zone.getGrid());
			
			Area visionArea = new Area();
			for (Vision vision : token.getVisionList()) {
				int x = token.getX();
				int y = token.getY();

				if (!vision.isEnabled()) {
					continue;
				}
				
				switch(vision.getAnchor()) {
				case CENTER:
					x += width/2;
					y += height/2;
				}
    			
				Area currVisionArea = FogUtil.calculateVisibility(x, y, vision.getArea(zone), zone.getTopology());
				if (currVisionArea != null) {
					visionArea.add(currVisionArea);
				}
			}

			zone.exposeArea(visionArea);
			MapTool.serverCommand().exposeFoW(zone.getId(), visionArea);
		}
	}
	
	public static void exposePCArea(Zone zone) {
		
		Area visionArea = new Area();
		for (Token token : zone.getPlayerTokens()) {
			
			if (!token.hasVision()) {
				continue;
			}
			
			int width = TokenSize.getWidth(token, zone.getGrid());
			int height = TokenSize.getHeight(token, zone.getGrid());
			
			for (Vision vision : token.getVisionList()) {
				int x = token.getX();
				int y = token.getY();

				if (!vision.isEnabled()) {
					continue;
				}
				
				switch(vision.getAnchor()) {
				case CENTER:
					x += width/2;
					y += height/2;
				}
    			
				Area currVisionArea = FogUtil.calculateVisibility(x, y, vision.getArea(zone), zone.getTopology());
				if (currVisionArea != null) {
					visionArea.add(currVisionArea);
				}
			}

		}
		zone.setFogArea(visionArea);
		MapTool.serverCommand().setFoW(zone.getId(), visionArea);
	}
	
	public static void exposeLastPath(Zone zone, Set<GUID> tokenSet) {

		for (GUID tokenGUID : tokenSet) {
			Token token = zone.getToken(tokenGUID);
			if (token == null) {
				continue;
			}
			
			if (!token.hasVision()) {
				continue;
			}
			
			int width = TokenSize.getWidth(token, zone.getGrid());
			int height = TokenSize.getHeight(token, zone.getGrid());
			
			Path lastPath = token.getLastPath();
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
					
					switch(vision.getAnchor()) {
					case CENTER:
						x += width/2;
						y += height/2;
					}
	    			
					Area currVisionArea = FogUtil.calculateVisibility(x, y, vision.getArea(zone), zone.getTopology());
					if (currVisionArea != null) {
						visionArea.add(currVisionArea);
					}
				}
			}

			zone.exposeArea(visionArea);
			MapTool.serverCommand().exposeFoW(zone.getId(), visionArea);
		}
		
	}
	
	public static void main(String[] args) {
		
		Area topology = new Area();
		Random r = new Random(12345);
		for (int i = 0; i < 200; i++) {
			int x = r.nextInt(5000);
			int y = r.nextInt(5000);
			int w = r.nextInt(25) + 25;
			int h = r.nextInt(25) + 25;
			
			topology.add(new Area(new Ellipse2D.Float(x, y, w, h)));
		}
		
		topology.subtract(new Area(new Ellipse2D.Float(2500-10, 2500-10, 20, 20)));
		
		Area vision = new Area(new Ellipse2D.Float(0, 0, 2000, 2000));
		
		long start = System.currentTimeMillis();
		for (int i = 0; i < 10; i++) {
			calculateVisibility(2500, 2500, vision, topology);
		}
		System.out.println("1: " + (System.currentTimeMillis() - start));
		
		start = System.currentTimeMillis();
		for (int i = 0; i < 10; i++) {
			calculateVisibility3(2500, 2500, vision, topology);
		}
		System.out.println("2: " + (System.currentTimeMillis() - start));
	}
}
