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
