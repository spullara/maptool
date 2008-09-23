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
import net.rptools.maptool.client.MapToolVariableResolver;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Token;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;

public class TokenVisibleFunction extends AbstractFunction {

	/** The singleton instance. */
	private static final TokenVisibleFunction instance = new TokenVisibleFunction();
	
	
	private TokenVisibleFunction() {
		super(0, 2, "setVisible", "getVisible");
	}
	
	
	/**
	 * Gets the instance of Visible.
	 * @return the instance.
	 */
	public static TokenVisibleFunction getInstance() {
		return instance;
	}
	
	public boolean getBooleanVisible(Token token) throws ParserException {
	    if (!MapTool.getPlayer().isGM()) {
            throw new ParserException("You must be the GM to use test the visibility of tokens");
        }
	    
	    return token.isVisible();
	}
	
	/**
	 * Gets if the token is visible.
	 * @param token The token to check.
	 * @return if the token is visible.
	 * @throws ParserException if the player does not have permissions to check.
	 */
	public Object getVisible(Token token) throws ParserException {
		return getBooleanVisible(token) ? BigDecimal.valueOf(1) :  BigDecimal.valueOf(0);
	}
	
	
	/**
	 * Sets if the token is visible or not.
	 * @param token the token to set.
	 * @param val the value to set the visible flag to.
	 */
	public void setVisible(Token token, Object val) {
		boolean set;
        if (val instanceof Integer) {
            set = ((Integer) val).intValue() != 0;
        } else if (val instanceof Boolean) {
            set = ((Boolean) val).booleanValue();
        } else {
            try {
                set = Integer.parseInt(val.toString()) != 0;
            } catch (NumberFormatException e) {
                set = Boolean.parseBoolean(val.toString());
            }
        }
        token.setVisible(set);
		MapTool.serverCommand().putToken(MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(),token);
	}
	
	
	
	@Override
	public Object childEvaluate(Parser parser, String functionName, List<Object> param)
			throws ParserException {
		if (functionName.equals("getVisible")) {
			return getVisible(parser, param);
		} else {
			return setVisible(parser, param);
		}
	}
	
	/**
	 * Gets if the token is visible
	 * @param parser The parser that called the object.
	 * @param args The arguments.
	 * @return if the token is visible or not.
	 * @throws ParserException if an error occurs.
	 */
	private Object getVisible(Parser parser, List<Object> args) throws ParserException {
		Token token;
		
		if (args.size() > 0 && args.get(0) instanceof GUID) {
			token = MapTool.getFrame().getCurrentZoneRenderer().getZone().getToken((GUID)args.get(0));
		} else if (args.size() > 0) {
			throw new ParserException("Usage: getVisible() or getVisible(target)");
		} else {
			MapToolVariableResolver res = (MapToolVariableResolver)parser.getVariableResolver();
			token = res.getTokenInContext();
		}
		
		return getVisible(token);
	}
	
	/**
	 * Sets if the token is visible
	 * @param parser The parser that called the object.
	 * @param args The arguments.
	 * @return the value visible is set to.
	 * @throws ParserException if an error occurs.
	 */
	private Object setVisible(Parser parser, List<Object> args) throws ParserException {

		Object val;
        Token token;
		
		if (args.size() > 1 && args.get(0) instanceof GUID) {
			token = MapTool.getFrame().getCurrentZoneRenderer().getZone().getToken((GUID)args.get(0));
			val = args.get(1);
		} else if (args.size() > 1) {
			throw new ParserException("Usage: setVisible(vis) or setVisible(target, vis)");
		} else {
			MapToolVariableResolver res = (MapToolVariableResolver)parser.getVariableResolver();
			token = res.getTokenInContext();
			val = args.get(0);
		}
		setVisible(token, val);
		MapTool.serverCommand().putToken(MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(),
        		token);
		return val;
	}
}
