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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import net.rptools.lib.FileUtil;
import net.rptools.lib.MD5Key;
import net.rptools.lib.image.ImageUtil;
import net.rptools.lib.transferable.FileTransferableHandler;
import net.rptools.lib.transferable.ImageTransferableHandler;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.AssetManager;

/**
 * @author tcroft
 */
public class TransferableHelper {

    // TODO: USE ImageTransferable in rplib
    private static final DataFlavor IMAGE_FLAVOR = new DataFlavor("image/x-java-image; class=java.awt.Image", "Image");

	/**
	 * Takes a drop event and returns an asset
	 * from it.  returns null if an asset could not be obtained
	 */
	public static Asset getAsset(DropTargetDropEvent dtde) {
		
        Transferable transferable = dtde.getTransferable();
    	dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

        Asset asset = null;
        try {
	        // EXISTING ASSET
	        if (transferable.isDataFlavorSupported(TransferableAsset.dataFlavor)) {
	
	        	asset = handleTransferableAsset(transferable);
	        } 
	        
	        else if (transferable.isDataFlavorSupported(TransferableAssetReference.dataFlavor)) {
	        	
	        	asset = handleTransferableAssetReference(transferable);
	        }
	        
            else {
                // DIRECT/BROWSER
            	try {
            		asset = handleImage(dtde, transferable);
            	} catch (UnsupportedFlavorException ufe) {

                	// LOCAL FILESYSTEM
                	asset = handleFileList(dtde, transferable);
            	}

            }
            
        } catch (Exception e) {
        	System.err.println ("Could not retrieve asset: " + e);
        	e.printStackTrace();
        	return null;
        }

        if (asset == null) {
            return null;
        }
        
        if (!AssetManager.hasAsset(asset)) {
    		AssetManager.putAsset(asset);
        }
        
        // Add it to the server if we need to
        if (!MapTool.getCampaign().containsAsset(asset)) {
        	
            MapTool.serverCommand().putAsset(asset);
        }

        return asset;
	}

    private static Asset handleImage (DropTargetDropEvent dtde, Transferable transferable) throws IOException, UnsupportedFlavorException {
        
        BufferedImage image = (BufferedImage) new ImageTransferableHandler().getTransferObject(transferable);        
        
        Asset asset = new Asset(ImageUtil.imageToBytes(image));
        
        return asset;
    }
    
	private static Asset handleFileList(DropTargetDropEvent dtde, Transferable transferable) throws Exception {
		
    	List<File> list = new FileTransferableHandler().getTransferObject(transferable);
    	
        // We only support using one at a time for now
		Asset asset = new Asset(FileUtil.loadFile(list.get(0)));

        return asset;
	}
	
	private static Asset handleTransferableAssetReference(Transferable transferable) throws Exception {
		
		return AssetManager.getAsset((MD5Key) transferable.getTransferData(TransferableAssetReference.dataFlavor));
	}
	
	private static Asset handleTransferableAsset(Transferable transferable) throws Exception {
		
        return (Asset) transferable.getTransferData(TransferableAsset.dataFlavor);
	}
}
