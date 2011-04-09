/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *  
 *	http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package net.rptools.maptool.client.ui.zone;

import java.awt.Graphics2D;
import java.awt.geom.Area;

import net.rptools.maptool.client.AppStyle;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.AttachedLightSource;
import net.rptools.maptool.model.LightSource;
import net.rptools.maptool.model.Token;

public class LightSourceIconOverlay implements ZoneOverlay {

	public void paintOverlay(ZoneRenderer renderer, Graphics2D g) {

		for (Token token : renderer.getZone().getAllTokens()) {

			if (token.hasLightSources()) {
				boolean foundNormalLight = false;
				for (AttachedLightSource attachedLightSource : token.getLightSources()) {
					LightSource lightSource = MapTool.getCampaign().getLightSource(attachedLightSource.getLightSourceId());
					if (lightSource != null && lightSource.getType() == LightSource.Type.NORMAL) {
						foundNormalLight = true;
						break;
					}
				}
				if (!foundNormalLight) {
					continue;
				}
				
				Area area = renderer.getTokenBounds(token);
				if (area == null) {
					continue;
				}

				int x = area.getBounds().x + (area.getBounds().width - AppStyle.lightSourceIcon.getWidth())/2;
				int y = area.getBounds().y + (area.getBounds().height - AppStyle.lightSourceIcon.getHeight())/2;
				g.drawImage(AppStyle.lightSourceIcon, x, y, null);
			}
		}
	}
}
