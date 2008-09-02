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
package net.rptools.maptool.client.ui.zone;

import java.awt.geom.Area;

import net.rptools.maptool.model.drawing.DrawablePaint;

public class DrawableLight {

	private DrawablePaint paint;
	private Area area;
	
	public DrawableLight(DrawablePaint paint, Area area) {
		super();
		this.paint = paint;
		this.area = area;
	}
	
	public DrawablePaint getPaint() {
		return paint;
	}
	public Area getArea() {
		return area;
	}
	
	
}
