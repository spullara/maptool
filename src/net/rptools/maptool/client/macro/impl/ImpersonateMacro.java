/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package net.rptools.maptool.client.macro.impl;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolMacroContext;
import net.rptools.maptool.client.macro.Macro;
import net.rptools.maptool.client.macro.MacroContext;
import net.rptools.maptool.client.macro.MacroDefinition;
import net.rptools.maptool.client.macro.MacroManager;
import net.rptools.maptool.client.ui.MapToolFrame;
import net.rptools.maptool.client.ui.commandpanel.CommandPanel;
import net.rptools.maptool.language.I18N;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Token;

@MacroDefinition(
		name = "impersonate",
		aliases = { "im" },
		description = "impersonate.description",
		expandRolls = false)
public class ImpersonateMacro implements Macro {
	public void execute(MacroContext context, String macro, MapToolMacroContext executionContext) {
		final MapToolFrame frame = MapTool.getFrame(); // cached for quicker access
		final CommandPanel cpanel = frame.getCommandPanel();
		macro = macro.trim();

		// Stop impersonating
		if (macro == null || macro.length() == 0) {
			cpanel.setIdentityGUID(null);
			frame.getImpersonatePanel().stopImpersonating();
			return;
		}
		// Figure out what we want to impersonate
		GUID oldGuid = cpanel.getIdentityGUID();
		String oldIdentity = cpanel.getIdentity();

		String name = macro;
		int index = macro.indexOf(":");
		if (index > 0) {
			if (macro.substring(0, index).equalsIgnoreCase("lib")) {
				index = macro.indexOf(":", index + 1);
			}
		}
		if (index > 0) {
			name = macro.substring(0, index).trim();
			macro = macro.substring(index + 1);
		}
		Token token = frame.getCurrentZoneRenderer().getZone().resolveToken(name);
		if (token != null) {
			name = token.getName();
		}
		// Permission
		if (!canImpersonate(token)) {
			MapTool.addLocalMessage(I18N.getText("impersonate.mustOwn", token.getName()));
			return;
		}
		// Impersonate
		if (index > 0) {
			if (token != null)
				cpanel.setIdentityGUID(token.getId());
			else
				cpanel.setIdentityName(name);
			MacroManager.executeMacro(macro, executionContext);
			if (oldGuid != null)
				cpanel.setIdentityGUID(oldGuid);
			else
				cpanel.setIdentityName(oldIdentity);
		} else {
			cpanel.setIdentityName(name);
			if (token == null || !canLoadTokenMacros(token)) {
				// we are impersonating but it's not a token or we are not allowed to see it's macros. so clear the token macro buttons panel
				frame.getImpersonatePanel().stopImpersonating();
			} else {
				// we are impersonating another token now. we need to display its token macro buttons
				frame.getImpersonatePanel().startImpersonating(token);
			}
		}
	}

	private boolean canImpersonate(Token token) {
		//my addition
		if (!MapTool.getServerPolicy().isRestrictedImpersonation()) {
			return true;
		}
		if (MapTool.getPlayer().isGM()) {
			return true;
		}
		if (token == null) {
			return false;
		}
		return token.isOwner(MapTool.getPlayer().getName());
	}

	private boolean canLoadTokenMacros(Token token) {
		if (MapTool.getPlayer().isGM()) {
			return true;
		}
		return token.isOwner(MapTool.getPlayer().getName());
	}
}
