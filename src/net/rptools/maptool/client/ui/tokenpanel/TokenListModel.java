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
package net.rptools.maptool.client.ui.tokenpanel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;

public class TokenListModel implements ListModel {

    private List<ListDataListener> listenerList = new CopyOnWriteArrayList<ListDataListener>();

    private Zone zone;
    private List<Token> tokenList;

    public TokenListModel () {
        this(null);
    }
    public TokenListModel(Zone zone) {
        this.zone = zone;
    }
    
    public int getSize() {
        return getTokenList().size();
    }

    public Object getElementAt(int index) {
        return getTokenList().get(index);
    }

    public void addListDataListener(ListDataListener l) {
        listenerList.add(l);
    }

    public void removeListDataListener(ListDataListener l) {
        listenerList.remove(l);
    }

    public void update() {
        tokenList = new ArrayList<Token>();
        
        if (zone == null) {
            return;
        }
        
        if (MapTool.getPlayer().isGM()) {
        	tokenList.addAll(zone.getAllTokens());
        } else {
        	for (Token token : zone.getAllTokens()) {
        		if (zone.isTokenVisible(token)) {
        			tokenList.add(token);
        		}
        	}
        }

        for (ListIterator<Token> iter = tokenList.listIterator(); iter.hasNext();) {
        	
        	if (iter.next().isObjectStamp()) {
        		iter.remove();
        	}
        }
        
        Collections.sort(tokenList, new Comparator<Token>(){
           public int compare(Token o1, Token o2) {
               String lName = o1.getName();
               String rName = o2.getName();

               return lName.compareTo(rName);
            } 
        });
        
        fireContentsChangedEvent(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, tokenList.size()));
    }
    
    private void fireContentsChangedEvent(ListDataEvent e) {

        for (ListDataListener listener : listenerList) {
            listener.contentsChanged(e);
        }
    }

    private List<Token> getTokenList() {
        if (tokenList == null) {
            update();
        }
        
        return tokenList;
    }
}
