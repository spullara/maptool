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
package net.rptools.maptool.client.tool;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import net.rptools.lib.image.ImageUtil;
import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.ScreenPoint;
import net.rptools.maptool.client.ui.zone.ZoneOverlay;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.client.walker.ZoneWalker;
import net.rptools.maptool.model.CellPoint;
import net.rptools.maptool.model.Path;
import net.rptools.maptool.model.ZonePoint;
import net.rptools.maptool.util.GraphicsUtil;

/**
 */
public class MeasureTool extends DefaultTool implements ZoneOverlay {

	private ZoneWalker walker;
	private Path<ZonePoint> gridlessPath;

	public MeasureTool() {
		try {
			setIcon(new ImageIcon(ImageUtil.getImage("net/rptools/maptool/client/image/tool/ruler-blue.png")));
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	@Override
	public String getTooltip() {
		return "tool.measure.tooltip";
	}

	@Override
	public String getInstructions() {
		return "tool.measure.instructions";
	}

	public void paintOverlay(ZoneRenderer renderer, Graphics2D g) {
		if (walker == null && gridlessPath == null) {
			return;
		}
		if (walker != null) {
			renderer.renderPath(g, walker.getPath(), renderer.getZone().getGrid().getDefaultFootprint());
			ScreenPoint sp = walker.getLastPoint().convertToScreen(renderer);

			int y = (int) sp.y - 10;
			int x = (int) sp.x + (int) (renderer.getScaledGridSize() / 2);
			GraphicsUtil.drawBoxedString(g, Integer.toString(walker.getDistance()), x, y);
		} else {
			Object oldAA = SwingUtil.useAntiAliasing(g);
			g.setColor(Color.black);
			ScreenPoint lastPoint = null;
			for (ZonePoint zp : gridlessPath.getCellPath()) {
				if (lastPoint == null) {
					lastPoint = ScreenPoint.fromZonePoint(renderer, zp);
					continue;
				}
				ScreenPoint nextPoint = ScreenPoint.fromZonePoint(renderer, zp.x, zp.y);
				g.drawLine((int) lastPoint.x, (int) lastPoint.y, (int) nextPoint.x, (int) nextPoint.y);
				lastPoint = nextPoint;
			}

			// distance
			double c = 0;
			ZonePoint lastZP = null;
			for (ZonePoint zp : gridlessPath.getCellPath()) {
				if (lastZP == null) {
					lastZP = zp;
					continue;
				}
				int a = lastZP.x - zp.x;
				int b = lastZP.y - zp.y;
				c += Math.sqrt(a * a + b * b);
				lastZP = zp;
			}

//    		int a = lastPoint.x - (set.offsetX + token.getX());
//    		int b = lastPoint.y - (set.offsetY + token.getY());
//
//         c +=  Math.sqrt(a*a + b*b)/zone.getUnitsPerCell();

			c /= renderer.getZone().getGrid().getSize();
			c *= renderer.getZone().getUnitsPerCell();

			String distance = String.format("%.1f", c);
			ScreenPoint sp = ScreenPoint.fromZonePoint(renderer, lastZP.x, lastZP.y);
			GraphicsUtil.drawBoxedString(g, distance, (int) sp.x, (int) sp.y - 20);

			SwingUtil.restoreAntiAliasing(g, oldAA);
		}
	}

	@Override
	protected void installKeystrokes(Map<KeyStroke, Action> actionMap) {
		super.installKeystrokes(actionMap);

		actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (walker == null && gridlessPath == null) {
					return;
				}
				// Waypoint
				if (walker != null) {
					CellPoint cp = renderer.getZone().getGrid().convert(new ScreenPoint(mouseX, mouseY).convertToZone(renderer));
					walker.toggleWaypoint(cp);
				} else {
					gridlessPath.addWayPoint(new ScreenPoint(mouseX, mouseY).convertToZone(renderer));
					gridlessPath.addPathCell(new ScreenPoint(mouseX, mouseY).convertToZone(renderer));
				}
			}
		});
	}

	////
	// MOUSE LISTENER
	@Override
	public void mousePressed(java.awt.event.MouseEvent e) {
		ZoneRenderer renderer = (ZoneRenderer) e.getSource();

		if (SwingUtilities.isLeftMouseButton(e)) {
			if (renderer.getZone().getGrid().getCapabilities().isPathingSupported()) {
				CellPoint cellPoint = renderer.getCellAt(new ScreenPoint(e.getX(), e.getY()));
				walker = renderer.getZone().getGrid().createZoneWalker();
				walker.addWaypoints(cellPoint, cellPoint);
			} else {
				gridlessPath = new Path<ZonePoint>();
				gridlessPath.addPathCell(new ScreenPoint(e.getX(), e.getY()).convertToZone(renderer));

				// Add a second one that will be replaced as the mouse moves around the screen
				gridlessPath.addPathCell(new ScreenPoint(e.getX(), e.getY()).convertToZone(renderer));
			}
			renderer.repaint();
			return;
		}
		super.mousePressed(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		ZoneRenderer renderer = (ZoneRenderer) e.getSource();

		if (SwingUtilities.isLeftMouseButton(e)) {
			walker = null;
			gridlessPath = null;
			renderer.repaint();
			return;
		}
		super.mouseReleased(e);
	}

	////
	// MOUSE MOTION LISTENER
	@Override
	public void mouseDragged(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {
			super.mouseDragged(e);
			return;
		}
		ZoneRenderer renderer = (ZoneRenderer) e.getSource();
		if (walker != null && renderer.getZone().getGrid().getCapabilities().isPathingSupported()) {
			CellPoint cellPoint = renderer.getCellAt(new ScreenPoint(e.getX(), e.getY()));
			walker.replaceLastWaypoint(cellPoint);
		} else if (gridlessPath != null) {
			gridlessPath.replaceLastPoint(new ScreenPoint(e.getX(), e.getY()).convertToZone(renderer));
		}
		renderer.repaint();
	}
}
