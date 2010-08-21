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
package net.rptools.maptool.client.macro.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolMacroContext;
import net.rptools.maptool.client.macro.Macro;
import net.rptools.maptool.client.macro.MacroContext;
import net.rptools.maptool.client.macro.MacroDefinition;
import net.rptools.maptool.model.CellPoint;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.ZonePoint;

@MacroDefinition(
		name = "goto",
		aliases = { "g" },
		description = "goto.desc"
)
public class GotoMacro implements Macro {
	private static Pattern COORD_PATTERN = Pattern.compile("(-?\\d+)\\s*,?\\s*(-?\\d+)");

	public void execute(MacroContext context, String parameter, MapToolMacroContext executionContext) {
		Matcher m = COORD_PATTERN.matcher(parameter.trim());

		if (m.matches()) {
			// goto coordinate locations
			int x = Integer.parseInt(m.group(1));
			int y = Integer.parseInt(m.group(2));

			MapTool.getFrame().getCurrentZoneRenderer().centerOn(new CellPoint(x, y));
		} else {
			// goto token location
			Zone zone = MapTool.getFrame().getCurrentZoneRenderer().getZone();
			Token token = zone.getTokenByName(parameter);

			if (!MapTool.getPlayer().isGM() && !zone.isTokenVisible(token)) {
				return;
			}

			if (token != null) {
				int x = token.getX();
				int y = token.getY();

				MapTool.getFrame().getCurrentZoneRenderer().centerOn(new ZonePoint(x, y));
			}
		}
	}
}
