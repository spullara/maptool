package net.rptools.maptool.client.ui;

import java.util.Set;

import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;

public class StampPopupMenu extends AbstractTokenPopupMenu {

	public StampPopupMenu(Set<GUID> selectedTokenSet, int x, int y,
			ZoneRenderer renderer, Token tokenUnderMouse) {
		super (selectedTokenSet, x, y, renderer, tokenUnderMouse);

		
		add(new SetFacingAction());
		add(new ClearFacingAction());
		add(new StartMoveAction());

		add(new JSeparator());

		addToggledGMItem(new VisibilityAction(), tokenUnderMouse.isVisible());
		add(new ChangeStateAction("light"));
		add(createArrangeMenu());
		
		add(new JSeparator());

		add(createSizeMenu(true));
		addToggledItem(new SnapToGridAction(tokenUnderMouse.isSnapToGrid(), renderer), tokenUnderMouse.isSnapToGrid());

		add(new JSeparator());

		add(new JMenuItem(new DeleteAction()));

		add(new JSeparator());

		add(createChangeToMenu(Zone.Layer.TOKEN, Zone.Layer.OBJECT, Zone.Layer.BACKGROUND));
		add(new ShowPropertiesDialogAction());
	}
}
