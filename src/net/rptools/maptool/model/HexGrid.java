/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package net.rptools.maptool.model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.IOException;

import net.rptools.lib.image.ImageUtil;
import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.AppState;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.TokenFootprint.OffsetTranslator;

/**
 * An abstract hex grid class that uses generic cartesian-coordinates for calculations to allow for various hex grid
 * orientations.
 * 
 * The v-axis points along the direction of edge to edge hexes
 */
public abstract class HexGrid extends Grid {
	// A regular hexagon is one where all angles are 60 degrees.
	// the ratio = minor_radius / edge_length
	public static final double REGULAR_HEX_RATIO = Math.sqrt(3) / 2;

	protected static BufferedImage pathHighlight;

	static {
		try {
			pathHighlight = ImageUtil.getCompatibleImage("net/rptools/maptool/client/image/hexBorder.png");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private static final GridCapabilities GRID_CAPABILITIES = new GridCapabilities() {
		public boolean isPathingSupported() {
			return true;
		}

		public boolean isSnapToGridSupported() {
			return true;
		}

		public boolean isPathLineSupported() {
			return false;
		}

		public boolean isSecondDimensionAdjustmentSupported() {
			return true;
		}

		public boolean isCoordinatesSupported() {
			return false;
		}
	};

	@Override
	public Rectangle getBounds(CellPoint cp) {
		// This is naive, but, give it a try
		ZonePoint zp = convert(cp);
		Shape shape = getCellShape();

		zp.x -= shape.getBounds().width / 2 + getOffsetX();
		zp.y -= shape.getBounds().height / 2 + getOffsetY();

		int w = shape.getBounds().width;
		int h = shape.getBounds().height;

//		System.out.println(new Rectangle(zp.x, zp.y, w, h));
		return new Rectangle(zp.x, zp.y, w, h);
	}

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
	 * The offset required to translate from the center of a cell to the top right (x_min, y_min) of the cell's bounding
	 * rectangle.
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
		return edgeLength / 2 + edgeProjection;
	}

	@Override
	public Dimension getCellOffset() {
		return cellOffset;
	}

	/**
	 * A generic form of getCellOffset() where V is the axis of edge to edge hexes.
	 * 
	 * @return The offset required to translate from the center of a cell to the least edge (v_min)
	 */
	public double getCellOffsetV() {
		return -getVRadius();
	}

	/**
	 * A generic form of getCellOffset() where U is the axis perpendicular to the line of edge to edge hexes.
	 * 
	 * @return The offset required to translate from the center of a cell to the least vertex (u_min)
	 */
	public double getCellOffsetU() {
		return -getURadius();
	}

	/**
	 * The offset required to translate from the center of a cell to the top right (x_min, y_min) of the cell's bounding
	 * rectangle.
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

		minorRadius = (double) size / 2;
		edgeLength = minorRadius / hexRatio;
		edgeProjection = Math.sqrt(edgeLength * edgeLength - minorRadius * minorRadius); // Pythagorus

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
		scaledMinorRadius = minorRadius * scale;
		scaledEdgeLength = edgeLength * scale;
		scaledEdgeProjection = edgeProjection * scale;

		scaledHex = createHalfShape(scaledMinorRadius, scaledEdgeProjection, scaledEdgeLength);

		lastScale = scale;
	}

	private GeneralPath createShape(double minorRadius, double edgeProjection, double edgeLength) {
		GeneralPath hex = new GeneralPath();
		hex.moveTo(0, (int) minorRadius);
		hex.lineTo((int) edgeProjection, 0);
		hex.lineTo((int) (edgeProjection + edgeLength), 0);
		hex.lineTo((int) (edgeProjection + edgeLength + edgeProjection), (int) minorRadius);
		hex.lineTo((int) (edgeProjection + edgeLength), (int) (minorRadius * 2));
		hex.lineTo((int) (edgeProjection), (int) (minorRadius * 2));

		orientHex(hex);
		return hex;
	}

	private GeneralPath createHalfShape(double minorRadius, double edgeProjection, double edgeLength) {
		GeneralPath hex = new GeneralPath();
		hex.moveTo(0, (int) minorRadius);
		hex.lineTo((int) edgeProjection, 0);
		hex.lineTo((int) (edgeProjection + edgeLength), 0);
		hex.lineTo((int) (edgeProjection + edgeLength + edgeProjection), (int) minorRadius);

		orientHex(hex);
		return hex;
	}

	/**
	 * Default orientation is for a vertical hex grid Override for other orientations
	 * 
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
		return (int) (getVRadius() * 2);
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

		Object oldAntiAlias = SwingUtil.useAntiAliasing(g);
		g.setColor(new Color(getZone().getGridColor()));
		g.setStroke(new BasicStroke(AppState.getGridSize()));

		for (double v = offV % (scaledMinorRadius * 2) - (scaledMinorRadius * 2); v < getRendererSizeV(renderer); v += scaledMinorRadius) {
			double offsetU = (int) (count % 2 == 0 ? 0 : -(scaledEdgeProjection + scaledEdgeLength));
			count++;

			double start = offU % (2 * scaledEdgeLength + 2 * scaledEdgeProjection) - (2 * scaledEdgeLength + 2 * scaledEdgeProjection);
			double end = getRendererSizeU(renderer) + 2 * scaledEdgeLength + 2 * scaledEdgeProjection;
			double incr = 2 * scaledEdgeLength + 2 * scaledEdgeProjection;
			for (double u = start; u < end; u += incr) {
				setGridDrawTranslation(g, u + offsetU, v);
				g.draw(scaledHex);
				setGridDrawTranslation(g, -(u + offsetU), -v);
			}
		}
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAntiAlias);
	}

	/**
	 * Generic form of getOffsetX for ease of transforming to other grid orientations.
	 * 
	 * @return The U component of the grid's offset.
	 */
	protected abstract int getOffsetU();

	/**
	 * Generic form of getOffsetY for ease of transforming to other grid orientations.
	 * 
	 * @return The V component of the grid's offset.
	 */
	protected abstract int getOffsetV();

	/**
	 * A method used by HexGrid.convert(ZonePoint zp) to allow for alternate grid orientations
	 * 
	 * @return Coordinates in Cell-space of the ZonePoint
	 */
	protected CellPoint convertZP(int zpU, int zpV) {
		int xSect;
		int ySect;

		int offsetZpU = zpU - getOffsetU();
		int offsetZpV = zpV - getOffsetV();

		if (offsetZpU < 0) {
			xSect = (int) (offsetZpU / (edgeProjection + edgeLength)) - 1;
		} else {
			xSect = (int) (offsetZpU / (edgeProjection + edgeLength));
		}
		if (offsetZpV < 0) {
			if (Math.abs(xSect) % 2 == 1)
				ySect = (int) ((offsetZpV - minorRadius) / (2 * minorRadius)) - 1;
			else
				ySect = (int) (offsetZpV / (2 * minorRadius)) - 1;
		} else {
			if (Math.abs(xSect) % 2 == 1)
				ySect = (int) ((offsetZpV - minorRadius) / (2 * minorRadius));
			else
				ySect = (int) (offsetZpV / (2 * minorRadius));
		}
		int xPxl = Math.abs((int) (offsetZpU - xSect * (edgeProjection + edgeLength)));
		int yPxl = Math.abs((int) (offsetZpV - ySect * (2 * minorRadius)));

		int gridX = xSect;
		int gridY = ySect;

		double m = edgeProjection / minorRadius;

//		System.out.format("gx:%d gy:%d px:%d py:%d m:%f\n", xSect, ySect, xPxl, yPxl, m);
//		System.out.format("gx:%d gy:%d px:%d py:%d\n", xSect, ySect, zp.x, zp.y);

		switch (Math.abs(xSect) % 2) {
		case 0:
			if (yPxl <= minorRadius) {
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
				if (xPxl < (edgeProjection - (yPxl - minorRadius) * m)) {
					gridX = xSect - 1;
					//gridY = ySect;
				} else {
					//gridX = xSect;
					//gridY = ySect;
				}
			} else {
				if (xPxl < (yPxl * m)) {
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
	 * 
	 * @return A ZonePoint positioned at the center of the Hex
	 */
	protected ZonePoint convertCP(int cpU, int cpV) {
		int u, v;

		u = (int) Math.round(cpU * (edgeProjection + edgeLength) + edgeLength) + getOffsetU();
		v = (int) (cpV * 2 * minorRadius + (Math.abs(cpU) % 2 == 0 ? 1 : 2) * minorRadius + getOffsetV());

		return new ZonePoint(u, v);
	}

	@Override
	public void setSecondDimension(double length) {
		if (length < minorRadius * 2) {
			hexRatio = REGULAR_HEX_RATIO;
		} else {
			//	some linear algebra and a quadratic equation results in:
			double aspectRatio = length / (2 * minorRadius);
			double a = 0.75;
			double c = -(aspectRatio * aspectRatio + 1) * minorRadius * minorRadius;
			double b = minorRadius * aspectRatio;
			edgeLength = (-b + Math.sqrt(b * b - 4 * a * c)) / (2 * a);
			hexRatio = minorRadius / edgeLength;
		}
	}

	@Override
	public double getSecondDimension() {
		return getURadius() * 2;
	}

	protected abstract OffsetTranslator getOffsetTranslator();
}
