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

import java.awt.Color;

import net.rptools.lib.MD5Key;
import net.rptools.maptool.client.AppPreferences;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.macro.MacroDefinition;
import net.rptools.maptool.model.TextMessage;
import net.rptools.maptool.model.Token;

@MacroDefinition(
	name = "say",
	aliases = { "s" },
	description = "Broadcast a message to all connected players."
)
public class SayMacro extends AbstractMacro {

    public void execute(String macro) {
    	macro = processText(macro);
    	
        StringBuilder sb = new StringBuilder();

        String identity = MapTool.getFrame().getCommandPanel().getIdentity();
        sb.append("<table cellpadding=0><tr>");
        
        if (!identity.equals(MapTool.getPlayer().getName()) && AppPreferences.getShowAvatarInChat()) {
        	Token token = MapTool.getFrame().getCurrentZoneRenderer().getZone().getTokenByName(identity);
        	if (token != null) {
        		MD5Key imageId = token.getPortraitImage();
        		if (imageId == null) {
        			imageId = token.getImageAssetId();
        		}
	        	sb.append("<td valign=top width=40 style=\"padding-right:5px\"><img src=\"asset://").append(imageId).append("-40\" ></td>");
        	}
        }
        
        sb.append("<td valign=top style=\"margin-right: 5px\">");
        sb.append(identity).append(": ");
        sb.append("</td><td valign=top>");

        Color color = MapTool.getFrame().getCommandPanel().getTextColorWell().getColor();
        if (color != null) {
        	sb.append("<span style='color:#").append(Integer.toHexString(color.getRGB()&0xffffff)).append("'>");
        }
        sb.append(macro);
        if (color != null) {
        	sb.append("</span>");
        }
        sb.append("</td>");
        
        sb.append("</tr></table>");
        
        MapTool.addMessage(TextMessage.say(sb.toString()));
    }
}
