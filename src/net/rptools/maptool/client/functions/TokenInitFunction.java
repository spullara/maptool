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

import java.util.ArrayList;
import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.InitiativeList.TokenInitiative;
import net.rptools.parser.ParserException;

/**
 * Set the token initiative
 * 
 * @author Jay
 */
public class TokenInitFunction extends AbstractTokenAccessorFunction {

    /** Getter has 0 or 1, setter has 1 or 2 */
    private TokenInitFunction() {
        super(0, 2, "setInitiative", "getInitiative");
    }
    
    /** singleton instance of this function */
    private static final TokenInitFunction singletonInstance = new TokenInitFunction();
    
    /** @return singleton instance */
    public static TokenInitFunction getInstance() { return singletonInstance; };
    
    /**
     * @see net.rptools.maptool.client.functions.AbstractTokenAccessorFunction#getValue(net.rptools.maptool.model.Token)
     */
    @Override
    protected Object getValue(Token token) throws ParserException {
        String ret = getTokenInitiative(token).getState();
        if (ret == null) ret = "";
        return ret;
    }

    /**
     * @see net.rptools.maptool.client.functions.AbstractTokenAccessorFunction#setValue(net.rptools.maptool.model.Token, java.lang.Object)
     */
    @Override
    protected Object setValue(Token token, Object value) throws ParserException {
        String sValue = null;
        if (value != null) sValue = value.toString();
        for (TokenInitiative ti : getTokenInitiatives(token)) ti.setState(sValue);
        return value;
    }
    
    /**
     * Get the first token initiative
     * 
     * @param token Get it for this token
     * @return The first token initiative value for the passed token
     * @throws ParserException Token isn't in initiative.
     */
    public static TokenInitiative getTokenInitiative(Token token) throws ParserException {
        Zone zone = MapTool.getFrame().getCurrentZoneRenderer().getZone();
        List<Integer> list = zone.getInitiativeList().indexOf(token);
        if (list.isEmpty()) 
            throw new ParserException("The token is not in the initiative list so no value can be set");
        return zone.getInitiativeList().getTokenInitiative(list.get(0).intValue()); 
    }
    
    /**
     * Get the first token initiative
     * 
     * @param token Get it for this token
     * @return The first token initiative value for the passed token
     * @throws ParserException Token isn't in initiative.
     */
    public static List<TokenInitiative> getTokenInitiatives(Token token) throws ParserException {
        Zone zone = MapTool.getFrame().getCurrentZoneRenderer().getZone();
        List<Integer> list = zone.getInitiativeList().indexOf(token);
        if (list.isEmpty()) 
            throw new ParserException("The token is not in the initiative list so no value can be set");
        List<TokenInitiative> ret = new ArrayList<TokenInitiative>(list.size());
        for (Integer index : list)
            ret.add(zone.getInitiativeList().getTokenInitiative(index.intValue()));
        return ret;
    }
}
