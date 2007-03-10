package net.rptools.maptool.client.swing;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JTextPane;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;

public class HTMLPanelRenderer extends JTextPane {

	private CellRendererPane rendererPane = new CellRendererPane();
	private Dimension size;
	private StyleSheet styleSheet;

	public HTMLPanelRenderer() {
		setContentType("text/html");
		setEditable(false);
		setDoubleBuffered(false);
		
		styleSheet = ((HTMLDocument) getDocument()).getStyleSheet();
		styleSheet.addRule("body { font-family: sans-serif; font-size: 11pt}");
		
		rendererPane.add(this);
	}

	public void addStyleSheetRule(String rule) {
		styleSheet.addRule(rule);
	}
	
	public void attach(JComponent c) {
		c.add(rendererPane);
	}

	public void detach(JComponent c) {
		c.remove(rendererPane);
	}

	public Dimension setText(String t, int maxWidth, int maxHeight) {
		setText(t);
		
		setSize(maxWidth, maxHeight);
		size = getPreferredSize();
		
		size.width = Math.min(size.width, maxWidth);
		
		return size;
	}
	
	public void render(Graphics g, int x, int y) {

		rendererPane.paintComponent(g, this, null, x, y, size.width, size.height);
	}
}
