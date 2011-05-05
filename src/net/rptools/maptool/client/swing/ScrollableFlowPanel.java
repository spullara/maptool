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
package net.rptools.maptool.client.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

/**
 * This code is taken directly from:
 *  
 * 		http://forum.java.sun.com/thread.jspa?forumID=57&threadID=701797&start=2
 * 
 * In direct response to this bug:
 * 
 * 		http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5082531
 * 
 * @author trevor
 */
public class ScrollableFlowPanel extends JPanel implements Scrollable {
	
	public ScrollableFlowPanel() {
		// Default
	}
	
	public ScrollableFlowPanel(int alignment) {
		setLayout(new FlowLayout(alignment));
	}
	
	public void setBounds( int x, int y, int width, int height ) {
		super.setBounds( x, y, getParent().getWidth(), height );
	}

	public Dimension getPreferredSize() {
		return new Dimension( getWidth(), getPreferredHeight() );
	}
	
	public Dimension getPreferredScrollableViewportSize() {
		return super.getPreferredSize();
	}

	public int getScrollableUnitIncrement( Rectangle visibleRect, int orientation, int direction ) {
		int hundredth = ( orientation ==  SwingConstants.VERTICAL
				? getParent().getHeight() : getParent().getWidth() ) / 100;
		return ( hundredth == 0 ? 1 : hundredth ); 
	}

	public int getScrollableBlockIncrement( Rectangle visibleRect, int orientation, int direction ) {
		return orientation == SwingConstants.VERTICAL ? getParent().getHeight() : getParent().getWidth();
	}

	public boolean getScrollableTracksViewportWidth() {
		return true;
	}

	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	private int getPreferredHeight() {
		int rv = 0;
		for ( int k = 0, count = getComponentCount(); k < count; k++ ) {
			Component comp = getComponent( k );
			Rectangle r = comp.getBounds();
			int height = r.y + r.height;
			if ( height > rv )
				rv = height;
		}
		rv += ( (FlowLayout) getLayout() ).getVgap();
		return rv;
	}
}
