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
package net.rptools.maptool.client;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.io.FileUtils;

import net.rptools.lib.FileUtil;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.transfer.ConsumerListener;

/**
 * Handles incoming segmented assets
 * @author trevor
 */
public class AssetTransferHandler implements ConsumerListener {

	public void assetComplete(Serializable id, String name, File data) {

		byte[] assetData = null;
		try {
			assetData = FileUtils.readFileToByteArray(data);
		} catch (IOException ioe) {
			MapTool.showError("Error loading composed asset file: " + id);
			return;
		}
		
		Asset asset = new Asset(name, assetData);
		if (!asset.getId().equals(id)) {
			MapTool.showError("Received an invalid image: " + id);
			return;
		}

		// Install it into our system
		AssetManager.putAsset(asset);

		// Remove the temp file
		data.delete();
		
		MapTool.getFrame().refresh();
	}

	public void assetUpdated(Serializable id) {
		// Nothing to do
	}
	public void assetAdded(Serializable id) {
		// Nothing to do
	}
}
