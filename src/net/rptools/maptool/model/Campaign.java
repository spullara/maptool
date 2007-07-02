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
import java.util.Set;
import java.util.Map.Entry;

import net.rptools.lib.MD5Key;


/**
 * This object contains {@link Zone}s and {@link Asset}s that make up a campaign.
 * Roughly this is equivalent to multiple tabs that will appear on the client and
 * all of the images that will appear on it.
 */
public class Campaign {
	
	public static final String DEFAULT_TOKEN_PROPERTY_TYPE = "Basic";
	
    private GUID id = new GUID();
    private Map<GUID, Zone> zones = Collections.synchronizedMap(new LinkedHashMap<GUID, Zone>());
    private ExportInfo exportInfo;
    private Map<String, List<TokenProperty>> tokenTypeMap;
    
    private transient boolean isBeingSerialized;

    public Campaign() {
    	// No op
    }

    public Campaign (Campaign campaign) {

    	zones = Collections.synchronizedMap(new LinkedHashMap<GUID, Zone>());
    	for (Entry<GUID, Zone> entry : campaign.zones.entrySet()) {
    		Zone copy = new Zone(entry.getValue());
    		zones.put(copy.getId(), copy);
    	}
    	if (tokenTypeMap != null) {
        	tokenTypeMap = new HashMap<String, List<TokenProperty>>(); 
        	for (Entry<String, List<TokenProperty>> entry : campaign.tokenTypeMap.entrySet()) {

        		List<TokenProperty> typeList = new ArrayList<TokenProperty>();
        		typeList.addAll(tokenTypeMap.get(entry.getKey()));

        		tokenTypeMap.put(entry.getKey(), typeList);
        	}
    		
    	}
    }
    
    public GUID getId() {
        return id;
    }

    /**
     * This is a workaround to avoid the renderer and the serializer interating on the drawables at the same time
     */
    public boolean isBeingSerialized() {
		return isBeingSerialized;
	}



    /**
     * This is a workaround to avoid the renderer and the serializer interating on the drawables at the same time
     */
	public void setBeingSerialized(boolean isBeingSerialized) {
		this.isBeingSerialized = isBeingSerialized;
	}



	public List<String> getTokenTypes() {
    	List<String> list = new ArrayList<String>();
    	list.addAll(getTokenTypeMap().keySet());
    	Collections.sort(list);
    	return list;
    }

    public List<TokenProperty> getTokenPropertyList(String tokenType) {
    	return getTokenTypeMap().get(tokenType);
    }
    
    public void putTokenType(String name, List<TokenProperty> propertyList) {
    	getTokenTypeMap().put(name, propertyList);
    }
    
    private Map<String, List<TokenProperty>> getTokenTypeMap() {
    	if (tokenTypeMap == null) {
    		tokenTypeMap = new HashMap<String, List<TokenProperty>>();
    		tokenTypeMap.put(DEFAULT_TOKEN_PROPERTY_TYPE, createBasicPropertyList());
    	}
    	return tokenTypeMap;
    }
    
    private List<TokenProperty> createBasicPropertyList() {
    	List<TokenProperty> list = new ArrayList<TokenProperty>();
    	list.add(new TokenProperty("Strength", "Str"));
    	list.add(new TokenProperty("Dexterity", "Dex"));
    	list.add(new TokenProperty("Constitution", "Cons"));
    	list.add(new TokenProperty("Intelligence", "Int"));
    	list.add(new TokenProperty("Wisdom", "Wis"));
    	list.add(new TokenProperty("Charisma", "Char"));
    	list.add(new TokenProperty("HP", true, true));
    	list.add(new TokenProperty("AC", true, true));
    	list.add(new TokenProperty("Defense", "Def"));
    	list.add(new TokenProperty("Movement", "Move"));
    	list.add(new TokenProperty("Elevation", "Elev", true, false));
    	return list;
    }
    
    public void setExportInfo(ExportInfo exportInfo) {
    	this.exportInfo = exportInfo;
    }
    
    public ExportInfo getExportInfo() {
    	return exportInfo;
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
    }

    public void removeAllZones() {
    	zones.clear();
    }
    
    public void removeZone(GUID id) {
        zones.remove(id);
    }

    public boolean containsAsset(Asset asset) {
    	return containsAsset(asset.getId());
    }
    
    public boolean containsAsset(MD5Key key) {
    	
    	for (Zone zone : zones.values()) {

    		Set<MD5Key> assetSet = zone.getAllAssetIds();
    		if (assetSet.contains(key)) {
    			return true;
    		}
    	}
    	
    	return false;
    }
	
}
