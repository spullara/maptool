package net.rptools.maptool.client.ui.commandpanel;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.MouseListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class MessagePanel extends JPanel {

	private StringBuffer messages;
	private JTextPane textPane;
	private JScrollPane scrollPane;
	
	public MessagePanel() {
		setLayout(new BorderLayout());
		messages = new StringBuffer();
		textPane = new JTextPane();
		textPane.setContentType("text/html");
		textPane.setBorder(null);
		textPane.setEditable(false);
		
		scrollPane = new JScrollPane(textPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		add(BorderLayout.CENTER, scrollPane);
	}

	@Override
	public synchronized void addMouseListener(MouseListener l) {
		super.addMouseListener(l);
		textPane.addMouseListener(l);
		scrollPane.addMouseListener(l);
	}
	
	@Override
	public synchronized void removeMouseListener(MouseListener l) {
		super.removeMouseListener(l);
		textPane.removeMouseListener(l);
		scrollPane.removeMouseListener(l);
	}
	
	public void addMessage(final String message) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				messages.append("<div>").append(message).append("</div>");
				textPane.setText("<html><body>" + messages.toString() + "</body></html>");
				textPane.scrollRectToVisible(new Rectangle(0, textPane.getSize().height, 1, 1));
				System.out.println(new Rectangle(0, textPane.getSize().height, 1, 1));
			}
		});
	}
	
}
