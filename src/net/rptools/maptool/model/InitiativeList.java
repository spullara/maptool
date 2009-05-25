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
package net.rptools.maptool.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import javax.swing.Icon;

import net.rptools.maptool.client.MapTool;

/**
 * All of the tokens currently being shown in the initiative list. It includes a reference to all
 * the tokens in order, a reference to the current token, a displayable initiative value and a
 * hold state for each token.
 * 
 * @author Jay
 */
public class InitiativeList implements Serializable {

    /*---------------------------------------------------------------------------------------------
     * Instance Variables 
     *-------------------------------------------------------------------------------------------*/
    
    /**
     * The tokens and their order within the initiative
     */
    private List<TokenInitiative> tokens = new ArrayList<TokenInitiative>();

    /**
     * The token in the list which currently has initiative.
     */
    private int current = -1;
    
    /**
     * The current round for initiative.
     */
    private int round = -1;
    
    /**
     * Used to add property change support to the round and current values.
     */
    private transient PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    /**
     * The zone that owns this initiative list.
     */
    private transient Zone zone;
    
    /**
     * The id of the zone that owns this initiative list, used for persistence
     */
    private GUID zoneId;
    
    /**
     * Hold the update when this variable is greater than 0. Some methods need to call 
     * {@link #updateServer()} when they are called, but they also get called by other 
     * methods that update the server. This keeps it from happening multiple times.
     */
    private transient int holdUpdate;
    
    /**
     * Hide all of the NPC's from the players.
     */
    private boolean hideNPC;
    
    /*---------------------------------------------------------------------------------------------
     * Class Variables
     *-------------------------------------------------------------------------------------------*/
    
    /**
     * Name of the tokens property passed in {@link PropertyChangeEvent}s.
     */
    public static final String TOKENS_PROP = "tokens";
    
    /**
     * Name of the round property passed in {@link PropertyChangeEvent}s.
     */
    public static final String ROUND_PROP = "round";
    
    /**
     * Name of the current property passed in {@link PropertyChangeEvent}s.
     */
    public static final String CURRENT_PROP = "current";
    
    /**
     * Name of the hide NPCs property passed in {@link PropertyChangeEvent}s.
     */
    public static final String HIDE_NPCS_PROP = "hideNPCs";
    
    /**
     * Name of the owner permission property passed in {@link PropertyChangeEvent}s.
     */
    public static final String OWNER_PERMISSIONS_PROP = "ownerPermissions";
    
    /*---------------------------------------------------------------------------------------------
     * Constructor
     *-------------------------------------------------------------------------------------------*/
        
    /**
     * Create an initiative list for a zone.
     * 
     * @param aZone The zone that owns this initiative list.
     */
    public InitiativeList(Zone aZone) {
        setZone(aZone);
    }
    
    /*---------------------------------------------------------------------------------------------
     * Instance Methods 
     *-------------------------------------------------------------------------------------------*/
        
    /**
     * Get the token initiative data at the passed index. Allows the other state to be set.
     * 
     * @param index Index of the token initiative data needed. 
     * @return The token initiative data for the passed index.
     */
    public TokenInitiative getTokenInitiative(int index) {
        return index >= 0 ? tokens.get(index) : null;
    }

    /**
     * Get the number of tokens in this list.
     * 
     * @return Number of tokens
     */
    public int getSize() {
        return tokens.size();
    }
    
    /**
     * Get the token at the passed index.
     * 
     * @param index Index of the token needed. 
     * @return The token for the passed index.
     */
    public Token getToken(int index) {
        return tokens.get(index).getToken();
    }

    /**
     * Insert a new token into the initiative.
     * 
     * @param index Insert the token here.
     * @param token Insert this token.
     * @return The token initiative value that holds the token.
     */
    public TokenInitiative insertToken(int index, Token token) {
        if (index == -1)
        	index = tokens.size();
        TokenInitiative ti = new TokenInitiative(token);
        tokens.add(index, ti);
        holdUpdate += 1;
        if (index < current)
        	setCurrent(current + 1);
        getPCS().fireIndexedPropertyChange(TOKENS_PROP, index, null, ti);
        holdUpdate -= 1;
        updateServer();
        return ti;
    }
    
    /**
     * Insert a new token into the initiative.
     * 
     * @param tokens Insert these tokens.
     */
    public void insertTokens(List<Token>  tokens) {
        holdUpdate += 1;
        for (Token token : tokens)
        	insertToken(-1, token);
        holdUpdate -= 1;
        updateServer();
    }
    
    /**
     * Find the index of the passed token.
     * 
     * @param token Search for this token.
     * @return A list of the indexes found for the listed token
     */
    public List<Integer> indexOf(Token token) {
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < tokens.size(); i++)
            if (token.equals(tokens.get(i).getToken()))
            	list.add(i);
        return list;
    }
    
    /**
     * Find the index of the passed token initiative.
     * 
     * @param ti Search for this token initiative instance
     * @return The index of the token initiative that was found or -1 if the token initiative was not found;
     */
    public int indexOf(TokenInitiative ti) {
        for (int i = 0; i < tokens.size(); i++)
            if (tokens.get(i).equals(ti))
            	return i;
        return -1;
    }
    
    /**
     * Remove a token from the initiative.
     * 
     * @param index Remove the token at this index.
     * @return The token that was removed.
     */
    public Token removeToken(int index) {
        TokenInitiative ti = tokens.remove(index);
        holdUpdate += 1;
        Token old = ti.getToken();        
        if (index <= current)
        	current -= 1;
        if (tokens.size() <= current)
        	current = -1;
        setCurrent(current);
        getPCS().fireIndexedPropertyChange(TOKENS_PROP, index, ti, null);
        holdUpdate -= 1;
        updateServer();
        return old; 
    }
    
    /** @return Getter for current */
    public int getCurrent() {
        return current;
    }
    
    /** @param aCurrent Setter for the current to set */
    public void setCurrent(int aCurrent) {
        if (current == aCurrent)
        	return;
        if (aCurrent < 0 || aCurrent >= tokens.size())
        	aCurrent = -1; // Don't allow bad values
        int old = current;
        current = aCurrent;
        getPCS().firePropertyChange(CURRENT_PROP, old, current);
        updateServer();
    }
    
    /**
     * Go to the next token in initiative order.
     */
    public void nextInitiative() {
        if (tokens.isEmpty())
        	return;
        holdUpdate += 1;
        int newRound = (round < 0) ? 1 : (current + 1 >= tokens.size()) ? round + 1 : round;
        int newCurrent = (current < 0 || current + 1 >= tokens.size()) ? 0 : current + 1;
        setCurrent(newCurrent);
        setRound(newRound);
        holdUpdate -= 1;
        updateServer();
    }

    /** @return Getter for round */
    public int getRound() {
        return round;
    }

    /** @param aRound Setter for the round to set */
    public void setRound(int aRound) {
        if (round == aRound)
        	return;
        int old = round;
        round = aRound;
        getPCS().firePropertyChange(ROUND_PROP, old, aRound);
        updateServer();
    }
    
    /**
     * Add a listener to any property change.
     * 
     * @param listener The listener to be added.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        getPCS().addPropertyChangeListener(listener);
    }

    /**
     * Add a listener to the given property name
     * 
     * @param propertyName Add the listener to this property name.
     * @param listener The listener to be added.
     */
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        getPCS().addPropertyChangeListener(propertyName, listener);
    }

    /**
     * Remove a listener for all property changes.
     * 
     * @param listener The listener to be removed.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        getPCS().removePropertyChangeListener(listener);
    }

    /**
     * Remove a listener from a given property name
     * 
     * @param propertyName Remove the listener from this property name.
     * @param listener The listener to be removed.
     */
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        getPCS().removePropertyChangeListener(propertyName, listener);
    }
    
    /**
     * Start a new unit of work.
     */
    public void startUnitOfWork() {
        holdUpdate += 1;
    }
    
    /**
     * Finish the current unit of work and update the server.
     */
    public void finishUnitOfWork() {
        holdUpdate -= 1;
        // XXX Shouldn't this next line be prefixed with if(holdUpdate < 1)?
        updateServer();
    }
    
    /**
     * Remove all of the tokens from the model and clear round and current 
     */
    public void clearModel() {
        if (current == -1 && round == -1 && tokens.isEmpty())
        	return;
        holdUpdate += 1;
        setCurrent(-1);
        setRound(-1);
        if (!tokens.isEmpty()) {
            List<TokenInitiative> old = tokens;
            tokens = new ArrayList<TokenInitiative>();
            getPCS().firePropertyChange(TOKENS_PROP, old, tokens);
        } // endif
        holdUpdate -= 1;
        updateServer();
    }

    /**
     * Updates occurred to the tokens. 
     */
    public void update() {
        
        // No zone, no tokens
        if (getZone() == null) {
            clearModel();
            return;
        } // endif
        
        // Remove deleted tokens
        holdUpdate += 1;
        boolean updateNeeded = false;
        ListIterator<TokenInitiative> i = tokens.listIterator();
        while (i.hasNext()) {
            TokenInitiative ti = i.next();
            if (getZone().getToken(ti.getId()) == null) {
                int index = tokens.indexOf(ti);
                if (index <= current)
                	setCurrent(current - 1);
                i.remove();
                updateNeeded = true;
                getPCS().fireIndexedPropertyChange(TOKENS_PROP, index, ti, null);
            } // endif
        } // endwhile
        holdUpdate -= 1;
        if (updateNeeded)
        	updateServer();
    }
    
    /**
     * Sort the tokens by their initiative state from largest to smallest. If the initiative state string can be converted into a 
     * {@link Double} that is done first. All values converted to {@link Double}s are always considered bigger than the {@link String}
     * values. The {@link String} values are considered bigger than any <code>null</code> values.
     */
    public void sort() {
        holdUpdate += 1;
        Collections.sort(tokens, new Comparator<TokenInitiative>() {
            public int compare(TokenInitiative o1, TokenInitiative o2) {
                
                // Get a number, string, or null for first parameter
                Object one = null;
                if (o1.state != null) {
                    one = o1.state;                
                    try {
                        one = Double.valueOf(o1.state);
                    } catch (NumberFormatException e) {
                        // Not a number so ignore
                    } // endtry
                } // endif
                
                // Repeat for second param
                Object two = null;
                if (o2.state != null) {
                    two = o2.state;                
                    try {
                        two = Double.valueOf(o2.state);
                    } catch (NumberFormatException e) {
                        // Not a number so ignore
                    } // endtry
                } // endif
                
                // Do the comparison
                if (one == two || (one != null && one.equals(two)))
                	return 0;
                if (one == null)
                	return 1; // Null is always the smallest value
                if (two == null)
                	return -1;
                if (one instanceof Double & two instanceof Double)
                	return ((Double)two).compareTo((Double)one);
                if (one instanceof String & two instanceof String)
                	return ((String)two).compareTo((String)one);
                if (one instanceof Double)
                	return -1; // Integers are bigger than strings
                return 1;
            }            
        });
        getPCS().firePropertyChange(TOKENS_PROP, null, tokens);
        holdUpdate -= 1;
        updateServer();
    }
    
    /** @return Getter for zone */
    public Zone getZone() {
        if (zone == null && zoneId != null)
            zone = MapTool.getCampaign().getZone(zoneId);
        return zone;
    }

    /** @return Getter for pcs */
    private PropertyChangeSupport getPCS() {
        if (pcs == null)
        	pcs = new PropertyChangeSupport(this);
        return pcs;
    }
    
    /**
     * Move a token from it's current position to the new one.
     * 
     * @param oldIndex Move the token at this index
     * @param index To here.
     */
    public void moveToken(int oldIndex, int index) {
        
        // Remove the token from its old position
        if (oldIndex < 0 || oldIndex == index)
        	return;
        int oldCurrent = current;
        current = -1;
        holdUpdate += 1;
        TokenInitiative ti = tokens.remove(oldIndex);
        getPCS().fireIndexedPropertyChange(TOKENS_PROP, oldIndex, ti, null);
        
        // Add it at it's new position
        index -= index > oldIndex ? 1 : 0;
        tokens.add(index, ti);
        getPCS().fireIndexedPropertyChange(TOKENS_PROP, index, null, ti);
        current = oldCurrent;
        
        // Adjust the current index
        if (current >= 0 && current > oldIndex && current <= index)
            setCurrent(current - 1);
        holdUpdate -= 1;
        updateServer();
    }
    
    /**
     * Update the server with the new list
     */
    public void updateServer() {
        if (holdUpdate > 0 || zoneId == null)
        	return;
        MapTool.serverCommand().updateInitiative(this, null);
    }

    /**
     * Update the server with the new Token Initiative
     * 
     * @param ti Item to update
     */
    public void updateServer(TokenInitiative ti) {
        if (holdUpdate > 0 || zoneId == null)
            return;
        MapTool.serverCommand().updateTokenInitiative(zoneId, ti.getId(), ti.isHolding(), ti.getState(), indexOf(ti));
    }

    /** @param aZone Setter for the zone */
    public void setZone(Zone aZone) {
        zone = aZone;
        if (aZone != null) {
            zoneId = aZone.getId();
        } else {
            zoneId = null;
        } // endif
    }

    /** @return Getter for hideNPC */
    public boolean isHideNPC() {
        return hideNPC;
    }

    /** @param hide Setter for hideNPC */
    public void setHideNPC(boolean hide) {
        if (hide == hideNPC)
        	return;
        boolean old = hideNPC;
        hideNPC = hide;
        getPCS().firePropertyChange(HIDE_NPCS_PROP, old, hide);
        updateServer();
    }

    /** @return Getter for tokens */
    public List<TokenInitiative> getTokens() {
        return Collections.unmodifiableList(tokens);
    }

    /*---------------------------------------------------------------------------------------------
     * TokenInitiative Inner Class
     *-------------------------------------------------------------------------------------------*/
    
    /**
     * This class holds all of the data to describe a token w/in initiative.
     * 
     * @author Jay
     */
    public class TokenInitiative {
        
        /*---------------------------------------------------------------------------------------------
         * Instance Variables 
         *-------------------------------------------------------------------------------------------*/
        
        /**
         * The id of the token which is needed for persistence. It is immutable.
         */
        private GUID id;
        
        /**
         * Flag indicating that the token is holding it's initiative.
         */
        private boolean holding;
        
        /**
         * Optional state that can be displayed in the initiative panel. 
         */
        private String state;
        
        /**
         * Save off the icon so that it can be displayed as needed.
         */
        private transient Icon displayIcon;

        /*---------------------------------------------------------------------------------------------
         * Constructors
         *-------------------------------------------------------------------------------------------*/
        
        /**
         * Create the token initiative for the passed token.
         * 
         * @param aToken Add this token to the initiative.
         */
        public TokenInitiative(Token aToken) {
            if (aToken != null) 
            	id = aToken.getId();
        }
        
        /*---------------------------------------------------------------------------------------------
         * Instance Methods 
         *-------------------------------------------------------------------------------------------*/
        
        /** @return Getter for token */
        public Token getToken() {
            return getZone().getToken(id);
        }


        /** @return Getter for id */
        public GUID getId() {
            return id;
        }

        /** @param id Setter for the id to set */
        public void setId(GUID id) {
            this.id = id;
        }

        /** @return Getter for holding */
        public boolean isHolding() {
            return holding;
        }

        /** @param isHolding Setter for the holding to set */
        public void setHolding(boolean isHolding) {
            if (holding == isHolding)
            	return;
            boolean old = holding;
            holding = isHolding;
            getPCS().fireIndexedPropertyChange(TOKENS_PROP, tokens.indexOf(this), old, isHolding);
            updateServer();
        }

        /** @return Getter for state */
        public String getState() {
            return state;
        }

        /** @param aState Setter for the state to set */
        public void setState(String aState) {
            if (state == aState || (state != null && state.equals(aState)))
            	return;
            String old = state;
            state = aState;
            getPCS().fireIndexedPropertyChange(TOKENS_PROP, tokens.indexOf(this), old, aState);
            updateServer();
        }

        /** @return Getter for displayIcon */
        public Icon getDisplayIcon() {
            return displayIcon;
        }

        /** @param displayIcon Setter for the displayIcon to set */
        public void setDisplayIcon(Icon displayIcon) {
            this.displayIcon = displayIcon;
        }
        
        /**
         * Update the internal state w/o firing events. Needed for single token 
         * init updates. 
         * 
         * @param isHolding New holding state
         * @param aState New state
         */
        public void update(boolean isHolding, String aState) {
            boolean old = holding;
            holding = isHolding;
            String oldState = state;
            state = aState;
            getPCS().fireIndexedPropertyChange(TOKENS_PROP, tokens.indexOf(this), old, isHolding);
            getPCS().fireIndexedPropertyChange(TOKENS_PROP, tokens.indexOf(this), oldState, aState);
        }
    }
}
