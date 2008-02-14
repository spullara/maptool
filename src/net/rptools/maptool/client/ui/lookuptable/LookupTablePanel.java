/* The MIT License
 * 
 * Copyright (c) 2005 David Rice, Trevor Croft
 * 
 * Permission is hereby granted, free of charge, to any person 
 * obtaining a copy of this software and associated documentation files 
 * (the "Software"), to deal in the Software without restriction, 
 * including without limitation the rights to use, copy, modify, merge, 
 * publish, distribute, sublicense, and/or sell copies of the Software, 
 * and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be 
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS 
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN 
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
 * SOFTWARE.
 */
package net.rptools.maptool.client.ui.lookuptable;

import javax.swing.JButton;

import net.rptools.lib.swing.ImagePanel;
import net.rptools.maptool.client.swing.AbeillePanel;

public class LookupTablePanel extends AbeillePanel {

	private ImagePanel imagePanel;
	
	public LookupTablePanel() {
		super("net/rptools/maptool/client/ui/forms/lookuptablePanel.jfrm");
		
		initImagePanel();
	}

	private void initImagePanel() {
		imagePanel = new ImagePanel();
	}
	
	public JButton getNewButton() {
		return (JButton) getComponent("newButton");
	}
	
	public JButton getEditButton() {
		return (JButton) getComponent("newButton");
	}
	
	public JButton getDeleteButton() {
		return (JButton) getComponent("newButton");
	}
	
	public JButton getDuplicateButton() {
		return (JButton) getComponent("newButton");
	}
	
	public JButton getRunButton() {
		return (JButton) getComponent("newButton");
	}
	
	public ImagePanel getImagePanel() {
		return imagePanel;
	}
}
