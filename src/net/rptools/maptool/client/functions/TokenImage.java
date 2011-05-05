/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
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
import net.rptools.maptool.language.I18N;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;

public class TokenImage extends AbstractFunction {

	/** Singleton instance. */
	private final static TokenImage instance = new TokenImage();

	private final static Pattern assetRE = Pattern.compile("asset://([^-]+)");

	enum imageType {
		TOKEN_IMAGE(0),
		TOKEN_PORTRAIT(1),
		TOKEN_HANDOUT(2);

		int value;

		imageType(int v) {
			value = v;
		}

		final int getValue() {
			return value;
		}
	};

	public final static String SET_IMAGE = "setImage";
	public final static String SET_PORTRAIT = "setTokenPortrait";
	public final static String SET_HANDOUT = "setTokenHandout";

	private TokenImage() {
		super(0, 2, "getTokenImage", "getTokenPortrait", "getTokenHandout",
				"setTokenImage", "setTokenPortrait", "setTokenHandout",
				"getImage");
	}

	/**
	 * Gets the TokenImage instance.
	 * 
	 * @return the instance.
	 */
	public static TokenImage getInstance() {
		return instance;
	}

	@Override
	public Object childEvaluate(Parser parser, String functionName, List<Object> args) throws ParserException {
		Token token;
		BigDecimal size = null;
		Zone zone = MapTool.getFrame().getCurrentZoneRenderer().getZone();

		if (functionName.equals("setTokenImage")) {
			if (args.size() != 1) {
				throw new ParserException(I18N.getText("macro.function.general.wrongNumParam", "setTokenImage", 1, args.size()));
			}
			MapToolVariableResolver res = (MapToolVariableResolver) parser.getVariableResolver();
			token = res.getTokenInContext();
			if (token == null) {
				throw new ParserException(I18N.getText("macro.function.general.noImpersonated", "setTokenImage"));
			}
			setImage(token, args.get(0).toString());
			zone.putToken(token);
			MapTool.serverCommand().putToken(zone.getId(), token);
			return "";
		}

		if (functionName.equals("setTokenPortrait")) {
			if (args.size() != 1) {
				throw new ParserException(I18N.getText("macro.function.general.wrongNumParam", "setTokenPortrait", 1, args.size()));
			}
			MapToolVariableResolver res = (MapToolVariableResolver) parser.getVariableResolver();
			token = res.getTokenInContext();
			if (token == null) {
				throw new ParserException(I18N.getText("macro.function.general.noImpersonated", "setTokenPortrait"));
			}
			setPortrait(token, args.get(0).toString());
			zone.putToken(token);
			MapTool.serverCommand().putToken(zone.getId(), token);
			return "";
		}

		if (functionName.equals("setTokenHandout")) {
			if (args.size() != 1) {
				throw new ParserException(I18N.getText("macro.function.general.wrongNumParam", "setTokenHandout", 1, args.size()));
			}
			MapToolVariableResolver res = (MapToolVariableResolver) parser.getVariableResolver();
			token = res.getTokenInContext();
			if (token == null) {
				throw new ParserException(I18N.getText("macro.function.general.noImpersonated", "setTokenHandout"));
			}
			setHandout(token, args.get(0).toString());
			zone.putToken(token);
			MapTool.serverCommand().putToken(zone.getId(), token);
			return "";
		}

		if (functionName.equals("getImage")) {
			if (args.size() == 0) {
				throw new ParserException(I18N.getText("macro.function.general.notEnoughParam", functionName, 1, args.size()));
			} else if (args.size() > 2) {
				throw new ParserException(I18N.getText("macro.function.general.tooManyParam", functionName, 2, args.size()));
			}
			token = findImageToken(args.get(0).toString(), "getImage");
			if (token == null) {
				throw new ParserException(I18N.getText("macro.function.general.unknownToken", functionName, args.get(0).toString()));
			}
			if (args.size() > 1) {
				size = (BigDecimal) args.get(1);
			}
		} else if (args.size() > 0) {
			if (args.get(0) instanceof GUID) {
				token = MapTool.getFrame().getCurrentZoneRenderer().getZone().getToken((GUID) args.get(0));
			} else if (args.get(0) instanceof BigDecimal) {
				MapToolVariableResolver res = (MapToolVariableResolver) parser.getVariableResolver();
				token = res.getTokenInContext();
				size = (BigDecimal) args.get(0);
			} else
				throw new ParserException(I18N.getText("macro.function.general.argumentTypeInvalid", functionName, 1, args.get(0).toString()));
		} else {
			MapToolVariableResolver res = (MapToolVariableResolver) parser.getVariableResolver();
			token = res.getTokenInContext();
			if (token == null) {
				throw new ParserException(I18N.getText("macro.function.general.noImpersonated", functionName));
			}
		}
		StringBuilder assetId = new StringBuilder("asset://");
		if (functionName.equals("getTokenImage")) {
			if (token.getImageAssetId() == null) {
				return "";
			}
			assetId.append(token.getImageAssetId().toString());
		} else if (functionName.equals("getTokenPortrait")) {
			if (token.getPortraitImage() == null) {
				return "";
			}
			assetId.append(token.getPortraitImage().toString());
		} else if (functionName.equals("getImage")) {
			if (token.getImageAssetId() == null) {
				return "";
			}
			assetId.append(token.getImageAssetId().toString());
		} else {
			if (token.getCharsheetImage() == null) {
				return "";
			}
			assetId.append(token.getCharsheetImage().toString());
		}
		if (size != null) {
			assetId.append("-");
			// Constrain it slightly, so its greater than 1
			int i = Math.max(size.intValue(), 1);
			assetId.append(i);
		}
		return assetId.toString();
	}

	private static void assignImage(Token token, String assetName, imageType type, String func) throws ParserException {
		Matcher m = assetRE.matcher(assetName);

		String assetId;
		if (m.matches()) {
			assetId = m.group(1);
		} else if (assetName.toLowerCase().startsWith("image:")) {
			assetId = findImageToken(assetName, func).getImageAssetId().toString();
		} else {
			throw new ParserException(I18N.getText("macro.function.general.argumentTypeInvalid", func, 1, assetName));
		}
		switch (type) {
		case TOKEN_IMAGE:
			token.setImageAsset(null, new MD5Key(assetId));
			break;
		case TOKEN_PORTRAIT:
			token.setPortraitImage(new MD5Key(assetId));
			break;
		case TOKEN_HANDOUT:
			token.setCharsheetImage(new MD5Key(assetId));
			break;
		default:
			throw new IllegalArgumentException("unknown image type " + type);
		}
		MapTool.serverCommand().putToken(MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(), token);
	}

	public static void setImage(Token token, String assetName) throws ParserException {
		assignImage(token, assetName, imageType.TOKEN_IMAGE, SET_IMAGE);
	}

	public static void setPortrait(Token token, String assetName) throws ParserException {
		assignImage(token, assetName, imageType.TOKEN_PORTRAIT, SET_PORTRAIT);
	}

	public static void setHandout(Token token, String assetName) throws ParserException {
		assignImage(token, assetName, imageType.TOKEN_HANDOUT, SET_HANDOUT);
	}

	public static Token findImageToken(final String name, String functionName) throws ParserException {
		Token imageToken = null;
		if (name != null && name.length() > 0) {
			List<ZoneRenderer> zrenderers = MapTool.getFrame().getZoneRenderers();
			for (ZoneRenderer zr : zrenderers) {
				List<Token> tokenList = zr.getZone().getTokensFiltered(new Zone.Filter() {
					public boolean matchToken(Token t) {
						return t.getName().equalsIgnoreCase(name);
					}
				});
				for (Token token : tokenList) {
					// If we are not the GM and the token is not visible to players then we don't
					// let them get functions from it.
					if (!MapTool.getPlayer().isGM() && !token.isVisible()) {
						throw new ParserException(I18N.getText("macro.function.general.unknownToken", functionName, name));
					}
					if (imageToken != null) {
						throw new ParserException("Duplicate " + name + " tokens");
					}
					imageToken = token;
				}
			}
			return imageToken;
		}
		throw new ParserException(I18N.getText("macro.function.general.unknownToken", functionName, name));
	}
}
