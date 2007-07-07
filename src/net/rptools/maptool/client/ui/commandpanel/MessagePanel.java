package net.rptools.maptool.client.ui.commandpanel;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import net.rptools.maptool.client.AppPreferences;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.TextMessage;

public class MessagePanel extends JPanel {

	private JScrollPane scrollPane;
	private HTMLDocument document;
	private JEditorPane textPane;

	private static final String SND_MESSAGE_RECEIVED = "messageReceived";
	
	public MessagePanel() {
		setLayout(new GridLayout());

		textPane = new JEditorPane();
		textPane.setEditable(false);
		textPane.setEditorKit(new HTMLEditorKit());
		
		document = (HTMLDocument) textPane.getDocument();
		
		// Create the style
		StyleSheet style = document.getStyleSheet();
		style.addRule("span{text-align:left; background: red}");
		style.addRule("div{text-align:left}");
		style.addRule("body{align:left;width:100%}");
		style.addRule("td{text-align:left}");
		style.addRule("body { font-family: sans-serif; font-size: " + AppPreferences.getFontSize() + "pt}");

		
		scrollPane = new JScrollPane(textPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(null);
		scrollPane.getViewport().setBorder(null);
		scrollPane.getViewport().setBackground(Color.white);
		scrollPane.getVerticalScrollBar().addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				
				boolean lock = (scrollPane.getSize().height + scrollPane.getVerticalScrollBar().getValue()) < scrollPane.getVerticalScrollBar().getMaximum();

				// The user has manually scrolled the scrollbar, Scroll lock time baby !
				MapTool.getFrame().getCommandPanel().getScrollLockButton().setSelected(lock);
			}
		});
		
		add(scrollPane);
		clearMessages();
		
		MapTool.getSoundManager().registerSoundEvent(SND_MESSAGE_RECEIVED, MapTool.getSoundManager().getRegisteredSound("Clink"));
	}
	
	public void refreshRenderer() {
		repaint();
	}

	public String getMessagesText() {
		
		return textPane.getText();
	}
	
	public void clearMessages() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				textPane.setText("<html><body id=\"body\"></body></html>");
				
			}
		});
	}
	
	public void addMessage(final TextMessage message) {
		
		if (!message.getSource().equals(MapTool.getPlayer().getName())) {
			MapTool.playSound(SND_MESSAGE_RECEIVED);
		}
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				
				String text = "<span>"+message.getMessage()+"</span>";
				text = text.replaceAll("\\[roll\\s*([^\\]]*)]", "&#171;$1&#187;");
				System.out.println(text);
				
				Element element = document.getElement("body");
				try {
					document.insertBeforeEnd(element, text);
				} catch (IOException ioe) {
					ioe.printStackTrace();
				} catch (BadLocationException ble) {
					ble.printStackTrace();
				}
				
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						if (!MapTool.getFrame().getCommandPanel().getScrollLockButton().isSelected()) {
							Rectangle rowBounds = new Rectangle(0, textPane.getSize().height, 1, 1);
							textPane.scrollRectToVisible(rowBounds);
						}
					}
				});
			}
		});
	}

}
