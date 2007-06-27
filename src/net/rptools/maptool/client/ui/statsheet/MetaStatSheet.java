package net.rptools.maptool.client.ui.statsheet;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

import net.rptools.lib.image.ImageUtil;
import net.rptools.lib.swing.SwingUtil;

public class MetaStatSheet {

	private StatSheet topSheet;
	private StatSheet bottomSheet;
	private StatSheet leftSheet;
	private StatSheet rightSheet;
	
	public MetaStatSheet(String backgroundLeft, Rectangle boundsLeft, String backgroundTop, Rectangle boundsTop) {
		
		try {
			BufferedImage imageLeft = ImageUtil.getCompatibleImage(backgroundLeft);
			
			BufferedImage imageRight = ImageUtil.flip(imageLeft, 1);
			Rectangle boundsRight = SwingUtil.flip(new Dimension(imageRight.getWidth(), imageRight.getHeight()), boundsLeft, 1);
	
			BufferedImage imageTop = ImageUtil.getCompatibleImage(backgroundTop);

			BufferedImage imageBottom = ImageUtil.flip(imageTop, 2);
			Rectangle boundsBottom = SwingUtil.flip(new Dimension(imageRight.getWidth(), imageRight.getHeight()), boundsRight, 2);
			
			// TODO: Flip the bounds too, right now I'm assuming they are roughly symmetrical, but this may not always be the case
			topSheet = new StatSheet(imageTop, boundsTop);
			bottomSheet = new StatSheet(imageBottom, boundsBottom);
			leftSheet = new StatSheet(imageLeft, boundsLeft);
			rightSheet = new StatSheet(imageRight, boundsRight);

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public void render(Graphics2D g, Map<String, String> propertyMap, Rectangle anchorBounds, Dimension viewBounds) {
		
		StatSheet sheet = null;
		int x = 0;
		int y = 0;
	
		int midX = anchorBounds.x + anchorBounds.width/2;
		int midY = anchorBounds.y + anchorBounds.height/2;
		
		// Try to fit it onto the screen
		if (leftSheet.getWidth() < anchorBounds.x && leftSheet.getHeight()/2 + midY < viewBounds.height && midY - leftSheet.getHeight()/2 > 0) {
			sheet = leftSheet;
			x = anchorBounds.x - leftSheet.getWidth();
			y = midY - leftSheet.getHeight()/2;
			
		} else if (rightSheet.getWidth() + anchorBounds.x + anchorBounds.width < viewBounds.width && rightSheet.getHeight()/2 + midY < viewBounds.height  && midY - leftSheet.getHeight()/2 > 0) {
			sheet = rightSheet;
			x = anchorBounds.x + anchorBounds.width;
			y = midY - rightSheet.getHeight()/2;
			
		} else if (anchorBounds.y - topSheet.getHeight() > 0) {
			sheet = topSheet;
			x = midX - topSheet.getWidth()/2;
			y = anchorBounds.y - topSheet.getHeight();
		} else {
			sheet = bottomSheet;
			x = midX - bottomSheet.getWidth()/2;
			y = anchorBounds.y + anchorBounds.height;
		}
		
		g.translate(x, y);
		sheet.render(g, propertyMap);
		g.translate(-x, -y);
	}
}
