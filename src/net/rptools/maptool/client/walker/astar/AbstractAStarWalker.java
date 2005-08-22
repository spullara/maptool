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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.rptools.maptool.client.AbstractZoneWalker;
import net.rptools.maptool.client.CellPoint;
import net.rptools.maptool.model.Zone;

public abstract class AbstractAStarWalker extends AbstractZoneWalker {

	public AbstractAStarWalker(Zone zone) {
		super(zone);
	}

	private int distance = -1;

	private int[][] neighborMap = new int[][] { { -1, -1, 10 }, { 0, -1, 5 },
			{ 1, -1, 10 }, { -1, 0, 5 }, { 1, 0, 5 }, { -1, 1, 10 },
			{ 0, 1, 5 }, { 1, 1, 10 } };

	@Override
	protected List<CellPoint> calculatePath(CellPoint start, CellPoint end) {
		List<AStarCellPoint> openList = new LinkedList<AStarCellPoint>();
		List<AStarCellPoint> closedList = new LinkedList<AStarCellPoint>();

		openList.add(new AStarCellPoint(start));

		AStarCellPoint node = null;

		while (openList.size() > 0) {
			node = openList.remove(0);
			if (node.equals(end)) {
				break;
			}

			for (int i = 0; i < neighborMap.length; i++) {
				int x = node.x + neighborMap[i][0];
				int y = node.y + neighborMap[i][1];
				AStarCellPoint neighborNode = new AStarCellPoint(x, y);
				if (closedList.contains(neighborNode)) {
					continue;
				}

				neighborNode.parent = node;
				neighborNode.gScore = gScore(start, neighborNode);
				neighborNode.hScore = hScore(neighborNode, end);

				if (openList.contains(neighborNode)) {
					AStarCellPoint oldNode = getNode(openList, neighborNode);

					// check if it is cheaper to get here the way that we just
					// came, versus the previous path
					if (neighborNode.gScore < oldNode.gScore) {
						oldNode.gScore = neighborNode.gScore;
						neighborNode = oldNode;
						neighborNode.parent = node;
					}
					continue;
				}

				pushNode(openList, neighborNode);
			}

			closedList.add(node);
			node = null;

		}

		List<CellPoint> ret = new LinkedList<CellPoint>();
		while (node != null) {
			ret.add(node);
			node = node.parent;
		}

		distance = -1;
		Collections.reverse(ret);
		return ret;
	}

	private void pushNode(List<AStarCellPoint> list, AStarCellPoint node) {
		for (int i = 0; i < list.size(); i++) {
			AStarCellPoint listNode = list.get(i);

			if (listNode.cost() > node.cost()) {
				list.add(i, node);
				return;
			}
		}
		list.add(node);
	}

	private AStarCellPoint getNode(List<AStarCellPoint> list,
			AStarCellPoint node) {
		for (AStarCellPoint listNode : list) {
			if (listNode.equals(node)) {
				return node;
			}
		}
		return null;
	}

	private static int calculateDistance(List<CellPoint> path, int feetPerCell) {
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
		
		assert distMethod1 == distMethod2 : String.format("Inconsistent distances simple=%d, path based=%d", distMethod1, distMethod2);
		return distMethod2;
	}

	protected abstract double gScore(CellPoint p1, CellPoint p2);

	protected abstract double hScore(CellPoint p1, CellPoint p2);

	@Override
	public int getDistance() {
		if (distance == -1) {
			distance = calculateDistance(getPath(), getZone().getFeetPerCell());
		}

		return distance;
	}
}
