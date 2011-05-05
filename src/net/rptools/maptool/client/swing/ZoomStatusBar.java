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
package net.rptools.maptool.client.swing;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;

import net.rptools.maptool.client.AppState;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;

/**
 * Manages the zoom level in the main MapTool window's status bar at the bottom of the window. This means displaying the
 * current zoom level as a percentage as well as allowing a value to be entered and changing the zoom level to that
 * amount.
 */
public class ZoomStatusBar extends JTextField implements ActionListener {
	private static final Dimension minSize = new Dimension(50, 10);

	public ZoomStatusBar() {
		super("", RIGHT);
		setToolTipText("Zoom Level");
		addActionListener(this);
	}

	@Override
	public boolean isEnabled() {
		return !AppState.isZoomLocked() && super.isEnabled();
	}

	public void actionPerformed(ActionEvent e) {
		JTextField target = (JTextField) e.getSource();
		if (MapTool.getFrame().getCurrentZoneRenderer() != null) {
			double zoom;
			ZoneRenderer renderer;
			try {
				zoom = Double.parseDouble(target.getText().replace("%.*", ""));
				renderer = MapTool.getFrame().getCurrentZoneRenderer();
				renderer.setScale(zoom / 100);
				renderer.maybeForcePlayersView();
			} catch (NumberFormatException e1) {
				// If the number is invalid, ignore it.
			}
			update();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#getMinimumSize()
	 */
	@Override
	public Dimension getMinimumSize() {
		return minSize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	@Override
	public Dimension getPreferredSize() {
		return getMinimumSize();
	}

	public void clear() {
		setText("");
	}

	public void update() {
		String zoom = "";
		if (MapTool.getFrame().getCurrentZoneRenderer() != null) {
			double scale = MapTool.getFrame().getCurrentZoneRenderer().getZoneScale().getScale();
			scale *= 100;
			zoom = String.format("%d%%", (int) scale);
		}
		setText(zoom);
	}
}
