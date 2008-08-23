package net.rptools.maptool.client.functions;

import java.math.BigDecimal;
import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.InitiativeList;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;

/**
 * Advance the initiative
 * 
 * @author Jay
 */
public class MiscInitiativeFunction extends AbstractFunction {

    /** Handle adding one, all, all PCs or all NPC tokens. */
	private MiscInitiativeFunction() {
		super(0, 0, "nextInitiative", "sortInitiative", "initiativeSize");
	}
	
    /** singleton instance of this function */
    private final static MiscInitiativeFunction instance = new MiscInitiativeFunction();

    /** @return singleton instance */
	public static MiscInitiativeFunction getInstance() { return instance; }	
	
	/**
	 * @see net.rptools.parser.function.AbstractFunction#childEvaluate(net.rptools.parser.Parser, java.lang.String, java.util.List)
	 */
	@Override
	public Object childEvaluate(Parser parser, String functionName, List<Object> args) throws ParserException {
        InitiativeList list = MapTool.getFrame().getCurrentZoneRenderer().getZone().getInitiativeList();
        if (MapTool.getPlayer() != null && !MapTool.getPlayer().isGM())
            throw new ParserException("Only the GM can " + functionName + ".");
        if (functionName.equals("nextInitiative")) {
            list.nextInitiative();
            return new BigDecimal(list.getCurrent());
        } else if (functionName.equals("sortInitiative")) {
            list.sort();
            return new BigDecimal(list.getSize());
        } else if (functionName.equals("initiativeSize")) {
            return new BigDecimal(list.getSize());
        } // endif
        throw new ParserException("Unexpected function: " + functionName);
	}
}