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

	private JPanel panel;
	private boolean hasPositionedItself;
	
	public GenericDialog(String title, Frame parent, JPanel panel) {
		super(parent, title, true);
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
		this.panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
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
		// if this this dialog is resused, we want it to show up where it was last
		if (!hasPositionedItself) {
			pack();
			positionInitialView();
			hasPositionedItself = true;
		}
		
		setVisible(true);
	}
}
