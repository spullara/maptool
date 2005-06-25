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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.SwingUtilities;

import net.rptools.common.util.ImageUtil;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.util.GraphicsUtil;

/**
 */
public class UnboundedZoneRenderer extends ZoneRenderer implements ZoneOverlay {

	private BufferedImage backBuffer;
	private BufferedImage tileImage;
	
	private Dimension lastSize;
	
	public UnboundedZoneRenderer(Zone zone) {
		super(zone);
		
		try {
			tileImage = ImageUtil.getImage("net/rptools/maptool/client/image/texture/grass.png");
		} catch (IOException ioe) {
			throw new IllegalArgumentException ("Could not find image:" + ioe);
		}
		
		addOverlay(this);
	}
	
	/* (non-Javadoc)
	 * @see net.rptools.maptool.client.ZoneRenderer#renderBoard(java.awt.Graphics)
	 */
	protected void renderBoard(Graphics2D g) {

		Dimension size = getSize();
		if (backBuffer == null || !lastSize.equals(size)) {
			createBackBuffer();
			lastSize = size;
		}
		
		g.drawImage(backBuffer, (offsetX % tileImage.getWidth()) - tileImage.getWidth(), 
				( offsetY % tileImage.getHeight()) - tileImage.getHeight(), null);
	}

	/* (non-Javadoc)
	 * @see net.rptools.maptool.client.ZoneRenderer#renderGrid(java.awt.Graphics)
	 */
	protected void renderGrid(Graphics2D g) {

        float scale = scaleArray[scaleIndex];
        float gridSize = zone.getGridSize() * scale;
        Dimension size = getSize();
        
        int offX = (int)(offsetX % gridSize);
        int offY = (int)(offsetY % gridSize);
        for (int row = 0; row < size.height + gridSize; row += gridSize) {
            
            g.drawLine(0, row + offY, size.width, row + offY);
        }

        for (int col = 0; col < size.width + gridSize; col += gridSize) {
            
            g.drawLine(col + offX, 0, col + offX, size.height);
        }
        
	}

	/* (non-Javadoc)
	 * @see net.rptools.maptool.client.ZoneRenderer#getBackgroundImage()
	 */
	public BufferedImage getBackgroundImage() {
		return backBuffer;
	}
	
	private void createBackBuffer() {
		
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
	
	////
	// ZONE OVERLAY
	public void paintOverlay(ZoneRenderer renderer, Graphics2D g) {
		
        // Find the cell in the middle of the screen
        Dimension size = getSize();
        Point cell = renderer.getCellAt(size.width/2, size.height/2); 
        
		g.setColor(Color.black);
		GraphicsUtil.drawBoxedString (g, cell.x + ", " + cell.y, 50, 15, SwingUtilities.LEFT);
	}
}
