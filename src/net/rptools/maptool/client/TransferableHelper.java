/*
 */
package net.rptools.maptool.client;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.List;

import net.rptools.clientserver.hessian.client.ClientConnection;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.util.FileUtil;
import net.rptools.maptool.util.MD5Key;

/**
 * @author tcroft
 */
public class TransferableHelper {

	// TODO: I don't like this here, but couldn't think of a better place
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
	        
	        // LOCAL FILES/BROWSER
	        else if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
	        	
	        	asset = handleFileList(dtde, transferable);
	        }	        	
	
        } catch (Exception e) {
        	System.out.println ("Could not retrieve asset: " + e);
        }

		return asset;
	}

	private static Asset handleFileList(DropTargetDropEvent dtde, Transferable transferable) throws Exception {
		
    	List<File> list = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
    	
    	if (list.size() == 0) {
    		return null;
    	}
    	
    	// For some reason, firefox does not actually write out the temporary file designated in
    	// this list until list line is called.  So it has to stay ABOVE the loadFile() call
    	// It also requires just a moment to copy from internal system whatever into the file
        dtde.dropComplete(true);
        try {
        	Thread.sleep(1000);
        } catch (Exception e) {
        	e.printStackTrace();
        }

        // We only support using one at a time for now
		Asset asset = new Asset(FileUtil.loadFile(list.get(0)));
		AssetManager.putAsset(asset);
        if (MapToolClient.isConnected()) {
        	
        	// TODO: abstract this
            ClientConnection conn = MapToolClient.getInstance().getConnection();
            
            conn.callMethod(MapToolClient.COMMANDS.putAsset.name(), asset);
        }

        return asset;
	}
	
	private static Asset handleTransferableAssetReference(Transferable transferable) throws Exception {
		
		return AssetManager.getAsset((MD5Key) transferable.getTransferData(TransferableAssetReference.dataFlavor));
	}
	
	private static Asset handleTransferableAsset(Transferable transferable) throws Exception {
		
    	Asset asset = null;
		
		// Add it to the system
		asset = (Asset) transferable.getTransferData(TransferableAsset.dataFlavor);
		AssetManager.putAsset(asset);
        if (MapToolClient.isConnected()) {
        	
        	// TODO: abstract this
            ClientConnection conn = MapToolClient.getInstance().getConnection();
            
            conn.callMethod(MapToolClient.COMMANDS.putAsset.name(), asset);
        }
    	
        return asset;
	}
}
