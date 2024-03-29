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
package net.rptools.maptool.client;

import java.awt.geom.Point2D;

import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.ZonePoint;

public class ScreenPoint extends Point2D.Double {
	public ScreenPoint(double x, double y) {
		super(x, y);
	}

	/**
	 * Translate the point from screen x,y to zone x,y
	 */
	public static ZonePoint convertToZone(ZoneRenderer renderer, double x, double y) {
		double scale = renderer.getScale();

		double zX = x;
		double zY = y;

		// Translate
		zX -= renderer.getViewOffsetX();
		zY -= renderer.getViewOffsetY();

		// Scale
		zX = (int) Math.floor(zX / scale);
		zY = (int) Math.floor(zY / scale);

//		System.out.println("s:" + scale + " x:" + x + " zx:" + zX + " c:" + (zX / scale) + " - " + Math.floor(zX / scale));

		return new ZonePoint((int) zX, (int) zY);
	}

	/**
	 * Translate the point from screen x,y to zone x,y
	 */
	public ZonePoint convertToZone(ZoneRenderer renderer) {
		return convertToZone(renderer, this.x, this.y);
	}

	/**
	 * Translate the point from screen x,y to the nearest top left corner of a zone x,y for when the zone point required
	 * is on the "zone point grid" as opposed to the area of zone space designated by the zone point.
	 */
	public ZonePoint convertToZoneRnd(ZoneRenderer renderer) {
		double scale = renderer.getScale();
		double rndAdj = 0.5 * scale;
		return convertToZone(renderer, this.x + rndAdj, this.y + rndAdj);
	}

	public static ScreenPoint fromZonePoint(ZoneRenderer renderer, ZonePoint zp) {
		return fromZonePoint(renderer, zp.x, zp.y);
	}

	public static ScreenPoint fromZonePoint(ZoneRenderer renderer, double x, double y) {
		double scale = renderer.getScale();

		double sX = x;
		double sY = y;

		sX = sX * scale;
		sY = sY * scale;

		// Translate
		sX += renderer.getViewOffsetX();
		sY += renderer.getViewOffsetY();

		return new ScreenPoint(sX, sY);
	}

	/**
	 * Converts a ZonePoint to a screen coordinate (ScreenPoint) and rounding both axis values to longs.
	 * 
	 * @param renderer
	 *            the ZoneRenderer to use for scaling the coordinate
	 * @param x
	 *            X axis coordinate
	 * @param y
	 *            Y axis coordinate
	 * @return new ScreenPoint
	 */
	public static ScreenPoint fromZonePointRnd(ZoneRenderer renderer, double x, double y) {
		ScreenPoint sp = fromZonePoint(renderer, x, y);
		sp.x = Math.round(sp.x);
		sp.y = Math.round(sp.y);
		return sp;
	}

	/**
	 * Same as {@link #fromZonePointRnd(ZoneRenderer, double, double)} but always rounds up.
	 * 
	 * @param renderer
	 *            the ZoneRenderer to use for scaling the coordinate
	 * @param x
	 *            X axis coordinate
	 * @param y
	 *            Y axis coordinate
	 * @return new ScreenPoint
	 */
	public static ScreenPoint fromZonePointHigh(ZoneRenderer renderer, double x, double y) {
		ScreenPoint sp = fromZonePoint(renderer, x, y);
		sp.x = Math.ceil(sp.x);
		sp.y = Math.ceil(sp.y);
		return sp;
	}

	/**
	 * Same as {@link #fromZonePointRnd(ZoneRenderer, double, double)} but always rounds down.
	 * 
	 * @param renderer
	 *            the ZoneRenderer to use for scaling the coordinate
	 * @param x
	 *            X axis coordinate
	 * @param y
	 *            Y axis coordinate
	 * @return new ScreenPoint
	 */
	public static ScreenPoint fromZonePointLow(ZoneRenderer renderer, double x, double y) {
		ScreenPoint sp = fromZonePoint(renderer, x, y);
		sp.x = Math.floor(sp.x);
		sp.y = Math.floor(sp.y);
		return sp;
	}

	@Override
	public String toString() {
		return "ScreenPoint" + super.toString();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object pt) {
		if (!(pt instanceof ScreenPoint))
			return false;
		ScreenPoint spt = (ScreenPoint) pt;
		return spt.x == x && spt.y == y;
	}

	public void translate(int dx, int dy) {
		x += dx;
		y += dy;
	}
}
