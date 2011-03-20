/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package net.rptools.maptool.client.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Area;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;

import net.rptools.maptool.client.AppUtil;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.functions.AbstractTokenAccessorFunction;
import net.rptools.maptool.client.functions.TokenBarFunction;
import net.rptools.maptool.client.ui.token.BarTokenOverlay;
import net.rptools.maptool.client.ui.token.BooleanTokenOverlay;
import net.rptools.maptool.client.ui.zone.FogUtil;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.language.I18N;
import net.rptools.maptool.model.CellPoint;
import net.rptools.maptool.model.ExposedAreaMetaData;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.InitiativeList;
import net.rptools.maptool.model.MacroButtonProperties;
import net.rptools.maptool.model.Path;
import net.rptools.maptool.model.Player;
import net.rptools.maptool.model.Player.Role;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.ZonePoint;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class TokenPopupMenu extends AbstractTokenPopupMenu {
	// TODO: This is temporary -- it should be changed to use {@link MapToolUtil#getColorNames()}
	// @formatter:off
    private static final Object[][] COLOR_ARRAY = new Object[][] {
	    { "Black", Color.black, Color.white },
	    { "Green", Color.green, Color.black },
	    { "Yellow", Color.yellow, Color.black },
	    { "Orange", new Color(255, 156, 0), Color.black }, // default orange is too light
	    { "Red", Color.red, Color.black },
	    { "Blue", Color.blue, Color.black },
	    { "Cyan", Color.cyan, Color.black },
	    { "Dark Gray", Color.darkGray, Color.black },
	    { "Magenta", Color.magenta, Color.black },
	    { "Pink", Color.pink, Color.black },
	    { "White", Color.white, Color.black }
	};
	// @formatter:on

	public TokenPopupMenu(Set<GUID> selectedTokenSet, int x, int y, ZoneRenderer renderer, Token tokenUnderMouse) {
		super(selectedTokenSet, x, y, renderer, tokenUnderMouse);

		add(new SetFacingAction());
		add(new ClearFacingAction());
		add(new StartMoveAction());
		addOwnedItem(new ImpersonateAction());
		addOwnedItem(createSizeMenu());
		addOwnedItem(createMacroMenu());
		addOwnedItem(createSpeechMenu());
		addOwnedItem(createStateMenu());
		addOwnedItem(createBarMenu());
		addOwnedItem(createInitiativeMenu());
		if (MapTool.getFrame().getInitiativePanel().hasOwnerPermission(tokenUnderMouse))
			add(new ChangeInitiativeState("initiative.menu.addToInitiative"));
		addOwnedItem(createFlipMenu());
		if (getTokenUnderMouse().getCharsheetImage() != null && AppUtil.playerOwns(getTokenUnderMouse())) {
			add(new ShowHandoutAction());
		}
		add(createHaloMenu());
		addOwnedItem(createArrangeMenu());
		addGMItem(createChangeToMenu(Zone.Layer.GM, Zone.Layer.OBJECT, Zone.Layer.BACKGROUND));

		add(new JSeparator());

		/*
		 * This adds the expose menu to token right click when the player is GM and the server setting is set to use
		 * individual FOW
		 */
		if (MapTool.getPlayer().isGM() && MapTool.getServerPolicy().isUseIndividualFOW()) {
			add(createExposedFOWMenu());
		}
		if (MapTool.getPlayer().isGM() || MapTool.getServerPolicy().getPlayersCanRevealVision()) {
			add(createExposeMenu());
			// if (MapTool.getPlayer().isGM()) {
			// addGMItem(createVisionMenu());
			// }
			// add(new JSeparator());
		}
		addOwnedItem(createLightSourceMenu());

		add(new JSeparator());

		addToggledItem(new ShowPathsAction(), renderer.isPathShowing(tokenUnderMouse));
		addToggledItem(new SnapToGridAction(tokenUnderMouse.isSnapToGrid(), renderer), tokenUnderMouse.isSnapToGrid());
		addToggledGMItem(new VisibilityAction(), tokenUnderMouse.isVisible());

		add(new JSeparator());

		add(new JMenuItem(new CutAction()));
		add(new JMenuItem(new CopyAction()));
		add(new JMenuItem(new DeleteAction()));

		add(new JSeparator());

		add(new RevertLastMoveAction());
		add(new ShowPropertiesDialogAction());
		addOwnedItem(new SaveAction());
	}

	protected JMenu createMacroMenu() {
		if (selectedTokenSet.size() != 1 || getTokenUnderMouse().getMacroNames(true).size() == 0) {
			return null;
		}
		JMenu macroMenu = new JMenu("Macros");
		List<MacroButtonProperties> macroList = getTokenUnderMouse().getMacroList(true);
		String group = "";
		Collections.sort(macroList);
		Map<String, JMenu> groups = new TreeMap<String, JMenu>();
		for (MacroButtonProperties macro : macroList) {
			group = macro.getGroup();
			group = (group == null || group.isEmpty() ? " General" : group); // leading space makes it come first
			JMenu submenu = groups.get(group);
			if (submenu == null) {
				submenu = new JMenu(group);
				groups.put(group, submenu);
			}
			submenu.add(new RunMacroAction(macro.getLabel(), macro));
		}
		// Add the group menus in alphabetical order
		for (JMenu submenu : groups.values())
			macroMenu.add(submenu);

		return macroMenu;
	}

	protected JMenu createSpeechMenu() {
		if (selectedTokenSet.size() != 1 || getTokenUnderMouse().getSpeechNames().size() == 0) {
			return null;
		}
		JMenu menu = new JMenu("Speech");
		List<String> keyList = new ArrayList<String>(getTokenUnderMouse().getSpeechNames());
		Collections.sort(keyList);
		for (String key : keyList) {
			menu.add(new SayAction(key, getTokenUnderMouse().getSpeech(key)));
		}
		return menu;
	}

	private JMenu createExposeMenu() {
		JMenu menu = new JMenu("Expose");
		menu.add(new ExposeVisibleAreaAction());
		menu.add(new ExposeLastPathAction());
		if (MapTool.getPlayer().getRole() == Role.GM) {
			menu.add(new ExposeVisibleAreaOnlyAction());
		}
		menu.setEnabled(getTokenUnderMouse().getHasSight());
		return menu;
	}

	private JMenu createExposedFOWMenu() {
		String viewMenu = I18N.getText("token.popup.menu.fow");
		JMenu menu = new JMenu(viewMenu);
		//menu.add(new AddGlobalExposedAreaAction());
		menu.add(new AddPartyExposedAreaAction());

		if (getRenderer().getZone().getTokens() != null && getRenderer().getZone().getTokens().size() != 0) {
			String tokenViewMenu = I18N.getText("token.popup.menu.fow.tokens");
			JMenu subMenu = new JMenu(tokenViewMenu);
			int subItemCount = 0;
			Zone zone = getRenderer().getZone();
			for (Token tok : zone.getTokens()) {
				if (tok.getHasSight() && tok.isVisible()) {
					ExposedAreaMetaData meta = zone.getExposedAreaMetaData(tok.getExposedAreaGUID());
					if (!meta.getExposedAreaHistory().isEmpty()) {
						subMenu.add(new AddTokensExposedAreaAction(tok.getId()));
						subItemCount++;
					}
				}
			}
			if (subItemCount != 0) {
				menu.add(subMenu);
			}
		}
		menu.addSeparator();
		menu.add(new ClearSelectedExposedAreaAction());

		return menu;
	}

	private class AddTokensExposedAreaAction extends AbstractAction {
		private final GUID tokID;

		public AddTokensExposedAreaAction(GUID theTokId) {
			tokID = theTokId;
			Token sourceToken = getRenderer().getZone().getToken(tokID);
			String tokensView = I18N.getText("token.popup.menu.fow.tokens.view", sourceToken.getName());
			I18N.setAction(tokensView, this, true);
		}

		public void actionPerformed(ActionEvent e) {
			Zone zone = getRenderer().getZone();
			Token sourceToken = getRenderer().getZone().getToken(tokID);
			ExposedAreaMetaData sourceMeta = zone.getExposedAreaMetaData(sourceToken.getExposedAreaGUID());
			for (GUID tok : selectedTokenSet) {
				Token targetToken = getRenderer().getZone().getToken(tok);
				ExposedAreaMetaData targetMeta = zone.getExposedAreaMetaData(targetToken.getExposedAreaGUID());
				targetMeta.addToExposedAreaHistory((Area) sourceMeta.getExposedAreaHistory().clone());
				getRenderer().flush(targetToken);
				zone.setExposedAreaMetaData(targetToken.getExposedAreaGUID(), targetMeta);
				MapTool.serverCommand().updateExposedAreaMeta(zone.getId(), targetToken.getExposedAreaGUID(), targetMeta);
			}
			getRenderer().repaint();
		}
	}

	private class AddPartyExposedAreaAction extends AbstractAction {
		public AddPartyExposedAreaAction() {
			I18N.setAction("token.popup.menu.fow.party", this, true);
		}

		public void actionPerformed(ActionEvent e) {
			Zone zone = getRenderer().getZone();
			List<Token> allToks = getRenderer().getZone().getTokens();
			for (Token tokenSource : allToks) {
				ExposedAreaMetaData sourceMeta = zone.getExposedAreaMetaData(tokenSource.getExposedAreaGUID());
				for (GUID tok : selectedTokenSet) {
					Token token = zone.getToken(tok);
					ExposedAreaMetaData meta = zone.getExposedAreaMetaData(token.getExposedAreaGUID());
					meta.addToExposedAreaHistory((Area) sourceMeta.getExposedAreaHistory().clone());
					getRenderer().flush(token);
					zone.setExposedAreaMetaData(token.getExposedAreaGUID(), meta);
					MapTool.serverCommand().updateExposedAreaMeta(zone.getId(), token.getExposedAreaGUID(), meta);
				}
			}
			getRenderer().repaint();
		}
	}

	private class AddGlobalExposedAreaAction extends AbstractAction {
		public AddGlobalExposedAreaAction() {
			I18N.setAction("token.popup.menu.fow.global", this, true);
		}

		public void actionPerformed(ActionEvent e) {
			Zone zone = getRenderer().getZone();
			Area area = zone.getExposedArea();
			for (GUID tok : selectedTokenSet) {
				Token token = getRenderer().getZone().getToken(tok);
				ExposedAreaMetaData meta = zone.getExposedAreaMetaData(token.getExposedAreaGUID());
				meta.addToExposedAreaHistory((Area) area.clone());
				zone.setExposedAreaMetaData(token.getExposedAreaGUID(), meta);
				getRenderer().flush(token);
				zone.setExposedAreaMetaData(token.getExposedAreaGUID(), meta);
				MapTool.serverCommand().updateExposedAreaMeta(zone.getId(), token.getExposedAreaGUID(), meta);
			}
			getRenderer().repaint();
		}
	}

	private class ClearSelectedExposedAreaAction extends AbstractAction {
		public ClearSelectedExposedAreaAction() {
			I18N.setAction("token.popup.menu.fow.clearselected", this, true);
		}

		public void actionPerformed(ActionEvent e) {
			if (MapTool.getServerPolicy().isUseIndividualFOW()) {
				Zone zone = getRenderer().getZone();
				for (GUID tok : selectedTokenSet) {
					Token token = zone.getToken(tok);
					ExposedAreaMetaData meta = zone.getExposedAreaMetaData(token.getExposedAreaGUID());
					meta.clearExposedAreaHistory();
					getRenderer().flush(token);
					zone.setExposedAreaMetaData(token.getExposedAreaGUID(), meta);
					MapTool.serverCommand().updateExposedAreaMeta(zone.getId(), token.getExposedAreaGUID(), meta);
				}
			}
			getRenderer().repaint();
		}
	}

	private class ExposeVisibleAreaAction extends AbstractAction {
		public ExposeVisibleAreaAction() {
			// putValue(Action.NAME, "Visible area (Ctrl - I)");
			I18N.setAction("token.popup.menu.expose.visible", this, true);
		}

		public void actionPerformed(ActionEvent e) {
			FogUtil.exposeVisibleArea(getRenderer(), selectedTokenSet);
			getRenderer().repaint();
		}
	}

	private class ExposeVisibleAreaOnlyAction extends AbstractAction {
		public ExposeVisibleAreaOnlyAction() {
			// putValue(Action.NAME, "Player visible area only (Ctrl - Shift - O)");
			I18N.setAction("token.popup.menu.expose.currentonly", this, true);
		}

		public void actionPerformed(ActionEvent e) {
			FogUtil.exposePCArea(getRenderer());
			MapTool.serverCommand().exposePCArea(getRenderer().getZone().getId());
			getRenderer().repaint();
		}
	}

	private class ExposeLastPathAction extends AbstractAction {
		public ExposeLastPathAction() {
			// putValue(Action.NAME, "Last path (Ctrl - P)");
			I18N.setAction("token.popup.menu.expose.lastpath", this, true);
			setEnabled(getTokenUnderMouse().getLastPath() != null);
		}

		public void actionPerformed(ActionEvent e) {
			FogUtil.exposeLastPath(getRenderer(), selectedTokenSet);
			getRenderer().repaint();
		}
	}

	protected JMenu createHaloMenu() {
		return createColorAreaMenu("token.popup.menu.halo", getTokenUnderMouse().getHaloColor(), SetHaloAction.class, SetColorChooserAction.class);
	}

	private JMenu createColorAreaMenu(String title, Color selectedColor, Class standardColorActionClass, Class customColorActionClass) {
		JMenu haloMenu = new JMenu(I18N.getText(title));
		try {
			Constructor standardColorActionConstructor = standardColorActionClass.getConstructor(new Class[] { TokenPopupMenu.class, ZoneRenderer.class, Set.class, Color.class, String.class });
			Constructor customColorActionConstructor = customColorActionClass.getConstructor(new Class[] { TokenPopupMenu.class, ZoneRenderer.class, Set.class, String.class });

			JCheckBoxMenuItem noneMenu = new JCheckBoxMenuItem((Action) standardColorActionConstructor.newInstance(new Object[] { this, getRenderer(), selectedTokenSet, null, "None" }));
			JCheckBoxMenuItem customMenu = new JCheckBoxMenuItem((Action) customColorActionConstructor.newInstance(new Object[] { this, getRenderer(), selectedTokenSet, "Custom" }));

			if (selectedColor == null) {
				noneMenu.setSelected(true);
			} else {
				customMenu.setSelected(true);
			}
			haloMenu.add(noneMenu);
			haloMenu.add(customMenu);
			haloMenu.add(new JSeparator());

			for (Object[] row : COLOR_ARRAY) {
				String name = (String) row[0];
				Color bgColor = (Color) row[1];
				Color fgColor = (Color) row[2];

				JCheckBoxMenuItem item = new JCheckBoxMenuItem((Action) standardColorActionConstructor.newInstance(new Object[] { this, getRenderer(), selectedTokenSet, bgColor, name }));
				item.setBackground(bgColor);
				item.setForeground(fgColor);

				if (bgColor.equals(selectedColor)) {
					item.setSelected(true);
					customMenu.setSelected(false);
				}
				haloMenu.add(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return haloMenu;
	}

	protected JMenu createBarMenu() {
		List<BarTokenOverlay> overlays = new ArrayList<BarTokenOverlay>(MapTool.getCampaign().getTokenBarsMap().values());
		if (overlays.isEmpty()) {
			return null;
		}
		JMenu stateMenu = I18N.createMenu("defaultTool.barMenu");
		Collections.sort(overlays, BarTokenOverlay.COMPARATOR);
		for (BarTokenOverlay overlay : overlays) {
			createBarItem(overlay.getName(), stateMenu, getTokenUnderMouse());
		} // endfor
		return stateMenu;
	}

	protected JMenu createStateMenu() {
		// Create the base menu
		JMenu stateMenu = I18N.createMenu("defaultTool.stateMenu");
		stateMenu.add(new ChangeStateAction("clear"));
		stateMenu.addSeparator();
		List<BooleanTokenOverlay> overlays = new ArrayList<BooleanTokenOverlay>(MapTool.getCampaign().getTokenStatesMap().values());
		Collections.sort(overlays, BooleanTokenOverlay.COMPARATOR);

		// Create the group menus first so that they can be placed at the top of the state menu
		Map<String, JMenu> groups = new TreeMap<String, JMenu>();
		for (BooleanTokenOverlay overlay : overlays) {
			String group = overlay.getGroup();
			if (group != null && (group = group.trim()).length() != 0) {
				JMenu menu = groups.get(group);
				if (menu == null) {
					menu = new JMenu(group);
					groups.put(group, menu);
				} // endif
			} // endif
		} // endfor

		// Add the group menus in alphabetical order
		for (JMenu menu : groups.values())
			stateMenu.add(menu);

		// Give each overlay a button in the proper menu
		for (BooleanTokenOverlay overlay : overlays) {
			String group = overlay.getGroup();
			JMenu menu = stateMenu;
			if (group != null && (group = group.trim()).length() != 0)
				menu = groups.get(group);
			createStateItem(overlay.getName(), menu, getTokenUnderMouse());
		} // endfor
		return stateMenu;
	}

	private JMenu createInitiativeMenu() {
		JMenu initiativeMenu = I18N.createMenu("initiative.menu");
		boolean isOwner = MapTool.getFrame().getInitiativePanel().hasOwnerPermission(getTokenUnderMouse());
		if (isOwner) {
			initiativeMenu.add(new ChangeInitiativeState("initiative.menu.add"));
			initiativeMenu.add(new ChangeInitiativeState("initiative.menu.remove"));
			initiativeMenu.addSeparator();
		} // endif
		initiativeMenu.add(new JMenuItem(new ChangeInitiativeState("initiative.menu.resume")));
		initiativeMenu.add(new JMenuItem(new ChangeInitiativeState("initiative.menu.hold")));
		initiativeMenu.addSeparator();
		initiativeMenu.add(new JMenuItem(new ChangeInitiativeState("initiative.menu.setState")));
		initiativeMenu.add(new JMenuItem(new ChangeInitiativeState("initiative.menu.clearState")));

		// Enable by state if only one token selected.
		if (selectedTokenSet.size() == 1) {
			List<Integer> list = MapTool.getFrame().getInitiativePanel().getList().indexOf(getTokenUnderMouse());
			int index = list.isEmpty() ? -1 : list.get(0).intValue();
			if (index >= 0) {
				if (isOwner)
					initiativeMenu.getMenuComponent(0).setEnabled(false);
				boolean hold = MapTool.getFrame().getInitiativePanel().getList().getTokenInitiative(index).isHolding();
				if (hold) {
					initiativeMenu.getMenuComponent(isOwner ? 4 : 1).setEnabled(false);
				} else {
					initiativeMenu.getMenuComponent(isOwner ? 3 : 0).setEnabled(false);
				}
			} else {
				if (isOwner)
					initiativeMenu.getMenuComponent(1).setEnabled(false);
				initiativeMenu.getMenuComponent(isOwner ? 4 : 3).setEnabled(false);
				initiativeMenu.getMenuComponent(isOwner ? 3 : 0).setEnabled(false);
			} // endif
		} // endif
		return initiativeMenu;
	}

	protected void addOwnedToggledItem(Action action, boolean checked) {
		if (action == null) {
			return;
		}
		JCheckBoxMenuItem item = new JCheckBoxMenuItem(action);
		item.setSelected(checked);
		item.setEnabled(tokensAreOwned());
		add(item);
	}

	@Override
	public void showPopup(JComponent component) {
		show(component, x, y);
	}

	private static class PlayerOwnershipMenu extends JCheckBoxMenuItem implements ActionListener {
		private final Set<GUID> tokenSet;
		private final Zone zone;
		private final boolean selected;
		private final String name;

		public PlayerOwnershipMenu(String name, boolean selected, Set<GUID> tokenSet, Zone zone) {
			super(name, selected);
			this.tokenSet = tokenSet;
			this.zone = zone;
			this.selected = selected;
			this.name = name;

			addActionListener(this);
		}

		public void actionPerformed(ActionEvent e) {
			for (GUID guid : tokenSet) {
				Token token = zone.getToken(guid);

				if (selected) {
					for (Player player : (Iterable<Player>) MapTool.getPlayerList()) {
						token.addOwner(player.getName());
					}
					token.removeOwner(name);
				} else {
					token.addOwner(name);
				}
				MapTool.serverCommand().putToken(zone.getId(), token);
			}
			MapTool.getFrame().updateTokenTree();
		}
	}

	/**
	 * Create a radio button menu item for a particuar state
	 * 
	 * @param state
	 *            Create the item for this state
	 * @param menu
	 *            The menu containing all items.
	 * @return A menu item for the passed state.
	 */
	private JCheckBoxMenuItem createStateItem(String state, JMenu menu, Token token) {
		JCheckBoxMenuItem item = new JCheckBoxMenuItem(new ChangeStateAction(state));
		Object value = token.getState(state);
		if (AbstractTokenAccessorFunction.getBooleanValue(value))
			item.setSelected(true);
		menu.add(item);
		return item;
	}

	private JMenuItem createBarItem(String bar, JMenu menu, Token token) {
		JMenuItem item = new JMenuItem(new ChangeBarAction(bar));
		Object value = token.getState(bar);
		int percent = (int) (TokenBarFunction.getBigDecimalValue(value).doubleValue() * 100);
		item.setText(bar + " (" + Integer.toString(percent) + "%)");
		menu.add(item);
		return item;
	}

	private class SetHaloAction extends AbstractAction {
		protected Color color;
		protected Set<GUID> tokenSet;
		protected ZoneRenderer renderer;

		public SetHaloAction(ZoneRenderer renderer, Set<GUID> tokenSet, Color color, String name) {
			this.color = color;
			this.tokenSet = tokenSet;
			this.renderer = renderer;

			putValue(Action.NAME, name);
		}

		public void actionPerformed(ActionEvent e) {
			Zone zone = renderer.getZone();
			for (GUID guid : tokenSet) {
				Token token = zone.getToken(guid);

				if (!AppUtil.playerOwns(token)) {
					continue;
				}
				updateToken(token, color);
				MapTool.serverCommand().putToken(zone.getId(), token);
			}
			MapTool.getFrame().updateTokenTree();
			renderer.repaint();
		}

		protected void updateToken(Token token, Color color) {
			token.setHaloColor(color);
		}
	}

	private class SetColorChooserAction extends AbstractAction {
		protected Color currentColor;
		protected Set<GUID> tokenSet;
		protected ZoneRenderer renderer;

//		private final String title = "Choose Halo Color";

		public SetColorChooserAction(ZoneRenderer renderer, Set<GUID> tokenSet, String name) {
			this.tokenSet = tokenSet;
			this.renderer = renderer;
			this.currentColor = renderer.getZone().getToken(tokenSet.iterator().next()).getHaloColor();
			putValue(Action.NAME, name);
		}

		public void actionPerformed(ActionEvent e) {
			Color color = showColorChooserDialog();
			if (color != null) {
				Zone zone = renderer.getZone();
				for (GUID guid : tokenSet) {
					Token token = zone.getToken(guid);

					if (!AppUtil.playerOwns(token)) {
						continue;
					}
					updateToken(token, color);
					MapTool.serverCommand().putToken(zone.getId(), token);
				}
				MapTool.getFrame().updateTokenTree();
				renderer.repaint();
			}
		}

		protected Color showColorChooserDialog() {
			return JColorChooser.showDialog(MapTool.getFrame().getContentPane(), "Choose Halo Color", currentColor);
		}

		protected void updateToken(Token token, Color color) {
			token.setHaloColor(color);
		}
	}

	private class SetVisionOverlayColorChooserAction extends SetColorChooserAction {
		public SetVisionOverlayColorChooserAction(ZoneRenderer renderer, Set<GUID> tokenSet, String name) {
			super(renderer, tokenSet, name);
			this.currentColor = renderer.getZone().getToken(tokenSet.iterator().next()).getVisionOverlayColor();
		}

		@Override
		protected Color showColorChooserDialog() {
			return JColorChooser.showDialog(MapTool.getFrame().getContentPane(), "Choose Vision Overlay Color", currentColor);
		}

		@Override
		protected void updateToken(Token token, Color color) {
			token.setVisionOverlayColor(color);
		}
	}

	private class SetVisionOverlayColorAction extends SetHaloAction {
		public SetVisionOverlayColorAction(ZoneRenderer renderer, Set tokenSet, Color color, String name) {
			super(renderer, tokenSet, color, name);
		}

		@Override
		protected void updateToken(Token token, Color color) {
			token.setVisionOverlayColor(color);
		}
	}

	private class ChangeBarAction extends AbstractAction {
		public ChangeBarAction(String bar) {
			putValue(ACTION_COMMAND_KEY, bar);
			putValue(NAME, bar);
		}

		public void actionPerformed(ActionEvent e) {
			String name = (String) getValue(NAME);
			JSlider slider = new JSlider(0, 100);
			JPanel labelPanel = new JPanel(new FormLayout("pref", "pref 2px:grow pref"));
			labelPanel.add(new JLabel(name + ":"), new CellConstraints(1, 1, CellConstraints.RIGHT, CellConstraints.TOP));
			JCheckBox hide = new JCheckBox("Hide");
			hide.putClientProperty("JSlider", slider);
			hide.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					JSlider js = (JSlider) ((JCheckBox) e.getSource()).getClientProperty("JSlider");
					js.setEnabled(!((JCheckBox) e.getSource()).isSelected());
				}
			});
			labelPanel.add(hide, new CellConstraints(1, 3, CellConstraints.RIGHT, CellConstraints.TOP));
			slider.setPaintLabels(true);
			slider.setPaintTicks(true);
			slider.setMajorTickSpacing(20);
			slider.createStandardLabels(20);
			slider.setMajorTickSpacing(10);
			if (getTokenUnderMouse().getState(name) == null) {
				hide.setSelected(true);
				slider.setEnabled(false);
				slider.setValue(100);
			} else {
				hide.setSelected(false);
				slider.setEnabled(true);
				slider.setValue((int) (TokenBarFunction.getBigDecimalValue(getTokenUnderMouse().getState(name)).doubleValue() * 100));
			}
			JPanel barPanel = new JPanel(new FormLayout("right:pref 2px pref", "pref"));
			barPanel.add(labelPanel, new CellConstraints(1, 1));
			barPanel.add(slider, new CellConstraints(3, 1));
			if (JOptionPane.showOptionDialog(MapTool.getFrame(), barPanel, "Set " + name + " Value", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null) == JOptionPane.OK_OPTION) {
				Zone zone = MapTool.getFrame().getCurrentZoneRenderer().getZone();
				for (GUID tokenGUID : selectedTokenSet) {
					Token token = zone.getToken(tokenGUID);
					BigDecimal val = hide.isSelected() ? null : new BigDecimal(slider.getValue() / 100.0);
					token.setState(name, val);
					MapTool.serverCommand().putToken(zone.getId(), token);
				}
			}
		}
	}

	/**
	 * Internal class used to handle token state changes.
	 */
	private class ChangeStateAction extends AbstractAction {
		/**
		 * Initialize a state action for a given state.
		 * 
		 * @param state
		 *            The name of the state set when this action is executed
		 */
		public ChangeStateAction(String state) {
			putValue(ACTION_COMMAND_KEY, state); // Set the state command

			// Load the name, mnemonic, accelerator, and description if
			// available
			String key = "defaultTool.stateAction." + state;
			String name = net.rptools.maptool.language.I18N.getText(key);
			if (!name.equals(key)) {
				putValue(NAME, name);
				int mnemonic = I18N.getMnemonic(key);
				if (mnemonic != -1)
					putValue(MNEMONIC_KEY, mnemonic);
				String accel = I18N.getAccelerator(key);
				if (accel != null)
					putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(accel));
				String description = I18N.getDescription(key);
				if (description != null)
					putValue(SHORT_DESCRIPTION, description);
			} else {
				// Default name if no I18N set
				putValue(NAME, state);
			} // endif
		}

		/**
		 * Set the state for all of the selected tokens.
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent aE) {
			ZoneRenderer renderer = MapTool.getFrame().getCurrentZoneRenderer();
			for (GUID tokenGUID : selectedTokenSet) {

				Token token = renderer.getZone().getToken(tokenGUID);
				if (aE.getActionCommand().equals("clear")) {
					for (String state : MapTool.getCampaign().getTokenStatesMap().keySet())
						token.setState(state, null);
				} else {
					token.setState(aE.getActionCommand(), ((JCheckBoxMenuItem) aE.getSource()).isSelected() ? Boolean.TRUE : null);
				} // endif
				MapTool.serverCommand().putToken(renderer.getZone().getId(), token);
			} // endfor
			renderer.repaint();
		}
	}

	private class ChangeInitiativeState extends AbstractAction {
		String name;

		public ChangeInitiativeState(String aName) {
			name = aName;
			I18N.setAction(aName, this);
		}

		public void actionPerformed(ActionEvent e) {
			Zone zone = getRenderer().getZone();
			InitiativeList init = MapTool.getFrame().getInitiativePanel().getList();
			String input = null;
			if (name.equals("initiative.menu.setState")) {
				input = JOptionPane.showInputDialog(I18N.getText("initiative.menu.enterState"));
				if (input == null)
					return;
				input = input.trim();
			} // endif
			for (GUID id : selectedTokenSet) {
				Token token = zone.getToken(id);
				Integer[] list = init.indexOf(token).toArray(new Integer[0]);
				if (name.equals("initiative.menu.add") || name.equals("initiative.menu.addToInitiative")) {
					init.insertToken(-1, token);
				} else {
					for (int i = list.length - 1; i >= 0; i--) {
						int index = list[i].intValue();
						if (name.equals("initiative.menu.remove")) {
							if (index != -1)
								init.removeToken(index);
						} else if (name.equals("initiative.menu.hold")) {
							if (index != -1)
								init.getTokenInitiative(index).setHolding(true);
						} else if (name.equals("initiative.menu.resume")) {
							if (index != -1)
								init.getTokenInitiative(index).setHolding(false);
						} else if (name.equals("initiative.menu.setState")) {
							if (index != -1)
								init.getTokenInitiative(index).setState(input);
						} else if (name.equals("initiative.menu.clearState")) {
							if (index != -1)
								init.getTokenInitiative(index).setState(null);
						} // endif
					} // endif
				} // endfor
			} // endfor
		}
	}

	private class AllOwnershipAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			Zone zone = getRenderer().getZone();

			for (GUID tokenGUID : selectedTokenSet) {
				Token token = zone.getToken(tokenGUID);
				if (token != null) {
					token.setOwnedByAll(true);
					MapTool.serverCommand().putToken(zone.getId(), token);
				}
			}
		}
	}

	private class RemoveAllOwnershipAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			Zone zone = getRenderer().getZone();

			for (GUID tokenGUID : selectedTokenSet) {
				Token token = zone.getToken(tokenGUID);
				if (token != null) {
					token.clearAllOwners();
					MapTool.serverCommand().putToken(zone.getId(), token);
				}
			}
		}
	}

	private class ShowPathsAction extends AbstractAction {
		public ShowPathsAction() {
			putValue(Action.NAME, "Show Path");
		}

		public void actionPerformed(ActionEvent e) {
			for (GUID tokenGUID : selectedTokenSet) {
				Token token = getRenderer().getZone().getToken(tokenGUID);
				if (token == null) {
					continue;
				}
				getRenderer().showPath(token, !getRenderer().isPathShowing(getTokenUnderMouse()));
			}
			getRenderer().repaint();
		}
	}

	private class RevertLastMoveAction extends AbstractAction {
		public RevertLastMoveAction() {
			putValue(Action.NAME, "Revert Last Move");

			// Only available if there is a last move
			for (GUID tokenGUID : selectedTokenSet) {
				Token token = getRenderer().getZone().getToken(tokenGUID);
				if (token == null) {
					continue;
				}
				if (token.getLastPath() == null) {
					setEnabled(false);
					break;
				}
			}
		}

		public void actionPerformed(ActionEvent e) {
			Zone zone = getRenderer().getZone();
			for (GUID tokenGUID : selectedTokenSet) {
				Token token = zone.getToken(tokenGUID);
				if (token == null) {
					continue;
				}
				Path path = token.getLastPath();
				if (path == null) {
					continue;
				}
				// Get the start cell of the last move
				// TODO: I don't like this hard wiring, find a better way
				ZonePoint zp = null;
				if (path.getCellPath().get(0) instanceof CellPoint) {
					zp = zone.getGrid().convert((CellPoint) path.getCellPath().get(0));
				} else {
					zp = (ZonePoint) path.getCellPath().get(0);
				}
				// Relocate
				token.setX(zp.x);
				token.setY(zp.y);

				// Do it again to cancel out the last move position
				token.setX(zp.x);
				token.setY(zp.y);

				// No more last path
				token.setLastPath(null);

				MapTool.serverCommand().putToken(zone.getId(), token);

				// Cache clearing
				getRenderer().flush(token);
			}
			getRenderer().repaint();
		}
	}

	public class RunMacroAction extends AbstractAction {
		private final MacroButtonProperties macro;

		public RunMacroAction(String key, MacroButtonProperties macro) {
			putValue(Action.NAME, key);
			this.macro = macro;
		}

		public void actionPerformed(ActionEvent e) {
			Set<Token> guidSet = new HashSet<Token>();
			for (GUID tokenID : selectedTokenSet) {
				guidSet.add(getRenderer().getZone().getToken(tokenID));
			}
			macro.executeMacro(guidSet);
		}
	}

	public class SayAction extends AbstractAction {
		private final String speech;

		public SayAction(String key, String speech) {
			putValue(Action.NAME, key);
			this.speech = speech;
		}

		public void actionPerformed(ActionEvent e) {
			String identity = getTokenUnderMouse().getName();
			String command = "/im " + identity + ":" + speech;
			JTextComponent commandArea = MapTool.getFrame().getCommandPanel().getCommandTextArea();
			commandArea.setText(command);
			MapTool.getFrame().getCommandPanel().commitCommand();
		}
	}
}
