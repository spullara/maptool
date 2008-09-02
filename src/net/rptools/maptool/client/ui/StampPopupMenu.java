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
package net.rptools.maptool.client.ui;

import java.util.Set;

import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import net.rptools.maptool.client.AppActions;
import net.rptools.maptool.client.AppUtil;
import net.rptools.maptool.client.ui.AbstractTokenPopupMenu.SaveAction;
import net.rptools.maptool.client.ui.AbstractTokenPopupMenu.ShowHandoutAction;
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
		add(createFlipMenu());
		add(createSizeMenu());
		add(createArrangeMenu());
		if (getTokenUnderMouse().getCharsheetImage() != null && AppUtil.playerOwns(getTokenUnderMouse())) {
			add(new ShowHandoutAction());
		}

		add(new JSeparator());
		addOwnedItem(createLightSourceMenu());
		add(new JSeparator());

		addToggledItem(new SnapToGridAction(tokenUnderMouse.isSnapToGrid(), renderer), tokenUnderMouse.isSnapToGrid());
		addToggledGMItem(new VisibilityAction(), tokenUnderMouse.isVisible());
		add(createChangeToMenu(Zone.Layer.TOKEN, Zone.Layer.GM, Zone.Layer.OBJECT, Zone.Layer.BACKGROUND));
		
		add(new JSeparator());

		add(AppActions.CUT_TOKENS);
		add(AppActions.COPY_TOKENS);
		add(new JMenuItem(new DeleteAction()));

		add(new JSeparator());

		add(new ShowPropertiesDialogAction());
		add(new SaveAction());
	}
}
