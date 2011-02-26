/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package net.rptools.maptool.client.walker.astar;

import java.util.List;

import net.rptools.maptool.model.CellPoint;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.ZonePoint;

public abstract class AbstractAStarHexEuclideanWalker extends AbstractAStarWalker {
	protected int[][] oddNeighborMap;
	protected int[][] evenNeighborMap;

	public AbstractAStarHexEuclideanWalker(Zone zone) {
		super(zone);
	}

	protected abstract void initNeighborMaps();

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

	@Override
	protected int calculateDistance(List<CellPoint> path, int feetPerCell) {
		int cellsMoved = path != null && path.size() > 1 ? path.size() - 1 : 0;
		return cellsMoved * feetPerCell;
	}
}
