package net.rptools.maptool.client;

import javax.swing.JOptionPane;

import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.Token;
import net.rptools.parser.MapVariableResolver;
import net.rptools.parser.VariableModifiers;

public class MapToolVariableResolver extends MapVariableResolver {

	@Override
	public boolean containsVariable(String name, VariableModifiers mods) {

		// If we don't have the value then we'll prompt for it
		return true;
	}

	@Override
	public Object getVariable(String name, VariableModifiers mods) {

		Object result = null;
		Token token = getTokenInContext();
		if (token != null) {
			
			if (token.getPropertyNames().contains(name)) {
				
				result = token.getProperty(name);
			}
		}
		
		// Default
		if (result == null) {
			result = super.getVariable(name, mods);
		}

		// Prompt
		if (result == null) {
			result = JOptionPane.showInputDialog(MapTool.getFrame(), "Value for: " + name, "Input Value", JOptionPane.QUESTION_MESSAGE);
		}
		
		return result;
	}
	
	@Override
	public void setVariable(String arg0, VariableModifiers arg1, Object arg2) {

		super.setVariable(arg0, arg1, arg2);
	}

	private Token getTokenInContext() {
		
		ZoneRenderer renderer = MapTool.getFrame().getCurrentZoneRenderer();
		if (renderer == null) {
			return null;
		}
		
		return renderer.getZone().resolveToken(MapTool.getFrame().getCommandPanel().getIdentity());
	}
}