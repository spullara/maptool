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

import java.awt.Color;
import java.awt.EventQueue;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.rptools.lib.MD5Key;
import net.rptools.maptool.client.AppPreferences;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolVariableResolver;
import net.rptools.maptool.client.functions.AbortFunction.AbortFunctionException;
import net.rptools.maptool.client.macro.MacroContext;
import net.rptools.maptool.language.I18N;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.MacroButtonProperties;
import net.rptools.maptool.model.ObservableList;
import net.rptools.maptool.model.Player;
import net.rptools.maptool.model.TextMessage;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.util.StringUtil;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class MacroLinkFunction extends AbstractFunction {
	private enum OutputTo {
		SELF, NONE, GM, ALL, SELF_AND_GM, LIST
	}

	/** Singleton instance of the MacroLinkFunction class. */
	private static final MacroLinkFunction instance = new MacroLinkFunction();

	/**
	 * Gets and instance of the MacroLinkFunction class.
	 * 
	 * @return an instance of MacroLinkFunction.
	 */
	public static MacroLinkFunction getInstance() {
		return instance;
	}

	private MacroLinkFunction() {
		super(1, 5, "macroLink", "macroLinkText", "execLink");
	}

	@Override
	public Object childEvaluate(Parser parser, String functionName, List<Object> args) throws ParserException {

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
				throw new ParserException(I18N.getText("macro.function.macroLink.missingName", "macroLink"));
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

		} else if ("macroLinkText".equalsIgnoreCase(functionName)) {
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
		} else { // execLink
			if (!MapTool.getParser().isMacroTrusted()) {
				throw new ParserException(I18N.getText("macro.function.general.noPerm", functionName));
			}

			boolean defer = false;
			if (args.size() > 1) {
				if (args.get(1) instanceof BigDecimal) {
					defer = BigDecimal.ZERO.equals(args.get(1)) ? false : true;
				}
			}
			execLink((String) args.get(0), defer);
			return "";
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

	private void execLink(final String link, boolean defer) {
		if (defer) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					runMacroLink(link);
				}
			});
		} else {
			this.runMacroLink(link);
		}
	}

	/**
	 * <p>
	 * This method generates a string in the form of a macro invocation.
	 * </p>
	 * <p>
	 * The resulting output is of the form <code>macro://</code><i>macroName</i><code>/</code><i>who</i> <code>/</code>
	 * <i>target</i><code>?</code><i>args</i>
	 * </p>
	 * <p>
	 * The <code>args</code> parameter is a String which is converted to a property list and then back to a String.
	 * </p>
	 * 
	 * @param macroName
	 *            such as <code>MacroName@Lib:Core</code>
	 * @param who
	 *            where output should go
	 * @param target
	 *            the string <code>impersonated</code>, <code>all</code>
	 * @param args
	 * @return
	 * @throws ParserException
	 */
	public String createMacroText(String macroName, String who, String target, String args) throws ParserException {
		if (macroName.toLowerCase().endsWith("@this")) {
			macroName = macroName.substring(0, macroName.length() - 4) + MapTool.getParser().getMacroSource();
		}
		StringBuilder sb = new StringBuilder();
		sb.append("macro://").append(macroName).append("/").append(who);
		sb.append("/").append(target).append("?");
		sb.append(encode(args));
		return sb.toString();
	}

	private String encode(String str) throws ParserException {
		try {
			JSONObject.fromObject(str);
			try {
				return URLEncoder.encode(str, "utf-8");
			} catch (UnsupportedEncodingException e) {
				throw new ParserException(e);
			}
		} catch (JSONException e) {
			return strPropListToArgs(str);
		}
	}

	private String decode(String str) throws ParserException {
		try {
			return JSONObject.fromObject(URLDecoder.decode(str, "utf-8")).toString();
		} catch (UnsupportedEncodingException e) {
			throw new ParserException(e);
		} catch (JSONException e) {
			return argsToStrPropList(str);
		}

	}

	/**
	 * Converts a URL argument string into a property list.
	 * 
	 * @param args
	 *            the URL argument string.
	 * @return a property list representation of the arguments.
	 * @throws ParserException
	 *             if the argument encoding is incorrect.
	 */
	public String argsToStrPropList(String args) throws ParserException {
		String vals[] = args.split("&");
		StringBuilder propList = new StringBuilder();

		try {
			for (String s : vals) {
				String decoded = URLDecoder.decode(s, "utf-8");
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
	 * 
	 * @param props
	 *            The property list to convert.
	 * @return a string that can be used as an argument to a url.
	 * @throws ParserException
	 *             if there is an error in encoding.
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
	 * 
	 * @param link
	 *            the link to get the tool tip of.
	 * @return a string containing the tool tip.
	 */
	public String macroLinkToolTip(String link) {
		Matcher m = Pattern.compile("([^:]*)://([^/]*)/([^/]*)/([^?]*)(?:\\?(.*))?").matcher(link);
		StringBuilder tip = new StringBuilder();

		if (m.matches()) {

			if (m.group(1).equalsIgnoreCase("macro")) {

				tip.append("<html>");
				if (isAutoExecLink(link)) {
					tip.append("<tr><th style='color: red'><u>&laquo;").append(I18N.getText("macro.function.macroLink.autoExecToolTip")).append("&raquo;</b></u></th></tr>");
				} else {
					tip.append("<tr><th><u>&laquo;Macro Link&raquo;</b></u></th></tr>");
				}
				tip.append("<table>");
				tip.append("<tr><th>Output to</th><td>").append(m.group(3)).append("</td></td>");
				tip.append("<tr><th>Command</th><td>").append(m.group(2)).append("</td></td>");
				String val = m.group(5);
				if (val != null) {
					try {
						Double.parseDouble(val);
						// Do nothing as its a number
					} catch (NumberFormatException e) {
						try {
							val = "\"" + argsToStrPropList(val) + "\"";
						} catch (ParserException e1) {
							MapTool.addLocalMessage(I18N.getText("macro.function.macroLink.errorRunning", e1.getLocalizedMessage()));
						}
					}
					tip.append("<tr><th>").append(I18N.getText("macro.function.macroLink.arguments")).append(val).append("</td></tr>");
				}
				String[] targets = m.group(4).split(",");
				tip.append("</table>");
				tip.append("<b>").append(I18N.getText("macro.function.macroLink.executeOn")).append("</b><ul>");
				Zone zone = MapTool.getFrame().getCurrentZoneRenderer().getZone();
				for (String t : targets) {
					String name;
					if (t.equalsIgnoreCase("impersonated")) {
						name = I18N.getText("macro.function.macroLink.impersonated");
					} else if (t.equalsIgnoreCase("selected")) {
						name = I18N.getText("macro.function.macroLink.selected");
					} else {
						Token token = zone.resolveToken(t);
						if (token == null) {
							name = I18N.getText("macro.function.macroLink.unknown");
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
	 * 
	 * @param link
	 *            the link to the macro.
	 */
	public void runMacroLink(String link) {
		runMacroLink(link, false);
	}

	private static final Pattern macroLink = Pattern.compile("(?s)([^:]*)://([^/]*)/([^/]*)/([^?]*)(?:\\?(.*))?");

	/**
	 * Runs the macro specified by the link.
	 * 
	 * @param link
	 *            the link to the macro.
	 * @param setVars
	 *            should the variables be set in the macro context as well as passed in as macro.args.
	 */
	public void runMacroLink(String link, boolean setVars) {
		if (link == null || link.length() == 0) {
			return;
		}
		Matcher m = macroLink.matcher(link);

		if (m.matches()) {
			OutputTo outputTo;
			String macroName = "";
			String args = "";
			Set<String> outputToPlayers = new HashSet<String>();

			if (m.group(1).equalsIgnoreCase("macro")) {

				String who = m.group(3);
				if (who.equalsIgnoreCase("self")) {
					outputTo = OutputTo.SELF;
				} else if (who.equalsIgnoreCase("gm")) {
					outputTo = OutputTo.GM;
				} else if (who.equalsIgnoreCase("none")) {
					outputTo = OutputTo.NONE;
				} else if (who.equalsIgnoreCase("all") || who.equalsIgnoreCase("say")) {
					outputTo = OutputTo.ALL;
				} else if (who.equalsIgnoreCase("gm-self") || who.equalsIgnoreCase("gmself")) {
					outputTo = OutputTo.SELF_AND_GM;
				} else if (who.equalsIgnoreCase("list")) {
					outputTo = OutputTo.LIST;
				} else {
					outputTo = OutputTo.NONE;
				}
				macroName = m.group(2);

				String val = m.group(5);
				if (val != null) {
					try {
						Double.parseDouble(val);
						// Do nothing as its a number
					} catch (NumberFormatException e) {
						try {
							val = argsToStrPropList(val);
						} catch (ParserException e1) {
							MapTool.addLocalMessage("Error running macro link: " + e1.getMessage());
						}
					}
					args = val;
					try {
						JSONObject jobj = JSONObject.fromObject(args);
						if (jobj.containsKey("mlOutputList")) {
							outputToPlayers.addAll(jobj.getJSONArray("mlOutputList"));
						}
					} catch (Exception e) {
						// Do nothing as we just dont populate the list.
					}
				}

				String[] targets = m.group(4).split(",");
				Zone zone = MapTool.getFrame().getCurrentZoneRenderer().getZone();

				try {
					for (String t : targets) {
						if (t.equalsIgnoreCase("impersonated")) {
							Token token;
							GUID guid = MapTool.getFrame().getCommandPanel().getIdentityGUID();
							if (guid != null)
								token = MapTool.getFrame().getCurrentZoneRenderer().getZone().getToken(guid);
							else
								token = zone.resolveToken(MapTool.getFrame().getCommandPanel().getIdentity());
							MapToolVariableResolver resolver = new MapToolVariableResolver(token);
							String output = MapTool.getParser().runMacro(resolver, token, macroName, args);
							doOutput(token, outputTo, output, outputToPlayers); // TODO
						} else if (t.equalsIgnoreCase("selected")) {
							for (GUID id : MapTool.getFrame().getCurrentZoneRenderer().getSelectedTokenSet()) {
								Token token = zone.getToken(id);
								MapToolVariableResolver resolver = new MapToolVariableResolver(token);
								String output = MapTool.getParser().runMacro(resolver, token, macroName, args);
								doOutput(token, outputTo, output, outputToPlayers);
							}
						} else {
							Token token = zone.resolveToken(t);
							MapToolVariableResolver resolver = new MapToolVariableResolver(token);
							String output = MapTool.getParser().runMacro(resolver, token, macroName, args);
							doOutput(token, outputTo, output, outputToPlayers);
						}
					}
				} catch (AbortFunctionException e) {
					// Do nothing
				} catch (ParserException e) {
					MapTool.addLocalMessage(e.getMessage());
				}

			}
		}

	}

	private void doOutput(Token token, OutputTo outputTo, String line, Set<String> playerList) {

		/*
		 * First we check our player list to make sure we are not sending things out multiple times or the wrong way
		 * This looks looks a little ugly, but all it is doing is searching for the strings say, gm, or gmself, and if
		 * it contains no other strings changes it to a more appropriate for such as /togm, /self, etc. If it contains
		 * other names then gm, self etc will be replaced with player names.
		 */
		if (outputTo == OutputTo.LIST) {
			if (playerList == null) {
				outputTo = OutputTo.NONE;
			} else if (playerList.contains("all") || playerList.contains("say")) {
				outputTo = OutputTo.ALL;
			} else if (playerList.contains("gmself")) {
				playerList.remove("gmself");
				if (playerList.size() == 0) { // if that was only thing in the list then dont use whispers
					outputTo = OutputTo.SELF_AND_GM;
				} else {
					playerList.addAll(getGMs());
					playerList.add(getSelf());
				}
			} else if (playerList.contains("gm-self")) {
				playerList.remove("gm-self");
				if (playerList.size() == 0) { // if that was only thing in the list then dont use whispers
					outputTo = OutputTo.SELF_AND_GM;
				} else {
					playerList.addAll(getGMs());
					playerList.add(getSelf());
				}
			} else if (playerList.contains("gm") && playerList.contains("self")) {
				playerList.remove("gm");
				playerList.remove("self");
				if (playerList.size() == 0) { // if that was only thing in the list then dont use whispers
					outputTo = OutputTo.SELF_AND_GM;
				} else {
					playerList.addAll(getGMs());
					playerList.add(getSelf());
				}
			} else if (playerList.contains("gm")) {
				playerList.remove("gm");
				if (playerList.size() == 0) { // if that was only thing in the list then dont use whispers
					outputTo = OutputTo.GM;
				} else {
					playerList.addAll(getGMs());
					playerList.add(getSelf());
				}
			} else if (playerList.contains("self")) {
				playerList.remove("self");
				if (playerList.size() == 0) { // if that was only thing in the list then dont use whispers
					outputTo = OutputTo.SELF;
				} else {
					playerList.add(getSelf());
				}
			}
		}

		switch (outputTo) {
		case SELF:
			MapTool.addLocalMessage(line);
			break;
		case SELF_AND_GM:
			MapTool.addMessage(new TextMessage(TextMessage.Channel.ME, null, MapTool.getPlayer().getName(), I18N.getText("togm.self", line), null));
			// Intentionally falls through
		case GM:
			MapTool.addMessage(new TextMessage(TextMessage.Channel.GM, null, MapTool.getPlayer().getName(), I18N.getText("togm.saysToGM", MapTool.getPlayer().getName()) + " " + line, null));
			break;
		case ALL:
			doSay(line, token, false, "");
			break;
		case LIST:
			StringBuilder sb = new StringBuilder();
			for (String name : playerList) {
				doWhisper(line, token, name);
				if (sb.length() > 0) {
					sb.append(", ");
				}
				sb.append(name);
			}
			MapTool.addMessage(new TextMessage(TextMessage.Channel.ME, null, MapTool.getPlayer().getName(), "<span class='whisper' style='color:blue'>"
					+ I18N.getText("whisper.you.string", sb.toString(), line) + "</span>", null));

			break;
		case NONE:
			// Do nothing with the output.
			break;
		}

	}

	private void doWhisper(String message, Token token, String playerName) {

		ObservableList<Player> playerList = MapTool.getPlayerList();
		List<String> players = new ArrayList<String>();
		for (int count = 0; count < playerList.size(); count++) {
			Player p = playerList.get(count);
			String thePlayer = p.getName();
			players.add(thePlayer);
		}
		String playerNameMatch = StringUtil.findMatch(playerName, players);
		playerName = (!playerNameMatch.equals("")) ? playerNameMatch : playerName;

		// Validate
		if (!MapTool.isPlayerConnected(playerName)) {
			MapTool.addMessage(new TextMessage(TextMessage.Channel.ME, null, MapTool.getPlayer().getName(), I18N.getText("msg.error.playerNotConnected", playerName), null));
		}
		if (MapTool.getPlayer().getName().equalsIgnoreCase(playerName)) {
			return;
		}

		// Send
		MapTool.addMessage(new TextMessage(TextMessage.Channel.WHISPER, playerName, MapTool.getPlayer().getName(), "<span class='whisper' style='color:blue'>"
				+ "<span class='whisper' style='color:blue'>" + I18N.getText("whisper.string", MapTool.getFrame().getCommandPanel().getIdentity(), message) + "</span>", null));

	}

	private List<String> getGMs() {
		List<String> gms = new ArrayList<String>();

		Iterator<Player> pliter = MapTool.getPlayerList().iterator();
		while (pliter.hasNext()) {
			Player plr = pliter.next();
			if (plr.isGM()) {
				gms.add(plr.getName());
			}
		}
		return gms;
	}

	private String getSelf() {
		return MapTool.getPlayer().getName();
	}

	/**
	 * Runs the macro specified by the link if it is auto executable otherwise does nothing..
	 * 
	 * @param link
	 *            the link to the macro.
	 */
	public void processMacroLink(String link) {
		if (isAutoExecLink(link)) {
			runMacroLink(link);
		}
	}

	/**
	 * Runs the macro specified by the link if it is auto executable otherwise does nothing..
	 * 
	 * @param link
	 *            the link to the macro.
	 */
	private boolean isAutoExecLink(String link) {
		Matcher m = Pattern.compile("([^:]*)://([^/]*)/([^/]*)/([^?]*)(?:\\?(.*))?").matcher(link);

		if (m.matches()) {
			if (m.group(1).equalsIgnoreCase("macro")) {
				String command = m.group(2);
				try {
					String[] parts = command.split("@");
					if (parts.length > 1) {
						Token token = MapTool.getParser().getTokenMacroLib(parts[1]);
						if (token == null) {
							return false;
						}
						MacroButtonProperties mbp = token.getMacro(parts[0], false);
						if (mbp == null) {
							return false;
						}
						if (mbp.getAutoExecute()) {
							// Next make sure that it is trusted
							boolean trusted = true;

							// If the token is not owned by everyone and all
							// owners are GMs then we are in
							// a secure context as players can not modify the
							// macro so GM can specify what
							// ever they want.
							if (token != null) {
								if (token.isOwnedByAll()) {
									trusted = false;
								} else {
									Set<String> gmPlayers = new HashSet<String>();
									for (Object o : MapTool.getPlayerList()) {
										Player p = (Player) o;
										if (p.isGM()) {
											gmPlayers.add(p.getName());
										}
									}
									for (String owner : token.getOwners()) {
										if (!gmPlayers.contains(owner)) {
											trusted = false;
											break;
										}
									}
								}
							}
							return trusted;
						}
					}
				} catch (ParserException e) {
					// TODO Should log this...
				}
			}
		}
		return false;
	}

	private void doSay(String msg, Token token, boolean trusted, String macroName) {
		StringBuilder sb = new StringBuilder();

		String identity = token == null ? MapTool.getPlayer().getName() : token.getName();

		sb.append("<table cellpadding=0><tr>");

		if (token != null && AppPreferences.getShowAvatarInChat()) {
			if (token != null) {
				MD5Key imageId = token.getPortraitImage();
				if (imageId == null) {
					imageId = token.getImageAssetId();
				}
				sb.append("<td valign=top width=40 style=\"padding-right:5px\"><img src=\"asset://").append(imageId).append("-40\" ></td>");
			}
		}

		sb.append("<td valign=top style=\"margin-right: 5px\">");
		if (trusted && !MapTool.getPlayer().isGM()) {
			sb.append("<span style='background-color: #C9F7AD' ").append("title='").append(macroName).append("'>");
		}
		sb.append(identity).append(": ");
		if (trusted && !MapTool.getPlayer().isGM()) {
			sb.append("</span>");
		}

		sb.append("</td><td valign=top>");

		Color color = MapTool.getFrame().getCommandPanel().getTextColorWell().getColor();
		if (color != null) {
			sb.append("<span style='color:#").append(String.format("%06X", (color.getRGB() & 0xFFFFFF))).append("'>");
		}
		sb.append(msg);
		if (color != null) {
			sb.append("</span>");

			sb.append("</td>");

			sb.append("</tr></table>");

			MacroContext context = new MacroContext();
			context.addTransform(msg);

			MapTool.addMessage(TextMessage.say(context.getTransformationHistory(), sb.toString()));
		}
	}
}
