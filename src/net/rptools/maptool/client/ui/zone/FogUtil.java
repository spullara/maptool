package net.rptools.maptool.client.ui.zone;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
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
		
		// For simplicity, this catches some of the edge cases
		vision.subtract(topology);
		
		double[] coords = new double[6];

		Point origin = new Point(x, y);
		
		Point firstPoint = null;
		Point firstOutsidePoint = null;
		
		Point lastPoint = null;
		Point lastOutsidePoint = null;
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
						vision.subtract(blockedArea);
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
			}
			
		}
		
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
}
