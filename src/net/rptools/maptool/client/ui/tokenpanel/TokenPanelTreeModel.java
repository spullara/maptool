package net.rptools.maptool.client.ui.tokenpanel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.event.ListDataEvent;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.text.View;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import net.rptools.maptool.client.AppUtil;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolUtil;
import net.rptools.maptool.model.ModelChangeEvent;
import net.rptools.maptool.model.ModelChangeListener;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;

public class TokenPanelTreeModel implements TreeModel, ModelChangeListener {

	private String root = "Views";
	
	private Zone zone;
	
    public enum View {
    	PLAYERS("Players"),
		VISIBLE("Visible"),
		GROUPS("Groups"),
		STAMPS("Stamps"),
		CLIPBOARD("Clipboard");
		;
		String displayName;
		private View(String displayName) {
			this.displayName = displayName;
		}
		public String getDisplayName() {
			return displayName;
		}
	}
    
    private JTree tree;
    
    public TokenPanelTreeModel(JTree tree) {
    	this.tree = tree;
		update();
    }
	
    private List<TreeModelListener> listenerList = new ArrayList<TreeModelListener>();

	private Map<View, List<Token>> viewMap = new HashMap<View, List<Token>>();

	private List<View> currentViewList = new ArrayList<View>();

	public Object getRoot() {
		return root;
	}

	public void setZone(Zone zone) {
		if (zone != null) {
			zone.removeModelChangeListener(this);
		}
		this.zone = zone;
		
		if (zone != null) {
			zone.addModelChangeListener(this);
		}
	}
	
	public Object getChild(Object parent, int index) {

		if (parent == root) {
			return currentViewList.get(index);
		}
		
		if (parent instanceof View) {
			return getViewList((View) parent).get(index);
		}
		
		return null;
	}

	public int getChildCount(Object parent) {

		if (parent == root) {
			return currentViewList.size();
		}
		
		if (parent instanceof View) {
			return getViewList((View)parent).size();
		}
		
		return 0;
	}
	
	private List<Token> getViewList(View view) {
		List<Token> list = viewMap.get(view);
		if (list == null) {
			return Collections.emptyList();
		}
		return list;
	}

	public boolean isLeaf(Object node) {
		return node instanceof Token;
	}

	public void valueForPathChanged(TreePath path, Object newValue) {
		// Nothing to do
	}

	public int getIndexOfChild(Object parent, Object child) {
		
		if (parent == root) {
			return currentViewList.indexOf(child);
		}
		
		if (parent instanceof View) {
			getViewList((View) parent).indexOf(child);
		}
		
		return -1;
	}

	public void addTreeModelListener(TreeModelListener l) {
		listenerList.add(l);
	}

	public void removeTreeModelListener(TreeModelListener l) {
		listenerList.remove(l);
	}
	
	public void update() {
		
		currentViewList.clear();
		
        if (zone == null) {
            return;
        }

        // Add in the appropriate views
        addPlayerTokens();
		addVisibleTokens();
		
		Enumeration<TreePath> expandedPaths = tree.getExpandedDescendants(new TreePath(root));
        fireStructureChangedEvent(new TreeModelEvent(this, new Object[]{getRoot()}, 
                new int[] { currentViewList.size() - 1 }, new Object[] {View.VISIBLE}));
        while (expandedPaths != null && expandedPaths.hasMoreElements()) {
        	tree.expandPath(expandedPaths.nextElement());
        }
		
	}

	private void addPlayerTokens() {

		if (!MapTool.getServerPolicy().useStrictTokenManagement()) {
			return;
		}
        currentViewList.add(View.PLAYERS);
        List<Token> tokenList = new ArrayList<Token>();
        viewMap.put(View.PLAYERS, tokenList);

        if (MapTool.getPlayer().isGM()) {
        	tokenList.addAll(zone.getTokens());
        } else {
        	for (Token token : zone.getTokens()) {
        		if (zone.isTokenVisible(token)) {
        			tokenList.add(token);
        		}
        	}
        }

        for (ListIterator<Token> iter = tokenList.listIterator(); iter.hasNext();) {
        	
        	Token token = iter.next();
        	if (token.isStamp() || (!token.hasOwners() && !token.isOwnedByAll())) {
        		iter.remove();
        	}
        	if (!AppUtil.playerOwnsToken(token) && !zone.isTokenVisible(token)) {
        		iter.remove();
        	}
        		
        }
        
        Collections.sort(tokenList, new Comparator<Token>(){
           public int compare(Token o1, Token o2) {
               String lName = o1.getName();
               String rName = o2.getName();

               return lName.compareTo(rName);
            } 
        });
        
	};
	
	private void addVisibleTokens() {
        
        currentViewList.add(View.VISIBLE);
        List<Token> tokenList = new ArrayList<Token>();
        viewMap.put(View.VISIBLE, tokenList);

        if (MapTool.getPlayer().isGM()) {
        	tokenList.addAll(zone.getTokens());
        } else {
        	for (Token token : zone.getTokens()) {
        		if (zone.isTokenVisible(token)) {
        			tokenList.add(token);
        		}
        	}
        }

        for (ListIterator<Token> iter = tokenList.listIterator(); iter.hasNext();) {
        	
        	if (iter.next().isStamp()) {
        		iter.remove();
        	}
        }
        
        Collections.sort(tokenList, new Comparator<Token>(){
           public int compare(Token o1, Token o2) {
               String lName = o1.getName();
               String rName = o2.getName();

               return lName.compareTo(rName);
            } 
        });
        
	}
	
    private void fireStructureChangedEvent(TreeModelEvent e) {
        TreeModelListener[] listeners = listenerList.toArray(new TreeModelListener[listenerList.size()]);
        for (TreeModelListener listener : listeners) {
          listener.treeStructureChanged(e);
        }
    }
      
    private void fireNodesInsertedEvent(TreeModelEvent e) {
        TreeModelListener[] listeners = listenerList.toArray(new TreeModelListener[listenerList.size()]);
        for (TreeModelListener listener : listeners) {
          listener.treeNodesInserted(e);
        }
    }

    ////
    // MODEL CHANGE LISTENER
    public void modelChanged(ModelChangeEvent event) {
    	update();
    }
}
