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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import net.rptools.lib.FileUtil;
import net.rptools.lib.MD5Key;
import net.rptools.lib.image.ImageUtil;
import net.rptools.lib.transferable.FileTransferableHandler;
import net.rptools.lib.transferable.GroupTokenTransferData;
import net.rptools.lib.transferable.ImageTransferableHandler;
import net.rptools.lib.transferable.TokenTransferData;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.util.PersistenceUtil;

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
	public static List getAsset(DropTargetDropEvent dtde) {
		
        Transferable transferable = dtde.getTransferable();
        List assets = new ArrayList();

        try {
	        // EXISTING ASSET
	        if (transferable.isDataFlavorSupported(TransferableAsset.dataFlavor)) {
	
	        	assets.add(handleTransferableAsset(transferable));
	        } 
	        
	        else if (transferable.isDataFlavorSupported(TransferableAssetReference.dataFlavor)) {
	        	
            assets.add(handleTransferableAssetReference(transferable));
	        }
	        
          // LOCAL FILESYSTEM
          else if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
              assets = handleFileList(dtde, transferable);
              
          // DIRECT/BROWSER
          } else if (transferable.isDataFlavorSupported(URL_FLAVOR)) {
            assets.add(handleImage(dtde, transferable));
          }
            
        } catch (Exception e) {
        	System.err.println ("Could not retrieve asset: " + e);
        	e.printStackTrace();
        	return null;
        }

        if (assets == null || assets.isEmpty()) {
            return null;
        }
        
        Asset asset = null;
        for (Object working : assets) {
        	if (working instanceof Asset) {
        		asset = (Asset)working;
        		if (!AssetManager.hasAsset(asset)) 
                    AssetManager.putAsset(asset);          
                if (!MapTool.getCampaign().containsAsset(asset)) 
                    MapTool.serverCommand().putAsset(asset);
        	}
        } 
        return assets;
	}

	private static final DataFlavor URL_FLAVOR = new DataFlavor("text/plain; class=java.lang.String", "Image");
	private static Asset handleImage (DropTargetDropEvent dtde, Transferable transferable) throws IOException, UnsupportedFlavorException {
        
        BufferedImage image = (BufferedImage) new ImageTransferableHandler().getTransferObject(transferable);      
        
        String name = null;
        if (transferable.isDataFlavorSupported(URL_FLAVOR)) {
        	try {
        		URL url = new URL((String)transferable.getTransferData(URL_FLAVOR));
        		name = FileUtil.getNameWithoutExtension(url);
        	} catch (Exception e) {e.printStackTrace();}
        }
        
        Asset asset = new Asset(name, ImageUtil.imageToBytes(image));
        
        return asset;
    }
    
	private static List handleFileList(DropTargetDropEvent dtde, Transferable transferable) throws Exception {
    	List<File> list = new FileTransferableHandler().getTransferObject(transferable);
        List assets = new ArrayList();
    	for (File file : list) {
    		if (Token.isTokenFile(file.getName())) {
    			assets.add(PersistenceUtil.loadToken(file));
    		} else {
                assets.add(AssetManager.createAsset(file));
    		}
    	}
    	return assets;
	}
	
	private static Asset handleTransferableAssetReference(Transferable transferable) throws Exception {
		
		return AssetManager.getAsset((MD5Key) transferable.getTransferData(TransferableAssetReference.dataFlavor));
	}
	
	private static Asset handleTransferableAsset(Transferable transferable) throws Exception {
		
        return (Asset) transferable.getTransferData(TransferableAsset.dataFlavor);
	}
  
    /**
     * Get the tokens from a token list data flavor.
     * 
     * @param transferable
     *            The data that was dropped.
     * @return The tokens from the data or <code>null</code> if this isn't the
     *         proper data type.
     */
    public static List<Token> getTokens(Transferable transferable) {
        try {
            List tokenMaps = (List)transferable.getTransferData(GroupTokenTransferData.GROUP_TOKEN_LIST_FLAVOR);
            List<Token> tokens = new ArrayList<Token>();            
            for (Object object : tokenMaps) {
                if (!(object instanceof TokenTransferData))
                    continue;
                TokenTransferData td = (TokenTransferData) object;
                if (td.getName() == null || td.getName().trim().length() == 0 || td.getToken() == null)
                    continue;
                tokens.add(new Token(td));
            } // endfor
            if (tokens.size() != tokenMaps.size()) {
                final String message = "Added " + tokens.size() + " tokens." + 
                    "\nThere were " + (tokenMaps.size() - tokens.size()) + " tokens that could not be added " +
                    "because they were missing names or images.";
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        JOptionPane.showMessageDialog(MapTool.getFrame(), message, "Warning", JOptionPane.WARNING_MESSAGE);
                    }
                });
            } // endif
            return tokens;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } // endtry
    }
    
    public static boolean isSupportedAssetFlavor(Transferable transferable) {
        return transferable.isDataFlavorSupported(TransferableAsset.dataFlavor)
                || transferable.isDataFlavorSupported(TransferableAssetReference.dataFlavor)
                || transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)
                || transferable.isDataFlavorSupported(URL_FLAVOR); 
    }
    
    public static boolean isSupportedTokenFlavor(Transferable transferable) {
        return transferable.isDataFlavorSupported(GroupTokenTransferData.GROUP_TOKEN_LIST_FLAVOR) || 
        	transferable.isDataFlavorSupported(TransferableToken.dataFlavor);
    }
}
