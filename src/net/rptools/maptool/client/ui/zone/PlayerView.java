package net.rptools.maptool.client.ui.zone;

import net.rptools.maptool.model.Player;

public class PlayerView {

	private Player.Role role;
	
	public PlayerView(Player.Role role) {
		this.role = role;
	}
	
	public Player.Role getRole() {
		return role;
	}
	
	public boolean isGMView() {
		return role == Player.Role.GM;
	}

	@Override
	public int hashCode() {
		return role.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PlayerView)) {
			return false;
		}
		
		PlayerView other = (PlayerView)obj;
		return role == other.role;
	}
}
