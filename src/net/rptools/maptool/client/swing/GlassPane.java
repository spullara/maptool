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
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class GlassPane extends JPanel {
	private BufferedImage backImage;

	private boolean modal;

	public GlassPane() {
		super(null); // No layout manager

	}

	public void setModel(boolean modal) {
		this.modal = modal;
	}

	@Override
	public void setVisible(boolean aFlag) {

		if (modal) {
			JComponent root = getRootPane();
			Dimension size = root.getSize();
			if (backImage == null || backImage.getWidth() != size.width
					|| backImage.getHeight() != size.height) {
				backImage = new BufferedImage(size.width, size.height,
						Transparency.OPAQUE);
			}

			// Get a copy of the current application state
			Graphics2D g = backImage.createGraphics();
			g.setClip(0, 0, size.width, size.height);
			root.paint(g);

			// Shade it
			g.setColor(new Color(1, 1, 1, .5f));
			g.fillRect(0, 0, size.width, size.height);
			
			// Consume all actions
			addMouseMotionListener(new MouseMotionAdapter(){});
			addMouseListener(new MouseAdapter(){});
		} else {
			for(MouseMotionListener listener : getMouseMotionListeners()) {
				removeMouseMotionListener(listener);
			}
			for(MouseListener listener : getMouseListeners()) {
				removeMouseListener(listener);
			}
		}
		

		setOpaque(modal);

		super.setVisible(aFlag);
		
		if (getComponents().length > 0) {
			getComponents()[0].requestFocus();
		}
	}

	@Override
	protected void paintComponent(Graphics g) {

		if (!modal) {
			return;
		}

		// Show the application contents behind the pane
		g.drawImage(backImage, 0, 0, this);
	}

}
