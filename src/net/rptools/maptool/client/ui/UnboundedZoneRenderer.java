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
package net.rptools.maptool.client.ui;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;

import net.rptools.common.util.ImageUtil;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.util.ImageManager;
import net.rptools.maptool.util.MD5Key;

/**
 */
public class UnboundedZoneRenderer extends ZoneRenderer {

	private BufferedImage backBuffer;
	private BufferedImage tileImage;
	
	private Dimension lastSize;
	
	public UnboundedZoneRenderer(Zone zone) {
		super(zone);
	}
	
	/* (non-Javadoc)
	 * @see net.rptools.maptool.client.ZoneRenderer#renderBoard(java.awt.Graphics)
	 */
	protected void renderBoard(Graphics2D g) {

		Dimension size = getSize();
		if (tileImage == null || backBuffer == null || !lastSize.equals(size)) {
			createBackBuffer();
			lastSize = size;
		}
		
		BufferedImage tileImage = getTileImage();
		g.drawImage(backBuffer, (viewOffset.x % tileImage.getWidth()) - tileImage.getWidth(), 
				( viewOffset.y % tileImage.getHeight()) - tileImage.getHeight(), null);
	}

	/* (non-Javadoc)
	 * @see net.rptools.maptool.client.ZoneRenderer#renderGrid(java.awt.Graphics)
	 */
	protected void renderGrid(Graphics2D g) {

        float scale = scaleArray[scaleIndex];
        float gridSize = zone.getGridSize() * scale;
        Dimension size = getSize();
        
        int offX = (int)(viewOffset.x % gridSize);
        int offY = (int)(viewOffset.y % gridSize);
        for (int row = 0; row < size.height + gridSize; row += gridSize) {
            
            g.drawLine(0, row + offY, size.width, row + offY);
        }

        for (int col = 0; col < size.width + gridSize; col += gridSize) {
            
            g.drawLine(col + offX, 0, col + offX, size.height);
        }
        
	}

	public BufferedImage getMiniImage() {
		return getTileImage();
	}
	
	private BufferedImage getTileImage() {

		if (tileImage != null) {
			return tileImage;
		}

		try {
			MD5Key assetId = zone.getAssetID();
			if (assetId == null) {
				// TODO: make this static
				tileImage = ImageUtil.getImage("net/rptools/maptool/client/image/texture/grass.png");
				return tileImage;
			}
	
			Asset asset = AssetManager.getAsset(assetId);
			if (asset != null) {
				BufferedImage image = ImageManager.getImage(asset);
				if (image != ImageManager.UNKNOWN_IMAGE) {
					backBuffer = null;
					tileImage = image;
				}
				return image;
			}
			
			return ImageManager.UNKNOWN_IMAGE;
			
		} catch (IOException ioe) {
			return ImageManager.UNKNOWN_IMAGE;
		}
	}
	
	private void createBackBuffer() {
		
		BufferedImage tileImage = getTileImage();
		
		Dimension size = getSize();
		backBuffer = new BufferedImage(size.width + tileImage.getWidth()*2, size.height + tileImage.getHeight()*2, Transparency.OPAQUE);
		
		int cols = backBuffer.getWidth() / tileImage.getWidth() + 1;
		int rows = backBuffer.getHeight() / tileImage.getHeight() + 1;
		Graphics g = null;
		try {
			g = backBuffer.getGraphics();
			
			for (int row = 0; row < rows; row++) {
				
				for (int col = 0; col < cols; col++) {
					
					g.drawImage(tileImage, col * tileImage.getWidth(), row * tileImage.getHeight(), null);
				}
			}
		} finally {
			if (g != null) {
				g.dispose();
			}
		}
	}
}
