package net.rptools.maptool.client.ui.macrobutton;

import javax.swing.SwingUtilities;
import java.awt.event.MouseEvent;

import net.rptools.maptool.model.MacroButtonProperties;
import net.rptools.maptool.client.ui.macrobuttonpanel.MacroButtonPopupMenu;

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
		if (SwingUtilities.isLeftMouseButton(e)) {
			executeButton();
		}
		if (SwingUtilities.isRightMouseButton(e)) {
			new MacroButtonPopupMenu(this, 0).show(this, e.getX(), e.getY());
		}
	}
	
	public void savePreferences() {
		prefs.savePreferences();
	}
}
