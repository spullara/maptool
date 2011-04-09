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

import net.rptools.maptool.client.ui.htmlframe.HTMLFrame;
import net.rptools.maptool.client.ui.htmlframe.HTMLFrameFactory;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;

public class MacroDialogFunctions extends AbstractFunction {
	private static final MacroDialogFunctions instance = new MacroDialogFunctions();

	private MacroDialogFunctions() {
		super(1, 1, "isDialogVisible",
				"isFrameVisible",
				"closeDialog",
				"resetFrame",
				"closeFrame"
		);
	}

	public static MacroDialogFunctions getInstance() {
		return instance;
	}

	@Override
	public Object childEvaluate(Parser parser, String functionName, List<Object> parameters) throws ParserException {
		if (functionName.equals("isDialogVisible")) {
			return HTMLFrameFactory.isVisible(false, parameters.get(0).toString()) ? BigDecimal.ONE : BigDecimal.ZERO;
		}
		if (functionName.equals("isFrameVisible")) {
			return HTMLFrameFactory.isVisible(true, parameters.get(0).toString()) ? BigDecimal.ONE : BigDecimal.ZERO;
		}
		if (functionName.equals("closeDialog")) {
			HTMLFrameFactory.close(false, parameters.get(0).toString());
			return "";
		}
		if (functionName.equals("closeFrame")) {
			HTMLFrameFactory.close(true, parameters.get(0).toString());
			return "";
		}
		if (functionName.equals("resetFrame")) {
			HTMLFrame.center(parameters.get(0).toString());
			return "";
		}
		return null;
	}
}
