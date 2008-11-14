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

import javax.swing.ImageIcon;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import net.rptools.maptool.client.AppStyle;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.MapToolFrame.MTFrame;
import net.rptools.maptool.client.ui.macrobuttons.buttongroups.ButtonGroup;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.MacroButtonProperties;

public class SelectionPanel extends AbstractMacroPanel {
	
	private List<Token> tokenList = null;
	
	public SelectionPanel() {
		//TODO: refactoring reminder
		setPanelClass("SelectionPanel");
		init(new ArrayList<Token>());  // when initially loading MT, the CurrentZoneRenderer isn't ready yet; just send an empty list
	}
	
	public void init(){
		init(MapTool.getFrame().getCurrentZoneRenderer().getSelectedTokensList());
	}

	public void init(List<Token> selectedTokenList) {
		// add the selection panel controls first
		add(new MenuButtonsPanel());

		// draw common group only when there is more than one token selected
		if (selectedTokenList.size() > 1) {
			add(new ButtonGroup(selectedTokenList, getCommonButtons(selectedTokenList), this));
		}
		for (Token token : selectedTokenList) {
			addArea(token.getId());
		}

		if (selectedTokenList.size() == 1) {
			// if only one token selected, show its image as tab icon
			MapTool.getFrame().getFrame(MTFrame.SELECTION).setFrameIcon(selectedTokenList.get(0).getIcon(16, 16));
		}
		MapTool.getEventDispatcher().addListener(this, MapTool.ZoneEvent.Activated);
	}

	private List<MacroButtonProperties> getCommonButtons(List<Token> tokenList) {
		// get the common macros of the tokens based on the macros' hash code (excluding the index)
		// hashcode => MacroButtonProperties list
		Map<Integer, List<MacroButtonProperties>> encounteredMacros = new HashMap<Integer, List<MacroButtonProperties>>();
		for (Token token : tokenList) {
			for (MacroButtonProperties macro : token.getMacroList(true)) {
				int hash = macro.hashCodeForComparison();
				List<MacroButtonProperties> l = encounteredMacros.get(hash);
				if (l == null) {
					l = new ArrayList<MacroButtonProperties>();
					encounteredMacros.put(hash, l);
				}
				l.add(macro);
			}
		}
		TreeSet<Integer> hashcodes = new TreeSet<Integer>();
		hashcodes.addAll(encounteredMacros.keySet());
		// create the list to hold one of each common macros
		// since we are only interested in finding common macros between tokens
		// we skip the map keys which have only 1 item in the arraylist
		// so we skip those like "Attack" => ["Elf"]
		List<MacroButtonProperties> commonMacros = new ArrayList<MacroButtonProperties>();
		for (int hash : hashcodes) {
			List<MacroButtonProperties> l = encounteredMacros.get(hash);
			if (l.size() > 1) {
				commonMacros.add(l.get(0));
			}
		}
		Collections.sort(commonMacros);  // sort by the properties' compare functionality, so they are grouped appropriately
		return commonMacros;
	}

	protected void clear() {
		// reset the tab icon
		MapTool.getFrame().getFrame(MTFrame.SELECTION).setFrameIcon(new ImageIcon(AppStyle.selectionPanelImage));
		removeAll();
		revalidate();
		repaint();
	}
	
	public void reset() {
		clear();
		init();
	}
}
