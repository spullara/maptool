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
package net.rptools.maptool.client.ui.zone.vbl;

import java.awt.Point;
import java.awt.geom.Point2D;

import net.rptools.lib.GeometryUtil;

public class AreaFace {
	private double facing;
	private final Point2D p1;
	private final Point2D p2;

	public AreaFace(Point2D p1, Point2D p2) {
		this.p1 = p1;
		this.p2 = p2;
		computeFacing();
	}

	private void computeFacing() {
		// This assumes a counter clockwise wind
		facing = GeometryUtil.getAngle(p1, p2) + 90;
	}

	public Point2D getP1() {
		return p1;
	}

	public Point2D getP2() {
		return p2;
	}

	public double getFacing() {
		return facing;
	}

	public Point2D getMidPoint() {
		double x1 = Math.min(p1.getX(), p2.getX());
		double x2 = Math.max(p1.getX(), p2.getX());

		double y1 = Math.min(p1.getY(), p2.getY());
		double y2 = Math.max(p1.getY(), p2.getY());

		return new Point2D.Double(x1 + (x2 - x1) / 2, y1 + (y2 - y1) / 2);
	}

	@Override
	public String toString() {
		return p1 + "x" + p2 + " f: " + facing;
	}

	public static void main(String[] args) {
		Point p1 = new Point(0, 0);
		Point p2 = new Point(10, 0);

		System.out.println(new AreaFace(p1, p2).getMidPoint());
	}
}
