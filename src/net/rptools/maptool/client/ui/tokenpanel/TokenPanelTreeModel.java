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
package net.rptools.maptool.client.ui.tokenpanel;

import java.awt.EventQueue;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.ModelChangeEvent;
import net.rptools.maptool.model.ModelChangeListener;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.util.GraphicsUtil;

public class TokenPanelTreeModel implements TreeModel, ModelChangeListener {

    public enum View {
		TOKENS("Tokens", Zone.Layer.TOKEN, false, false),
    	PLAYERS("Players", Zone.Layer.TOKEN, false, false),
		GROUPS("Groups", Zone.Layer.TOKEN, false, false),
		GM("GM", Zone.Layer.GM, false, true),
		OBJECTS("Objects", Zone.Layer.OBJECT, false, true),
		BACKGROUND("Background", Zone.Layer.BACKGROUND, false, true),
		CLIPBOARD("Clipboard", Zone.Layer.TOKEN, false, true),
		LIGHT_SOURCES("Light Sources", null, false, false);

		String displayName;
		boolean required;
		Zone.Layer layer;
		boolean isAdmin;

		private View(String displayName, Zone.Layer layer, boolean required, boolean isAdmin) {
			this.displayName = displayName;
			this.required = required;
			this.layer = layer;
			this.isAdmin = isAdmin;
		}
		public String getDisplayName() {
			return displayName;
		}
		public Zone.Layer getLayer() {
			return layer;
		}
		public boolean isRequired () {
			return required;
		}
	}

    private List<TokenFilter> filterList = new ArrayList<TokenFilter>();
    
	private String root = "Views";
	
	private Zone zone;
	
    private JTree tree;
    
    private volatile boolean updatePending = false;
    
    public TokenPanelTreeModel(JTree tree) {
    	this.tree = tree;
		update();

		// It would be useful to have this list be static, but it's really not that big of a memory footprint
		// TODO: refactor to more tightly couple the View enum and the corresponding filter
    	filterList.add(new TokenTokenFilter());
    	filterList.add(new PlayerTokenFilter());
    	filterList.add(new GMFilter());
    	filterList.add(new ObjectFilter());
    	filterList.add(new BackgroundFilter());
    	filterList.add(new LightSourceFilter());
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
		update();
		
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
		// better solution would be to use a timeout to invoke the internal update to give more
		// token events the chance to arrive, but in this case EventQueue overload will 
		// manage to delay it quite nicely
		
		if ( !updatePending ) {
			updatePending = true;
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					updatePending = false;
			    	updateInternal();
				}
			});
		}
		
	}
	
	private void updateInternal() {

		currentViewList.clear();
		viewMap.clear();
		
        // Plan to show all of the views in order to keep the 
        // order
        for (TokenFilter filter : filterList) {
    		if (filter.view.isAdmin && !MapTool.getPlayer().isGM()) {
    			continue;
    		}
    		
        	currentViewList.add(filter.view);
        }
        
        // Add in the appropriate views
        List<Token> tokenList = new ArrayList<Token>();
        if (zone != null) {
        	tokenList = zone.getAllTokens();
        }
        for (Token token : tokenList) {
        	for (TokenFilter filter : filterList) {
        		filter.filter(token);
        	}
        }

        // Clear out any view without any tokens
        for (ListIterator<View> viewIter = currentViewList.listIterator(); viewIter.hasNext();) {
        	View view = viewIter.next();
        	
        	if (!view.isRequired() && (viewMap.get(view) == null || viewMap.get(view).size() == 0)) {
        		viewIter.remove();
        	}
        }
        
        // Sort
        for (List<Token> tokens : viewMap.values()) {
        	Collections.sort(tokens, NAME_AND_STATE_COMPARATOR);
        }

        // Keep the expanded branches consistent
		Enumeration<TreePath> expandedPaths = tree.getExpandedDescendants(new TreePath(root));
        fireStructureChangedEvent(new TreeModelEvent(this, new Object[]{getRoot()}, 
                new int[] { currentViewList.size() - 1 }, new Object[] {View.TOKENS}));
        while (expandedPaths != null && expandedPaths.hasMoreElements()) {
        	tree.expandPath(expandedPaths.nextElement());
        }
		
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

    private abstract class TokenFilter {
    	
    	private View view;
    	
    	public TokenFilter(View view) {
    		this.view = view;
    	}
    	
    	private void filter(Token token) {
    		
    		if (accept(token)) {
    			List<Token> tokenList = viewMap.get(view);
    			if (tokenList == null) {
    				tokenList = new ArrayList<Token>();
    				viewMap.put(view, tokenList);
    			}
    			
    			tokenList.add(token);
    		}
    	}
    	
    	protected abstract boolean accept(Token token);
    }
    
    private class PlayerTokenFilter extends TokenFilter {
    	
    	public PlayerTokenFilter() {
    		super(View.PLAYERS);
    	}
    	
    	@Override
    	protected boolean accept(Token token) {
    		if (MapTool.getServerPolicy().isUseIndividualViews()) {
    			if (MapTool.getPlayer().isGM() || token.isOwner(MapTool.getPlayer().getName())) {
    				return token.getType() == Token.Type.PC;
    			} else {
    				return false;
    			}
    		} else {
    			return token.getType() == Token.Type.PC;
    		}
    	}
    }
    
    private class ObjectFilter extends TokenFilter {
    	
    	public ObjectFilter() {
    		super(View.OBJECTS);
    	}
    	
    	@Override
    	protected boolean accept(Token token) {
    		return MapTool.getPlayer().isGM() && token.isObjectStamp();
    	}
    }
    
    private class GMFilter extends TokenFilter {
    	
    	public GMFilter() {
    		super(View.GM);
    	}
    	
    	@Override
    	protected boolean accept(Token token) {
    		return MapTool.getPlayer().isGM() && token.isGMStamp();
    	}
    }
    
    private class BackgroundFilter extends TokenFilter {
    	
    	public BackgroundFilter() {
    		super(View.BACKGROUND);
    	}
    	
    	@Override
    	protected boolean accept(Token token) {
    		return MapTool.getPlayer().isGM() && token.isBackgroundStamp();
    	}
    }
    
    
    private class LightSourceFilter extends TokenFilter {
    	public LightSourceFilter() {
    		super(View.LIGHT_SOURCES);
    	}
    	
    	@Override
    	protected boolean accept(Token token) {
    		if (MapTool.getPlayer().isGM()) {
    			if (token.getLightSources().size() > 0) {
    				return true;
    			}
    		} else if (token.isOwner(MapTool.getPlayer().getName())) {
    			if (token.getLightSources().size() > 0) {
    				return true;
    			}
    		}
    		return false;
    	}
    }
    private class TokenTokenFilter extends TokenFilter {
    	
    	public TokenTokenFilter() {
    		super(View.TOKENS);
    	}
    	
    	@Override
    	protected boolean accept(Token token) {
    		
    		ZoneRenderer renderer = MapTool.getFrame().getCurrentZoneRenderer();
    		if (renderer == null) {
    			return false;
    		}

    		if (token.isStamp() || token.getType() == Token.Type.PC) {
    			return false;
    		}
    		
    		if (MapTool.getPlayer().isGM()) {
    			return true;
    		}
    		
    		// Check visibility
//			Area visibleArea = renderer.getVisibleScreenArea();
//			if (visibleArea != null) {
//				Area tokenBounds = renderer.getTokenBounds(token);
//				if (tokenBounds == null || !GraphicsUtil.intersects(visibleArea, tokenBounds)) {
//					return false;
//				}
//			}

    		if (token.isOwner(MapTool.getPlayer().getName())) {
    			return true;
    		}
    		
    		if (MapTool.getServerPolicy().useStrictTokenManagement()) {
    			return false;
    		}
    		    		
        	return zone.isTokenVisible(token);
    	}
    }
    ////
    // MODEL CHANGE LISTENER
    public void modelChanged(ModelChangeEvent event) {
    	update();
    }
    
    ////
    // SORTING
	private static final Comparator<Token> NAME_AND_STATE_COMPARATOR = new Comparator<Token>() {
		public int compare(Token o1, Token o2) {
			if (o1.isVisible() != o2.isVisible()) {
				return o1.isVisible() ? -1 : 1;
			}
			
			return o1.getName().compareToIgnoreCase(o2.getName());
		}
	};

}
