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
package net.rptools.maptool.client.ui.zone.vbl;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Area;
import java.awt.geom.Point2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class AreaTreeInspector extends JPanel {
	private final AreaTree tree;
	private final Color[] colors = {
			Color.gray,
			Color.blue,
			Color.yellow,
			Color.orange,
			Color.cyan
	};

	private Point2D point;

	public AreaTreeInspector() {
		Area area = new Area();

		area.add(new Area(new Rectangle(100, 100, 300, 300)));
		area.subtract(new Area(new Rectangle(150, 200, 100, 100)));
		area.subtract(new Area(new Rectangle(300, 200, 75, 100)));
		area.add(new Area(new Rectangle(175, 225, 50, 50)));
		area.subtract(new Area(new Rectangle(180, 230, 20, 20)));

		area.add(new Area(new Rectangle(450, 100, 300, 300)));
		area.subtract(new Area(new Rectangle(500, 200, 100, 100)));
		area.subtract(new Area(new Rectangle(650, 200, 75, 100)));
		area.add(new Area(new Rectangle(525, 225, 50, 50)));

		tree = new AreaTree(area);

		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				point = e.getPoint();
				repaint();
			}
		});
	}

	@Override
	protected void paintComponent(Graphics g) {
		Dimension size = getSize();
		g.setColor(Color.white);
		g.fillRect(0, 0, size.width, size.height);

		AreaOcean ocean = tree.getOcean();

//		paintOcean((Graphics2D)g, ocean, 0);

		if (point != null) {
			Graphics2D g2d = (Graphics2D) g.create(0, 0, size.width, size.height);

			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f));
			g2d.setColor(Color.blue);

			ocean = tree.getOceanAt(point);
			if (ocean != null && ocean.getBounds() != null) {
				g2d.fill(ocean.getBounds());
			}
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .25f));
			g2d.setColor(Color.red);

			for (VisibleAreaSegment segment : ocean.getVisibleAreaSegments(point)) {
				Area area = segment.getArea();
				if (area != null) {
					g2d.fill(area);
				}
			}
			g2d.dispose();
		}
	}

	private void paintOcean(Graphics2D g, AreaOcean ocean, int depth) {
		if (ocean.getBounds() != null) {
			g.setColor(Color.white);
			g.fill(ocean.getBounds());

			g.setColor(colors[depth % colors.length]);
			g.draw(ocean.getBounds());
		}
		for (AreaIsland island : ocean.getIslands()) {
			paintIsland(g, island, depth + 1);
		}
	}

	private void paintIsland(Graphics2D g, AreaIsland island, int depth) {
		g.setColor(Color.gray);
		g.fill(island.getBounds());

		g.setColor(colors[depth % colors.length]);
		g.draw(island.getBounds());

		for (AreaOcean ocean : island.getOceans()) {
			paintOcean(g, ocean, depth + 1);
		}
	}

	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setBounds(100, 100, 800, 500);
		f.add(new AreaTreeInspector());
		f.setVisible(true);
	}
}
