/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *  
 *	http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package net.rptools.maptool.client.swing;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;

public class ScrollablePanel extends JPanel implements Scrollable {

	private int unitIncrement;
	private int blockIncrement;

	public ScrollablePanel(JComponent component, int unitIncrement, int blockIncrement) {
		setLayout(new GridLayout());
		add(component);
		
		this.unitIncrement = unitIncrement;
		this.blockIncrement = blockIncrement;
	}

	public ScrollablePanel(JComponent component, int unitIncrement) {
		this(component, unitIncrement, unitIncrement * 3);
	}

	public ScrollablePanel(JComponent component) {
		this(component, 20);
	}
	
	public static JScrollPane wrap(JComponent component) {
		
		JScrollPane pane = new JScrollPane(new ScrollablePanel(component), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		return pane;
	}
	
	////
	// SCROLLABLE
	public Dimension getPreferredScrollableViewportSize() {
		// TODO Auto-generated method stub
		return null;
	}
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return blockIncrement;
	}
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}
	public boolean getScrollableTracksViewportWidth() {
		return true;
	}
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return unitIncrement;
	}
}
