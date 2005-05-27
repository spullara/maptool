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
import java.awt.LayoutManager2;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


/**
 */
public class PositionalLayout implements LayoutManager2 {

	public enum Position {
		
		NW, N, NE,
		W, CENTER, E,
		SW, S, SE
	}

	private int padding = 0; 

	private Map<Component, Position> compPositionMap = new HashMap<Component, Position>();

	public PositionalLayout() {}
	
	public PositionalLayout(int edgePadding) {
		padding = edgePadding;
	}
	
	public void addLayoutComponent(Component comp,Object constraints){
		if (! (constraints instanceof Position)) {
			return;
		}
		
		compPositionMap.put(comp, (Position) constraints);
	}
	
	public void addLayoutComponent(String name, Component comp) {
		throw new IllegalArgumentException("Use add(comp, Position)");
	}
	
	public float getLayoutAlignmentX(Container target){
		return 0;
	}
	
	public float getLayoutAlignmentY(Container target){
		return 0;
	}
	
	public void invalidateLayout(Container target) {
		// Nothing to do right now
	}
	
	public void layoutContainer(Container parent) {
		
		Dimension size = parent.getSize();
		
		Component[] compArray = parent.getComponents();
		for (Component comp : compArray) {
			
			Position pos = compPositionMap.get(comp);
			Dimension compSize = comp.getSize();
			
			int x = 0;
			int y = 0;
			
			switch(pos) {
			case NW: {x = padding; y = padding; break;}
			case N:  {x = center(size.width, compSize.width); y = padding; break;}
			case NE: {x = size.width - compSize.width - padding; y = padding; break;}
			case W:  {x = padding; y = center(size.height, compSize.height); break;}
			case E:  {x = size.width - compSize.width - padding; y = center(size.height, compSize.height); break;}
			case SW: {x = padding; y = size.height - compSize.height - padding; break;}
			case S:  {x = center(size.width, compSize.width); y = size.height - compSize.height - padding; break;}
			case SE: {x = size.width - compSize.width - padding; y = size.height - compSize.height - padding; break;}
			case CENTER: {
				x = 0;
				y = 0;
				
				// Fill available space
				comp.setSize(size);
			}
			}
			
			comp.setLocation(x, y);
		}
	}
	
	private int center (int outsideWidth, int insideWidth) {
		return (outsideWidth - insideWidth) / 2;
	}
	
	public Dimension maximumLayoutSize(Container target){
		return preferredLayoutSize(target);
	}
	
	public Dimension minimumLayoutSize(Container parent) {
		return preferredLayoutSize(parent);
	}
	
	public Dimension preferredLayoutSize(Container parent) {
		return new Dimension(0,0);
	}
	
	public void removeLayoutComponent(Component comp) {

		compPositionMap.remove(comp);
	}

	public static void main (String[] args) {
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panel = new PositionalPanel();
		
		panel.add(createButton("NW"), Position.NW);
		panel.add(createButton("N"), Position.N);
		panel.add(createButton("NE"), Position.NE);
		panel.add(createButton("W"), Position.W);
		panel.add(createButton("E"), Position.E);
		panel.add(createButton("SW"), Position.SW);
		panel.add(createButton("S"), Position.S);
		panel.add(createButton("SE"), Position.SE);
		panel.add(createButton("CENTER"), Position.CENTER);
		
		frame.setContentPane(panel);
		
		frame.setSize(200, 200);
		frame.setVisible(true);
	}
	
	private static JButton createButton(String label) {
		JButton button = new JButton (label);
		button.setSize(button.getMinimumSize());
		
		return button;
	}
}
