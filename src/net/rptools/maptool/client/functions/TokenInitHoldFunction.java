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

import net.rptools.maptool.language.I18N;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.InitiativeList.TokenInitiative;
import net.rptools.parser.ParserException;

/**
 * Set the token initiative hold value
 * 
 * @author Jay
 */
public class TokenInitHoldFunction extends AbstractTokenAccessorFunction {

    /** Getter has 0 or 1, setter has 1 or 2 */
    private TokenInitHoldFunction() {
        super(0, 2, "setInitiativeHold", "getInitiativeHold");
    }
    
    /** singleton instance of this function */
    private static final TokenInitHoldFunction singletonInstance = new TokenInitHoldFunction();
    
    /** @return singleton instance */
    public static TokenInitHoldFunction getInstance() { return singletonInstance; };
    
    /**
     * @see net.rptools.maptool.client.functions.AbstractTokenAccessorFunction#getValue(net.rptools.maptool.model.Token)
     */
    @Override
    protected Object getValue(Token token) throws ParserException {
        TokenInitiative ti = TokenInitFunction.getTokenInitiative(token);
        if (ti == null)
            return I18N.getText("macro.function.TokenInit.notOnList");
        return ti.isHolding() ? BigDecimal.ONE : BigDecimal.ZERO; 
    }

    /**
     * @see net.rptools.maptool.client.functions.AbstractTokenAccessorFunction#setValue(net.rptools.maptool.model.Token, java.lang.Object)
     */
    @Override
    protected Object setValue(Token token, Object value) throws ParserException {
        boolean set = getBooleanValue(value);
        List<TokenInitiative> tis = TokenInitFunction.getTokenInitiatives(token);
        if (tis.isEmpty())
            return I18N.getText("macro.function.TokenInit.notOnListSet");
        for (TokenInitiative ti : tis) ti.setHolding(set);
        return set ? BigDecimal.ONE : BigDecimal.ZERO;
    }
}
