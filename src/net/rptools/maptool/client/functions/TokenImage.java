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
package net.rptools.maptool.client.functions;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.rptools.lib.MD5Key;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolVariableResolver;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;

public class TokenImage extends AbstractFunction {

	/** Singleton instance. */
	private final static TokenImage instance = new TokenImage();

	private TokenImage() {
		super(0,2, "getTokenImage", "getTokenPortrait", "getTokenHandout", "setTokenImage", "getImage");
	}

	
	/**
	 * Gets the TokenImage instance.
	 * @return the instance.
	 */
	public static TokenImage getInstance() {
		return instance;
	}
	
	
	@Override
	public Object childEvaluate(Parser parser, String functionName, List<Object> args)
			throws ParserException {
		
		Token token;
		BigDecimal size = null;

		if (functionName.equals("setTokenImage")) {
			if (args.size() < 1) {
				throw new ParserException("Not enough arguments for setImage(asset)");
			}
			MapToolVariableResolver res = (MapToolVariableResolver)parser.getVariableResolver();
			token = res.getTokenInContext();
			if (token == null) {
				throw new ParserException(functionName + "(): No Impersonated token.");
			}
			setImage(token, args.get(0).toString());
			return "";
		}
		
		
		if (functionName.equals("getImage")) {
			if (args.size() < 1) {
				throw new ParserException("Not enough arguments for getImage(imageName)");
			}
			token = findImageToken(args.get(0).toString());				
			if (token == null) {
				throw new ParserException("Can not find image token " + args.get(0));
			}
			if (args.size() > 1) {
				size = (BigDecimal) args.get(1);				
			}
		} else if  (args.size() > 0 && args.get(0) instanceof GUID) {
			token = MapTool.getFrame().getCurrentZoneRenderer().getZone().getToken((GUID)args.get(0));
		} else if (args.size() > 0 && args.get(0) instanceof BigDecimal) {
			MapToolVariableResolver res = (MapToolVariableResolver)parser.getVariableResolver();
			token = res.getTokenInContext();
			size = (BigDecimal) args.get(0);
		} else if (args.size() > 0) {
			throw new ParserException("getTokenImage() unknown argumets.");
		} else {
			MapToolVariableResolver res = (MapToolVariableResolver)parser.getVariableResolver();
			token = res.getTokenInContext();
			if (token == null) {
				throw new ParserException(functionName + "(): No Impersonated token.");
			}
		}
		
		
		StringBuilder assetId = new StringBuilder("asset://");
		if (functionName.equals("getTokenImage")) {
			assetId.append(token.getImageAssetId().toString());
		} else if (functionName.equals("getTokenPortrait")) {
			assetId.append(token.getPortraitImage().toString());
		} else if (functionName.equals("getImage")) {
			assetId.append(token.getImageAssetId().toString());
		} else {
			assetId.append(token.getCharsheetImage().toString());
		}
		if (size != null) {
			assetId.append("-");
			// Constrain it slightly, so its greater than 1 
			int i = Math.max(size.intValue(),1);
			assetId.append(i);
		}
		return assetId.toString();
	}
	
	
	private void setImage(Token token, String assetName) throws ParserException {
		Matcher m = Pattern.compile("asset://([^-]+).*").matcher(assetName);
		
		String assetId; 
		
		if (m.matches()) {
			assetId = m.group(1);
		} else if (assetName.toLowerCase().startsWith("image:")) {
			assetId = findImageToken(assetName).getImageAssetId().toString();
		} else {
			throw new ParserException("Invalid argument for setImage()");
		}
				
		token.setImageAsset(null, new MD5Key(assetId));
 		MapTool.serverCommand().putToken(MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(), token);
	}
	
	
	private  Token findImageToken(final String name) throws ParserException {
		Token imageToken = null;
		if (name != null && name.length() > 0) {
			List<ZoneRenderer> zrenderers = MapTool.getFrame().getZoneRenderers();
			for (ZoneRenderer zr : zrenderers) {
				List<Token> tokenList = zr.getZone().getTokensFiltered(new Zone.Filter() {
					public boolean matchToken(Token t) {
						return t.getName().equalsIgnoreCase(name);
					}});

				for (Token token : tokenList) {
					// If we are not the GM and the token is not visible to players then we don't
					// let them get functions from it.	
					if (!MapTool.getPlayer().isGM() && !token.isVisible()) {
						throw new ParserException("Unable to find image token  " + name);
					}
					if (imageToken != null) {
						throw new ParserException("Duplicate " + name + " tokens");
					}

					imageToken = token;
				}
			}
			return imageToken;
		}
		throw new ParserException("Unable to find image token  " + name);		
	}
}
