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
package net.rptools.maptool.client.ui.model;

import java.awt.Image;
import java.awt.datatransfer.Transferable;

import net.rptools.common.swing.ImagePanelModel;
import net.rptools.maptool.client.TransferableAsset;
import net.rptools.maptool.model.AssetGroup;
import net.rptools.maptool.util.ImageManager;

public class AssetGroupImagePanelModel implements ImagePanelModel {

	private AssetGroup assetGroup;
	
	public AssetGroupImagePanelModel(AssetGroup assetGroup) {
		this.assetGroup = assetGroup;
	}
	
	public int getImageCount() {
		return assetGroup.getAssetCount();
	}

	public Image getImage(int index) {
		return ImageManager.getImage(assetGroup.getAssets().get(index));
	}

	public Transferable getTransferable(int index) {
		return new TransferableAsset(assetGroup.getAssets().get(index));
	}
    
    public String getCaption(int index) {
        return "";
    }
    
    public Object getID(int index) {
        return new Integer(index);
    }
    
    public Image getImage(Object ID) {
        return getImage(((Integer)ID).intValue());
    }
}
