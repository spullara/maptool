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
 * Represents a player pointer on the screen
 */
public class Pointer {

	public enum Type {
		ARROW,
		SPEECH_BUBBLE,
		THOUGHT_BUBBLE
	}
	
	private GUID zoneGUID;
	private int x;
	private int y;
	private double direction; // 
	private String type;
	
	public Pointer() {/* Hessian serializable */}
	
	public Pointer(Zone zone, int x, int y, double direction, Type type) {
		this.zoneGUID = zone.getId();
		this.x = x;
		this.y = y;
		this.direction = direction;
		this.type = type.name();
	}
	
	public String toString() {
		return x + "." + y + "-" + direction;
	}

	public Type getType() {
		return type != null ? Type.valueOf(type) : Type.ARROW;
	}
	
	public GUID getZoneGUID() {
		return zoneGUID;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public double getDirection() {
		return direction;
	}
}
