/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package net.rptools.maptool.model;


public class GridFactory {

	public static final String HEX_VERT = "Vertical Hex";
	public static final String HEX_HORI = "Horizontal Hex";
	public static final String SQUARE = "Square";
	public static final String NONE = "None";

	public static Grid createGrid(String type) {
		return createGrid(type,true,false);
	}
	public static Grid createGrid(String type, boolean faceEdges, boolean faceVertices) {
	
		if (isHexVertical(type)) {
			return new HexGridVertical(faceEdges, faceVertices);
		}
		
		if (isHexHorizontal(type)) {
			return new HexGridHorizontal(faceEdges, faceVertices);
		}
		
		if (isSquare(type)) {
			return new SquareGrid(faceEdges, faceVertices);
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
