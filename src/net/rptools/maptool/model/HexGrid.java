package net.rptools.maptool.model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.IOException;

import net.rptools.lib.image.ImageUtil;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.client.walker.ZoneWalker;
import net.rptools.maptool.client.walker.astar.AStarHexEuclideanWalker;

public class HexGrid extends Grid {

	private static BufferedImage pathHighlight;

	static {
		try {
			pathHighlight = ImageUtil.getCompatibleImage("net/rptools/maptool/client/image/hexBorder.png");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	private static final GridCapabilities GRID_CAPABILITIES= new GridCapabilities() {
		public boolean isPathingSupported() {return true;}
		public boolean isSnapToGridSupported() {return true;}
	};
	
	private static final int[] FACING_ANGLES = new int[] {
		-150, -90, -30, 30, 90, 150
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
	public BufferedImage getCellHighlight() {
		return pathHighlight;
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
		return topWidth*2; // topWidth is sideSize*2;
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
		// Using size as the face-to-face distance or 
		// minor diameter of the hex.
		topWidth = size/Math.sqrt(3);
		sideSize = topWidth/2;
		height = size/2;
		
		scaledHex = null;

		// Cell offset gives the offset to apply to the 
		// cell zone coords to draw images/tokens
		cellOffset = new Dimension((int)-topWidth, -height);

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
		
		scaledHex = createShape(scaledHeight, scaledSideSize, scaledTopWidth);
		/*
		scaledHex = new GeneralPath();
		scaledHex.moveTo(0, (int)scaledHeight);
		scaledHex.lineTo((int)scaledSideSize, 0);
		scaledHex.lineTo((int)(scaledSideSize + scaledTopWidth), 0);
		scaledHex.lineTo((int)(scaledSideSize + scaledTopWidth + scaledSideSize), (int)scaledHeight);
		*/
		lastScale = scale;
	}
	
	private GeneralPath createShape(double height, double sideSize, double topWidth) {

		GeneralPath hex = new GeneralPath();
		hex.moveTo(0, (int)height);
		hex.lineTo((int)sideSize, 0);
		hex.lineTo((int)(sideSize + topWidth), 0);
		hex.lineTo((int)(sideSize + topWidth + sideSize), (int)height);
		//hex.lineTo((int)(sideSize + topWidth), (int)(height*2));
		//hex.lineTo((int)(sideSize), (int)(height*2));

		return hex;
	}
	
	@Override
	public CellPoint convert(ZonePoint zp) {
		int xSect;
		int ySect;
		
		if (zp.x < 0) {
			xSect = (int)(zp.x / (sideSize + topWidth)) - 1;			
		} else {
			xSect = (int)(zp.x / (sideSize + topWidth));			
		}
		if (zp.y < 0) {
			if (xSect % 2 == 1)
				ySect = (int)((zp.y - height) / (2*height)) - 1;
			else
				ySect = (int)(zp.y / (2*height)) - 1;
		} else {
			if (xSect % 2 == 1)
				ySect = (int)((zp.y - height)/ (2*height));
			else
				ySect = (int)(zp.y / (2*height));
		}

		int xPxl = Math.abs((int)(zp.x - xSect * (sideSize + topWidth)));
		int yPxl = Math.abs((int)(zp.y - ySect * (2 * height)));

		int gridX = xSect;
		int gridY = ySect;
		
		double m = sideSize / height;
		
//		System.out.format("gx:%d gy:%d px:%d py:%d m:%f\n", xSect, ySect, xPxl, yPxl, m);
//		System.out.format("gx:%d gy:%d px:%d py:%d\n", xSect, ySect, zp.x, zp.y);

		switch (xSect % 2) {
		case 0:
			if ( yPxl <= height ) {
				if (xPxl < sideSize - yPxl * m) {
					gridX = xSect - 1;
					gridY = ySect - 1;
				}
			} else {
				if (xPxl < (yPxl - height) * m) {
					gridX = xSect - 1;
					//gridY = ySect;
				}
			}
			break;
		case 1:
			if (yPxl >= height) {
				if (xPxl < (sideSize - (yPxl - height) * m) ) {
					gridX = xSect - 1;
					//gridY = ySect;
				} else {
					//gridX = xSect;
					//gridY = ySect;
				}
			} else {
				if (xPxl < (yPxl * m) ) {
					gridX = xSect - 1;
					//gridY = ySect;
				} else {
					//gridX = xSect;
					gridY = ySect - 1;
				}
			}
			
			break;
		}
//		System.out.format("gx:%d gy:%d\n", gridX, gridY);
		
		return new CellPoint(gridX, gridY);
	}

	/* (non-Javadoc)
	 * @see net.rptools.maptool.model.Grid#convert(net.rptools.maptool.model.CellPoint)
	 * 
	 * Returns the center point of the hex as the ZonePoint. cellOffset is
	 * used to position tokens correctly.
	 */
	@Override
	public ZonePoint convert(CellPoint cp) {
		int x,y;
		
		x = (int)Math.round(cp.x * (sideSize + topWidth) + topWidth) + getOffsetX();			
		
		y = cp.y * 2 * height + (cp.x % 2 == 0 ? 1 : 2)* height + getOffsetY();
		
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

//		double scaledSize = scale * getSize();
		
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

			for (double x = offX%(3 * scaledTopWidth) - (3 * scaledTopWidth); x < renderer.getSize().width + scaledTopWidth; x += scaledSideSize*2 + scaledTopWidth*2) {

				g.translate(x + offsetX, y);
				g.draw(scaledHex);
				g.translate(-(x + offsetX), -y);
			}
		}
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAntiAlias);
	}

}
