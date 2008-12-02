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

public class TokenPropertyFunctions extends AbstractFunction {
	
	private static final TokenPropertyFunctions instance = 
							new TokenPropertyFunctions();
	
	private TokenPropertyFunctions() {
		super(0, 3, "getPropertyNames", "getAllPropertyNames", "hasProperty", 
				    "isNPC", "isPC", "setPC", "setNPC", "getLayer", "setLayer",
					"getSize", "setSize", "getOwners", "isOwnedByAll", "isOwner",
					"resetProperty", "getProperty", "setProperty", "isPropertyEmpty",
					"getPropertyDefault", "sendToBack", "bringToFront",
					"getLibProperty", "setLibProperty", "getLibPropertyNames",
					"setPropertyType", "getPropertyType");
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
			Token token = resolver.getTokenInContext();
			return token.getPropertyType();
		}
		
		if (functionName.equals("setPropertyType")) {
			if (parameters.size() < 1) {
				throw new ParserException("Not enough parameters for setPropertyType(name)");
			}

			Token token = resolver.getTokenInContext();
			token.setPropertyType(parameters.get(0).toString());
	 		MapTool.serverCommand().putToken(MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(), token);

			return "";
		}
		
		if (functionName.equals("getPropertyNames")) {
			return getPropertyNames(resolver.getTokenInContext(), parameters.size() > 0 ? parameters.get(0).toString() : ",");
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
			return hasProperty(resolver.getTokenInContext(), parameters.get(0).toString()) ? BigDecimal.ONE : BigDecimal.ZERO;
		}
		
		
		if (functionName.equals("isNPC")) {
			return resolver.getTokenInContext().getType() == Token.Type.NPC ? BigDecimal.ONE : BigDecimal.ZERO;
		}
		
		if (functionName.equals("isPC")) {
			return resolver.getTokenInContext().getType() == Token.Type.PC ? BigDecimal.ONE : BigDecimal.ZERO;			
		}
		
		if (functionName.equals("setPC")) {
			resolver.getTokenInContext().setType(Token.Type.PC);
	 		MapTool.getFrame().updateTokenTree();
			return "";
		}

		if (functionName.equals("setNPC")) {
			resolver.getTokenInContext().setType(Token.Type.NPC);
	 		MapTool.getFrame().updateTokenTree();
			return "";
		}

		if (functionName.equals("getLayer")) {
			return resolver.getTokenInContext().getLayer().name();
		}
		
		if (functionName.equals("setLayer")) {
			if (parameters.size() < 1) {
				throw new ParserException("Not enough parameters for setLayer(layerName)");
			}			
			return setLayer(resolver.getTokenInContext(), parameters.get(0).toString());
		}
		
		if (functionName.equalsIgnoreCase("getSize")) {
			return getSize(resolver.getTokenInContext());
		}

		if (functionName.equalsIgnoreCase("setSize")) {
			if (parameters.size() < 1) {
				throw new ParserException("Not enough parameters for setSize(size)");
			}			
			return setSize(resolver.getTokenInContext(), parameters.get(0).toString());
		}
		
		if (functionName.equalsIgnoreCase("getOwners")) {
			return getOwners(resolver.getTokenInContext(), parameters.size() > 0 ? parameters.get(0).toString() : ",");
		}
		
		if (functionName.equals("isOwnedByAll")) {
			return resolver.getTokenInContext().isOwnedByAll() ? BigDecimal.ONE : BigDecimal.ZERO;
		}
	
		if (functionName.equals("isOwner")) {
			if (parameters.size() > 0) {
				return resolver.getTokenInContext().isOwner(parameters.get(0).toString());
			} 
			return resolver.getTokenInContext().isOwner(MapTool.getPlayer().getName()); 
		}
		
		if (functionName.equals("resetProperty")) {
			if (parameters.size() < 1) {
				throw new ParserException("Not enough parameters for clearProperty(name)");
			}			
			Token token = resolver.getTokenInContext();
			token.setProperty(parameters.get(0).toString(), null);
			return "";
		}
		
		if (functionName.equals("setProperty")) {
			if (parameters.size() < 2) {
				throw new ParserException("Not enough parameters for setProperty(name, val)");
			}		
			Token token = resolver.getTokenInContext();
			token.setProperty(parameters.get(0).toString(), parameters.get(1));
			return "";
		}

		if (functionName.equals("getProperty")) {
			if (parameters.size() < 1) {
				throw new ParserException("Not enough parameters for getProperty(name)");
			}			
			Token token = resolver.getTokenInContext();
			Object val = token.getProperty(parameters.get(0).toString());			
			return val == null ? "" : val;
		}
		
		
		if (functionName.equals("isPropertyEmpty")) {
			if (parameters.size() < 1) {
				throw new ParserException("Not enough parameters for getProperty(name)");
			}			
			Token token = resolver.getTokenInContext();
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
			Token token = resolver.getTokenInContext();

			tokens.add(token.getId());
			MapTool.serverCommand().bringTokensToFront(
					renderer.getZone().getId(), tokens);

			MapTool.getFrame().refresh();
			return "";
		}
		
		
		if (functionName.equals("sendToBack")) {
			ZoneRenderer renderer = MapTool.getFrame().getCurrentZoneRenderer();
			Set<GUID> tokens = new HashSet<GUID>();
			Token token = resolver.getTokenInContext();

			tokens.add(token.getId());
			MapTool.serverCommand().sendTokensToBack(
					renderer.getZone().getId(), tokens);

			MapTool.getFrame().refresh();
			return "";
		}
		
		
		if (functionName.equals("getLibProperty")) {
			if (parameters.size() < 1) {
				throw new ParserException("Not enough parameters for getLibProperty(name)");
			}			

			String location;
			if (parameters.size() > 1) {
				location = parameters.get(1).toString();
			} else {
				location = MapTool.getParser().getMacroSource();
			}
			Token token = MapTool.getParser().getTokenMacroLib(location);
			
			Object val = token.getProperty(parameters.get(0).toString());			
			return val == null ? "" : val;
		}
		
		
		if (functionName.equals("setLibProperty")) {
			if (parameters.size() < 3) {
				throw new ParserException("Not enough parameters for setLibProperty(name, value)");
			}			

			String location;
			if (parameters.size() > 2) {
				location = parameters.get(2).toString();
			} else {
				location = MapTool.getParser().getMacroSource();
			}
			Token token = MapTool.getParser().getTokenMacroLib(location);
			token.setProperty(parameters.get(0).toString(), parameters.get(1));		
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
		
		return null;
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
			return StringFunctions.getInstance().join(namesList, delim);
		} else {
			List<TokenProperty> props = MapTool.getCampaign().getCampaignProperties().getTokenPropertyList(type);
			if (props == null) {
				throw new ParserException("Unknown property type "+ type);
			}
			ArrayList<String> namesList = new ArrayList<String>();
			for (TokenProperty tp : props) {
				namesList.add(tp.getName());
			}
			return StringFunctions.getInstance().join(namesList);
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
		return StringFunctions.getInstance().join(names, delim);
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
		return StringFunctions.getInstance().join(owners, delim);
	}
	
	

}
