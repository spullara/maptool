/**
 * 
 */
package net.rptools.maptool.client.functions;

import java.util.List;
import java.util.regex.Pattern;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolVariableResolver;
import net.rptools.maptool.client.ui.commandpanel.CommandPanel;
import net.rptools.maptool.language.I18N;
import net.rptools.maptool.model.TextMessage;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;

/**
 * Chat related functions like broadcast()
 * 
 * @author bdornauf
 */
public class ChatFunction extends AbstractFunction {

	/**
	 * Ctor
	 */
	public ChatFunction() {
		super(1, 3, "broadcast");
	}

	/**
	 * The singleton instance.
	 * */
	private final static ChatFunction instance = new ChatFunction();

	/**
	 * Gets the Input instance.
	 * 
	 * @return the instance.
	 */
	public static ChatFunction getInstance() {
		return instance;
	}

	@Override
	public Object childEvaluate(Parser parser, String functionName,
			List<Object> parameters) throws ParserException {
		MapToolVariableResolver resolver = ((MapToolVariableResolver) parser
				.getVariableResolver());

		if (functionName.equals("broadcast")) {
			return broadcast(resolver, parameters);
		} else {
			throw new ParserException("Unknown function: " + functionName);
		}
	}

	/**
	 * broadcast sends a message to the chatpanel of all clients using
	 * TextMessage.SAY
	 * 
	 * @return empty string
	 */
	private Object broadcast(MapToolVariableResolver resolver, List<Object> param) throws ParserException {
		// broadcast shall be trusted
		if (!MapTool.getParser().isMacroTrusted()) {
			throw new ParserException(I18N.getText("macro.function.general.noPerm", "broadcast"));
		}

		if (param.size() >= 1 && param.size() <= 3) {
			String message = param.get(0).toString();
			// have to check the message for cheating
			// or constructed cheats wouldnt be found
			message = checkForCheating(message);

			String targets = param.size() > 1 ? param.get(1).toString() : "";
			String delim = param.size() > 2 ? param.get(2).toString() : ",";

			if (message != "") {
				if (param.size() == 1) {
					MapTool.addGlobalMessage(message);
				} else {
					// specified separator
					MapTool.addGlobalMessage(message, targets, delim);
				}
			}
			return "";
		} else {
			throw new ParserException("broadcast(message [, targets]): wrong number of arguments");
		}
	}

	/**
	 * check if a message contains characters flagged as cheating and delete the
	 * message if found. As well
	 * 
	 * @param message
	 * @return message
	 */
	private String checkForCheating(String message) {
		// Detect whether the person is attempting to fake rolls.
		Pattern cheater_pattern = CommandPanel.CHEATER_PATTERN;

		if (cheater_pattern.matcher(message).find()) {
			MapTool.addServerMessage(TextMessage.me(null, "Cheater. You have been reported."));
			MapTool.serverCommand().message(TextMessage.gm(null, MapTool.getPlayer().getName() + " was caught <i>cheating</i>: " + message));
			message = "";
		}
		return message;
	}
}
