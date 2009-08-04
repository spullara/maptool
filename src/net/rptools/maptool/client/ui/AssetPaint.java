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

import net.rptools.maptool.model.Asset;
import net.rptools.maptool.util.ImageManager;

public class AssetPaint extends TexturePaint {

	private Asset asset;
	
	public AssetPaint(Asset asset) {
		super(ImageManager.getImageAndWait(asset.getId()), new Rectangle2D.Float(0, 0, ImageManager.getImageAndWait(asset.getId()).getWidth(), ImageManager.getImageAndWait(asset.getId()).getHeight()));
		this.asset = asset;
	}
	
	public Asset getAsset() {
		return asset;
	}
}
