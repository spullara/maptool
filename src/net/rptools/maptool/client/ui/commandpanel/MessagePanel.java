package net.rptools.maptool.client.ui.commandpanel;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JTextPane;

public class MessagePanel extends JPanel {

	private Box messageBox;
	
	public MessagePanel() {
		setLayout(new BorderLayout());
		messageBox = Box.createVerticalBox();
		
		add(BorderLayout.CENTER, messageBox);
	}
	
	public void addMessage(String message) {
		
		JTextPane area = new JTextPane();
		area.setContentType("text/html");
		area.setText("<html><body>" + message + "</body></html>");
		
		messageBox.add(area);
		messageBox.add(Box.createHorizontalGlue());
		
		revalidate();
		repaint();
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(100, 100);
	}
}
