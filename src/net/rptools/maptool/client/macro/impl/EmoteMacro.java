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
import net.rptools.maptool.client.macro.MacroContext;
import net.rptools.maptool.client.macro.MacroDefinition;
import net.rptools.maptool.model.TextMessage;

@MacroDefinition(
	name = "emote",
	aliases = { "me" },
	description = "Broadcast an emote to all connected players."
)
public class EmoteMacro extends AbstractMacro {

    public void execute(MacroContext context, String macro, boolean trusted, String macroName) {
    	macro = processText(macro);
        StringBuilder sb = new StringBuilder();
        
    	// Prevent spoofing
    	sb.append("* ");

        sb.append("<span style='color:green'>");
        sb.append(MapTool.getFrame().getCommandPanel().getIdentity());
        sb.append(" ");
        
        sb.append(macro);
        sb.append("</span>");
        MapTool.addMessage(TextMessage.say(context.getTransformationHistory(), sb.toString()));
    }
}
