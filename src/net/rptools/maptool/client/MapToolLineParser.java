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
package net.rptools.maptool.client;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.rptools.common.expression.ExpressionParser;
import net.rptools.common.expression.Result;
import net.rptools.maptool.client.functions.AbortFunction;
import net.rptools.maptool.client.functions.AddAllToInitiativeFunction;
import net.rptools.maptool.client.functions.CurrentInitiativeFunction;
import net.rptools.maptool.client.functions.InitiativeRoundFunction;
import net.rptools.maptool.client.functions.InputFunction;
import net.rptools.maptool.client.functions.MiscInitiativeFunction;
import net.rptools.maptool.client.functions.RemoveAllFromInitiativeFunction;
import net.rptools.maptool.client.functions.StrListFunctions;
import net.rptools.maptool.client.functions.StrPropFunctions;
import net.rptools.maptool.client.functions.TokenAddToInitiativeFunction;
import net.rptools.maptool.client.functions.TokenBarFunction;
import net.rptools.maptool.client.functions.TokenGMNameFunction;
import net.rptools.maptool.client.functions.TokenHaloFunction;
import net.rptools.maptool.client.functions.TokenInitFunction;
import net.rptools.maptool.client.functions.TokenInitHoldFunction;
import net.rptools.maptool.client.functions.TokenLabelFunction;
import net.rptools.maptool.client.functions.LookupTableFunction;
import net.rptools.maptool.client.functions.TokenNameFunction;
import net.rptools.maptool.client.functions.StateImageFunction;
import net.rptools.maptool.client.functions.TokenImage;
import net.rptools.maptool.client.functions.TokenRemoveFromInitiativeFunction;
import net.rptools.maptool.client.functions.TokenStateFunction;
import net.rptools.maptool.client.functions.TokenVisibleFunction;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.MacroButtonProperties;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;
import net.rptools.parser.ParserException;
import net.rptools.parser.VariableResolver;
import net.rptools.parser.function.Function;

public class MapToolLineParser {

	/** MapTool functions to add to the parser.  */
	private static final Function[] mapToolParserFunctions = {
		StateImageFunction.getInstance(),
		LookupTableFunction.getInstance(),
		TokenImage.getInstance(),
		AddAllToInitiativeFunction.getInstance(),
		MiscInitiativeFunction.getInstance(),
		RemoveAllFromInitiativeFunction.getInstance(),
		CurrentInitiativeFunction.getInstance(),
		InitiativeRoundFunction.getInstance(),
		InputFunction.getInstance(),
		StrPropFunctions.getInstance(),
		StrListFunctions.getInstance(),
		AbortFunction.getInstance(),
	};

	/** MapTool functions to add to the parser when a token is in context. */
	private static final Function[] mapToolContextParserFunctions = {
		TokenGMNameFunction.getInstance(),
		TokenHaloFunction.getInstance(),
		TokenLabelFunction.getInstance(),
		TokenNameFunction.getInstance(),
		TokenStateFunction.getInstance(),
		TokenVisibleFunction.getInstance(),
		TokenInitFunction.getInstance(),
		TokenInitHoldFunction.getInstance(),
		TokenAddToInitiativeFunction.getInstance(),
		TokenRemoveFromInitiativeFunction.getInstance(),
		TokenBarFunction.getInstance(),
	};

	private static final int PARSER_MAX_RECURSE = 50;
	private int parserRecurseDepth;

	private static final int MACRO_MAX_RECURSE = 50;	// Max number of recursive macro calls
	private int macroRecurseDepth = 0;

	private static final int MAX_LOOPS = 500;			// Max number of loop iterations

	private enum Output {		// Mutually exclusive output formats
		NONE,
		RESULT,
		TOOLTIP,
		EXPANDED,
		UNFORMATTED,
	}

	private enum LoopType {		// Mutually exclusive looping options
		NO_LOOP,
		COUNT,
		FOR,
		WHILE,
		FOREACH,
	}

	private enum BranchType {	// Mutually exclusive branching options
		NO_BRANCH,
		IF,
		SWITCH,
	}

	private enum CodeType {		// Mutually exclusive code-execution options
		NO_CODE,
		MACRO,
		CODEBLOCK,
	}


	/*****************************************************************************
	 * OptionType - defines roll options, including values for default parameters.
	 *****************************************************************************/
	// These items are only used in the enum below, but have to be declared out here
	// because they must appear before being used in the enum definitions.
	private static final String defaultLoopSep = "\", \"";
	private static final Object nullParam = (Object)null;

	/*
	 * In order to add a new roll option, follow the instructions in the "todo" comments in this file.
	 */
	private enum OptionType {
		/*
		 * TODO: If you're adding a new option, make an entry in this table
		 */

		// The format is:
		//   NAME   (nameRegex, minParams, maxParams, defaultValues...)
		//
		// You must provide (maxParams - minParams) default values (BigDecimal or String types).
		NO_OPTION   ("",              0, 0),
		// output formats
		EXPANDED    ("e|expanded",    0, 0),
		HIDDEN      ("h|hidden|hide", 0, 0),
		RESULT      ("r|result",      0, 0),
		UNFORMATTED ("u|unformatted", 0, 0),
		TOOLTIP     ("t|tooltip",     0, 1, nullParam),
		// loops
		COUNT       ("c|count",       1, 2, defaultLoopSep),
		FOR         ("for",           3, 5, BigDecimal.ONE, defaultLoopSep),
		FOREACH     ("foreach",       2, 4, defaultLoopSep, ","),
		WHILE       ("while",         1, 2, defaultLoopSep),
		// branches
		IF          ("if",            1, 1),
		SWITCH      ("switch",        1, 1),
		// code
		CODE        ("code",          0, 0),
		MACRO       ("macro",         1, 1);

		protected final String nameRegex;
		protected final int minParams, maxParams;
		protected final Object[] defaultParams;

		OptionType(String nameRegex, int minParams, int maxParams, Object... defaultParams) {
			this.nameRegex = nameRegex;
			this.minParams = minParams;
			this.maxParams = maxParams;
			if (defaultParams == null) {
				// The Java 5 varargs facility has a small hack which we must work around.
				// If you pass a single null argument, Java doesn't know whether you wanted a single variable arg of null,
				// or if you meant to say that the variable argument array itself should be null.
				// Java chooses the latter, but we want the former.
				this.defaultParams = new Object[1];
				this.defaultParams[0] = null;
			} else {
				this.defaultParams = defaultParams;
			}
			if (this.defaultParams.length != (maxParams - minParams)) {
				System.out.println(String.format("Internal error: roll option %s specifies wrong number of default parameters", name()));
			}
		}

		/** Obtain one of the enum values, or null if <code>strName</code> doesn't match any of them. */
		protected static OptionType optionTypeFromName(String strName) {
			for (OptionType rot : OptionType.values()) {
				if (Pattern.compile("^\\s*" + rot.getNameRegex() + "\\s*$", Pattern.CASE_INSENSITIVE).matcher(strName).matches()) {
					return rot;
				}
			}
			return null;
		}

		/** Returns the regex that matches all valid names for this option. */
		public String getNameRegex() { return nameRegex; }
		public int getMinParams() { return minParams; }
		public int getMaxParams() { return maxParams; }
		
		/** Returns a copy of the default params array for this option type. */
		public Object[] getDefaultParams() {
			Object[] retval = new Object[maxParams];
			for (int i=minParams; i<maxParams; i++) {
				retval[i] = defaultParams[i-minParams];
			}
			return retval;
		}

		public String toString() {
			String retval = name() + ", default params: [";
			boolean first = true;
			for (Object p : defaultParams) {
				if (first) {
					first = false;
				} else {
					retval += ", ";
				}
				if (p==null)
					retval += "null";
				else if (p instanceof String)
					retval += "\"" + p + "\"";
				else
					retval += p.toString();
			}
			retval += "]";
			return retval;
		}
	}


	/**********************************************************************************
	 * OptionInfo class - holds extracted name and parameters for a roll option.
	 **********************************************************************************/
	private class OptionInfo {
		private OptionType optionType;
		private String optionName;
		private int optionStart, optionEnd;
		private String srcString;
		private Object[] params;

		/** Attempts to create an OptionInfo object by parsing the text in <code>optionString</code>
		 *  beginning at position <code>start</code>. */
		public OptionInfo(String optionString, int start) throws RollOptionException {
			srcString = optionString;
			optionStart = start;
			parseOptionString(optionString, start);
		}

		/** Parses a roll option and sets the RollOptionType and parameters.
		 *  <br>Missing optional parameters are set to the default for the type.
		 *  @param optionString The string containing the option
		 *  @param start Where in the string to begin parsing from 
		 *  @throws RollOptionException if the option string can't be parsed.*/
		private void parseOptionString(String optionString, int start) throws RollOptionException {
			boolean paramsFound;	// does the option string have a "(" after the name?
			int endOfString = optionString.length();

			// Find the name
			Pattern pattern = Pattern.compile("^\\s*(?:(\\w+)\\s*\\(|(\\w+))");	// matches "abcd(" or "abcd"
			Matcher matcher = pattern.matcher(optionString);
			matcher.region(start, endOfString);
			if (! matcher.find()) {
				throw new RollOptionException("Bad roll option");
			}
			paramsFound = (matcher.group(1) != null);
			String name = paramsFound ? matcher.group(1).trim() : matcher.group(2).trim();
			start = matcher.end();
			matcher.region(start, endOfString);

			// Get the option type and default params from the name
			optionType = OptionType.optionTypeFromName(name);
			if (optionType == null) {
				throw new RollOptionException(String.format("Unknown option name \"%s\"", name));
			}
			optionName = name;
			params = optionType.getDefaultParams();	// begin with default values for optional params

			// If no params found (i.e. no "(" after option name), we're done
			if (!paramsFound) {
				if (optionType.getMinParams() == 0) {
					optionEnd = start;
					return;
				} else {
					throw new RollOptionException(String.format("Roll option \"%s\" requires a list of parameters in parentheses", 
							optionName, optionType.getMinParams()));
				}
			}

			// Otherwise, match the individual parameters one at a time
			pattern = Pattern.compile( "^(?:((?:[^()\",]|\"[^\"]*\"|\\((?:[^()\"]|\"[^\"]*\")*\\))+)(,|\\))){1}?" );
			matcher = pattern.matcher(optionString);
			matcher.region(start, endOfString);
			List<String> paramList = new ArrayList<String>();
			boolean lastItem = false;	// true if last match ended in ")"

			while (!lastItem) {
				if (matcher.find()) {
					String param = matcher.group(1).trim();
					paramList.add(param);
					lastItem = matcher.group(2).equalsIgnoreCase(")");
					start = matcher.end();
					matcher.region(start, endOfString);
				} else {
					throw new RollOptionException(String.format("Roll option %s: bad option parameters %s", optionName, srcString));
				}
			}

			// Error checking
			int min = optionType.getMinParams(), max = optionType.getMaxParams();
			int numParamsFound = paramList.size();
			if (numParamsFound < min || numParamsFound > max) {
				throw new RollOptionException(String.format("Roll option %s must have %d to %d parameters; found %d: %s", 
						optionName, min, max, numParamsFound, srcString));
			}

			// Fill in the found parameters, converting to BigDecimal if possible.
			for (int i=0; i<numParamsFound; i++) {
				params[i] = toNumIfPossible(paramList.get(i));
			}

			optionEnd = start;
			return;
		}

		/** Converts a String to a BigDecimal if possible, otherwise returns original String. */
		private Object toNumIfPossible(String s) {
			Object retval = s;
			try {
				retval = new BigDecimal(Integer.decode(s));
			} catch (NumberFormatException nfe) {
				// Do nothing
			}
			return retval;
		}

		public String getName() { return optionName; }
		public int getStart() { return optionStart; }
		public int getEnd() { return optionEnd; }

		/** Gets a parameter (Object type). */
		public Object getObjectParam(int index) {
			return params[index];
		}
		/** Gets the text of a parameter. */
		public String getStringParam(int index) {
			Object o = params[index];
			return (o == null) ? null : o.toString();
		}
		/** Gets the text of a parameter if it is a valid identifier. 
		 * @throws ParserException if the parameter text is not a valid identifier.*/
		public String getIdentifierParam(int index) throws ParserException {
			String s = params[index].toString();
			if (!s.matches("[a-zA-Z]\\w*")) {	// MapTool doesn't allow variable names to start with '_'
				throw new ParserException(String.format("\"%s\" is not a valid variable name", s));
			}
			return s;
		}
		/** Gets a parameter, casting it to BigDecimal. */
		public BigDecimal getNumericParam(int index) {
			return (BigDecimal)params[index];
		}
		/** Gets the integer value of a parameter. */
		public int getIntParam(int index) {
			return getNumericParam(index).intValue();
		}

		/** Returns a param, parsing it as an expression if it is a string. */
		public Object getParsedParam(int index, MapToolVariableResolver res, Token tokenInContext) 
		throws ParserException {
			Object retval = params[index];
			// No parsing is done if the param isn't a String (e.g. it's already a BigDecimal)
			if (params[index] instanceof String) {
				Result result = parseExpression(res, tokenInContext, (String)params[index]);
				retval = result.getValue();
			}
			return retval;
		}

		/** Returns a param as int, parsing it as an expression if it is a string. */
		public int getParsedIntParam(int index, MapToolVariableResolver res, Token tokenInContext)
		throws ParserException {
			Object retval = getParsedParam(index, res, tokenInContext);
			if (! (retval instanceof BigDecimal))
				throw new ParserException(String.format("\"%s\" is not a number.", retval.toString()));
			return ((BigDecimal)retval).intValue();
		}

		public String toString() {
			String retval = optionName + ": params: (";
			boolean first = true;
			for (Object p : params) {
				if (first) {
					first = false;
				} else {
					retval += ", ";
				}
				if (p==null)
					retval += "null";
				else if (p instanceof String)
					retval += "\"" + p + "\"";
				else
					retval += p.toString();
			}
			retval += ")";
			return retval;
		}
	} ///////////////////// end of OptionInfo class

	/** Thrown when a roll option can't be parsed. */
	@SuppressWarnings("serial")
	public class RollOptionException extends Exception {
		public String msg;

		public RollOptionException(String msg) {
			this.msg = msg;
		}
	}

	/** Scans a string of options and builds OptionInfo objects for each option found.
	 * @param optionString A string containing a comma-delimited list of roll options.
	 * @throws RollOptionException if any of the options are unknown or don't match the template for that option type.*/
	private List<OptionInfo> getRollOptionList(String optionString) throws  RollOptionException {
		if (optionString == null) return null;

		List<OptionInfo> list = new ArrayList<OptionInfo>();
		optionString = optionString.trim();
		int start = 0;
		int endOfString = optionString.length();
		boolean atEnd = false;
		Pattern commaPattern = Pattern.compile("^\\s*,\\s*(?!$)");

		while (start < endOfString) {
			OptionInfo roi;
			if (atEnd) {
				// If last param didn't end with ",", there shouldn't have been another option
				throw new RollOptionException("Roll option list can't end with a comma");
			}
			// Eat the next option from string, and add parsed option to list
			roi = new OptionInfo(optionString, start);
			list.add(roi);
			start = roi.getEnd();
			// Eat any "," sitting between options
			Matcher matcher = commaPattern.matcher(optionString);
			matcher.region(start, endOfString);
			if (matcher.find()) {
				start = matcher.end();
				atEnd = false;
			} else {
				atEnd = true;
			}
		}

		return list;
	}

	// This is starting to get ridiculous... I sense an incoming rewrite using ANTLR
	private static final Pattern roll_pattern = Pattern.compile(
			"\\[\\s*(?:((?:[^\\]:(]|\\((?:[^()\"]|\"[^\"]*\"|\\((?:[^)\"]|\"[^\"]*\")*\\))*\\))*):\\s*)?((?:\\{(?:[^{}\"]|\"[^\"]*\"|\\{(?:[^}\"]|\"[^\"]*\")*})*}|[^\\]{\"]|\"[^\"]*\")*?)\\s*]|\\{\\s*((?:[^}\"]|\"[^\"]*\")*?)\\s*}"
			);
	//	private static final Pattern opt_pattern = Pattern.compile("(\\w+(?:\\((?:[^()\"]|\"[^\"]*\"|\\((?:[^()\"]|\"[^\"]*\")+\\))+\\))?)\\s*,\\s*");


	public String parseLine(String line) throws ParserException {
		return parseLine(null, line);
	}

	public String parseLine(Token tokenInContext, String line) throws ParserException {
		return parseLine(null, tokenInContext, line);
	}

	public String parseLine(MapToolVariableResolver res, Token tokenInContext, String line) throws ParserException {

		if (line == null) {
			return "";
		}

		line = line.trim();
		if (line.length() == 0) {
			return "";
		}

		// Keep the same context for this line
		MapToolVariableResolver resolver = (res==null) ? new MapToolVariableResolver(tokenInContext) : res;

		StringBuilder builder = new StringBuilder();
		Matcher matcher = roll_pattern.matcher(line);
		int start;

		for (start = 0; matcher.find(start); start = matcher.end()) {
			builder.append(line.substring(start, matcher.start())); // add everything before the roll

			// These variables will hold data extracted from the roll options.
			Output output = Output.TOOLTIP;
			String text = null;	// used by the T option

			LoopType loopType = LoopType.NO_LOOP;
			int loopStart = 0, loopEnd = 0, loopStep = 1;
			int loopCount = 0;
			String loopSep = null;
			String loopVar = null, loopCondition = null;
			List<String> foreachList = new ArrayList<String>();

			BranchType branchType = BranchType.NO_BRANCH;
			Object branchCondition = null;

			CodeType codeType = CodeType.NO_CODE;
			String macroName = null;


			if (matcher.group().startsWith("[")) {
				String opts = matcher.group(1);
				String roll = matcher.group(2);
				if (opts != null) {
					// Turn the opts string into a list of OptionInfo objects.
					List<OptionInfo> optionList = null;
					try {
						optionList = getRollOptionList(opts);
					} catch (RollOptionException roe) {
						doError(roe.msg, opts, roll);
					}

					// Scan the roll options and prepare variables for later use
					for (OptionInfo option : optionList) {
						String error = null;
						/*
						 * TODO: If you're adding a new option, add a new case here to collect info from the parameters.
						 *       If your option uses parameters, use the option.getXxxParam() methods to get
						 *       the text or parsed values of the parameters.
						 */
						switch (option.optionType) {

						///////////////////////////////////////////////////
						// OUTPUT FORMAT OPTIONS
						///////////////////////////////////////////////////
						case HIDDEN:
							output = Output.NONE;
							break;
						case RESULT:
							output = Output.RESULT;
							break;
						case EXPANDED:
							output = Output.EXPANDED;
							break;
						case UNFORMATTED:
							output = Output.UNFORMATTED;
							break;
						case TOOLTIP:
							// T(display_text)
							output = Output.TOOLTIP;
							text = option.getStringParam(0);
							break;
// old code kept for reference:
//							Matcher m = Pattern.compile("t(?:ooltip)?(?:\\(((?:[^()\"]|\"[^\"]*\"|\\((?:[^()\"]|\"[^\"]*\")*\\))+?)\\))?", Pattern.CASE_INSENSITIVE).matcher(opt);
//							if (m.matches()) {
//								output = Output.TOOLTIP;
//
//								text = m.group(1);
//							} else {
//								throw new ParserException(errorString("Invalid option: " + opt, opts, roll));
//							}
//							break;

						///////////////////////////////////////////////////
						// LOOP OPTIONS
						///////////////////////////////////////////////////
						case COUNT:
							// COUNT(num [, sep])
							loopType = LoopType.COUNT;
							error = null;
							try {
								loopCount = option.getParsedIntParam(0, resolver, tokenInContext);
								if (loopCount < 0)
									error = String.format("COUNT option requires a non-negative number (got %d)", loopCount);
							} catch (ParserException pe) {
								error = String.format("Error processing COUNT option: %s", pe.getMessage());
							}
							loopSep = option.getStringParam(1);

							if (error != null) doError(error, opts, roll);
							break;
// old code kept for reference:
//							Matcher m = Pattern.compile("(?:for|c(?:ount)?)\\(((?:[^()\"]|\"[^\"]*\"|\\((?:[^()\"]|\"[^\"]*\")*\\))+?)\\)", Pattern.CASE_INSENSITIVE).matcher(opt);
//							loopType = LoopType.FOR;
//							if (m.matches()) {
//								String args[] = m.group(1).split(",", 2);
//								Result result = parseExpression(resolver, tokenInContext, args[0]);
//								try {
//									count = ((Number)result.getValue()).intValue();
//									if (count < 0)
//										throw new ParserException(errorString("Invalid count: " + String.valueOf(count), opts, roll));
//								} catch (ClassCastException e) {
//									throw new ParserException(errorString("Invalid count: " + result.getValue().toString(), opts, roll));
//								}
//
//								if (args.length > 1) {
//									separator = args[1];
//								}
//							} else {
//								throw new ParserException(errorString("Invalid option: " + opt, opts, roll));
//							}
//							break;

						case FOR:
							// FOR(var, start, end [, step [, sep]])
							loopType = LoopType.FOR;
							error = null;
							try {
								loopVar = option.getIdentifierParam(0);
								loopStart = option.getParsedIntParam(1, resolver, tokenInContext);
								loopEnd = option.getParsedIntParam(2, resolver, tokenInContext);
								try {
									loopStep = option.getParsedIntParam(3, resolver, tokenInContext);
								} catch (ParserException pe) {
									// Build a more informative error message for this common mistake
									String msg = pe.getMessage();
									msg = msg + " To specify a non-default loop separator, " +
											"you must use the format FOR(var,start,end,step,separator)";
									throw new ParserException(msg);
								}
								loopSep = option.getStringParam(4);
								if (loopStep != 0)
									loopCount = (int)Math.floor(Math.abs( (loopEnd - loopStart)/loopStep + 1 ));

								if (loopVar.equalsIgnoreCase(""))
									error = "FOR variable name missing";
								if (loopStep == 0)
									error = "FOR loop step can't be zero";
								if ((loopEnd < loopStart && loopStep > 0) || (loopEnd > loopStart && loopStep < 0))
									error = String.format("FOR loop step size is in the wrong direction (start=%d, end=%d, step=%d)", 
											loopStart, loopEnd, loopStep);
							} catch (ParserException pe) {
								error = String.format("Error processing FOR option: %s", pe.getMessage());
							}
							
							if (error != null) doError(error, opts, roll);
							break;

						case FOREACH:
							// FOREACH(var, list [, outputDelim [, inputDelim]])
							loopType = LoopType.FOREACH;
							error = null;
							try {
								loopVar = option.getIdentifierParam(0);
								String listString = option.getParsedParam(1, resolver, tokenInContext).toString();
								loopSep = option.getStringParam(2);
								String listDelim = option.getStringParam(3);

								foreachList = new ArrayList<String>();
								StrListFunctions.parse(listString, foreachList, listDelim);
								loopCount = foreachList.size();

								if (loopVar.equalsIgnoreCase(""))
									error = "FOREACH variable name missing";
							} catch (ParserException pe) {
								error = String.format("Error processing FOREACH option: %s", pe.getMessage());
							}

							if (error != null) doError(error, opts, roll);
							break;

						case WHILE:
							// WHILE(cond [, sep])
							loopType = LoopType.WHILE;
							loopCondition = option.getStringParam(0);
							loopSep = option.getStringParam(1);
							break;

						///////////////////////////////////////////////////
						// BRANCH OPTIONS
						///////////////////////////////////////////////////
						case IF:
							// IF(condition)
							branchType = BranchType.IF;
							branchCondition = option.getStringParam(0);
							break;
						case SWITCH:
							// SWITCH(condition)
							branchType = BranchType.SWITCH;
							branchCondition = option.getObjectParam(0);
							break;

						///////////////////////////////////////////////////
						// CODE OPTIONS
						///////////////////////////////////////////////////
						case MACRO:
							// MACRO("macroName@location")
							codeType = CodeType.MACRO;
							macroName = option.getStringParam(0);
							break;
						case CODE:
							codeType = CodeType.CODEBLOCK;
							break;

						default:
							// should never happen
							doError("Bad option found", opts, roll);
						}
					}
				}

				// Now that the options have been dealt with, process the body of the roll.
				// We deal with looping first, then branching, then deliver the output.
				StringBuilder expressionBuilder = new StringBuilder();
				int iteration = 0;
				boolean doLoop = true;
				while (doLoop) {
					int loopConditionValue;
					Integer branchConditionValue = null;
					Object branchConditionParsed = null;

					// Process loop settings
					if (iteration > MAX_LOOPS) {
						doError("Too many loop iterations (possible infinite loop?)", opts, roll);
					}
					
					if (loopType != LoopType.NO_LOOP) {
						// We only update roll.count in a loop statement.  This allows simple nested 
						// statements to inherit roll.count from the outer statement.
						resolver.setVariable("roll.count", iteration);
					}

					switch (loopType) {
					/*
					 * TODO: If you're adding a new looping option, add a new case to handle the iteration
					 */
					case NO_LOOP:
						if (iteration > 0) {		// stop after first iteration
							doLoop = false;
						}
						break;
					case COUNT:
						if (iteration == loopCount) {
							doLoop = false;
						}
						break;
					case FOR:
						if (iteration != loopCount) {
							resolver.setVariable(loopVar, new BigDecimal(loopStart + loopStep * iteration));
						} else {
							doLoop = false;
							resolver.setVariable(loopVar, null);
						}
						break;
					case FOREACH:
						if (iteration != loopCount) {
							String item = foreachList.get(iteration);
							resolver.setVariable(loopVar, item);
						} else {
							doLoop = false;
							resolver.setVariable(loopVar, null);
						}
						break;
					case WHILE:
						// This is a hack to get around a bug with the parser's comparison operators.
						// The InlineTreeFormatter class in the parser chokes on comparison operators, because they're
						// not listed in the operator precedence table.
						//
						// The workaround is that "non-deterministic" functions fully evaluate their arguments,
						// so the comparison operators are reduced to a number by the time the buggy code is reached.
						// The if() function defined in dicelib is such a function, so we use it here to eat
						// any comparison operators.
						String hackCondition = (loopCondition == null) ? null : String.format("if(%s, 1, 0)", loopCondition);
						// Stop loop if the while condition is false
						try {
							Result result = parseExpression(resolver, tokenInContext, hackCondition);
							loopConditionValue = ((Number)result.getValue()).intValue();
							if (loopConditionValue == 0) {
								doLoop = false;
							}
						} catch (Exception e) {
							doError(String.format("Invalid condition in WHILE(%s) roll option", loopCondition), opts, roll);
						}
						break;
					}

					// Output the loop separator
					if (doLoop && iteration != 0 && output != Output.NONE) {
						expressionBuilder.append(parseExpression(resolver, tokenInContext, loopSep).getValue());
					}

					if (!doLoop) {
						break;
					}

					iteration++;

					// Extract the appropriate branch to evaluate.

					// Evaluate the branch condition/expression
					if (branchCondition != null) {
						// This is a similar hack to the one used for the loopCondition above.
						String hackCondition = (branchCondition == null) ? null : branchCondition.toString();
						if (branchType == BranchType.IF) {
							hackCondition = (hackCondition == null) ? null : String.format("if(%s, 1, 0)", hackCondition);
						}
						Result result = null;
						try {
							result = parseExpression(resolver, tokenInContext, hackCondition);
						} catch (Exception e) {
							doError(String.format("Invalid condition in %s(%s) roll option", branchType.toString(), 
									branchCondition.toString()), opts, roll);
						}
						branchConditionParsed = result.getValue();
						if (branchConditionParsed instanceof Number) {
							branchConditionValue = ((Number)branchConditionParsed).intValue();
						}
					}

					// Set up regexes for scanning through the branches.
					// branchRegex then defines one matcher group for the parseable content of the branch.
					String rollBranch = roll;
					String branchRegex, branchSepRegex, branchLastSepRegex;
					if (codeType != CodeType.CODEBLOCK) {
						// matches any text not containing a ";" (skipping over strings) 
						String noCodeRegex = "((?:[^\";]|\"[^\"]*\"|'[^']*')*)";
						branchRegex = noCodeRegex;
						branchSepRegex = ";";
						branchLastSepRegex = ";?";	// The last clause doesn't have to end with a separator
					} else {
						// matches text inside braces "{...}", skipping over strings (one level of {} nesting allowed)
						String codeRegex = "\\{((?:[^{}\"]|\"[^\"]*\"|'[^']*'|\\{(?:[^}\"]|\"[^\"]*\"|'[^']*')*})*)}";
						branchRegex = codeRegex;
						branchSepRegex = ";";
						branchLastSepRegex = ";?";	// The last clause doesn't have to end with a separator
					}

					// Extract the branch to use
					switch (branchType) {
					/*
					 * TODO: If you're adding a new branching option, add a new case to extract the branch text
					 */
					case NO_BRANCH:
					{
						// There's only one branch, so our regex is very simple
						String testRegex = String.format("^\\s*%s\\s*$", branchRegex);
						Matcher testMatcher = Pattern.compile(testRegex).matcher(roll);
						if (testMatcher.find()) {
							rollBranch = testMatcher.group(1);
						} else {
							doError("Error in body of roll.", opts, roll);
						}
						break;	
					}
					case IF:
					{
						// IF can have one or two branches.
						// When there's only one branch and the condition is false, there's no output.
						if (branchConditionValue == null) {
							doError("Invalid IF condition: " + branchCondition 
									+ ", evaluates to: " + branchConditionParsed.toString(), opts, roll);
						}
						int whichBranch = (branchConditionValue != 0) ? 0 : 1;
						String testRegex = String.format("^\\s*%s\\s*(?:%s\\s*%s\\s*%s)?\\s*$", 
								branchRegex, branchSepRegex, branchRegex, branchLastSepRegex);
						Matcher testMatcher = Pattern.compile(testRegex).matcher(roll);
						if (testMatcher.find()) {	// verifies that roll body is well-formed
							rollBranch = testMatcher.group(1+whichBranch);
							if (rollBranch == null) rollBranch = "''";	// quick-and-dirty way to get no output
							rollBranch = rollBranch.trim();
						} else {
							doError("Error in roll for IF option", opts, roll);
						}
						break;
					}
					case SWITCH:
					{
						// We augment the branch regex to detect the "case xxx:" or "default:" prefixes,
						// and search for a match.  An error is thrown if no case match is found.
						
						// Regex matches 'default', 'case 123:', 'case "123":', 'case "abc":', but not 'case abc:'
						branchRegex = "(?:case\\s*\"?((?<!\")(?:\\+|-)?[\\d]+(?!\")|(?<=\")[^\"]*(?=\"))\"?|(default))\\s*:\\s*" + branchRegex;
						String caseTarget = branchConditionParsed.toString();
						String testRegex = String.format("^(?:\\s*%s\\s*%s\\s*)*\\s*%s\\s*%s\\s*$", 
								branchRegex, branchSepRegex, branchRegex, branchLastSepRegex);
						Matcher testMatcher = Pattern.compile(testRegex).matcher(roll);
						if (testMatcher.find()) {	// verifies that roll body is well-formed
							String scanRegex = String.format("\\s*%s\\s*(?:%s)?", branchRegex, branchSepRegex);
							Matcher scanMatcher = Pattern.compile(scanRegex).matcher(roll);
							boolean foundMatch = false;
							while (!foundMatch && scanMatcher.find()) {
								String caseLabel = scanMatcher.group(1);	// "case (xxx):"
								String def = scanMatcher.group(2);		// "(default):"
								String branch = scanMatcher.group(3);
								if (def != null) {
									rollBranch = branch.trim();
									foundMatch = true;;
								}
								if (caseLabel != null && caseLabel.matches(caseTarget)) {
									rollBranch = branch.trim();
									foundMatch = true;
								}
							}
							if (!foundMatch) {
								doError("SWITCH option found no match for " + caseTarget, opts, roll);
							}
						} else {
							doError("Error in roll for SWITCH option", opts, roll);
						}

						break;
					}
					} // end of switch(branchType) statement

					// Construct the output.  
					// If a MACRO or CODE block is being used, we default to bare output as in the RESULT style.
					// The output style NONE is also allowed in these cases.
					Result result;
					String output_text;
					switch(codeType) {
					case NO_CODE:
						// If none of the code options are active, any of the formatting options can be used.
						switch (output) {
						/*
						 * TODO: If you're adding a new formatting option, add a new case to build the output
						 */
						case NONE:
							parseExpression(resolver, tokenInContext, rollBranch);
							break;
						case RESULT:
							result = parseExpression(resolver, tokenInContext, rollBranch);
							expressionBuilder.append(result != null ? result.getValue().toString() : "");
							break;
						case TOOLTIP:
							String tooltip = rollBranch + " = ";
							output_text = null;
							result = parseExpression(resolver, tokenInContext, rollBranch);
							tooltip += result.getDetailExpression();
							if (text == null) {
								output_text = result.getValue().toString();
							} else {
								if (!result.getDetailExpression().equals(result.getValue().toString())) {
									tooltip += " = " + result.getValue();
								}
								resolver.setVariable("roll.result", result.getValue());
								output_text = parseExpression(resolver, tokenInContext, text).getValue().toString();
							}
							tooltip = tooltip.replaceAll("'", "&#39;");
							expressionBuilder.append(output_text != null ? "\036" + tooltip + "\037" + output_text + "\036" : "");
							break;
						case EXPANDED:
							expressionBuilder.append("\036" + rollBranch + " = " + expandRoll(resolver, tokenInContext, rollBranch) + "\036");
							break;
						case UNFORMATTED:
							output_text = rollBranch + " = " + expandRoll(resolver, tokenInContext, rollBranch);

							// Escape quotes so that the result can be used in a title attribute
							output_text = output_text.replaceAll("'", "&#39;");
							output_text = output_text.replaceAll("\"", "&#34;");

							expressionBuilder.append("\036\01u\02" + output_text + "\036");
						}	// end of switch(output) statement
						break;	// end of case NO_CODE in switch(codeType) statement
					/*
					 * TODO: If you're adding a new code option, add a new case to execute the code
					 */
					case MACRO:
						// [MACRO("macroName@location"): args]
						result = parseExpression(resolver, tokenInContext, macroName);
						String callName = result.getValue().toString();
						result = parseExpression(resolver, tokenInContext, rollBranch);
						String macroArgs = result.getValue().toString();
						output_text = runMacro(resolver, tokenInContext, callName, macroArgs);
						if (output != Output.NONE) {
							expressionBuilder.append(output_text);
						}
						resolver.setVariable("roll.count", iteration);	// reset this because called code might change it
						break;

					case CODEBLOCK:
						output_text = runMacroBlock(resolver, tokenInContext, rollBranch);
						resolver.setVariable("roll.count", iteration);	// reset this because called code might change it
						if (output != Output.NONE) {
							expressionBuilder.append(output_text);
						}
						break;
					}
				}
				builder.append(expressionBuilder);
			} else if (matcher.group().startsWith("{")) {
				String roll = matcher.group(3);
				Result result = parseExpression(resolver, tokenInContext, roll);
				builder.append(result != null ? result.getValue().toString() : "");
			}
		}

		builder.append(line.substring(start));

		return builder.toString();
	}

	
	
	
	public Result parseExpression(String expression) throws ParserException {
		return parseExpression(null, expression);
	}

	public Result parseExpression(Token tokenInContext, String expression) throws ParserException {

		return parseExpression(new MapToolVariableResolver(tokenInContext), tokenInContext, expression);
	}
	public Result parseExpression(VariableResolver resolver, Token tokenInContext, String expression) throws ParserException {

		if (parserRecurseDepth > PARSER_MAX_RECURSE) {
			throw new ParserException("Max recurse limit reached");
		}
		try {
			parserRecurseDepth ++;
			return  createParser(resolver, tokenInContext == null ? false : true).evaluate(expression);
		} catch (RuntimeException re) {

			if (re.getCause() instanceof ParserException) {
				throw (ParserException) re.getCause();
			}

			throw re;
		} finally {
			parserRecurseDepth--;
		}
	}	

	public String expandRoll(String roll) {
		return expandRoll(null, roll);
	}

	public String expandRoll(Token tokenInContext, String roll) {
		return expandRoll(new MapToolVariableResolver(tokenInContext), tokenInContext, roll);
	}

	public String expandRoll(MapToolVariableResolver resolver, Token tokenInContext, String roll) {

		try {
			Result result = parseExpression(resolver, tokenInContext, roll);

			StringBuilder sb = new StringBuilder();

			if (result.getDetailExpression().equals(result.getValue().toString())) {
				sb.append(result.getDetailExpression());
			} else {
				sb.append(result.getDetailExpression()).append(" = ").append(result.getValue());
			}

			return sb.toString();
		} catch (ParserException e) {
			return "Invalid expression: " + roll;
		}

	}    

	/** Runs a macro from a specified location. */
	public String runMacro(MapToolVariableResolver resolver, Token tokenInContext, String qMacroName, String args) 
	throws ParserException {
		String macroBody = null;

		String[] macroParts = qMacroName.split("@",2);
		String macroLocation;

		String macroName = macroParts[0];
		if (macroParts.length == 1) {
			macroLocation = null;
		} else {
			macroLocation = macroParts[1];
		}

		// For convenience to macro authors, no error on a blank macro name
		if (macroName.equalsIgnoreCase(""))
			return "";

		if (macroLocation == null) {
			// Unqualified names are not allowed.
			throw new ParserException(String.format("Must specify a location for the macro \"%s\" to be run.",macroName));
		} else if (macroLocation.equalsIgnoreCase("TOKEN")) {
			// Search token for the macro
			if (tokenInContext != null) {
				MacroButtonProperties buttonProps = tokenInContext.getMacro(macroName, true);
				
				macroBody = buttonProps.getCommand(); 
			}

// These choices are disabled because Lindharin's upcoming macro panel revamp will require
// a rewrite of how we access campaign and global macros.
//
		} else if (macroLocation.equalsIgnoreCase("CAMPAIGN")) {
			throw new ParserException("Calling campaign macros is not currently supported.");
//			CampaignPanel cp = MapTool.getFrame().getCampaignPanel();
//			Component[] comps = cp.getComponents();
//			for (Component comp : comps) {
//				if (comp instanceof CampaignMacroButton) {
//					CampaignMacroButton cmb = (CampaignMacroButton)comp;
//					String lbl = cmb.getMacroLabel();
//					if (lbl.equalsIgnoreCase(macroName)) {
//						macroBody = cmb.getCommand();
//						break;
//					}
//				}
//			}			
		} else if (macroLocation.equalsIgnoreCase("GLOBAL")) {
			throw new ParserException("Calling global macros is not currently supported.");
//			GlobalPanel gp = MapTool.getFrame().getGlobalPanel();
//			Component[] comps = gp.getComponents();
//			for (Component comp : comps) {
//				if (comp instanceof GlobalMacroButton) {
//					GlobalMacroButton gmb = (GlobalMacroButton)comp;
//					String lbl = gmb.getMacroLabel();
//					if (lbl.equalsIgnoreCase(macroName)) {
//						macroBody = gmb.getCommand();
//						break;
//					}
//				}
//			}
		} else { // Search for a token called macroLocation (must start with "Lib:")
			macroBody = getTokenLibMacro(macroName, macroLocation);
		}

		// Error if macro not found
		if (macroBody == null) {
			throw new ParserException(String.format("Unknown macro \"%s\"", macroName));
		}

		MapToolVariableResolver macroResolver = new MapToolVariableResolver(tokenInContext);
		macroResolver.setVariable("macro.args", args);
		macroResolver.setVariable("macro.return", "");

		// Call the macro
		macroRecurseDepth++;
		if (macroRecurseDepth > MACRO_MAX_RECURSE) {
			throw new ParserException("Max macro recurse depth reached");
		}
		try {
			String macroOutput = runMacroBlock(macroResolver, tokenInContext, macroBody);
			// Copy the return value of the macro into our current variable scope.
			resolver.setVariable("macro.return", macroResolver.getVariable("macro.return"));
			return macroOutput;
		} finally {
			macroRecurseDepth--;
		}
	}


	/** Executes a string as a block of macro code. */
	String runMacroBlock(MapToolVariableResolver resolver, Token tokenInContext, String macroBody) throws ParserException {
		String macroOutput = parseLine(resolver, tokenInContext, macroBody);
		return macroOutput;
	}

	
	/** Searches all maps for a token and returns the body of the requested macro.
	 * 
	 * @param macro The name of the macro to fetch.
	 * @param location The name of the token containing the macro.  Must begin with "lib:".
	 * @return The body of the requested macro.
	 * @throws ParserException if the token name is illegal, the token appears multiple times,
	 * or if the caller doesn't have access to the token.
	 */
	public String getTokenLibMacro(String macro, String location) throws ParserException {
		if (!location.matches("(?i)^lib:.*")) {
			throw new ParserException("Macros from other tokens are only available if the token name starts with \"Lib:\"");
		}
		//final String libTokenName = "Lib:" + location;
		final String libTokenName = location;
		Token libToken = null;
		String macroBody = null;
		if (libTokenName != null && libTokenName.length() > 0) {
			List<ZoneRenderer> zrenderers = MapTool.getFrame().getZoneRenderers();
			for (ZoneRenderer zr : zrenderers) {
				List<Token> tokenList = zr.getZone().getTokensFiltered(new Zone.Filter() {
					public boolean matchToken(Token t) {
						return t.getName().equalsIgnoreCase(libTokenName);
					}});

				for (Token token : tokenList) {
					// If we are not the GM and the token is not visible to players then we don't
					// let them get functions from it.
					if (!MapTool.getPlayer().isGM() && !token.isVisible()) {
						throw new ParserException("Unable to execute macro from  " + libTokenName);
					}
					if (libToken != null) {
						throw new ParserException("Duplicate " + libTokenName + " tokens");
					}

					libToken = token;
					MacroButtonProperties buttonProps = token.getMacro(macro, true);
					macroBody = buttonProps.getCommand();
				}
			}
			return macroBody;
		} 
		return null;
	}

	/** Throws a helpful ParserException that shows the roll options and body.
	 * @param msg The message
	 * @param opts The roll options
	 * @param roll The roll body */
	void doError(String msg, String opts, String roll) throws ParserException {
		throw new ParserException(errorString(msg, opts, roll));
	}

	/** Builds a formatted string showing the roll options and roll body. */
	String errorString(String msg, String opts, String roll) {
		String retval = "<br>&nbsp;&nbsp;&nbsp;" + msg;
		retval += "<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<u>Statement options (if any)</u>: " + opts;
		if (roll.length() <= 200) {
			retval += "<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<u>Statement body</u>: " + roll;
		} else {
			retval += "<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<u>Statement body (first 200 characters)</u>: " + roll.substring(0,199);
		}
		return retval;
	}

	private ExpressionParser createParser(VariableResolver resolver, boolean hasTokenInContext) {
		ExpressionParser parser = new ExpressionParser(resolver);
		parser.getParser().addFunctions(mapToolParserFunctions);
		if (hasTokenInContext) {
			parser.getParser().addFunctions(mapToolContextParserFunctions);
		}
		return parser;
	}
}
