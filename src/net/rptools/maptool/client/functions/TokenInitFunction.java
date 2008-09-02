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
            throw new ParserException("The token is not in the initiative list.");
        List<TokenInitiative> ret = new ArrayList<TokenInitiative>(list.size());
        for (Integer index : list)
            ret.add(zone.getInitiativeList().getTokenInitiative(index.intValue()));
        return ret;
    }
}
