package net.rptools.maptool.client.ui.macrobuttonpanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Scrollable;

import net.rptools.maptool.model.Token;

public class SelectionTab extends JPanel implements Scrollable {
	
	public SelectionTab() {
		setLayout(new SelectionTabLayout());
		setBackground(Color.white);
	}

	public void update(List<Token> tokenList) {
		clear();
		add(new ButtonGroup(tokenList));
		for (Token token : tokenList) {
			add(new ButtonGroup(token));
		}
		revalidate();
		repaint();
	}
	
	public void clear() {
		removeAll();
		revalidate();
		repaint();
	}

	private static class SelectionTabLayout implements LayoutManager2 {
		public void layoutContainer(Container parent) {
			
			int x = 0;
			int y = 0;
			
			for (Component c : parent.getComponents()) {

				c.setSize(parent.getSize().width, c.getPreferredSize().height);
				c.setLocation(0, y);
				
				y += c.getSize().height;
				
			}
			
		}
		public void addLayoutComponent(Component comp, Object constraints) {}
		public void addLayoutComponent(String name, Component comp) {}
		public float getLayoutAlignmentX(Container target) {
			return 0;
		}
		public float getLayoutAlignmentY(Container target) {
			return 0;
		}
		public void invalidateLayout(Container target) {}
		public Dimension maximumLayoutSize(Container target) {
			return new Dimension(0, 0);
		}
		public Dimension minimumLayoutSize(Container parent) {
			return new Dimension(0, 0);
		}
		public Dimension preferredLayoutSize(Container parent) {
			return new Dimension(0, 0);
		}
		public void removeLayoutComponent(Component comp) {}
	}
	
	////
	// SCROLLABLE
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}
	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return 75;
	}
	public boolean getScrollableTracksViewportHeight() {
		return getPreferredSize().height < getParent().getSize().height;
	}
	public boolean getScrollableTracksViewportWidth() {
		return true;
	}
	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return 25;
	}
}