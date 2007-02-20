package net.rptools.maptool.model;

import java.io.IOException;

import net.rptools.lib.FileUtil;
import net.rptools.maptool.client.AppConstants;
import net.rptools.maptool.client.AppPreferences;
import net.rptools.maptool.util.ImageManager;

public class CampaignFactory {

	public static Campaign createBasicCampaign() {
		Campaign campaign = new Campaign();
		
        try {
			Asset asset = new Asset("Grasslands", FileUtil.loadResource("net/rptools/lib/resource/image/texture/grass.png"));
            final Zone zone = ZoneFactory.createZone(Zone.MapType.INFINITE, asset.getId());

            // TODO: This should really be in the factory method
			zone.setGrid(GridFactory.createGrid(AppPreferences
					.getDefaultGridType()));
			zone.getGrid().setOffset(0, 0);
			zone.setGridColor(AppConstants.DEFAULT_GRID_COLOR.getRGB());

			// Make sure the image is loaded to avoid a flash screen when it becomes visible
			ImageManager.getImageAndWait(asset);

			campaign.putZone(zone);
        } catch (IOException ioe) {
        	ioe.printStackTrace();
        }

        return campaign;
	}
}
