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
package net.rptools.maptool.client.ui.macrobuttons.panels;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TabPopupListener extends MouseAdapter {

	private JComponent component;
	private int index;
	
	public TabPopupListener(JComponent component, int index) {
		this.component = component;
		this.index = index;
	}

	public void mousePressed(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {
			new TabPopupMenu(component, index).show(component, e.getX(), e.getY());
		} else {
			//System.out.println("Tab index: " + ((JTabbedPane) component).indexAtLocation(e.getX(), e.getY()));
		}
	}
}
