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
package net.rptools.maptool.model.drawing;

import java.awt.Color;
import java.awt.Paint;
import java.io.Serializable;

public class DrawableColorPaint extends DrawablePaint implements Serializable {

	private int color;
	private transient Color colorCache;

	public DrawableColorPaint() {
		// For serialization
	}
	
	public DrawableColorPaint(Color color) {
		this.color = color.getRGB();
	}
	
	@Override
	public Paint getPaint() {
		if (colorCache == null) {
			colorCache = new Color(color);
		}
		return colorCache;
	}

	@Override
	public Paint getPaint(int offsetX, int offsetY, double scale) {
		return getPaint();
	}
	
}
