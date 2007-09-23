package net.rptools.maptool.client.ui.token;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.AssetManager;
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

				int offX = Math.min(halfGridSize, Math.max(token.getAnchor().x + dx, -halfGridSize));
				int offY = Math.min(halfGridSize, Math.max(token.getAnchor().y + dy, -halfGridSize));
				
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

		if (!zone.getGrid().getCapabilities().isSnapToGridSupported()) {
			g.setColor(Color.black);
			g.fillRect(0, 0, size.width, size.height);
			g.setColor(Color.white);
			g.drawString("Not supported", 30, 65);
			return;
		}
		
		// Gather info
		
		Paint backgroundPaint = zone.getBackgroundPaint().getPaint();

		Color gridColor = new Color(zone.getGridColor());
		Area gridShape = zone.getGrid().getCellShape();

		BufferedImage image = ImageManager.getImage(AssetManager.getAsset(token.getImageAssetId()));

		Dimension tokenSize = token.getSize(zone.getGrid());
		
		Point centerPoint = new Point(size.width/2, size.height/2);
		
		Graphics2D g2d = (Graphics2D) g;
	
		// Background
		g2d.setPaint(backgroundPaint);
		g2d.fillRect(0, 0, size.width, size.height);
		
		// Grid
		int offsetX = (size.width - gridShape.getBounds().width)/2;
		int offsetY = (size.height - gridShape.getBounds().height)/2;
		g2d.setColor(gridColor);
		
		g2d.translate(offsetX, offsetY);
		g2d.draw(gridShape);
		g2d.translate(-offsetX, -offsetY);

		// Token
		g2d.drawImage(image, centerPoint.x - tokenSize.width/2 + token.getAnchor().x, centerPoint.y - tokenSize.height/2 + token.getAnchor().y, tokenSize.width, tokenSize.height, this);
		
	}
}
