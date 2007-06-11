package net.rptools.maptool.model;


public class CampaignFactory {

	
	public static Campaign createBasicCampaign() {
		Campaign campaign = new Campaign();
		campaign.putZone(ZoneFactory.createZone());

        return campaign;
	}
}
