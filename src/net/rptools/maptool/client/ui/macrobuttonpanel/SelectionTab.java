package net.rptools.maptool.client.ui.macrobuttonpanel;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.util.List;

import net.rptools.maptool.model.Token;

public class SelectionTab extends JPanel implements Scrollable {
	
	public SelectionTab() {
		setLayout(new GridBagLayout());
		//setBackground(Color.white);
	}

	public void update(List<Token> tokenList) {
		clear();
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridy = 0;
		constraints.gridx = 0;
		constraints.weightx = 1;
		
		// draw common group only when there is more than one token selected
		if (tokenList.size() > 1) {
			add(new ButtonGroup(tokenList), constraints);
			constraints.gridy++;
		}
		for (Token token : tokenList) {
			add(new ButtonGroup(token), constraints);
			constraints.gridy ++;
		}

		// Spacer
		constraints.weighty = 1;
		add(new JLabel(), constraints);
		
		revalidate();
		repaint();
	}
	
	public void clear() {
		removeAll();
		revalidate();
		repaint();
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