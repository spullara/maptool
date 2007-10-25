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

import net.rptools.lib.GeometryUtil;
import net.rptools.lib.GeometryUtil.PointNode;
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

	public static Area calculateVisibility(int x, int y, Area vision, AreaData topology) {
		
		vision = new Area(vision);
		vision.transform(AffineTransform.getTranslateInstance(x, y));
		
		// sanity check
		if (topology.contains(x, y)) {
			return null;
		}
		
		Point origin = new Point(x, y);
		
		Area clearedArea = new Area();
		
		int blockCount = 0;
		int pointCount = 0;
		int origSize = 0;
		int afterSize = 0;
		for (AreaMeta areaMeta : topology.getAreaList(origin)) {
		
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
				
				pointCount ++;
				
				Point point = new Point((int)coords[0], (int)coords[1]);
				Point outsidePoint = GraphicsUtil.getProjectedPoint(origin, point, 100000);
				
				if (firstPoint == null) {
					firstPoint = point;
					firstOutsidePoint = outsidePoint;
				}

				if (lastPoint != null) {
					if (type != PathIterator.SEG_MOVETO) {
						pointList.add(new AreaPoint(lastPoint, point, outsidePoint, lastOutsidePoint, GeometryUtil.getDistance(originPoint, point)));
					} else {
						// Close the last shape
						if (lastPoint != null) {
							pointList.add(new AreaPoint(firstPoint, lastPoint, lastOutsidePoint, firstOutsidePoint, GeometryUtil.getDistance(originPoint, point)));
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
				pointList.add(new AreaPoint(firstPoint, lastPoint, lastOutsidePoint, firstOutsidePoint, GeometryUtil.getDistance(originPoint, lastPoint)));
			}
			
			origSize = pointList.size();
			for (ListIterator<AreaPoint> pointIter = pointList.listIterator(); pointIter.hasNext();) {
				AreaPoint point = pointIter.next();
				if (!frontFaces.contains(GeometryUtil.getLineSegmentId(point.p1, point.p2))) {
					pointIter.remove();
				}
			}
			afterSize = pointList.size();
			
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
		System.out.println(pointCount + " : " + origSize + " : " + afterSize + " : " + blockCount);

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
    			
				Area currVisionArea = FogUtil.calculateVisibility(p.x, p.y, vision.getArea(zone, token), renderer.getTopologyAreaData());
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
    			
				Area currVisionArea = FogUtil.calculateVisibility(p.x, p.y, vision.getArea(zone, token), renderer.getTopologyAreaData());
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
	    			
					Area currVisionArea = FogUtil.calculateVisibility(p.x, p.y, vision.getArea(zone, token), renderer.getTopologyAreaData());
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
		calculateVisibility(topSize/2, topSize/2, vision, data);

		
		Area area1 = new Area();
//		JOptionPane.showMessageDialog(new JFrame(), "Hello");
		long start = System.currentTimeMillis();
		for (int i = 0; i < 1; i++) {
			area1 = calculateVisibility(topSize/2, topSize/2, vision, data);
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
						theArea = calculateVisibility(x, y, vision, data);
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
						theArea = calculateVisibility(x, y, vision, data);
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
						theArea = calculateVisibility(x, y, vision, data);
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
						theArea = calculateVisibility(x, y, vision, data);
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
				
			}
		});
		f.setVisible(true);
		
	}
}
