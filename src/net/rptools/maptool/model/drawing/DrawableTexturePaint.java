package net.rptools.maptool.model.drawing;

import java.awt.Image;
import java.awt.Paint;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.Serializable;

import net.rptools.lib.MD5Key;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.util.ImageManager;

public class DrawableTexturePaint extends DrawablePaint implements Serializable, ImageObserver {

	private MD5Key assetId;
	private double scale;
	private transient BufferedImage image;

	public DrawableTexturePaint() {
		// Serializable
	}
	
	public DrawableTexturePaint(MD5Key id) {
		this(id, 1);
	}
	public DrawableTexturePaint(MD5Key id, double scale) {
		assetId = id;
		this.scale = scale;
	}
	public DrawableTexturePaint(Asset asset) {
		this(asset.getId());
	}
	public DrawableTexturePaint(Asset asset, double scale) {
		this(asset.getId(), 1);
	}

	@Override
	public Paint getPaint(int offsetX, int offsetY, double scale) {
		BufferedImage texture = null;
		if (image != null) {
			texture = image;
		} else {
			texture = ImageManager.getImage(AssetManager.getAsset(assetId), this);
			if (texture != ImageManager.UNKNOWN_IMAGE) {
				image = texture;
			}
		}

		return new TexturePaint(texture, new Rectangle2D.Double(offsetX, offsetY, texture.getWidth()*scale*this.scale, texture.getHeight()*scale*this.scale));
	}
	
	@Override
	public Paint getPaint() {
		return getPaint(0, 0, 1);
	}

	public Asset getAsset() {
		return AssetManager.getAsset(assetId);
	}
	
	public MD5Key getAssetId() {
		return assetId;
	}
	
	////
	// IMAGE OBSERVER
	public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
		System.out.println("imageUpdate");
		MapTool.getFrame().getCurrentZoneRenderer().flushDrawableRenderer();
		MapTool.getFrame().refresh();
		return false;
	}
}
