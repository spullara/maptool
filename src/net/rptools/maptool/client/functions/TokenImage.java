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

public class TokenImage extends AbstractFunction {

	/** Singleton instance. */
	private final static TokenImage instance = new TokenImage();

	private TokenImage() {
		super(0,2, "getTokenImage");
	}

	
	/**
	 * Gets the TokenImage instance.
	 * @return the instance.
	 */
	public static TokenImage getInstance() {
		return instance;
	}
	
	
	@Override
	public Object childEvaluate(Parser parser, String functionName, List<Object> args)
			throws ParserException {
		
		Token token;
		BigDecimal size = null;
		
		if (args.size() > 0 && args.get(0) instanceof GUID) {
			token = MapTool.getFrame().getCurrentZoneRenderer().getZone().getToken((GUID)args.get(0));
		} else if (args.size() > 0 && args.get(0) instanceof BigDecimal) {
			MapToolVariableResolver res = (MapToolVariableResolver)parser.getVariableResolver();
			token = res.getTokenInContext();
			size = (BigDecimal) args.get(0);
		} else if (args.size() > 0) {
			throw new ParserException("getTokenImage() unknown argumets.");
		} else {
			MapToolVariableResolver res = (MapToolVariableResolver)parser.getVariableResolver();
			token = res.getTokenInContext();
		}
		
		StringBuilder assetId = new StringBuilder("asset://");
		assetId.append(token.getImageAssetId().toString());
		if (size != null) {
			assetId.append("-");
			// Constrain it slightly, so its between 1 and 500 :)
			int i = Math.max(Math.min(size.intValue(), 500),1);
			assetId.append(i);
		}
		return assetId.toString();
	}
}
