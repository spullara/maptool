/* The MIT License
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
import net.rptools.maptool.client.macro.MacroManager;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.TextMessage;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;

@MacroDefinition(
        name = "impersonate",
        aliases = { "im" },
        description = "Speak as if you were something/one else"
    )
public class ImpersonateMacro implements Macro {
	
	public void execute(String macro) {

		macro = macro.trim();

		// Stop impersonating
		if (macro == null || macro.length() == 0) {
        	MapTool.getFrame().getCommandPanel().setIdentity(null);
        	return;
		}

		// Figure out what we want to impersonate
		String oldIdentity = MapTool.getFrame().getCommandPanel().getIdentity();

		String name = macro;
		int index = macro.indexOf(":");
		if (index > 0) {
			name = macro.substring(0, index).trim();
			macro = macro.substring(index+1);
		}
		
		Token token = MapTool.getFrame().getCurrentZoneRenderer().getZone().getTokenByName(name);
		if (token == null) {
			token = MapTool.getFrame().getCurrentZoneRenderer().getZone().getTokenByGMName(name);
		}
		if (token == null) {
			try {
				token = MapTool.getFrame().getCurrentZoneRenderer().getZone().getToken(GUID.valueOf(name));
			} catch (Exception e) {
				// indication of not a GUID, OK to ignore
			}
		}
		if (token != null) {
			name = token.getName();
		}

		// Permission
		if (!canImpersonate(token)) {
			MapTool.addLocalMessage("You can only impersonate tokens that you own");
			return;
		}
		
		// Impersonate
		if ( index > 0 ) {
			MapTool.getFrame().getCommandPanel().setIdentity(name);
			MacroManager.executeMacro(macro);
			MapTool.getFrame().getCommandPanel().setIdentity(oldIdentity);
		} else {
			MapTool.getFrame().getCommandPanel().setIdentity(name);
        }
	}

	private boolean canImpersonate(Token token) {

		if (MapTool.getPlayer().isGM()) {
			return true;
		}
		
		if (token == null) {
			return false;
		}
		
		return token.isOwner(MapTool.getPlayer().getName());
	}
}
