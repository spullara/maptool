package net.rptools.maptool.client.ui.tokenpanel;

import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import net.rptools.maptool.model.Token;

public class TokenPanelTreeModel implements TreeModel {

	private Object root;

	private List<Token> visibleTokenList;
	private List<Token> tokenGroupList;
	private List<Token> stampList;
	private List<Token> clipboardList;
	
	public Object getRoot() {
		return root;
	}

	public Object getChild(Object parent, int index) {

		
		return null;
	}

	public int getChildCount(Object parent) {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isLeaf(Object node) {
		// TODO Auto-generated method stub
		return false;
	}

	public void valueForPathChanged(TreePath path, Object newValue) {
		// TODO Auto-generated method stub

	}

	public int getIndexOfChild(Object parent, Object child) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void addTreeModelListener(TreeModelListener l) {
		// TODO Auto-generated method stub

	}

	public void removeTreeModelListener(TreeModelListener l) {
		// TODO Auto-generated method stub

	}
	
	
	public static void main(String[] args) {
		
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setBounds(100, 100, 200, 300);
		
		JTree tree = new JTree();
		tree.setModel(new TokenPanelTreeModel());
		
		f.add(tree);
		
		f.setVisible(true);
	}

}
