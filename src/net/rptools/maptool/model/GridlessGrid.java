package net.rptools.maptool.model;

import java.awt.Rectangle;
import java.awt.geom.Area;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.rptools.maptool.client.MapTool;

public class GridlessGrid extends Grid {

	private static List<TokenFootprint> footprintList;
	
	private static final GridCapabilities GRID_CAPABILITIES= new GridCapabilities() {
		public boolean isPathingSupported() {return false;}
		public boolean isSnapToGridSupported() {return false;}
		public boolean isPathLineSupported() {return false;}
		public boolean isSecondDimensionAdjustmentSupported() {return false;}
		public boolean isCoordinatesSupported() {return false;}
	};

	private static final int[] FACING_ANGLES = new int[] {
		-135, -90, -45, 0, 45, 90, 135, 180
	};
	
	@Override
	public List<TokenFootprint> getFootprints() {
		if (footprintList == null) {
			try {
				footprintList = loadFootprints("net/rptools/maptool/model/gridlessGridFootprints.xml");
			} catch (IOException ioe) {
				ioe.printStackTrace();
				MapTool.showError("Could not load gridless grid footprints");
			}
		}
		return footprintList;
	}
	
	@Override
	public int[] getFacingAngles() {
		return FACING_ANGLES;
	}
	
	@Override
	public Rectangle getBounds(CellPoint cp) {
		
		return new Rectangle(cp.x, cp.y, getSize(), getSize());
	}
	
	@Override
	public ZonePoint convert(CellPoint cp) {
		return new ZonePoint(cp.x, cp.y);
	}
	@Override
	public CellPoint convert(ZonePoint zp) {
		return new CellPoint(zp.x, zp.y);
	}
	@Override
	protected Area createCellShape(int size) {
		// Doesn't do this
		return null;
	}
	@Override
	public GridCapabilities getCapabilities() {
		return GRID_CAPABILITIES;
	}

	@Override
	public double getCellWidth() {
		return getSize();
	}
	
	public double getCellHeight() {
		return getSize();
	}
}
