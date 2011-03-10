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
package net.rptools.maptool.client.ui.zone;

import java.util.List;

import net.rptools.maptool.model.Player;
import net.rptools.maptool.model.Token;

public class PlayerView {
	private final Player.Role role;
	private final List<Token> tokens; // Optional

	// Optimization
	private final String hash;

	public PlayerView(Player.Role role) {
		this(role, null);
	}

	public PlayerView(Player.Role role, List<Token> tokens) {
		this.role = role;
		this.tokens = tokens != null && !tokens.isEmpty() ? tokens : null;
		hash = calculateHashcode();
	}

	public Player.Role getRole() {
		return role;
	}

	public boolean isGMView() {
		return role == Player.Role.GM;
	}

	public List<Token> getTokens() {
		return tokens;
	}

	public boolean isUsingTokenView() {
		return tokens != null;
	}

	@Override
	public int hashCode() {
		return hash.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof PlayerView)) {
			return false;
		}
		PlayerView other = (PlayerView) obj;
		return hash.equals(other.hash);
	}

	private String calculateHashcode() {
		StringBuilder builder = new StringBuilder();
		builder.append(role);
		if (tokens != null) {
			for (Token token : tokens) {
				builder.append(token.getId());
			}
		}
		return builder.toString();
	}
}
