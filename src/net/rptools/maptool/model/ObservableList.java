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
package net.rptools.maptool.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Observable;

// TODO: Make this class implement 'List'
public class ObservableList<K> extends Observable  {

    private List<K> list;
    
    public enum Event {
        add,
        remove,
        clear,
    }

    public ObservableList() {
        list = new ArrayList<K>();
    }
    
    public ObservableList(List<K> list) {
        assert list != null : "List cannot be null";
        
        this.list = list;
    }

    public List<K> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }
    
    public void sort(Comparator<K> comparitor) {
        Collections.sort(list, comparitor);
    }
    
    public boolean contains(K item) {
        return list.contains(item);
    }
    
    public K get(int i) {
        return list.get(i);
    }
    
    public void add(K item) {
        list.add(item);
        fireUpdate(Event.add, item);
    }

    public void add(int index, K element) {
        list.add(index, element);
        fireUpdate(Event.add, element);
    }
    
    public void remove(K item) {
        list.remove(item);
        fireUpdate(Event.remove, item);
    }
    
    public void remove(int i) {
        K source = list.remove(i);
        fireUpdate(Event.remove, source);
    }
    
    public void clear() {
        list.clear();
        fireUpdate(Event.clear, null);
    }
    
    public int size() {
        return list.size();
    }
    
    ////
    // INTERNAL
    protected void fireUpdate(Event event, K source) {
        setChanged();
        notifyObservers(event);
    }

    public class ObservableEvent {
        private Event event;
        private K source;
        
        public ObservableEvent(Event event, K  source) {
            this.event = event;
            this.source = source;
        }
        
        public Event getEvent() {
            return event;
        }
        
        public K getSource() {
            return source;
        }
    }
}
