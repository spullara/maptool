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
package net.rptools.maptool.client.swing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JFrame;

import net.rptools.maptool.util.ImageUtil;

/**
 * @author trevor
 */
public class ImageBorder {

	private BufferedImage topRight;
	private BufferedImage top;
	private BufferedImage topLeft;
	private BufferedImage left;
	private BufferedImage bottomLeft;
	private BufferedImage bottom;
	private BufferedImage bottomRight;
	private BufferedImage right;
	
	private int topMargin;
	private int bottomMargin;
	private int leftMargin;
	private int rightMargin;
	
	private boolean valid;
	
	public ImageBorder(String imagePath) {
		
		try {
			topRight = ImageUtil.getCompatibleImage(imagePath + "/tr.png");
			top = ImageUtil.getCompatibleImage(imagePath + "/top.png");
			topLeft = ImageUtil.getCompatibleImage(imagePath + "/tl.png");
			left = ImageUtil.getCompatibleImage(imagePath + "/left.png");
			bottomLeft = ImageUtil.getCompatibleImage(imagePath + "/bl.png");
			bottom = ImageUtil.getCompatibleImage(imagePath + "/bottom.png");
			bottomRight = ImageUtil.getCompatibleImage(imagePath + "/br.png");
			right = ImageUtil.getCompatibleImage(imagePath + "/right.png");

			topMargin = max(topRight.getHeight(), top.getHeight(), topLeft.getHeight());
			bottomMargin = max(bottomRight.getHeight(), bottom.getHeight(), bottomLeft.getHeight());
			rightMargin = max(topRight.getWidth(), right.getWidth(), bottomRight.getWidth());
			leftMargin = max(topLeft.getWidth(), left.getWidth(), bottomLeft.getWidth());
			
			valid = true;
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public int getTopMargin() {
		return topMargin;
	}

	public int getBottomMargin() {
		return bottomMargin;
	}
	
	public int getRightMargin() {
		return rightMargin;
	}
	
	public int getLeftMargin() {
		return leftMargin;
	}
	
	public void paintAround(Graphics2D g, Rectangle rect) {
		paintAround(g, rect.x, rect.y, rect.width, rect.height);
	}
	
	public void paintAround(Graphics2D g, int x, int y, int width, int height) {
		paintWithin(g, x - leftMargin, y - topMargin, width + leftMargin + rightMargin, height + topMargin + bottomMargin);
	}
	
	public void paintWithin(Graphics2D g, Rectangle rect) {
		paintWithin(g, rect.x, rect.y, rect.width, rect.height);
	}
	
	public void paintWithin(Graphics2D g, int x, int y, int width, int height) {
		
		// Draw Corners
		g.drawImage(topLeft, x + leftMargin - topLeft.getWidth(), y + topMargin - topLeft.getHeight(), null);
		g.drawImage(topRight, x + width - rightMargin, y + topMargin - topRight.getHeight(), null);
		g.drawImage(bottomLeft, x + leftMargin - bottomLeft.getWidth(), y + height - bottomMargin, null);
		g.drawImage(bottomRight, x + width - rightMargin, y + height - bottomMargin, null);
		
		// Draw top
		int i;
		int max = width - rightMargin;
		for (i = leftMargin; i < max - top.getWidth(); i += top.getWidth()) {
			g.drawImage(top, x + i, y + topMargin - top.getHeight(), null);
		}
		if ( i != max) {
			g.drawImage(top.getSubimage(0, 0, max - i, top.getHeight()), i + x, y + topMargin - top.getHeight(), null);
		}
		
		// Bottom
		for (i = leftMargin; i < max - bottom.getWidth(); i += bottom.getWidth()) {
			g.drawImage(bottom, x + i, y + height - bottomMargin, null);
		}
		if ( i != max) {
			g.drawImage(bottom.getSubimage(0, 0, max - i, bottom.getHeight()), x + i, y + height - bottomMargin, null);
		}
		
		// Left
		max = height - bottomMargin;
		for (i = topMargin; i < max - left.getHeight(); i += left.getHeight()) {
			g.drawImage(left, x + leftMargin - left.getWidth(), y + i, null);
		}
		if ( i != max) {
			g.drawImage(left.getSubimage(0, 0, left.getWidth(), max - i), x + leftMargin - left.getWidth(), y + i, null);
		}
		
		// Right
		for (i = topMargin; i < max - right.getHeight(); i += right.getHeight()) {
			g.drawImage(right, x + width - leftMargin, y + i, null);
		}
		if ( i != max) {
			g.drawImage(right.getSubimage(0, 0, right.getWidth(), max - i), x + width - leftMargin, y + i, null);
		}
	}
	
	private int max(int i1, int i2, int i3) {
		int bigger = i1 > i2 ? i1 : i2;
		return bigger > i3 ? bigger : i3;
	}
	
	public static void main(String[] args) {
		
		JFrame frame = new JFrame();
		frame.setLocation(400, 400);
		frame.setSize(400,400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(new JComponent() {
		
			ImageBorder border = new ImageBorder("net/rptools/maptool/client/image/border/default");

			{
				addMouseMotionListener(new MouseMotionAdapter() {
					
					public void mouseMoved(java.awt.event.MouseEvent e) {
						System.out.println (e.getX() + "." + e.getY());
					}
				});
			}
			
			protected void paintComponent(java.awt.Graphics g) {
				
				g.setColor(Color.red);
				g.drawRect(24, 24, 202, 202);
				
				border.paintWithin((Graphics2D) g, 25, 25, 200, 200);
			}
		});
		
		frame.setVisible(true);
	}
}
