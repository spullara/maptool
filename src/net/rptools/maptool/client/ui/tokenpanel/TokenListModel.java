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
        	tokenList.addAll(zone.getTokens());
        } else {
        	for (Token token : zone.getTokens()) {
        		if (zone.isTokenVisible(token)) {
        			tokenList.add(token);
        		}
        	}
        }

        for (ListIterator<Token> iter = tokenList.listIterator(); iter.hasNext();) {
        	
        	if (iter.next().isStamp()) {
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
