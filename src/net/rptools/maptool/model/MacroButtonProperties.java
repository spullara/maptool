package net.rptools.maptool.model;

import net.rptools.maptool.client.ui.MacroButtonHotKeyManager;

/**
 * This (data)class is used by the MacroButtons that extend the AbstractMacroButton class.
 * @see net.rptools.maptool.client.ui.macrobutton.AbstractMacroButton
 * The Campaign class initializes a MacroButtonProperties array with default values.
 * @see net.rptools.maptool.model.Campaign
 * When a campaign is loaded the properties from the saved file will overwrite the defaults
 * provided here.
 */
public class MacroButtonProperties {
	private int index;
	private String colorKey;
	private String hotKey;
	private String command;
	private String label;
	private boolean autoExecute;
	private boolean includeLabel;  //include the macro label when printing output?

	public MacroButtonProperties(int index, String colorKey, String hotKey, String command, String label, boolean autoExecute, boolean includeLabel) {
		this.index = index;
		this.colorKey = colorKey;
		this.hotKey = hotKey;
		this.command = command;
		this.label = label;
		this.autoExecute = autoExecute;
		this.includeLabel = includeLabel;
	}

	public MacroButtonProperties(int index)	{
		this.index = index;
		colorKey = "";
		hotKey = MacroButtonHotKeyManager.HOTKEYS[0];
		command = "";
		//label = String.valueOf(index);
		label = "(new)";
		autoExecute = true;
		includeLabel = false;
		
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

	public void reset() {
		colorKey = "";
		hotKey = MacroButtonHotKeyManager.HOTKEYS[0];
		command = "";
		label = String.valueOf(index);
		autoExecute = true;
		includeLabel = false;		
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

		return true;
	}

	public int hashCode() {
		int result;
		result = index;
		result = 31 * result + (colorKey != null ? colorKey.hashCode() : 0);
		result = 31 * result + (hotKey != null ? hotKey.hashCode() : 0);
		result = 31 * result + (command != null ? command.hashCode() : 0);
		result = 31 * result + (label != null ? label.hashCode() : 0);
		result = 31 * result + (autoExecute ? 1 : 0);
		result = 31 * result + (includeLabel ? 1 : 0);
		return result;
	}
}
