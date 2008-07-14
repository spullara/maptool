package net.rptools.maptool.client.ui.macrobuttonpanel;

import java.awt.FlowLayout;
import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.swing.ScrollableFlowPanel;
import net.rptools.maptool.client.ui.MacroButtonHotKeyManager;
import net.rptools.maptool.client.ui.macrobutton.CampaignMacroButton;
import net.rptools.maptool.model.MacroButtonProperties;

public class CampaignTab extends ScrollableFlowPanel{
	public CampaignTab() {
		super(FlowLayout.LEFT);
		init();
	}

	private void init() {
		List<MacroButtonProperties> properties = MapTool.getCampaign().getMacroButtonPropertiesArray();

		for (MacroButtonProperties prop : properties) {
			add(new CampaignMacroButton(prop));
		}
		doLayout();
	}
	
	public void addButton() {
		//TODO: can be moved to constructor
		final MacroButtonProperties properties = new MacroButtonProperties(MapTool.getCampaign().getMacroButtonNextIndex());
		MapTool.getCampaign().addMacroButtonProperty(properties);
		add(new CampaignMacroButton(properties));
		doLayout();
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
	}

	public void deleteButton(CampaignMacroButton button) {
		remove(button);
	}
}
