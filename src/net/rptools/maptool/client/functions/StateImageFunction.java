package net.rptools.maptool.client.functions;

import java.math.BigDecimal;
import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.token.ImageTokenOverlay;
import net.rptools.maptool.client.ui.token.TokenOverlay;
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
		TokenOverlay over = MapTool.getCampaign().getTokenStatesMap().get(stateName);
		if (over == null) {
			throw new ParserException("Unknown state (" + stateName + ")" );
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
			throw new ParserException("State " + stateName + " is not an image state.");
		}
	}

	
}
