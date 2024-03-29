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
package net.rptools.maptool.client.ui.statsheet;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import net.rptools.lib.image.ImageUtil;
import net.rptools.maptool.client.swing.ResourceLoader;

public class MetaStatSheet {

	private static final int PADDING = 5;
	
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

		Font attributeFont = null;
		if (props.containsKey("attributeFont")) {
			attributeFont = Font.decode(props.getProperty("attributeFont"));
		}
		Color attributeColor = null;
		if (props.containsKey("attributeColor")) {
			attributeColor = Color.decode(props.getProperty("attributeColor"));
		}
		
		Font valueFont = null;
		if (props.containsKey("valueFont")) {
			valueFont = Font.decode(props.getProperty("valueFont"));
		}
		Color valueColor = null;
		if (props.containsKey("valueColor")) {
			valueColor = Color.decode(props.getProperty("valueColor"));
		}
		
		topSheet = new StatSheet(topImage, topBounds, attributeFont, attributeColor, valueFont, valueColor);
		bottomSheet = new StatSheet(bottomImage, bottomBounds, attributeFont, attributeColor, valueFont, valueColor);
		leftSheet = new StatSheet(leftImage, leftBounds, attributeFont, attributeColor, valueFont, valueColor);
		rightSheet = new StatSheet(rightImage, rightBounds, attributeFont, attributeColor, valueFont, valueColor);
	}
	
	public void render(Graphics2D g, Map<String, String> propertyMap, Rectangle anchorBounds, Dimension viewBounds) {
		
		StatSheet sheet = null;
		int x = 0;
		int y = 0;
	
		int midX = anchorBounds.x + anchorBounds.width/2;
		int midY = anchorBounds.y + anchorBounds.height/2;
		
		// Try to fit it onto the screen
		if (leftSheet.getWidth() < anchorBounds.x - PADDING && leftSheet.getHeight()/2 + midY < viewBounds.height && midY - leftSheet.getHeight()/2 > 0) {
			sheet = leftSheet;
			x = anchorBounds.x - leftSheet.getWidth() - PADDING;
			y = midY - leftSheet.getHeight()/2;
			
		} else if (rightSheet.getWidth() + anchorBounds.x + anchorBounds.width + PADDING < viewBounds.width && rightSheet.getHeight()/2 + midY < viewBounds.height  && midY - leftSheet.getHeight()/2 > 0) {
			sheet = rightSheet;
			x = anchorBounds.x + anchorBounds.width + PADDING;
			y = midY - rightSheet.getHeight()/2;
			
		} else if (anchorBounds.y - topSheet.getHeight() - PADDING > 0) {
			sheet = topSheet;
			x = midX - topSheet.getWidth()/2;
			y = anchorBounds.y - topSheet.getHeight() - PADDING;
		} else {
			sheet = bottomSheet;
			x = midX - bottomSheet.getWidth()/2;
			y = anchorBounds.y + anchorBounds.height + PADDING;
		}
		
		g.translate(x, y);
		sheet.render(g, propertyMap);
		g.translate(-x, -y);
	}
}
