package net.rptools.maptool.client.ui.macrobuttonpanel;

import java.awt.FlowLayout;
import java.util.List;

import net.rptools.maptool.client.swing.ScrollableFlowPanel;
import net.rptools.maptool.client.ui.MacroButtonHotKeyManager;
import net.rptools.maptool.client.ui.macrobutton.GlobalMacroButton;
import net.rptools.maptool.client.ui.macrobutton.MacroButtonPrefs;
import net.rptools.maptool.model.MacroButtonProperties;

public class GlobalTab extends ScrollableFlowPanel {
	public GlobalTab() {
		super(FlowLayout.LEFT);
		init();
	}

	private void init() {
		List<MacroButtonProperties> properties = MacroButtonPrefs.getButtonProperties();
		for (MacroButtonProperties prop : properties) {
			add(new GlobalMacroButton(prop));
		}
		doLayout();
	}

	public void addButton() {
		//TODO: can be moved to constructor
		final MacroButtonProperties properties = new MacroButtonProperties(MacroButtonPrefs.getNextIndex());
		add(new GlobalMacroButton(properties));
		doLayout();
	}

	public void addButton(MacroButtonProperties properties) {
		MacroButtonProperties prop = new MacroButtonProperties(MacroButtonPrefs.getNextIndex(),
															   properties.getColorKey(),
															   MacroButtonHotKeyManager.HOTKEYS[0],
															   properties.getCommand(),
															   properties.getLabel(),
															   properties.getAutoExecute(),
															   properties.getIncludeLabel());
		add(new GlobalMacroButton(prop));
		doLayout();
	}

	public void deleteButton(GlobalMacroButton button) {
		remove(button);
	}
}
