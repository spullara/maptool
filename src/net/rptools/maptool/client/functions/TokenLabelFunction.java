/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package net.rptools.maptool.client.functions;

import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolVariableResolver;
import net.rptools.maptool.language.I18N;
import net.rptools.maptool.model.Token;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;

public class TokenLabelFunction extends AbstractFunction {
	/** The singleton instance. */
	private final static TokenLabelFunction instance = new TokenLabelFunction();

	public static TokenLabelFunction getInstance() {
		return instance;
	}

	private TokenLabelFunction() {
		super(0, 2, "getLabel", "setLabel");
	}

	/**
	 * Gets the label for the specified token.
	 * 
	 * @param token
	 *            The token to get the label for.
	 * @return the label.
	 */
	public String getLabel(Token token) {
		return token.getLabel() != null ? token.getLabel() : "";
	}

	/**
	 * Sets the label for the specified token.
	 * 
	 * @param token
	 *            The token to set the label for.
	 * @param label
	 *            the label to set.
	 */
	public void setLabel(Token token, String label) {
		token.setLabel(label);
		MapTool.serverCommand().putToken(MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(), token);
	}

	@Override
	public Object childEvaluate(Parser parser, String functionName, List<Object> args) throws ParserException {
		if (functionName.equals("getLabel")) {
			return getLabel(parser, args);
		} else {
			return setLabel(parser, args);
		}
	}

	/**
	 * Gets the label of the token
	 * 
	 * @param parser
	 *            The parser that called the Object.
	 * @param args
	 *            The arguments passed.
	 * @return the name of the token.
	 * @throws ParserException
	 *             when an error occurs.
	 */
	private Object getLabel(Parser parser, List<Object> args) throws ParserException {
		Token token;

		if (args.size() == 1) {
			if (!MapTool.getParser().isMacroTrusted()) {
				throw new ParserException(I18N.getText("macro.function.general.noPermOther", "getLabel"));
			}

			token = FindTokenFunctions.findToken(args.get(0).toString(), null);
			if (token == null) {
				throw new ParserException(I18N.getText("macro.function.general.unknownToken", "getLabel", args.get(0).toString()));
			}
		} else if (args.isEmpty()) {
			MapToolVariableResolver res = (MapToolVariableResolver) parser.getVariableResolver();
			token = res.getTokenInContext();
			if (token == null) {
				throw new ParserException(I18N.getText("macro.function.general.noImpersonated", "getLabel"));
			}
		} else {
			throw new ParserException(I18N.getText("macro.function.general.tooManyParam", "getLabel", 1, args.size()));
		}
		return getLabel(token);
	}

	/**
	 * Sets the label of the token.
	 * 
	 * @param parser
	 *            The parser that called the Object.
	 * @param args
	 *            The arguments passed.
	 * @return the new name of the token.
	 * @throws ParserException
	 *             when an error occurs.
	 */
	private Object setLabel(Parser parser, List<Object> args) throws ParserException {
		Token token;

		if (args.size() == 2) {
			if (!MapTool.getParser().isMacroTrusted()) {
				throw new ParserException(I18N.getText("macro.function.general.noPermOther", "setLabel"));
			}
			token = FindTokenFunctions.findToken(args.get(1).toString(), null);
			if (token == null) {
				throw new ParserException(I18N.getText("macro.function.general.unknownToken", "setLabel", args.get(1).toString()));
			}
		} else if (args.size() == 1) {
			MapToolVariableResolver res = (MapToolVariableResolver) parser.getVariableResolver();
			token = res.getTokenInContext();
			if (token == null) {
				throw new ParserException(I18N.getText("macro.function.general.noImpersonated", "setLabel"));
			}
		} else if (args.isEmpty()) {
			throw new ParserException(I18N.getText("macro.function.general.notEnoughParam", "setLabel", 1, args.size()));
		} else {
			throw new ParserException(I18N.getText("macro.function.general.tooManyParam", "setLabel", 2, args.size()));
		}
		setLabel(token, args.get(0).toString());
		MapTool.serverCommand().putToken(MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(), token);
		MapTool.getFrame().getCurrentZoneRenderer().getZone().putToken(token);
		return args.get(0);
	}
}
