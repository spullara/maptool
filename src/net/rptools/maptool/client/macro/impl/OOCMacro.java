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

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.macro.Macro;
import net.rptools.maptool.client.macro.MacroDefinition;
import net.rptools.maptool.model.TextMessage;

@MacroDefinition(
	name = "ooc",
	aliases = { "ooc" },
	description = "Out Of Character chat"
)
public class OOCMacro extends AbstractMacro {

	public void execute(String macro) {
		macro = processText(macro);
		StringBuilder sb = new StringBuilder();

		// Prevent spoofing
		sb.append(MapTool.getFrame().getCommandPanel().getIdentity());
		sb.append(": ");
		
		Color color = MapTool.getFrame().getCommandPanel().getTextColorWell().getColor();
        if (color != null) {
        	sb.append("<span style='color:#").append(String.format("%06X", (color.getRGB() & 0xFFFFFF))).append("'>");
        }
		sb.append("(( ").append(macro).append(" ))");
		if (color != null) {
        	sb.append("</span>");
        }
		MapTool.addMessage(TextMessage.say(sb.toString()));
	}
}