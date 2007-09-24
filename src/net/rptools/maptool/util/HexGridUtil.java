/* The MIT License
 * 
 * Copyright (c) 2005 David Rice, Trevor Croft
 * 
 * Permission is hereby granted, free of charge, to any person 
 * obtaining a copy of this software and associated documentation files 
 * (the "Software"), to deal in the Software without restriction, 
 * including without limitation the rights to use, copy, modify, merge, 
 * publish, distribute, sublicense, and/or sell copies of the Software, 
 * and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be 
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS 
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN 
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
 * SOFTWARE.
 */
package net.rptools.maptool.util;

import java.awt.Dimension;
import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import net.rptools.maptool.model.CellPoint;
import net.rptools.maptool.model.HexGrid;
import net.rptools.maptool.model.HexGridHorizontal;


/**
 * Provides methods to handle hexgrid issues that don't exist with a square grid.
 * @author Tylere
 */
public class HexGridUtil {

	/** 
	 * Convert to u-v coordinates where the v-axis points
	 * along the direction of edge to edge hexes
	 */ 
	private static int[] toUVCoords(CellPoint cp, HexGrid grid) {
		int cpU, cpV;
		if (grid instanceof HexGridHorizontal) {
			cpU = cp.y;
			cpV = cp.x;
		}
		else {
			cpU = cp.x;
			cpV = cp.y;
		}
		return new int[] {cpU, cpV};
	}
	
	/**
	 * Convert from u-v coords to grid coords
	 * @return the point in grid-space
	 */
	private static CellPoint fromUVCoords(int u, int v, HexGrid grid) {
		CellPoint cp = new CellPoint(u, v);
		if (grid instanceof HexGridHorizontal) {
			cp.x = v;
			cp.y = u;
		}
		return cp;
	}

	public static CellPoint getWaypoint(HexGrid grid, CellPoint cp, int width, int height) {
	
		if( width == height ) {
			int[] cpUV = toUVCoords(cp, grid); 
			return fromUVCoords(cpUV[0], cpUV[1] + (int)((width-1)/2), grid);
		}
		
		return cp;
	}
	

}
