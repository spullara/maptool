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
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.macrobuttonpanel.ButtonGroup;

/**
 * Macro buttons that are generated according to the macros of a
 * token. These are not saved and they cant be hotkeyed.
 */
public class TokenMacroButton extends JButton {
	
	private String macro;
	private String identity;
	private List<ButtonGroup.Tuple> tuples;
	private static final Insets buttonInsets = new Insets(2, 2, 2, 2); 

	//TODO: this class can be made better if it references a token object itself
	// this.token = token
	//TODO: adding TokenChangedListener and TokenChangedEvent and TokenDeletedEvent
	// would make us even more happy.
	public TokenMacroButton(String label, String macro) {
		super(label);
		addMouseListener(new MouseHandler());
		this.macro = macro;
		setMargin(buttonInsets);
	}

	public TokenMacroButton(String identity, String label, String macro) {
		super(label);
		this.macro = macro;
		this.identity = identity;
		addMouseListener(new MouseHandler());
		setMargin(buttonInsets);
		
	}
	
	public TokenMacroButton(List<ButtonGroup.Tuple> tuples, String label) {
		super(label);
		this.tuples = tuples;
		addMouseListener(new MouseHandler2());
		setMargin(buttonInsets);
	}

	//TODO: combine the mousehandlers
	private class MouseHandler extends MouseAdapter	{
		@Override
		public void mousePressed(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e)) {
				runMacro(identity, macro);
			}
		}
	}
	
	private void runMacro(String identity, String macro) {
		if (macro == null) {
			return;
		}

		JTextPane commandArea = MapTool.getFrame().getCommandPanel().getCommandTextArea();
		if (identity != null) {
			String command = "/im " + identity + ":" + macro;
			commandArea.setText(command);
		} else {
			commandArea.setText(macro);
		}
		MapTool.getFrame().getCommandPanel().commitCommand();
		//commandArea.requestFocusInWindow();
	}

	private class MouseHandler2 extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e)) {
				JTextPane commandArea = MapTool.getFrame().getCommandPanel().getCommandTextArea();
				
				for (ButtonGroup.Tuple tuple : tuples) {
					String command = "/im " + tuple.tokenName + ":" + tuple.macro;
					commandArea.setText(command);
					MapTool.getFrame().getCommandPanel().commitCommand();
				}
			}
		}
	}
}
