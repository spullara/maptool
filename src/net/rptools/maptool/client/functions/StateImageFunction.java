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
import net.rptools.maptool.client.ui.token.BooleanTokenOverlay;
import net.rptools.maptool.client.ui.token.ImageTokenOverlay;
import net.rptools.maptool.language.I18N;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;

public class StateImageFunction extends AbstractFunction {

	/** The singleton instance. */
	private final static StateImageFunction instance = new StateImageFunction();
	
	private StateImageFunction() {
		super(1, 2, "getStateImage");
	}
	
	
	/** 
	 * Gets the StateImage instance.
	 * @return the instance.
	 */
	public static StateImageFunction getInstance() {
		return instance;
	}
	
	
	@Override
	public Object childEvaluate(Parser parser, String functionName, List<Object> args) throws ParserException {
		String stateName;
		BigDecimal size = null;
		
		stateName = args.get(0).toString();
		if (args.size() > 1) {
			if (args.get(1) instanceof BigDecimal) {
				size = (BigDecimal) args.get(1);
			}	
		}
		BooleanTokenOverlay over = MapTool.getCampaign().getTokenStatesMap().get(stateName);
		if (over == null) {
			throw new ParserException(I18N.getText("macro.function.stateImage.unknownState", "getStateImage()", stateName ));
		}
		if (over instanceof ImageTokenOverlay) {
			StringBuilder assetId = new StringBuilder("asset://");
			assetId.append(((ImageTokenOverlay)over).getAssetId().toString());
			if (size != null) {
				assetId.append("-");
				// Constrain it slightly, so its between 1 and 500 :)
				int i = Math.max(Math.min(size.intValue(), 500),1);
				assetId.append(i);
			}
			return assetId.toString();
		} else {
			throw new ParserException(I18N.getText("macro.function.stateImage.notImage", functionName, stateName));
		}
	}

	
}
