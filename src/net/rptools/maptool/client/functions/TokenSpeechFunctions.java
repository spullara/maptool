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

import net.rptools.maptool.client.MapToolVariableResolver;
import net.rptools.maptool.language.I18N;
import net.rptools.maptool.model.Token;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;
import net.sf.json.JSONArray;

public class TokenSpeechFunctions extends AbstractFunction {


	private static final TokenSpeechFunctions instance = new TokenSpeechFunctions();

	private TokenSpeechFunctions() {
		super(0, 2, "getSpeech", "setSpeech", "getSpeechNames");
	}


	public static TokenSpeechFunctions getInstance() {
		return instance;
	}


	@Override
	public Object childEvaluate(Parser parser, String functionName,
			List<Object> parameters) throws ParserException {
		final Token token = ((MapToolVariableResolver)parser.getVariableResolver()).getTokenInContext();
		if (token == null) {
			throw new ParserException(I18N.getText("macro.function.general.noImpersonated", functionName));
		}

		if (functionName.equals("getSpeech")) {
			if (parameters.size() < 1) {
				throw new ParserException(I18N.getText("macro.function.general.notEnoughParam", functionName, 1, parameters.size()));
			}
			String speech = token.getSpeech(parameters.get(0).toString());
			return speech == null ? "" : speech;
		}

		if (functionName.equals("setSpeech")) {
			if (parameters.size() < 2) {
				throw new ParserException(I18N.getText("macro.function.general.notEnoughParam", functionName, 2, parameters.size()));
			}
			token.setSpeech(parameters.get(0).toString(), parameters.get(1).toString());
			return "";
		}


		if (functionName.equals("getSpeechNames")) {
			String[] speech = new String[token.getSpeechNames().size()];
			String delim = parameters.size() > 0 ? parameters.get(0).toString() : ",";
			if ("json".equals(delim)) {
				return JSONArray.fromObject(token.getSpeechNames()).toString();
			} else {
				return StringFunctions.getInstance().join(token.getSpeechNames().toArray(speech), delim);
			}
		}
		return null;
	}

}
