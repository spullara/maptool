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
package net.rptools.maptool.model.drawing;

import java.awt.Color;
import java.awt.Paint;
import java.awt.image.ImageObserver;
import java.io.Serializable;

import net.rptools.maptool.client.ui.AssetPaint;
import net.rptools.maptool.model.Asset;

public abstract class DrawablePaint implements Serializable {
	public abstract Paint getPaint(ImageObserver... observers);

	public abstract Paint getPaint(int offsetX, int offsetY, double scale, ImageObserver... observers);

	public static DrawablePaint convertPaint(Paint paint) {
		if (paint == null) {
			return null;
		}
		if (paint instanceof Color) {
			return new DrawableColorPaint((Color) paint);
		}
		if (paint instanceof AssetPaint) {
			Asset asset = ((AssetPaint) paint).getAsset();
			return new DrawableTexturePaint(asset);
		}
		throw new IllegalArgumentException("Invalid type of paint: " + paint.getClass().getName());
	}
}
