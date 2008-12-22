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
	name = "reply",
	aliases = { "rep" },
	description = "Reply to the last player to whisper to you."
)
public class WhisperReplyMacro extends AbstractMacro {

    public void execute(MacroContext context, String message, boolean trusted, String macroName) {
        
        
        String playerName = MapTool.getLastWhisperer();
        if (playerName == null) 
        {
        	MapTool.addMessage(TextMessage.me(context.getTransformationHistory(), "<b>You have no one to which to reply.</b>"));
        }
        
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
