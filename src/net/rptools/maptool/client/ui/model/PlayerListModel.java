/* The MIT License
 * 
 * Copyright (c) 2005 David Rice, Trevor Croft
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
package net.rptools.maptool.client.ui.model;

import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractListModel;

import net.rptools.maptool.model.ObservableList;
import net.rptools.maptool.model.Player;

public class PlayerListModel extends AbstractListModel implements Observer {
    
    private ObservableList<Player> playerList;

    public PlayerListModel(ObservableList<Player> playerList) {
        this.playerList = playerList;
        
        // TODO: Figure out how to clean this up when no longer in use
        // for now it doesn't matter, but, it's bad design
        playerList.addObserver(this);
    }
    
    public Object getElementAt(int index) {
        return playerList.get(index);
    }

    public int getSize() {
        return playerList.size();
    }

    ////
    // OBSERVER
    public void update(Observable o, Object arg) {
        fireContentsChanged(this, 0, playerList.size());
    }
}