/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *  
 *	http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package net.rptools.maptool.client.functions;

import java.util.Iterator;
import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.ObservableList;
import net.rptools.maptool.model.Player;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;
import net.sf.json.JSONArray;

public class PlayerFunctions extends AbstractFunction {
	private static final PlayerFunctions instance = new PlayerFunctions();

	private PlayerFunctions() {
		super(0, 1, "getPlayerName", "getAllPlayerNames");
	}

	public static PlayerFunctions getInstance() {
		return instance;
	}

	@Override
	public Object childEvaluate(Parser parser, String functionName,
			List<Object> parameters) throws ParserException {
		if (functionName.equals("getPlayerName")) {
			return MapTool.getPlayer().getName();
		} else {
			ObservableList<Player> players = MapTool.getPlayerList();
			String[] playerArray = new String[players.size()];
			Iterator<Player> iter = players.iterator();

			int i = 0;
			while (iter.hasNext()) {
				playerArray[i] = iter.next().getName();
				i++;
			}
			String delim = parameters.size() > 0 ? parameters.get(0).toString() : ",";
			if ("json".equals(delim)) {
				return JSONArray.fromObject(playerArray).toString();
			} else {
				return StringFunctions.getInstance().join(playerArray, delim);
			}
		}
	}
}
