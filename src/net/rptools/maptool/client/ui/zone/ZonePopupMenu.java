package net.rptools.maptool.client.ui.zone;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.Zone;

public class ZonePopupMenu extends JPopupMenu {

	private Zone zone;
	
	public ZonePopupMenu(Zone zone) {
		super("Zone");
		
		this.zone = zone;

		Action action = null;
		if (zone.isVisible()) {
			action = new AbstractAction() {
				{
					putValue(NAME, "KILL VISIBILITY");
				}
				public void actionPerformed(ActionEvent e) {
					ZonePopupMenu.this.zone.setVisible(false);
					MapTool.serverCommand().setZoneVisibility(ZonePopupMenu.this.zone.getId(), false);
				}
			};
		} else {
			action = new AbstractAction() {
				{
					putValue(NAME, "see it");
				}
				public void actionPerformed(ActionEvent e) {
					
					ZonePopupMenu.this.zone.setVisible(true);
					MapTool.serverCommand().setZoneVisibility(ZonePopupMenu.this.zone.getId(), true);
				}
			};
		}
		add(new JMenuItem(action));
	}

	
	
}
