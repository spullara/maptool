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
package net.rptools.maptool.model;

/**
 * @author trevor
 */
public class Player {

	public enum Role {
		
		PLAYER,
		GM
	}
	
	private String name; // Primary Key
	private String role;
	private String password;
	
	public Player() {
		// For serialization
	}
	
	public Player (String name, Role role, String password) {
		this.name = name;
		this.role = role.name();
		this.password = password;
	}
	
	public boolean equals(Object obj) {
		
		if (!(obj instanceof Player)) {
			return false;
		}
		
		return name.equals(((Player)obj).name);
	}

	public int hashCode() {
		
		return name.hashCode();
	}
	
	public boolean isGM() {
		return getRole() == Role.GM;
	}
	
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getPassword() {
		return password;
	}
	
	/**
	 * @return Returns the role.
	 */
	public Role getRole() {
		return Role.valueOf(role);
	}
	
	public String toString() {
		return name + " " + (getRole() == Role.PLAYER ? "(Player)" : "(GM)");
	}
}
