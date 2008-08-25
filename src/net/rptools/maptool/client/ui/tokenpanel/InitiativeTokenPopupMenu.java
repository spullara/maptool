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

import java.awt.event.ActionEvent;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.TokenPopupMenu;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.language.I18N;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.InitiativeList;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.ZonePoint;
import net.rptools.maptool.model.InitiativeList.TokenInitiative;

/**
 * The popup menu for initiative tokens.
 * 
 * @author Jay
 */
public class InitiativeTokenPopupMenu extends TokenPopupMenu {

    /**
     * The token initiative instances selected by the user.
     */
    private Set<TokenInitiative> selectedTokenInitiatives;
    
    /**
     * The token initiative under the mouse is used for commands that work on a single token init.
     */
    private TokenInitiative tokenInitiativeUnderMouse;
    
    /**
     * @param selectedTokenSet Tokens handled by this menu
     * @param selectedInitiatives TokenIntiatives handled by this menu
     * @param x X location of the menu
     * @param y Y location of the menu
     * @param renderer Renderer of the map
     * @param tokenUnderMouse The token under the mouse provides default values.
     * @param tokenInitUnderMouse The initiative item under the mouse.
     */
    public InitiativeTokenPopupMenu(Set<GUID> selectedTokenSet, Set<TokenInitiative> selectedInitiatives,  int x, int y, 
            ZoneRenderer renderer, Token tokenUnderMouse, TokenInitiative tokenInitUnderMouse) {
        super(selectedTokenSet, x, y, renderer, tokenUnderMouse);
        removeAll();
        selectedTokenInitiatives = selectedInitiatives;
        tokenInitiativeUnderMouse = tokenInitUnderMouse;

        // Build the menu
        InitiativePanel ip = MapTool.getFrame().getInitiativePanel();
        I18N.setAction("initPanel.toggleHold", TOGGLE_HOLD_ACTION);
        addOwnedItem(TOGGLE_HOLD_ACTION);
        I18N.setAction("initPanel.setInitState", SET_INIT_STATE_VALUE);
        addOwnedItem(SET_INIT_STATE_VALUE);
        I18N.setAction("initPanel.clearInitState", CLEAR_INIT_STATE_VALUE);
        addOwnedItem(CLEAR_INIT_STATE_VALUE);
        I18N.setAction("initPanel.remove", REMOVE_TOKEN_ACTION);
        if (ip.hasGMPermission() || ip.isOwnerPermissions()) {
            add(new JMenuItem(REMOVE_TOKEN_ACTION));
            REMOVE_TOKEN_ACTION.setEnabled(ip.hasOwnerPermission(tokenUnderMouse));
        } // endif
        if (ip.hasGMPermission()) addSeparator();
        I18N.setAction("initPanel.moveUp", MOVE_UP_ACTION);
        addGMItem(MOVE_UP_ACTION);
        I18N.setAction("initPanel.moveDown", MOVE_DOWN_ACTION);
        addGMItem(MOVE_DOWN_ACTION);
        addSeparator();
        I18N.setAction("initPanel.center", CENTER_ACTION);
        add(CENTER_ACTION);
        addOwnedItem(new ImpersonateAction());
        addOwnedItem(createMacroMenu());
        addOwnedItem(createSpeechMenu());
        addOwnedItem(createStateMenu());
        addOwnedItem(createHaloMenu());
        
        // Do move up or move down need to be disabled
        InitiativeList list = getRenderer().getZone().getInitiativeList();
        int index = list.indexOf(tokenInitUnderMouse);
        if (index == 0) MOVE_UP_ACTION.setEnabled(false);
        if (index == list.getSize() - 1) MOVE_DOWN_ACTION.setEnabled(false);
    }
    
    /**
     * This action will turn the selected token's initiative on and off.
     */
    public final Action TOGGLE_HOLD_ACTION = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            for (TokenInitiative ti : selectedTokenInitiatives)
                ti.setHolding(!ti.isHolding());
        };
    };

    /**
     * This action will set the initiative state of the currently selected token.
     */
    public final Action SET_INIT_STATE_VALUE = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            String message = I18N.getText("initPanel.enterState");
            String defaultValue = "";
            if (selectedTokenInitiatives.size() == 1) {
                TokenInitiative ti = selectedTokenInitiatives.iterator().next();
                String sName = ti.getToken().getName();
                if (MapTool.getPlayer().isGM() && ti.getToken().getGMName() != null)
                    sName += " (" + ti.getToken().getGMName() + ")";
                message = String.format(I18N.getText("initPanel.enterState"), sName);
                defaultValue = ti.getState();
            } // endif
            String input = JOptionPane.showInputDialog(message, defaultValue);
            if (input == null) return;
            input = input.trim();
            for (TokenInitiative ti : selectedTokenInitiatives)
                ti.setState(input.trim());
        };
    };

    /**
     * This action will set the initiative state of the currently selected token.
     */
    public final Action CLEAR_INIT_STATE_VALUE = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            for (TokenInitiative ti : selectedTokenInitiatives)
                ti.setState(null);
        };
    };

    /**
     * This action will remove the selected token from the list.
     */
    public final Action REMOVE_TOKEN_ACTION = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            InitiativeList list = getRenderer().getZone().getInitiativeList();
            InitiativePanel ip = MapTool.getFrame().getInitiativePanel();
            for (TokenInitiative ti : selectedTokenInitiatives) {
                if (ip.hasOwnerPermission(ti.getToken())) {
                  int index = list.indexOf(ti);
                  list.removeToken(index);
                } // endif
            } // endfor
        };
    };
    
    /**
     * This action will move a token up one space
     */
    public final Action MOVE_UP_ACTION = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            InitiativeList list = getRenderer().getZone().getInitiativeList();
            int index = list.indexOf(tokenInitiativeUnderMouse);
            list.moveToken(index, index - 1);
        };
    };    
    
    /**
     * This action will move a token up one space
     */
    public final Action MOVE_DOWN_ACTION = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            InitiativeList list = getRenderer().getZone().getInitiativeList();
            int index = list.indexOf(tokenInitiativeUnderMouse);
            list.moveToken(index, index + 2);
        };
    };

    /**
     * This action will center the selected token on the map and select it.
     */
    public final Action CENTER_ACTION = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            Token token = tokenInitiativeUnderMouse.getToken();
            getRenderer().centerOn(new ZonePoint(token.getX(), token.getY()));
            getRenderer().clearSelectedTokens();
            getRenderer().selectToken(token.getId());
        };
    };
}
