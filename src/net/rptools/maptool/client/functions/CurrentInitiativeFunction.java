package net.rptools.maptool.client.functions;

import java.math.BigDecimal;
import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.InitiativeList;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;

/**
 * Accessor for the combatant with the current initiative
 * 
 * @author Jay
 */
public class CurrentInitiativeFunction extends AbstractFunction {

    /** Handle adding one, all, all PCs or all NPC tokens. */
	private CurrentInitiativeFunction() {
		super(0, 1, "getCurrentInitiative", "setCurrentInitiative");
	}
	
    /** singleton instance of this function */
    private final static CurrentInitiativeFunction instance = new CurrentInitiativeFunction();

    /** @return singleton instance */
	public static CurrentInitiativeFunction getInstance() { return instance; }	
	
	/**
	 * @see net.rptools.parser.function.AbstractFunction#childEvaluate(net.rptools.parser.Parser, java.lang.String, java.util.List)
	 */
	@Override
	public Object childEvaluate(Parser parser, String functionName, List<Object> args) throws ParserException {
        if (!MapTool.getFrame().getInitiativePanel().hasGMPermission())
            throw new ParserException("Only the GM can get or set the current initiative.");
	    if (functionName.equals("getCurrentInitiative")) {
	        return getCurrentInitiative();
	    } else {
	        if (args.size() != 1)
	            throw new ParserException("Must call setInitiativeRound with one parameter");
            setCurrentInitiative(args.get(0));
	        return args.get(0);
        } // endif
	}
	
	/**
	 * Get the current initiative;
	 * 
	 * @return The current initiative
	 */
	public Object getCurrentInitiative() {
        InitiativeList list = MapTool.getFrame().getCurrentZoneRenderer().getZone().getInitiativeList();
        return new BigDecimal(list.getCurrent());
	}
	
	/**
	 * Set the initiative round.
	 * 
	 * @param value New value for the round.
	 */
	public void setCurrentInitiative(Object value) {
        InitiativeList list = MapTool.getFrame().getCurrentZoneRenderer().getZone().getInitiativeList();
        list.setCurrent(InitiativeRoundFunction.getInt(value));
    }
}