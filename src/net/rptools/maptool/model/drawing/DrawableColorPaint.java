package net.rptools.maptool.model.drawing;

import java.awt.Color;
import java.awt.Paint;
import java.io.Serializable;

public class DrawableColorPaint extends DrawablePaint implements Serializable {

	private int color;
	private transient Color colorCache;

	public DrawableColorPaint() {
		// For serialization
	}
	
	public DrawableColorPaint(Color color) {
		this.color = color.getRGB();
	}
	
	@Override
	public Paint getPaint() {
		if (colorCache == null) {
			colorCache = new Color(color);
		}
		return colorCache;
	}

}
