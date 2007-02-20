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

/**
 * An abstract hex grid class that uses generic cartesian-coordinates
 * for calculations to allow for various hex grid orientations.
 * 
 * The v-axis points along the direction of edge to edge hexes
 */
public abstract class HexGrid extends Grid {

	protected static BufferedImage pathHighlight;

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
		public boolean isPathLineSupported() {return true;}
	};
	
	protected int[] facingAngles;
	
	// Hex defining variables for convenience
	protected double edgeProjection;
	protected int minorRadius;
	protected double edgeLength;
	
	// Hex defining variables scaled for zoom
	private double scaledEdgeProjection;
	private double scaledMinorRadius;
	private double scaledEdgeLength;
	private GeneralPath scaledHex;
	private double lastScale = -1;
	
	/**
	 * The offset required to translate from the center of a cell
	 * to the top right (x_min, y_min) of the cell's bounding rectangle.
	 */
	private Dimension cellOffset;
	
	public HexGrid() {
		super();
		initFacingAngles();
	}
	
	@Override
	protected Area createCellShape(int size) {
		return new Area(createShape(minorRadius, edgeProjection, edgeLength));
	}
	
	/**
	 * @return Distance from the center to edge of a hex
	 */
	public double getVRadius() {
		return minorRadius;
	}
	
	/**
	 * @return Distance from the center to vertex of a hex
	 */
	public double getURadius() {
		return edgeLength;
	}
	
	@Override
	public Dimension getCellOffset() {
		return cellOffset;
	}
	
	/**
	 * A generic form of getCellOffset() where V is the axis of edge to edge hexes.
	 * @return The offset required to translate from the center of a cell
	 * to the least edge (v_min)
	 */
	public double getCellOffsetV() {
		return -minorRadius;
	}
	
	/**
	 * A generic form of getCellOffset() where U is the axis perpendicular
	 * to the line of edge to edge hexes.
	 * @return The offset required to translate from the center of a cell
	 * to the least vertex (u_min)
	 */
	public double getCellOffsetU() {
		return -(int)edgeLength;
	}

	
	/**
	 * @param numCells Number of cells to average over
	 * @return The midpoint along the v-axis of the cell group.  This will
	 * always land in the center of a cell
	 */
	public double getCellGroupCenterVComponent(int numCells) {
		return minorRadius*numCells;
	}

	/**
	 * Currently only works for "square" cell groups (ie numCellsU == numCellsV)
	 * @param numCellsU Number of cells to "average" over
	 * @return The closest cell-center, or vertex to the midpoint along
	 * the u-axis of the cell group
	 */
	public double getCellGroupCenterUComponent(int numCellsU ) {
		if( numCellsU % 2 == 0 ) {
			return (edgeLength + edgeProjection)*numCellsU/2;
		}
		return ((edgeLength + edgeProjection)*numCellsU + edgeProjection)/2;
	}
	
	/**
	 * The offset required to translate from the center of a cell
	 * to the top right (x_min, y_min) of the cell's bounding rectangle.
	 */
	protected abstract Dimension setCellOffset();
	

	@Override
	public int[] getFacingAngles() {
		return facingAngles;
	}
	
	@Override
	public void setSize(int size) {
		// Using size as the edge-to-edge distance or 
		// minor diameter of the hex.
		
		size = constrainSize(size);

		edgeLength = size/Math.sqrt(3);
		edgeProjection = edgeLength/2;
		minorRadius = size/2;
		
		scaledHex = null;

		// Cell offset gives the offset to apply to the 
		// cell zone coords to draw images/tokens
		cellOffset = setCellOffset();

		super.setSize(size);

	}
			
	protected void createShape(double scale) {

		if (lastScale == scale && scaledHex != null) {
			return;
		}
		
		scaledMinorRadius = minorRadius*scale;
		scaledEdgeLength = edgeLength*scale;
		scaledEdgeProjection = edgeProjection*scale;
		
		scaledHex = createShape(scaledMinorRadius, scaledEdgeProjection, scaledEdgeLength);

		lastScale = scale;
	}
	
	private GeneralPath createShape(double minorRadius, double edgeProjection, double edgeLength) {

		GeneralPath hex = new GeneralPath();
		hex.moveTo(0, (int)minorRadius);
		hex.lineTo((int)edgeProjection, 0);
		hex.lineTo((int)(edgeProjection + edgeLength), 0);
		hex.lineTo((int)(edgeProjection + edgeLength + edgeProjection), (int)minorRadius);
		
		orientHex(hex);

		return hex;
	}
	
	/**
	 * Default orientation is for a vertical hex grid
	 * Override for other orientations
	 * @param hex
	 */
	protected void orientHex(GeneralPath hex) {
		return;
	}

	@Override
	public GridCapabilities getCapabilities() {
		return GRID_CAPABILITIES;
	}

	@Override
	public int getTokenSpace() {
		return (int)(minorRadius * 2);
	}
	
	protected abstract void initFacingAngles();
	protected abstract void setGridDrawTranslation(Graphics2D g, double u, double v);
	protected abstract double getRendererSizeU(ZoneRenderer renderer);
	protected abstract double getRendererSizeV(ZoneRenderer renderer);
	protected abstract int getOffV(ZoneRenderer renderer);
	protected abstract int getOffU(ZoneRenderer renderer);
	
	@Override
	public void draw(ZoneRenderer renderer, Graphics2D g, Rectangle bounds) {

        createShape(renderer.getScale());
        
        int offU = getOffU(renderer);
        int offV = getOffV(renderer);

        int count = 0;

        Object oldAntiAlias = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(getZone().getGridColor()));
                
		for (double v = offV%(scaledMinorRadius*2) - (scaledMinorRadius*2);
				v < getRendererSizeV(renderer);
				v += scaledMinorRadius) {

			double offsetU = (int)(count % 2 == 0 ? 0 :
					-(scaledEdgeProjection + scaledEdgeLength));
					count ++;

			for (double u = offU%(3 * scaledEdgeLength) - (3 * scaledEdgeLength);
					u < getRendererSizeU(renderer) + scaledEdgeLength;
					u += scaledEdgeProjection*2 + scaledEdgeLength*2) {

				setGridDrawTranslation( g, u + offsetU, v);
				g.draw(scaledHex);
				setGridDrawTranslation( g, -(u + offsetU), -v);
			}
		}
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAntiAlias);
	}
	
	/**
	 * Generic form of getOffsetX for ease of transforming to other
	 * grid orientations.
	 * @return The U component of the grid's offset.
	 */
	protected abstract int getOffsetU();
	
	/**
	 * Generic form of getOffsetY for ease of transforming to other
	 * grid orientations.
	 * @return The V component of the grid's offset.
	 */
	protected abstract int getOffsetV();
	
	/**
	 * A method used by HexGrid.convert(ZonePoint zp) to allow for alternate grid orientations
	 * @return Coordinates in Cell-space of the ZonePoint
	 */
	protected CellPoint convertZP(int zpU, int zpV) {
		int xSect;
		int ySect;
		
		int offsetZpU = zpU - getOffsetU();
		int offsetZpV = zpV - getOffsetV();
		
		if (offsetZpU < 0) {
			xSect = (int)(offsetZpU / (edgeProjection + edgeLength)) - 1;			
		} else {
			xSect = (int)(offsetZpU / (edgeProjection + edgeLength));			
		}
		if (offsetZpV < 0) {
			if (Math.abs(xSect) % 2 == 1)
				ySect = (int)((offsetZpV - minorRadius) / (2*minorRadius)) - 1;
			else
				ySect = (int)(offsetZpV / (2*minorRadius)) - 1;
		} else {
			if (Math.abs(xSect) % 2 == 1)
				ySect = (int)((offsetZpV - minorRadius)/ (2*minorRadius));
			else
				ySect = (int)(offsetZpV / (2*minorRadius));
		}

		int xPxl = Math.abs((int)(offsetZpU - xSect * (edgeProjection + edgeLength)));
		int yPxl = Math.abs((int)(offsetZpV - ySect * (2 * minorRadius)));

		int gridX = xSect;
		int gridY = ySect;
		
		double m = edgeProjection / minorRadius;
		
//		System.out.format("gx:%d gy:%d px:%d py:%d m:%f\n", xSect, ySect, xPxl, yPxl, m);
//		System.out.format("gx:%d gy:%d px:%d py:%d\n", xSect, ySect, zp.x, zp.y);

		switch (Math.abs(xSect) % 2) {
		case 0:
			if ( yPxl <= minorRadius ) {
				if (xPxl < edgeProjection - yPxl * m) {
					gridX = xSect - 1;
					gridY = ySect - 1;
				}
			} else {
				if (xPxl < (yPxl - minorRadius) * m) {
					gridX = xSect - 1;
					//gridY = ySect;
				}
			}
			break;
		case 1:
			if (yPxl >= minorRadius) {
				if (xPxl < (edgeProjection - (yPxl - minorRadius) * m) ) {
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

	
	/**
	 * A method used by HexGrid.convert(CellPoint cp) to allow for alternate grid orientations
	 * @return A ZonePoint positioned at the center of the Hex
	 */
	protected ZonePoint convertCP(int cpU, int cpV) {
		int u,v;
		
		u = (int)Math.round(cpU * (edgeProjection + edgeLength) + edgeLength) + getOffsetU();			
		
		v = cpV * 2 * minorRadius + (Math.abs(cpU) % 2 == 0 ? 1 : 2)* minorRadius + getOffsetV();
		
		return new ZonePoint(u, v);
	}

}
