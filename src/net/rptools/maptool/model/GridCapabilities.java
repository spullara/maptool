package net.rptools.maptool.model;

public interface GridCapabilities {

	public boolean isSnapToGridSupported();
	public boolean isPathingSupported();
	public boolean isPathLineSupported();
	public boolean isCoordinatesSupported();
	/**
	 * The secondary dimension should be linked to changes in the primary dimension
	 * but the primary dimension independant of the secondary.
	 */
	public boolean isSecondDimensionAdjustmentSupported();
}
