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
package net.rptools.maptool.client.ui.zone;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;

import net.rptools.lib.MD5Key;
import net.rptools.lib.image.ImageUtil;
import net.rptools.maptool.client.AppState;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.util.ImageManager;

/**
 */
public class UnboundedZoneRenderer extends ZoneRenderer {

	private BufferedImage backBuffer;
	private BufferedImage tileImage;
	
	private Dimension lastSize;
	
	private boolean loaded;
	
	public UnboundedZoneRenderer(Zone zone) {
		super(zone);
	}
	
	@Override
	public boolean isLoading() {
		return !loaded;
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
		g.drawImage(backBuffer, (getViewOffsetX() % tileImage.getWidth()) - tileImage.getWidth(), 
				( getViewOffsetY() % tileImage.getHeight()) - tileImage.getHeight(), null);
	}

	/* (non-Javadoc)
	 * @see net.rptools.maptool.client.ZoneRenderer#renderGrid(java.awt.Graphics)
	 */
	protected void renderGrid(Graphics2D g) {

        float scale = getScale();
        float gridSize = zone.getGridSize() * scale;
        Dimension size = getSize();

        g.setColor(new Color(zone.getGridColor()));
        
        int offX = (int)(getViewOffsetX() % gridSize);
        int offY = (int)(getViewOffsetY() % gridSize);
        for (int row = 0; row < size.height + gridSize; row += gridSize) {
            
            if (AppState.getGridSize() == 1) {
                g.drawLine(0, row + offY, size.width, row + offY);
            } else {
            	g.fillRect(0, row + offY - (AppState.getGridSize()/2), size.width, AppState.getGridSize());
            }
        }

        for (int col = 0; col < size.width + gridSize; col += gridSize) {
            
            if (AppState.getGridSize() == 1) {
                g.drawLine(col + offX, 0, col + offX, size.height);
            } else {
            	g.fillRect(col + offX - (AppState.getGridSize()/2), 0, AppState.getGridSize(), size.height);
            }
        }
        
	}
	
    @Override
	public BufferedImage getMiniImage(int size) {
        // TODO: I suppose this should honor the size
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
				tileImage = ImageUtil.getImage("net/rptools/lib/resource/image/texture/grass.png");
				return tileImage;
			}
	
			Asset asset = AssetManager.getAsset(assetId);
			if (asset != null) {
				BufferedImage image = ImageManager.getImage(asset, this);
				if (image != ImageManager.UNKNOWN_IMAGE) {
					backBuffer = null;
					tileImage = image;
					
					loaded = true;
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
		backBuffer = ImageUtil.createCompatibleImage(size.width + tileImage.getWidth()*2, size.height + tileImage.getHeight()*2, Transparency.OPAQUE);
		
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
