package net.rptools.maptool.model;

import net.rptools.maptool.client.AppPreferences;

public class GridFactory {

	public static final String HEX_VERT = "Vertical Hex";
	public static final String HEX_HORI = "Horizontal Hex";
	public static final String SQUARE = "Square";
	
	public static Grid createGrid(String type) {
	
		//For backwards compatibilty with 1.2b14? and previous
		if(type.equals("Hex")) {
			AppPreferences.setDefaultGridType(HEX_VERT);
			return new HexGridVertical();
		}
		
		if (isHexVertical(type)) {
			return new HexGridVertical();
		}
		
		if (isHexHorizontal(type)) {
			return new HexGridHorizontal();
		}
		
		if (isSquare(type)) {
			return new SquareGrid();
		}
		
		throw new IllegalArgumentException("Unknown grid type: " + type);
	}
	
	public static String getGridType(Grid grid) {
		
		if (grid instanceof HexGridVertical) {
			return HEX_VERT;
		}
		
		if (grid instanceof HexGridHorizontal) {
			return HEX_HORI;
		}

		if (grid instanceof SquareGrid) {
			return SQUARE;
		}
		
		throw new IllegalArgumentException("Don't know type of grid: " + grid.getClass().getName());
	}
	
	public static boolean isSquare(String gridType) {
		return SQUARE.equals(gridType);
	}
	
	public static boolean isHexVertical(String gridType) {
		return HEX_VERT.equals(gridType);
	}
	
	public static boolean isHexHorizontal(String gridType) {
		return HEX_HORI.equals(gridType);
	}
}
