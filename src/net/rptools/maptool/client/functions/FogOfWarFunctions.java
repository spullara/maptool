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
package net.rptools.maptool.client.functions;

import java.util.List;
import java.util.Set;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.zone.FogUtil;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.language.I18N;
import net.rptools.maptool.model.GUID;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;

/**
 * @author jfrazierjr
 * 
 */
public class FogOfWarFunctions extends AbstractFunction {
	private static final FogOfWarFunctions instance = new FogOfWarFunctions();

	private FogOfWarFunctions() {
		super(0, 2, "exposePCOnlyArea", "exposeFOW");
	}

	public static FogOfWarFunctions getInstance() {
		return instance;
	}

	@Override
	public Object childEvaluate(Parser parser, String functionName, List<Object> parameters) throws ParserException {
		if (!MapTool.getParser().isMacroTrusted()) {
			throw new ParserException(I18N.getText("macro.function.general.noPerm", functionName));
		}
		if (parameters.size() > 1) {
			throw new ParserException(I18N.getText("macro.function.general.tooManyParam", functionName, 1, parameters.size()));
		}
		ZoneRenderer zoneRenderer = (parameters.size() == 1 && parameters.get(0) instanceof String) ? getZoneRenderer((String) parameters.get(0)) : getZoneRenderer(null);

		/*
		 * String empty = exposePCOnlyArea(optional String mapName)
		 */
		if (functionName.equals("exposePCOnlyArea")) {
			FogUtil.exposePCArea(zoneRenderer);
			return "";
		}
		/*
		 * String empty = exposeFOW(optional String mapName)
		 */
		if (functionName.equals("exposeFOW")) {
			FogUtil.exposeVisibleArea(zoneRenderer, getTokenSelectedSet(zoneRenderer));
			return "";
		}
		throw new ParserException(I18N.getText("macro.function.general.unknownFunction", functionName));
	}

	private Set<GUID> getTokenSelectedSet(final ZoneRenderer zr) {
		Set<GUID> tokens = MapTool.getFrame().getCurrentZoneRenderer().getSelectedTokenSet();
		Set<GUID> ownedTokens = MapTool.getFrame().getCurrentZoneRenderer().getOwnedTokens(tokens);
		return ownedTokens;
	}

	private ZoneRenderer getZoneRenderer(final String name) {
		if (name == null) {
			return MapTool.getFrame().getCurrentZoneRenderer();
		}
		for (ZoneRenderer zr : MapTool.getFrame().getZoneRenderers()) {
			if (zr.getZone().getName().equals(name.toString())) {
				return zr;
			}
		}
		return MapTool.getFrame().getCurrentZoneRenderer();
	}
}
