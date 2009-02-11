package net.rptools.maptool.client.functions;

import java.awt.Image;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolVariableResolver;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Grid;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.TokenFootprint;
import net.rptools.maptool.model.TokenProperty;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.util.ImageManager;
import net.rptools.maptool.util.TokenUtil;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;
import net.sf.json.JSONArray;

public class TokenPropertyFunctions extends AbstractFunction {
	
	private static final TokenPropertyFunctions instance = 
							new TokenPropertyFunctions();
	
	private TokenPropertyFunctions() {
		super(0, 4, "getPropertyNames", "getAllPropertyNames", "hasProperty", 
				    "isNPC", "isPC", "setPC", "setNPC", "getLayer", "setLayer",
					"getSize", "setSize", "getOwners", "isOwnedByAll", "isOwner",
					"resetProperty", "getProperty", "setProperty", "isPropertyEmpty",
					"getPropertyDefault", "sendToBack", "bringToFront",
					"getLibProperty", "setLibProperty", "getLibPropertyNames",
					"setPropertyType", "getPropertyType",
					"getRawProperty", "getTokenFacing", "setTokenFacing", "removeTokenFacing");
	}
	
	
	public static TokenPropertyFunctions getInstance() {
		return instance;
	}

	@Override
	public Object childEvaluate(Parser parser, String functionName,
			List<Object> parameters) throws ParserException {
		
		MapToolVariableResolver resolver = 
				(MapToolVariableResolver) parser.getVariableResolver();
		
		if (functionName.equals("getPropertyType")) {
			Token token = getTokenFromParam(resolver, "getPropertyType", parameters, 0);
			return token.getPropertyType();
		}
		
		if (functionName.equals("setPropertyType")) {
			if (parameters.size() < 1) {
				throw new ParserException("Not enough parameters for setPropertyType(name)");
			}

			Token token = getTokenFromParam(resolver, "setPropertyType", parameters, 1);
			token.setPropertyType(parameters.get(0).toString());
	 		MapTool.serverCommand().putToken(MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(), token);

			return "";
		}
		
		if (functionName.equals("getPropertyNames")) {
			Token token = getTokenFromParam(resolver, "getPropertyNames", parameters, 1);
			return getPropertyNames(token, parameters.size() > 0 ? parameters.get(0).toString() : ",");
		} 
		
		if (functionName.equals("getAllPropertyNames")) {
			if (parameters.size() < 1) {
				return getAllPropertyNames(null, parameters.size() > 0 ? parameters.get(0).toString() : ",");
			} else {
				return getAllPropertyNames(parameters.get(0).toString(), parameters.size() > 1 ? parameters.get(1).toString() : ",");
			}
		}
		
		if (functionName.equals("hasProperty")) {
			if (parameters.size() < 1) {
				throw new ParserException("Not enough parameters for hasProperty(name)");
			}
			Token token = getTokenFromParam(resolver, "hasProperty", parameters, 1);
			return hasProperty(token, parameters.get(0).toString()) ? BigDecimal.ONE : BigDecimal.ZERO;
		}
		
		
		if (functionName.equals("isNPC")) {
			Token token = getTokenFromParam(resolver, "isNPC", parameters, 0);
			return token.getType() == Token.Type.NPC ? BigDecimal.ONE : BigDecimal.ZERO;
		}
		
		if (functionName.equals("isPC")) {
			Token token = getTokenFromParam(resolver, "isPC", parameters, 0);
			return token.getType() == Token.Type.PC ? BigDecimal.ONE : BigDecimal.ZERO;			
		}
		
		if (functionName.equals("setPC")) {
			Token token = getTokenFromParam(resolver, "setPC", parameters, 0);
			token.setType(Token.Type.PC);
	 		MapTool.serverCommand().putToken(MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(), token);
	 		MapTool.getFrame().updateTokenTree();

			return "";
		}

		if (functionName.equals("setNPC")) {
			Token token = getTokenFromParam(resolver, "setNPC", parameters, 0);
			token.setType(Token.Type.NPC);
	 		MapTool.serverCommand().putToken(MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(), token);
	 		MapTool.getFrame().updateTokenTree();

	 		return "";
		}

		if (functionName.equals("getLayer")) {
			Token token = getTokenFromParam(resolver, "getLayer", parameters, 0);
			return token.getLayer().name();
		}
		
		if (functionName.equals("setLayer")) {
			Token token = getTokenFromParam(resolver, "setLayer", parameters, 1);
			if (parameters.size() < 1) {
				throw new ParserException("Not enough parameters for setLayer(layerName)");
			}			
			return setLayer(token, parameters.get(0).toString());
		}
		
		if (functionName.equalsIgnoreCase("getSize")) {
			Token token = getTokenFromParam(resolver, "getSize", parameters, 0);
			return getSize(token);
		}

		if (functionName.equalsIgnoreCase("setSize")) {
			if (parameters.size() < 1) {
				throw new ParserException("Not enough parameters for setSize(size)");
			}			
			Token token = getTokenFromParam(resolver, "setSize", parameters, 1);
			return setSize(token, parameters.get(0).toString());
		}
		
		if (functionName.equalsIgnoreCase("getOwners")) {
			Token token = getTokenFromParam(resolver, "getOwners", parameters, 1);
			return getOwners(token, parameters.size() > 0 ? parameters.get(0).toString() : ",");
		}
		
		if (functionName.equals("isOwnedByAll")) {
			Token token = getTokenFromParam(resolver, "isOwnedByAll", parameters, 0);
			return token.isOwnedByAll() ? BigDecimal.ONE : BigDecimal.ZERO;
		}
	
		if (functionName.equals("isOwner")) {
			Token token = getTokenFromParam(resolver, "isOwner", parameters, 1);
			if (parameters.size() > 0) {
				return token.isOwner(parameters.get(0).toString());
			} 
			return token.isOwner(MapTool.getPlayer().getName()); 
		}
		
		if (functionName.equals("resetProperty")) {
			if (parameters.size() < 1) {
				throw new ParserException("Not enough parameters for clearProperty(name)");
			}			
			Token token = getTokenFromParam(resolver, "resetProperty", parameters, 1);
			token.setProperty(parameters.get(0).toString(), null);
	 		MapTool.serverCommand().putToken(MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(), token);
	 		MapTool.getFrame().getCurrentZoneRenderer().getZone().putToken(token);
			return "";
		}
		
		if (functionName.equals("setProperty")) {
			if (parameters.size() < 2) {
				throw new ParserException("Not enough parameters for setProperty(name, val)");
			}		
			Token token = getTokenFromParam(resolver, "setProperty", parameters, 2);
			token.setProperty(parameters.get(0).toString(), parameters.get(1).toString());
	 		MapTool.serverCommand().putToken(MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(), token);
	 		MapTool.getFrame().getCurrentZoneRenderer().getZone().putToken(token);
			return "";
		}

		if (functionName.equals("getRawProperty")) {
			if (parameters.size() < 1) {
				throw new ParserException("Not enough parameters for getRawProperty(name)");
			}			
			Token token = getTokenFromParam(resolver, "getRawProperty", parameters, 1);
			Object val = token.getProperty(parameters.get(0).toString());			
			return val == null ? "" : val;
		}
		

		if (functionName.equals("getProperty")) {
			if (parameters.size() < 1) {
				throw new ParserException("Not enough parameters for getProperty(name)");
			}			
			Token token = getTokenFromParam(resolver, "getProperty", parameters, 1);
			return token.getEvaluatedProperty(parameters.get(0).toString());
		}
		
		
		if (functionName.equals("isPropertyEmpty")) {
			if (parameters.size() < 1) {
				throw new ParserException("Not enough parameters for isPropertyEmpty(name)");
			}			
			Token token = getTokenFromParam(resolver, "isPropertyEmpty", parameters, 1);
			return token.getProperty(parameters.get(0).toString()) == null ? BigDecimal.ONE : BigDecimal.ZERO;			
		}
		
		
		if (functionName.equals("getPropertyDefault")) {
			if (parameters.size() < 1) {
				throw new ParserException("Not enough parameters for getPropertyDefault(name)");
			}			
			
			Token token = resolver.getTokenInContext();
			String name = parameters.get(0).toString();
			
			List<TokenProperty> propertyList = MapTool.getCampaign().getCampaignProperties().getTokenPropertyList(token.getPropertyType()); 
			if (propertyList != null) {
				for (TokenProperty property : propertyList) {
					if (name.equalsIgnoreCase(property.getName()) || name.equalsIgnoreCase(property.getShortName())) {
						Object val = property.getDefaultValue();
						return val == null ? "" : val;
					}
				}
			}

			return "";
		}
		
		
		if (functionName.equals("bringToFront")) {
			ZoneRenderer renderer = MapTool.getFrame().getCurrentZoneRenderer();
			Set<GUID> tokens = new HashSet<GUID>();
			Token token = getTokenFromParam(resolver, "bringToFront", parameters, 0);

			tokens.add(token.getId());
			MapTool.serverCommand().bringTokensToFront(
					renderer.getZone().getId(), tokens);

			return "";
		}
		
		
		if (functionName.equals("sendToBack")) {
			ZoneRenderer renderer = MapTool.getFrame().getCurrentZoneRenderer();
			Set<GUID> tokens = new HashSet<GUID>();
			Token token = getTokenFromParam(resolver, "sendToBack", parameters, 0);

			tokens.add(token.getId());
			MapTool.serverCommand().sendTokensToBack(
					renderer.getZone().getId(), tokens);

			return "";
		}
		
		
		if (functionName.equals("getLibProperty")) {
			String location;
			if (parameters.size() > 1) {
				location = parameters.get(1).toString();
			} else {
				location = MapTool.getParser().getMacroSource();
			}
			Token token = MapTool.getParser().getTokenMacroLib(location);
			
			Object val = token.getProperty(parameters.get(0).toString());			
			
			// Try concert it to a number
			// Attempt to convert to a number ...
			try {
				val = new BigDecimal(val.toString());
			} catch (Exception e) {
				// Ignore, use previous value of "val"
			}
			return val == null ? "" : val;
		}
		
		
		if (functionName.equals("setLibProperty")) {
			if (parameters.size() < 2) {
				throw new ParserException("Not enough parameters for setLibProperty(name, value)");
			}			

			String location;
			if (parameters.size() > 2) {
				location = parameters.get(2).toString();
			} else {
				location = MapTool.getParser().getMacroSource();
			}
			Token token = MapTool.getParser().getTokenMacroLib(location);
			token.setProperty(parameters.get(0).toString(), parameters.get(1).toString());		
			Zone zone = MapTool.getParser().getTokenMacroLibZone(location);
			MapTool.serverCommand().putToken(zone.getId(), token);

			return "";
		}
		
		if (functionName.equals("getLibPropertyNames")) {
			String location;
			if (parameters.size() > 0) {
				location = parameters.get(0).toString();
				if (location.equals("*")) {
					location = MapTool.getParser().getMacroSource();
				}
  			} else {
				location = MapTool.getParser().getMacroSource();
			}
			Token token = MapTool.getParser().getTokenMacroLib(location);
			String delim = parameters.size() > 1 ? parameters.get(1).toString() : ",";
			
			return getPropertyNames(token, delim);
		}
		
		
		if (functionName.equals("getTokenFacing")) {
			Token token = getTokenFromParam(resolver, "getTokenFacing", parameters, 0);
			if (token.getFacing() == null) {
				return "";
			}
			return BigDecimal.valueOf(token.getFacing());
		}
		
		if (functionName.equals("setTokenFacing")) {
			if (parameters.size() < 1) {
				throw new ParserException("Not enough arguments for setTokenFacing()");
			}
			if (!(parameters.get(0) instanceof BigDecimal)) { 
				throw new ParserException("First argument to setTokenFacing() must be a number");
			}
			
			Token token = getTokenFromParam(resolver, "setTokenFacing", parameters, 1);
			token.setFacing(((BigDecimal)parameters.get(0)).intValue());
			return "";
		}
		
		if (functionName.equals("removeTokenFacing")) {
			Token token = getTokenFromParam(resolver, "removeTokenFacing", parameters, 0);
			token.setFacing(null);
			return "";
		}
		
		throw new ParserException("Unknown function " + functionName);
	}

	/**
	 * Gets the size of the token.
	 * @param token The token to get the size of.
	 * @return the size of the token.
	 */
	private String getSize(Token token) {
		Grid grid = MapTool.getFrame().getCurrentZoneRenderer().getZone().getGrid();
		for (TokenFootprint footprint : grid.getFootprints()) {
			if (token.isSnapToScale() && token.getFootprint(grid) == footprint) {
				return footprint.getName();
			}	
		} 
		return "";
	}
	
	/** 
	 * Sets the size of the token.
	 * @param token The token to set the size of.
	 * @param size The size to set the token to.
	 * @return The new size of the token.
	 * @throws ParserException if the size specified is an invalid size.
	 */
	private String setSize(Token token, String size) throws ParserException {
		ZoneRenderer renderer = MapTool.getFrame().getCurrentZoneRenderer();
		Grid grid = renderer.getZone().getGrid();
		for (TokenFootprint footprint : grid.getFootprints()) {
			if (token.isSnapToScale() && footprint.getName().equalsIgnoreCase(size)) {
				token.setFootprint(grid, footprint);
				token.setSnapToScale(true);
				renderer.flush(token);
				MapTool.serverCommand().putToken(renderer.getZone().getId(), token);
				renderer.repaint();
		 		MapTool.getFrame().updateTokenTree();
		 		MapTool.serverCommand().putToken(MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(), token);
		 		MapTool.getFrame().getCurrentZoneRenderer().getZone().putToken(token);
				return getSize(token);
			}	
		} 
		throw new ParserException("Invalid token size (" + size + ")");
	}

	/**
	 * Sets the layer of the token.
	 * @param token The token to move to a different layer.
	 * @param layerName the name of the layer to move the token to.
	 * @return the name of the layer the token was moved to.
	 * @throws ParserException if the layer name is invalid.
	 */
	private String setLayer(Token token, String layerName) throws ParserException {
		
		Zone.Layer layer;
		
		if (layerName.equalsIgnoreCase("token")) {
			layer = Zone.Layer.TOKEN;
		} else if (layerName.equalsIgnoreCase("background")) {
			layer = Zone.Layer.BACKGROUND;
		} else if (layerName.equalsIgnoreCase("gm") || layerName.equalsIgnoreCase("hidden")) {
			layer = Zone.Layer.GM;
		} else if (layerName.equalsIgnoreCase("object")) {
			layer = Zone.Layer.OBJECT;
		} else {
			throw new ParserException("Unknown Layer (" + layerName + ")");
		}
		
		token.setLayer(layer);
		switch (layer) {
			case BACKGROUND:
			case OBJECT:
				token.setShape(Token.TokenShape.TOP_DOWN);
				break;
			case GM:
			case TOKEN:
				Image image = ImageManager.getImage(AssetManager.getAsset(token.getImageAssetId()));
				if (image == null || image == ImageManager.UNKNOWN_IMAGE) {
					token.setShape(Token.TokenShape.TOP_DOWN);
				} else {
					token.setShape(TokenUtil.guessTokenType(image));
				}
				break;
		}	
			
 		MapTool.serverCommand().putToken(MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(), token);
 		MapTool.getFrame().getCurrentZoneRenderer().getZone().putToken(token);
 		MapTool.getFrame().updateTokenTree();
		return layerName;
	}

	/**
	 * Checks to see if the token has the specified property.
	 * @param token The token to check.
	 * @param name The name of the property to check.
	 * @return true if the token has the property.
	 */
	private boolean hasProperty(Token token, String name) {
		Object val = token.getProperty(name);
		if (val == null) {
			return false;
		}
		
		if (val.toString().length() == 0) {
			return false;
		}
		
		return true;
	}

	/** 
	 * Gets all the property names for the specified type.
	 * If type is null then all the property names for all types are returned.
	 * @param type The type of property.
	 * @param delim The list delimiter.
	 * @return a string list containing the property names.
	 * @throws ParserException 
	 */
	private String getAllPropertyNames(String type, String delim) throws ParserException {
		if (type == null || type.length() == 0 || type.equals("*")) {
			Map<String, List<TokenProperty>> pmap = 
					MapTool.getCampaign().getCampaignProperties().getTokenTypeMap();
			ArrayList<String> namesList = new ArrayList<String>();
			
			for (Entry<String, List<TokenProperty>> entry : pmap.entrySet()) {
				for (TokenProperty tp : entry.getValue()) {
					namesList.add(tp.getName());
				}
			}
			if ("json".equals(delim)) {
				return JSONArray.fromObject(namesList).toString();
			} else {
				return StringFunctions.getInstance().join(namesList, delim);
			}
		} else {
			List<TokenProperty> props = MapTool.getCampaign().getCampaignProperties().getTokenPropertyList(type);
			if (props == null) {
				throw new ParserException("Unknown property type "+ type);
			}
			ArrayList<String> namesList = new ArrayList<String>();
			for (TokenProperty tp : props) {
				namesList.add(tp.getName());
			}
			if ("json".equals(delim)) {
				return JSONArray.fromObject(namesList).toString();
			} else {
				return StringFunctions.getInstance().join(namesList);
			}
		}
	}

	/**
	 * Creates a string list delimited by delim of the names of all the 
	 * properties for a given token.
	 * @param token The token to get the property names for.
	 * @param delim The delimiter for the list.
	 * @return the string list of property names.
	 */
	private String getPropertyNames(Token token, String delim) {
		String[] names = new String[token.getPropertyNames().size()]; 
		token.getPropertyNames().toArray(names);
		if ("json".equals(delim)) {
			return JSONArray.fromObject(names).toString();
		} else {
			return StringFunctions.getInstance().join(names, delim);
		} 
	}

	/**
	 * Gets the owners for the token.
	 * @param token The token to get the owners for.
	 * @param delim the delimiter for the list.
	 * @return a string list of the token owners.
	 */
	public String getOwners(Token token, String delim) {
		String[] owners = new String[token.getOwners().size()];
		token.getOwners().toArray(owners);		
		if ("json".endsWith(delim)) {
			return JSONArray.fromObject(owners).toString();
		} else {
			return StringFunctions.getInstance().join(owners, delim);
		}
	}
	
	

	/**
	 * Gets the token from the specified index or returns the token in context. This method
	 * will check the list size before trying to retrieve the token so it is safe to use
	 * for functions that have the token as a optional argument.
	 * @param res The variable resolver.
	 * @param functionName The function name (used for generating exception messages).
	 * @param param The parameters for the function.
	 * @param index The index to find the token at.
	 * @return the token.
	 * @throws ParserException if a token is specified but the macro is not trusted, or the 
	 *                         specified token can not be found, or if no token is specified
	 *                         and no token is impersonated.
	 */
	private Token getTokenFromParam(MapToolVariableResolver res, String functionName, List<Object> param, int index) throws ParserException {
		Token token;
		if (param.size() > index) {
			if (!MapTool.getParser().isMacroTrusted()) {
				throw new ParserException(functionName + "(): You do not have permission to refer to another token");
			}
			
			token = FindTokenFunctions.findToken(param.get(index).toString(), null);
			if (token == null) {
				throw new ParserException(functionName + "(): Unknown token or id" + param.get(index));
			}
		} else {
			token = res.getTokenInContext();
			if (token == null) {
				throw new ParserException(functionName + "(): No impersonated token");
			}
		}
		return token;
	}
}
