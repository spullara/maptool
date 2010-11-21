/**
 * 
 */
package net.rptools.maptool.model;

import java.awt.geom.Area;

public class ExposedAreaMetaData
{
	private Area exposedAreaHistory;

	public ExposedAreaMetaData()
	{
		exposedAreaHistory = new Area();
	}
	public ExposedAreaMetaData(Area area)
	{
		exposedAreaHistory = (Area) area.clone();
	}
	public Area getExposedAreaHistory()
	{
		if(exposedAreaHistory == null)
		{
			exposedAreaHistory = new Area();
		}
		return exposedAreaHistory;
	}
	public void addToExposedAreaHistory(Area newArea) {
		if(newArea== null)
		{
			newArea = new Area();
		}
		this.exposedAreaHistory.add((Area) newArea.clone()) ;
	}
	public void removeExposedAreaHistory(Area newArea) {
		if(newArea== null)
		{
			newArea = new Area();
		}
		this.exposedAreaHistory.subtract((Area) newArea.clone()) ;
	}
	public void clearExposedAreaHistory() {
		this.exposedAreaHistory = new Area();
	}
}