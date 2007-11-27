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

import java.io.IOException;
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
    
    private CampaignProperties campaignProperties = new CampaignProperties();
    
    private transient boolean isBeingSerialized;

    // DEPRECATED: As of 1.3b20 these are now in campaignProperties, but are here for backward compatibility
    private Map<String, List<TokenProperty>> tokenTypeMap;
    private List<String> remoteRepositoryList;

    private Map<String, Map<GUID, LightSource>> lightSourcesMap;
    
    private Map<String, LookupTable> lookupTableMap;
    
    // DEPRECATED: as of 1.3b19 here to support old serialized versions
    private Map<GUID, LightSource> lightSourceMap;
    
    public Campaign() {
    	// No op
    }

    private void checkCampaignPropertyConversion() {
    	if (tokenTypeMap != null) {
    		campaignProperties.setTokenTypeMap(tokenTypeMap);
    		tokenTypeMap = null;
    	}
    	if (remoteRepositoryList != null) {
    		campaignProperties.setRemoteRepositoryList(remoteRepositoryList);
    		remoteRepositoryList = null;
    	}
    	if (lightSourcesMap != null) {
    		campaignProperties.setLightSourcesMap(lightSourcesMap);
    		lightSourcesMap = null;
    	}
    	if (lookupTableMap != null) {
    		campaignProperties.setLookupTableMap(lookupTableMap);
    		lookupTableMap = null;
    	}
    }
    
    public List<String> getRemoteRepositoryList() {
    	checkCampaignPropertyConversion(); // TODO: Remove, for compatibility 1.3b19-1.3b20
    	return campaignProperties.getRemoteRepositoryList();
    }
    
    public Campaign (Campaign campaign) {

    	zones = Collections.synchronizedMap(new LinkedHashMap<GUID, Zone>());
    	for (Entry<GUID, Zone> entry : campaign.zones.entrySet()) {
    		Zone copy = new Zone(entry.getValue());
    		zones.put(copy.getId(), copy);
    	}
    	campaignProperties = new CampaignProperties(campaign.campaignProperties);
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
    	checkCampaignPropertyConversion(); // TODO: Remove, for compatibility 1.3b19-1.3b20
    	return campaignProperties.getTokenTypeMap();
    }
    
    public Map<String, LookupTable> getLookupTableMap() {
    	checkCampaignPropertyConversion(); // TODO: Remove, for compatibility 1.3b19-1.3b20
    	return campaignProperties.getLookupTableMap();
    }
    
    public LightSource getLightSource(GUID lightSourceId) {

    	for (Map<GUID, LightSource> map : getLightSourcesMap().values()) {
    		if (map.containsKey(lightSourceId)) {
    			return map.get(lightSourceId);
    		}
    	}
    	return null;
    }

    public Map<String, Map<GUID, LightSource>> getLightSourcesMap() {
    	checkCampaignPropertyConversion(); // TODO: Remove, for compatibility 1.3b19-1.3b20
    	return campaignProperties.getLightSourcesMap();
    }
    
    public Map<GUID, LightSource> getLightSourceMap(String type) {
    	return getLightSourcesMap().get(type);
    }
    
    private List<TokenProperty> createBasicPropertyList() {
    	List<TokenProperty> list = new ArrayList<TokenProperty>();
    	list.add(new TokenProperty("Strength", "Str"));
    	list.add(new TokenProperty("Dexterity", "Dex"));
    	list.add(new TokenProperty("Constitution", "Con"));
    	list.add(new TokenProperty("Intelligence", "Int"));
    	list.add(new TokenProperty("Wisdom", "Wis"));
    	list.add(new TokenProperty("Charisma", "Char"));
    	list.add(new TokenProperty("HP", true, true));
    	list.add(new TokenProperty("AC", true, true));
    	list.add(new TokenProperty("Defense", "Def"));
    	list.add(new TokenProperty("Movement", "Mov"));
    	list.add(new TokenProperty("Elevation", "Elv", true, false));
    	list.add(new TokenProperty("Description", "Des"));
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

    public void mergeCampaignProperties(CampaignProperties properties) {
    	properties.mergeInto(campaignProperties);
    }
    
    public void replaceCampaignProperties(CampaignProperties properties) {
    	campaignProperties = new CampaignProperties(properties);
    }
    
    /**
     * Get a copy of the properties.  This is for persistence.  Modification of the properties
     * do not affect this campaign
     */
    public CampaignProperties getCampaignProperties() {
    	return new CampaignProperties(campaignProperties);
    }
}
