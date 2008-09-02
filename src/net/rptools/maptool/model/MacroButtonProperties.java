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
package net.rptools.maptool.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.rptools.maptool.client.ui.MacroButtonHotKeyManager;

/**
 * This (data)class is used by the MacroButtons that extend the AbstractMacroButton class.
 * @see net.rptools.maptool.client.ui.macrobuttons.buttons.AbstractMacroButton
 * The Campaign class initializes a MacroButtonProperties array with default values.
 * @see net.rptools.maptool.model.Campaign
 * When a campaign is loaded the properties from the saved file will overwrite the defaults
 * provided here.
 */
public class MacroButtonProperties implements Comparable<Object> {
	private int index;
	private String colorKey;
	private String hotKey;
	private String command;
	private String label;
	private String sortby;
	private boolean autoExecute;
	private boolean includeLabel;  //include the macro label when printing output?
	private boolean applyToTokens; //when the button is clicked it will impersonate every selected token when executing the macro

	public MacroButtonProperties(int index, String colorKey, String hotKey, String command, String label, String sortby, boolean autoExecute, boolean includeLabel, boolean applyToTokens) {
		this.index = index;
		this.colorKey = colorKey;
		this.hotKey = hotKey;
		this.command = command;
		this.label = label;
		this.sortby = sortby;
		this.autoExecute = autoExecute;
		this.includeLabel = includeLabel;
		this.applyToTokens = applyToTokens;
	}

	public MacroButtonProperties(int index)	{
		this.index = index;
		colorKey = "";
		hotKey = MacroButtonHotKeyManager.HOTKEYS[0];
		command = "";
		//label = String.valueOf(index);
		label = "(new)";
		sortby = "";
		autoExecute = true;
		includeLabel = false;
		applyToTokens = false;
		
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index)	{
		this.index = index;
	}

	public String getColorKey()	{
		return colorKey;
	}

	public void setColorKey(String colorKey) {
		this.colorKey = colorKey;
	}

	public String getHotKey() {
		return hotKey;
	}

	public void setHotKey(String hotKey) {
		this.hotKey = hotKey;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getSortby() {
		return sortby;
	}

	public void setSortby(String sortby) {
		this.sortby = sortby;
	}

	public boolean getAutoExecute()	{
		return autoExecute;
	}

	public void setAutoExecute(boolean autoExecute)	{
		this.autoExecute = autoExecute;
	}

	public boolean getIncludeLabel() {
		return includeLabel;
	}

	public void setIncludeLabel(boolean includeLabel) {
		this.includeLabel = includeLabel;
	}

	public boolean getApplyToTokens() {
		return applyToTokens;
	}

	public void setApplyToTokens(boolean applyToTokens) {
		this.applyToTokens = applyToTokens;
	}

	public void reset() {
		colorKey = "";
		hotKey = MacroButtonHotKeyManager.HOTKEYS[0];
		command = "";
		label = String.valueOf(index);
		sortby = "";
		autoExecute = true;
		includeLabel = false;	
		applyToTokens = false;
	}

	//TODO: may have to rewrite hashcode and equals to only take index into account
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		MacroButtonProperties that = (MacroButtonProperties) o;

		if (autoExecute != that.autoExecute) {
			return false;
		}
		if (includeLabel != that.includeLabel) {
			return false;
		}
		if (applyToTokens != that.applyToTokens) {
			return false;
		}
		if (index != that.index) {
			return false;
		}
		if (colorKey != null ? !colorKey.equals(that.colorKey) : that.colorKey != null) {
			return false;
		}
		if (command != null ? !command.equals(that.command) : that.command != null) {
			return false;
		}
		if (hotKey != null ? !hotKey.equals(that.hotKey) : that.hotKey != null) {
			return false;
		}
		if (label != null ? !label.equals(that.label) : that.label != null) {
			return false;
		}
		if (sortby != null ? !sortby.equals(that.sortby) : that.sortby != null) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		int result;
		result = index;
		result = 31 * result + (colorKey != null ? colorKey.hashCode() : 0);
		result = 31 * result + (hotKey != null ? hotKey.hashCode() : 0);
		result = 31 * result + (command != null ? command.hashCode() : 0);
		result = 31 * result + (label != null ? label.hashCode() : 0);
		result = 31 * result + (sortby != null ? sortby.hashCode() : 0);
		result = 31 * result + (autoExecute ? 1 : 0);
		result = 31 * result + (includeLabel ? 1 : 0);
		result = 31 * result + (applyToTokens ? 1 : 0);
		return result;
	}
	
	// function to enable sorting of buttons; uses the sortby field
	// concatenated with the label field.  Case Insensitive
	public int compareTo(Object b2) throws ClassCastException {
	    if (!(b2 instanceof MacroButtonProperties))
	      throw new ClassCastException("A MacroButtonProperties object expected.");
	    String b1string = ( sortby!=null ? sortby : "" ).concat(label!=null ? label : "");
	    String b2sortby = ((MacroButtonProperties) b2).getSortby();
	    if (b2sortby == null) b2sortby="";
	    String b2label = ((MacroButtonProperties) b2).getLabel();
	    if (b2label == null) b2label="";
	    // now parse the sort strings to help dice codes sort properly
	    b1string = modifySortString(b1string);
	    String b2string = modifySortString(b2sortby.concat(b2label));
	    return b1string.compareToIgnoreCase(b2string);
	}

	// function to pad numbers with leading zeroes to help sort them appropriately.  
	// So this will turn a 2d6 into 0002d0006, and 10d6 into 0010d0006, so the 2d6
	// will sort as lower.
	private static final Pattern sortStringPattern = Pattern.compile("(\\d+)");
	private static String modifySortString(String str){
	    StringBuffer result = new StringBuffer();
	    Matcher matcher = sortStringPattern.matcher(str);
	    while ( matcher.find() ) {
	      matcher.appendReplacement(result, paddingString(matcher.group(1), 4, '0', true));
	    }
	    matcher.appendTail(result);
	    return result.toString();
	}
	
	// function found at http://www.rgagnon.com/javadetails/java-0448.html
	// to pad a string by inserting additional characters
	public static String paddingString ( String s, int n, char c , boolean paddingLeft  ) {
	    StringBuffer str = new StringBuffer(s);
	    int strLength  = str.length();
	    if ( n > 0 && n > strLength ) {
	      for ( int i = 0; i <= n ; i ++ ) {
	            if ( paddingLeft ) {
	              if ( i < n - strLength ) str.insert( 0, c );
	            }
	            else {
	              if ( i > strLength ) str.append( c );
	            }
	      	}
	    }
	    return str.toString();
	}
	
}
