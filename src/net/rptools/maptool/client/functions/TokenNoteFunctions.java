/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *  
 *	http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
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

public class TokenNoteFunctions extends AbstractFunction {

	private static final TokenNoteFunctions instance = new TokenNoteFunctions();

	private TokenNoteFunctions() {
		super(0, 1, "getNotes", "getGMNotes", "setNotes", "setGMNotes");
	}

	public static TokenNoteFunctions getInstance() {
		return instance;
	}

	@Override
	public Object childEvaluate(Parser parser, String functionName, List<Object> parameters) throws ParserException {
		final Token tokenInContext = ((MapToolVariableResolver) parser.getVariableResolver()).getTokenInContext();
		if (tokenInContext == null) {
			throw new ParserException(I18N.getText("macro.function.general.noImpersonated", functionName));
		}
		if (functionName.equals("getNotes")) {
			String notes = tokenInContext.getNotes();
			return notes == null ? "" : notes;
		}
		if (functionName.equals("setNotes")) {
			if (parameters.size() < 1) {
				throw new ParserException(I18N.getText("macro.function.general.notEnoughParam", functionName, 1, parameters.size()));
			}
			tokenInContext.setNotes(parameters.get(0).toString());
			MapTool.serverCommand().putToken(MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(), tokenInContext);
			return "";
		}
		if (functionName.equals("getGMNotes")) {
			if (!MapTool.getParser().isMacroTrusted()) {
				throw new ParserException(I18N.getText("macro.function.general.noPerm", functionName));
			}
			String notes = tokenInContext.getGMNotes();
			return notes == null ? "" : notes;
		}
		if (functionName.equals("setGMNotes")) {
			if (!MapTool.getParser().isMacroTrusted()) {
				throw new ParserException(I18N.getText("macro.function.general.noPerm", functionName));
			}
			if (parameters.size() < 1) {
				throw new ParserException(I18N.getText("macro.function.general.notEnoughParam", functionName, 1, parameters.size()));
			}
			tokenInContext.setGMNotes(parameters.get(0).toString());
			MapTool.serverCommand().putToken(MapTool.getFrame().getCurrentZoneRenderer().getZone().getId(), tokenInContext);
			return "";
		}
		return null;
	}
}
