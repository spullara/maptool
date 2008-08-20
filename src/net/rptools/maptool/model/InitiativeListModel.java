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
package net.rptools.maptool.model;

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.AbstractListModel;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.InitiativeList.TokenInitiative;
import net.rptools.maptool.model.Token.Type;

/**
 * This implements a list model for the for the panel. It removes all of the tokens that aren't
 * visible to players if needed.
 * 
 * @author Jay
 */
public class InitiativeListModel extends AbstractListModel implements PropertyChangeListener {

    /*---------------------------------------------------------------------------------------------
     * Instance Variables 
     *-------------------------------------------------------------------------------------------*/
    
    /**
     * All of the tokens for this initiative list.
     */
    private InitiativeList list;
    
    /*---------------------------------------------------------------------------------------------
     * Instance Methods 
     *-------------------------------------------------------------------------------------------*/
        
    /**
     * Get the token with the current initiative. Handle GM vs. Player
     * 
     * @return The current token displayed to the user or <code>null</code> if there is no current token. 
     * May be different for GM and Player
     */
    public TokenInitiative getCurrentTokenInitiative() {
        if (list.getCurrent() < 0) return null;
        if (MapTool.getPlayer() == null || MapTool.getPlayer().isGM())
          return list.getTokenInitiative(list.getCurrent());
        TokenInitiative visible = null;
        for (int i = 0; i <= list.getCurrent(); i++) {
            TokenInitiative ti = list.getTokenInitiative(i);
            if (isTokenVisible(ti.getToken(), list.isHideNPC())) visible = ti;
        } // endfor
        return visible;
    }    
    
    /**
     * Get the display index for the token at the passed list index
     * 
     * @param index The list index of a token;
     * @return The index in the display model or -1 if the item is not displayed.
     */
    public int getDisplayIndex(int index) {
        if (index < 0 || MapTool.getPlayer() == null || MapTool.getPlayer().isGM())
            return index;
        if (!isTokenVisible(list.getToken(index), list.isHideNPC())) return -1;
        int found = -1;
        for (int i = 0; i <= index; i++)
            if (isTokenVisible(list.getToken(i), list.isHideNPC())) found += 1;
        return found;
    }
        
    /** @return Getter for list */
    public InitiativeList getList() {
        return list;
    }

    /** @param theList Setter for the list to set */
    public void setList(InitiativeList theList) {
        
        // Remove the old list 
        int oldCount = 0;
        if (list != null) {
            list.removePropertyChangeListener(this);
            oldCount = getSize();
        } // endif
        
        // Add the new one
        list = theList;
        int newCount = 0;
        if (list != null) {
            list.addPropertyChangeListener(this);
            newCount = getSize();
        } // endif
        
        // Fire events
        if (oldCount > 0 || newCount > 0) {
            if (oldCount > newCount) {
                fireIntervalRemoved(this, newCount, oldCount - 1);
            } else if (oldCount < newCount) {
                fireIntervalAdded(this, oldCount, newCount - 1);
            } // endif
            fireContentsChanged(this, 0, Math.min(newCount, oldCount) - 1);
        } // endif
    }

    /**
     * Is the passed token displayed in the list?
     * 
     * @param token Token being displayed
     * @param hideNPC Flag indicating that NPC's are hidden.
     * @return The value <code>true</code> if this token is shown to the user.
     */
    public boolean isTokenVisible(Token token, boolean hideNPC) {
        if (MapTool.getPlayer() == null || MapTool.getPlayer().isGM()) return true;
        if (!token.isVisible()) return false;
        if (hideNPC && token.getType() == Type.NPC) return false;
        return true;
    }
    
    /*---------------------------------------------------------------------------------------------
     * PropertyChangeEvent Interface Methods 
     *-------------------------------------------------------------------------------------------*/
    
    /**
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
        
        // Handle by property name
        if (evt.getPropertyName().equals(InitiativeList.CURRENT_PROP)) {
            
            // Change the two old and new token with initiative
            int oldIndex = getDisplayIndex(((Integer)evt.getOldValue()).intValue());
            int newIndex = getDisplayIndex(((Integer)evt.getNewValue()).intValue());
            if (oldIndex != newIndex) {
                fireContentsChanged(InitiativeListModel.this, oldIndex, oldIndex);
                fireContentsChanged(InitiativeListModel.this, newIndex, newIndex);
            } // endif
        } else if (evt.getPropertyName().equals(InitiativeList.TOKENS_PROP)) {
            if (evt instanceof IndexedPropertyChangeEvent) {
                int index = ((IndexedPropertyChangeEvent)evt).getIndex();
                int displayIndex = getDisplayIndex(index);
                if (evt.getOldValue() == null && evt.getNewValue() instanceof TokenInitiative) {

                    // Inserted a token
                    if (isTokenVisible(list.getToken(index), list.isHideNPC()))
                        fireIntervalAdded(InitiativeListModel.this, displayIndex, displayIndex);
                } else if (evt.getNewValue() == null & evt.getOldValue() instanceof TokenInitiative) {

                    // Removed a token
                    if (isTokenVisible(((TokenInitiative)evt.getOldValue()).getToken(), list.isHideNPC()))
                        fireIntervalRemoved(InitiativeListModel.this, displayIndex, displayIndex);
                } else {

                    // Update a token
                    if (isTokenVisible(list.getToken(index), list.isHideNPC()))
                        fireContentsChanged(InitiativeListModel.this, displayIndex, displayIndex);
                } // endif
            } else if (evt.getPropertyName().equals(InitiativeList.HIDE_NPCS_PROP)) {
                
                // Changed visibility of NPC tokens.
                List<TokenInitiative> tokens = list.getTokens();
                int oldSize = getSize(tokens, ((Boolean)evt.getOldValue()).booleanValue());
                int newSize = getSize(tokens, ((Boolean)evt.getNewValue()).booleanValue());
                if (oldSize > newSize) {
                    fireIntervalRemoved(InitiativeListModel.this, newSize, oldSize - 1);
                } else if (newSize > oldSize) {
                    fireIntervalAdded(InitiativeListModel.this, oldSize, newSize - 1);
                } // endif
                fireContentsChanged(InitiativeListModel.this, 0, Math.min(oldSize, newSize));
            } else {

                if (evt.getOldValue() instanceof List && evt.getNewValue() instanceof List && ((List)evt.getNewValue()).isEmpty()) { 

                    // Did a clear, delete everything
                    List<TokenInitiative> tokens = (List<TokenInitiative>)evt.getOldValue();
                    fireIntervalRemoved(InitiativeListModel.this, 0, getSize(tokens, list.isHideNPC()));
                } else if (evt.getOldValue() == null && evt.getNewValue() instanceof List) {

                    // Just sorted, update everything
                    List<TokenInitiative> tokens = (List<TokenInitiative>)evt.getNewValue();
                    fireContentsChanged(InitiativeListModel.this, 0, getSize(tokens, list.isHideNPC()));
                } // endif
            } // endif
        }
    }
    
    /**
     * Get the number of visible tokens in a list;
     *  
     * @param tokens Search for visible tokens in this list.
     * @param hideNPC Should the NPC's be hidden?
     * @return The number of visible tokens.
     */
    private int getSize(List<TokenInitiative> tokens, boolean hideNPC) {
        if (tokens == null || tokens.isEmpty()) return 0;
        int size = 0;
        if (MapTool.getPlayer() == null || MapTool.getPlayer().isGM()) {
            size = tokens.size();
        } else {
            for (TokenInitiative ti : tokens)
                if (isTokenVisible(ti.getToken(), hideNPC)) size += 1; 
        }
        return size;
    }
    
    /*---------------------------------------------------------------------------------------------
     * ListModel Interface Methods 
     *-------------------------------------------------------------------------------------------*/
    
    /**
     * Get the token initiative at the passed display index. Handle GM vs. Player
     * 
     * @see javax.swing.ListModel#getElementAt(int)
     */
    public Object getElementAt(int index) {
        if (MapTool.getPlayer() == null || MapTool.getPlayer().isGM())
            return list.getTokenInitiative(index);
        int found = index;
        for (int i = 0; i < list.getSize(); i++) {
            TokenInitiative ti = list.getTokenInitiative(i);
            if (isTokenVisible(ti.getToken(), list.isHideNPC())) {
                found -= 1;
                if (found == -1) return ti;
            } // endif
        } // endfor
        throw new IndexOutOfBoundsException("Bad visible token index: " + index);
    }
    
    /**
     * Get the size of the list model, handle GM vs. Player.
     * 
     * @see javax.swing.ListModel#getSize()
     */
    public int getSize() {
        if (MapTool.getPlayer() == null || MapTool.getPlayer().isGM())
          return list.getSize();
        int size = 0;
        for (int i = 0; i < list.getSize(); i++) {
            TokenInitiative ti = list.getTokenInitiative(i);
            if (isTokenVisible(ti.getToken(), list.isHideNPC())) size += 1;
        } // endfor
        return size;
    }
}
