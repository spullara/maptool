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