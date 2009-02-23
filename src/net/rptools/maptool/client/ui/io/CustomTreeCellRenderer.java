/**
 * 
 */
package net.rptools.maptool.client.ui.io;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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