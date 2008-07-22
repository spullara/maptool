package net.rptools.maptool.client.ui.macrobuttons.panels;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import net.rptools.maptool.client.AppStyle;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;

public class MenuButtonsPanel extends JPanel {

	public MenuButtonsPanel() {
		//TODO: refactoring reminder
		setLayout(new FlowLayout(FlowLayout.LEFT));
		
		ImageIcon i = new ImageIcon(AppStyle.arrowOut);
		JButton label = new JButton(i) {
			public Insets getInsets() {
				return new Insets(2, 2, 2, 2);
			}
		};
		label.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent event) {
				ZoneRenderer renderer = MapTool.getFrame().getCurrentZoneRenderer();
				renderer.selectTokens(new Rectangle(renderer.getX(), renderer.getY(), renderer.getWidth(), renderer.getHeight()));
			}
		});
		label.setToolTipText("Select all tokens on the map");
		label.setBackground(null);
		add(label);
		
		ImageIcon i3 = new ImageIcon(AppStyle.arrowIn);
		JButton label3 = new JButton(i3) {
			public Insets getInsets() {
				return new Insets(2, 2, 2, 2);
			}
		};
		label3.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent event) {
				ZoneRenderer renderer = MapTool.getFrame().getCurrentZoneRenderer();
				renderer.clearSelectedTokens();
			}
		});
		label3.setToolTipText("Deselect all tokens");
		label3.setBackground(null);
		add(label3);
		/*
		ImageIcon i1 = new ImageIcon(AppStyle.arrowRotateClockwise);
		JButton label1 = new JButton(i1) {
			public Insets getInsets() {
				return new Insets(2, 2, 2, 2);
			}
		};
		label1.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent event) {
				MapTool.getFrame().getCurrentZoneRenderer().undoSelectToken();
			}
		});
		label1.setToolTipText("Revert to previous selection");
		label1.setBackground(null);
		add(label1);
		*/
	}

	
	@Override
	public Dimension getPreferredSize() {

		Dimension size = getParent().getSize();

		FlowLayout layout = (FlowLayout) getLayout();
		Insets insets = getInsets();

		// This isn't exact, but hopefully it's close enough
		int x = layout.getHgap() + insets.left;
		int y = layout.getVgap();
		int rowHeight = 0;
		for (Component c : getComponents()) {

			Dimension cSize = c.getPreferredSize();
			if (x + cSize.width + layout.getHgap() > size.width - insets.right && x > 0) {
				x = 0;
				y += rowHeight + layout.getVgap();
				rowHeight = 0;
			}

			x += cSize.width + layout.getHgap();
			rowHeight = Math.max(cSize.height, rowHeight);
		}

		y += rowHeight + layout.getVgap();

		y += getInsets().top;
		y += getInsets().bottom;

		Dimension prefSize = new Dimension(size.width, y);
		return prefSize;
	}

}
