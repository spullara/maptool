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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.border.BevelBorder;

import net.rptools.maptool.client.MapToolClient;

/**
 * @author trevor
 */
public class ColorPickerButton extends JComponent {

	private Color color;
	private String title;
	
	public ColorPickerButton(String title, Color defaultColor) {
		color = defaultColor;
		this.title = title;
		
		addMouseListener(new MouseAdapter(){
			
			public void mouseClicked(MouseEvent e) {
				
	            Color oldColor = color;
	            Color newColor = JColorChooser.showDialog(MapToolClient.getInstance(), ColorPickerButton.this.title, oldColor);
	            
	            if (newColor != null) {
	                ColorPickerButton.this.color = newColor;
	                repaint();
	            }
			}
		});
		
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
	}
	

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	protected void paintComponent(Graphics g) {

		Dimension mySize = getSize();
		
		g.setColor(color);
		g.fillRect(0, 0, mySize.width, mySize.height);
	}
	
	public Color getSelectedColor() {
		return color;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#getMinimumSize()
	 */
	public Dimension getMinimumSize() {
		return new Dimension(16, 16);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	public Dimension getPreferredSize() {
		return getMinimumSize();
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#getMaximumSize()
	 */
	public Dimension getMaximumSize() {
		return getMinimumSize();
	}
}
