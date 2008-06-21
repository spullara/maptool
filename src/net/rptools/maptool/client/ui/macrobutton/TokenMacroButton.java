/* The MIT License
 *
 * Copyright (c) 2008 Gokhan Ozcan
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
package net.rptools.maptool.client.ui.macrobutton;

import javax.swing.JButton;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import net.rptools.maptool.client.MapTool;

/**
 * Macro buttons that are generated according to the macros of a
 * impersonated token. These are not saved and they cant be hotkeyed.
 */
public class TokenMacroButton extends JButton {
	private String macro;

	public TokenMacroButton(String label, String macro) {
		super(label);
		addMouseListener(new MouseHandler());
		this.macro = macro;
	}

	private class MouseHandler extends MouseAdapter	{
		@Override
		public void mousePressed(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e)) {
				if (macro != null) {
					JTextPane commandArea = MapTool.getFrame().getCommandPanel().getCommandTextArea();
					commandArea.setText(macro);
					MapTool.getFrame().getCommandPanel().commitCommand();
					commandArea.requestFocusInWindow();
				}
			}
		}
	}
}
