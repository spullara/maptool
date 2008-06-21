package net.rptools.maptool.client.ui.macrobuttonpanel;

import javax.swing.*;
import java.awt.event.ActionEvent;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.MacroButtonDialog;
import net.rptools.maptool.client.ui.MacroButtonHotKeyManager;
import net.rptools.maptool.client.ui.macrobutton.AbstractMacroButton;
import net.rptools.maptool.client.ui.macrobutton.CampaignMacroButton;
import net.rptools.maptool.client.ui.macrobutton.GlobalMacroButton;
import net.rptools.maptool.client.ui.macrobutton.MacroButtonPrefs;

public class MacroButtonPopupMenu extends JPopupMenu{
	
	private final AbstractMacroButton button;
	private int index;
	
	public MacroButtonPopupMenu(AbstractMacroButton parent, int index) {
		this.button = parent;
		this.index = index;
		add(new EditButtonAction());
		add(new JSeparator());
		add(new AddNewButtonAction());
		add(new DuplicateButtonAction());
		add(new JSeparator());
		add(new ResetButtonAction());
		add(new DeleteButtonAction());
	}

	private class AddNewButtonAction extends AbstractAction {
		public AddNewButtonAction() {
			putValue(Action.NAME, "New");
		}

		public void actionPerformed(ActionEvent event) {
			// TODO: refactor to put tab index from Tab enum
			if (index == 0) {
				MapTool.getFrame().getMacroTabbedPane().addGlobalMacroButton();
			} else if (index == 1) {
				MapTool.getFrame().getMacroTabbedPane().addCampaignMacroButton();
			}
		}
	}

	private class EditButtonAction extends AbstractAction {
		public EditButtonAction() {
			putValue(Action.NAME, "Edit");
		}

		public void actionPerformed(ActionEvent event) {
			new MacroButtonDialog().show(button);
			button.savePreferences();
		}
	}

	private class DeleteButtonAction extends AbstractAction {
		public DeleteButtonAction() {
			putValue(Action.NAME, "Delete");
		}

		public void actionPerformed(ActionEvent event) {
			// remove the hot key or the hot key will remain and you'll get an exception later
			// when you want to assign that hotkey to another button.
			button.getHotKeyManager().assignKeyStroke(MacroButtonHotKeyManager.HOTKEYS[0]);
			
			if (index == 0) {
				MacroButtonPrefs.delete((GlobalMacroButton) button);
				MapTool.getFrame().getMacroTabbedPane().deleteGlobalMacroButton((GlobalMacroButton) button);
			} else if (index == 1) {
				MapTool.getCampaign().deleteMacroButton(button.getProperties());
				MapTool.getFrame().getMacroTabbedPane().deleteCampaignMacroButton((CampaignMacroButton) button);
			}
		}
	}

	private class DuplicateButtonAction extends AbstractAction {
		public DuplicateButtonAction() {
			putValue(Action.NAME, "Duplicate");
		}

		public void actionPerformed(ActionEvent event) {
			if (index == 0) {
				MapTool.getFrame().getMacroTabbedPane().addGlobalMacroButton(button.getProperties());
			} else if (index == 1) {
				MapTool.getFrame().getMacroTabbedPane().addCampaignMacroButton(button.getProperties());
			}
		}
	}

	private class ResetButtonAction extends AbstractAction {
		public ResetButtonAction() {
			putValue(Action.NAME, "Reset");
		}

		public void actionPerformed(ActionEvent event) {
			button.reset();
			button.savePreferences();
		}
	}
}