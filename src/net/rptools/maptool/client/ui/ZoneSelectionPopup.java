package net.rptools.maptool.client.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.AppState;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ScreenPoint;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.ZonePoint;
import net.rptools.maptool.util.ImageManager;

public class ZoneSelectionPopup extends JPopupMenu {

	private static final int MAP_SIZE_WIDTH = 100;
	private static final int MAP_SIZE_HEIGHT = 80;
	private static final int PADDING = 3;
	
	public ZoneSelectionPopup() {
		
		List<ZoneRenderer> rendererList = new LinkedList<ZoneRenderer>(MapTool.getFrame().getZoneRenderers());
		if (!MapTool.getPlayer().isGM()) {
			for (ListIterator<ZoneRenderer> iter = rendererList.listIterator(); iter.hasNext();) {
				ZoneRenderer renderer = iter.next();
				if (!renderer.getZone().isVisible()) {
					iter.remove();
				}
			}
		}
		
		int rows = rendererList.size() >= 6 ? (int)Math.ceil(rendererList.size() / 6.0) : 1;
		int cols = rendererList.size() >= 6 ? 6 : rendererList.size();

		setLayout(new GridLayout(rows, cols));
		
		for (ZoneRenderer renderer : rendererList) {
			
			add(new SelectZoneButton(renderer));
		}
	}
	
	private class SelectZoneButton extends JButton implements ActionListener{
		
		private ZoneRenderer renderer;
		
		public SelectZoneButton(ZoneRenderer renderer) {
			this.renderer = renderer;
			addActionListener(this);
		}

		@Override
		public Dimension getPreferredSize() {
			FontMetrics fm = getFontMetrics(getFont());
			return new Dimension(MAP_SIZE_WIDTH + PADDING * 2, MAP_SIZE_HEIGHT + fm.getHeight() + PADDING + PADDING*2);
		}
		
		public void actionPerformed(ActionEvent e) {
			
    		if (MapTool.getFrame().getCurrentZoneRenderer() != renderer) {
        		MapTool.getFrame().setCurrentZoneRenderer(renderer);
        		MapTool.getFrame().refresh();
        		
        		if (AppState.isPlayerViewLinked()) {
                	ZonePoint zp = new ScreenPoint(renderer.getWidth()/2, renderer.getHeight()/2).convertToZone(renderer);
        			MapTool.serverCommand().enforceZone(renderer.getZone().getId());
        			MapTool.serverCommand().enforceZoneView(renderer.getZone().getId(), zp.x, zp.y, renderer.getScaleIndex());
        		}
    		}
    		
    		ZoneSelectionPopup.this.setVisible(false);
		}

		@Override
		protected void paintComponent(Graphics g) {

			Graphics2D g2d = (Graphics2D) g;
			Insets insets = getInsets();
			Object oldAA = g2d.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			boolean isSelected = renderer == MapTool.getFrame().getCurrentZoneRenderer(); 
			
			// Background
			Dimension size = getSize();
			g.setColor(isSelected ? Color.lightGray : Color.white);
			g.fillRect(0, 0, size.width, size.height);

			if (!renderer.getZone().isVisible()) {
				Stroke oldStroke = g2d.getStroke();
				g.setColor(isSelected ? Color.darkGray : Color.lightGray);
				g2d.setStroke(new BasicStroke(2));
				
				for (int i = 0; i < size.width*2 || i < size.height*2; i += 10) {
					g.drawLine(i, 0, 0, i);
				}
				
				g2d.setStroke(oldStroke);
			}
			
			// Map
			BufferedImage img = renderer.getMiniImage(MAP_SIZE_WIDTH);
	        if (img == null || img == ImageManager.UNKNOWN_IMAGE) {
	            img = ImageManager.UNKNOWN_IMAGE;
	            
	            // Let's wake up when the image arrives
	            ImageManager.addObservers(renderer.getZone().getAssetID(), this);
	        }

	        Dimension imgSize = new Dimension(img.getWidth(), img.getHeight());
			SwingUtil.constrainTo(imgSize, MAP_SIZE_WIDTH, MAP_SIZE_HEIGHT);
			g.drawImage(img, (size.width - imgSize.width)/2, PADDING + insets.top, imgSize.width, imgSize.height, this);

			// Label
			String name = renderer.getZone().getName();
			if (name == null || name.length() == 0) {
				name = "Map";
			}

			int nameWidth = SwingUtilities.computeStringWidth(g.getFontMetrics(), name);
			FontMetrics fm = g.getFontMetrics();
			
			if (!renderer.getZone().isVisible()) {
				// Clear out the lines from the label
				g.setColor(isSelected ? Color.lightGray : Color.white);
				g.fillRect((size.width - nameWidth)/2-3, size.height - fm.getHeight() - insets.bottom, nameWidth+6, fm.getHeight()); 
			}
			
			g.setColor(Color.black);
			g.drawString(name, size.width/2 - nameWidth/2, size.height - fm.getDescent() - insets.bottom);
			
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAA);
		}
		
		@Override
		public boolean imageUpdate(Image img, int infoflags, int x, int y, int w, int h) {
			repaint();
			return super.imageUpdate(img, infoflags, x, y, w, h);
		}
	}
}
