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
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.rptools.lib.MD5Key;
import net.rptools.lib.image.ImageUtil;
import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.AppStyle;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.util.ImageManager;

public class AssetViewerDialog extends JDialog {

	private MD5Key assetId;
	private boolean sized;
	private Rectangle cancelBounds;
	
	private boolean showHelp;
	
	public AssetViewerDialog(String title, MD5Key assetId) {
		super(MapTool.getFrame(), title);
		this.assetId = assetId;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new GridLayout());
		setUndecorated(true);

		add(new InnerPanel());
	}
	
	@Override
	public void setVisible(boolean b) {
		if (b) {
			SwingUtil.centerOver(this, MapTool.getFrame());
		}
		super.setVisible(b);
	}
	
	private class InnerPanel extends JPanel {

		private int dragStartX, dragStartY;
		
		public InnerPanel() {
			setMinimumSize(new Dimension(100, 100));
			setPreferredSize(new Dimension(100, 100));
			setOpaque(false);

			addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					dragStartX = e.getX();
					dragStartY = e.getY();
				}
				@Override
				public void mouseClicked(MouseEvent e) {
					if (cancelBounds != null && cancelBounds.contains(e.getX(), e.getY())) {
						AssetViewerDialog.this.setVisible(false);
						AssetViewerDialog.this.dispose();
					}
				}
				@Override
				public void mouseEntered(MouseEvent e) {
					showHelp = true;
					repaint();
				}
				
				@Override
				public void mouseExited(MouseEvent e) {
					showHelp = false;
					repaint();
				}
			});
			addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseDragged(MouseEvent e) {

					int dx = e.getX() - dragStartX;
					int dy = e.getY() - dragStartY;
					
					if (SwingUtilities.isLeftMouseButton(e)) {
						// Move
						Point currLocation = AssetViewerDialog.this.getLocation();
						AssetViewerDialog.this.setLocation(currLocation.x + dx, currLocation.y + dy);
					} else {
						// Resize
						Dimension size = AssetViewerDialog.this.getSize();
						
						// Keep aspect ratio the same
						size.width += dx;
						size.height += dy;
						
						BufferedImage image = ImageManager.getImage(AssetManager.getAsset(assetId), AssetViewerDialog.this);
						double ratio = image.getWidth()/(double)image.getHeight();
						size.height = (int)(size.width / ratio);

						// Keep it within the screen
						Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
						if (size.width > screenSize.width || size.height > screenSize.height) {
							SwingUtil.constrainTo(size, screenSize.width, screenSize.height);
						}

						AssetViewerDialog.this.setSize(size.width, size.height);
						AssetViewerDialog.this.validate();
						
						dragStartX = e.getX();
						dragStartY = e.getY();
					}
				}
			});
			
		}
		
		@Override
		public void paintComponent(Graphics g) {
			
			Graphics2D g2d = (Graphics2D) g;
			
			Dimension size = getSize();
			
			BufferedImage image = ImageManager.getImage(AssetManager.getAsset(assetId), this);
			if (!sized) {
				updateSize(image);
				if (image != ImageManager.UNKNOWN_IMAGE) {
					sized = true;
				}
			}
			
			Dimension imgSize = new Dimension(image.getWidth(), image.getHeight());
			SwingUtil.constrainTo(imgSize, size.width, size.height);

			Object oldHint = g2d.getRenderingHint(RenderingHints.KEY_RENDERING);
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.drawImage(image, 0, 0, imgSize.width, imgSize.height, this);
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, oldHint);
			
			// BORDER
			g.setColor(Color.black);
			g.drawRect(0, 0, size.width-1, size.height-1);
		
			// Controls
			BufferedImage cancelButton = AppStyle.cancelButton;
			int x = size.width - cancelButton.getWidth() - 1;
			int y = 1;
			
			g.drawImage(cancelButton, x, y, this);
			cancelBounds = new Rectangle(x, y, cancelButton.getWidth(), cancelButton.getHeight());
			
			// Help
			if (showHelp) {
				Object oldAA = SwingUtil.useAntiAliasing(g2d);

				String helpLeftClick = "Left click-drag to move";
				String helpRightClick = "Right click-drag to resize";
				
				FontMetrics fm = g2d.getFontMetrics();
				
				int hx = 5;
				int hy = 5;

				g.setColor(Color.black);
				g.drawString(helpLeftClick, hx, hy+fm.getAscent());
				g.drawString(helpRightClick, hx, hy+fm.getHeight() + fm.getAscent()+5);
				
				g.setColor(Color.white);
				g.drawString(helpLeftClick, hx-1, hy+fm.getAscent()-1);
				g.drawString(helpRightClick, hx-1, hy+fm.getHeight() + fm.getAscent()+5-1);

				SwingUtil.restoreAntiAliasing(g2d, oldAA);
			}
		}
		
		@Override
		public boolean imageUpdate(Image img, int infoflags, int x, int y, int w,
				int h) {

			if (infoflags == ALLBITS) {
				updateSize(img);
			}
			
			return super.imageUpdate(img, infoflags, x, y, w, h);
		}
		
	}

	private void updateSize(Image img) {

		Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
		
		// Keep it within the screen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if (size.width > screenSize.width || size.height > screenSize.height) {
			SwingUtil.constrainTo(size, screenSize.width, screenSize.height);
		}
		
		getContentPane().setPreferredSize(size);
		getContentPane().setMinimumSize(size);

		pack();

		// Keep it on screen
		SwingUtil.centerOver(this, MapTool.getFrame());
		Point p = getLocation();
		setLocation(Math.max(0, p.x), Math.max(0, p.y));
	}
}
