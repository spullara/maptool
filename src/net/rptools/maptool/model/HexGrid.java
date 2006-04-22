package net.rptools.maptool.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import net.rptools.maptool.client.ScreenPoint;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;

public class HexGrid extends Grid {

	private static final GridCapabilities GRID_CAPABILITIES= new GridCapabilities() {
		public boolean isPathingSupported() {return false;}
		public boolean isSnapToGridSupported() {return false;}
	};
	
	private Polygon topLeftArea;
	private Polygon bottomLeftArea;
	private Polygon topRightArea;
	private Polygon bottomRightArea;
	private int sideSize;
	private int height;
	private int topWidth;
	
	private GeneralPath scaledHex;
	private int scaledSideSize;
	private int scaledHeight;
	private int scaledTopWidth;
	private double lastScale = -1;
	
	public HexGrid(Zone zone) {
		super(zone);
	}
	
	@Override
	public void setSize(int size) {
		super.setSize(size);
		
		height = size/3;
		topWidth = (size*3/4)/2;
		sideSize = (size/8);
		
		// Create the info necessary to calculate cells
		topLeftArea = new Polygon();
		topLeftArea.addPoint(0, 0);
		topLeftArea.addPoint(sideSize, 0);
		topLeftArea.addPoint(0, height);
		
		bottomLeftArea = new Polygon();
		bottomLeftArea.addPoint(0, height);
		bottomLeftArea.addPoint(0, height*2);
		bottomLeftArea.addPoint(sideSize, height*2);
		
		topRightArea = new Polygon();
		topRightArea.addPoint(sideSize + topWidth, 0);
		topRightArea.addPoint(size, 0);
		topRightArea.addPoint(size, height);
		topRightArea.addPoint(sideSize*2 + topWidth, height);
		
		bottomRightArea = new Polygon();
		bottomRightArea.addPoint(sideSize*2 + topWidth, height);
		bottomRightArea.addPoint(size, height);
		bottomRightArea.addPoint(size, height*2);
		bottomRightArea.addPoint(sideSize + topWidth, height*2);
	}
	
	private void createShape(double scale) {

		if (lastScale == scale) {
			return;
		}
		
		int size = (int)(getSize() * scale);

//		private static int sideSize = 5;
//		private static int height = 13;
//		private static int topWidth = 15;
		scaledHeight = size/3;
		scaledTopWidth = (size*3/4)/2;
		scaledSideSize = (size/8);
		
		scaledHex = new GeneralPath();
		scaledHex.moveTo(0, scaledHeight);
		scaledHex.lineTo(scaledSideSize, 0);
		scaledHex.lineTo(scaledSideSize + scaledTopWidth, 0);
		scaledHex.lineTo(scaledSideSize + scaledTopWidth + scaledSideSize, scaledHeight);

		lastScale = scale;
	}
	
	@Override
	public CellPoint convert(ZonePoint zp) {

		int size = getSize();
		
		// Strategy: cut up the zone into squares, then calculate which hex the exact point is in
		int gridX = zp.x / size;
		int gridY = zp.y / height*2;
		
		int offsetX = zp.x % size;
		int offsetY = zp.y % height*2;
		
		if (topLeftArea.contains(offsetX, offsetY)) {
			gridX --;
		} else if (topRightArea.contains(offsetX, offsetY)) {
			gridX++;
		} else if (bottomLeftArea.contains(offsetX, offsetY)) {
			gridX --;
			gridY ++;
		} else if (bottomRightArea.contains(offsetX, offsetY)) {
			gridX ++;
			gridY ++;
		}
		
		return new CellPoint(gridX, gridY);
	}

	@Override
	public ZonePoint convert(CellPoint cp) {
		return new ZonePoint(0, 0);
	}

	@Override
	public GridCapabilities getCapabilities() {
		return GRID_CAPABILITIES;
	}

	@Override
	public int getTokenSpace() {
		return (getSize() * 3/4)/2 + getSize()/16; // topWidth + sideSize/2
	}
	
	@Override
	public void draw(ZoneRenderer renderer, Graphics2D g, Rectangle bounds) {

		double scale = renderer.getScale();
        double gridSize = getSize() * scale;

        createShape(scale);
        
        int offX = (int)(renderer.getViewOffsetX() % gridSize + getOffsetX()*scale);
        int offY = (int)(renderer.getViewOffsetY() % gridSize + getOffsetY()*scale);

        int startCol = (int)((int)(bounds.x / gridSize) * gridSize);
        int startRow = (int)((int)(bounds.y / gridSize) * gridSize);

        int count = ((int)(renderer.getViewOffsetY() / gridSize)) % 2 == 0 ? 0 : 1;
        
        g.setColor(Color.red);
        ScreenPoint sp = ScreenPoint.fromZonePoint(renderer, (topWidth + sideSize)/2, height);
        g.fillOval(sp.x-4, sp.y-4, 8, 8);
        
//        g.setColor(Color.red);
//        sp = ScreenPoint.fromZonePoint(renderer, 0, 0);
//        g.drawLine(sp.x, 0, sp.x, renderer.getSize().height);
        
        g.setColor(new Color(getZone().getGridColor()));
        g.translate(offX-gridSize, offY-gridSize);
		for (int y = 0; y < renderer.getSize().height + gridSize * 2; y += scaledHeight) {

			int offsetX = (count % 2 == 0 ? 0 : scaledSideSize + scaledTopWidth);
			count ++;

			for (int x = 0; x < renderer.getSize().width + gridSize*2; x += scaledTopWidth * 2 + scaledSideSize * 2) {

				g.translate(x + offsetX, y);
				g.draw(scaledHex);
				g.translate(-(x + offsetX), -y);
			}
		}
		g.translate(-offX+gridSize, -offY+gridSize);
	}

}
