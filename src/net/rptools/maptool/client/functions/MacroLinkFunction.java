package net.rptools.maptool.client.functions;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.TextMessage;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;

public class MacroLinkFunction extends AbstractFunction {

	
	private enum OutputTo {
		SELF,
		NONE,
		GM,
	}
	
	/** Singleton instance of the MacroLinkFunction class. */
	private static final MacroLinkFunction instance = new MacroLinkFunction();
	
	
	/** 
	 * Gets and instance of the MacroLinkFunction class.
	 * @return an instance of MacroLinkFunction.
	 */
	public static MacroLinkFunction getInstance() {
		return instance;
	}
	
	
	private MacroLinkFunction() {
		super(1,5, "macroLink", "macroLinkText");		
	}

	@Override
	public Object childEvaluate(Parser parser, String functionName, List<Object> args)
			throws ParserException {
		
		boolean formatted;
		
		String macroName;
		String linkText;
		String linkArgs;
		String linkWho;
		String linkTarget;
		
		if ("macroLink".equalsIgnoreCase(functionName)) {
			formatted = true;
			linkText = args.get(0).toString();
			if (args.size() < 2) {
				throw new ParserException("Missing macro name"); 
			}
			macroName = args.get(1).toString();
			
			if (args.size() > 2) {
				linkWho = args.get(2).toString(); 
			} else {
				linkWho = "none";
			}
			
			if (args.size() > 3) {
				linkArgs = args.get(3).toString();
			} else {
				linkArgs = "";
			}
			
			if (args.size() > 4) {
				linkTarget = args.get(4).toString();
			} else {
				linkTarget = "Impersonated";
			}
			
		} else {
			formatted = false;
			linkText = "";
			macroName = args.get(0).toString();
			
			if (args.size() > 1) {
				linkWho = args.get(1).toString(); 
			} else {
				linkWho = "none";
			}
			
			if (args.size() > 2) {
				linkArgs = args.get(2).toString();
			} else {
				linkArgs = "";
			}
			
			if (args.size() > 3) {
				linkTarget = args.get(3).toString();
			} else {
				linkTarget = "Impersonated";
			}
		}
		
		StringBuilder sb = new StringBuilder();
		
		if (formatted) {
			sb.append("<a href='");
		}
		sb.append(createMacroText(macroName, linkWho, linkTarget, linkArgs));
		if (formatted) {
			sb.append("'>").append(linkText).append("</a>");
		}
		return sb.toString();

	}

	
	public String createMacroText(String macroName, String who, String target, String args) throws ParserException {
		StringBuilder sb = new StringBuilder();
		sb.append("macro://").append(macroName).append("/").append(who);
		sb.append("/").append(target).append("?");
		sb.append(strPropListToArgs(args));
		return sb.toString();
	}
	
	
	/**
	 * Converts a URL argument string into a property list.
	 * @param args the URL argument string.
	 * @return a property list representation of the arguments.
	 * @throws ParserException if the argument encoding is incorrect.
	 */
	public String argsToStrPropList(String args) throws ParserException {
		String vals[] = args.split("&");
		StringBuilder propList = new StringBuilder();
		
		try {
			for (String s : vals) {
				String decoded = URLDecoder.decode(s, "utf8");
				decoded = decoded.replaceAll(";", "&#59");
				if (propList.length() == 0) {
					propList.append(decoded); 
				} else {
					propList.append(" ; ");
					propList.append(decoded);
				}
			}
			return propList.toString();
		} catch (UnsupportedEncodingException e) {
			throw new ParserException(e);
		}
	}
	
	/**
	 * Takes a Property list and creates a URL Ready argument list.
	 * @param props The property list to convert.
	 * @return a string that can be used as an argument to a url.
	 * @throws ParserException if there is an error in encoding.
	 */
	public String strPropListToArgs(String props) throws ParserException {
		String vals[] = props.split(";");
		StringBuilder args = new StringBuilder();
		try {
			for (String s : vals) {
				s = s.trim();
				String encoded = URLEncoder.encode(s, "utf-8");
				if (args.length() == 0) {
					args.append(encoded);
				} else {
					args.append("&");
					args.append(encoded);
				}
			}
		} catch (UnsupportedEncodingException e) {
			throw new ParserException(e);
		}
		
		return args.toString();
	}
	
	/**
	 * Gets a string that describes the macro link.
	 * @param link the link to get the tool tip of.
	 * @return a string containing the tool tip.
	 */
	public String macroLinkToolTip(String link) {
		Matcher m = Pattern.compile("([^:]*)://([^/]*)/([^/]*)/([^?]*)(?:\\?(.*))?").matcher(link);
		StringBuilder tip = new StringBuilder();

		if (m.matches()) {

			if (m.group(1).equalsIgnoreCase("macro")) {
			
				tip.append("<html>");
				tip.append("<tr><th><u>&laquo;Macro Link&raquo;</b></u></th></tr>");
				tip.append("<table>");
				tip.append("<tr><th>Output to</th><td>").append(m.group(3)).append("</td></td>");
				tip.append("<tr><th>Command</th><td>").append(m.group(2)).append("</td></td>");
				String val = m.group(5);
				if (val != null) {
					try {
						Double.parseDouble(val);
						//  Do nothing as its a number
					} catch (NumberFormatException e) {
						try {
							val = "\"" +  argsToStrPropList(val) + "\"";
						} catch (ParserException e1) {
							MapTool.addLocalMessage("Error running macro link: " + e1.getMessage());
						}
					}
					tip.append("<tr><th>Arguments</th><td>").append(val).append("</td></tr>");
				}
				String[] targets = m.group(4).split(",");
				tip.append("</table>");
				tip.append("<b>Run On</b><ul>");
				Zone zone = MapTool.getFrame().getCurrentZoneRenderer().getZone();
				for (String t : targets) {
					String name;
					if (t.equalsIgnoreCase("impersonated")) {
						name = "Impersonated Token";
					} else if (t.equalsIgnoreCase("selected")) {
						name = "Selected Tokens";
					} else {
						Token token = zone.resolveToken(t);
						if (token == null) {
							name = "Unknown";
						} else {
							name = token.getName();
						}
					}
					tip.append("<li>").append(name).append("</li>");
				}
				tip.append("</ul>");
	
				tip.append("</html>");
			}
		}
		
		return tip.toString();
	}

	/** 
	 * Runs the macro specified by the link.
	 * @param link the link to the macro.
	 */
	public void runMacroLink(String link) {
		runMacroLink(link, false);
	}
	
	/** 
	 * Runs the macro specified by the link.
	 * @param link the link to the macro.
	 * @param setVars should the variables be set in the macro context 
	 *        as well as passed in as macro.args.
	 */
	public void runMacroLink(String link, boolean setVars) {
		if (link == null || link.length() == 0) {
			return;
		}

		Matcher m = Pattern.compile("([^:]*)://([^/]*)/([^/]*)/([^?]*)(?:\\?(.*))?").matcher(link);
		
		
		if (m.matches()) {
			OutputTo outputTo;
			StringBuilder command = new StringBuilder();
			if (m.group(1).equalsIgnoreCase("macro")) {
				
				String who = m.group(3);
				if (who.equalsIgnoreCase("self")) {
					outputTo = OutputTo.SELF;
				} else if (who.equals("gm")) {
					outputTo = OutputTo.GM;
				} else if (who.equals("none")) {
					outputTo = OutputTo.NONE;
				} else {
					outputTo = OutputTo.NONE;
				}
				
				command.append("[MACRO('");
				command.append(m.group(2));
				command.append("'): ");
				String val = m.group(5);
				if (val != null) {
					try {
						Double.parseDouble(val);
						//  Do nothing as its a number
					} catch (NumberFormatException e) {
						try {
							val = "\"" +  argsToStrPropList(val) + "\"";
						} catch (ParserException e1) {
							MapTool.addLocalMessage("Error running macro link: " + e1.getMessage());
						}
					}
					command.append(val);
				} 
				command.append("]");

				String[] targets = m.group(4).split(",");
				Zone zone = MapTool.getFrame().getCurrentZoneRenderer().getZone();
				
				StringBuilder output = new StringBuilder();
				try {
					for (String t : targets) {
						if (t.equalsIgnoreCase("impersonated")) {
							String identity = MapTool.getFrame().getCommandPanel().getIdentity();
							Token token = zone.resolveToken(identity);
							output.append(MapTool.getParser().parseLine(token, command.toString()));
						} else if (t.equalsIgnoreCase("selected")) {
							for (GUID id : MapTool.getFrame().getCurrentZoneRenderer().getSelectedTokenSet()) {
								Token token = zone.getToken(id);
								output.append(MapTool.getParser().parseLine(token, command.toString()));
							}
						} else {
							Token token = zone.resolveToken(t);
							if (token == null) {
							} else {
								output.append(MapTool.getParser().parseLine(token, command.toString()));
							}
						}
					}
				} catch (ParserException e) {
					MapTool.addLocalMessage(e.getMessage());
				}
			
				switch (outputTo) {
					case SELF:
						MapTool.addLocalMessage(output.toString());
						break;
					case GM: 
				        MapTool.addMessage(new TextMessage(TextMessage.Channel.GM, null, MapTool.getPlayer().getName(),
				        		MapTool.getPlayer().getName() + " says to the GM: " + output.toString(), null));
				        break;
					case NONE:
						// Do nothing with the output.
						break;
				}
			}
		}

	}

}
