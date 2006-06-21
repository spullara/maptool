package net.rptools.maptool.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;

import net.rptools.maptool.client.ScreenPoint;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;

public class HexGrid extends Grid {

	private static final GridCapabilities GRID_CAPABILITIES= new GridCapabilities() {
		public boolean isPathingSupported() {return false;}
		public boolean isSnapToGridSupported() {return false;}
	};
	
	private static final int[] FACING_ANGLES = new int[] {
		-135, -90, -45, 45, 90, 135
	};
	
	private Polygon topLeftArea;
	private Polygon bottomLeftArea;
	private Polygon topRightArea;
	private Polygon bottomRightArea;
	private int sideSize;
	private int height;
	private int topWidth;
	
	private GeneralPath scaledHex;
	private double scaledSideSize;
	private double scaledHeight;
	private double scaledTopWidth;
	private double lastScale = -1;
	
	public HexGrid() {
		super();
	}
	
	@Override
	public int getCellHeight() {
		return height*2;
	}
	
	@Override
	public int getCellWidth() {
		return getSize();
	}
	
	@Override
	public int getDefaultGridSize() {
		return 80;
	}
	
	@Override
	public int[] getFacingAngles() {
		return FACING_ANGLES;
	}
	
	@Override
	public void setSize(int size) {
		super.setSize(size);
		
		topWidth = (size*3/4)/2;
		sideSize = (size/8);
		height = (int)(topWidth * Math.sin(Math.toRadians(53)));
		
		// Create the info necessary to calculate cells
		topLeftArea = new Polygon();
		topLeftArea.addPoint(0, 0);
		topLeftArea.addPoint(sideSize, 0);
		topLeftArea.addPoint(0, height);
		topLeftArea.translate(0, -height);
		
		bottomLeftArea = new Polygon();
		bottomLeftArea.addPoint(0, height);
		bottomLeftArea.addPoint(0, height*2);
		bottomLeftArea.addPoint(sideSize, height*2);
		bottomLeftArea.translate(0, -height);
		
		topRightArea = new Polygon();
		topRightArea.addPoint(sideSize + topWidth, 0);
		topRightArea.addPoint(size, 0);
		topRightArea.addPoint(size, height);
		topRightArea.addPoint(sideSize*2 + topWidth, height);
		topRightArea.translate(0, -height);
		
		bottomRightArea = new Polygon();
		bottomRightArea.addPoint(sideSize*2 + topWidth, height);
		bottomRightArea.addPoint(size, height);
		bottomRightArea.addPoint(size, height*2);
		bottomRightArea.addPoint(sideSize + topWidth, height*2);
		bottomRightArea.translate(0, -height);
		
		scaledHex = null;
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
	
	@Override
	public CellPoint convert(ZonePoint zp) {

		int size = getSize();
		
		// Strategy: cut up the zone into squares, then calculate which hex the exact point is in
		int gridX = (int)(zp.x / size) * 2;
		int gridY = (int)((zp.y + height) / (height*2));

		int offsetX = zp.x % size;
		int offsetY = (int)((zp.y + height) % (height*2));
		if (topLeftArea.contains(offsetX, offsetY)) {
			gridX --;
			//System.out.println("\ttl gx" + gridX );
		} else if (topRightArea.contains(offsetX, offsetY)) {
			gridX++;
			//System.out.println("\ttr gx:" + gridX);
		} else if (bottomLeftArea.contains(offsetX, offsetY)) {
			gridX --;
			gridY ++;
			//System.out.println("\tbl gx:" + gridX + " gy:" + gridY);
		} else if (bottomRightArea.contains(offsetX, offsetY)) {
			gridX ++;
			gridY ++;
			//System.out.println("\tbr gx:" + gridX + " gy:" + gridY);
		}
		
//		System.out.println("ox:" + origX + " oy:" + origY + " zp:" + zp + " gx:" + gridX + " gy:" + gridY + " ox:" + offsetX + " oy:" + offsetY);		
		return new CellPoint(gridX, gridY);
	}

	@Override
	public ZonePoint convert(CellPoint cp) {
		
		int x = (int)(cp.x * (sideSize + topWidth));
		int y = (int)(cp.y * height * 2) - (x % 2 == 1 ? height : 0);

		//System.out.println (cp.x+","+cp.y + " - " + x + "," + y);
		return new ZonePoint(x, y);
	}

	@Override
	public GridCapabilities getCapabilities() {
		return GRID_CAPABILITIES;
	}

	@Override
	public int getTokenSpace() {
		return getSize() - topWidth - sideSize;
	}
	
	@Override
	public void draw(ZoneRenderer renderer, Graphics2D g, Rectangle bounds) {

		double scale = renderer.getScale();

		double cellWidth = getCellWidth() * scale;
		double cellHeight = getCellHeight() * scale;
		
        createShape(scale);
        //System.out.println(scaledHeight + " - " + scale + " - " + renderer.getZoneScale().getIndex() + " - " + renderer.getZoneScale().SCALE_1TO1_INDEX);
        
        int offX = (int)(renderer.getViewOffsetX() % cellWidth + getOffsetX()*scale);
        int offY = (int)(renderer.getViewOffsetY() % cellHeight + getOffsetY()*scale);

        int count = 0;
        
        g.setColor(Color.red);
        CellPoint cp = new CellPoint(0,0);
        ZonePoint zp = convert(cp);
        ScreenPoint sp = ScreenPoint.fromZonePoint(renderer, zp.x, zp.y);
        g.fillOval(sp.x-4, sp.y-4, 8, 8);
        g.drawLine(sp.x, 0, sp.x, renderer.getSize().height);
        g.drawLine(0, sp.y, renderer.getSize().width, sp.y);
//
//        cp = new CellPoint(1,0);
//        zp = convert(cp);
//        sp = ScreenPoint.fromZonePoint(renderer, zp.x, zp.y);
//        g.fillOval(sp.x-4, sp.y-4, 8, 8);
        
//        g.setColor(Color.blue);
//        sp = ScreenPoint.fromZonePoint(renderer, getSize() - topWidth, 0);
//        g.drawLine(sp.x, 0, sp.x, renderer.getSize().height);
        
        g.setColor(new Color(getZone().getGridColor()));
        g.translate(offX - cellWidth, offY - cellHeight + scaledHeight);
		for (double y = 0; y < renderer.getSize().height + cellHeight * 2; y += scaledHeight) {

			double offsetX = (int)(count % 2 == 0 ? 0 : scaledSideSize + scaledTopWidth);
			count ++;

			for (double x = 0; x < renderer.getSize().width + cellWidth*2; x += scaledTopWidth * 2 + scaledSideSize * 2) {

				g.translate(x + offsetX, y);
				g.draw(scaledHex);
				g.translate(-(x + offsetX), -y);
			}
		}
		
		g.translate(-offX + cellWidth, -offY + cellHeight -scaledHeight);
	}

}
