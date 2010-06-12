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
package net.rptools.maptool.client;

import java.awt.Color;
import java.security.SecureRandom;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.drawing.DrawablePaint;
import net.rptools.maptool.model.drawing.DrawableTexturePaint;

public class MapToolUtil {

//    private static Random random = new Random ( System.currentTimeMillis() );
	private static Random random = new SecureRandom();

	private static AtomicInteger nextTokenId = new AtomicInteger(1);

	/**
	 * The map of color names to color values
	 */
	private static final Map<String, Color> COLOR_MAP = new LinkedHashMap<String, Color>();

	/**
	 * Set up the color map
	 */
	static {
		COLOR_MAP.put("white", Color.WHITE);
		COLOR_MAP.put("lightgray", Color.LIGHT_GRAY);
		COLOR_MAP.put("gray", Color.GRAY);
		COLOR_MAP.put("darkgray", Color.DARK_GRAY);
		COLOR_MAP.put("black", Color.BLACK);
		COLOR_MAP.put("blue", Color.BLUE);
		COLOR_MAP.put("cyan", Color.CYAN);
		COLOR_MAP.put("green", Color.GREEN);
		COLOR_MAP.put("magenta", Color.MAGENTA);
		COLOR_MAP.put("orange", Color.ORANGE);
		COLOR_MAP.put("pink", Color.PINK);
		COLOR_MAP.put("red", Color.RED);
		COLOR_MAP.put("yellow", Color.YELLOW);
	}

	public static int getRandomNumber ( int max )
	{
		return getRandomNumber ( 0, max );
	}

	public static int getRandomNumber ( int min, int max )
	{
		return (int)(( ( max - min ) * random.nextDouble() ) + min);
	}

	public static float getRandomRealNumber ( float max )
	{
		return getRandomRealNumber ( 0, max );
	}

	public static float getRandomRealNumber ( float min, float max )
	{
		return (float)( ( max - min ) * random.nextDouble() ) + min;
	}

	public static boolean percentageCheckAbove ( int percentage )
	{
		return (random.nextDouble()* 100) > percentage;
	}

	public static boolean percentageCheckBelow ( int percentage )
	{
		double roll = random.nextDouble()* 100;
		return roll < percentage;
	}

	private static final Pattern NAME_PATTERN = Pattern.compile("(.*) (\\d+)");
	public static String nextTokenId(Zone zone, Token token) {

		boolean isToken = token.isToken();
		String baseName = token.getName();
		String newName;

		Integer newNum = null;
		if(isToken && AppPreferences.getNewTokenNaming().equals(Token.NAME_USE_CREATURE)) {
			newName = "Creature";
		} else if (baseName == null) {

			int nextId = nextTokenId.getAndIncrement();
			char ch = (char)('a' + MapTool.getPlayerList().indexOf(MapTool.getPlayer()));
			return ch + Integer.toString(nextId);
		} else {

			baseName = baseName.trim();
			Matcher m = NAME_PATTERN.matcher(baseName);

			if (m.find()) {
				newName = m.group(1);
				newNum = Integer.parseInt(m.group(2));
			} else {
				newName = baseName;
			}
		}

		boolean random = (isToken && AppPreferences.getDuplicateTokenNumber().equals(Token.NUM_RANDOM));
		boolean addNumToGM = !AppPreferences.getTokenNumberDisplay().equals(Token.NUM_ON_NAME);
		boolean addNumToName = !AppPreferences.getTokenNumberDisplay().equals(Token.NUM_ON_GM);

		if (newNum != null || random || zone.getTokenByName(newName) != null) {
			int maxNum = 99;
			if (random && isToken) {
				if (zone.getTokenCount() >= 89) {
					maxNum = 999;
				}
				if (zone.getTokenCount() >= 900) {
					maxNum = 9999;
				}

				newNum = getRandomNumber(10,maxNum);
			}

			if ( random ) {

				while ( true ) {
					boolean repeat = false;
					if ( addNumToName ) {
						repeat = repeat || (zone.getTokenByName(newName + " " + newNum) != null);
					}

					if ( addNumToGM ) {
						repeat = repeat || (zone.getTokenByGMName(Integer.toString(newNum)) != null);
					}

					if ( !repeat ) {
						break;
					}

					newNum = getRandomNumber(10,maxNum);
				}
			} else {
				newNum = zone.findFreeNumber(addNumToName ? newName : null, addNumToGM);
			}

			if ( addNumToName ) {
				newName += " ";
				newName += newNum;
			}

			if ( addNumToGM ) {
				token.setGMName(Integer.toString(newNum));
			}
		}

		return newName;

	}


	public static boolean isDebugEnabled() {
		return System.getProperty("MAPTOOL_DEV") != null;
	}

	public static boolean isValidColor(String name) {
		return COLOR_MAP.containsKey(name);
	}

	public static Color getColor(String name) {
		return COLOR_MAP.get(name);
	}

	public static Set<String> getColorNames() {
		return COLOR_MAP.keySet();
	}

	public static void uploadTexture(DrawablePaint paint) {

		if (paint == null) {
			return;
		}

		if (paint instanceof DrawableTexturePaint) {
			Asset asset = ((DrawableTexturePaint)paint).getAsset();
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
