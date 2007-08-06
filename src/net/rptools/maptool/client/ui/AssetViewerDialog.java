package net.rptools.maptool.client.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

import javax.swing.JDialog;
import javax.swing.JPanel;

import net.rptools.lib.MD5Key;
import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.util.ImageManager;

public class AssetViewerDialog extends JDialog {

	private MD5Key assetId;
	private boolean sized;
	
	public AssetViewerDialog(String title, MD5Key assetId) {
		super(MapTool.getFrame(), title);
		this.assetId = assetId;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new GridLayout());
		setMinimumSize(new Dimension(100, 100));
		

		add(new InnerPanel());
	}
	
	private class InnerPanel extends JPanel {

		private int dragStartX, dragStartY;
		
		public InnerPanel() {
			addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					dragStartX = e.getX();
					dragStartY = e.getY();
				}
			});
			addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseDragged(MouseEvent e) {

					int dx = e.getX() - dragStartX;
					int dy = e.getY() - dragStartY;
					
					Point currLocation = AssetViewerDialog.this.getLocation();
					AssetViewerDialog.this.setLocation(currLocation.x + dx, currLocation.y + dy);
				}
			});
		}
		
		@Override
		public void paintComponent(Graphics g) {
			
			Dimension size = getSize();
			g.setColor(Color.black);
			g.fillRect(0, 0, size.width, size.height);
			
			BufferedImage image = ImageManager.getImage(AssetManager.getAsset(assetId), this);
			if (!sized) {
				updateSize(image);
				if (image != ImageManager.UNKNOWN_IMAGE) {
					sized = true;
				}
			}
			
			g.drawImage(image, 0, 0, this);
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
		Insets insets = getInsets();
		setSize(img.getWidth(null) + insets.left + insets.right, img.getHeight(null) + insets.top + insets.bottom);
		SwingUtil.centerOver(this, MapTool.getFrame());
	}
}
