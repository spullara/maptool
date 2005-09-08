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
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;

public class TokenTreeModel implements TreeModel {

    private List<TreeModelListener> listenerList = new CopyOnWriteArrayList<TreeModelListener>();
    private Object root = new String("");

    private Zone zone;
    private List<Token> tokenList;
    
    public TokenTreeModel() {
        this(null);
    }
    public TokenTreeModel(Zone zone) {
        this.zone = zone;
    }

    public void update() {
        tokenList = new ArrayList<Token>();
        
        if (zone == null) {
            return;
        }
        
        tokenList.addAll(zone.getTokens());
        
        Collections.sort(tokenList, new Comparator<Token>(){
           public int compare(Token o1, Token o2) {
                return o1.getName().compareTo(o2.getName());
            } 
        });
        
        fireStructureChangedEvent(new TreeModelEvent(this, new Object[]{getRoot()}));
    }
    
    private List<Token> getTokenList() {
        if (tokenList == null) {
            update();
        }
        
        return tokenList;
    }
    
    public Object getRoot() {
        return root;
    }

    public Object getChild(Object parent, int index) {

        // There is only one level atm
        return getTokenList().get(index);
    }

    public int getChildCount(Object parent) {
        return getTokenList().size();
    }

    public boolean isLeaf(Object node) {

        return node != root;
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
        // no-op
    }

    public int getIndexOfChild(Object parent, Object child) {

        return getTokenList().indexOf(child);
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
     */
    public void addTreeModelListener(TreeModelListener l) {
        listenerList.add(l);
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeModel#removeTreeModelListener(javax.swing.event.TreeModelListener)
     */
    public void removeTreeModelListener(TreeModelListener l) {
        listenerList.remove(l);
    }

    private void fireStructureChangedEvent(TreeModelEvent e) {
        TreeModelListener[] listeners = listenerList.toArray(new TreeModelListener[listenerList.size()]);
        for (TreeModelListener listener : listeners) {
            listener.treeStructureChanged(e);
        }
    }

    private void fireNodesInsertedEvent(TreeModelEvent e) {
        TreeModelListener[] listeners = listenerList.toArray(new TreeModelListener[listenerList.size()]);
        for (TreeModelListener listener : listeners) {
            listener.treeNodesInserted(e);
        }
    }
}
