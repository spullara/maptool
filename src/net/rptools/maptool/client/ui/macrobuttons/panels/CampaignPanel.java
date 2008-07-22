package net.rptools.maptool.client.ui.macrobuttons.panels;

import java.awt.FlowLayout;
import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.swing.ScrollableFlowPanel;
import net.rptools.maptool.client.ui.MacroButtonHotKeyManager;
import net.rptools.maptool.client.ui.macrobuttons.buttons.CampaignMacroButton;
import net.rptools.maptool.model.MacroButtonProperties;

public class CampaignPanel extends ScrollableFlowPanel{
	public CampaignPanel() {
		super(FlowLayout.LEFT);
		init();
	}

	private void init() {
		List<MacroButtonProperties> properties = MapTool.getCampaign().getMacroButtonPropertiesArray();

		for (MacroButtonProperties prop : properties) {
			add(new CampaignMacroButton(prop));
		}
		doLayout();
		revalidate();
		repaint();
	}
	
	public void addButton() {
		//TODO: can be moved to constructor
		final MacroButtonProperties properties = new MacroButtonProperties(MapTool.getCampaign().getMacroButtonNextIndex());
		MapTool.getCampaign().addMacroButtonProperty(properties);
		add(new CampaignMacroButton(properties));
		doLayout();
		revalidate();
		repaint();
	}
	
	public void addButton(MacroButtonProperties properties) {
		MacroButtonProperties prop = new MacroButtonProperties(MapTool.getCampaign().getMacroButtonNextIndex(),
															   properties.getColorKey(),
															   MacroButtonHotKeyManager.HOTKEYS[0],
															   properties.getCommand(),
															   properties.getLabel(),
															   properties.getAutoExecute(),
															   properties.getIncludeLabel());
		MapTool.getCampaign().addMacroButtonProperty(properties);
		add(new CampaignMacroButton(prop));
		doLayout();
		revalidate();
		repaint();
	}

	public void deleteButton(CampaignMacroButton button) {
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
