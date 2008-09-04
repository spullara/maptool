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
package net.rptools.maptool.client.ui.zone.vbl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Area;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class AreaTreeInspector extends JPanel {

	private AreaTree tree;
	private Color[] colors = {
		
		Color.gray,
		Color.blue,
		Color.yellow,
		Color.orange,
		Color.cyan
	};
	
	public AreaTreeInspector() {

		Area area = new Area();
		
		area.add(new Area(new Rectangle(100, 100, 300, 300)));
		area.subtract(new Area(new Rectangle(150, 200, 100, 100)));
		area.subtract(new Area(new Rectangle(300, 200, 75, 100)));
		area.add(new Area(new Rectangle(175, 225, 50, 50)));

		area.add(new Area(new Rectangle(450, 100, 300, 300)));
		area.subtract(new Area(new Rectangle(500, 200, 100, 100)));
		area.subtract(new Area(new Rectangle(650, 200, 75, 100)));
		area.add(new Area(new Rectangle(525, 225, 50, 50)));

		tree = new AreaTree(area);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		
		Dimension size = getSize();
		g.setColor(Color.white);
		g.fillRect(0, 0, size.width, size.height);
		
		AreaOcean ocean = tree.getOcean();
		
		paintOcean((Graphics2D)g, ocean, 0);
	}
	
	private void paintOcean(Graphics2D g, AreaOcean ocean, int depth) {
		
		if (ocean.getBounds() != null) {
			g.setColor(Color.white);
			g.fill(ocean.getBounds());
			
			g.setColor(colors[depth%colors.length]);
			g.draw(ocean.getBounds());
		}
		
		for (AreaIsland island : ocean.getIslands()) {
			paintIsland(g, island, depth+1);
		}
	}
	
	private void paintIsland(Graphics2D g, AreaIsland island, int depth) {
		
		g.setColor(Color.gray);
		g.fill(island.getBounds());
		
		g.setColor(colors[depth%colors.length]);
		g.draw(island.getBounds());
		
		for (AreaOcean ocean : island.getOceans()) {
			paintOcean(g, ocean, depth+1);
		}
	}
	
	
	public static void main(String[] args) {
		
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setBounds(100,100, 500, 500);
		f.add(new AreaTreeInspector());
		f.setVisible(true);
	}
}
