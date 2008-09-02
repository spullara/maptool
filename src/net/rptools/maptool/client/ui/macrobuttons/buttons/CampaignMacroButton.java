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
package net.rptools.maptool.client.ui.macrobuttons.buttons;

import javax.swing.SwingUtilities;
import java.awt.event.MouseEvent;

import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.MacroButtonProperties;
import net.rptools.maptool.model.Token;

/**
 * Campaign macro buttons. They are saved/restored with the campaign.
 */
public class CampaignMacroButton extends AbstractMacroButton {

	public CampaignMacroButton(MacroButtonProperties properties) {
		super(properties);
		addMouseListener(this);
	}

	public void mousePressed(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e) && !SwingUtil.isShiftDown(e) && !properties.getApplyToTokens() ) {
			executeButton();
		} else if (SwingUtilities.isLeftMouseButton(e) && ( SwingUtil.isShiftDown(e) || properties.getApplyToTokens() ) ) {
			for (Token token : MapTool.getFrame().getCurrentZoneRenderer().getSelectedTokensList()) {
				MapTool.getFrame().getCommandPanel().quickCommit("/im " + token.getId());
				executeButton();
				MapTool.getFrame().getCommandPanel().quickCommit("/im");
			}
		} else if (SwingUtilities.isRightMouseButton(e)) {
			new MacroButtonPopupMenu(this, 1).show(this, e.getX(), e.getY());
		}
	}
	
	public void savePreferences() {
		MapTool.getCampaign().setMacroButtonProperty(properties);
	}
}
