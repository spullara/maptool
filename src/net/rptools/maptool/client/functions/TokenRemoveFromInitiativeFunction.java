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
import net.rptools.maptool.model.InitiativeList;
import net.rptools.maptool.model.Token;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;

/**
 * Remove a token from initiative
 * 
 * @author Jay
 */
public class TokenRemoveFromInitiativeFunction extends AbstractFunction {

    /** Handle adding one, all, all PCs or all NPC tokens. */
	private TokenRemoveFromInitiativeFunction() {
		super(0, 1, "removeFromInitiative");
	}
	
    /** singleton instance of this function */
    private final static TokenRemoveFromInitiativeFunction instance = new TokenRemoveFromInitiativeFunction();

    /** @return singleton instance */
	public static TokenRemoveFromInitiativeFunction getInstance() { return instance; }	
	
	/**
	 * @see net.rptools.parser.function.AbstractFunction#childEvaluate(net.rptools.parser.Parser, java.lang.String, java.util.List)
	 */
	@Override
	public Object childEvaluate(Parser parser, String functionName, List<Object> args) throws ParserException {
	    InitiativeList list = MapTool.getFrame().getCurrentZoneRenderer().getZone().getInitiativeList();
	    Token token = AbstractTokenAccessorFunction.getTarget(parser, args, 1);
        if (!MapTool.getFrame().getInitiativePanel().hasOwnerPermission(token)) {
            String message = "Only the GM can call removeFromInitiative function.";
            if (MapTool.getFrame().getInitiativePanel().isOwnerPermissions()) message = "Only the GM or Owner can call removeFromInitiative.";
            throw new ParserException(message);
        } // endif
	    List<Integer> tokens = list.indexOf(token);
	    list.startUnitOfWork();
	    for (int i = tokens.size() - 1; i >= 0; i--) list.removeToken(tokens.get(i).intValue());
	    list.finishUnitOfWork();
	    return new BigDecimal(tokens.size());
	}
}
