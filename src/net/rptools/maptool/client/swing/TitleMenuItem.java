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
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

/**
 */
public class TitleMenuItem extends JMenuItem {

	private String title;
	
	public TitleMenuItem(String title) {
		super(title);
		setEnabled(false);
		
		this.title = title;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	protected void paintComponent(Graphics g) {

		g.setColor(Color.darkGray);
		g.fillRect(0, 0, getSize().width, getSize().height);
		
		g.setColor(Color.white);
		FontMetrics fm = g.getFontMetrics();
		
		int x = (getSize().width - SwingUtilities.computeStringWidth(fm, title)) / 2;
		int y = (getSize().height - fm.getHeight()) / 2 + fm.getAscent();
		
		g.drawString(title, x, y);
	}
}
