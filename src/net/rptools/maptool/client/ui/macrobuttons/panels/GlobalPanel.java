package net.rptools.maptool.client.ui.macrobuttons.panels;

import java.awt.FlowLayout;
import java.util.Collections;
import java.util.List;

import net.rptools.maptool.client.swing.ScrollableFlowPanel;
import net.rptools.maptool.client.ui.MacroButtonHotKeyManager;
import net.rptools.maptool.client.ui.macrobuttons.buttons.GlobalMacroButton;
import net.rptools.maptool.client.ui.macrobuttons.buttons.MacroButtonPrefs;
import net.rptools.maptool.model.MacroButtonProperties;

public class GlobalPanel extends ScrollableFlowPanel {
	public GlobalPanel() {
		super(FlowLayout.LEFT);
		init();
	}

	private void init() {
		List<MacroButtonProperties> properties = MacroButtonPrefs.getButtonProperties();
		Collections.sort(properties);

		for (MacroButtonProperties prop : properties) {
			add(new GlobalMacroButton(prop));
		}
		//doLayout();
		revalidate();
		repaint();
	}

	public void addButton() {
		//TODO: can be moved to constructor
		final MacroButtonProperties properties = new MacroButtonProperties(MacroButtonPrefs.getNextIndex());
		add(new GlobalMacroButton(properties));
		doLayout();
		revalidate();
		repaint();
	}

	public void addButton(MacroButtonProperties properties) {
		MacroButtonProperties prop = new MacroButtonProperties(MacroButtonPrefs.getNextIndex(),
															   properties.getColorKey(),
															   MacroButtonHotKeyManager.HOTKEYS[0],
															   properties.getCommand(),
															   properties.getLabel(),
															   properties.getSortby(),
															   properties.getAutoExecute(),
															   properties.getIncludeLabel(),
															   properties.getApplyToTokens());
		add(new GlobalMacroButton(prop));
		doLayout();
		revalidate();
		repaint();
	}

	public void deleteButton(GlobalMacroButton button) {
		remove(button);
	}
	
	private void clear() {
		removeAll();
		revalidate();
		repaint();
	}
	
	public void reset() {
		clear();
		init();
	}
}
