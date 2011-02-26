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

import net.rptools.maptool.model.Zone;

public class AStarHorizHexEuclideanWalker extends AbstractAStarHexEuclideanWalker {
	public AStarHorizHexEuclideanWalker(Zone zone) {
		super(zone);
		initNeighborMaps();
	}

	// @formatter:off
	@Override
	protected void initNeighborMaps() {
		oddNeighborMap = new int[][] 
	      { { 0, -1, 1 },	{ 0, 0, 0 },		{ 1, -1, 1 }, 
			{ -1, 0, 1 },						{ 1, 0, 1 }, 
			{ 0, 1, 1 },		{ 0, 0, 0 },		{ 1, 1, 1 } };
		
		evenNeighborMap = new int[][] 
  	      { { -1, -1, 1 },	{ 0, 0, 0 },		{ 0, -1, 1 }, 
			{ -1, 0, 1 },						{ 1, 0, 1 }, 
			{ -1, 1, 1 },	{ 0, 0, 0 },		{ 0, 1, 1 } };
	}
	// @formatter:on

	@Override
	protected int[][] getNeighborMap(int x, int y) {
		return y % 2 == 0 ? evenNeighborMap : oddNeighborMap;
	}
}
