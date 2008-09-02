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
	
	public Image getImage() {
		return img;
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
