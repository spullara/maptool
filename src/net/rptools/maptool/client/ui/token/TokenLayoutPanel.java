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
package net.rptools.maptool.client.ui.token;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.AppStyle;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.util.ImageManager;

public class TokenLayoutPanel extends JPanel {

	private Token token;

	private int dragOffsetX;
	private int dragOffsetY;
	
	public TokenLayoutPanel() {
		
		addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {

				// Not for non snap to scale
				if (!token.isSnapToScale()) {
					return;
				}
				
				double delta = e.getWheelRotation() > 0 ? -.1 : .1;
				
				double scale = token.getSizeScale() + delta;

				// Range
				scale = Math.max(.1, scale);
				scale = Math.min(3, scale);
				
				token.setSizeScale(scale);
				
				repaint();
			}
		});
		
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				dragOffsetX = e.getX();
				dragOffsetY = e.getY();
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				
				int dx = e.getX() - dragOffsetX;
				int dy = e.getY() - dragOffsetY;
				
				Zone zone = MapTool.getFrame().getCurrentZoneRenderer().getZone();
				
				int halfGridSize = zone.getGrid().getSize() / 2;
				int maxXoff = Math.max(halfGridSize, token.getBounds(zone).width - zone.getGrid().getSize());
				int maxYoff = Math.max(halfGridSize, token.getBounds(zone).height - zone.getGrid().getSize());

				int offX = Math.min(maxXoff, Math.max(token.getAnchor().x + dx, -maxXoff));
				int offY = Math.min(maxYoff, Math.max(token.getAnchor().y + dy, -maxYoff));
				
				token.setAnchor(offX, offY);
				
				dragOffsetX = e.getX();
				dragOffsetY = e.getY();
				
				repaint();
			}
		});
	}

	public double getSizeScale() {
		return token.getSizeScale();
	}
	
	public int getAnchorX() {
		return token.getAnchor().x;
	}
	
	public int getAnchorY() {
		return token.getAnchor().y;
	}
	
	public void setToken(Token token) {
		this.token = new Token(token);
	}
	
	@Override
	protected void paintComponent(Graphics g) {

		Dimension size = getSize();
		Zone zone = MapTool.getFrame().getCurrentZoneRenderer().getZone();

		// Gather info
		BufferedImage image = ImageManager.getImage(token.getImageAssetId());

		Rectangle tokenSize = token.getBounds(zone);
		Dimension imgSize = new Dimension(image.getWidth(), image.getHeight());
		SwingUtil.constrainTo(imgSize, tokenSize.width, tokenSize.height);
		
		Point centerPoint = new Point(size.width/2, size.height/2);
		
		Graphics2D g2d = (Graphics2D) g;
	
		// Background
		((Graphics2D)g).setPaint(new TexturePaint(AppStyle.panelTexture, new Rectangle(0, 0, AppStyle.panelTexture.getWidth(), AppStyle.panelTexture.getHeight())));
		g2d.fillRect(0, 0, size.width, size.height);
		AppStyle.shadowBorder.paintWithin((Graphics2D)g, 0, 0, size.width, size.height);
		
		// Grid
		if (zone.getGrid().getCapabilities().isSnapToGridSupported()) {
			Area gridShape = zone.getGrid().getCellShape();
			int offsetX = (size.width - gridShape.getBounds().width)/2;
			int offsetY = (size.height - gridShape.getBounds().height)/2;
			g2d.setColor(Color.black);
			
			g2d.translate(offsetX, offsetY);
			g2d.draw(gridShape);
			g2d.translate(-offsetX, -offsetY);
		}
		
		// Token
		g2d.drawImage(image, centerPoint.x - imgSize.width/2 + token.getAnchor().x, centerPoint.y - imgSize.height/2 + token.getAnchor().y, imgSize.width, imgSize.height, this);
		
	}
}
