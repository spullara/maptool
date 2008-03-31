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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import net.rptools.lib.MD5Key;
import net.rptools.lib.image.ImageUtil;
import net.rptools.lib.io.PackedFile;
import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.AppUtil;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.Scale;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.client.ui.zone.PlayerView;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.model.Campaign;
import net.rptools.maptool.model.CampaignProperties;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.thoughtworks.xstream.XStream;

/**
 * @author trevor
 */
public class PersistenceUtil {

	private static final String PROP_VERSION = "version";
	
	static {
		PackedFile.init(AppUtil.getAppHome("tmp"));
	}
	
	public static void saveCampaign(Campaign campaign, File campaignFile) throws IOException {
		
		PackedFile pakFile = new PackedFile(campaignFile);

		// Configure the meta file (this is for legacy support)
		PersistedCampaign persistedCampaign = new PersistedCampaign();
		
		persistedCampaign.campaign = campaign;

		// Keep track of the current view
		ZoneRenderer currentZoneRenderer = MapTool.getFrame().getCurrentZoneRenderer();
		if (currentZoneRenderer != null) {
			persistedCampaign.currentZoneId = currentZoneRenderer.getZone().getId();
			persistedCampaign.currentView = currentZoneRenderer.getZoneScale();
		}
		
		// Save all assets in active use
		for (Zone zone : campaign.getZones()) {
			
			Set<MD5Key> assetSet = zone.getAllAssetIds();
			for (MD5Key key : assetSet) {
				
				// Put in a placeholder 
				persistedCampaign.assetMap.put(key, null);
				
				// And store the asset elsewhere
				pakFile.putFile("assets/" + key, AssetManager.getAsset(key));
			}
		}
		
		pakFile.setContent(persistedCampaign);
		pakFile.setProperty(PROP_VERSION, MapTool.getVersion());
		
		pakFile.save();
		pakFile.close();
		
		// Save the campaign thumbnail
		saveCampaignThumbnail(campaignFile.getName());        
		
	}

	/* A public function because I think it should be called when a campaign is opened as well
	* so if it is opened then closed without saving, there is still a preview created;
	* however, the rendering of the campaign appears to complete after AppActions.loadCampaign 
	* returns, causing the preview to always appear as black if this method is called from
	* within loadCampaign.  Either need to find another place to call saveCampaignThumbnail
	* upon opening, or code to delay it's call until the render is complete.  =P
	*/
	static public void saveCampaignThumbnail(String fileName) {
		BufferedImage screen = MapTool.takeMapScreenShot(new PlayerView(MapTool.getPlayer().getRole()));
		if (screen == null) {
			return;
		}
		
		Dimension imgSize = new Dimension(screen.getWidth(null), screen.getHeight(null));
		SwingUtil.constrainTo(imgSize, 200, 200);

		BufferedImage thumb = new BufferedImage(imgSize.width,imgSize.height, BufferedImage.TYPE_INT_BGR);
		Graphics2D g2d = thumb.createGraphics();
		g2d.drawImage(screen, 0, 0, imgSize.width, imgSize.height, null);
        g2d.dispose();
        
		File thumbFile = getCampaignThumbnailFile(fileName);
        
        try{
        	ImageIO.write(thumb, "jpg", thumbFile);
        }
        catch (IOException ioe) {
        	MapTool.showError("Could not save the campaign preview image: " + ioe);
        }
	}
	
	/**
	 * Gets a file pointing to where the campaign's thumbnail image should be.
	 * @param fileName The campaign's file name.
	 */
	public static File getCampaignThumbnailFile(String fileName) {
		return new File(AppUtil.getAppHome("campaignthumbs"), fileName + ".jpg");
	}
	
	public static PersistedCampaign loadCampaign(File campaignFile) throws IOException {
		
		// Try the new way first
		PackedFile pakfile = new PackedFile(campaignFile);
		try {
			
			// Sanity check
			String version = (String)pakfile.getProperty(PROP_VERSION);
			
			PersistedCampaign persistedCampaign = (PersistedCampaign) pakfile.getContent();

			// Now load up any images that we need
			// Note that the values are all placeholders
			for (MD5Key key : persistedCampaign.assetMap.keySet()) {
				
				if (!AssetManager.hasAsset(key)) {
					Asset asset = (Asset) pakfile.getFileObject("assets/" + key);
					AssetManager.putAsset(asset);

					if (!MapTool.isHostingServer() && !MapTool.isPersonalServer()) {
						// If we are remotely installing this campaign, we'll need to send the image data to the server
			            MapTool.serverCommand().putAsset(asset);
					}
				}
			}
			
			return persistedCampaign;
		} catch (IOException ioe) {
			
			// Well, try the old way
			return loadLegacyCampaign(campaignFile);
		}
	}
	
	public static PersistedCampaign loadLegacyCampaign(File campaignFile) throws IOException {
		
		InputStream is = new BufferedInputStream(new FileInputStream(campaignFile));
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
		
		// Do some sanity work on the campaign
		// This specifically handles the case when the zone mappings
		// are out of sync in the save file
		Campaign campaign = persistedCampaign.campaign;
		Set<Zone> zoneSet = new HashSet<Zone>(campaign.getZones());
		campaign.removeAllZones();
		for (Zone zone : zoneSet) {
			campaign.putZone(zone);
		}
		
		return persistedCampaign;
	}
	
	public static BufferedImage getTokenThumbnail(File file) throws Exception {

		PackedFile pakFile = new PackedFile(file);

		BufferedImage thumb = null; 
		if (pakFile.hasFile(Token.FILE_THUMBNAIL)) {
			InputStream in = pakFile.getFile(Token.FILE_THUMBNAIL);
			try {
				thumb = ImageIO.read(in);
			} finally {
				if (in != null) {
					in.close();
				}
			}
		} 
		
		pakFile.close();
		
		return thumb;
	}
	
	public static void saveToken(Token token, File file) throws IOException {
		
		PackedFile pakFile = new PackedFile(file);

		for (MD5Key assetId : token.getAllImageAssets()) {
			if (assetId == null) {
				continue;
			}
			
			// And store the asset elsewhere
			pakFile.putFile("assets/" + assetId, AssetManager.getAsset(assetId));
		}
		
		// Thumbnail
		BufferedImage image = ImageManager.getImage(AssetManager.getAsset(token.getImageAssetId()));
		Dimension sz = new Dimension(image.getWidth(), image.getHeight());
		SwingUtil.constrainTo(sz, 50);
		BufferedImage thumb = new BufferedImage(sz.width, sz.height, BufferedImage.TRANSLUCENT);
		Graphics2D g = thumb.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawImage(image, 0, 0, sz.width, sz.height, null);
		g.dispose();
		
		pakFile.putFile(Token.FILE_THUMBNAIL, ImageUtil.imageToBytes(thumb, "png"));
		
		pakFile.setContent(token);
		pakFile.setProperty(PROP_VERSION, MapTool.getVersion());
		
		pakFile.save();
		pakFile.close();
		
	}
	
	public static Token loadToken(File file) throws IOException {
		
		PackedFile pakFile = new PackedFile(file);
		
		// TODO: Check version
		Token token = (Token) pakFile.getContent();
		
		for (MD5Key key : token.getAllImageAssets()) {
			if (key == null) {
				continue;
			}
			
			if (!AssetManager.hasAsset(key)) {
				Asset asset = (Asset) pakFile.getFileObject("assets/" + key);
				AssetManager.putAsset(asset);

				if (!MapTool.isHostingServer() && !MapTool.isPersonalServer()) {
					// If we are remotely installing this token, we'll need to send the image data to the server
		            MapTool.serverCommand().putAsset(asset);
				}
			}
		}

		return token;
	}
	
	public static CampaignProperties loadCampaignProperties(File file) throws IOException {
		
		if (!file.exists()) {
			throw new FileNotFoundException();
		}
		
		FileInputStream in = new FileInputStream(file);
		try {
			return loadCampaignProperties(in);
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}
	
	public static CampaignProperties loadCampaignProperties(InputStream in) throws IOException {
		
		return (CampaignProperties) new XStream().fromXML(in);
	}
	
	public static void saveCampaignProperties(Campaign campaign, File file) throws IOException {
		
		// Put this in FileUtil
		if (file.getName().indexOf(".") < 0) {
			file = new File(file.getAbsolutePath()
					+ ".mtprops");
		}

		
		new XStream().toXML(campaign.getCampaignProperties(), new FileOutputStream(file));
	}
	
	public static <T> T hessianClone(T object) throws IOException {

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		HessianOutput out = new HessianOutput(os);
		out.writeObject(object);

		HessianInput in = new HessianInput(new ByteArrayInputStream(os.toByteArray()));

		return (T)in.readObject();
	}
	
	public static class PersistedCampaign {
		
		public Campaign campaign;
		public Map<MD5Key, Asset> assetMap = new HashMap<MD5Key, Asset>();
		public GUID currentZoneId;
		public Scale currentView;
		public String mapToolVersion;
	}
}
