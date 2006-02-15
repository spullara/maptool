package net.rptools.maptool.client.ui.commandpanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class MessagePanel extends JPanel {

	private Box messageBox;
	private StringBuffer messages;
	private JTextPane textPane;
	
	public MessagePanel() {
		setLayout(new BorderLayout());
		messageBox = Box.createVerticalBox();
		messages = new StringBuffer();
		textPane = new JTextPane();
		textPane.setContentType("text/html");
		
		JScrollPane pane = new JScrollPane(textPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		add(BorderLayout.CENTER, pane);
	}
	
	public void addMessage(String message) {
		
//		JTextPane area = new JTextPane();
//		area.setContentType("text/html");
//		area.setText("<html><body>" + message + "</body></html>");
//		
//		messageBox.add(area);
//		messageBox.add(Box.createHorizontalGlue());
//		
//		revalidate();
//		repaint();
		
		messages.append(message).append("<br>");
		textPane.setText("<html><body>" + messages.toString() + "</body></html>");
		textPane.scrollRectToVisible(new Rectangle(0, getSize().height, 1, 1));
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(100, 100);
	}
}
