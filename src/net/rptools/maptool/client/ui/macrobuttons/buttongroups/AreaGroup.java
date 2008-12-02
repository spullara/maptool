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
package net.rptools.maptool.client.ui.macrobuttons.buttongroups;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.dnd.DropTargetDropEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import net.rptools.maptool.client.ui.macrobuttons.panels.AbstractMacroPanel;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.MacroButtonProperties;

public class AreaGroup extends AbstractButtonGroup {

	// constructor for creating an area group in the campaign/global panels
	public AreaGroup(List<MacroButtonProperties> propertiesList, String panelLabel, AbstractMacroPanel panel) {
		setPropertiesList(propertiesList);
		setPanel(panel);
		setPanelClass(panel.getPanelClass());
		setGroupClass("AreaGroup");
		setGroupLabel(panelLabel);
		setTokenId(panel.getToken());
		addMouseListener(this);
		drawArea();
	}

	// constructor for creating an area group for the impersonate/selection panels
	public AreaGroup(GUID tokenId, AbstractMacroPanel panel) {
		setTokenId(tokenId);
		setPropertiesList(getToken().getMacroList(true));
		setPanel(panel);
		setPanelClass(panel.getPanelClass());
		setGroupClass("AreaGroup");
		setGroupLabel(getTokenName(getToken()));
		addMouseListener(this);
		drawArea();
	}

	// constructor for creating an area spacer, used to take up space where an area label would be
	public AreaGroup(int height, AbstractMacroPanel panel){
		setSpacerHeight(height);
		setPanel(panel);
		setPanelClass(panel.getPanelClass());
		setOpaque(false);
//		addMouseListener(this);  don't use; the label has its own 
	}
	
	public void drawArea(){
		if (getToken() == null || getGroupLabel().equals("")){
			// don't put an extra border around the campaign/global panels, or if there is no label
		} else {
			ThumbnailedBorder border = createBorder(getGroupLabel());
			setBorder(border);
			add(new AreaGroup(12, getPanel())); // spacer
		}

		String lastGroup = "akjaA#$Qq4jakjj#%455jkkajDAJFAJ"; // random string
		String currentGroup = "";

		List<MacroButtonProperties> propertiesList = getPropertiesList();
		List<MacroButtonProperties> groupList = new ArrayList<MacroButtonProperties>();
		Collections.sort(propertiesList);

		if (propertiesList.isEmpty()){
			add(new ButtonGroup(propertiesList, "", getPanel(), getTokenId()));
		} else {
			// build separate button groups for each user-defined group
			for (MacroButtonProperties prop : propertiesList) {
				currentGroup = prop.getGroup();
				if ( !groupList.isEmpty() && !lastGroup.equalsIgnoreCase(currentGroup)){
					add(new ButtonGroup(groupList, lastGroup, getPanel(), getTokenId()));
					groupList.clear();
				}
				lastGroup = currentGroup;
				groupList.add(prop);
			}
			if (!groupList.isEmpty()){
				add(new ButtonGroup(groupList, lastGroup, getPanel(), getTokenId()));
				groupList.clear();
			}
		}

		setLayout(new FlowLayout(FlowLayout.LEFT));
		revalidate();
		repaint();
	}

	public void drop(DropTargetDropEvent event) {
		//System.out.println("BG: drop!");
		event.rejectDrop();  // don't accept drops in an area group, it should be in the button group
		event.dropComplete(true);
	}

	public Insets getInsets() {
		return new Insets(0,1,3,0);
	}

	@Override
	public Dimension getPreferredSize() {
		FlowLayout layout = (FlowLayout) getLayout();
		Insets insets = getInsets();
		// This isn't exact, but hopefully it's close enough
		int availableWidth = getPanel().getAvailableWidth() - insets.left - insets.right;
		int height = insets.top + insets.bottom + layout.getVgap();
		int rowHeight = 0;
		int rowWidth = insets.left + layout.getHgap() + insets.right;
		for (Component c : getComponents()) {
			Dimension cSize = c.getPreferredSize();
			if (rowWidth + cSize.width + layout.getHgap() - 5 > availableWidth && rowWidth > 0) {
				height += rowHeight + layout.getVgap(); 
				rowHeight = 0;
				rowWidth = insets.left + layout.getHgap() + insets.right;
			}
			rowWidth += cSize.width + layout.getHgap();
			rowHeight = Math.max(cSize.height, rowHeight);
		}
		height += rowHeight;
		Dimension prefSize = new Dimension(availableWidth, height);
		return prefSize;
	}

}
