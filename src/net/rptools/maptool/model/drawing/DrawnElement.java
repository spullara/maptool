/*
 */
package net.rptools.maptool.model.drawing;

import java.io.Serializable;

/**
 */
// TODO: I'm not a fan of this class name, refactory it later
public class DrawnElement implements Serializable {

	private Drawable drawable;
	private Pen pen;
	
	public DrawnElement(Drawable drawable, Pen pen) {
		this.drawable = drawable;
		this.pen = pen;
	}
	
	public Drawable getDrawable() {
		return drawable;
	}
	
	public Pen getPen() {
		return pen;
	}
}
