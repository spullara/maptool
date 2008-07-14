package net.rptools.maptool.client.ui.macrobutton;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class TransferableMacroButton implements Transferable {

	public static final DataFlavor tokenMacroButtonFlavor = new DataFlavor(TokenMacroButton.class, "Token Macro Button");
	
	//private TokenMacroButton button;
	private TransferData transferData;

	public TransferableMacroButton(TokenMacroButton button) {
		//this.button = button;
		transferData = new TransferData(button.getMacro(), button.getCommand());
	}

	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] {tokenMacroButtonFlavor};
	}

	public boolean isDataFlavorSupported(DataFlavor dataFlavor) {
		return dataFlavor.equals(tokenMacroButtonFlavor);
	}

	public Object getTransferData(DataFlavor dataFlavor) throws UnsupportedFlavorException, IOException {
		if (dataFlavor.equals(tokenMacroButtonFlavor)) {
			return transferData;
		}
		
		return null;
	}
}
