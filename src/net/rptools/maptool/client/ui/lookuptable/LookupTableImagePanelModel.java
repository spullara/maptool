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
package net.rptools.maptool.client.ui.lookuptable;

import java.awt.Image;
import java.awt.Paint;
import java.awt.datatransfer.Transferable;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.rptools.lib.swing.ImagePanelModel;
import net.rptools.maptool.client.AppStyle;
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
		
		Image image = AppStyle.lookupTableDefaultImage;
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
