/* The MIT License
 * 
 * Copyright (c) 2008 Jay Gorrell
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
package net.rptools.maptool.client.functions;

import java.math.BigDecimal;

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
        return TokenInitFunction.getTokenInitiative(token).isHolding() ? BigDecimal.ONE : BigDecimal.ZERO; 
    }

    /**
     * @see net.rptools.maptool.client.functions.AbstractTokenAccessorFunction#setValue(net.rptools.maptool.model.Token, java.lang.Object)
     */
    @Override
    protected Object setValue(Token token, Object value) throws ParserException {
        boolean set = getBooleanValue(value);
        for (TokenInitiative ti : TokenInitFunction.getTokenInitiatives(token)) ti.setHolding(set);
        return set ? BigDecimal.ONE : BigDecimal.ZERO;
    }
}
