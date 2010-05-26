package net.rptools.maptool.client.functions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolVariableResolver;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.language.I18N;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;
import net.sf.json.JSONArray;

public class TokenSelectionFunctions extends AbstractFunction {

	private static final TokenSelectionFunctions instance = new TokenSelectionFunctions();

	private TokenSelectionFunctions() {
		super(0, 2, "selectTokens", "deselectTokens");
	}

	public static TokenSelectionFunctions getInstance() {
		return instance;
	}

	@Override
	public Object childEvaluate(Parser parser, String functionName, List<Object> parameters) throws ParserException {

		if (functionName.equals("selectTokens")) {
			selectTokens(parameters);
		} else if (functionName.equals("deselectTokens")) {
			deselectTokens(parameters);
		} else {
			throw new ParserException(I18N.getText("macro.function.general.unknownFunction", functionName));
		}
		return BigDecimal.ONE;
	}

	private void deselectTokens(List<Object> parameters) throws ParserException {

		ZoneRenderer zr = MapTool.getFrame().getCurrentZoneRenderer();
		Zone zone = zr.getZone();

		if (parameters == null || parameters.size() < 1) {

			// Select all tokens
			List<Token> allTokens = zone.getTokens();

			if (allTokens != null) {
				for (Token t : allTokens) {
					GUID tid = t.getId();
					zr.deselectToken(tid);
				}
			}

		} else if (parameters.size() == 1) {
			String paramStr = parameters.get(0).toString();
			Token t = zone.resolveToken(paramStr.trim());
			if (t != null) {
				zr.deselectToken(t.getId());
			} else {
				throw new ParserException(I18N.getText("macro.function.general.unknownToken", "deselectTokens", paramStr.trim()));
			}
		} else if (parameters.size() == 2) {
			// Either a JSON Array or a String List

			String delim = parameters.get(0).toString();
			String paramStr = parameters.get(1).toString();

			if (delim.equalsIgnoreCase("json")) {
				// A JSON Array was supplied

				Object json = JSONMacroFunctions.convertToJSON(paramStr);
				if (json instanceof JSONArray) {
					for (Object o : (JSONArray) json) {
						String identifier = (String) o;
						Token t = zone.resolveToken(identifier.trim());
						if (t != null) {
							zr.deselectToken(t.getId());
						} else {
							throw new ParserException(I18N.getText("macro.function.general.unknownToken", "deselectTokens", identifier));
						}
					}
				}
			} else {
				// String List

				String[] strList = paramStr.split(delim);
				for (String s : strList) {
					Token t = zone.resolveToken(s.trim());
					if (t != null) {
						zr.deselectToken(t.getId());
					} else {
						throw new ParserException(I18N.getText("macro.function.general.unknownToken", "deselectTokens", s));
					}
				}
			}

		}
	}

	private void selectTokens(List<Object> parameters) throws ParserException {

		Zone zone = MapTool.getFrame().getCurrentZoneRenderer().getZone();
		Collection<GUID> allGUIDs = new ArrayList<GUID>();

		if (parameters == null || parameters.size() < 1) {

			// Select all tokens
			List<Token> allTokens = zone.getTokens();

			if (allTokens != null) {
				for (Token t : allTokens) {
					GUID tid = t.getId();
					allGUIDs.add(tid);
				}
			}

		} else if (parameters.size() == 1) {
			String paramStr = parameters.get(0).toString();
			Token t = zone.resolveToken(parameters.get(0).toString());
			if (t != null) {
				allGUIDs.add(t.getId());
			} else {
				throw new ParserException(I18N.getText("macro.function.general.unknownToken", "selectTokens", paramStr));
			}
		} else if (parameters.size() == 2) {
			// Either a JSON Array or a String List
			String delim = parameters.get(0).toString();
			String paramStr = parameters.get(1).toString();

			if (delim.equalsIgnoreCase("json")) {
				// A JSON Array was supplied

				Object json = JSONMacroFunctions.convertToJSON(paramStr);
				if (json instanceof JSONArray) {
					for (Object o : (JSONArray) json) {
						String identifier = (String) o;
						Token t = zone.resolveToken(identifier);
						if (t != null) {
							allGUIDs.add(t.getId());
						} else {
							throw new ParserException(I18N.getText("macro.function.general.unknownToken", "selectTokens", identifier));
						}
					}
				}
			} else {
				// String List

				String[] strList = paramStr.split(delim);
				for (String s : strList) {
					Token t = zone.resolveToken(s.trim());
					if (t != null) {
						allGUIDs.add(t.getId());
					} else {
						throw new ParserException(I18N.getText("macro.function.general.unknownToken", "selectTokens", s));
					}
				}
			}

		}

		MapTool.getFrame().getCurrentZoneRenderer().selectTokens(allGUIDs);

	}

}
