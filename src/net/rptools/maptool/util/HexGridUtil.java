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
	
	/**
	 * 
	 * @param width Token width in Cells
	 * @return
	 */
	public static Point getCellGroupCenterOffset(HexGrid grid, int width, float scale) {
		int sizeFactor = width;
		int pU = 0;
		int pV = 0;

		pU = (int)(grid.getCellGroupCenterUComponent(sizeFactor)*scale);
		pV = (int)(grid.getCellGroupCenterVComponent(sizeFactor)*scale);
			
		CellPoint cp = fromUVCoords(pU, pV, grid);
		
		return new Point(cp.x,cp.y);
	}
	
	/**********************************************************************************
	 * The following methods are for handling hex token patterns as used in d20 games *
	 * as described here: http://www.d20srd.org/srd/variant/adventuring/hexGrid.htm   *
	 **********************************************************************************/
	
	/**
	 * @param size The token size (token diameter in cells)
	 * @param baseCellPoint The token's base CellPoint coordinate
	 * @return All CellPoints included in the token's space
	 */
	public static Set<CellPoint> getD20OccupiedCells(int size, CellPoint baseCellPoint, HexGrid grid) {
		Set<CellPoint> includedCellsSet = new HashSet<CellPoint>();
		Set<Point> UVPoints = new HashSet<Point>();

		int[] cpUV = toUVCoords(baseCellPoint, grid); 
		int cpU = cpUV[0];
		int cpV = cpUV[1];
		
		// Algorithm to gather all cells occupied by a creature according to 
		// d20 rules for creature sizes
		boolean cellIsOdd = Math.abs(cpU) % 2 == 0 ? false : true;
		boolean sizeIsOdd = size % 2 == 0 ? false : true;
		double halfSize = Math.ceil(size/2.0);
		
		int i=0;
		int j=0;

		for (int u = 0; u <= halfSize +(sizeIsOdd ? -1 : 0); u++ ) {	
			
			for (int v = i; v < size-j; v++ ) {
				UVPoints.add(new Point(cpU+u, cpV+v));
				if( u > 0 && u < halfSize ) {
					UVPoints.add(new Point(cpU-u, cpV+v));
				}
			}
			
			if ((size-u + (sizeIsOdd ? -1 : 0)) % 2 == 0) {
				if(cellIsOdd) { i++; }
				else { j++; }
			}
			else {
				if(cellIsOdd) { j++; }
				else { i++; }
			}
		}
		
		// Convert back to grid coordinates and add the cells
		for (Point p : UVPoints) {
			// p.x == p.u, p.y == p.v
			includedCellsSet.add(fromUVCoords(p.x, p.y, grid));
		}
		
		return includedCellsSet;
	}

	/**
	 * @param size The token size (token diameter in cells)
	 * @return The offset required to translate from the top-left of a token's base cell
	 * to the top left of a token's bounding rectange
	 */
	public static Point getD20GroupOffset(int size, float scale, HexGrid grid) {
		
		int uOffset = 0;		
		
		if (size > 1) {
			double numUSectionsUp = Math.ceil(((double)size-2.0)/2);
			uOffset = (int)(numUSectionsUp*1.5*grid.getCellOffsetU()*scale);
		}
		
		CellPoint cpOffset = fromUVCoords(uOffset, 0, grid);		
		return new Point(cpOffset.x, cpOffset.y);
	}
}
