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

import java.math.BigDecimal;
import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;

public class IsTrustedFunction extends AbstractFunction {
	private static final IsTrustedFunction instance = new IsTrustedFunction();

	private IsTrustedFunction() {
		super(0, 0, "isTrusted", "isGM");
	}
	
	public static IsTrustedFunction getInstance() {
		return instance;
	}

	@Override
	public Object childEvaluate(Parser parser, String functionName,
			List<Object> parameters) throws ParserException {
		if (functionName.equals("isTrusted")) {
			return MapTool.getParser().isMacroTrusted() ? BigDecimal.ONE : BigDecimal.ZERO; 
		} else {
			return MapTool.getPlayer().isGM() ? BigDecimal.ONE : BigDecimal.ZERO;
		}
	}

}
