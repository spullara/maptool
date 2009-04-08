package net.rptools.maptool.client.functions;

import java.util.LinkedList;
import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.language.I18N;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;
import net.sf.json.JSONArray;

public class MapFunctions extends AbstractFunction {
	
	private static final MapFunctions instance = new MapFunctions();

	private MapFunctions() {
		super(0, 1, "getAllMapNames", "getCurrentMapName", "getVisibleMapNames");
	}
	
	
	public static MapFunctions getInstance() {
		return instance;
	}


	@Override
	public Object childEvaluate(Parser parser, String functionName,
			List<Object> parameters) throws ParserException {
		
 		
		if (functionName.equals("getCurrentMapName")) {
			return MapTool.getFrame().getCurrentZoneRenderer().getZone().getName();
		} else {
			boolean allMaps = functionName.equals("getAllMapNames");

		    if (allMaps && !MapTool.getParser().isMacroTrusted()) {
				throw new ParserException(I18N.getText("macro.function.general.noPerm", functionName));
	        }

			List<String> mapNames = new LinkedList<String>();
			for (ZoneRenderer zr : MapTool.getFrame().getZoneRenderers()) {
				if (allMaps || zr.getZone().isVisible()) {
					mapNames.add(zr.getZone().getName());
				}
			}
			
			String delim = parameters.size() > 0 ? parameters.get(0).toString() : ",";
			if ("json".equals(delim)) {
				return JSONArray.fromObject(mapNames);
			} else {
				return StringFunctions.getInstance().join(mapNames, delim);
			}
		}
	}


}
