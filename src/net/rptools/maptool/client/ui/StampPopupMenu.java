package net.rptools.maptool.client.ui;

import java.awt.event.ActionEvent;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import net.rptools.maptool.client.AppUtil;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.tool.FacingTool;
import net.rptools.maptool.client.tool.PointerTool;
import net.rptools.maptool.client.ui.token.LightDialog;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.language.I18N;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.TokenSize;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.util.ImageManager;
import net.rptools.maptool.util.TokenUtil;

public class StampPopupMenu extends JPopupMenu {

	private ZoneRenderer renderer;

	int x, y;

	Set<GUID> selectedTokenSet;

	private Token tokenUnderMouse;

	public StampPopupMenu(Set<GUID> selectedTokenSet, int x, int y,
			ZoneRenderer renderer, Token tokenUnderMouse) {
		this.renderer = renderer;
		this.x = x;
		this.y = y;
		this.selectedTokenSet = selectedTokenSet;
		this.tokenUnderMouse = tokenUnderMouse;

		// SIZE
		// TODO: Genericize the heck out of this.
		JMenu sizeMenu = new JMenu("Size");
		JMenuItem freeSize = new JMenuItem(new FreeSizeAction());

		sizeMenu.add(freeSize);
		sizeMenu.addSeparator();

		for (TokenSize.Size size : TokenSize.Size.values()) {
			JMenuItem menuItem = new JCheckBoxMenuItem(new ChangeSizeAction(
					size.name(), size));
			if (tokenUnderMouse.isSnapToScale() && tokenUnderMouse.getSize() == size.value()) {
				menuItem.setSelected(true);
			}

			sizeMenu.add(menuItem);
		}

		// Grid
		boolean snapToGrid = !tokenUnderMouse.isSnapToGrid();
		JCheckBoxMenuItem snapToGridMenuItem = new JCheckBoxMenuItem(
				"placeholder", !snapToGrid);
		snapToGridMenuItem.setAction(new SnapToGridAction(snapToGrid, renderer));

		// Visibility
		JCheckBoxMenuItem visibilityMenuItem = new JCheckBoxMenuItem("Visible", tokenUnderMouse.isVisible());
		visibilityMenuItem.addActionListener(new VisibilityAction());

		// Arrange
		JMenu arrangeMenu = new JMenu("Arrange");
		JMenuItem bringToFrontMenuItem = new JMenuItem("Bring to Front");
		bringToFrontMenuItem.addActionListener(new BringToFrontAction());

		JMenuItem sendToBackMenuItem = new JMenuItem("Send to Back");
		sendToBackMenuItem.addActionListener(new SendToBackAction());

		arrangeMenu.add(bringToFrontMenuItem);
		arrangeMenu.add(sendToBackMenuItem);

		// Organize
		add(new SetFacingAction());
		add(new ClearFacingAction());
		add(new JMenuItem(new StartMoveAction()));

		add(new JSeparator());

		add(visibilityMenuItem);
		add(new ChangeStateAction("light"));
		add(arrangeMenu);
		
		add(new JSeparator());

		add(sizeMenu);
		add(snapToGridMenuItem);

		add(new JSeparator());

		add(new JMenuItem(new DeleteAction()));

		add(new JSeparator());

		JMenu changeTypeMenu = new JMenu("Change to");
		
		changeTypeMenu.add(new JMenuItem(new ChangeTypeAction(Zone.Layer.TOKEN)));
		changeTypeMenu.add(new JMenuItem(new ChangeTypeAction(Zone.Layer.STAMP)));
		changeTypeMenu.add(new JMenuItem(new ChangeTypeAction(Zone.Layer.BACKGROUND)));
		
		add(changeTypeMenu);
	}

	public void showPopup(JComponent component) {
		show(component, x, y);
	}
	
	public class ChangeTypeAction extends AbstractAction{
		
		private Zone.Layer layer;
		
		public ChangeTypeAction(Zone.Layer layer) {
			putValue(Action.NAME, layer.toString());
			this.layer = layer;
		}
		
		public void actionPerformed(ActionEvent e) {
			
			for (GUID tokenGUID : selectedTokenSet) {
				Token token = renderer.getZone().getToken(tokenGUID);
				if (token == null) {
					continue;
				}

				token.setLayer(layer);
				MapTool.serverCommand().putToken(renderer.getZone().getId(), token);
			}
			
			renderer.repaint();
			MapTool.getFrame().updateTokenTree();
		}
	}
	
	public class FreeSizeAction extends AbstractAction {
		
		public FreeSizeAction() {
			putValue(Action.NAME, "Free Size");
		}
		
		public void actionPerformed(ActionEvent e) {

			for (GUID tokenGUID : selectedTokenSet) {
				Token token = renderer.getZone().getToken(tokenGUID);
				if (token == null) {
					continue;
				}
				
				token.setSnapToScale(false);
				MapTool.serverCommand().putToken(renderer.getZone().getId(), token);
			}
			
			renderer.repaint();
		}
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
	
	private class ClearFacingAction extends AbstractAction {
		
		public ClearFacingAction() {
			super("Clear Facing");
		}
		
		public void actionPerformed(ActionEvent e) {
			
			ZoneRenderer renderer = MapTool.getFrame().getCurrentZoneRenderer();
			for (GUID tokenGUID : selectedTokenSet) {

				Token token = renderer.getZone().getToken(tokenGUID);
				token.setFacing(null);
				MapTool.serverCommand().putToken(renderer.getZone().getId(),
						token);
			}

			renderer.repaint();
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
				token.setSnapToScale(true);
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

				MapTool.serverCommand().putToken(renderer.getZone().getId(),
						token);
			}

			renderer.repaint();
		}
	}

	private class BringToFrontAction extends AbstractAction {

		public void actionPerformed(ActionEvent e) {

			MapTool.serverCommand().bringTokensToFront(
					renderer.getZone().getId(), selectedTokenSet);

			MapTool.getFrame().refresh();
		}
	}

	private class SendToBackAction extends AbstractAction {

		public void actionPerformed(ActionEvent e) {

			MapTool.serverCommand().sendTokensToBack(
					renderer.getZone().getId(), selectedTokenSet);

			MapTool.getFrame().refresh();
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

				if (AppUtil.playerOwns(token)) {
					renderer.getZone().removeToken(tokenGUID);
					MapTool.serverCommand().removeToken(
							renderer.getZone().getId(), tokenGUID);
				}
			}
		}
	}
}
