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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import net.rptools.maptool.client.walker.AbstractZoneWalker;
import net.rptools.maptool.model.CellPoint;
import net.rptools.maptool.model.Zone;

public abstract class AbstractAStarWalker extends AbstractZoneWalker {

	public AbstractAStarWalker(Zone zone) {
		super(zone);
	}

	private int distance = -1;

	protected abstract int[][] getNeighborMap(int x, int y);
	
	@Override
	protected List<CellPoint> calculatePath(CellPoint start, CellPoint end) {
		List<AStarCellPoint> openList = new ArrayList<AStarCellPoint>();
		Map<AStarCellPoint, AStarCellPoint> openSet = new HashMap<AStarCellPoint, AStarCellPoint>(); // For faster lookups
		Set<AStarCellPoint> closedSet = new HashSet<AStarCellPoint>();

		openList.add(new AStarCellPoint(start));
		openSet.put(openList.get(0), openList.get(0));

		AStarCellPoint node = null;

		while (openList.size() > 0) {
			node = openList.remove(0);
			openSet.remove(node);
			if (node.equals(end)) {
				break;
			}

			int[][] neighborMap = getNeighborMap(node.x, node.y);
			for (int i = 0; i < neighborMap.length; i++) {
				int x = node.x + neighborMap[i][0];
				int y = node.y + neighborMap[i][1];
				AStarCellPoint neighborNode = new AStarCellPoint(x, y);
				if (closedSet.contains(neighborNode)) {
					continue;
				}

				neighborNode.parent = node;
				neighborNode.gScore = gScore(start, neighborNode);
				neighborNode.hScore = hScore(neighborNode, end);

				if (openSet.containsKey(neighborNode)) {
					AStarCellPoint oldNode = openSet.get(neighborNode);

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
				openSet.put(neighborNode, neighborNode);
			}

			closedSet.add(node);
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
		if (list.size() == 0) {
			list.add(node);
			return;
		}
		
		if (node.cost() < list.get(0).cost()) {
			list.add(0, node);
			return;
		}
		
		if (node.cost() > list.get(list.size()-1).cost()) {
			list.add(node);
			return;
		}

		for (ListIterator<AStarCellPoint> iter = list.listIterator(); iter.hasNext();) {
			AStarCellPoint listNode = iter.next();
			if (listNode.cost() > node.cost()) {
				iter.previous();
				iter.add(node);
				return;
			}
		}
	}

	protected abstract int calculateDistance(List<CellPoint> path, int feetPerCell);

	protected abstract double gScore(CellPoint p1, CellPoint p2);

	protected abstract double hScore(CellPoint p1, CellPoint p2);

	public int getDistance() {
		if (distance == -1) {
			distance = calculateDistance(getPath().getCellPath(), getZone().getUnitsPerCell());
		}

		return distance;
	}
}
