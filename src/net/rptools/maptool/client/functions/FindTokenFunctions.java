package net.rptools.maptool.client.functions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolVariableResolver;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;
import net.sf.json.JSONArray;

public class FindTokenFunctions extends AbstractFunction {

	private enum FindType {
		SELECTED,
		IMPERSONATED,
		NPC,
		PC,
		ALL,
		CURRENT,
		EXPOSED,
		STATE, 
		OWNED,
		VISIBLE
	}
	
	private static final FindTokenFunctions instance = new FindTokenFunctions();
	
	
	/**
	 * Filter for all tokens.
	 */
	private class AllFilter implements Zone.Filter {
		public boolean matchToken(Token t) {
			return t.getLayer() == Zone.Layer.TOKEN ||
				   t.getLayer() == Zone.Layer.GM;
		}
	}

	/**
	 * Filter for NPC tokens.
	 */
	private class NPCFilter implements Zone.Filter {
		public boolean matchToken(Token t) {
			return (t.getLayer() == Zone.Layer.TOKEN ||
			        t.getLayer() == Zone.Layer.GM) &&
			        t.getType() == Token.Type.NPC;
		}
	}

	/**
	 * Filter for PC tokens.
	 */
	private class PCFilter implements Zone.Filter {
		public boolean matchToken(Token t) {
			return (t.getLayer() == Zone.Layer.TOKEN ||
			        t.getLayer() == Zone.Layer.GM) &&
			        t.getType() == Token.Type.PC;
		}
	}
	
	/**
	 * Filter for player exposed tokens. 
	 */
	private class ExposedFilter implements Zone.Filter {
		public boolean matchToken(Token t) {
	        
			return (t.getLayer() == Zone.Layer.TOKEN &&
					MapTool.getFrame().getCurrentZoneRenderer().getZone().isTokenVisible(t));
		}
	}

	/**
	 * Filter for finding tokens by set state.
	 */
	private class StateFilter implements Zone.Filter {
		
		private final String stateName;
		
		public StateFilter(String stateName) {
			this.stateName = stateName;
		}
		public boolean matchToken(Token t) {
			Object val = t.getState(stateName);
			
			if (val == null) {
				return false;
			}
			
			if (val instanceof Boolean) {
				return ((Boolean)val).booleanValue();
			}
			
			if (val instanceof BigDecimal) {
				if (val.equals(BigDecimal.ZERO)) {
					return false;
				} else {
					return true;
				}
			}
			
			return true;
		}
		
	}
	
	/**
	 * Filter for finding tokens by owner.
	 */
	private class OwnedFilter implements Zone.Filter {
		
		private final String name;
		
		public OwnedFilter(String name) {
			this.name = name;
		}
		public boolean matchToken(Token t) {
			return t.isOwner(name);
		}
		
	}
	
	private FindTokenFunctions() {
		super(0,2, "findToken", "currentToken", "getTokenName", "getTokenNames", 
				   "getSelectedNames", "getTokens", "getSelected", "getImpersonated",
				   "getImpersonatedName", "getExposedTokens", "getExposedTokenNames",
				   "getPC", "getNPC", "getPCNames", "getNPCNames", "getWithState",
				   "getWithStateNames", "getOwned", "getOwnedNames", "getVisibleTokens",
				   "getVisibleTokenNames");
	}
	
	
	public static FindTokenFunctions getInstance() {
		return instance;
	}
	
	
	@Override
	public Object childEvaluate(Parser parser, String functionName,
			List<Object> parameters) throws ParserException {

		boolean nameOnly = false;
		
		if (!MapTool.getParser().isMacroTrusted()) {
			throw new ParserException("You do not have permissions to call the " + functionName + "() function");
		}
		
		if (functionName.equals("findToken")) {
			if (parameters.size() < 1) {
				throw new ParserException("Not enough parameters for findToken(identifier)");
			}
			String mapName = parameters.size() > 1 ? parameters.get(1).toString() : null;
			return findToken(parameters.get(0).toString(), mapName);
		}
		
		String delim = ",";
		FindType findType;
		String findArgs = null;
		if (functionName.equals("currentToken")) {
			findType = FindType.CURRENT;
		} else if  (functionName.startsWith("getSelected")) {
			findType = FindType.SELECTED;
			delim = parameters.size() > 0 ? parameters.get(0).toString() : delim;
		} else if (functionName.startsWith("getImpersonated")) {
			findType = FindType.IMPERSONATED;
		} else if (functionName.startsWith("getPC")) {
			findType = FindType.PC;
			delim = parameters.size() > 0 ? parameters.get(0).toString() : delim;
		} else if (functionName.startsWith("getNPC")) {
			findType = FindType.NPC;
			delim = parameters.size() > 0 ? parameters.get(0).toString() : delim;
		} else if (functionName.startsWith("getToken")) {
			findType = FindType.ALL;
			delim = parameters.size() > 0 ? parameters.get(0).toString() : delim;
		} else if (functionName.startsWith("getExposedToken")) {
			findType = FindType.EXPOSED;
			delim = parameters.size() > 0 ? parameters.get(0).toString() : delim;
		} else if (functionName.startsWith("getWithState")) {
			if (parameters.size() < 1) {
				throw new ParserException("Not enough arguments for getWithState(state)");
			}
			findType = FindType.STATE;
			findArgs = parameters.get(0).toString();
			delim = parameters.size() > 1 ? parameters.get(1).toString() : delim;
		} else if (functionName.startsWith("getOwned")) {
			if (parameters.size() < 1) {
				throw new ParserException("Not enough arguments for getOwned(name)");
			}
			findType = FindType.OWNED;
			findArgs = parameters.get(0).toString();
			delim = parameters.size() > 1 ? parameters.get(1).toString() : delim;
		} else if (functionName.startsWith("getVisibleToken")) {
			findType = FindType.VISIBLE;
			delim = parameters.size() > 0 ? parameters.get(0).toString() : delim;		
		} else {
			return null;
		}
		 
		if (functionName.endsWith("Name") ||  functionName.endsWith("Names")) {
			nameOnly = true;
		}
		
		return getTokens(parser, findType, nameOnly, delim, findArgs);
	}


	/**
	 * Gets the names or ids of the tokens on the current map.
	 * @param parser The parser that called the function.
	 * @param findType The type of tokens to find.
	 * @param nameOnly If a list of names is wanted.
	 * @param delim The delimiter to use for lists, or "json" for a json array.
	 * @param findArgs Any arguments for the find function
	 * @return a string list that contains the ids or names of the tokens.
	 */
	private String getTokens(Parser parser, FindType findType, boolean nameOnly, String delim, String findArgs) {
		List<Token> tokenList = new LinkedList<Token>();
		ZoneRenderer zoneRenderer = MapTool.getFrame().getCurrentZoneRenderer();
		Zone zone = zoneRenderer.getZone();
		
		switch (findType) {
			case ALL:
				tokenList = zone.getTokensFiltered(new AllFilter());
				break;
			case NPC:
				tokenList = zone.getTokensFiltered(new NPCFilter());
				break;
			case PC:
				tokenList = zone.getTokensFiltered(new PCFilter());
				break;
			case SELECTED:
				tokenList = MapTool.getFrame().getCurrentZoneRenderer().getSelectedTokensList();
				break;
			case CURRENT:
				tokenList.add(((MapToolVariableResolver) parser.getVariableResolver()).getTokenInContext());
				break;
			case IMPERSONATED:
				String identity = MapTool.getFrame().getCommandPanel().getIdentity();
				tokenList.add(zone.resolveToken(identity));
				break;
			case EXPOSED:
				tokenList = zone.getTokensFiltered(new ExposedFilter());
				break;
			case STATE:
				tokenList = zone.getTokensFiltered(new StateFilter(findArgs));
				break;
			case OWNED:
				tokenList = zone.getTokensFiltered(new OwnedFilter(findArgs));
				break;
			case VISIBLE:
				for (GUID id : zoneRenderer.getVisibleTokenSet())  {
					tokenList.add(zone.getToken(id));
				}
		}
		
		ArrayList<String> values = new ArrayList<String>();
		for (Token token : tokenList) {
			if (nameOnly) {
				values.add(token.getName());
			} else {
				values.add(token.getId().toString());
			}
		}
		
		if ("json".equals(delim)) {
			return JSONArray.fromObject(values).toString();
		} else {
			return StringFunctions.getInstance().join(values, delim);
		}
	}


	/**
	 * Finds the specified token.
	 * @param identifier the name of the token.
	 * @return the id of the token.
	 */
	private String findToken(String identifier, String zoneName) {
		if (zoneName == null ){
			Zone zone = MapTool.getFrame().getCurrentZoneRenderer().getZone();
			Token token = zone.resolveToken(identifier);
			return token == null ? "" : token.getId().toString();
		} else {
			List<ZoneRenderer> zrenderers = MapTool.getFrame().getZoneRenderers();
			for (ZoneRenderer zr : zrenderers) {
				Zone zone = zr.getZone();
				if (zone.getName().equalsIgnoreCase(zoneName)) {
					Token token = zone.resolveToken(identifier);
					if (token != null) {
						return token.getId().toString();
					}
				}
			}
		}
		return "";
	}

}
