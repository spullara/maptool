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
package net.rptools.maptool.client.ui.zone;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.Zone;

public class ZonePopupMenu extends JPopupMenu {

	private Zone zone;
	
	public ZonePopupMenu(Zone zone) {
		super("Zone");
		
		this.zone = zone;

		Action action = null;
		if (zone.isVisible()) {
			action = new AbstractAction() {
				{
					putValue(NAME, "Hide from players");
				}
				public void actionPerformed(ActionEvent e) {
					ZonePopupMenu.this.zone.setVisible(false);
					MapTool.serverCommand().setZoneVisibility(ZonePopupMenu.this.zone.getId(), false);
					MapTool.getFrame().getZoneMiniMapPanel().flush();
					MapTool.getFrame().refresh();
				}
			};
		} else {
			action = new AbstractAction() {
				{
					putValue(NAME, "Show to players");
				}
				public void actionPerformed(ActionEvent e) {
					
					ZonePopupMenu.this.zone.setVisible(true);
					MapTool.serverCommand().setZoneVisibility(ZonePopupMenu.this.zone.getId(), true);
					MapTool.getFrame().getZoneMiniMapPanel().flush();
					MapTool.getFrame().refresh();
				}
			};
		}
		add(new JMenuItem(action));
	}

	
	
}
