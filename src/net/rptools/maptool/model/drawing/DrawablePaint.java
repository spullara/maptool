package net.rptools.maptool.model.drawing;

import java.awt.Color;
import java.awt.Paint;
import java.io.Serializable;

import net.rptools.maptool.client.ui.AssetPaint;
import net.rptools.maptool.model.Asset;

public abstract class DrawablePaint implements Serializable {

	public abstract Paint getPaint();
	public abstract Paint getPaint(int offsetX, int offsetY, double scale);
	
	public static DrawablePaint convertPaint(Paint paint) {
		if (paint == null) {
			return null;
		}
		
		if (paint instanceof Color) {
			return new DrawableColorPaint((Color) paint);
		}
		if (paint instanceof AssetPaint) {
			
			Asset asset = ((AssetPaint) paint).getAsset();
			return new DrawableTexturePaint(asset);
		}
		
		throw new IllegalArgumentException("Invalid type of paint: " + paint.getClass().getName());
	}
	
}
