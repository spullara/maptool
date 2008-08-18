package net.rptools.maptool.client.functions;

import java.math.BigDecimal;
import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.macro.MacroManager;
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
			return "No such table: " + name;
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
    			throw new ParserException("No image available.");
    		}
    		
    		BigDecimal size = null;
    		if (params.size() > 2) {
    			if (params.get(2) instanceof BigDecimal) {
    				size = (BigDecimal) params.get(2);
    			} else {
    				throw new ParserException("Invalid size.");
    			}
    		}
    		
    		StringBuilder assetId = new StringBuilder("asset://");
    		assetId.append(result.getImageId().toString());
    		if (size != null) {
    			int i = Math.min(Math.max(size.intValue(), 1), 500); // Constrain to between 1 and 500
    			assetId.append("-");
    			assetId.append(i);
    		}
    		return assetId.toString();
    	}
	}
}
