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
package net.rptools.maptool.model;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.geom.Area;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.KeyStroke;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.tool.PointerTool;

public class GridlessGrid extends Grid {
	private static List<TokenFootprint> footprintList;

	// @formatter:off
	private static final GridCapabilities GRID_CAPABILITIES= new GridCapabilities() {
		public boolean isPathingSupported() {return false;}
		public boolean isSnapToGridSupported() {return false;}
		public boolean isPathLineSupported() {return false;}
		public boolean isSecondDimensionAdjustmentSupported() {return false;}
		public boolean isCoordinatesSupported() {return false;}
	};
	// @formatter:on

	private static final int[] FACING_ANGLES = new int[] { -135, -90, -45, 0, 45, 90, 135, 180 };

	@Override
	public List<TokenFootprint> getFootprints() {
		if (footprintList == null) {
			try {
				footprintList = loadFootprints("net/rptools/maptool/model/gridlessGridFootprints.xml");
			} catch (IOException ioe) {
				MapTool.showError("GridlessGrid.error.notLoaded", ioe);
			}
		}
		return footprintList;
	}

	@Override
	public int[] getFacingAngles() {
		return FACING_ANGLES;
	}

	/*
	 * May as well use the same keys as for the square grid...
	 */
	@Override
	public void installMovementKeys(PointerTool callback, Map<KeyStroke, Action> actionMap) {
		if (movementKeys == null) {
			movementKeys = new HashMap<KeyStroke, Action>(18); // This is 13/0.75, rounded up
			Rectangle r = getFootprint(null).getBounds(this);
			movementKeys.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD7, 0), new MovementKey(callback, -r.width, -r.height));
			movementKeys.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD8, 0), new MovementKey(callback, 0, -r.height));
			movementKeys.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD9, 0), new MovementKey(callback, r.width, -r.height));
			movementKeys.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD4, 0), new MovementKey(callback, -r.width, 0));
//			movementKeys.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD5, 0), new MovementKey(callback, 0, 0));
			movementKeys.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD6, 0), new MovementKey(callback, r.width, 0));
			movementKeys.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD1, 0), new MovementKey(callback, -r.width, r.height));
			movementKeys.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD2, 0), new MovementKey(callback, 0, r.height));
			movementKeys.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD3, 0), new MovementKey(callback, r.width, r.height));
			movementKeys.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), new MovementKey(callback, -r.width, 0));
			movementKeys.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), new MovementKey(callback, r.width, 0));
			movementKeys.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), new MovementKey(callback, 0, -r.height));
			movementKeys.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), new MovementKey(callback, 0, r.height));
		}
		actionMap.putAll(movementKeys);
	}

	@Override
	public void uninstallMovementKeys(Map<KeyStroke, Action> actionMap) {
		if (movementKeys != null) {
			for (KeyStroke key : movementKeys.keySet()) {
				actionMap.remove(key);
			}
		}
	}

	@Override
	public Rectangle getBounds(CellPoint cp) {
		return new Rectangle(cp.x, cp.y, getSize(), getSize());
	}

	@Override
	public ZonePoint convert(CellPoint cp) {
		return new ZonePoint(cp.x, cp.y);
	}

	@Override
	public CellPoint convert(ZonePoint zp) {
		return new CellPoint(zp.x, zp.y);
	}

	@Override
	protected Area createCellShape(int size) {
		// Doesn't do this
		return null;
	}

	@Override
	public GridCapabilities getCapabilities() {
		return GRID_CAPABILITIES;
	}

	@Override
	public double getCellWidth() {
		return getSize();
	}

	@Override
	public double getCellHeight() {
		return getSize();
	}
}
