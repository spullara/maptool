package net.rptools.maptool.client.ui;

import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;

import net.rptools.maptool.client.AppActions.ClientAction;

/** 
 * This little baby will keep the menu items selected state intact.  Not the most elegant, but works
 */
public class RPCheckBoxMenuItem extends JCheckBoxMenuItem{

	public RPCheckBoxMenuItem(Action action) {
		super(action);
		
		addPropertyChangeListener("ancestor", new PropertyChangeListener() {
			public void propertyChange(java.beans.PropertyChangeEvent evt) {

				Action action = getAction();
				if (action instanceof ClientAction) {
					setSelected(((ClientAction)action).isSelected());
				}
			}
		});
	}
	
	
}
