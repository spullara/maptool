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
package net.rptools.maptool.client.ui.macrobuttons.buttons;

import javax.swing.JButton;
import javax.swing.SwingUtilities;
import java.awt.Cursor;
import java.awt.Insets;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.commandpanel.CommandPanel;
import net.rptools.maptool.model.Token;

/**
 * Macro buttons that are generated according to the macros of a
 * token. These are not saved and they cant be hotkeyed.
 */
public class TokenMacroButton extends JButton {
	
	private String label;
	private String command;
	private String identity;
	
	private static final Insets buttonInsets = new Insets(1, 1, 1, 1);
	
	private Token token;
	private List<Token> tokenList;

	private DragSource dragSource;
	private DragGestureListener dgListener;
	private DragSourceListener dsListener;
	
	// impersonate tab buttons
	public TokenMacroButton(Token token, String label, String command) {
		super(label);
		addMouseListener(new MouseHandler());
		this.command = command;
		setMargin(buttonInsets);
		makeDraggable(DragSource.DefaultCopyDrop);
	}
	
	// selection tab token buttons
	public TokenMacroButton(Token token, String identity, String label, String command) {
		super(label);
		this.label = label;
		this.command = command;
		this.identity = identity;
		this.token = token;
		addMouseListener(new MouseHandler());
		setMargin(buttonInsets);
		makeDraggable(DragSource.DefaultCopyDrop);
	}

	// selection tab common macro buttons
	public TokenMacroButton(List<Token> tokenList, String label) {
		super(label);
		this.label = label;
		this.tokenList = tokenList;
		addMouseListener(new MouseHandler());
		setMargin(buttonInsets);
		makeDraggable(DragSource.DefaultCopyNoDrop);
	}

	private void makeDraggable(Cursor cursor) {
		dragSource = DragSource.getDefaultDragSource();
		dgListener = new DGListener(cursor);
		dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY, dgListener);
		dsListener = new DSListener();
	}
	
	@Override
	public String toString() {
		return "MacroButton-" + getText();
	}
	
	private class MouseHandler extends MouseAdapter {
		public void mouseReleased(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e)) {
				CommandPanel commandPanel = MapTool.getFrame().getCommandPanel();
				
				if (tokenList != null) {
					for (Token token : tokenList) {
						String command = "/im " + token.getId() + ":" + token.getMacro(label);
						commandPanel.quickCommit(command);
					}
				} else {
					if (identity != null) {
						String c = "/im " + identity + ":" + command;
						commandPanel.quickCommit(c);
					} else {
						commandPanel.quickCommit(command);
					}
				}
			} else if (SwingUtilities.isRightMouseButton(e)) {
				if (tokenList == null) {
					new TokenButtonPopupMenu(TokenMacroButton.this, 0).show(TokenMacroButton.this, e.getX(), e.getY());
				} else {
					new TokenButtonPopupMenu(TokenMacroButton.this, 1).show(TokenMacroButton.this, e.getX(), e.getY());
				}
			}
		}
	}
	
	private class DGListener implements DragGestureListener {

		final Cursor cursor;
		
		public DGListener(Cursor cursor) {
			this.cursor = cursor;
		}

		public void dragGestureRecognized(DragGestureEvent dge) {
			Transferable t = new TransferableMacroButton(TokenMacroButton.this);
			dge.startDrag(cursor, t, dsListener);
		}
	}

	private class DSListener implements DragSourceListener {
		
		public void dragEnter(DragSourceDragEvent event) {
			//System.out.println("TMB: drag enter");
			//DragSourceContext context = event.getDragSourceContext();
			//context.getComponent()
		}

		public void dragOver(DragSourceDragEvent event) {
			//System.out.println("TMB: drag over");
		}

		public void dropActionChanged(DragSourceDragEvent event) {
			//System.out.println("TMB: drop action changed");
		}

		public void dragExit(DragSourceEvent event) {
			//System.out.println("TMB: drag exit");
		}

		public void dragDropEnd(DragSourceDropEvent event) {
			//System.out.println("TMB: drag drop end");
			MapTool.getFrame().updateSelectionPanel();
		}
	}
	
	public String getMacro() {
		return label;
	}
	
	public String getCommand() {
		return command;
	}
	
	public Token getToken() {
		return token;
	}
	
	public List<Token> getTokenList() {
		return tokenList;
	}
}