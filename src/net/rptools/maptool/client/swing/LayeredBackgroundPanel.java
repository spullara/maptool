/* The MIT License
 * 
 * Copyright (c) 2005 David Rice, Trevor Croft
 * 
 * Permission is hereby granted, free of charge, to any person 
 * obtaining a copy of this software and associated documentation files 
 * (the "Software"), to deal in the Software without restriction, 
 * including without limitation the rights to use, copy, modify, merge, 
 * publish, distribute, sublicense, and/or sell copies of the Software, 
 * and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be 
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS 
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN 
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
 * SOFTWARE.
 */
package net.rptools.maptool.client.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;

/**
 */
public class LayeredBackgroundPanel extends JLayeredPane {

	private JComponent backgroundComponent;
	
	public LayeredBackgroundPanel () {
		
		setLayout(new LayeredLayoutManager());
	}
	
	public JComponent getBackgroundComponent() {
		return backgroundComponent;
	}
	
	public void setBackgroundComponent(JComponent comp) {
		
		if (backgroundComponent != null) {
			remove(backgroundComponent);
		}
		
		backgroundComponent = comp;
		if (backgroundComponent != null) {
			backgroundComponent.setSize(getSize());
			add(backgroundComponent, new Integer(JLayeredPane.DEFAULT_LAYER.intValue()-1)); // Below everything else
		}
		
		doLayout();
		repaint();
	}
	
	private class LayeredLayoutManager implements LayoutManager {
		
		public void addLayoutComponent(String name, Component comp) {
		}
		public void layoutContainer(Container parent) {
			
			if (backgroundComponent != null) {
				backgroundComponent.setSize(getSize());
			}
		}
		public Dimension minimumLayoutSize(Container parent) {
			return null;
		}
		public Dimension preferredLayoutSize(Container parent) {
			return null;
		}
		public void removeLayoutComponent(Component comp) {
		}
	}
}
