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

import net.rptools.maptool.model.CellPoint;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.ZonePoint;

public class AStarHexEuclideanWalker extends AbstractAStarWalker {

	private int[][] oddNeighborMap = new int[][] 
	      { { -1, 0, 1 }, { 0, -1, 1 }, { 1, 0, 1 }, 
			{ 0, 0, 0 },   			  { 0, 0, 0 }, 
			{ -1, 1, 1 },  { 0, 1, 1 }, { 1, 1, 1 } };

	private int[][] evenNeighborMap = new int[][] 
  	      { { -1, -1, 1 }, { 0, -1, 1 }, { 1, -1, 1 }, 
			{ 0, 0, 0 },   			  { 0, 0, 0 }, 
			{ -1, 0, 1 },  { 0, 1, 1 }, { 1, 0, 1 } };

	public AStarHexEuclideanWalker (Zone zone) {
		super(zone);
	}

	@Override
	protected int[][] getNeighborMap(int x, int y) {

		return x % 2 == 0 ? evenNeighborMap : oddNeighborMap;
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
		
		ZonePoint zp1 = getZone().getGrid().convert(p1);
		ZonePoint zp2 = getZone().getGrid().convert(p2);
		
        int a = zp2.x - zp1.x;
        int b = zp2.y - zp1.y;

        return Math.sqrt(a * a + b * b);
    }

}
