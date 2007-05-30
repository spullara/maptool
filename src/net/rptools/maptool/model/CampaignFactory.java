package net.rptools.maptool.model;

import java.io.File;
import java.io.IOException;

import net.rptools.lib.FileUtil;
import net.rptools.maptool.client.AppConstants;
import net.rptools.maptool.client.AppPreferences;
import net.rptools.maptool.client.AppUtil;
import net.rptools.maptool.util.ImageManager;

public class CampaignFactory {

	public static final String DEFAULT_MAP_NAME = "Grasslands";
	
	public static Campaign createBasicCampaign() {
		Campaign campaign = new Campaign();
		
        try {
        	// TODO: I really don't like this being hard wired this way, need to make it a preference or something
        	File grassImage = new File(AppUtil.getAppHome("resource/Default/Textures").getAbsolutePath() + "/Grass.png");
        	if (grassImage.exists()) {
        	
				Asset asset = new Asset(DEFAULT_MAP_NAME, FileUtil.loadFile(grassImage));
	            final Zone zone = ZoneFactory.createZone(asset.getId());
	
	            // TODO: This should really be in the factory method
				zone.setGrid(GridFactory.createGrid(AppPreferences
						.getDefaultGridType()));
				zone.getGrid().setOffset(0, 0);
				zone.setGridColor(AppConstants.DEFAULT_GRID_COLOR.getRGB());
				zone.setName("Grasslands");
	
				// Make sure the image is loaded to avoid a flash screen when it becomes visible
				ImageManager.getImageAndWait(asset);
	
				campaign.putZone(zone);
        	} else {
        		System.out.println("Could not find image for default map");
        	}
        } catch (IOException ioe) {
        	ioe.printStackTrace();
        }

        return campaign;
	}
}
