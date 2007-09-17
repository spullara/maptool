package net.rptools.maptool.client.ui.zone;

import net.rptools.maptool.model.Player;

public class ZoneView {

	private Player.Role role;
	
	public ZoneView(Player.Role role) {
		this.role = role;
	}
	
	public Player.Role getRole() {
		return role;
	}
	
	public boolean isGMView() {
		return role == Player.Role.GM;
	}
}
