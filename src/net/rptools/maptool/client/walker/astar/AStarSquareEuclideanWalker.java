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
package net.rptools.maptool.client.walker.astar;

import java.util.List;

import net.rptools.maptool.model.CellPoint;
import net.rptools.maptool.model.Zone;

public class AStarSquareEuclideanWalker extends AbstractAStarWalker {

	private int[][] neighborMap = new int[][] { { -1, -1, 10 }, { 0, -1, 5 },
			{ 1, -1, 10 }, { -1, 0, 5 }, { 1, 0, 5 }, { -1, 1, 10 },
			{ 0, 1, 5 }, { 1, 1, 10 } };

	public AStarSquareEuclideanWalker (Zone zone) {
		super(zone);
	}

	@Override
	public int[][] getNeighborMap(int x, int y) {
		return neighborMap;
	}
	
	@Override
	protected double gScore(CellPoint p1, CellPoint p2) {
		return euclideanDistance(p1, p2);
	}

	@Override
	protected double hScore(CellPoint p1, CellPoint p2) {
		return euclideanDistance(p1, p2);
	}

	private double euclideanDistance(CellPoint p1, CellPoint p2) {
        int a = p2.x - p1.x;
        int b = p2.y - p1.y;

        return Math.sqrt(a * a + b * b);
    }

	@Override
	protected int calculateDistance(List<CellPoint> path, int feetPerCell) {
		if (path == null || path.size() == 0)
			return 0;

		int distMethod1;

		// LATER: When we add path barriers we have to remove method 1 completely.
		// I am leaving it now for double checking since we had such a bad time with
		// distance measurements.
		
		{ // method 1 (used for double check of new algorithm)
			CellPoint start = path.get(0);
			CellPoint end = path.get(path.size() - 1);

			int numDiag = Math.min(Math.abs(start.x - end.x), Math.abs(start.y
					- end.y));
			int numStrt = Math.max(Math.abs(start.x - end.x), Math.abs(start.y
					- end.y))
					- numDiag;
			distMethod1 = feetPerCell
					* (numStrt + numDiag + numDiag / 2);
		}

		int distMethod2;
		
		{
			int numDiag = 0;
			int numStrt = 0;

			CellPoint previousPoint = null;
			for (CellPoint point : path) {
				if (previousPoint != null) {
					int change = Math.abs(previousPoint.x - point.x) + Math.abs(previousPoint.y - point.y);
					
					switch (change) {
					case 1:
						numStrt++;
						break;
					case 2:
						numDiag++;
						break;
					default:
						assert false : String.format("Illegal path, cells are not contiguous change=%d", change);
						return -1;
					}
				}

				previousPoint = point;
			}

			distMethod2 = feetPerCell
			* (numStrt + numDiag + numDiag / 2);
		}

    // This assert is broken if you move the cursor to the left or above the second waypoint
//		assert distMethod1 == distMethod2 : String.format("Inconsistent distances simple=%d, path based=%d", distMethod1, distMethod2);
		return distMethod2;
	}	
}
