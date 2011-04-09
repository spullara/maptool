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
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;

public class HasImpersonated extends AbstractFunction {
	private static final HasImpersonated instance = new HasImpersonated();

	private HasImpersonated() {
		super(0, 0, "hasImpersonated");
	}

	public static HasImpersonated getInstance() {
		return instance;
	}

	@Override
	public Object childEvaluate(Parser parser, String functionName, List<Object> parameters) throws ParserException {
		Zone zone = MapTool.getFrame().getCurrentZoneRenderer().getZone();
		Token t;
		GUID guid = MapTool.getFrame().getCommandPanel().getIdentityGUID();
		if (guid != null)
			t = MapTool.getFrame().getCurrentZoneRenderer().getZone().getToken(guid);
		else
			t = zone.resolveToken(MapTool.getFrame().getCommandPanel().getIdentity());
		return t == null ? BigDecimal.ZERO : BigDecimal.ONE;
	}
}
