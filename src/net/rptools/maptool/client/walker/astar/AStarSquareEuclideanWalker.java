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
package net.rptools.maptool.client.walker.astar;

import java.util.List;

import net.rptools.maptool.client.walker.WalkerMetric;
import net.rptools.maptool.model.CellPoint;
import net.rptools.maptool.model.Zone;

public class AStarSquareEuclideanWalker extends AbstractAStarWalker {
	
	private static final int[] NORTH = {0,-1};
	private static final int[] WEST = {-1,0};
	private static final int[] SOUTH = {0,1};
	private static final int[] EAST = {1,0};
	private static final int[] NORTH_EAST = {1,-1};
	private static final int[] SOUTH_EAST = {1,1};
	private static final int[] NORTH_WEST = {-1,-1};
	private static final int[] SOUTH_WEST = {-1,1};
	
	
	private final WalkerMetric metric;
	
	private final int[][] neighborMap;
	
	public AStarSquareEuclideanWalker (Zone zone, WalkerMetric metric) {
		super(zone);
		this.metric = metric;
		
		switch (metric) {
			case NO_DIAGONALS:
				neighborMap = new int[][] {NORTH,EAST,SOUTH,WEST };
				break;

			case ONE_ONE_ONE:
			case MANHATTAN:
				//promote straight directions to avoid 'only-diagonals' effect
				neighborMap = new int[][] {NORTH,EAST,SOUTH,WEST,NORTH_EAST,SOUTH_EAST,SOUTH_WEST,NORTH_WEST};
				break;	
			default:
				//promote diagonals over straight directions by putting them in front of array
				neighborMap = new int[][] {NORTH_EAST,SOUTH_EAST,SOUTH_WEST,NORTH_WEST,NORTH,EAST,SOUTH,WEST};
				break;
			
		}
		
	}

	@Override
	public int[][] getNeighborMap(int x, int y) {
		return neighborMap;
	}
	
	@Override
	protected double gScore(CellPoint p1, CellPoint p2) {
		return metricDistance(p1, p2);
	}

	@Override
	protected double hScore(CellPoint p1, CellPoint p2) {
		return metricDistance(p1, p2);
	}

	private double metricDistance(CellPoint p1, CellPoint p2) {
        int a = p2.x - p1.x;
        int b = p2.y - p1.y;
        
        final double distance;
        
        switch (metric) {
//        	case ONE_ONE_ONE:
//        		distance = Math.max(Math.abs(a),Math.abs(b));
//        		break;
        	case MANHATTAN:
        	case NO_DIAGONALS:
        		distance = Math.abs(a) + Math.abs(b);
        		break;
        	default:
        	case ONE_TWO_ONE:
        	case ONE_ONE_ONE:
        		distance = Math.sqrt(a * a + b * b);
        		break;
        }
        
        return distance;

    }

	@Override
	protected int calculateDistance(List<CellPoint> path, int feetPerCell) {
		if (path == null || path.size() == 0)
			return 0;

		
		final int feetDistance;
		
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
			
			final int cellDistance;
			switch (metric) {
				case MANHATTAN:
				case NO_DIAGONALS:
					cellDistance = (numStrt + numDiag*2);
					break;
				case ONE_ONE_ONE:
					cellDistance = (numStrt+numDiag);
					break;
				default:
				case ONE_TWO_ONE:
					cellDistance = (numStrt + numDiag + numDiag / 2);
					break;
				
			}
			
			feetDistance = feetPerCell * cellDistance;

			
		}

		return feetDistance;
	}	
}
