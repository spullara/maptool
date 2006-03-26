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
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.AppStyle;
import net.rptools.maptool.client.AppUtil;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.tool.PointerTool;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.util.ImageManager;

public class StackSummaryPanel extends JComponent implements FocusListener, MouseListener, MouseMotionListener {
	
	public static final int PADDING = 7;
	
	private List<Token> tokenList;
	private int gridSize;
	
	private List<TokenLocation> tokenLocationList = new ArrayList<TokenLocation>();
	
	public StackSummaryPanel(int gridSize, List<Token> tokenList) {
		this.tokenList = tokenList;
		this.gridSize = gridSize;
		
		setPreferredSize(new Dimension(tokenList.size()*(gridSize + PADDING) + PADDING, gridSize + PADDING*2));
		setBackground(Color.gray);
		setForeground(Color.black);
		setRequestFocusEnabled(true);
		
		addFocusListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	@Override
	protected void paintComponent(Graphics g) {

		Dimension size = getSize();
		
		// Background
		g.setColor(getBackground());
		g.fillRect(0, 0, size.width, size.height);
		
		// Border
		AppStyle.border.paintWithin((Graphics2D) g, 0, 0, size.width-1, size.height-1);
		
		// Images
		tokenLocationList.clear();
		for (int i = 0; i < tokenList.size(); i++) {
			
			Token token = tokenList.get(i);
			
			BufferedImage image = ImageManager.getImage(AssetManager.getAsset(token.getAssetID()), this);
			
			Dimension imgSize = new Dimension(image.getWidth(), image.getHeight());
			SwingUtil.constrainTo(imgSize, gridSize);

			Rectangle bounds = new Rectangle(PADDING + i*(gridSize + PADDING), PADDING, imgSize.width, imgSize.height);
			
			g.drawImage(image, bounds.x, bounds.y, bounds.width, bounds.height, this);
			
			tokenLocationList.add(new TokenLocation(bounds, token));
		}
	}

	// TODO: consolidate this with the zonerenderer version
	private static class TokenLocation {
		public Rectangle bounds;
		public Token token;
		
		public TokenLocation(Rectangle bounds, Token token) {
			this.bounds = bounds;
			this.token = token;
		}
	}

	////
	// FOCUS LISTENER
	public void focusGained(FocusEvent e) {
	}
	public void focusLost(FocusEvent e) {

		// Go away
		MapTool.getFrame().hideGlassPane();
	}
	
	////
	// MOUSE LISTENER
	public void mouseClicked(MouseEvent e) {
	}
	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {
		MapTool.getFrame().hideGlassPane();
	}
	public void mousePressed(MouseEvent e) {
	}
	public void mouseReleased(MouseEvent e) {
	}
	
	////
	// MOUSE MOTION LISTENER
	public void mouseDragged(MouseEvent e) {
		
		for (TokenLocation location : tokenLocationList) {
			if (location.bounds.contains(e.getX(), e.getY())) {
			
				if (!AppUtil.playerOwnsToken(location.token)) {
					return;
				}
				
				ZoneRenderer renderer = MapTool.getFrame().getCurrentZoneRenderer();
				
				renderer.clearSelectedTokens();
				renderer.selectToken(location.token.getId());
				MapTool.getFrame().hideGlassPane();
				
				Tool tool = MapTool.getFrame().getToolbox().getSelectedTool();
				if (!(tool instanceof PointerTool)) {
					return;
				}
				
				((PointerTool) tool).startTokenDrag(location.token);
				
				return;
			}
		}
	}
	public void mouseMoved(MouseEvent e) {
	}
	
}
