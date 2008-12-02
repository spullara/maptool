package net.rptools.maptool.client.functions;

import java.util.Iterator;
import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.ObservableList;
import net.rptools.maptool.model.Player;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;

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
			while(iter.hasNext()) {
				playerArray[i] = iter.next().getName();
				i++;
			}
			
			String delim = parameters.size() > 0 ? parameters.get(0).toString() : ",";
			
			return StringFunctions.getInstance().join(playerArray, delim);
		}
	}


}
