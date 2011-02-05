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

import java.awt.Color;
import java.math.BigDecimal;
import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolUtil;
import net.rptools.maptool.client.MapToolVariableResolver;
import net.rptools.maptool.language.I18N;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.util.StringUtil;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;

public class TokenHaloFunction extends AbstractFunction {
	// TODO: This is a copy of the array in the {@link TokenPopupMenu} (which is apparently temporary)
	private final static TokenHaloFunction instance = new TokenHaloFunction();

	private TokenHaloFunction() {
		super(0, 3, "getHalo", "setHalo");
	}

	/**
	 * Gets the singleton Halo instance.
	 * 
	 * @return the Halo instance.
	 */
	public static TokenHaloFunction getInstance() {
		return instance;
	}

	@Override
	public Object childEvaluate(Parser parser, String functionName, List<Object> args)
			throws ParserException {

		if (functionName.equals("getHalo")) {
			return getHalo(parser, args);
		} else {
			return setHalo(parser, args);
		}

	}

	/**
	 * Gets the halo for the token.
	 * 
	 * @param token
	 *            the token to get the halo for.
	 * @return the halo.
	 */
	public Object getHalo(Token token) {
		if (token.getHaloColor() != null) {
			return "#" + Integer.toHexString(token.getHaloColor().getRGB()).substring(2);
		} else {
			return "None";
		}
	}

	/**
	 * Sets the halo color of the token.
	 * 
	 * @param token
	 *            the token to set halo of.
	 * @param value
	 *            the value to set.
	 * @throws ParserException
	 *             if there is an error determining color.
	 */
	public void setHalo(Token token, Object value) throws ParserException {
		if (value instanceof Color) {
			token.setHaloColor((Color) value);
		} else if (value instanceof BigDecimal) {
			token.setHaloColor(new Color(((BigDecimal) value).intValue()));
		} else {
			String col = value.toString();
			if (StringUtil.isEmpty(col) || col.equalsIgnoreCase("none") || col.equalsIgnoreCase("default")) {
				token.setHaloColor(null);
			} else {
				String hex = col;
				Color color = MapToolUtil.getColor(hex);
				token.setHaloColor(color);
			}
//			else if (col.startsWith("#") || col.startsWith("0x")) { // It's a hexadecimal color representation
//				String hex;
//				if (col.startsWith("#")) {
//					hex = col.substring(1);
//				} else {
//					hex = col.substring(2);
//				}
//				if (hex.length() == 3) {
//					try {
//						Color color = new Color(Integer.parseInt(hex.substring(0, 1), 16) * 15,
//												Integer.parseInt(hex.substring(1, 2), 16) * 15,
//												Integer.parseInt(hex.substring(2, 3), 16) * 15);
//						token.setHaloColor(color);
//					} catch (NumberFormatException e) {
//						throw new ParserException(I18N.getText("macro.function.haloFunctions.invalidColor", col));
//					}
//				} else if (hex.length() == 6) {
//					try {
//						Color color = new Color(Integer.parseInt(hex.substring(0, 2), 16),
//												Integer.parseInt(hex.substring(2, 4), 16),
//												Integer.parseInt(hex.substring(4, 6), 16));
//						token.setHaloColor(color);
//					} catch (NumberFormatException e) {
//						throw new ParserException(I18N.getText("macro.function.haloFunctions.invalidColor", col));
//					}
//				} else if (hex.length() == 8) {
//					try {
//						Color color = new Color(Integer.parseInt(hex.substring(2, 4), 16),
//												Integer.parseInt(hex.substring(4, 6), 16),
//												Integer.parseInt(hex.substring(6, 8), 16));
//						token.setHaloColor(color);
//					} catch (NumberFormatException e) {
//						throw new ParserException(I18N.getText("macro.function.haloFunctions.invalidColor", col));
//					}
//				} else {
//					throw new ParserException(I18N.getText("macro.function.haloFunctions.invalidColor", col));
//				}
//			} else {
//				// Try to find the halo color in the array
//				boolean found = false;
//				String cname = (String) value;
//				for (Object[] colval : COLOR_ARRAY) {
//					if (cname.equalsIgnoreCase((String) colval[0])) {
//						token.setHaloColor((Color) colval[1]);
//						found = true;
//						break;
//					}
//				}
//				if (!found) {
//					throw new ParserException(I18N.getText("macro.function.haloFunctions.invalidColor", col));
//				}
//			}
		}
		// TODO: This works for now but could result in a lot of resending of data
		MapTool.serverCommand().putToken(MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(), token);
		MapTool.getFrame().getCurrentZoneRenderer().getZone().putToken(token);
	}

	/**
	 * Gets the halo of the token.
	 * 
	 * @param parser
	 *            The parser that called the object.
	 * @param args
	 *            The arguments.
	 * @return the halo color.
	 * @throws ParserException
	 *             if an error occurs.
	 */
	private Object getHalo(Parser parser, List<Object> args) throws ParserException {
		Token token;

		if (args.size() == 1) {
			if (!MapTool.getParser().isMacroTrusted()) {
				throw new ParserException(I18N.getText("macro.function.general.noPermOther", "getHalo"));
			}
			token = FindTokenFunctions.findToken(args.get(0).toString(), null);
			if (token == null) {
				throw new ParserException(I18N.getText("macro.function.general.unknownToken", "getHalo", args.get(0).toString()));
			}
		} else if (args.size() == 0) {
			MapToolVariableResolver res = (MapToolVariableResolver) parser.getVariableResolver();
			token = res.getTokenInContext();
			if (token == null) {
				throw new ParserException(I18N.getText("macro.function.general.noImpersonated", "getHalo"));
			}
		} else {
			throw new ParserException(I18N.getText("macro.function.general.tooManyParam", "getHalo", 1, args.size()));
		}
		return getHalo(token);
	}

	/**
	 * Sets the halo of the token.
	 * 
	 * @param parser
	 *            The parser that called the object.
	 * @param args
	 *            The arguments.
	 * @return the halo color.
	 * @throws ParserException
	 *             if an error occurs.
	 */
	private Object setHalo(Parser parser, List<Object> args) throws ParserException {

		Token token;
		Object value = args.get(0);

		switch (args.size()) {
		case 0:
			throw new ParserException(I18N.getText("macro.function.general.notEnoughParam", "setHalo", 1, args.size()));
		default:
			throw new ParserException(I18N.getText("macro.function.general.tooManyParam", "setHalo", 2, args.size()));
		case 1:
			MapToolVariableResolver res = (MapToolVariableResolver) parser.getVariableResolver();
			token = res.getTokenInContext();
			if (token == null) {
				throw new ParserException(I18N.getText("macro.function.general.noImpersonated", "setHalo"));
			}
			break;
		case 2:
			if (!MapTool.getParser().isMacroTrusted()) {
				throw new ParserException(I18N.getText("macro.function.general.noPermOther", "setHalo"));
			}
			token = FindTokenFunctions.findToken(args.get(1).toString(), null);
			if (token == null) {
				throw new ParserException(I18N.getText("macro.function.general.unknownToken", "setHalo", args.get(1).toString()));
			}
		}
		setHalo(token, value);
		return value;
	}
}
