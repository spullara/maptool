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

import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.InitiativeList;
import net.rptools.maptool.model.Token;
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
     * Pass GUIDs around when dragging.
     */
    public static final DataFlavor GUID_FLAVOR;
    
    /**
     * Logger instance for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(InitiativeTransferHandler.class.getName());
    
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
    
    /**
     * Build the flavors and handle exceptions.
     */
    static {
        DataFlavor guid = null;
        try {
            guid = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=net.rptools.lib.GUID");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.WARNING, "Should never happen since the GUID is a valid class when the classpath is correct.");
        } // endtry
        GUID_FLAVOR = guid;
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
           if (GUID_FLAVOR.equals(transferFlavors[i])) return true;
        return false;
    }
    
    /**
     * @see javax.swing.TransferHandler#importData(javax.swing.JComponent, java.awt.datatransfer.Transferable)
     */
    @Override
    public boolean importData(JComponent comp, Transferable t) {
        try {
            if (!t.isDataFlavorSupported(GUID_FLAVOR)) return false;
            
            // Get the token and it's current position
            InitiativeList list = panel.getList(); 
            Token token = list.getZone().getToken((GUID)t.getTransferData(GUID_FLAVOR));
            JList displayList = (JList)comp;
            int newIndex = displayList.getSelectedIndex();
            list.moveToken(token, newIndex);            
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
        return new GUIDTransferable(ti.getId());
    }
        
    /*---------------------------------------------------------------------------------------------
     * GUIDTransferable Inner Class
     *-------------------------------------------------------------------------------------------*/

    /**
     * Transferable for token identifiers.
     * 
     * @author Jay
     */
    public static class GUIDTransferable implements Transferable {

        /**
         * Transferred id.
         */
        GUID id;
        
        /**
         * Build the transferable.
         * 
         * @param anId The id of the token being transferred.
         */
        public GUIDTransferable(GUID anId) {
            id = anId;
        }
        
        /**
         * @see java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer.DataFlavor)
         */
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (GUID_FLAVOR.equals(flavor)) {
                return id;
            }
            LOGGER.warning("Can't support flavor: " + flavor.getHumanPresentableName());
            throw new UnsupportedFlavorException(flavor);
        }

        /**
         * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
         */
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[] {GUID_FLAVOR};
        }

        /**
         * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(java.awt.datatransfer.DataFlavor)
         */
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return GUID_FLAVOR.equals(flavor);
        }
    }
}