/* The MIT License
 *
 * Copyright (c) 2008 Gokhan Ozcan
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation files
 * (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
		if (SwingUtilities.isLeftMouseButton(e) && !SwingUtil.isShiftDown(e)) {
			executeButton();
		} else if (SwingUtilities.isLeftMouseButton(e) && SwingUtil.isShiftDown(e)) {
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
