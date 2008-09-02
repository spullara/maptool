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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

// TODO: Make this class implement 'List'
public class ObservableList<K> extends Observable implements Iterable {

    private List<K> list;
    
    public enum Event {
        add,
        append,
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

    public int indexOf(K item) {
    	return list.indexOf(item);
    }

    public K get(int i) {
        return list.get(i);
    }
    
    public void add(K item) {
        list.add(item);
        fireUpdate(Event.append, item);
    }

    public void add(int index, K element) {
        list.add(index, element);
        fireUpdate((index == list.size() ? Event.append : Event.add), element);
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
    
    /**
     * Get an iterator over the items in the list.
     * 
     * @return An iterator over the displayed list.
     */
    public Iterator<K> iterator() {
      return list.iterator(); 
    }
}
