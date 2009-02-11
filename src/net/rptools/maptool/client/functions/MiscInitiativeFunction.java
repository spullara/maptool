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
package net.rptools.maptool.client.functions;

import java.math.BigDecimal;
import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.tokenpanel.InitiativePanel;
import net.rptools.maptool.model.InitiativeList;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;

/**
 * Advance the initiative
 * 
 * @author Jay
 */
public class MiscInitiativeFunction extends AbstractFunction {

    /** Handle adding one, all, all PCs or all NPC tokens. */
	private MiscInitiativeFunction() {
		super(0, 0, "nextInitiative", "sortInitiative", "initiativeSize");
	}
	
    /** singleton instance of this function */
    private final static MiscInitiativeFunction instance = new MiscInitiativeFunction();

    /** @return singleton instance */
	public static MiscInitiativeFunction getInstance() { return instance; }	
	
	/**
	 * @see net.rptools.parser.function.AbstractFunction#childEvaluate(net.rptools.parser.Parser, java.lang.String, java.util.List)
	 */
	@Override
	public Object childEvaluate(Parser parser, String functionName, List<Object> args) throws ParserException {
        InitiativeList list = MapTool.getFrame().getCurrentZoneRenderer().getZone().getInitiativeList();
        InitiativePanel ip = MapTool.getFrame().getInitiativePanel();
        if (functionName.equals("nextInitiative")) {
        	if (!MapTool.getParser().isMacroTrusted()) {
        		if (!ip.hasGMPermission() && (list.getCurrent() <= 0 || !ip.hasOwnerPermission(list.getTokenInitiative(list.getCurrent()).getToken()))) {
        			String message = "Only the GM can perform nextInitiative.";
        			if (ip.isOwnerPermissions()) message = "Only the GM or owner can perform nextInitiative.";
        			throw new ParserException(message);
        		} // endif
        	}
            list.nextInitiative();
            return new BigDecimal(list.getCurrent());
        } 
        if (!MapTool.getParser().isMacroTrusted() && !ip.hasGMPermission())
            throw new ParserException("Only the GM can call the " + functionName + " function.");
        if (functionName.equals("sortInitiative")) {
            list.sort();
            return new BigDecimal(list.getSize());
        } else if (functionName.equals("initiativeSize")) {
            return new BigDecimal(list.getSize());
        } // endif
        throw new ParserException("Unexpected function: " + functionName);
	}
}