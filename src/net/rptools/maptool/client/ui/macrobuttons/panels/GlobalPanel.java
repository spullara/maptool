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

import java.util.ArrayList;
import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.macrobuttons.buttongroups.AbstractButtonGroup;
import net.rptools.maptool.client.ui.macrobuttons.buttons.MacroButtonPrefs;
import net.rptools.maptool.model.MacroButtonProperties;

public class GlobalPanel extends AbstractMacroPanel {
	public GlobalPanel() {
		super();
		setPanelClass("GlobalPanel");
		addMouseListener(this);
		init();
	}

	private void init() {
		List<MacroButtonProperties> properties = MacroButtonPrefs.getButtonProperties();
		addArea(properties, "");
	}

	public void reset() {
		clear();
		init();
	}
	
	public static void deleteButtonGroup(String macroGroup) {
		AbstractButtonGroup.clearHotkeys(MapTool.getFrame().getGlobalPanel(), macroGroup);
		List<MacroButtonProperties> finalProps = new ArrayList<MacroButtonProperties>();
		for(MacroButtonProperties nextProp : MacroButtonPrefs.getButtonProperties()) {
			if(!macroGroup.equals(nextProp.getGroup())) {
				finalProps.add(nextProp);
			}
		}
		MacroButtonPrefs.getButtonProperties().clear();
		for(MacroButtonProperties nextProp : finalProps) {
			MacroButtonPrefs.savePreferences(nextProp);
		}
		MapTool.getFrame().getGlobalPanel().reset();
	}
	
	public static void clearPanel() {
		AbstractMacroPanel.clearHotkeys(MapTool.getFrame().getGlobalPanel());
		MacroButtonPrefs.getButtonProperties().clear();
		MapTool.getFrame().getGlobalPanel().reset();
	}
}
