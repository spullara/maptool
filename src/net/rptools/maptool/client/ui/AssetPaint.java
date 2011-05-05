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
package net.rptools.maptool.client.ui;

import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import net.rptools.maptool.model.Asset;
import net.rptools.maptool.util.ImageManager;

public class AssetPaint extends TexturePaint {

	private Asset asset;

	public AssetPaint(Asset asset) {
		this(ImageManager.getImageAndWait(asset.getId()));
		this.asset = asset;
	}

	// Only used to avoid a bunch of calls to getImageAndWait() that the compiler may
	// not be able to optimize (method calls may not be optimizable when side effects
	// of the method are not known to the compiler).
	private AssetPaint(BufferedImage img) {
		super(img, new Rectangle2D.Float(0, 0, img.getWidth(), img.getHeight()));
	}

	public Asset getAsset() {
		return asset;
	}
}
