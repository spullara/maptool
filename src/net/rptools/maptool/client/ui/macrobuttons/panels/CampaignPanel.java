/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package net.rptools.maptool.client.ui.macrobuttons.panels;

import java.awt.FlowLayout;
import java.util.Collections;
import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.swing.ScrollableFlowPanel;
import net.rptools.maptool.client.ui.MacroButtonDialog;
import net.rptools.maptool.client.ui.MacroButtonHotKeyManager;
import net.rptools.maptool.client.ui.macrobuttons.buttons.AbstractMacroButton;
import net.rptools.maptool.client.ui.macrobuttons.buttons.CampaignMacroButton;
import net.rptools.maptool.model.MacroButtonProperties;

public class CampaignPanel extends ScrollableFlowPanel{
	public CampaignPanel() {
		super(FlowLayout.LEFT);
		init();
	}

	private void init() {
		List<MacroButtonProperties> properties = MapTool.getCampaign().getMacroButtonPropertiesArray();
		Collections.sort(properties);

		for (MacroButtonProperties prop : properties) {
			add(new CampaignMacroButton(prop));
		}
		doLayout();
		revalidate();
		repaint();
	}
	
	public void addButton() {
		AbstractMacroButton button = new CampaignMacroButton();
		MacroButtonDialog dialog = new MacroButtonDialog();
		dialog.show(button);
		if (dialog.wasUpdated()) { 
			button.savePreferences();
			MapTool.getCampaign().addMacroButtonProperty(button.getProperties());
			add(button);
			doLayout();
			revalidate();
			repaint();
		}
	}
	
	public void addButton(MacroButtonProperties properties) {
		MacroButtonProperties prop = new MacroButtonProperties(MapTool.getCampaign().getMacroButtonNextIndex(),
															   properties.getColorKey(),
															   MacroButtonHotKeyManager.HOTKEYS[0],
															   properties.getCommand(),
															   properties.getLabel(),
															   properties.getSortby(),
															   properties.getAutoExecute(),
															   properties.getIncludeLabel(),
															   properties.getApplyToTokens());
		MapTool.getCampaign().addMacroButtonProperty(prop); //fixed bug where duplicated campaign buttons weren't saved properly; it was re-adding the original instead of the new one.
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
