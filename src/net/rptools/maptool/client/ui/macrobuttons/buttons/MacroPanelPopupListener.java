package net.rptools.maptool.client.ui.macrobuttons.buttons;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MacroPanelPopupListener extends MouseAdapter {

	private JComponent component;
	private int index;
	
	public MacroPanelPopupListener(JComponent component, int index) {
		this.component = component;
		this.index = index;
	}

	public void mousePressed(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {
			new MacroPanelPopupMenu(component, index).show(component, e.getX(), e.getY());
		}
	}
}
