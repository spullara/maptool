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
package net.rptools.maptool.client.ui.io;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

/**
 * @author crash
 * 
 */
@SuppressWarnings("serial")
class CustomTreeCellRenderer extends JCheckBox implements TreeCellRenderer {
	DefaultMutableTreeNode node;
	MaptoolNode mtnode;

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {

		node = (DefaultMutableTreeNode) value;
		mtnode = (MaptoolNode) node.getUserObject();
		setText(mtnode.toString());
		setBackground(tree.getBackground());
		setEnabled(tree.isEnabled());
		setComponentOrientation(tree.getComponentOrientation());
		return this;
	}

	protected boolean isFirstLevel() {
		return node.getParent() == node.getRoot();
	}
}
