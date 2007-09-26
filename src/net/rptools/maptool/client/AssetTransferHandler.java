/* The MIT License
 * 
 * Copyright (c) 2005 David Rice, Trevor Croft
 * 
 * Permission is hereby granted, free of charge, to any person 
 * obtaining a copy of this software and associated documentation files 
 * (the "Software"), to deal in the Software without restriction, 
 * including without limitation the rights to use, copy, modify, merge, 
 * publish, distribute, sublicense, and/or sell copies of the Software, 
 * and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be 
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS 
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN 
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
 * SOFTWARE.
 */
package net.rptools.maptool.client;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

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
			assetData = FileUtil.loadFile(data);
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
}
