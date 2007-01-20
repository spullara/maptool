package net.rptools.maptool.client.ui.zone;

import net.rptools.maptool.model.Player;

public class ZoneView {

	private int role;
	
	public ZoneView(int role) {
		this.role = role;
	}
	
	public int getRole() {
		return role;
	}
	
	public boolean isGMView() {
		return role == Player.Role.GM;
	}
}
