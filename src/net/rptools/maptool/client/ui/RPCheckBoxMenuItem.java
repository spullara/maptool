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
