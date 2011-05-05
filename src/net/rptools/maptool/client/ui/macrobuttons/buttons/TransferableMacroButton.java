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
package net.rptools.maptool.client.ui.macrobuttons.buttons;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class TransferableMacroButton implements Transferable {

	public static final DataFlavor macroButtonFlavor = new DataFlavor(MacroButton.class, "Macro Button");
	
	//private TokenMacroButton button;
	private TransferData transferData;

	public TransferableMacroButton(MacroButton button) {
		//this.button = button;
		transferData = new TransferData(button);
	}

	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] {macroButtonFlavor};
	}

	public boolean isDataFlavorSupported(DataFlavor dataFlavor) {
		return dataFlavor.equals(macroButtonFlavor);
	}

	public Object getTransferData(DataFlavor dataFlavor) throws UnsupportedFlavorException, IOException {
		if (dataFlavor.equals(macroButtonFlavor)) {
			return transferData;
		}
		
		return null;
	}
}
