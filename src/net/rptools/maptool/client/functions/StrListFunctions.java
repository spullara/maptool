package net.rptools.maptool.client.functions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;
import net.rptools.parser.function.ParameterException;

import org.apache.commons.lang.StringUtils;

/**
 * Implements various string utility functions.
 * <br>
 * <br>The <code>list*</code> functions operate on a list string of the form "item1, item2, ...".
 * <br>An optional <code>delim</code> sets the item delimiter.
 * <br>
 * <br><code>listGet(list, index [,delim])</code> returns the item at position <code>index</code>, or <code>""</code> if out of bounds.
 * <br>
 * <br><code>listDelete(list, index [,delim])</code> returns a new list with the item at position <code>index</code> deleted. 
 * <br>
 * <br><code>listCount(list [,delim])</code> returns the number of entries in the list.
 * <br>
 * <br><code>listFind(list, target [,delim])</code> returns the index of the first occurence of <code>target</code>, or -1 if not found.
 * <br>
 * <br><code>listAppend(list, target [,delim])</code> returns a new list with <code>target</code> appended. 
 * <br>
 * <br><code>listInsert(list, index, target [,delim])</code> returns a new list with <code>target</code> 
 * inserted before the entry at <code>index</code>.  
 * <br>
 * <br><code>listReplace(list, index, target [,delim])</code> returns a new list with the entry at <code>index</code>
 * repaced by <code>target</code> 
 * <br>
 * 
 * @author knizia.fan
 */
public class StrListFunctions extends AbstractFunction {
    public StrListFunctions() {
        super(1, 3, "listGet", "listDelete", "listCount", "listFind", "listAppend", "listInsert", "listReplace");
    }
    
	/** The singleton instance. */
	private final static StrListFunctions instance = new StrListFunctions();
	
	/** Gets the Input instance.
	 * @return the instance. */
	public static StrListFunctions getInstance() {
		return instance;
	}
	
    /** Parses a list.
     * @param listStr has the form "item1, item2, ..."
     * @param list is populated with the list items.
     */
    public static void parse(String listStr, List<String> list) {
    	parse(listStr, list, ",");
    }
    /** Parses a list.
     * @param listStr has the form "item1, item2, ..."
     * @param list is populated with the list items.
     * @param delim is the list delimiter to use.
     */

    public static void parse(String listStr, List<String> list, String delim) {
    	if (StringUtils.isEmpty(listStr.trim()))
    		return;	// null strings have zero entries
    	Pattern pattern = Pattern.compile("[^\\" + delim + "]*");
    	Matcher matcher = pattern.matcher(listStr);
    	int lastEnd = -1;	// where the last match ended
    	while (matcher.find()) {
    		// Avoid null matches at the end of other matches
    		if (lastEnd != matcher.start()) {
    			list.add(matcher.group().trim());
    			lastEnd = matcher.end();
    		}
    	}
    }
    
    @Override
    public Object childEvaluate(Parser parser, String functionName, List<Object> parameters) throws ParserException {
    	Object retval = "";
    	String listStr = parameters.get(0).toString().trim();
    	String lastParam = parameters.get(parameters.size()-1).toString();
    	
    	ArrayList<String> list = new ArrayList<String>();
    	
    	if ("listGet".equalsIgnoreCase(functionName))
    		retval = listGet(parameters, listStr, lastParam, list);
    	else if ("listDelete".equalsIgnoreCase(functionName))
    		retval = listDelete(parameters, listStr, lastParam, list);
    	else if ("listCount".equalsIgnoreCase(functionName))
    		retval = listCount(parameters, listStr, lastParam, list);
    	else if ("listFind".equalsIgnoreCase(functionName))
    		retval = listFind(parameters, listStr, lastParam, list);
    	else if ("listAppend".equalsIgnoreCase(functionName))
    		retval = listAppend(parameters, listStr, lastParam, list);
    	else if ("listInsert".equalsIgnoreCase(functionName))
    		retval = listInsert(parameters, listStr, lastParam, list);
    	else if ("listReplace".equalsIgnoreCase(functionName))
    		retval = listReplace(parameters, listStr, lastParam, list);
    	
    	return retval;
    }

    public Object listGet(List<Object> parameters, String listStr, String lastParam, List<String> list) 
    throws ParameterException {
    	Object retval = "";
		if (parameters.size() == 2) {
			checkVaryingParameters("listGet()", 2, parameters, new Class[] {String.class, BigDecimal.class});
			parse(listStr, list);
		} else {
			checkVaryingParameters("listGet()", 3, parameters, new Class[] {String.class, BigDecimal.class, String.class});
			parse(listStr, list, lastParam);
		}
		int index = ((BigDecimal)parameters.get(1)).intValue();
		if (index >= 0 && index < list.size()) {
			String value = list.get(index);
    		if (value != null) {
    			try {	// convert to numeric value if possible
    				Integer intval = Integer.decode(value);
    				retval = new BigDecimal(intval);
    			} catch (Exception e) {
    				retval = value;
    			}
    		} else {
    			retval = "";
    		}
			retval = list.get(index);
		}
    	return retval;
    }
    	
    public Object listDelete(List<Object> parameters, String listStr, String lastParam, List<String> list) 
    throws ParameterException {
    	Object retval = "";
		if (parameters.size() == 2) {
			checkVaryingParameters("listDelete()", 2, parameters, new Class[] {String.class, BigDecimal.class});
			parse(listStr, list);
		} else {
			checkVaryingParameters("listDelete()", 3, parameters, new Class[] {String.class, BigDecimal.class, String.class});
			parse(listStr, list, lastParam); 
		}
		int index = ((BigDecimal)parameters.get(1)).intValue();
		StringBuilder sb = new StringBuilder();
		boolean inRange = ( index >= 0 && index < list.size());
		for (int i=0; i<list.size(); i++) {
			if (i != index) {
				sb.append(list.get(i));
				sb.append(", ");
			}
		}
		if (list.size() > (inRange ? 1 : 0)) {
			// Delete the last ", "
			sb.delete(sb.length()-2, sb.length());
		}
		retval = sb.toString();
    	return retval;
    }

    public Object listCount(List<Object> parameters, String listStr, String lastParam, List<String> list) 
    throws ParameterException {
    	Object retval = "";
		if (parameters.size() == 1) {
			checkVaryingParameters("listCount()", 1, parameters, new Class[] {String.class});
			parse(listStr, list);
		} else {
			checkVaryingParameters("listCount()", 2, parameters, new Class[] {String.class, String.class});
			parse(listStr, list, lastParam);
		}
		retval = new BigDecimal(list.size());
    	return retval;
    }
    	
    public Object listFind(List<Object> parameters, String listStr, String lastParam, List<String> list) 
    throws ParameterException {
    	Object retval = "";
		if (parameters.size() == 2) {
			checkVaryingParameters("listFind()", 2, parameters, new Class[] {String.class, String.class});
			parse(listStr, list);
		} else {
			checkVaryingParameters("listFind()", 3, parameters, new Class[] {String.class, String.class, String.class});
			parse(listStr, list, lastParam);
		}
		String target = parameters.get(1).toString().trim();
		int index;
		for (index=0; index<list.size(); index++) {
			if (target.equalsIgnoreCase(list.get(index))) {
				break;
			}
		}
		if (index == list.size()) index = -1;
		retval = new BigDecimal(index);
    	return retval;
    }
    	
    public Object listAppend(List<Object> parameters, String listStr, String lastParam, List<String> list) 
    throws ParameterException {
    	Object retval = "";
		if (parameters.size() == 2) {
			checkVaryingParameters("listAppend()", 2, parameters, new Class[] {String.class, String.class});
			parse(listStr, list);
		} else {
			checkVaryingParameters("listAppend()", 3, parameters, new Class[] {String.class, String.class, String.class});
			parse(listStr, list, lastParam);
		}
		String target = parameters.get(1).toString().trim();
		StringBuilder sb = new StringBuilder();
		for (String item: list) {
			sb.append(item);
			sb.append(", ");
		}
		sb.append(target);
		retval = sb.toString();
    	return retval;
    }

    public Object listInsert(List<Object> parameters, String listStr, String lastParam, List<String> list) 
    throws ParameterException {
    	Object retval = "";
		if (parameters.size() == 3) {
			checkVaryingParameters("listInsert()", 3, parameters, new Class[] {String.class, BigDecimal.class, String.class});
			parse(listStr, list);
		} else {
			checkVaryingParameters("listInsert()", 4, parameters, new Class[] {String.class, BigDecimal.class, String.class, String.class});
			parse(listStr, list, lastParam);
		}
		int index = ((BigDecimal)parameters.get(1)).intValue();
		String target = parameters.get(2).toString().trim();
		StringBuilder sb = new StringBuilder();
		if (list.size()==0) {
			if (index==0) {
				retval = target;
			}
		} else {
    		for (int i=0; i<list.size()+1; i++) {
    			if (i==index) {
    				sb.append(target);
    				sb.append(", ");
    			}
    			if (i<list.size()) {
    				sb.append(list.get(i));
    				sb.append(", ");
    			}
    		}
			sb.delete(sb.length()-2, sb.length());	// remove the trailing ", "
			retval = sb.toString();
		}
    	return retval;
    }
    	
    public Object listReplace(List<Object> parameters, String listStr, String lastParam, List<String> list) 
    throws ParameterException {
    	Object retval = "";
		if (parameters.size() == 3) {
			checkVaryingParameters("listReplace()", 3, parameters, new Class[] {String.class, BigDecimal.class, String.class});
			parse(listStr, list);
		} else {
			checkVaryingParameters("listReplace()", 4, parameters, new Class[] {String.class, BigDecimal.class, String.class, String.class});
			parse(listStr, list, lastParam);
		}

		if (list.size() == 0)	// can't replace if there are no entries
			return retval;	

		int index = ((BigDecimal)parameters.get(1)).intValue();
		String target = parameters.get(2).toString().trim();
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<list.size(); i++) {
			if (i==index) {
				sb.append(target);
			} else {
				sb.append(list.get(i));
			}
			sb.append(", ");
		}
		sb.delete(sb.length()-2, sb.length());	// remove the trailing ", "
		retval = sb.toString();
    	return retval;
    }
    
    @Override
    public void checkParameters(List<Object> parameters) throws ParameterException {
        super.checkParameters(parameters);
        // The work is done in checkVaryingParameters() instead.
    }

    public void checkVaryingParameters(
    		String funcName, int numParams, List<Object> parameters, Class[] expected) throws ParameterException {
    	if (numParams != parameters.size()) 
    		throw new ParameterException(String.format("%s requires %d parameters", funcName, numParams));
    	
    	if (numParams > expected.length) numParams = expected.length;

    	for (int i=0; i<numParams; i++) {
    		if (expected[i]!=null && !(expected[i].isInstance(parameters.get(i))))
    			throw new ParameterException(String.format("Illegal type for argument %d to %s, expected %s but got %s",
    					i+1, funcName, expected[i].getName(), parameters.get(i).getClass().getName()));
    	}
    }
}

/* Here is a test macro

list = [list = "a,b,c,,e"]
<br>count = [listCount(list)]
<br>item 1 = '[listGet(list, 1)]'
<br>item 3 = '[listGet(list, 3)]'
<br>delete 2 --> [listDelete(list, 2)]
<br>find "c" --> [listFind(list, "c")]
<br>find "" --> [listFind(list, "")]
<br>append "f" --> [listAppend(list, "f")]
<br>insert "aa" at 1 --> [listInsert(list, 1, "aa")]
<br>insert "f" at 5 --> [listInsert(list, 5, "f")]
<br>replace 0 with "A" --> [listReplace(list, 0, "A")]
<br>replace 3 with "D" --> [listReplace(list, 3, "D")]
<br>replace 5 with "F" --> [listReplace(list, 5, "F")]
<br>replace 1 with "" --> [listReplace(list, 1, "")]

*/
 

