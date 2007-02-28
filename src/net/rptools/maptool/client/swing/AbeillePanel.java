package net.rptools.maptool.client.swing;

import java.awt.Component;
import java.awt.GridLayout;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;

import com.jeta.forms.components.panel.FormPanel;

public class AbeillePanel extends JPanel {

	private FormPanel panel;
	private Set<Component> initializedSet = new HashSet<Component>();
	
	public AbeillePanel(String panelForm) {
		setLayout(new GridLayout());

		panel = new FormPanel(panelForm);

		add(panel);
	}

	protected void replaceComponent(String panelName, String name, Component component) {
		panel.getFormAccessor(panelName).replaceBean(name, component);
		panel.reset();
	}
	
	protected Component getComponent(String name) {
		return panel.getComponentByName(name);
	}
	
	/**
	 * Returns true if the component needs to be initialized.  Will only return 'true' 
	 * the first time this is called for this component
	 */
	protected boolean initialize(Component c) {
		boolean initialized = initializedSet.contains(c);
		if (!initialized) {
			initializedSet.add(c);
		}
		return !initialized;
	}
}
