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
package net.rptools.maptool.model.drawing;

import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

/**
 * An oval.
 */
public class Oval extends Rectangle {
	/**
	 * @param x
	 * @param y
	 */
	public Oval(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	@Override
	protected void draw(Graphics2D g) {
		int minX = Math.min(startPoint.x, endPoint.x);
		int minY = Math.min(startPoint.y, endPoint.y);

		int width = Math.abs(startPoint.x - endPoint.x);
		int height = Math.abs(startPoint.y - endPoint.y);

		g.drawOval(minX, minY, width, height);
	}

	@Override
	protected void drawBackground(Graphics2D g) {
		int minX = Math.min(startPoint.x, endPoint.x);
		int minY = Math.min(startPoint.y, endPoint.y);

		int width = Math.abs(startPoint.x - endPoint.x);
		int height = Math.abs(startPoint.y - endPoint.y);

		g.fillOval(minX, minY, width, height);
	}

	@Override
	public Area getArea() {
		java.awt.Rectangle r = getBounds();
		return new Area(new Ellipse2D.Double(r.x, r.y, r.width, r.height));
	}
}
