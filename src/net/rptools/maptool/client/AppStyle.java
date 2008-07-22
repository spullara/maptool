/* The MIT License
 * 
 * Copyright (c) 2005 David Rice, Trevor Croft
 * 
 * Permission is hereby granted, free of charge, to any person 
 * obtaining a copy of this software and associated documentation files 
 * (the "Software"), to deal in the Software without restriction, 
 * including without limitation the rights to use, copy, modify, merge, 
 * publish, distribute, sublicense, and/or sell copies of the Software, 
 * and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be 
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS 
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN 
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
 * SOFTWARE.
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
	public static BufferedImage arrowOut;
	public static BufferedImage arrowRotateClockwise;
	public static BufferedImage arrowIn;
	
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
			arrowOut = ImageUtil.getCompatibleImage("net/rptools/maptool/client/image/arrow_out.png");			
			arrowRotateClockwise = ImageUtil.getCompatibleImage("net/rptools/maptool/client/image/arrow_rotate_clockwise.png");
			arrowIn = ImageUtil.getCompatibleImage("net/rptools/maptool/client/image/arrow_in_red.png");
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
