package net.rptools.maptool.model;


public class GridFactory {

	public static final String HEX_VERT = "Vertical Hex";
	public static final String HEX_HORI = "Horizontal Hex";
	public static final String SQUARE = "Square";
	public static final String NONE = "None";
	
	public static Grid createGrid(String type) {
	
		if (isHexVertical(type)) {
			return new HexGridVertical();
		}
		
		if (isHexHorizontal(type)) {
			return new HexGridHorizontal();
		}
		
		if (isSquare(type)) {
			return new SquareGrid();
		}
		
		if (isNone(type)) {
			return new GridlessGrid();
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
		
		if (grid instanceof GridlessGrid) {
			return NONE;
		}
		
		throw new IllegalArgumentException("Don't know type of grid: " + grid.getClass().getName());
	}

	public static boolean isSquare(String gridType) {
		return SQUARE.equals(gridType);
	}
	
	public static boolean isNone(String gridType) {
		return NONE.equals(gridType);
	}
	
	public static boolean isHexVertical(String gridType) {
		return HEX_VERT.equals(gridType);
	}
	
	public static boolean isHexHorizontal(String gridType) {
		return HEX_HORI.equals(gridType);
	}
}
