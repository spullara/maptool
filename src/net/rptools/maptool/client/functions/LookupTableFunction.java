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
import net.rptools.maptool.client.macro.MacroManager;
import net.rptools.maptool.language.I18N;
import net.rptools.maptool.model.LookupTable;
import net.rptools.maptool.model.LookupTable.LookupEntry;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;
import net.rptools.parser.function.ParameterException;

public class LookupTableFunction extends AbstractFunction {

	public LookupTableFunction() {
		super(1, 3, "tbl", "table", "tblImage", "tableImage");
	}
	
	/** The singleton instance. */
	private final static LookupTableFunction instance = new LookupTableFunction();
	
	/**
	 * Gets the instance of TableLookup.
	 * @return the TableLookup.
	 */
	public static LookupTableFunction getInstance() {
		return instance;
	}

	@Override
	public Object childEvaluate(Parser parser, String function, List<Object> params) throws ParserException {

		String name = params.get(0).toString();
		
		String roll = null;
		if (params.size() > 1) {
			roll = params.get(1).toString().length() == 0 ? null : params.get(1).toString();
		} 		
		
		LookupTable lookupTable = MapTool.getCampaign().getLookupTableMap().get(name);
		if (lookupTable == null) {
			throw new ParserException(I18N.getText("macro.function.LookupTableFunctions.unknownTable", function, name));
		}
		
    	LookupEntry result = lookupTable.getLookup(roll);

    	if (function.equals("table") || function.equals("tbl")) {
        	String val = result.getValue();
    		try {
    			BigDecimal bival = new BigDecimal(val);
    			return bival;
    		} catch (NumberFormatException nfe) {
    			return val;
    		}
    	} else { // We want the image URI
    		
    		if (result.getImageId() == null) {
    			throw new ParserException(I18N.getText("macro.function.LookupTableFunctions.noImage", function, name));
    		}
    		
    		BigDecimal size = null;
    		if (params.size() > 2) {
    			if (params.get(2) instanceof BigDecimal) {
    				size = (BigDecimal) params.get(2);
    			} else {
    				throw new ParserException(I18N.getText("macro.function.LookupTableFunctions.invalidSize", function));
    			}
    		}
    		
    		StringBuilder assetId = new StringBuilder("asset://");
    		assetId.append(result.getImageId().toString());
    		if (size != null) {
    			int i = Math.max(size.intValue(), 1); // Constrain to a minimum of 1
    			assetId.append("-");
    			assetId.append(i);
    		}
    		return assetId.toString();
    	}
	}
}
