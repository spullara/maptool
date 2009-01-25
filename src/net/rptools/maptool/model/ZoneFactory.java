/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package net.rptools.maptool.model;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import net.rptools.lib.FileUtil;
import net.rptools.lib.MD5Key;
import net.rptools.maptool.client.AppPreferences;
import net.rptools.maptool.client.AppUtil;
import net.rptools.maptool.model.drawing.DrawableColorPaint;
import net.rptools.maptool.model.drawing.DrawableTexturePaint;
import net.rptools.maptool.util.ImageManager;

public class ZoneFactory {

	public static final String DEFAULT_MAP_NAME = "Grasslands";
	public static MD5Key defaultImageId;
	
	static {
    	// TODO: I really don't like this being hard wired this way, need to make it a preference or something
    	File grassImage = new File(AppUtil.getAppHome("resource/Default/Textures").getAbsolutePath() + "/Grass.png");
    	if (grassImage.exists()) {
    		try {
    			Asset asset = new Asset(DEFAULT_MAP_NAME, FileUtil.loadFile(grassImage));
    			defaultImageId = asset.getId();
    			
				// Make sure the image is loaded to avoid a flash screen when it becomes visible
				ImageManager.getImageAndWait(asset);
    		} catch (IOException ioe) {
    			ioe.printStackTrace();
    		}
    		
    	}
	}
	
	public static Zone createZone() {
		
		Zone zone = new Zone();
		
		zone.setName(DEFAULT_MAP_NAME);
		zone.setBackgroundPaint(new DrawableTexturePaint(defaultImageId));
		zone.setFogPaint(new DrawableColorPaint(Color.black));

		zone.setVisible(AppPreferences.getNewMapsVisible());
		zone.setHasFog(AppPreferences.getNewMapsHaveFOW());
		zone.setUnitsPerCell(AppPreferences.getDefaultUnitsPerCell());
		zone.setTokenVisionDistance(AppPreferences.getDefaultVisionDistance());
		
		zone.setGrid(GridFactory.createGrid(AppPreferences.getDefaultGridType(),AppPreferences.getFaceEdge(), AppPreferences.getFaceVertex()));
		zone.setGridColor(AppPreferences.getDefaultGridColor().getRGB());
		zone.getGrid().setSize(AppPreferences.getDefaultGridSize());
		zone.getGrid().setOffset(0, 0);

	    return zone;
	}

}
