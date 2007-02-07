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
package net.rptools.maptool.client.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JComponent;

import net.rptools.lib.swing.SwingUtil;

public class ImagePreviewPanel extends JComponent {

	private Image img;

	public ImagePreviewPanel() {
		setPreferredSize(new Dimension(150, 100));
		setMinimumSize(new Dimension(150, 100));
	}

	public void setImage(Image image) {

		this.img = image;
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {

		// Image
		Dimension size = getSize();
		if (img != null) {
			Dimension imgSize = new Dimension(img.getWidth(null), img
					.getHeight(null));
			SwingUtil.constrainTo(imgSize, size.width, size.height);

			// Border
			int x = (size.width - imgSize.width) / 2;
			int y = (size.height - imgSize.height) / 2;

			g.drawImage(img, x, y, imgSize.width, imgSize.height, null);
			g.setColor(Color.black);
			g.drawRect(x, y, imgSize.width - 1, imgSize.height - 1);
		}

	}
}
