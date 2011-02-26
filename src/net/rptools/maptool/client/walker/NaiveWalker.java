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
package net.rptools.maptool.client.walker;

import java.util.ArrayList;
import java.util.List;

import net.rptools.maptool.model.CellPoint;
import net.rptools.maptool.model.Zone;

public class NaiveWalker extends AbstractZoneWalker {
	public NaiveWalker(Zone zone) {
		super(zone);
	}

	private int distance;

	@Override
	protected List<CellPoint> calculatePath(CellPoint start, CellPoint end) {
		List<CellPoint> list = new ArrayList<CellPoint>();

		int x = start.x;
		int y = start.y;

		int count = 0;
		while (true && count < 100) {
			list.add(new CellPoint(x, y));

			if (x == end.x && y == end.y) {
				break;
			}
			if (x < end.x)
				x++;
			if (x > end.x)
				x--;
			if (y < end.y)
				y++;
			if (y > end.y)
				y--;

			count++;
		}
		distance = (list.size() - 1) * 5;
		return list;
	}

	public int getDistance() {
		return distance;
	}
}
