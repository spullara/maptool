package net.rptools.maptool.client.ui.macrobuttonpanel;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import java.awt.event.ActionEvent;

import net.rptools.maptool.client.MapTool;

public class MacroPanelPopupMenu extends JPopupMenu{
	
	//private final JComponent parent;
	private int index;
	
	//TODO: replace index with Tab.TABNAME.index
	public MacroPanelPopupMenu(JComponent parent, int index) {
		//this.parent = parent;
		this.index = index;
		add(new AddNewButtonAction());
	}

	private class AddNewButtonAction extends AbstractAction {
		public AddNewButtonAction() {
			putValue(Action.NAME, "New Button");
		}

		public void actionPerformed(ActionEvent event) {
			// add a new global macro button
			if (index == 0) {
				MapTool.getFrame().getMacroTabbedPane().addGlobalMacroButton();
			} else if (index == 1) {
				MapTool.getFrame().getMacroTabbedPane().addCampaignMacroButton();
			}
		}
	}
}
