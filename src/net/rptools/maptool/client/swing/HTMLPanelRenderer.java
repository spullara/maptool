/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package net.rptools.maptool.client.swing;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JTextPane;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;

import net.rptools.lib.MD5Key;
import net.rptools.lib.image.ImageUtil;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.util.ImageManager;

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
		
		Document document = (HTMLDocument) getDocument();
		
		// Use a little bit of black magic to get our images to display correctly
		// TODO: Need a way to flush this cache
		HTMLPanelImageCache imageCache = new HTMLPanelImageCache();
		document.putProperty("imageCache", imageCache);
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
