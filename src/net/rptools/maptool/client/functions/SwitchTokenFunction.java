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
import net.rptools.maptool.model.Zone;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;

public class SwitchTokenFunction extends AbstractFunction {
	private static final SwitchTokenFunction instance = new SwitchTokenFunction();

	private SwitchTokenFunction() {
		super(1, 1, "switchToken");
	}

	public static SwitchTokenFunction getInstance() {
		return instance;
	}

	@Override
	public Object childEvaluate(Parser parser, String functionName, List<Object> parameters) throws ParserException {
		if (!MapTool.getParser().isMacroTrusted()) {
			throw new ParserException(I18N.getText("macro.function.general.noPerm", functionName));
		}
		if (parameters.size() < 1) {
			throw new ParserException(I18N.getText("macro.function.general.notEnoughParam", functionName, 1, parameters.size()));
		}
		Zone zone = MapTool.getFrame().getCurrentZoneRenderer().getZone();
		Token token = zone.resolveToken(parameters.get(0).toString());
		if (token == null) {
			throw new ParserException(I18N.getText("macro.function.general.unknownToken", functionName, parameters.get(0).toString()));
		}
		((MapToolVariableResolver) parser.getVariableResolver()).setTokenIncontext(token);
		return "";
	}
}
