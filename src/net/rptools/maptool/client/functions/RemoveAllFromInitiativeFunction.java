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
import net.rptools.maptool.language.I18N;
import net.rptools.maptool.model.InitiativeList;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Token.Type;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;

/**
 * Remove a set of tokens to initiative
 * 
 * @author Jay
 */
public class RemoveAllFromInitiativeFunction extends AbstractFunction {

    /** Handle removing, all, all PCs or all NPC tokens. */
	private RemoveAllFromInitiativeFunction() {
		super(0, 0, "removeAllFromInitiative", "removeAllPCsFromInitiative", "removeAllNPCsFromInitiative");
	}
	
    /** singleton instance of this function */
    private final static RemoveAllFromInitiativeFunction instance = new RemoveAllFromInitiativeFunction();

    /** @return singleton instance */
	public static RemoveAllFromInitiativeFunction getInstance() { return instance; }	
	
	/**
	 * @see net.rptools.parser.function.AbstractFunction#childEvaluate(net.rptools.parser.Parser, java.lang.String, java.util.List)
	 */
	@Override
	public Object childEvaluate(Parser parser, String functionName, List<Object> args) throws ParserException {
        InitiativeList list = MapTool.getFrame().getCurrentZoneRenderer().getZone().getInitiativeList();
        int count = 0;
        
        if (!MapTool.getParser().isMacroTrusted()) {
        	throw new ParserException(I18N.getText("macro.function.general.noPerm", functionName));
        }
        if (functionName.equals("removeAllFromInitiative")) {
            count = list.getSize();
            list.clearModel();
        } else {
            list.startUnitOfWork();
            boolean pcs = functionName.equals("removeAllPCsFromInitiative");
            for (int i = list.getSize() - 1; i >= 0; i--) {
                Token token = list.getTokenInitiative(i).getToken();
                if (token.getType() == Type.PC && pcs || token.getType() == Type.NPC && !pcs) {
                    list.removeToken(i);
                    count++;
                } // endif
            } // endfor
            list.setRound(-1);
            list.setCurrent(-1);
            list.finishUnitOfWork();
        } // endif
        return new BigDecimal(count);
	}
}
