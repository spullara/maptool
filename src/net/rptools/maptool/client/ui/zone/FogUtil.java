package net.rptools.maptool.client.ui.zone;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

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
	
	public static Area calculateVisibility2(int x, int y, Area vision, AreaData topology) {

		vision = new Area(vision);
		vision.transform(AffineTransform.getTranslateInstance(x, y));
		
		// sanity check
		if (topology.contains(x, y)) {
			return null;
		}
		
		Point origin = new Point(x, y);
		
		int blockCount = 0;
		for (Area area : topology.getAreaList(origin)) {
		
			if (!vision.intersects(area.getBounds())) {
				continue;
			}
				
			// Simple method to clear some nasty artifacts
//			vision.subtract(area);
			
			List<AreaPoint> pointList = new ArrayList<AreaPoint>();
			
			double[] coords = new double[6];
	
			Point firstPoint = null;
			Point firstOutsidePoint = null;
			
			Point lastPoint = null;
			Point lastOutsidePoint = null;
			Point originPoint = new Point(x, y);
			for (PathIterator iter = area.getPathIterator(null); !iter.isDone(); iter.next()) {
				
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
			
			for (AreaPoint point : pointList) {
				Rectangle r = new Rectangle();
				r.x = Math.min(point.p1.x, point.p2.x)-1;
				r.y = Math.min(point.p1.y, point.p2.y)-1;
				r.width = Math.max(point.p1.x, point.p2.x) - r.x+2;
				r.height = Math.max(point.p1.y, point.p2.y) - r.y+2;
	
				if (!vision.intersects(r)) {
					continue;
				}
				Area blockedArea = createBlockArea(point.p1, point.p2, point.p3, point.p4);
				vision.subtract(blockedArea);
				blockCount++;
			}
		}
//		System.out.println("Blockcount: " + blockCount);
		
		// For simplicity, this catches some of the edge cases
		return vision;
	}	

	private static double getDistance(Point p1, Point p2) {
		double a = p2.x - p1.x;
		double b = p2.y - p1.y;
		return Math.abs(Math.sqrt(a+b));
	}
	
	private static List<Area> skippedAreaList = new ArrayList<Area>();
	public static Area calculateVisibility3(int x, int y, Area vision, AreaData topology) {
		skippedAreaList.clear();
		
		vision = new Area(vision);
		vision.transform(AffineTransform.getTranslateInstance(x, y));
		
		// sanity check
		if (topology.contains(x, y)) {
			return null;
		}
		
		Point origin = new Point(x, y);
		
		Area clearedArea = new Area();
		
		int blockCount = 0;
		for (Area area : topology.getAreaList(origin)) {
		
			if (clearedArea.contains(area.getBounds())) {
				skippedAreaList.add(area);
				continue;
			}
				
			// Simple method to clear some nasty artifacts
//			vision.subtract(area);
			
			List<AreaPoint> pointList = new ArrayList<AreaPoint>();
			
			double[] coords = new double[6];
	
			Point firstPoint = null;
			Point firstOutsidePoint = null;
			
			Point lastPoint = null;
			Point lastOutsidePoint = null;
			Point originPoint = new Point(x, y);
			for (PathIterator iter = area.getPathIterator(null); !iter.isDone(); iter.next()) {
				
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
//		System.out.println("Blockcount: " + blockCount);
		vision.subtract(clearedArea);
		
		// For simplicity, this catches some of the edge cases
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
			
			int width = TokenSize.getWidth(token, zone.getGrid());
			int height = TokenSize.getHeight(token, zone.getGrid());
			
			Area visionArea = new Area();
			for (Vision vision : token.getVisionList()) {
				int x = token.getX();
				int y = token.getY();

				if (!vision.isEnabled()) {
					continue;
				}
				
				Point p = calculateVisionCenter(token, vision, renderer, x, y, width, height);
    			
				Area currVisionArea = FogUtil.calculateVisibility(p.x, p.y, vision.getArea(zone), zone.getTopology());
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
			
			int width = TokenSize.getWidth(token, zone.getGrid());
			int height = TokenSize.getHeight(token, zone.getGrid());
			
			for (Vision vision : token.getVisionList()) {
				int x = token.getX();
				int y = token.getY();

				if (!vision.isEnabled()) {
					continue;
				}
				
				Point p = calculateVisionCenter(token, vision, renderer, x, y, width, height);
    			
				Area currVisionArea = FogUtil.calculateVisibility(p.x, p.y, vision.getArea(zone), zone.getTopology());
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
			
			int width = TokenSize.getWidth(token, zone.getGrid());
			int height = TokenSize.getHeight(token, zone.getGrid());
			
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

					Point p = calculateVisionCenter(token, vision, renderer, x, y, width, height);
	    			
					Area currVisionArea = FogUtil.calculateVisibility(p.x, p.y, vision.getArea(zone), zone.getTopology());
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
		
		Point p = new Point(0,0);
		
//		switch(vision.getAnchor()) {
//		case CENTER:
//			Grid grid = renderer.getZone().getGrid();			
//			Point pOffset = grid.cellGroupCenterOffset(height, width, token.isToken());
//			p.x += pOffset.x;
//			p.y += pOffset.y;
//		}
		
		p.x += x;
		p.y += y;

		return p;
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
		
		public List<Area> getAreaList(final Point centerPoint) {
			List<AreaMeta> areaMetaList = new ArrayList<AreaMeta>(metaList);
			
			Collections.sort(areaMetaList, new Comparator<AreaMeta>() {
				public int compare(AreaMeta o1, AreaMeta o2) {
					Double d1 = centerPoint.distance(o1.getCenterPoint());
					Double d2 = centerPoint.distance(o2.getCenterPoint());
					return d1.compareTo(d2);
				}
			});
			
			List<Area> areaList = new ArrayList<Area>();
			for (AreaMeta meta : areaMetaList) {
				areaList.add(meta.area);
			}

			return areaList;
		}
		
		private void digest() {

			if (metaList != null) {
				// Already digested
				return;
			}
			
			metaList = new ArrayList<AreaMeta>();
			
			// Break the big area into independent areas
			float[] coords = new float[6];
			AreaMeta areaMeta = new AreaMeta();
			for (PathIterator iter = area.getPathIterator(null); !iter.isDone(); iter.next()) {
				
				int type = iter.currentSegment(coords);
				switch (type) {
				case PathIterator.SEG_CLOSE: {

					areaMeta.close();
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
				
				// NOT SUPPORTED
//				case PathIterator.SEG_CUBICTO: coordCount = 3; break;
//				case PathIterator.SEG_QUADTO: coordCount = 2;break;
				}
				
			}
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
		for (int i = 0; i < 1000; i++) {
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
		calculateVisibility2(topSize/2, topSize/2, vision, data);

		
		Area area1 = new Area();
//		JOptionPane.showMessageDialog(new JFrame(), "Hello");
		long start = System.currentTimeMillis();
		for (int i = 0; i < 1; i++) {
			area1 = calculateVisibility3(topSize/2, topSize/2, vision, data);
		}
		System.out.println("1: " + (System.currentTimeMillis() - start));
//		JOptionPane.showMessageDialog(new JFrame(), "world");
		
		Area area2 = null;
		start = System.currentTimeMillis();
		for (int i = 0; i < 1; i++) {
			area2 = calculateVisibility2(topSize/2, topSize/2, vision, data);
		}
		System.out.println("2: " + (System.currentTimeMillis() - start));
		
		System.out.println("Equal: " + (area2.equals(area1)));

		final Area a1 = area1;
		final Area a2 = area2;
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setBounds(0, 0, 400, 200);
		f.setLayout(new GridLayout());
		f.add(new JPanel() {
			Area theArea = a2;
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
			}
			@Override
			protected void paintComponent(Graphics g) {

				Dimension size = getSize();
				g.setColor(Color.white);
				g.fillRect(0, 0, size.width, size.height);
				
				Graphics2D g2d = (Graphics2D)g;
				
				AffineTransform at = AffineTransform.getScaleInstance((size.width/2)/(double)topSize, (size.height)/(double)topSize);
				Area top = topology.createTransformedArea(at);
				
				g.setColor (Color.black);
				g.drawLine(size.width/2, 0, size.width/2, size.height);

				g.setClip(new Rectangle(0, 0, size.width/2, size.height));
				g.setColor(Color.green);
				g2d.fill(top);
				
				g.setColor(Color.lightGray);
				g2d.fill(a1.createTransformedArea(at));
				
				g.setColor(Color.black);
				g.drawLine(size.width/4, size.height/2-4, size.width/4, size.height/2+4);
				g.drawLine(size.width/4-4, size.height/2, size.width/4+4, size.height/2);

				g.setClip(new Rectangle(size.width/2, 0, size.width/2, size.height));
				g2d.translate(200, 0);
				g.setColor(Color.green);
				g2d.fill(top);
				g.setColor(Color.gray);
				if (theArea != null) {
					g2d.fill(theArea.createTransformedArea(at));
				}
				
				g.setColor(Color.black);
				g.drawLine(size.width/4, size.height/2-4, size.width/4, size.height/2+4);
				g.drawLine(size.width/4-4, size.height/2, size.width/4+4, size.height/2);
				
				g.setColor(Color.red);
				System.out.println("Size: " + data.metaList.size() + " - " + skippedAreaList.size());
				for (Area area : skippedAreaList) {
					g2d.fill(area.createTransformedArea(at));
				}
				g2d.translate(-200, 0);
			}
		});
		f.setVisible(true);
		
	}

}
