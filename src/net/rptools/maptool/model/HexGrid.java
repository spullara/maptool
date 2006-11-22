package net.rptools.maptool.model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;

import net.rptools.maptool.client.ScreenPoint;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.client.walker.ZoneWalker;
import net.rptools.maptool.client.walker.astar.AStarHexEuclideanWalker;

public class HexGrid extends Grid {

	private static final GridCapabilities GRID_CAPABILITIES= new GridCapabilities() {
		public boolean isPathingSupported() {return true;}
		public boolean isSnapToGridSupported() {return true;}
	};
	
	private static final int[] FACING_ANGLES = new int[] {
		-135, -90, -45, 45, 90, 135
	};
	
	private double sideSize;
	private int height;
	private double topWidth;
	
	private GeneralPath scaledHex;
	private double scaledSideSize;
	private double scaledHeight;
	private double scaledTopWidth;
	private double lastScale = -1;

	private Dimension cellOffset;
	
	public HexGrid() {
		super();
	}
	
	@Override
	protected Area createCellShape(int size) {
		return new Area(createShape(height, sideSize, topWidth));
	}
	
	@Override
	public Dimension getCellOffset() {
		return cellOffset;
	}
	
	@Override
	public double getCellHeight() {
		return height*2;
	}
	
	@Override
	public double getCellWidth() {
		return topWidth + sideSize*2;
	}
	
	@Override
	public int[] getFacingAngles() {
		return FACING_ANGLES;
	}
	
	@Override
	public ZoneWalker createZoneWalker() {
		return new AStarHexEuclideanWalker(getZone());
	}
	
	@Override
	public void setSize(int size) {
		
		topWidth = size/2;
		sideSize = topWidth/2;
		height = (int)(topWidth * Math.cos(Math.toRadians(30)));
		
		scaledHex = null;
		
		//cellOffset = new Dimension((int)(sideSize/2), -height);
		cellOffset = new Dimension(0, -height);

		// TODO: this super checks min and max size limits, it should realy happen before we make the calcs
		// but we also need to create the shape before calling super so that createCellShape() doesn't break
		super.setSize(size);
	}
	
	private void createShape(double scale) {

		if (lastScale == scale && scaledHex != null) {
			return;
		}
		
		scaledHeight = height*scale;
		scaledTopWidth = topWidth*scale;
		scaledSideSize = sideSize*scale;
		
		scaledHex = new GeneralPath();
		scaledHex.moveTo(0, (int)scaledHeight);
		scaledHex.lineTo((int)scaledSideSize, 0);
		scaledHex.lineTo((int)(scaledSideSize + scaledTopWidth), 0);
		scaledHex.lineTo((int)(scaledSideSize + scaledTopWidth + scaledSideSize), (int)scaledHeight);

		lastScale = scale;
	}
	
	private GeneralPath createShape(double height, double sideSize, double topWidth) {

		GeneralPath hex = new GeneralPath();
		hex.moveTo(0, (int)height);
		hex.lineTo((int)sideSize, 0);
		hex.lineTo((int)(sideSize + topWidth), 0);
		hex.lineTo((int)(sideSize + topWidth + sideSize), (int)height);
		hex.lineTo((int)(sideSize + topWidth), (int)(height*2));
		hex.lineTo((int)(sideSize), (int)(height*2));

		return hex;
	}
	
	@Override
	public CellPoint convert(ZonePoint zp) {

		int xSect = (int)(zp.x / (sideSize + topWidth));
		int ySect = (int)((zp.y / (2*height)) + (zp.y < 0 ? -1 : 0));

		int xPxl = Math.abs((int)(zp.x - xSect * (sideSize + topWidth)));
		int yPxl = Math.abs((int)(zp.y - ySect * (2 * height)));

		int gridX = xSect;
		int gridY = ySect;
		
		double m = sideSize / height;
		
//		System.out.format("gx:%d gy:%d px:%d py:%d m:%f\n", xSect, ySect, xPxl, yPxl, m);

		switch (xSect % 2) {
		case 0:

			if (xPxl < sideSize - yPxl * m) {
				gridX = xSect - 1;
				gridY = ySect - 1;
			}
			
			if (xPxl < - sideSize - xPxl * m) {
				gridX = xSect - 1;
				gridY = ySect;
			}
			
			break;
		case 1:
			
			if (yPxl >= height) {
				
				if (xPxl < (2 * sideSize - yPxl * m)) {
					gridX = xSect - 1;
					gridY = ySect;
				} else {
					gridX = xSect;
					gridY = ySect;
				}
				
			} else {
				
				if (xPxl < yPxl * m) {
					gridX = xSect - 1;
					gridY = ySect;
				} else {
					gridX = xSect;
					gridY = ySect - 1;
				}
			}
			
			break;
		}
		
		return new CellPoint(gridX, gridY);
	}

	@Override
	public ZonePoint convert(CellPoint cp) {

		int x = (int)Math.round(cp.x * (sideSize + topWidth)) + getOffsetX();
		int y = cp.y * 2 * height + (cp.x % 2 == 0  ||  cp.x < 0 ? 1 : 2) * height + getOffsetY();
		
		return new ZonePoint(x, y);
	}

	@Override
	public GridCapabilities getCapabilities() {
		return GRID_CAPABILITIES;
	}

	@Override
	public int getTokenSpace() {
		return (int)(height * 2);
	}
	
	@Override
	public void draw(ZoneRenderer renderer, Graphics2D g, Rectangle bounds) {

		double scale = renderer.getScale();

		double scaledSize = scale * getSize();
		
        createShape(scale);
        
        int offX = (int)(renderer.getViewOffsetX() + getOffsetX()*scale);
        int offY = (int)(renderer.getViewOffsetY() + getOffsetY()*scale);

        int count = 0;
        
//        g.setColor(Color.red);
//        CellPoint cp = new CellPoint(0,0);
//        ZonePoint zp = convert(cp);
//        ScreenPoint sp = ScreenPoint.fromZonePoint(renderer, zp.x, zp.y);
//        g.fillOval(sp.x-4, sp.y-4, 8, 8);
//        g.drawLine(sp.x, 0, sp.x, renderer.getSize().height);
//        g.drawLine(0, sp.y, renderer.getSize().width, sp.y);

        Object oldAntiAlias = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(getZone().getGridColor()));
		for (double y = offY%(scaledHeight*2) - (scaledHeight*2); y < renderer.getSize().height; y += scaledHeight) {

			double offsetX = (int)(count % 2 == 0 ? 0 : -(scaledSideSize + scaledTopWidth));
			count ++;

			for (double x = offX%(scaledSize+scaledTopWidth) - (scaledSize+scaledTopWidth); x < renderer.getSize().width + scaledTopWidth; x += scaledSideSize*2 + scaledTopWidth*2) {

				g.translate(x + offsetX, y);
				g.draw(scaledHex);
				g.translate(-(x + offsetX), -y);
			}
		}
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAntiAlias);
	}

}
