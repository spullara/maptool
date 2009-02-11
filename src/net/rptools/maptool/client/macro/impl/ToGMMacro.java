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

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolMacroContext;
import net.rptools.maptool.client.macro.MacroContext;
import net.rptools.maptool.client.macro.MacroDefinition;
import net.rptools.maptool.model.TextMessage;

@MacroDefinition(
        name = "gm",
        aliases = { "togm" },
        description = "Send to GMs exclusively"
    )
public class ToGMMacro extends AbstractRollMacro {

    public void execute(MacroContext context, String macro, MapToolMacroContext executionContext) {

    	StringBuilder sb = new StringBuilder();

    	if (executionContext != null && executionContext.isTrusted() && !MapTool.getPlayer().isGM()) {
        	sb.append("<span style='background-color: #C9F7AD' ").append("title='").append(executionContext.getName());
        	sb.append("@").append(executionContext.getSouce()).append("'>");
        	sb.append(MapTool.getPlayer().getName()).append("</span>").append(" says to the GM: ");
        	sb.append(macro);
    	} else {
        	sb.append(MapTool.getPlayer().getName()).append(" says to the GM: ").append(macro);
    	}
        MapTool.addMessage(new TextMessage(TextMessage.Channel.GM, null, MapTool.getPlayer().getName(),  sb.toString(), context.getTransformationHistory()));
		MapTool.addMessage(new TextMessage(TextMessage.Channel.ME, null, MapTool.getPlayer().getName(), "You say to the GM: " + macro, context.getTransformationHistory()));
    }
}
