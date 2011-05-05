/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package net.rptools.maptool.client.swing;

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import net.rptools.lib.swing.SwingUtil;

public class GenericDialog extends JDialog {
	private final JPanel panel;
	private boolean hasPositionedItself;

	public GenericDialog(String title, Frame parent, JPanel panel) {
		this(title, parent, panel, true);
	}

	public GenericDialog(String title, Frame parent, JPanel panel, boolean modal) {
		super(parent, title, modal);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		this.panel = panel;
		setLayout(new GridLayout());

		add(this.panel);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				closeDialog();
			}
		});
		// ESCAPE cancels the window without commiting
		this.panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
		this.panel.getActionMap().put("cancel", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				closeDialog();
			}
		});
	}

	public void closeDialog() {
		setVisible(false);
		dispose();
	}

	protected void positionInitialView() {
		SwingUtil.centerOver(this, getOwner());
	}

	public void showDialog() {
		// We want to center over our parent, but only the first time.
		// If this dialog is reused, we want it to show up where it was last.
		if (!hasPositionedItself) {
			pack();
			positionInitialView();
			hasPositionedItself = true;
		}
		setVisible(true);
	}
}
