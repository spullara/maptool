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
import java.io.IOException;

import net.rptools.maptool.model.Asset;



/**
 */
public class TransferableAsset implements Transferable {

    public static final DataFlavor dataFlavor = new DataFlavor(Asset.class, "Asset");
    
    private Asset asset;
    
    public TransferableAsset(Asset asset) {
        this.asset = asset;
    }
    
    public Asset getAsset() {
    	return asset;
    }
    
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return asset;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] { dataFlavor};
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(dataFlavor);
    }
}
