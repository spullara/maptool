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

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import net.rptools.clientserver.simple.client.ClientConnection;
import net.rptools.clientserver.simple.server.ServerConnection;
import net.rptools.clientserver.simple.server.ServerObserver;
import net.rptools.maptool.client.swing.InnerPanel;


/**
 */
public class ServerPanel extends InnerPanel implements ServerObserver {

	private ServerConnection server;
	private ServerListModel serverListModel;
	private List<ClientConnection> connectionList;

	private JList connectionListUI;
	
	public ServerPanel(ServerConnection server) {
		
		this.server = server;
		
		connectionList = Collections.synchronizedList(new ArrayList<ClientConnection>());
		serverListModel = new ServerListModel();

		connectionListUI = new JList();
		connectionListUI.setModel(serverListModel);
		
		// UI
        Border outterBorder = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
        Border innerBorder = BorderFactory.createEmptyBorder(10, 2, 2, 2);
        setBorder(BorderFactory.createCompoundBorder(outterBorder, innerBorder));
        setLayout(new BorderLayout());
		
        add(BorderLayout.CENTER, new JScrollPane(connectionListUI));
	}
	
	/* (non-Javadoc)
	 * @see clientserver.simple.server.ServerObserver#connectionAdded(clientserver.simple.client.ClientConnection)
	 */
	public void connectionAdded(ClientConnection conn) {

		serverListModel.add(conn);
	}
	
	/* (non-Javadoc)
	 * @see clientserver.simple.server.ServerObserver#connectionRemoved(clientserver.simple.client.ClientConnection)
	 */
	public void connectionRemoved(ClientConnection conn) {
		
		serverListModel.remove(conn);
	}
	
	private class ServerListModel extends AbstractListModel {
		
		/* (non-Javadoc)
		 * @see javax.swing.ListModel#getElementAt(int)
		 */
		public Object getElementAt(int index) {
			return connectionList.get(index);
		}
		
		/* (non-Javadoc)
		 * @see javax.swing.ListModel#getSize()
		 */
		public int getSize() {
			return connectionList.size();
		}
		
		public void remove(ClientConnection conn) {
			
			int index = connectionList.indexOf(conn);
			connectionList.remove(conn);
			fireIntervalRemoved(this, index, index);
		}
		
		public void add(ClientConnection conn) {
		
			int index = connectionList.size();
			connectionList.add(conn);
			fireIntervalAdded(this, index, index);
		}
	}
}
