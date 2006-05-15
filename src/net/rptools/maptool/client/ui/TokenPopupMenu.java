package net.rptools.maptool.client.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import net.rptools.maptool.client.AppUtil;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.tool.FacingTool;
import net.rptools.maptool.client.tool.PointerTool;
import net.rptools.maptool.client.ui.token.LightDialog;
import net.rptools.maptool.client.ui.token.TokenStates;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.language.I18N;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Player;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.TokenSize;
import net.rptools.maptool.model.Zone;

public class TokenPopupMenu extends JPopupMenu {

	private ZoneRenderer renderer;

	int x, y;

	Set<GUID> selectedTokenSet;

	private Token tokenUnderMouse;

	public TokenPopupMenu(Set<GUID> selectedTokenSet, int x, int y,
			ZoneRenderer renderer, Token tokenUnderMouse) {
		this.renderer = renderer;
		this.x = x;
		this.y = y;
		this.selectedTokenSet = selectedTokenSet;
		this.tokenUnderMouse = tokenUnderMouse;

		boolean enabled = true;
		if (!MapTool.getPlayer().isGM()
				&& MapTool.getServerPolicy().useStrictTokenManagement()) {
			for (GUID tokenGUID : selectedTokenSet) {
				Token token = renderer.getZone().getToken(tokenGUID);

				if (!token.isOwner(MapTool.getPlayer().getName())) {
					enabled = false;
					break;
				}
			}
		}

		// SIZE
		// TODO: Genericize the heck out of this.
		JMenu sizeMenu = new JMenu("Size");
		sizeMenu.setEnabled(enabled);

		JMenuItem freeSize = new JMenuItem("Free Size");
		freeSize.setEnabled(false);

		sizeMenu.add(freeSize);
		sizeMenu.addSeparator();

		for (TokenSize.Size size : TokenSize.Size.values()) {
			JMenuItem menuItem = new JCheckBoxMenuItem(new ChangeSizeAction(
					size.name(), size));
			if (tokenUnderMouse.getSize() == size.value()) {
				menuItem.setSelected(true);
			}

			sizeMenu.add(menuItem);
		}

		// Grid
		boolean snapToGrid = !tokenUnderMouse.isSnapToGrid();
		JCheckBoxMenuItem snapToGridMenuItem = new JCheckBoxMenuItem(
				"placeholder", !snapToGrid);
		snapToGridMenuItem
				.setAction(new SnapToGridAction(snapToGrid, renderer));
		snapToGridMenuItem.setEnabled(renderer.getZone().getGrid()
				.getCapabilities().isSnapToGridSupported()
				&& enabled);

		// Visibility
		JCheckBoxMenuItem visibilityMenuItem = new JCheckBoxMenuItem("Visible",
				tokenUnderMouse.isVisible());
		visibilityMenuItem.setEnabled(enabled);
		// TODO: Make this an action, not aic
		visibilityMenuItem.addActionListener(new VisibilityAction());

		// Rename
		// TODO: Make this an action, not aic
		JMenuItem renameMenuItem = new JMenuItem("Rename");
		renameMenuItem.setEnabled(enabled);
		if (selectedTokenSet.size() == 1) {

			renameMenuItem.addActionListener(new RenameAction());
		}

		// Arrange
		JMenu arrangeMenu = new JMenu("Arrange");
		arrangeMenu.setEnabled(enabled);
		JMenuItem bringToFrontMenuItem = new JMenuItem("Bring to Front");
		bringToFrontMenuItem.addActionListener(new BringToFrontAction());

		JMenuItem sendToBackMenuItem = new JMenuItem("Send to Back");
		sendToBackMenuItem.addActionListener(new SendToBackAction());

		arrangeMenu.add(bringToFrontMenuItem);
		arrangeMenu.add(sendToBackMenuItem);

		// Create the state menu
		JMenu stateMenu = I18N.createMenu("defaultTool.stateMenu");
		stateMenu.setEnabled(enabled);
		stateMenu.add(new ChangeStateAction("clear"));
		stateMenu.addSeparator();
		for (String state : TokenStates.getStates())
			createStateItem(state, stateMenu, tokenUnderMouse);

		// Ownership
		JMenu ownerMenu = I18N.createMenu("defaultTool.ownerMenu");
		ownerMenu.setEnabled(enabled);
		if (MapTool.getPlayer().isGM()
				&& MapTool.getServerPolicy().useStrictTokenManagement()) {

			JCheckBoxMenuItem allMenuItem = new JCheckBoxMenuItem("All");
			allMenuItem.addActionListener(new AllOwnershipAction());
			ownerMenu.add(allMenuItem);

			JMenuItem removeAllMenuItem = new JMenuItem("Remove All");
			removeAllMenuItem.addActionListener(new RemoveAllOwnershipAction());
			ownerMenu.add(removeAllMenuItem);
			ownerMenu.add(new JSeparator());

			int playerCount = 0;
			for (Player player : (Iterable<Player>) MapTool.getPlayerList()) {

				if (player.isGM()) {
					continue;
				}

				boolean selected = false;

				for (GUID tokenGUID : selectedTokenSet) {
					Token token = renderer.getZone().getToken(tokenGUID);
					if (token.isOwner(player.getName())) {
						selected = true;
						break;
					}
				}
				JCheckBoxMenuItem playerMenu = new PlayerOwnershipMenu(player
						.getName(), selected, selectedTokenSet, renderer
						.getZone());

				ownerMenu.add(playerMenu);
				playerCount++;
			}

			if (playerCount == 0) {
				JMenuItem noPlayerMenu = new JMenuItem("No players");
				noPlayerMenu.setEnabled(false);
				ownerMenu.add(noPlayerMenu);
			}

		}

		// Organize
		add(stateMenu);
		add(sizeMenu);
		add(arrangeMenu);
		add(new ChangeStateAction("light"));
		add(snapToGridMenuItem);
		add(visibilityMenuItem);
		add(new SetFacingAction());
		add(new JSeparator());
		add(renameMenuItem);
		add(new JMenuItem(new StartMoveAction()));
		add(new JSeparator());
		add(new JMenuItem(new DeleteAction()));

		// GM Only
		if (MapTool.getPlayer().isGM()
				&& MapTool.getServerPolicy().useStrictTokenManagement()) {
			add(ownerMenu);
		}

	}

	public void showPopup(JComponent component) {
		show(component, x, y);
	}

	private static class PlayerOwnershipMenu extends JCheckBoxMenuItem
			implements ActionListener {

		private Set<GUID> tokenSet;

		private Zone zone;

		private boolean selected;

		private String name;

		public PlayerOwnershipMenu(String name, boolean selected,
				Set<GUID> tokenSet, Zone zone) {
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
					for (Player player : (Iterable<Player>) MapTool
							.getPlayerList()) {
						token.addOwner(player.getName());
					}
					token.removeOwner(name);
				} else {
					token.addOwner(name);
				}

				MapTool.serverCommand().putToken(zone.getId(), token);
			}
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
	private JCheckBoxMenuItem createStateItem(String state, JMenu menu,
			Token token) {
		JCheckBoxMenuItem item = new JCheckBoxMenuItem(new ChangeStateAction(
				state));
		Object value = token.getState(state);
		if (value != null && value instanceof Boolean
				&& ((Boolean) value).booleanValue())
			item.setSelected(true);
		menu.add(item);
		return item;
	}

	private class SetFacingAction extends AbstractAction {
		
		public SetFacingAction() {
			super("Set Facing");
		}
		
		public void actionPerformed(ActionEvent e) {
			
			Toolbox toolbox = MapTool.getFrame().getToolbox(); 
			
			FacingTool tool = (FacingTool) toolbox.getTool(FacingTool.class);
			tool.init(tokenUnderMouse, selectedTokenSet);
			
			toolbox.setSelectedTool(FacingTool.class);
		}
	}
	
	private class SnapToGridAction extends AbstractAction {

		private boolean snapToGrid;

		private ZoneRenderer renderer;

		public SnapToGridAction(boolean snapToGrid, ZoneRenderer renderer) {
			super("Snap to grid");
			this.snapToGrid = snapToGrid;
			this.renderer = renderer;
		}

		public void actionPerformed(ActionEvent e) {

			for (GUID guid : selectedTokenSet) {

				Token token = renderer.getZone().getToken(guid);
				if (token == null) {
					continue;
				}

				token.setSnapToGrid(snapToGrid);
				MapTool.serverCommand().putToken(renderer.getZone().getId(),
						token);
			}
		}
	}

	/**
	 * Internal class used to handle token state changes.
	 * 
	 * @author jgorrell
	 * @version $Revision: 1882 $ $Date: 2006-03-08 17:15:47 -0600 (Wed, 08 Mar
	 *          2006) $ $Author: tcroft $
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
					// Wipe out the entire state HashMap, this is what the
					// previous
					// code attempted to do but was failing due to the Set
					// returned
					// by getStatePropertyNames being a non-static view into a
					// set.
					// Removing items from the map was messing up the iteration.
					// Here, clear all states, unfortunately, including light.
					token.getStatePropertyNames().clear();
				} else if (aE.getActionCommand().equals("light")) {
					LightDialog.show(token, "light");
				} else {
					token
							.setState(aE.getActionCommand(),
									((JCheckBoxMenuItem) aE.getSource())
											.isSelected() ? Boolean.TRUE : null);
				} // endif
				MapTool.serverCommand().putToken(renderer.getZone().getId(),
						token);
			} // endfor
			renderer.repaint();
		}
	}

	private class ChangeSizeAction extends AbstractAction {

		private TokenSize.Size size;

		public ChangeSizeAction(String label, TokenSize.Size size) {
			super(label);
			this.size = size;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {

			ZoneRenderer renderer = MapTool.getFrame().getCurrentZoneRenderer();
			for (GUID tokenGUID : selectedTokenSet) {

				Token token = renderer.getZone().getToken(tokenGUID);
				token.setSize(size.value());
				MapTool.serverCommand().putToken(renderer.getZone().getId(),
						token);
			}

			renderer.repaint();
		}

	}

	private class VisibilityAction extends AbstractAction {

		public void actionPerformed(ActionEvent e) {

			for (GUID guid : selectedTokenSet) {

				Token token = renderer.getZone().getToken(guid);
				if (token == null) {
					continue;
				}

				token.setVisible(((JCheckBoxMenuItem) e.getSource())
						.isSelected());
				renderer.flush(token);

				MapTool.serverCommand().putToken(renderer.getZone().getId(),
						token);
			}

			renderer.repaint();
		}
	}

	private class RenameAction extends AbstractAction {

		public void actionPerformed(ActionEvent e) {

			Token token = renderer.getZone().getToken(
					selectedTokenSet.iterator().next());

			String newName = (String) JOptionPane.showInputDialog(renderer,
					"Pick a new name for this token", "Rename Token",
					JOptionPane.QUESTION_MESSAGE, null, null, token.getName());
			if (newName == null || newName.length() == 0) {
				return;
			}

			token.setName(newName);
			MapTool.serverCommand().putToken(renderer.getZone().getId(), token);
			MapTool.getFrame().repaint();
		}
	}

	private class BringToFrontAction extends AbstractAction {

		public void actionPerformed(ActionEvent e) {

			MapTool.serverCommand().bringTokensToFront(
					renderer.getZone().getId(), selectedTokenSet);

			MapTool.getFrame().repaint();
		}
	}

	private class SendToBackAction extends AbstractAction {

		public void actionPerformed(ActionEvent e) {

			MapTool.serverCommand().sendTokensToBack(
					renderer.getZone().getId(), selectedTokenSet);

			MapTool.getFrame().repaint();
		}
	}

	private class AllOwnershipAction extends AbstractAction {

		public void actionPerformed(ActionEvent e) {
			for (GUID tokenGUID : selectedTokenSet) {
				Token token = renderer.getZone().getToken(tokenGUID);
				if (token != null) {
					token.setAllOwners();
					MapTool.serverCommand().putToken(
							renderer.getZone().getId(), token);
				}
			}
		}
	}

	private class RemoveAllOwnershipAction extends AbstractAction {

		public void actionPerformed(ActionEvent e) {
			for (GUID tokenGUID : selectedTokenSet) {
				Token token = renderer.getZone().getToken(tokenGUID);
				if (token != null) {
					token.clearAllOwners();
					MapTool.serverCommand().putToken(
							renderer.getZone().getId(), token);
				}
			}
		}
	}

	private class StartMoveAction extends AbstractAction {

		public StartMoveAction() {
			putValue(Action.NAME, "Move");

			Tool tool = MapTool.getFrame().getToolbox().getSelectedTool();
			if (!(tool instanceof PointerTool)) {
				setEnabled(false);
			}
		}

		public void actionPerformed(ActionEvent e) {

			PointerTool tool = (PointerTool) MapTool.getFrame().getToolbox()
					.getSelectedTool();

			tool.startTokenDrag(tokenUnderMouse);
		}
	}

	private class DeleteAction extends AbstractAction {

		public DeleteAction() {
			putValue(Action.NAME, "Delete");
		}

		public void actionPerformed(ActionEvent e) {

			if (!MapTool
					.confirm("Are you sure you want to delete the selected tokens ?")) {
				return;
			}

			for (GUID tokenGUID : selectedTokenSet) {

				Token token = renderer.getZone().getToken(tokenGUID);

				if (AppUtil.playerOwnsToken(token)) {
					renderer.getZone().removeToken(tokenGUID);
					MapTool.serverCommand().removeToken(
							renderer.getZone().getId(), tokenGUID);
				}
			}
		}
	}
}
