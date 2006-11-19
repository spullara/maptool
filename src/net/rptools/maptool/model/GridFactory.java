package net.rptools.maptool.model;

public class GridFactory {

	public static final String HEX = "Hex";
	public static final String SQUARE = "Square";
	
	public static Grid createGrid(String type) {
	
		if (isHex(type)) {
			return new HexGrid();
		}
		
		if (isSquare(type)) {
			return new SquareGrid();
		}
		
		throw new IllegalArgumentException("Unknown grid type: " + type);
	}
	
	public static String getGridType(Grid grid) {
		if (grid instanceof HexGrid) {
			return HEX;
		}
		
		if (grid instanceof SquareGrid) {
			return SQUARE;
		}
		
		throw new IllegalArgumentException("Don't know type of grid: " + grid.getClass().getName());
	}
	
	public static boolean isSquare(String gridType) {
		return SQUARE.equals(gridType);
	}
	
	public static boolean isHex(String gridType) {
		return HEX.equals(gridType);
	}
}
