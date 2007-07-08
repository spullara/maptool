package net.rptools.maptool.client.ui.commandpanel;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
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
		textPane.addComponentListener(new ComponentListener() {
			public void componentHidden(ComponentEvent e) {}
			public void componentMoved(ComponentEvent e) {}
			public void componentResized(ComponentEvent e) {
				// Jump to the bottom on new text
				if (!MapTool.getFrame().getCommandPanel().getScrollLockButton().isSelected()) {
					Rectangle rowBounds = new Rectangle(0, textPane.getSize().height, 1, 1);
					textPane.scrollRectToVisible(rowBounds);
				}
			}
			public void componentShown(ComponentEvent e) {}
		});
		
		document = (HTMLDocument) textPane.getDocument();
		refreshRenderer();
		
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
		// Create the style
		StyleSheet style = document.getStyleSheet();
		style.addRule("body { font-family: sans-serif; font-size: " + AppPreferences.getFontSize() + "pt}");
		style.addRule("div {margin-bottom: 5px");
//		style.addRule("span.roll {border: 1px black solid");

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
				
				String text = "<div>"+message.getMessage()+"</div>";
				text = text.replaceAll("\\[roll\\s*([^\\]]*)]", "<span class='roll' style='background:#cfcfcf;color:black;border-style:inset'>&nbsp;$1&nbsp;</span>");
				
				Element element = document.getElement("body");
				try {
					document.insertBeforeEnd(element, text);
				} catch (IOException ioe) {
					ioe.printStackTrace();
				} catch (BadLocationException ble) {
					ble.printStackTrace();
				}
			}
		});
	}

}
