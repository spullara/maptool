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
package net.rptools.maptool.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import net.rptools.lib.MD5Key;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.model.Campaign;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.drawing.DrawablePaint;
import net.rptools.maptool.model.drawing.DrawableTexturePaint;
import net.rptools.maptool.model.drawing.DrawnElement;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

/**
 * @author trevor
 */
public class PersistenceUtil {

	public static void saveCampaign(Campaign campaign, File campaignFile) throws IOException {
		
		// This is a veeeeeery primitive form of perstence, and will be changing very soon
		OutputStream os = new FileOutputStream(campaignFile);
		HessianOutput out = new HessianOutput(os);
		
		PersistedCampaign persistedCampaign = new PersistedCampaign();
		
		persistedCampaign.campaign = campaign;
		
		// Save all assets in active use
		for (Zone zone : campaign.getZones()) {
			
			persistedCampaign.assetMap.put(zone.getAssetID(), AssetManager.getAsset(zone.getAssetID()));
			
			for (Token token : zone.getAllTokens()) {
				
				persistedCampaign.assetMap.put(token.getAssetID(), AssetManager.getAsset(token.getAssetID()));
			}

			// Painted textures
			for (DrawnElement drawn : zone.getDrawnElements()) {
    			DrawablePaint paint = drawn.getPen().getPaint(); 
    			if (paint instanceof DrawableTexturePaint) {
    				MD5Key assetId = ((DrawableTexturePaint)paint).getAssetId();
    				Asset asset = ((DrawableTexturePaint)paint).getAsset();
    				persistedCampaign.assetMap.put(assetId, asset);
    			}
    			
    			paint = drawn.getPen().getBackgroundPaint();
    			if (paint instanceof DrawableTexturePaint) {
    				MD5Key assetId = ((DrawableTexturePaint)paint).getAssetId();
    				Asset asset = ((DrawableTexturePaint)paint).getAsset();
    				persistedCampaign.assetMap.put(assetId, asset);
    			}
			}
		}

		out.writeObject(persistedCampaign);
		os.close();		
	}
	
	public static Campaign loadCampaign(File campaignFile) throws IOException {
		
		InputStream is = new FileInputStream(campaignFile);
		HessianInput in = new HessianInput(is);

		PersistedCampaign persistedCampaign = (PersistedCampaign) in.readObject(null);
		is.close();

		for (MD5Key key : persistedCampaign.assetMap.keySet()) {

            Asset asset = persistedCampaign.assetMap.get(key);
            
			if (!AssetManager.hasAsset(key)) {
				AssetManager.putAsset(asset);
			}

			if (!MapTool.isHostingServer() && !MapTool.isPersonalServer()) {
				// If we are remotely installing this campaign, we'll need to send the image data to the server
	            MapTool.serverCommand().putAsset(asset);
			}
		}
		
		return persistedCampaign.campaign;
	}
	
	public static class PersistedCampaign {
		
		public Campaign campaign;
		public Map<MD5Key, Asset> assetMap = new HashMap<MD5Key, Asset>();
		
	}
}
