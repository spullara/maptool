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
package net.rptools.maptool.client;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.IOException;

import net.rptools.lib.image.ImageUtil;
import net.rptools.lib.swing.ImageBorder;

/**
 * @author trevor
 */
public class AppStyle {

	public static ImageBorder border = ImageBorder.GRAY;
	public static ImageBorder selectedBorder = ImageBorder.RED;
	public static ImageBorder selectedStampBorder = ImageBorder.BLUE;
	public static ImageBorder miniMapBorder = AppConstants.GRAY_BORDER;
	public static ImageBorder shadowBorder = AppConstants.SHADOW_BORDER;
	public static ImageBorder commonMacroBorder = AppConstants.HIGHLIGHT_BORDER;

	public static Font labelFont = Font.decode("serif-NORMAL-12");
	
	public static BufferedImage tokenInvisible;

    public static BufferedImage cellWaypointImage;
	
    public static BufferedImage stackImage;
    
    public static BufferedImage markerImage;
    
    public static Color selectionBoxOutline = Color.black;
    public static Color selectionBoxFill = Color.blue;
    
    public static BufferedImage chatImage;
    public static BufferedImage chatScrollImage;
    public static BufferedImage chatScrollLockImage;
    
    public static Color topologyColor = new Color(0, 0, 255, 128);
    public static Color topologyAddColor = new Color(255, 0, 0, 128);
    public static Color topologyRemoveColor = new Color(255, 255, 255, 128);

    public static BufferedImage boundedBackgroundTile;
    
    public static BufferedImage cancelButton;
    public static BufferedImage addButton;
    
    public static BufferedImage panelTexture;
    
    public static BufferedImage lookupTableDefaultImage;
	
	public static BufferedImage resourceLibraryImage;
	public static BufferedImage mapExplorerImage;
	public static BufferedImage connectionsImage;
	public static BufferedImage chatPanelImage;
	public static BufferedImage globalPanelImage;
	public static BufferedImage campaignPanelImage;
	public static BufferedImage selectionPanelImage;
	public static BufferedImage impersonatePanelImage;
    public static BufferedImage tablesPanelImage;
    public static BufferedImage initiativePanelImage;
	public static BufferedImage arrowOut;
	public static BufferedImage arrowRotateClockwise;
	public static BufferedImage arrowIn;
	public static BufferedImage arrowRight;
	public static BufferedImage arrowLeft;
	
	public static BufferedImage lightSourceIcon;
	
	static {
		
		try {
			// Set defaults
			tokenInvisible = ImageUtil.getCompatibleImage("net/rptools/maptool/client/image/icon_invisible.png");
            cellWaypointImage  = ImageUtil.getCompatibleImage("net/rptools/maptool/client/image/redDot.png");
            stackImage  = ImageUtil.getCompatibleImage("net/rptools/maptool/client/image/stack.png");
            markerImage = ImageUtil.getCompatibleImage("net/rptools/maptool/client/image/marker.png");
            chatImage  = ImageUtil.getCompatibleImage("net/rptools/maptool/client/image/chat-blue.png");
            chatScrollImage = ImageUtil.getCompatibleImage("net/rptools/maptool/client/image/comments.png"); 
            chatScrollLockImage = ImageUtil.getCompatibleImage("net/rptools/maptool/client/image/comments_delete.png"); 
            boundedBackgroundTile = ImageUtil.getCompatibleImage("net/rptools/maptool/client/image/Black.png");
            panelTexture = ImageUtil.getCompatibleImage("net/rptools/maptool/client/image/panelTexture.jpg");
            
			cancelButton = ImageUtil.getCompatibleImage("net/rptools/maptool/client/image/cancel_sm.png");
			addButton = ImageUtil.getCompatibleImage("net/rptools/maptool/client/image/add_sm.png");

			lookupTableDefaultImage = ImageUtil.getCompatibleImage("net/rptools/maptool/client/image/document.jpg");

			resourceLibraryImage = ImageUtil.getCompatibleImage("net/rptools/maptool/client/image/book_open.png");
			mapExplorerImage = ImageUtil.getCompatibleImage("net/rptools/maptool/client/image/eye.png");
			connectionsImage = ImageUtil.getCompatibleImage("net/rptools/maptool/client/image/computer.png");
			chatPanelImage = ImageUtil.getCompatibleImage("net/rptools/maptool/client/image/application.png");
			globalPanelImage = ImageUtil.getCompatibleImage("net/rptools/maptool/client/image/global_panel.png");
			campaignPanelImage = ImageUtil.getCompatibleImage("net/rptools/maptool/client/image/campaign_panel.png");
			selectionPanelImage = ImageUtil.getCompatibleImage("net/rptools/maptool/client/image/cursor.png");
			impersonatePanelImage = ImageUtil.getCompatibleImage("net/rptools/maptool/client/image/impersonate.png");
            tablesPanelImage = ImageUtil.getCompatibleImage("net/rptools/maptool/client/image/layers.png");         
            initiativePanelImage = ImageUtil.getCompatibleImage("net/rptools/maptool/client/image/initiativePanel.png");         
			arrowOut = ImageUtil.getCompatibleImage("net/rptools/maptool/client/image/arrow_out.png");			
			arrowRotateClockwise = ImageUtil.getCompatibleImage("net/rptools/maptool/client/image/arrow_rotate_clockwise.png");
			arrowIn = ImageUtil.getCompatibleImage("net/rptools/maptool/client/image/arrow_in_red.png");
			arrowRight = ImageUtil.getCompatibleImage("net/rptools/maptool/client/image/arrow_right.png");
			arrowLeft = ImageUtil.getCompatibleImage("net/rptools/maptool/client/image/arrow_left.png");
			
			lightSourceIcon = ImageUtil.getCompatibleImage("net/rptools/maptool/client/image/lightbulb.png");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
