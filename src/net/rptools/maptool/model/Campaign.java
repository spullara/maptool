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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.rptools.maptool.model.drawing.Overlay;


/**
 * This object contains {@link Zone}s and {@link Asset}s that make up a campaign.
 * Roughly this is equivalent to multiple tabs that will appear on the client and
 * all of the images that will appear on it.
 */
public class Campaign {
    private GUID id = new GUID();
    private Map<GUID, Zone> zones = Collections.synchronizedMap(new LinkedHashMap<GUID, Zone>());
    private Map<GUID, Overlay> overlays = Collections.synchronizedMap(new HashMap<GUID, Overlay>());
    private Map<GUID, Asset> assets = Collections.synchronizedMap(new HashMap<GUID, Asset>());

    public GUID getId() {
        return id;
    }

    public void setId(GUID id) {
        this.id = id;
    }

    public List<Zone> getZones() {
        return new ArrayList<Zone>(zones.values());
    }

    public Zone getZone(GUID id) {
        return zones.get(id);
    }

    public void putZone(Zone zone) {
        zones.put(zone.getId(), zone);
        putOverlay(new Overlay(zone.getId()));
    }

    public void removeZone(GUID id) {
        zones.remove(id);
        removeOverlay(id);
    }
    
    public Overlay getOverlay(GUID id) {
        return overlays.get(id);
    }
    
    protected void putOverlay(Overlay overlay) {
        overlays.put(overlay.getId(), overlay);
    }
    
    protected void removeOverlay(GUID id) {
        overlays.remove(id);
    }

    public void putAsset(Asset asset) {
        assets.put(asset.getId(), asset);
    }
    
    public Asset getAsset(GUID id) {
        return assets.get(id);
    }

    public void removeAsset(GUID id) {
        assets.get(id);
    }
}
