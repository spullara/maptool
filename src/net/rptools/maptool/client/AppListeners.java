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
package net.rptools.maptool.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.rptools.maptool.model.Zone;

/**
 */
public class AppListeners {

	private static List<ZoneActivityListener> zoneListenerList = Collections.synchronizedList(new ArrayList<ZoneActivityListener>());
	private static List<PreferencesListener> preferencesListenerList = Collections.synchronizedList(new ArrayList<PreferencesListener>());
	
	public static void addZoneListener(ZoneActivityListener listener) {
		zoneListenerList.add(listener);
	}
	
	public static boolean removeZoneListener(ZoneActivityListener listener) {
		return zoneListenerList.remove(listener);
	}
	
	public static void fireZoneAdded(Zone zone) {
		
		for (ZoneActivityListener listener : zoneListenerList) {
			listener.zoneAdded(zone);
		}
	}
	
	public static void fireZoneActivated(Zone zone) {
		
		for (ZoneActivityListener listener : zoneListenerList) {
			listener.zoneActivated(zone);
		}
	}
	
	public static void addPreferencesListener(PreferencesListener listener) {
		preferencesListenerList.add(listener);
	}
	
	public boolean removePreferencesListener(PreferencesListener listener) {
		return preferencesListenerList.remove(listener);
	}
	
	public static void firePreferencesUpdated() {
		for (PreferencesListener listener : preferencesListenerList) {
			listener.preferencesUpdated();
		}
	}
}
