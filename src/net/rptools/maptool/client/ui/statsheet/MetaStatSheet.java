package net.rptools.maptool.client.ui.statsheet;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import net.rptools.lib.image.ImageUtil;
import net.rptools.maptool.client.swing.ResourceLoader;

public class MetaStatSheet {

	private StatSheet topSheet;
	private StatSheet bottomSheet;
	private StatSheet leftSheet;
	private StatSheet rightSheet;

	public MetaStatSheet(String statSheetProperties) throws IOException {
		
		Properties props = new Properties();
		props.load(MetaStatSheet.class.getClassLoader().getResourceAsStream(statSheetProperties));
		
		BufferedImage topImage = ImageUtil.getCompatibleImage(props.getProperty("topImage"));
		Rectangle topBounds = ResourceLoader.loadRectangle(props.getProperty("topBounds"));
		
		BufferedImage bottomImage = ImageUtil.getCompatibleImage(props.getProperty("bottomImage"));
		Rectangle bottomBounds = ResourceLoader.loadRectangle(props.getProperty("bottomBounds"));
		
		BufferedImage leftImage = ImageUtil.getCompatibleImage(props.getProperty("leftImage"));
		Rectangle leftBounds = ResourceLoader.loadRectangle(props.getProperty("leftBounds"));
		
		BufferedImage rightImage = ImageUtil.getCompatibleImage(props.getProperty("rightImage"));
		Rectangle rightBounds = ResourceLoader.loadRectangle(props.getProperty("rightBounds"));
		
		topSheet = new StatSheet(topImage, topBounds);
		bottomSheet = new StatSheet(bottomImage, bottomBounds);
		leftSheet = new StatSheet(leftImage, leftBounds);
		rightSheet = new StatSheet(rightImage, rightBounds);
	}
	
	public MetaStatSheet(BufferedImage leftImage, Rectangle leftBounds, BufferedImage rightImage, Rectangle rightBounds, BufferedImage topImage, Rectangle topBounds, BufferedImage bottomImage, Rectangle bottomBounds) {
		
			topSheet = new StatSheet(topImage, topBounds);
			bottomSheet = new StatSheet(bottomImage, bottomBounds);
			leftSheet = new StatSheet(leftImage, leftBounds);
			rightSheet = new StatSheet(rightImage, rightBounds);
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
