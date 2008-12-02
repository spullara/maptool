package net.rptools.maptool.client.ui.htmlframe;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import net.rptools.maptool.client.swing.MessagePanelEditorKit;

@SuppressWarnings("serial")
public class HTMLPanel extends JPanel {

	private final HTMLPane pane = new HTMLPane();
	private final JPanel closePanel = new JPanel();

	/**
	 * Creates a new HTMLPanel.
	 * @param container The container that will hold the HTML panel.
	 * @param input If the panel is in input mode.
	 * @param scrollBar Should panel have scrollbars or not.
	 */
	HTMLPanel(final HTMLPanelContainer container, boolean input, boolean scrollBar) {
		setLayout(new BorderLayout());
		
		JButton closeButton = new JButton("Close");
		closeButton.setActionCommand("Close");
		closeButton.addActionListener(container);
		closePanel.setLayout(new BoxLayout(closePanel, BoxLayout.LINE_AXIS));
		closePanel.add(Box.createHorizontalGlue());
		closePanel.add(closeButton);
		closePanel.add(Box.createHorizontalGlue());
		
		if (scrollBar) {
			add(new JScrollPane(pane), BorderLayout.CENTER);
		} else {
			add(pane, BorderLayout.CENTER);
		}
		updateContents("", input);
		
		// ESCAPE closes the window
		pane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
		pane.getActionMap().put("cancel", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				container.closeRequest();
			}});
		
		// Add an action listener so we can get notified about form events.
		pane.addActionListener(container);
	}

	/**
	 * Update the contents of the panel.
	 * @param html The HTML to display.
	 * @param input If the panel is in input mode.
	 */
	public void updateContents(final String html, boolean input) {
		if (input) {
			remove(closePanel); 
		} else {
			add(closePanel, BorderLayout.SOUTH);
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				((MessagePanelEditorKit)pane.getEditorKit()).flush();
				pane.setText(html);
			}
		});
	}

	/**
	 * Flushes any caching for the panel.
	 */
	public void flush() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				((MessagePanelEditorKit)pane.getEditorKit()).flush();
			}
		});		
	}
	
	/**
	 * Updates if this panel is an input panel or not.
	 * @param input is this panel an input panel or not.
	 */
	void updateContents(boolean input) {
		if (input) {
			remove(closePanel); 
		} else {
			add(closePanel, BorderLayout.SOUTH);
		}
		revalidate();
	}
	
}
