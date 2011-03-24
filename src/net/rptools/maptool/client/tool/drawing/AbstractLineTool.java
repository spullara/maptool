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
package net.rptools.maptool.client.tool.drawing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;

import javax.swing.SwingUtilities;

import net.rptools.maptool.client.ScreenPoint;
import net.rptools.maptool.client.tool.ToolHelper;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.ZonePoint;
import net.rptools.maptool.model.drawing.Drawable;
import net.rptools.maptool.model.drawing.DrawableColorPaint;
import net.rptools.maptool.model.drawing.LineSegment;
import net.rptools.maptool.model.drawing.Pen;
import net.rptools.maptool.model.drawing.ShapeDrawable;

/**
 * Tool for drawing freehand lines.
 */
public abstract class AbstractLineTool extends AbstractDrawingTool {
	private int currentX;
	private int currentY;

	private LineSegment line;
	protected boolean drawMeasurementDisabled;

	protected int getCurrentX() {
		return currentX;
	}

	protected int getCurrentY() {
		return currentY;
	}

	protected LineSegment getLine() {
		return this.line;
	}

	protected void startLine(MouseEvent e) {
		line = new LineSegment(getPen().getThickness());
		addPoint(e);
	}

	protected Point addPoint(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {
			return null;
		}
		ZonePoint zp = getPoint(e);

		if (line == null)
			return null; // Escape has been pressed
		Point ret = new Point(zp.x, zp.y);

		line.getPoints().add(ret);
		currentX = zp.x;
		currentY = zp.y;

		renderer.repaint();
		return ret;
	}

	protected void removePoint(Point p) {
		if (line == null)
			return; // Escape has been pressed

		// Remove most recently added
		// TODO: optimize this
		Collections.reverse(line.getPoints());
		line.getPoints().remove(p);
		Collections.reverse(line.getPoints());
	}

	protected void stopLine(MouseEvent e) {
		if (line == null)
			return; // Escape has been pressed
		addPoint(e);

		Drawable drawable = line;
		if (isBackgroundFill(e) && line.getPoints().size() > 3) { // TODO: There's a bug where the last point is duplicated, hence 3 points
			drawable = new ShapeDrawable(getPolygon(line));
		}
		completeDrawable(renderer.getZone().getId(), getPen(), drawable);

		line = null;
		currentX = -1;
		currentY = -1;
	}

	protected Polygon getPolygon(LineSegment line) {
		Polygon polygon = new Polygon();
		for (Point point : line.getPoints()) {
			polygon.addPoint(point.x, point.y);
		}
		return polygon;
	}

	@Override
	public void paintOverlay(ZoneRenderer renderer, Graphics2D g) {
		if (line != null) {
			Pen pen = getPen();
			pen.setForegroundMode(Pen.MODE_SOLID);

			if (pen.isEraser()) {
				pen = new Pen(pen);
				pen.setEraser(false);
				pen.setPaint(new DrawableColorPaint(Color.white));
			}
			paintTransformed(g, renderer, line, pen);

			List<Point> pointList = line.getPoints();
			if (!drawMeasurementDisabled && pointList.size() > 1 && drawMeasurement()) {

				Point start = pointList.get(pointList.size() - 2);
				Point end = pointList.get(pointList.size() - 1);

				ScreenPoint sp = ScreenPoint.fromZonePoint(renderer, start.x, start.y);
				ScreenPoint ep = ScreenPoint.fromZonePoint(renderer, end.x, end.y);

				//ep.y -= 15;

				ToolHelper.drawMeasurement(renderer, g, sp, ep);
			}
		}
	}

	protected boolean drawMeasurement() {
		return true;
	}

	/**
	 * @see net.rptools.maptool.client.ui.Tool#resetTool()
	 */
	@Override
	protected void resetTool() {
		if (line != null) {
			line = null;
			currentX = -1;
			currentY = -1;
			renderer.repaint();
		} else {
			super.resetTool();
		}
	}
}
