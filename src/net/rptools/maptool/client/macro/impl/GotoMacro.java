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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ZonePoint;
import net.rptools.maptool.client.macro.Macro;
import net.rptools.maptool.client.macro.MacroDefinition;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;

@MacroDefinition(
	name = "goto",
	aliases = { "g" },
	description = "Goto location or token. /goto X,Y or /goto <tokenname>"
)
public class GotoMacro implements Macro {
	private static Pattern COORD_PAT = Pattern.compile("(\\d+)\\s*,\\s*(\\d+)");

    public void execute(String parameter) {
    	Matcher m = COORD_PAT.matcher(parameter.trim());

    	if (m.matches()) {
    		// goto coordinate locations
    		int x = Integer.parseInt(m.group(1));
    		int y = Integer.parseInt(m.group(2));

    		MapTool.getFrame().getCurrentZoneRenderer().centerOn(new ZonePoint(x, y));
    	} else {
    		// goto token location
    		Zone zone = MapTool.getFrame().getCurrentZoneRenderer().getZone();
    		Token token = zone.getTokenByName(parameter);
    		
            if (!MapTool.getPlayer().isGM() && !zone.isTokenVisible(token)) {
                return;
            }
            
    		if (token != null) {
    			int x = token.getX();
    			int y = token.getY();

        		MapTool.getFrame().getCurrentZoneRenderer().centerOn(new ZonePoint(x, y));
    		}
    	}
    }
}
