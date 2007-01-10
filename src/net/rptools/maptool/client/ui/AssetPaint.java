package net.rptools.maptool.client.ui;

import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;

import net.rptools.maptool.model.Asset;
import net.rptools.maptool.util.ImageManager;

public class AssetPaint extends TexturePaint {

	private Asset asset;
	
	public AssetPaint(Asset asset) {
		super(ImageManager.getImageAndWait(asset), new Rectangle2D.Float(0, 0, ImageManager.getImageAndWait(asset).getWidth(), ImageManager.getImageAndWait(asset).getHeight()));
		this.asset = asset;
	}
	
	public Asset getAsset() {
		return asset;
	}
}
