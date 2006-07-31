/* The MIT License
 * 
 * Copyright (c) 2006 Jay Gorrell, David Rice, Trevor Croft
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

package net.rptools.maptool.client.ui.tokenpanel;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

import net.rptools.lib.transferable.MapToolTokenTransferData;
import net.rptools.maptool.model.Token;

/**
 * @author jgorrell
 * @version $Revision$ $Date$ $Author$
 */
public class TokenPanelTransferHandler extends TransferHandler {

  /*---------------------------------------------------------------------------------------------
   * Constructor
   *-------------------------------------------------------------------------------------------*/

  /**
   * Create the transfer handler for the passed display component.
   * 
   * @param displayComponent Create the handler for this component.
   */
  public TokenPanelTransferHandler(JComponent displayComponent) {
    if (displayComponent instanceof JList) 
      ((JList)displayComponent).setDragEnabled(true);
    displayComponent.setTransferHandler(this);
  }
  
  /*---------------------------------------------------------------------------------------------
   * Overridden TransferHandler methods
   *-------------------------------------------------------------------------------------------*/

  /**
   * @see javax.swing.TransferHandler#canImport(javax.swing.JComponent, java.awt.datatransfer.DataFlavor[])
   */
  @Override
  public boolean canImport(JComponent aComp, DataFlavor[] aTransferFlavors) {
    return false;
  }

  /**
   * @see javax.swing.TransferHandler#importData(javax.swing.JComponent, java.awt.datatransfer.Transferable)
   */
  @Override
  public boolean importData(JComponent aComp, Transferable aT) {
    return false;
  }

  /**
   * @see javax.swing.TransferHandler#getSourceActions(javax.swing.JComponent)
   */
  @Override
  public int getSourceActions(JComponent aC) {
    return TransferHandler.COPY;
  }
  
  /**
   * @see javax.swing.TransferHandler#createTransferable(javax.swing.JComponent)
   */
  @Override
  protected Transferable createTransferable(JComponent aC) {
    if (aC instanceof JList) {
      Object[] selectedValues = ((JList)aC).getSelectedValues();
      return new TokenPanelTransferable(selectedValues);
    } // endif
    return null;
  }
}

/**
 * Used to transfer the selected tokens from the token panel.
 * 
 * @author jgorrell
 * @version $Revision$ $Date$ $Author$
 */
class TokenPanelTransferable implements Transferable {

  /**
   * The array of tokens read from the token panel when the transferable was created
   */
  private Object[] tokens;
  
  /**
   * Create the transferable for the given tokens. 
   * 
   * @param theTokens Tokens being transfered. Uses object array since that is what is 
   * provided by the list.
   */
  TokenPanelTransferable(Object[] theTokens) {
    tokens = theTokens;
  }
  
  /**
   * @see java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer.DataFlavor)
   */
  public Object getTransferData(DataFlavor aFlavor) throws UnsupportedFlavorException, IOException {
    if (!isDataFlavorSupported(aFlavor)) 
      throw new UnsupportedFlavorException(aFlavor);
    MapToolTokenTransferData tokenList = new MapToolTokenTransferData();
    for (int i = 0; i < tokens.length; i++)
      tokenList.add(((Token)tokens[i]).toTransferData());
    return tokenList;
  }

  /**
   * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
   */
  public DataFlavor[] getTransferDataFlavors() {
    return new DataFlavor[] { MapToolTokenTransferData.MAP_TOOL_TOKEN_LIST_FLAVOR };
  }

  /**
   * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(java.awt.datatransfer.DataFlavor)
   */
  public boolean isDataFlavorSupported(DataFlavor aFlavor) {
    return MapToolTokenTransferData.MAP_TOOL_TOKEN_LIST_FLAVOR.equals(aFlavor);
  }
  
}
