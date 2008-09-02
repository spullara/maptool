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

import net.rptools.lib.net.Location;

public class ExportInfo {

	// TODO: Make this an enum
	public interface View {
		public static final int GM = 1;
		public static final int PLAYER = 2;
	}

	// TODO: Make this an enum
	public interface Type {
		public static final int CURRENT_VIEW = 2;
		public static final int FULL_MAP = 3;
	}
	
	private Location location;
	private int view;
	private int type;
	
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getView() {
		return view;
	}
	public void setView(int view) {
		this.view = view;
	}
	
}