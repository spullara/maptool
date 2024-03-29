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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.rptools.maptool.language.I18N;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;
import net.rptools.parser.function.ParameterException;

/**
 * Implements various "property string" (<code>*PropStr()</code>) functions.
 * <br>The <code>properties</code> string is of the form "key1 = value1 ; key2=value2; ...".
 * <br>An optional final argument <code>delim</code> sets the item delimiter
 * @author knizia.fan
 */
public class StrPropFunctions extends AbstractFunction {
	public StrPropFunctions() {
		super(1, UNLIMITED_PARAMETERS, "getStrProp", "setStrProp", "deleteStrProp", "varsFromStrProp", "strPropFromVars",
				"countStrProp", "indexKeyStrProp", "indexValueStrProp", "formatStrProp");
	}

	/** The singleton instance. */
	private final static StrPropFunctions instance = new StrPropFunctions();

	/** Gets the Input instance.
	 * @return the instance. */
	public static StrPropFunctions getInstance() {
		return instance;
	}


	/** Parses a property string.
	 * @param props has the form "key1=val1 ; key2=val2 ; ..."
	 * @param map is populated with the settings.  The keys are normalized to upper case.
	 * @param oldKeys holds the un-normalized keys, in their original order.
	 * @param delim is the setting delimiter to use
	 */
	public static void parse(String props, Map<String,String> map,
			List<String> oldKeys, List<String> oldKeysNormalized, String delim) {
		String delimPatt;
		if (delim.equalsIgnoreCase("")) {
			delimPatt = ";";
		} else {
			delimPatt = fullyQuoteString(delim);		// XXX Why are we not using \\Q...\\E instead?
		}

		// Added "." to allowed key names since variables name scan contain dots.
		String entryPatt = "([\\w.]+\\s*=.*?)" + delimPatt + "|([\\w.]+\\s*=.*)";
		Pattern entryParser = Pattern.compile(entryPatt);
		String keyValuePatt = "([\\w.]+)\\s*=\\s*(.*)";
		Pattern keyValueParser = Pattern.compile(keyValuePatt);

		// Extract the keys and values already in the props string.
		// Save the old keys so we can rebuild the props string in the same order.
		boolean lastEntry = false;
		Matcher entryMatcher = entryParser.matcher(props);
		while (entryMatcher.find()) {
			if (!lastEntry) {
				//	    		String entry = entryMatcher.group();
				String entry = entryMatcher.group(1);
				if (entry==null) {
					entry = entryMatcher.group(2);
					// We're here because there was no trailing delimiter in this match.
					// In this case, the next match will be empty, but we don't want to grab it.
					// (We do grab the final empty match if the string ended with the delimiter.)
					// This flag will prevent that.
					lastEntry = true;
				}
				Matcher keyValueMatcher = keyValueParser.matcher(entry);
				if (keyValueMatcher.find()) {
					String propKey = keyValueMatcher.group(1).trim();
					String propValue = keyValueMatcher.group(2).trim();
					map.put(propKey.toUpperCase(), propValue);
					oldKeys.add(propKey);
				}
			}
		}
		for (String key : oldKeys)
			oldKeysNormalized.add(key.toUpperCase());
	}

	/** Prepares a string for use in regex operations. */
	public static String fullyQuoteString(String s) {
		// We escape each non-alphanumeric character in the delimiter string
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<s.length(); i++) {
			if (!Character.isLetterOrDigit(s.charAt(i))) {
				sb.append("\\");
			}
			sb.append(s.charAt(i));
		}
		return sb.toString();
	}

	@Override
	public Object childEvaluate(Parser parser, String functionName, List<Object> parameters) throws ParserException {
		Object retval = "";
		String props = parameters.get(0).toString();		// contains property settings
		String lastParam = parameters.get(parameters.size()-1).toString();

		// Extract the keys and values already in the props string.
		// Save the old keys so we can rebuild the props string in the same order.
		HashMap<String,String> map = new HashMap<String,String>();
		ArrayList<String> oldKeys = new ArrayList<String>();
		ArrayList<String> oldKeysNormalized = new ArrayList<String>();

		if ("getStrProp".equalsIgnoreCase(functionName))
			retval = getStrProp(parameters, lastParam, props, map, oldKeys, oldKeysNormalized);
		else if ("setStrProp".equalsIgnoreCase(functionName))
			retval = setStrProp(parameters, lastParam, props, map, oldKeys, oldKeysNormalized);
		else if ("deleteStrProp".equalsIgnoreCase(functionName))
			retval = deleteStrProp(parameters, lastParam, props, map, oldKeys, oldKeysNormalized);
		else if ("varsFromStrProp".equalsIgnoreCase(functionName))
			retval = varsFromStrProp(parameters, lastParam, props, map, oldKeys, oldKeysNormalized, parser);
		else if ("strPropFromVars".equalsIgnoreCase(functionName))
			retval = strPropFromVars(parameters, lastParam, props, map, oldKeys, oldKeysNormalized, parser);
		else if ("countStrProp".equalsIgnoreCase(functionName))
			retval = countStrProp(parameters, lastParam, props, map, oldKeys, oldKeysNormalized);
		else if ("indexKeyStrProp".equalsIgnoreCase(functionName))
			retval = indexKeyStrProp(parameters, lastParam, props, map, oldKeys, oldKeysNormalized);
		else if ("indexValueStrProp".equalsIgnoreCase(functionName))
			retval = indexValueStrProp(parameters, lastParam, props, map, oldKeys, oldKeysNormalized);
		else if ("formatStrProp".equalsIgnoreCase(functionName))
			retval = formatStrProp(parameters, lastParam, props, map, oldKeys, oldKeysNormalized);

		return retval;
	}

	/** MapTool code: <code>getStrProp(properties, key [, defaultValue [, delim]])</code>
	 * @param key A string to look up
	 * @param defaultValue An optional default returned when <code>key</code> is not found (default is <code>""</code>
	 * @return The matching value for <code>key</code>, or <code>""</code> if not found.
	 * The value is converted to a number if possible.
	 */
	public Object getStrProp(List<Object> parameters, String lastParam, String props, Map<String,String> map,
			List<String> oldKeys, List<String> oldKeysNormalized)
	throws ParserException {
		Object retval = "";
		String delim = ";";
		String userKey;

		int minParams = 2;
		int maxParams = minParams + 2;	// both defaultValue and delim are optional parameters
		checkVaryingParameters("getStrProp()", minParams, maxParams, parameters, new Class[] {String.class, String.class, null, String.class});
		if (parameters.size()>=3) {
			retval = parameters.get(2);	// this third parameter is returned if the key is not found
		}
		if (parameters.size()==maxParams)
			delim = lastParam;
		parse(props, map, oldKeys, oldKeysNormalized, delim);

		userKey = parameters.get(1).toString();		// the key being passed in
		String value = map.get(userKey.toUpperCase());
		if (value != null) {
			Integer intval = strToInt(value);
			retval = (intval==null) ? value : new BigDecimal(intval);
		}
		return retval;
	}

	/** MapTool code: <code>setStrProp(properties, key, value)</code> -
	 * adds or replaces a key's value, respecting the order and case of existing keys.
	 * @param key A string to look up
	 * @param value A string or number to assign to <code>key</code>
	 * @return The new property string.
	 */
	public Object setStrProp(List<Object> parameters, String lastParam, String props, Map<String,String> map,
			List<String> oldKeys, List<String> oldKeysNormalized)
	throws ParserException {
		Object retval = "";
		String delim = ";";
		String userKey, userValue;

		int minParams = 3;
		int maxParams = minParams + 1;
		checkVaryingParameters("setStrProp()", minParams, maxParams, parameters, new Class[] {String.class, String.class, null, null, String.class});
		if (parameters.size() == maxParams)
			delim = lastParam;
		parse(props, map, oldKeys, oldKeysNormalized, delim);

		userKey = parameters.get(1).toString();		// the key being passed in
		userValue = parameters.get(2).toString();
		map.put(userKey.toUpperCase(), userValue);
		// Reconstruct the property string, in the same order as the original.
		// If the key already existed, preserve the case of the original key string.
		StringBuilder sb = new StringBuilder();
		if (!oldKeysNormalized.contains(userKey.toUpperCase())) {
			oldKeys.add(userKey);
		}
		for (String k : oldKeys) {
			String v = map.get(k.toUpperCase());
			sb.append(k);
			sb.append("=");
			sb.append(v);
			sb.append(" " + delim + " ");
		}
		retval = sb.toString();
		return retval;
	}

	/** MapTool code: <code>deleteStrProp(properties, key)</code> - deletes a key from the properties string.
	 * @param key A string to look up.
	 * @return The new property string. (If <code>key</code> is not found, no changes are made.)
	 */
	public Object deleteStrProp(List<Object> parameters, String lastParam, String props, Map<String,String> map,
			List<String> oldKeys, List<String> oldKeysNormalized)
	throws ParserException {
		Object retval = "";
		String delim = ";";
		String userKey;

		int minParams = 2;
		int maxParams = minParams + 1;
		checkVaryingParameters("deleteStrProp()", minParams, maxParams, parameters, new Class[] {String.class, String.class});
		if (parameters.size() == maxParams)
			delim = lastParam;
		parse(props, map, oldKeys, oldKeysNormalized, delim);

		userKey = parameters.get(1).toString();		// the key being passed in
		// reconstruct the property string, without the specified key
		StringBuilder sb = new StringBuilder();
		for (String k : oldKeys) {
			if (k.compareToIgnoreCase(userKey) == 0)
				continue;
			String v = map.get(k.toUpperCase());
			sb.append(k);
			sb.append("=");
			sb.append(v);
			sb.append(" " + delim + " ");
		}
		retval = sb.toString();
		return retval;
	}

	/** MapTool code: <code>varsFromStrProp(properties, setVars)</code> - assigns each of the values to variables named by the keys.
	 * @param setVars This argument can be:
	 * <br>"NONE" - no assignments are made
	 * <br>"SUFFIXED" - assignments are made to variable names with "_" appended
	 * <br>"UNSUFFIXED" - assignments are made to unmodified variable names
	 * @return The number of assignments made.
	 *
	 * For backwards compatibility with an earlier version, this function accepts the following argument patterns:
	 *   - properties
	 *   - properties, setVars
	 *   - properties, delim
	 *   - properties, setVars, delim
	 *
	 * The 2-parameter options are distinguished based on the actual string contents.
	 * (So, you can't use "NONE", "SUFFIXED", or "UNSUFFIXED" as non-default delimiters ;) )
	 */
	public Object varsFromStrProp(List<Object> parameters, String lastParam, String props, Map<String,String> map,
			List<String> oldKeys, List<String> oldKeysNormalized, Parser parser)
	throws ParserException {
		Object retval = "";
		String delim = ";";
		int option;	// 0-none, 1-suffixed, 2-unsuffixed

		int minParams = 1;
		int maxParams = minParams + 2;
		checkVaryingParameters("varsFromStrProp()", minParams, maxParams, parameters, new Class[] {String.class, String.class});
		if (parameters.size() == maxParams)
			delim = lastParam;

		if (parameters.size() == 1) {
			option = 2;	// default to unsuffixed to match old behavior pre 1.3b48
		} else {
			option = -1;
			String setVars = (String)parameters.get(1);
			if (setVars.equalsIgnoreCase("NONE")) {
				option = 0;
				return BigDecimal.ZERO;
			} else if (setVars.equalsIgnoreCase("SUFFIXED")) {
				option = 1;
			} else if (setVars.equalsIgnoreCase("UNSUFFIXED")) {
				option = 2;
			}

			if (option == -1) {
				// The second parameter was not a setVars option, so it must have been a non-default delimiter.
				// This is only legal if only 2 arguments were given.
				if (parameters.size() == 2) {
					option = 2;
					delim = setVars;
				} else {
					throw new ParameterException(I18N.getText("macro.function.varsFromstrProp.wrongArgs", setVars));
				}
			}
		}

		parse(props, map, oldKeys, oldKeysNormalized, delim);

		for (String k : oldKeys) {
			String v = map.get(k.toUpperCase());
			if (v != null) {
				switch (option) {
				case 0:
					// Do nothing
					break;
				case 1:
					parser.setVariable(k + "_", v);
					break;
				case 2:
					parser.setVariable(k, v);
					break;
				}
			}
		}
		retval = String.valueOf(oldKeys.size());
		return retval;
	}

	/** MapTool code: <code>strPropFromVars(varList, varStyle)</code>.
	 * @param varList A comma-separated list of variable names.
	 * @param varStyle Either "SUFFIXED" or "UNSUFFIXED", indicating how to decorate the variable names when fetching values.
	 * @return A property string containing the settings of all the variables.
	 */
	public Object strPropFromVars(List<Object> parameters, String lastParam, String props, Map<String,String> map,
			List<String> oldKeys, List<String> oldKeysNormalized, Parser parser)
	throws ParserException {
		Object retval = null;
		String delim = ";";

		int minParams = 2;
		int maxParams = minParams + 1;
		checkVaryingParameters("strPropFromVars()", minParams, maxParams, parameters, new Class[] {String.class, String.class});
		if (parameters.size() == maxParams)
			delim = lastParam;

		String varStyleString = parameters.get(1).toString();
		int varStyle;
		if (varStyleString.equalsIgnoreCase("SUFFIXED")) {
			varStyle = 0;
		} else if (varStyleString.equalsIgnoreCase("UNSUFFIXED")) {
			varStyle = 1;
		} else {
			throw new ParameterException(I18N.getText("macro.function.strPropFromVar.wrongArgs", varStyleString));
		}
		List<String> varList = new ArrayList<String>();
		StrListFunctions.parse(parameters.get(0).toString(), varList, ",");
		StringBuilder sb = new StringBuilder();
		for (String var : varList) {
			String varToGet = (varStyle==0) ? var + "_" : var;
			sb.append(var);
			sb.append("=");
			String value = parser.getVariable(varToGet).toString();
			sb.append(value);
			sb.append(" " + delim + " ");
		}
		retval = sb.toString();
		return retval;
	}

	/** MapTool code: <code>countStrProp(properties)</code>
	 * @return The number of property entries in the string.
	 */
	public Object countStrProp(List<Object> parameters, String lastParam, String props, Map<String,String> map,
			List<String> oldKeys, List<String> oldKeysNormalized)
	throws ParserException {
		Object retval = "";
		String delim = ";";

		int minParams = 1;
		int maxParams = minParams + 1;
		checkVaryingParameters("countStrProp()", minParams, maxParams, parameters, new Class[] {String.class});
		if (parameters.size() == maxParams)
			delim = lastParam;
		parse(props, map, oldKeys, oldKeysNormalized, delim);

		retval = new BigDecimal(oldKeys.size());
		return retval;
	}

	/** MapTool code: <code>indexKeyStrProp(properties, index)</code> - returns the key at the position given by <code>index</code>.
	 * @param index A number from 0 to (length-1). Ignored if out of range.
	 * @return The key for the setting at position <code>index</code>
	 */
	public Object indexKeyStrProp(List<Object> parameters, String lastParam, String props, Map<String,String> map,
			List<String> oldKeys, List<String> oldKeysNormalized)
	throws ParserException {
		Object retval = "";
		String delim = ";";

		int minParams = 2;
		int maxParams = minParams + 1;
		checkVaryingParameters("indexKeyStrProp()", minParams, maxParams, parameters, new Class[] {String.class, BigDecimal.class});
		if (parameters.size() == maxParams)
			delim = lastParam;
		parse(props, map, oldKeys, oldKeysNormalized, delim);

		int index = ((BigDecimal)parameters.get(1)).intValue();
		if (index < 0 || index >= oldKeys.size()) {
			retval = "";
		} else {
			retval = oldKeys.get(index);
		}
		return retval;
	}

	/** MapTool code: <code>indexValueStrProp(properties, index)</code> - returns the value at the position given by <code>index</code>.
	 * @param index A number from 0 to (length-1). Ignored if out of range.
	 * @return The value (converted to a number if possible) for the setting at position <code>index</code>
	 */
	public Object indexValueStrProp(List<Object> parameters, String lastParam, String props, Map<String,String> map,
			List<String> oldKeys, List<String> oldKeysNormalized)
	throws ParserException {
		String value = "";
		Object retval = null;
		String delim = ";";

		int minParams = 2;
		int maxParams = minParams + 1;
		checkVaryingParameters("indexValueStrProp()", minParams, maxParams, parameters, new Class[] {String.class, BigDecimal.class});
		if (parameters.size() == maxParams)
			delim = lastParam;
		parse(props, map, oldKeys, oldKeysNormalized, delim);

		int index = ((BigDecimal)parameters.get(1)).intValue();
		if (index < 0 || index >= oldKeys.size()) {
			value = "";
		} else {
			value = map.get(oldKeys.get(index).toUpperCase());
		}

		if (value != null) {
			try {	// convert to numeric value if possible
				Integer intval = Integer.decode(value);
				retval = new BigDecimal(intval);
			} catch (Exception e) {
				retval = value;
			}
		}

		return retval;
	}

	/** MapTool code: <code>formatStrProp(properties, listFormat, entryFormat, separator [, delim])</code> -
	 * @param listFormat Controls overall format of the output, with "%list" where the list goes.
	 * @param itemFormat Controls appearance of each entry, with "%key" and "%value" where the keys and values go.
	 * @param separator Placed between each output item.
	 * @return A string containing the formatted property string.
	 */
	public Object formatStrProp(List<Object> parameters, String lastParam, String props, Map<String,String> map,
			List<String> oldKeys, List<String> oldKeysNormalized)
	throws ParserException {
		Object retval = null;
		String delim = ";";

		int minParams = 4;
		int maxParams = minParams + 1;
		checkVaryingParameters("formatStrProp()", minParams, maxParams, parameters,
				new Class[] {String.class, String.class, String.class, String.class, String.class});
		if (parameters.size() == maxParams)
			delim = lastParam;
		parse(props, map, oldKeys, oldKeysNormalized, delim);

		String listFormat  = parameters.get(1).toString();
		String entryFormat = parameters.get(2).toString();
		String separator   = parameters.get(3).toString();

		StringBuilder sb = new StringBuilder();
		boolean firstEntry = true;
		for (String key : oldKeys) {
			if (firstEntry) {
				firstEntry = false;
			} else {
				sb.append(separator);
			}
			String entry = entryFormat;
			entry = entry.replaceAll("\\%key", key);
			String value = map.get(key.toUpperCase());
			value = fullyQuoteString(value);
			entry = entry.replaceAll("\\%value", value);
			sb.append(entry);
		}

		retval = listFormat.replaceFirst("\\%list", sb.toString());
		return retval;
	}



	/** Tries to convert a string to a number, returning <code>null</code> on failure. */
	public Integer strToInt(String s) {
		Integer intval = null;
		try {	// convert to numeric value if possible
			intval = Integer.decode(s);
		} catch (Exception e) {
			intval = null;
		}
		return intval;
	}

	@Override
	public void checkParameters(List<Object> parameters) throws ParameterException {
		super.checkParameters(parameters);
		// The work is done in checkVaryingParameters() instead.
	}

	/** Checks number and types of parameters (pass null type to suppress typechecking for that slot). */
	public void checkVaryingParameters(
			String funcName, int minParams, int maxParams, List<Object> parameters, Class<?>[] expected)
	throws ParameterException {
		if (parameters.size() < minParams || parameters.size() > maxParams) {
			if (minParams == maxParams) {
				throw new ParameterException(I18N.getText("macro.function.strLst.incorrectParamExact", funcName, minParams));
			} else {
				throw new ParameterException(I18N.getText("macro.function.strLst.incorrectParamExact", funcName, minParams, maxParams));
			}
		}

		int numToCheck = expected.length;
		if (numToCheck > parameters.size()) numToCheck = parameters.size();

		for (int i=0; i<numToCheck; i++) {
			if (expected[i]!=null && !(expected[i].isInstance(parameters.get(i))))
				throw new ParameterException(I18N.getText("macro.function.strLst.incorrectParamExact", funcName, i+1, expected[i].getSimpleName(),
						parameters.get(i), parameters.get(i).getClass().getSimpleName()));
		}
	}
}

// @formatter:off

/* Here is a test macro

<b>Tests:</b>
[h: OK = "OK"] [h: Fail = "<b>Fail</b>"]
[h: pnull = ""]
[h: psemi = ";"]
[h: p1 = "a=arrow"]
[h: p1semi = "a=arrow;"]
[h: p2 = "a=5;b=bob"]
[h: p2semi = "a = 5;b = bob;"]
<br>getStrProp():
{if( getStrProp(pnull,"")=="" && getStrProp(pnull,"a")==""
     && getStrProp(psemi,"")=="" && getStrProp(psemi,"a")==""
     && getStrProp(p1,"a")=="arrow" && getStrProp(p1,"")=="" && getStrProp(p1,"b")==""
     && getStrProp(p1semi,"a")=="arrow" && getStrProp(p1semi,"")=="" && getStrProp(p1semi,"b")==""
     && (getStrProp(p2,"a")+5)==10 && getStrProp(p2,"b")=="bob" && getStrProp(p2,"c")==""
     && (getStrProp(p2semi,"a")+5)==10 && getStrProp(p2semi,"b")=="bob" && getStrProp(p2semi,"c")==""
     , OK, Fail)}

<br>setStrProp():
{if( setStrProp(pnull,"a","arrow")=="a=arrow ; " && setStrProp(pnull,"b",3)=="b=3 ; "
     && setStrProp(psemi,"a","arrow")=="a=arrow ; " && setStrProp(psemi,"b",3)=="b=3 ; "
     && setStrProp(p1,"a",5)=="a=5 ; " && setStrProp(p1,"b","boy")=="a=arrow ; b=boy ; "
     && setStrProp(p1semi,"a",5)=="a=5 ; " && setStrProp(p1semi,"b","boy")=="a=arrow ; b=boy ; "
     && setStrProp(p2,"a","ann")=="a=ann ; b=bob ; " && setStrProp(p2,"B",4)=="a=5 ; b=4 ; "
     && setStrProp(p2,"d","dave")=="a=5 ; b=bob ; d=dave ; "
     && setStrProp(p2semi,"a","ann")=="a=ann ; b=bob ; " && setStrProp(p2semi,"B",4)=="a=5 ; b=4 ; "
     && setStrProp(p2semi,"d","dave")=="a=5 ; b=bob ; d=dave ; "
     , OK, Fail)}

<br>deleteStrProp():
{if( deleteStrProp(pnull,"")=="" && deleteStrProp(pnull,"a")==""
     && deleteStrProp(psemi,"")=="" && deleteStrProp(psemi,"a")==""
     && deleteStrProp(p1,"a")=="" && deleteStrProp(p1,"")=="a=arrow ; " && deleteStrProp(p1,"b")=="a=arrow ; "
     && deleteStrProp(p1semi,"a")=="" && deleteStrProp(p1semi,"")=="a=arrow ; " && deleteStrProp(p1semi,"b")=="a=arrow ; "
     && deleteStrProp(p2,"b")=="a=5 ; " && deleteStrProp(p2,"a")=="b=bob ; "
     , OK, Fail)}

<br>varsFromStrProp():
[h: success = 1]
[h: n=varsFromStrProp("d=5; e=eat", "suffixed")] [h: success = if(n==2 && d_==5 && (d_+1)==6 && e_=="eat", success, 0)]
[h: n=varsFromStrProp("f=foo;", "suffixed")] [h: success = if(n==1 && f_=="foo", success, 0)]
[h: n=varsFromStrProp("g=goo; h=9", "unsuffixed")] [h: success = if(n==2 && g=="goo" && (h+1)==10, success, 0)]

{if( success
     && varsFromStrProp(pnull,"suffixed")==0 && varsFromStrProp(psemi,"suffixed")==0
     && varsFromStrProp(p2, "none")==0
     , OK, Fail)}

<br>strPropFromVars():
[h: a="apple"] [h: bob="bobstring"] [h: c=1]
[h: str = strPropFromVars("a,bob,c", "unsuffixed")]
{if( str=="a=apple ; bob=bobstring ; c=1 ; "
     , OK, Fail)}

<br>countStrProp():
{if( countStrProp(pnull)==0 && countStrProp(psemi)==0 && countStrProp(p1)==1 && countStrProp(p1semi)==1
     && countStrProp(p2)==2 && countStrProp(p2semi)==2
     , OK, Fail)}

<br>indexKeyStrProp():
{if( indexKeyStrProp(pnull,0)=="" && indexKeyStrProp(pnull,1)=="" && indexKeyStrProp(psemi,0)==""
     && indexKeyStrProp(p1,0)=="a" && indexKeyStrProp(p1,1)==""
     && indexKeyStrProp(p2semi,1)=="b" && indexKeyStrProp(p2,2)==""
     , OK, Fail)}

<br>indexValueStrProp():
{if( indexValueStrProp(pnull,0)=="" && indexValueStrProp(pnull,1)=="" && indexValueStrProp(psemi,0)==""
     && indexValueStrProp(p1,0)=="arrow" && indexValueStrProp(p1,1)==""
     && (indexValueStrProp(p2,0)+3)==8 && indexValueStrProp(p2semi,1)=="bob" && indexValueStrProp(p2,2)==""
     , OK, Fail)}

<br>formatStrProp():
{if( formatStrProp(p2, "BEGIN%listEND", "(%key,%value)", "...")=="BEGIN(a,5)...(b,bob)END"
     , OK, Fail)}


<br><br>



<b>Tests with alternate delimiter:</b>
[h: OK = "OK"] [h: Fail = "<b>Fail</b>"]
[h: pnull = ""]
[h: psemi = "%%"]
[h: p1 = "a=arrow"]
[h: p1semi = "a=arrow%%"]
[h: p2 = "a=5%%b=bob"]
[h: p2semi = "a = 5%%b = bob%%"]
<br>getStrProp():
{if( getStrProp(pnull,"","","%%")=="" && getStrProp(pnull,"a","","%%")==""
     && getStrProp(psemi,"","","%%")=="" && getStrProp(psemi,"a","","%%")==""
     && getStrProp(p1,"a","","%%")=="arrow" && getStrProp(p1,"","","%%")=="" && getStrProp(p1,"b","","%%")==""
     && getStrProp(p1semi,"a","","%%")=="arrow" && getStrProp(p1semi,"","","%%")=="" && getStrProp(p1semi,"b","","%%")==""
     && (getStrProp(p2,"a","","%%")+5)==10 && getStrProp(p2,"b","","%%")=="bob" && getStrProp(p2,"c","","%%")==""
     && (getStrProp(p2semi,"a","","%%")+5)==10 && getStrProp(p2semi,"b","","%%")=="bob" && getStrProp(p2semi,"c","","%%")==""
     , OK, Fail)}

<br>setStrProp():
{if( setStrProp(pnull,"a","arrow","%%")=="a=arrow %% " && setStrProp(pnull,"b",3,"%%")=="b=3 %% "
     && setStrProp(psemi,"a","arrow","%%")=="a=arrow %% " && setStrProp(psemi,"b",3,"%%")=="b=3 %% "
     && setStrProp(p1,"a",5,"%%")=="a=5 %% " && setStrProp(p1,"b","boy","%%")=="a=arrow %% b=boy %% "
     && setStrProp(p1semi,"a",5,"%%")=="a=5 %% " && setStrProp(p1semi,"b","boy","%%")=="a=arrow %% b=boy %% "
     && setStrProp(p2,"a","ann","%%")=="a=ann %% b=bob %% " && setStrProp(p2,"B",4,"%%")=="a=5 %% b=4 %% "
     && setStrProp(p2,"d","dave","%%")=="a=5 %% b=bob %% d=dave %% "
     && setStrProp(p2semi,"a","ann","%%")=="a=ann %% b=bob %% " && setStrProp(p2semi,"B",4,"%%")=="a=5 %% b=4 %% "
     && setStrProp(p2semi,"d","dave","%%")=="a=5 %% b=bob %% d=dave %% "
     , OK, Fail)}

<br>deleteStrProp():
{if( deleteStrProp(pnull,"","%%")=="" && deleteStrProp(pnull,"a","%%")==""
     && deleteStrProp(psemi,"","%%")=="" && deleteStrProp(psemi,"a","%%")==""
     && deleteStrProp(p1,"a","%%")=="" && deleteStrProp(p1,"","%%")=="a=arrow %% " && deleteStrProp(p1,"b","%%")=="a=arrow %% "
     && deleteStrProp(p1semi,"a","%%")=="" && deleteStrProp(p1semi,"","%%")=="a=arrow %% " && deleteStrProp(p1semi,"b","%%")=="a=arrow %% "
     && deleteStrProp(p2,"b","%%")=="a=5 %% " && deleteStrProp(p2,"a","%%")=="b=bob %% "
     , OK, Fail)}

<br>varsFromStrProp():
[h: success = 1]
[h: n=varsFromStrProp("d=5%% e=eat", "suffixed","%%")] [h: success = if(n==2 && d_==5 && (d_+1)==6 && e_=="eat", success, 0)]
[h: n=varsFromStrProp("f=foo%%", "suffixed","%%")] [h: success = if(n==1 && f_=="foo", success, 0)]
[h: n=varsFromStrProp("g=goo%% h=9", "unsuffixed","%%")] [h: success = if(n==2 && g=="goo" && (h+1)==10, success, 0)]

{if( success
     && varsFromStrProp(pnull,"suffixed","%%")==0 && varsFromStrProp(psemi,"suffixed","%%")==0
     && varsFromStrProp(p2, "none","%%")==0
     , OK, Fail)}

<br>strPropFromVars():
[h: a="apple"] [h: bob="bobstring"] [h: c=1]
[h: str = strPropFromVars("a,bob,c", "unsuffixed", "%%")]
{if( str=="a=apple %% bob=bobstring %% c=1 %% "
     , OK, Fail)}

<br>countStrProp():
{if( countStrProp(pnull,"%%")==0 && countStrProp(psemi,"%%")==0 && countStrProp(p1,"%%")==1 && countStrProp(p1semi,"%%")==1
     && countStrProp(p2,"%%")==2 && countStrProp(p2semi,"%%")==2
     , OK, Fail)}

<br>indexKeyStrProp():
{if( indexKeyStrProp(pnull,0,"%%")=="" && indexKeyStrProp(pnull,1,"%%")=="" && indexKeyStrProp(psemi,0,"%%")==""
     && indexKeyStrProp(p1,0,"%%")=="a" && indexKeyStrProp(p1,1,"%%")==""
     && indexKeyStrProp(p2semi,1,"%%")=="b" && indexKeyStrProp(p2,2,"%%")==""
     , OK, Fail)}

<br>indexValueStrProp():
{if( indexValueStrProp(pnull,0,"%%")=="" && indexValueStrProp(pnull,1,"%%")=="" && indexValueStrProp(psemi,0,"%%")==""
     && indexValueStrProp(p1,0,"%%")=="arrow" && indexValueStrProp(p1,1,"%%")==""
     && (indexValueStrProp(p2,0,"%%")+3)==8 && indexValueStrProp(p2semi,1,"%%")=="bob" && indexValueStrProp(p2,2,"%%")==""
     , OK, Fail)}

<br>formatStrProp():
{if( formatStrProp(p2, "BEGIN%listEND", "(%key,%value)", "...","%%")=="BEGIN(a,5)...(b,bob)END"
     , OK, Fail)}







<br><br>
<b>Examples:</b>
<br>prop = [prop = "a=33; b = bob ; c=cat ; D=99"] [h: n=2] [h: k="c"]
<br>The value of {k} is [getStrProp(prop, k)]
<br>The key of entry {n} is [indexKeyStrProp(prop, n)]
<br>The value of entry {n} is [indexValueStrProp(prop, 2)]
<br>Reading in variables yields [varsFromStrProp(prop,"suffixed")] variables to use: a_+1={a_+1}, b_={b_}, c_={c_}, D_-9={d_-9}
<br>There are [cnt = countStrProp(prop)] settings.
They are [c(cnt, ", "): indexKeyStrProp(prop, roll.count-1) +" is "+ indexValueStrProp(prop, roll.count-1)]
<br>Eliminating {k} from [prop] yields [deleteStrProp(prop, k)]

 */
// @formatter:on
