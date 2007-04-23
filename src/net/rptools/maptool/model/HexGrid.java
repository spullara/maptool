package net.rptools.maptool.model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Set;

import net.rptools.lib.image.ImageUtil;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.util.HexGridUtil;

/**
 * An abstract hex grid class that uses generic cartesian-coordinates
 * for calculations to allow for various hex grid orientations.
 * 
 * The v-axis points along the direction of edge to edge hexes
 */
public abstract class HexGrid extends Grid {
	
	// A regular hexagon is one where all angles are 60 degrees.
	// the ratio = minor_radius / edge_length
	public static double REGULAR_HEX_RATIO = Math.sqrt(3)/2;

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
		public boolean isSecondDimensionAdjustmentSupported() {return true;}
	};
	
	/**
	 * minorRadius / edgeLength
	 */
	private double hexRatio = REGULAR_HEX_RATIO;
	
	// Hex defining variables for convenience
	private double edgeProjection;
	private double minorRadius;
	private double edgeLength;
	
	// Hex defining variables scaled for zoom
	private double scaledEdgeProjection;
	private double scaledMinorRadius;
	private double scaledEdgeLength;
	private transient GeneralPath scaledHex;
	private double lastScale = -1;
	
	/**
	 * The offset required to translate from the center of a cell
	 * to the top right (x_min, y_min) of the cell's bounding rectangle.
	 */
	private Dimension cellOffset;
	
	public HexGrid() {
		super();
	}
	
	@Override
	protected Area createCellShape(int size) {
		// don't use size.  it has already been used to set the minorRadius
		// and will only introduce a rounding error.
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
		return edgeLength/2 + edgeProjection;
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
		return -getVRadius();
	}
	
	/**
	 * A generic form of getCellOffset() where U is the axis perpendicular
	 * to the line of edge to edge hexes.
	 * @return The offset required to translate from the center of a cell
	 * to the least vertex (u_min)
	 */
	public double getCellOffsetU() {
		return -getURadius();
	}

	
	/**
	 * @param numCells Number of cells to average over
	 * @return The midpoint along the v-axis of the cell group.  This will
	 * always land in the center of a cell
	 */
	public double getCellGroupCenterVComponent(int numCells) {
		return getVRadius()*numCells;
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
	public void setSize(int size) {

		if (hexRatio == 0) {
			hexRatio = REGULAR_HEX_RATIO;
		}
		
		// Using size as the edge-to-edge distance or 
		// minor diameter of the hex.
		size = constrainSize(size);

		minorRadius = (double)size/2;
		edgeLength = minorRadius/hexRatio;
		edgeProjection = Math.sqrt(edgeLength*edgeLength - minorRadius*minorRadius); // Pythagorus
		
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
		return (int)(getVRadius() * 2);
	}
	
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

			for (double u = offU%(2*scaledEdgeLength + 2*scaledEdgeProjection) - (2*scaledEdgeLength + 2*scaledEdgeProjection);
					u < getRendererSizeU(renderer)+2*scaledEdgeLength + 2*scaledEdgeProjection;
					u += 2*scaledEdgeLength + 2*scaledEdgeProjection) {

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
		
		v = (int)(cpV * 2 * minorRadius + (Math.abs(cpU) % 2 == 0 ? 1 : 2)* minorRadius + getOffsetV());
		
		return new ZonePoint(u, v);
	}
	
	@Override
	public Set<CellPoint> getOccupiedCells(int height, int width, CellPoint baseCellPoint) {
		// Future prefs could allow for various standards of hex creature sizes?
		return HexGridUtil.getD20OccupiedCells(width, baseCellPoint, this);
	}
	
	@Override
	public CellPoint getWaypointPosition(int height, int width, CellPoint baseCellPoint) {
		return HexGridUtil.getWaypoint(this, baseCellPoint, width, height);
	}

	@Override
	public Point cellGroupCenterOffset(int height, int width, boolean isToken) {
        int x = cellOffset.width;
        int y = cellOffset.height;
        
        // If the cell group is a token then translate to
        // the closest vertex or cell center to the token's center
        if ( isToken )  {
        	
        	// Now move to the top right of the cell group's bounding rectangle
        	int tokenSize = Math.max((int)Math.round(width/getCellWidth()), 1);
        	
        	Point p = new Point();
        	
        	// if it is a d20 pattern token then offset appropriately (currently the only option)
        	p = HexGridUtil.getD20GroupOffset(tokenSize, 1, this);
        	x += p.x;
        	y += p.y;
        	
        	// From the top right corner translate to the closest vertex or cell center
        	// to the cell group's center 
            p = HexGridUtil.getCellGroupCenterOffset(this, tokenSize, 1);
            x += p.x;
            y += p.y;
        }
        else {
        	x += width/2 < getCellWidth() ? getCellWidth()/2 : width/2;
        	y += height/2 < getCellHeight() ? getCellHeight()/2 : height/2;
        }

        return new Point (x, y);
	}
	
	@Override
	public Point cellGroupTopLeftOffset(int height, int width, boolean isToken) {
		int x = 0;
		int y = 0;
		
        x = cellOffset.width;
        y = cellOffset.height;

        if( isToken ) {
        	// Account for funky token hex patterns
        	int tokenSize = Math.max((int)Math.round(width/getCellWidth()), 1);
        	Point p = HexGridUtil.getD20GroupOffset(tokenSize, 1, this);
        	x += p.x;
        	y += p.y;
        }

        return new Point(x,y);
	}
	
	@Override
	public void setSecondDimension(double length) {
		if (length < minorRadius*2 ) {
			hexRatio = REGULAR_HEX_RATIO;
		}
		else {
			//	some linear algebra and a quadratic equation results in:
			double aspectRatio = length / (2*minorRadius);
			double a = 0.75;
			double c = -(aspectRatio*aspectRatio+1)*minorRadius*minorRadius;
			double b = minorRadius*aspectRatio;
			edgeLength = (-b + Math.sqrt(b*b - 4*a*c))/(2*a);
			hexRatio = minorRadius/edgeLength;
		}
	}

	@Override
	public double getSecondDimension() {
		return getURadius()*2;
	}

}
