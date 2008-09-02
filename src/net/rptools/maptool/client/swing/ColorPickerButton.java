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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.border.BevelBorder;

import net.rptools.maptool.client.MapTool;

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
	            Color newColor = JColorChooser.showDialog(MapTool.getFrame(), ColorPickerButton.this.title, oldColor);
	            
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
