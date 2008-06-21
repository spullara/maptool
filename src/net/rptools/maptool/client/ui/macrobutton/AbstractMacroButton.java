package net.rptools.maptool.client.ui.macrobutton;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JTextPane;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolUtil;
import net.rptools.maptool.client.ui.MacroButtonHotKeyManager;
import net.rptools.maptool.client.ui.MacroButtonDialog;
import net.rptools.maptool.util.GraphicsUtil;
import net.rptools.maptool.model.MacroButtonProperties;

/**
 * Base class of CampaignMacroButton and GlobalMacroButton.
 * TokenMacroButton doesn't extend this class because it is very simple.
 * MacroButtons that extend this class use MacroButtonProperties
 * @see net.rptools.maptool.model.MacroButtonProperties as data object.
 * 
 * These buttons are used in Macro Button Panel in the UI.
 */
public abstract class AbstractMacroButton extends JButton implements MouseListener
{
	protected final MacroButtonProperties properties;
	
	protected final MacroButtonHotKeyManager hotKeyManager;
	protected static MacroButtonDialog macroButtonDialog = new MacroButtonDialog();


	public AbstractMacroButton(MacroButtonProperties properties) {

		this.properties = properties;
		
		// we have to call setColor() and setText() here since properties only hold "dumb" data.
		setColor(properties.getColorKey());
		setText(getButtonText());
		hotKeyManager = new MacroButtonHotKeyManager(this);
		hotKeyManager.assignKeyStroke(properties.getHotKey());
	}

	public int getIndex() {
		return properties.getIndex();
	}
	
	public void setIndex(int index) {
		properties.setIndex(index);
	}
	
	public void setAutoExecute(boolean autoExecute) {
		properties.setAutoExecute(autoExecute);
	}
	public boolean getAutoExecute() {
		return properties.getAutoExecute();
	}

	// setting the hot key will not affect the KeyStroke
	// use getHotkeyManager().assignHotKey() for that purpose
	public void setHotKey(String hotKey) {
		properties.setHotKey(hotKey);
	}

	public String getHotKey() {
		return properties.getHotKey();
	}

	public MacroButtonHotKeyManager getHotKeyManager()
	{
		return hotKeyManager;
	}

	public void setIncludeLabel(boolean includeLabel) {
		properties.setIncludeLabel(includeLabel);
	}
	public boolean getIncludeLabel() {
		return properties.getIncludeLabel();
	}

	public String getMacroLabel() {
		return properties.getLabel();
	}
	public void setMacroLabel(String label) {
		properties.setLabel(label);
	}

	public String getCommand() {
		return properties.getCommand();
	}

	public void setCommand(String command) {
		properties.setCommand(command);
	}


	public void setColor(String colorKey) {

		properties.setColorKey(colorKey);

		//If the key doesn't correspond to one of our colors, then use the default
		if (!MapToolUtil.isValidColor(colorKey))
			setBackground(null);
		else {
			// because the text is dark, we need to lighten the darker colors
			setBackground(GraphicsUtil.lighter(MapToolUtil.getColor(colorKey)));
		}
	}

	public String getColor() {
		if (!MapToolUtil.isValidColor(properties.getColorKey()))
			return "default";
		else
			return properties.getColorKey();
	}

	/*
	 *  Get the text for the macro button by filtering out
	 *   label macro (if any), and add hotkey hint (if any)
	 */
	public String getButtonText() {

		String buttonLabel;
		final Pattern MACRO_LABEL = Pattern.compile("^(/\\w+\\s+)(.*)$");
		String label = properties.getLabel();
		Matcher m = MACRO_LABEL.matcher(label);
		if(m.matches())
			buttonLabel = m.group(2);
		else
			buttonLabel = label;

		// if there is no hotkey (HOTKEY[0]) then no need to add hint
		String hotKey = properties.getHotKey();
		if( hotKey.equals(MacroButtonHotKeyManager.HOTKEYS[0]) )
			return buttonLabel;
		else
			return "<html>" + buttonLabel + "<font size=-3> (" + hotKey + ")</font></html>";
	}

	// Override these mouse events in subclasses to specify component specific behavior.
	public void mouseClicked(MouseEvent event) {
	}

	public void mousePressed(MouseEvent event) {
	}

	public void mouseReleased(MouseEvent event)	{
	}

	public void mouseEntered(MouseEvent event) {
	}

	public void mouseExited(MouseEvent event) {
	}

	public void executeButton() {

		if (properties.getCommand() != null) {

			JTextPane commandArea = MapTool.getFrame().getCommandPanel().getCommandTextArea();
			String oldText = commandArea.getText();

			if (properties.getIncludeLabel()) {
				String commandToExecute = properties.getLabel();
				commandArea.setText(commandToExecute);

				MapTool.getFrame().getCommandPanel().commitCommand();

			}

			String commandsToExecute[] = parseMultiLineCommand(properties.getCommand());

			for (String command : commandsToExecute) {
				// If we aren't auto execute, then append the text instead of replace it
				commandArea.setText((!properties.getAutoExecute() ? oldText + " " : "") + command);
				if (properties.getAutoExecute()) {
					MapTool.getFrame().getCommandPanel().commitCommand();
				}
			}

			commandArea.requestFocusInWindow();

		}
	}


	private String[] parseMultiLineCommand(String multiLineCommand) {

		// lookahead for new macro "/" after "\n" to prevent uneccessary splitting.
		String pattern = "\n(?=/)";
		String[] parsedCommand = multiLineCommand.split(pattern);

		return parsedCommand;
	}

	public abstract void savePreferences();
	
	public MacroButtonProperties getProperties() {
		return properties;
	}

	public void reset() {
		properties.reset();
		setColor("");
		setHotKey(MacroButtonHotKeyManager.HOTKEYS[0]);
		setCommand("");
		setMacroLabel(String.valueOf(properties.getIndex()));
		setAutoExecute(true);
		setIncludeLabel(false);
		setText(getButtonText());
	}
}
