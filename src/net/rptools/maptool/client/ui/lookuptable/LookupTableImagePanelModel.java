/* The MIT License
 * 
 * Copyright (c) 2005 David Rice, Trevor Croft
 * 
 * Permission is hereby granted, free of charge, to any person 
 * obtaining a copy of this software and associated documentation files 
 * (the "Software"), to deal in the Software without restriction, 
 * including without limitation the rights to use, copy, modify, merge, 
 * publish, distribute, sublicense, and/or sell copies of the Software, 
 * and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be 
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS 
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN 
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
 * SOFTWARE.
 */
package net.rptools.maptool.client.ui.lookuptable;

import java.awt.Image;
import java.awt.Paint;
import java.awt.datatransfer.Transferable;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.rptools.lib.swing.ImagePanelModel;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.model.LookupTable;
import net.rptools.maptool.util.ImageManager;

public class LookupTableImagePanelModel implements ImagePanelModel {
	
	private ImageObserver[] imageObservers;
	
	public LookupTableImagePanelModel(ImageObserver... observers) {
		imageObservers = observers;
	}

	public int getImageCount() {
		return MapTool.getCampaign().getLookupTableMap().size();
	}

	public Transferable getTransferable(int arg0) {
		return null;
	}

	public Object getID(int index) {
		if (index < 0) {
			return null;
		}
		
		return getLookupTableIDList().get(index);
	}

	public Image getImage(Object id) {

		LookupTable table = MapTool.getCampaign().getLookupTableMap().get(id);
		if (table == null) {
			return ImageManager.BROKEN_IMAGE;
		}
		
		Image image = ImageManager.UNKNOWN_IMAGE;
		if (table.getTableImage() != null) {
			image = ImageManager.getImage(AssetManager.getAsset(table.getTableImage()), imageObservers);
		}
		
		return image;
	}

	public Image getImage(int index) {
		return getImage(getID(index));
	}

	public String getCaption(int index) {
		if (index < 0) {
			return "";
		}

		LookupTable table = MapTool.getCampaign().getLookupTableMap().get(getID(index));
		
		return table.getName();
	}

	public Paint getBackground(int arg0) {
		return null;
	}

	public Image[] getDecorations(int arg0) {
		return null;
	}
	
	private List<String> getLookupTableIDList() {
		List<String> idList = new ArrayList<String>(MapTool.getCampaign().getLookupTableMap().keySet());
		Collections.sort(idList);
		return idList;
	}

}
