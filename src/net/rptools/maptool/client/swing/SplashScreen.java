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
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class SplashScreen extends JFrame {
	private Image splashImage;

	private int imgWidth, imgHeight;

	public SplashScreen(String imgName, final String text) {
		setUndecorated(true);
		loadSplashImage(imgName);

		setContentPane(new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				g.drawImage(splashImage, 0, 0, this);
				g.setColor(Color.black);
				g.drawRect(0, 0, imgWidth-1, imgHeight-1);
				
				FontMetrics fm = g.getFontMetrics();
				
				int x = 5;
				int y = imgHeight - fm.getDescent() - 5;
				
				g.setColor(new Color(255, 247, 232, 210));
				g.fillRoundRect(x-2, y - fm.getAscent() - 2, SwingUtilities.computeStringWidth(fm, text) + 4, fm.getHeight() + 4, 3, 3);
				
				g.setColor(Color.black);
				g.drawString(text, x, y);
			}
		});
	}

	public void loadSplashImage(String imgName) {
		MediaTracker tracker = new MediaTracker(this);
		splashImage = Toolkit.getDefaultToolkit().createImage(SplashScreen.class.getClassLoader().getResource(imgName));
		tracker.addImage(splashImage, 0);
		try {
			tracker.waitForAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
		imgWidth = splashImage.getWidth(this);
		imgHeight = splashImage.getHeight(this);
	}

	public void showSplashScreen() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int w = imgWidth;
		int h = imgHeight;
		int x = (screenSize.width - w) / 2;
		int y = (screenSize.height - h) / 2;
		setBounds(x, y, w, h);
		setVisible(true);
	}

	public void hideSplashScreen() {
		setVisible(false);
		dispose();
		
	}

}
