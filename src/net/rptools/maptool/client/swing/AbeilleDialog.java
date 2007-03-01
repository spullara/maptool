package net.rptools.maptool.client.swing;

import java.awt.Component;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JDialog;

import com.jeta.forms.components.panel.FormPanel;

public class AbeilleDialog extends JDialog {

	private FormPanel panel;
	
	public AbeilleDialog(String panelForm, Frame owner, String title, boolean modal) {
		super(owner, title, modal);
		setLayout(new GridLayout());

		panel = new FormPanel(panelForm);

		add(panel);
	}

	protected void replaceComponent(String panelName, String name, Component component) {
		panel.getFormAccessor(panelName).replaceBean(name, component);
	}
	
	protected Component getComponent(String name) {
		return panel.getComponentByName(name);
	}
	
}
