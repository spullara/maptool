/* The MIT License
 * 
 * Copyright (c) 2005 David Rice, Trevor Croft
 * 
 * Permission is hereby granted, free of charge, to any person 
 * obtaining a copy of this software and associated documentation files 
 * (the "Software"), to deal in the Software without restriction, 
 * including without limitation the rights to use, copy, modify, merge, 
 * publish, distribute, sublicense, and/or sell copies of the Software, 
 * and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be 
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS 
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN 
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
 * SOFTWARE.
 */
package net.rptools.maptool.client.macro.impl;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.macro.Macro;
import net.rptools.maptool.client.macro.MacroDefinition;
import net.rptools.maptool.model.TextMessage;

@MacroDefinition(
	name = "whisper",
	aliases = { "w" },
	description = "Send a message to a specific player."
)
public class WhisperMacro implements Macro {

    public void execute(String macro) {
        
        int index = macro.indexOf(" ");
        if (index < 0) {
            MapTool.addMessage(TextMessage.me("<b>Must supply a player name.</b>"));
            return;
        }
        
        String playerName = macro.substring(0, index);
        String message = macro.substring(index+1);
        
        // Validate
        if (!MapTool.isPlayerConnected(playerName)) {
            MapTool.addMessage(TextMessage.me("'" + playerName + "' is not connected."));
            return;
        }
        if (MapTool.getPlayer().getName().equalsIgnoreCase(playerName)) {
            MapTool.addMessage(TextMessage.me("Talking to yourself again?"));
            return;
        }
        
        // Send
        MapTool.addMessage(TextMessage.whisper(playerName, "<span class='whisper' style='color:blue'>" + MapTool.getFrame().getCommandPanel().getIdentity()+" whispers: "+message+"</span>"));
        MapTool.addMessage(TextMessage.me("<span class='whisper' style='color:blue'>You whisper to " + playerName + ": "+message));
    }
}
