/* The MIT License
 * 
 * Copyright (c) 2005 David Rice, Trevor Croft
 * 
 * Permission is hereby granted, free of charge, to any person 
 * obtaining a copy of this software and associated documentation files 
 * (the "Software"), to deal in the Software without restriction, 
 * including without limitation the rights to use, copy, modify, merge, 
 * publish, distribute, sublicense, and/or sell copies of the Software, 
 * and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be 
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS 
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN 
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
 * SOFTWARE.
 */
package net.rptools.maptool.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

import net.rptools.maptool.model.Player;

/**
 * @author trevor
 */
public class PlayerList extends JList {

	private List<Player> playerList = new ArrayList<Player>();

	public PlayerList() {
		setModel(new PlayerListModel(playerList));
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	public void add(Player player) {
		if (!playerList.contains(player)) {
			playerList.add(player);
			
			// LATER: Make this non-anonymous
			Collections.sort(playerList, new Comparator() {
				
				public int compare(Object arg0,Object arg1) {
					return ((Player) arg0).getName().compareToIgnoreCase(((Player) arg1).getName());
				}
			});
		}
		((PlayerListModel) getModel()).update();
	}

	public void remove(Player player) {
		playerList.remove(player);
		((PlayerListModel) getModel()).update();
	}
	
	private static class PlayerListModel extends AbstractListModel {
		
		private List<Player> playerList;

		public PlayerListModel(List<Player> playerList) {
			this.playerList = playerList;
		}
		
		/* (non-Javadoc)
		 * @see javax.swing.ListModel#getElementAt(int)
		 */
		public Object getElementAt(int index) {
			return playerList.get(index);
		}

		/* (non-Javadoc)
		 * @see javax.swing.ListModel#getSize()
		 */
		public int getSize() {
			return playerList.size();
		}

		public void update() {
			fireContentsChanged(this, 0, playerList.size());
		}
	}
}
