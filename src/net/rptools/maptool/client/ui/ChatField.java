package net.rptools.maptool.client.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComponent;

import net.rptools.maptool.client.MapTool;

public class ChatField extends JComponent implements KeyListener {

	private static final Dimension PREFERRED_SIZE = new Dimension(200, 25);
	
	private StringBuilder currentTypedChars = new StringBuilder();
	
	private ChatPanel chatPanel;
	private boolean isTyping;
	
	public ChatField(ChatPanel chatPanel) {
		addKeyListener(this);
		this.chatPanel = chatPanel;
		
		setForeground(Color.white);
		setBackground(Color.black);
	}

	public void startTyping() {
		isTyping = true;
		requestFocus();
		currentTypedChars.setLength(0);

		repaint();
	}
	
	public void commitTyping() {
		chatPanel.commitCommand(currentTypedChars.toString());
		stopTyping();
	}
	
	public void cancelTyping() {
		stopTyping();
	}
	
	private void stopTyping() {
		isTyping = false;
		repaint();
	}
	
	@Override
	public Dimension getPreferredSize() {
		return PREFERRED_SIZE;
	}
	
	@Override
	public boolean isFocusable() {
		return true;
	}

	@Override
	protected void paintComponent(Graphics g) {
		
		FontMetrics fm = g.getFontMetrics();
		Dimension size = getSize();
		
		if (isTyping) {
			
			String str = "/" + currentTypedChars;
			
			g.setColor(getBackground());
			g.drawString(str, 3 + 1, size.height - 3 - fm.getDescent() + 1);
			
			g.setColor(getForeground());
			g.drawString(str, 3, size.height - 3 - fm.getDescent());
		}		
	}
	
	////
	// KEY LISTENER
	public void keyPressed(KeyEvent e) {

		if (!isTyping) {
			return;
		}
		
		switch (e.getKeyCode()) {
		case KeyEvent.VK_BACK_SPACE: {
			if (currentTypedChars.length() > 0) {
				currentTypedChars.setLength(currentTypedChars.length()-1);
				repaint();
			}
			return;
		}
		case KeyEvent.VK_ENTER: {
			commitTyping();
		}
		case KeyEvent.VK_ESCAPE: {
			cancelTyping();
		}
		}
	}
	public void keyReleased(KeyEvent e) {}
	
	public void keyTyped(KeyEvent e) {

		if (!isTyping) {
			return;
		}

		if (e.getKeyChar() == '/' && currentTypedChars.length() == 0) {
			return;
		}
		
		char ch = e.getKeyChar();
		if (ch >= 32 && ch <= 127) {
			currentTypedChars.append(ch);
			repaint();
		}
	}
}
