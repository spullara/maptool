package net.rptools.maptool.client.ui.macrobuttons.buttons;

import javax.swing.SwingUtilities;
import java.awt.event.MouseEvent;

import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.MacroButtonProperties;
import net.rptools.maptool.model.Token;

/**
 * Macro buttons that aren't tied to a specific campaign. Hence "global" :)
 */
public class GlobalMacroButton extends AbstractMacroButton
{
	private final MacroButtonPrefs prefs;

	public GlobalMacroButton(MacroButtonProperties properties) {
		super(properties);
		addMouseListener(this);
		prefs = new MacroButtonPrefs(this);
	}

	public void mousePressed(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e) && !SwingUtil.isShiftDown(e) && !properties.getApplyToTokens()) {
			executeButton();
		} else if ( SwingUtilities.isLeftMouseButton(e) && ( SwingUtil.isShiftDown(e) || properties.getApplyToTokens() ) ) {
			for (Token token : MapTool.getFrame().getCurrentZoneRenderer().getSelectedTokensList()) {
				MapTool.getFrame().getCommandPanel().quickCommit("/im " + token.getId());
				executeButton();
				MapTool.getFrame().getCommandPanel().quickCommit("/im");
			}
		}else if (SwingUtilities.isRightMouseButton(e)) {
			new MacroButtonPopupMenu(this, 0).show(this, e.getX(), e.getY());
		}
	}
	
	public void savePreferences() {
		prefs.savePreferences();
	}
}
