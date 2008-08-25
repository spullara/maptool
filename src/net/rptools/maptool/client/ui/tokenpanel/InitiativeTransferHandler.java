/* The MIT License
 * 
 * Copyright (c) 2008 Jay Gorrell
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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

import net.rptools.maptool.model.InitiativeList;
import net.rptools.maptool.model.InitiativeList.TokenInitiative;

/**
 * This is the transfer handler for the list in the {@link InitiativePanel}.
 * 
 * @author Jay
 */
public class InitiativeTransferHandler extends TransferHandler {

    /*---------------------------------------------------------------------------------------------
     * Instance Variables 
     *-------------------------------------------------------------------------------------------*/
    
    /**
     * Model containing all of the tokens in this initiative.
     */
    private InitiativePanel panel;
    
    /*---------------------------------------------------------------------------------------------
     * Class Variables 
     *-------------------------------------------------------------------------------------------*/
    
    /**
     * Logger instance for this class.
     */
    static final Logger LOGGER = Logger.getLogger(InitiativeTransferHandler.class.getName());
    
    /*---------------------------------------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------------------------------------*/

    /**
     * Create a handler for the passed panel
     * 
     * @param aPanel The panel supported by this handler.
     */
    public InitiativeTransferHandler(InitiativePanel aPanel) {
        panel = aPanel;
    }
    
    /*---------------------------------------------------------------------------------------------
     * Overridden TransferHandler methods
     *-------------------------------------------------------------------------------------------*/

    /**
     * @see javax.swing.TransferHandler#canImport(javax.swing.JComponent, java.awt.datatransfer.DataFlavor[])
     */
    @Override
    public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
        for (int i = 0; i < transferFlavors.length; i++)
           if (InitiativeTransferable.INIT_TOKEN_FLAVOR.equals(transferFlavors[i])) return true;
        return false;
    }
    
    /**
     * @see javax.swing.TransferHandler#importData(javax.swing.JComponent, java.awt.datatransfer.Transferable)
     */
    @Override
    public boolean importData(JComponent comp, Transferable t) {
        try {
            if (!t.isDataFlavorSupported(InitiativeTransferable.INIT_TOKEN_FLAVOR)) return false;
            
            // Get the token and it's current position
            InitiativeList list = panel.getList(); 
            InitiativeTransferable data = (InitiativeTransferable)t.getTransferData(InitiativeTransferable.INIT_TOKEN_FLAVOR);
            JList displayList = (JList)comp;
            int newIndex = displayList.getSelectedIndex();
            if (newIndex == -1) newIndex = list.getSize();            
            list.moveToken(data.getInititiave(), newIndex);            
            return true;
        } catch (UnsupportedFlavorException e) {
            LOGGER.log(Level.WARNING, "Should not happen, I've already checked to make sure it is valid", e);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Rat bastards changed valid types after I started reading data", e);
        } // entry
        return false;
    }
    
    /**
     * @see javax.swing.TransferHandler#getSourceActions(javax.swing.JComponent)
     */
    @Override
    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }
    
    /**
     * @see javax.swing.TransferHandler#createTransferable(javax.swing.JComponent)
     */
    @Override
    protected Transferable createTransferable(JComponent c) {
        JList displayList = (JList)c;
        TokenInitiative ti = (TokenInitiative)displayList.getSelectedValue();
        if (ti == null || ti.getId() == null) return null;
        return new InitiativeTransferable(ti.getId(), panel.getList().indexOf(ti));
    }
}