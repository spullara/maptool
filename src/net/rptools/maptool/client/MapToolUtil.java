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
package net.rptools.maptool.client;

import java.awt.Color;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.drawing.DrawablePaint;
import net.rptools.maptool.model.drawing.DrawableTexturePaint;
import net.rptools.maptool.util.StringUtil;

public class MapToolUtil {

//    private static Random random = new Random ( System.currentTimeMillis() );
	private static Random random = new SecureRandom();

	private static AtomicInteger nextTokenId = new AtomicInteger(1);

	/**
	 * The map of color names to color values
	 */
	private static final Map<String, Color> COLOR_MAP = new TreeMap<String, Color>();
	private static final Map<String, Color> COLOR_MAP_HTML = new HashMap<String, Color>();

	/**
	 * Set up the color map
	 */
	static {
		// Built-in Java colors that happen to match the values used by HTML...
		COLOR_MAP.put("black", Color.BLACK);
		COLOR_MAP.put("blue", Color.BLUE);
		COLOR_MAP.put("cyan", Color.CYAN);
		COLOR_MAP.put("gray", Color.GRAY);
		COLOR_MAP.put("magenta", Color.MAGENTA);
		COLOR_MAP.put("red", Color.RED);
		COLOR_MAP.put("white", Color.WHITE);
		COLOR_MAP.put("yellow", Color.YELLOW);

		// The built-in Java colors that DO NOT match the HTML colors...
		COLOR_MAP.put("darkgray", new Color(0xA9, 0xA9, 0xA9)); // Color.DARK_GRAY
		COLOR_MAP.put("green", new Color(0x00, 0x80, 0x00)); // Color.GREEN
		COLOR_MAP.put("lightgray", new Color(0xD3, 0xD3, 0xD3)); // Color.LIGHT_GRAY
		COLOR_MAP.put("orange", new Color(0xFF, 0xA5, 0x00)); // Color.ORANGE
		COLOR_MAP.put("pink", new Color(0xFF, 0xC0, 0xCB)); // Color.PINK

		// And the HTML colors that don't exist at all as built-in Java values...
		COLOR_MAP.put("aqua", new Color(0x00, 0xFF, 0xFF)); // same as Color.CYAN
		COLOR_MAP.put("fuchsia", new Color(0xFF, 0x00, 0xFF)); // same as Color.MAGENTA
		COLOR_MAP.put("lime", new Color(0xBF, 0xFF, 0x00));
		COLOR_MAP.put("maroon", new Color(0x80, 0x00, 0x00));
		COLOR_MAP.put("navy", new Color(0x00, 0x00, 0x80));
		COLOR_MAP.put("olive", new Color(0x80, 0x80, 0x00));
		COLOR_MAP.put("purple", new Color(0x80, 0x00, 0x80));
		COLOR_MAP.put("silver", new Color(0xC0, 0xC0, 0xC0));
		COLOR_MAP.put("teal", new Color(0x00, 0x80, 0x80));

		// These are valid HTML colors.  When getFontColor() is called, if one of these is
		// selected then the name is returned.  When another value is selected, the Color
		// is converted to the '#112233f' notation and returned instead -- even if it's a name
		// in COLOR_MAP, above.
		String[] html = { "black", "white", "fuchsia", "aqua", "silver", "red", "lime", "blue", "yellow", "gray", "purple", "maroon", "navy", "olive", "green", "teal" };
		for (int i = 0; i < html.length; i++) {
			Color c = COLOR_MAP.get(html[i]);
			assert c != null : "HTML color not in predefined list?";
			COLOR_MAP_HTML.put(html[i], c);
		}
	}

	public static int getRandomNumber(int max) {
		return getRandomNumber(0, max);
	}

	public static int getRandomNumber(int min, int max) {
		return (int) (((max - min) * random.nextDouble()) + min);
	}

	public static float getRandomRealNumber(float max) {
		return getRandomRealNumber(0, max);
	}

	public static float getRandomRealNumber(float min, float max) {
		return (float) ((max - min) * random.nextDouble()) + min;
	}

	public static boolean percentageCheckAbove(int percentage) {
		return (random.nextDouble() * 100) > percentage;
	}

	public static boolean percentageCheckBelow(int percentage) {
		double roll = random.nextDouble() * 100;
		return roll < percentage;
	}

	private static final Pattern NAME_PATTERN = Pattern.compile("^(.*)\\s+(\\d+)\\s*$");

	/**
	 * Determine what the name of the new token should be. This method tries to choose a token name which is (a) unique
	 * and (b) adheres to a numeric sequence.
	 * 
	 * @param zone
	 *            the map that the token is being placed onto
	 * @param token
	 *            the new token to be named
	 * @return the new token's algorithmically generated name
	 */
	public static String nextTokenId(Zone zone, Token token) {
		boolean isToken = token.isToken();
		String baseName = token.getName();
		String newName;
		Integer newNum = null;

		if (isToken && AppPreferences.getNewTokenNaming().equals(Token.NAME_USE_CREATURE)) {
			newName = "Creature";
		} else if (baseName == null) {
			int nextId = nextTokenId.getAndIncrement();
			char ch = (char) ('a' + MapTool.getPlayerList().indexOf(MapTool.getPlayer()));
			return ch + Integer.toString(nextId);
		} else {
			baseName = baseName.trim();
			Matcher m = NAME_PATTERN.matcher(baseName);
			if (m.find()) {
				newName = m.group(1);
				try {
					newNum = Integer.parseInt(m.group(2));
				} catch (NumberFormatException nfe) {
					// This exception happens if the number is too big to fit inside an integer.
					// In this case, we use the original name as the filename and assign a new number as the suffix.
					newName = baseName;
				}
			} else {
				newName = baseName;
			}
		}
		boolean random = (isToken && AppPreferences.getDuplicateTokenNumber().equals(Token.NUM_RANDOM));
		boolean addNumToGM = !AppPreferences.getTokenNumberDisplay().equals(Token.NUM_ON_NAME);
		boolean addNumToName = !AppPreferences.getTokenNumberDisplay().equals(Token.NUM_ON_GM);

		/*
		 * If the token already has a number suffix, if the preferences indicate that token numbering should be random
		 * and this token is on the Token layer, or if the token already exists somewhere on this map, then we need to
		 * choose a new name.
		 */
		if (newNum != null || random || zone.getTokenByName(newName) != null) {
			// Figure out the proper number of digits and generate a random number.
			int maxNum = 99;
			if (random) {
				if (zone.getTokenCount() >= 89)
					maxNum = 999;
				if (zone.getTokenCount() >= 900)
					maxNum = 9999;
				newNum = getRandomNumber(10, maxNum);
				/*
				 * If we're generating a random number suffix, check to see if the value we have is already taken and
				 * pick a new one if so. The "Token Name" field is separate from the "GM Name" field.
				 */
				while (true) {
					boolean repeat = false;
					if (addNumToName)
						repeat = repeat || (zone.getTokenByName(newName + " " + newNum) != null);
					if (addNumToGM)
						repeat = repeat || (zone.getTokenByGMName(Integer.toString(newNum)) != null);
					if (!repeat)
						break;
					newNum = getRandomNumber(10, maxNum);
				}
			} else {
				newNum = zone.findFreeNumber(addNumToName ? newName : null, addNumToGM);
			}
			if (addNumToName) {
				newName += " ";
				newName += newNum;
			}
			if (addNumToGM)
				token.setGMName(Integer.toString(newNum));
		}
		return newName;
	}

	public static boolean isDebugEnabled() {
		return System.getProperty("MAPTOOL_DEV") != null;
	}

	public static boolean isValidColor(String name) {
		return COLOR_MAP.containsKey(name);
	}

	public static boolean isHtmlColor(String name) {
		return COLOR_MAP_HTML.containsKey(name);
	}

	/**
	 * Returns a {@link Color} object if the parameter can be evaluated as a color. This includes a text search against
	 * a list of known colors (case-insensitive; see {@link #COLOR_MAP}) and conversion of the string into a color using
	 * {@link Color#decode(String)}. Invalid strings cause <code>COLOR_MAP.get("black")</code> to be returned. Calls
	 * {@link #convertStringToColor(String)} if the parameter is not a recognized color name.
	 * 
	 * @param name
	 *            a recognized color name or an integer color value in octal or hexadecimal form (such as
	 *            <code>#123</code>, <code>0x112233</code>, or <code>0X111222333</code>)
	 * @return the corresponding Color object or {@link Color#BLACK} if not in a recognized format
	 */
	public static Color getColor(String name) {
		name = name.trim().toLowerCase();
		Color c = COLOR_MAP.get(name);
		if (c != null)
			return c;
		c = convertStringToColor(name);
		return c;
	}

	/**
	 * Converts the incoming string value to a Color object and stores <code>val</code> and the Color as a key/value
	 * pair in a cache. The incoming string may start with a <code>#</code> to indicate a numeric color value in CSS
	 * format. Any errors cause {@link #COLOR_MAP}<code>.get("black")</code> to be returned.
	 * 
	 * @param val
	 *            color value to interpret
	 * @return Color object
	 */
	private static Color convertStringToColor(String val) {
		Color c;
		if (StringUtil.isEmpty(val)) {
			c = COLOR_MAP.get("black");
		} else {
			try {
				c = Color.decode(val);
				COLOR_MAP.put(val.toLowerCase(), c);
			} catch (NumberFormatException nfe) {
				c = COLOR_MAP.get("black");
			}
		}
		return c;
	}

	public static Set<String> getColorNames() {
		return COLOR_MAP.keySet();
	}

	public static void uploadTexture(DrawablePaint paint) {
		if (paint == null) {
			return;
		}
		if (paint instanceof DrawableTexturePaint) {
			Asset asset = ((DrawableTexturePaint) paint).getAsset();
			uploadAsset(asset);
		}
	}

	public static void uploadAsset(Asset asset) {
		if (asset == null) {
			return;
		}
		if (!AssetManager.hasAsset(asset.getId())) {
			AssetManager.putAsset(asset);
		}
		if (!MapTool.isHostingServer() && !MapTool.getCampaign().containsAsset(asset.getId())) {
			MapTool.serverCommand().putAsset(asset);
		}
	}
}
