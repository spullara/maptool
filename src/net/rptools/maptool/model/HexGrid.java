package net.rptools.maptool.model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;

import net.rptools.maptool.client.ScreenPoint;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;

public class HexGrid extends Grid {

	private static final GridCapabilities GRID_CAPABILITIES= new GridCapabilities() {
		public boolean isPathingSupported() {return false;}
		public boolean isSnapToGridSupported() {return true;}
	};
	
	private static final int[] FACING_ANGLES = new int[] {
		-135, -90, -45, 45, 90, 135
	};
	
	private double sideSize;
	private int height;
	private int topWidth;
	
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
	public Dimension getCellOffset() {
		return cellOffset;
	}
	
	@Override
	public double getCellHeight() {
		return height*2;
	}
	
	@Override
	public double getCellWidth() {
		return getSize() + sideSize;
	}
	
	@Override
	public int getDefaultGridSize() {
		return 50;
	}
	
	@Override
	public int[] getFacingAngles() {
		return FACING_ANGLES;
	}
	
	@Override
	public void setSize(int size) {
		super.setSize(size);
		
		topWidth = size/2;
		sideSize = (int)(topWidth*Math.sin(Math.toRadians(30)));
		height = (int)(topWidth * Math.cos(Math.toRadians(30)));
		
		scaledHex = null;
		
		cellOffset = new Dimension((int)(sideSize/2), -height);
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

		int xSect = (int)(zp.x / (sideSize + topWidth));
		int ySect = (int)(zp.y / (2*height)) + (zp.y < 0 ? -1 : 0);

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

		int x = (int)(cp.x * (sideSize + topWidth));
		int y = cp.y * 2 * height + (cp.x % 2 == 0  ||  cp.x < 0 ? 1 : 2) * height;
		
		return new ZonePoint(x, y);
	}

	@Override
	public GridCapabilities getCapabilities() {
		return GRID_CAPABILITIES;
	}

	@Override
	public int getTokenSpace() {
		return (int)(getSize() - sideSize);
	}
	
	@Override
	public void draw(ZoneRenderer renderer, Graphics2D g, Rectangle bounds) {

		double scale = renderer.getScale();

		double cellWidth = getCellWidth() * scale;
		double cellHeight = getCellHeight() * scale;
		double scaledSize = scale * getSize();
		
        createShape(scale);
        //System.out.println(scaledHeight + " - " + scale + " - " + renderer.getZoneScale().getIndex() + " - " + renderer.getZoneScale().SCALE_1TO1_INDEX);

//        g.translate(0, -height);
//        g.setColor(Color.blue);
//        g.fill(topLeftArea);
//        g.fill(topRightArea);
//        g.fill(bottomLeftArea);
//        g.fill(bottomRightArea);
//        g.translate(0, height);
        
        int offX = (int)(renderer.getViewOffsetX() + getOffsetX()*scale);
        int offY = (int)(renderer.getViewOffsetY() + getOffsetY()*scale);

        int count = 0;
        
        g.setColor(Color.red);
        CellPoint cp = new CellPoint(0,0);
        ZonePoint zp = convert(cp);
        ScreenPoint sp = ScreenPoint.fromZonePoint(renderer, zp.x, zp.y);
        g.fillOval(sp.x-4, sp.y-4, 8, 8);
        g.drawLine(sp.x, 0, sp.x, renderer.getSize().height);
        g.drawLine(0, sp.y, renderer.getSize().width, sp.y);

        g.setColor(Color.blue);
        cp = new CellPoint(15,0);
        zp = convert(cp);
        sp = ScreenPoint.fromZonePoint(renderer, zp.x, zp.y);
        g.fillOval(sp.x-4, sp.y-4, 8, 8);
        g.drawLine(sp.x, 0, sp.x, renderer.getSize().height);
        g.drawLine(0, sp.y, renderer.getSize().width, sp.y);

//        g.setColor(Color.yellow);
//        double ds = topWidth + sideSize*2;
//        for (int lx = 0; lx < 15; lx ++) {
//            
//            g.drawLine((int)(lx*ds), 0, (int)(lx*ds), renderer.getSize().height);
//        	
//        }
        
//        cp = new CellPoint(1,0);
//        zp = convert(cp);
//        sp = ScreenPoint.fromZonePoint(renderer, zp.x, zp.y);
//        g.fillOval(sp.x-4, sp.y-4, 8, 8);

//        g.setColor(Color.blue);
//        sp = ScreenPoint.fromZonePoint(renderer, getSize() - topWidth, 0);
//        g.drawLine(sp.x, 0, sp.x, renderer.getSize().height);
        
        g.setColor(new Color(getZone().getGridColor()));
		for (double y = offY%(scaledHeight*2) - (scaledHeight*2); y < renderer.getSize().height + cellHeight * 2; y += scaledHeight) {

			double offsetX = (int)(count % 2 == 0 ? 0 : -(scaledSideSize + scaledTopWidth));
			count ++;

			for (double x = offX%(scaledSize+scaledTopWidth) - (scaledSize+scaledTopWidth); x < renderer.getSize().width + cellWidth*2; x += scaledSideSize*2 + scaledTopWidth*2) {

				g.translate(x + offsetX, y);
				g.draw(scaledHex);
				g.translate(-(x + offsetX), -y);
			}
		}
	}

}
