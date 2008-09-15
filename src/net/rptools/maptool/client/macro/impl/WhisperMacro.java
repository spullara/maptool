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
package net.rptools.maptool.client.macro.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.macro.MacroContext;
import net.rptools.maptool.client.macro.MacroDefinition;
import net.rptools.maptool.model.ObservableList;
import net.rptools.maptool.model.Player;
import net.rptools.maptool.model.TextMessage;
import net.rptools.maptool.util.StringUtil;

@MacroDefinition(
	name = "whisper",
	aliases = { "w" },
	description = "Send a message to a specific player."
)
public class WhisperMacro extends AbstractMacro {

    public void execute(MacroContext context, String macro) {
        
        String playerName = StringUtil.getFirstWord(macro);
           
        if (playerName == null) {
            MapTool.addMessage(TextMessage.me(context.getTransformationHistory(), "<b>Must supply a player name.</b>"));
            return;
        }
        int indexSpace = (macro.startsWith("\"")) ? macro.indexOf(" ", playerName.length()+2):  macro.indexOf(" ");
        
        String message = processText(macro.substring(indexSpace+1));
        ObservableList<Player> playerList = MapTool.getPlayerList();
        List<String> players = new ArrayList<String>();
        for(int count = 0; count < playerList.size(); count++) 
        {
        	Player p = playerList.get(count);
        	String thePlayer = p.getName();
        	players.add(thePlayer);
        }
        String playerNameMatch = StringUtil.findMatch(playerName, players); 
        playerName = (!playerNameMatch.equals(""))? playerNameMatch: playerName;
        
        // Validate
        if (!MapTool.isPlayerConnected(playerName)) {
            MapTool.addMessage(TextMessage.me(context.getTransformationHistory(), "'" + playerName + "' is not connected."));
            return;
        }
        if (MapTool.getPlayer().getName().equalsIgnoreCase(playerName)) {
            MapTool.addMessage(TextMessage.me(context.getTransformationHistory(), "Talking to yourself again?"));
            return;
        }
        
        // Send
        MapTool.addMessage(TextMessage.whisper(context.getTransformationHistory(), playerName, "<span class='whisper' style='color:blue'>" + MapTool.getFrame().getCommandPanel().getIdentity()+" whispers: "+message+"</span>"));
        MapTool.addMessage(TextMessage.me(context.getTransformationHistory(), "<span class='whisper' style='color:blue'>You whisper to " + playerName + ": "+message));
    }
}
