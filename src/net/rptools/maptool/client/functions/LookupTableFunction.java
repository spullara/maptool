package net.rptools.maptool.client.functions;

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
		super(1, 2, "tbl");
	}
	
	@Override
	public Object childEvaluate(Parser parser, String function, List<Object> params) throws ParserException {

		String name = params.get(0).toString();
		String roll = params.size() > 1 ? params.get(1).toString() : null;

		LookupTable lookupTable = MapTool.getCampaign().getLookupTableMap().get(name);
		if (lookupTable == null) {
			return "No such table: " + name;
		}
		
    	LookupEntry result = lookupTable.getLookup(roll);

    	return result.getValue();
	}
}
