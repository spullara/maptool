package net.rptools.maptool.client.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JPanel;

import net.rptools.maptool.util.GraphicsUtil;

public abstract class MessageDialog extends JPanel {

	public MessageDialog () {
		addMouseListener(new MouseAdapter(){});
		addMouseMotionListener(new MouseMotionAdapter(){});
	}
	
	protected abstract String getStatus();
	
	@Override
	protected void paintComponent(Graphics g) {
		
		Dimension size = getSize();
		g.setColor(new Color(0, 0, 0, .5f));
		g.fillRect(0, 0, size.width, size.height);
		
		GraphicsUtil.drawBoxedString((Graphics2D) g, getStatus(), size.width/2, size.height/2);
		
	}
	
}
