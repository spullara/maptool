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

import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolVariableResolver;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Token;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;

public class TokenNameFunction extends AbstractFunction {
	
	/** Singleton instance. */
	private final static TokenNameFunction instance = new TokenNameFunction();

	private TokenNameFunction() {
		super(0,1, "getName", "setName");
	}

	/**
	 * Gets the instance of Name.
	 * @return the instance of name.
	 */
	public static TokenNameFunction getInstance() {
		return instance;
	}

	@Override
	public Object childEvaluate(Parser parser, String functionName, List<Object> args)
			throws ParserException {
		if (functionName.equals("getName")) {
			return getName(parser, args);
		} else {
			return setName(parser, args);
		}
	}

	/**
	 * Gets the name of the token.
	 * @param token the token to get the name of.
	 * @return the name of the token.
	 */
	public String getName(Token token) {
		return token.getName();
	}
	
	
	/**
	 * Sets the name of the token. 
	 * @param token The token to set the name of.
	 * @param name the name of the token.
	 */
	public void setName(Token token, String name) {
		token.setName(name);
	}
	
	
	/**
	 * Gets the name of the token
	 * @param parser The parser that called the Object.
	 * @param args The arguments passed.
	 * @return the name of the token.
	 * @throws ParserException when an error occurs.
	 */
	private Object getName(Parser parser, List<Object> args) throws ParserException {
		Token token;
		
		if (args.size() == 1) {
			token = FindTokenFunctions.findToken(args.get(0).toString(), null);
			if (token == null) {
				throw new ParserException("getName(): can not find token or ID " + args.get(0));
			}
		} else if (args.size() == 0) {
			MapToolVariableResolver res = (MapToolVariableResolver)parser.getVariableResolver();
			token = res.getTokenInContext();
			if (token == null) {
				throw new ParserException("getName(): No impersonated token");
			}
		} else {
			throw new ParserException("getName(): Incorrect number of parameters.");
		}

		
		return token.getName();
	}

	/**
	 * Sets the name of the token.
	 * @param parser The parser that called the Object.
	 * @param args The arguments passed.
	 * @return the new name of the token.
	 * @throws ParserException when an error occurs.
	 */
	private Object setName(Parser parser, List<Object> args) throws ParserException {
        Token token;
		
		if (args.size() == 2) {
			token = FindTokenFunctions.findToken(args.get(1).toString(), null);
			if (token == null) {
				throw new ParserException("seName(): can not find token or ID " + args.get(1));
			}
		} else if (args.size() == 1) {
			MapToolVariableResolver res = (MapToolVariableResolver)parser.getVariableResolver();
			token = res.getTokenInContext();
			if (token == null) {
				throw new ParserException("getVisible(): No impersonated token");
			}
		} else {
			throw new ParserException("getVisible(): Incorrect number of parameters.");
		}
		
		token.setName(args.get(0).toString());
		ZoneRenderer renderer = MapTool.getFrame().getCurrentZoneRenderer(); 
		MapTool.serverCommand().putToken(MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(),
        		token);
		return args.get(0);
	}

}
