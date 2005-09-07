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
package net.rptools.maptool.client.ui;

import java.awt.Graphics2D;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.SwingUtilities;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.swing.Animatable;
import net.rptools.maptool.client.swing.AnimationManager;
import net.rptools.maptool.util.GraphicsUtil;

public class EventOverlay implements ZoneOverlay, Animatable {

    private List<EventDetail> eventList = new CopyOnWriteArrayList<EventDetail>();

    // TODO: make this configurable
    private static final int MESSAGE_DELAY = 3000; 

    public EventOverlay () {
        AnimationManager.addAnimatable(this);
    }
    
    public void paintOverlay(ZoneRenderer renderer, Graphics2D g) {

        int y = 10;
        for (EventDetail detail : eventList) {
            
            GraphicsUtil.drawBoxedString(g, detail.message, 10, y, SwingUtilities.LEFT);
            
            y += 15;
        }
    }

    public void addEvent(String message) {
        eventList.add(new EventDetail(message));
    }
    
    private static class EventDetail {
        
        public long timestamp;
        public String message;
        
        public EventDetail(String message) {
            this.message = message;
            timestamp = System.currentTimeMillis();
        }
    }
    
    ////
    // ANIMATABLE
    public void animate() {

        boolean requiresRepaint = false;
        while (eventList.size() > 0) {
            
            EventDetail detail = eventList.get(0);
            if (System.currentTimeMillis() - detail.timestamp > MESSAGE_DELAY) {

                eventList.remove(0);
                requiresRepaint = true;
            } else {
                break;
            }
        }
        if (requiresRepaint) {
            ZoneRenderer renderer = MapTool.getFrame().getCurrentZoneRenderer();
            if (renderer != null) {
                renderer.repaint();
            }
        }
    }

}
