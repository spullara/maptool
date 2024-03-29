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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.rptools.lib.swing.ImagePanelModel;
import net.rptools.maptool.client.AppStyle;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.LookupTable;
import net.rptools.maptool.util.ImageManager;

import org.apache.log4j.Logger;

public class LookupTableImagePanelModel implements ImagePanelModel {

	private static final Logger log = Logger.getLogger(LookupTableImagePanelModel.class);

	private final ImageObserver[] imageObservers;

	public LookupTableImagePanelModel(ImageObserver... observers) {
		imageObservers = observers;
	}

	public int getImageCount() {
		return getFilteredLookupTable().size();
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

		LookupTable table = getFilteredLookupTable().get(id);
		if (table == null) {
			log.debug("LookupTableImagePanelModel.getImage(" + id + "):  not resolved");
			return ImageManager.BROKEN_IMAGE;
		}

		Image image = AppStyle.lookupTableDefaultImage;
		if (table.getTableImage() != null) {
			image = ImageManager.getImage(table.getTableImage(), imageObservers);
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

		LookupTable table = getFilteredLookupTable().get(getID(index));

		return table.getName();
	}

	public Paint getBackground(int arg0) {
		return null;
	}

	public Image[] getDecorations(int arg0) {
		return null;
	}

	private List<String> getLookupTableIDList() {

		List<String> idList = new ArrayList<String>(getFilteredLookupTable().keySet());
		Collections.sort(idList);
		return idList;
	}

	/**Retrieves a Map containing tables and their names from campaign
	 * properties.
	 * @return Map&ltString, LookupTable&gt -- If the client belongs to a GM, all tables will
	 * be returned.  If the client belongs to a player, only non-
	 * hidden tables will be returned.
	 */
	private Map<String, LookupTable> getFilteredLookupTable() {
		if (MapTool.getPlayer() == null) {
			return new HashMap<String, LookupTable>();
		}

		Map<String, LookupTable> lookupTables = new HashMap<String, LookupTable>(MapTool.getCampaign().getLookupTableMap());
		if(!MapTool.getPlayer().isGM()) {
			for(String nextKey : MapTool.getCampaign().getLookupTableMap().keySet()) {
				if(!lookupTables.get(nextKey).getVisible()) {
					lookupTables.remove(nextKey);
				}
			}
		}
		return lookupTables;
	}
}
