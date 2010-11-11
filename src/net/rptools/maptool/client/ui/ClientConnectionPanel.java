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
package net.rptools.maptool.client.ui;

import java.awt.event.MouseListener;

import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;

import net.rptools.lib.swing.PopupListener;
import net.rptools.maptool.client.AppActions;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.PlayerListModel;

/**
 * Implements the contents of the Window -> Connections status panel.
 * Previously this class only displayed a list of connected clients, but it is
 * being extended to include other information as well:
 * <ul>
 * <li>current map name,
 * <li>viewing range of current map (as a rectangle of grid coordinates),
 * <li>whether a macro is running (?),
 * <li>IP address (for ping/traceroute tests?)
 * <li>others?
 * </ul>
 */
public class ClientConnectionPanel extends JList {
	public ClientConnectionPanel () {
		setModel(new PlayerListModel(MapTool.getPlayerList()));
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//		setCellRenderer(new DefaultListCellRenderer());

		addMouseListener(createPopupListener());
	}

	private MouseListener createPopupListener() {
		PopupListener listener = new PopupListener(createPopupMenu());
		return listener;
	}

	private JPopupMenu createPopupMenu() {
		JPopupMenu menu = new JPopupMenu ();
		menu.add(new JMenuItem(AppActions.BOOT_CONNECTED_PLAYER));
		return menu;
	}
}
