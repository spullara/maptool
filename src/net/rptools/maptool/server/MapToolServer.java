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
package net.rptools.maptool.server;

import java.io.IOException;

import net.rptools.clientserver.hessian.server.ServerConnection;
import net.rptools.clientserver.simple.server.ServerObserver;
import net.rptools.maptool.model.Campaign;


/**
 * @author drice
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class MapToolServer {

    private final MapToolServerConnection conn;
    private final ServerMethodHandler handler;
    
    private Campaign campaign;
	private ServerConfig config;
	private ServerPolicy policy;

    public MapToolServer(ServerConfig config, ServerPolicy policy) throws IOException {
    	
        handler = new ServerMethodHandler(this);
        conn = new MapToolServerConnection(this, config.getPort());
        conn.addMessageHandler(handler);

		campaign = new Campaign();
		
		this.config = config;
		this.policy = policy;
    }

    public void addObserver(ServerObserver observer) {
    	
        if (observer != null) {
        	conn.addObserver(observer);
        }
    }
    
    public void removeObserver(ServerObserver observer) {
    	conn.removeObserver(observer);
    }
    
    public ServerConnection getConnection() {
        return conn;
    }
    
	public boolean isPlayerConnected(String id) {
		return conn.getPlayer(id) != null;
	}
	
    public void setCampaign(Campaign campaign) {
        
        // Don't allow null campaigns, but allow the campaign to be cleared out
        if (campaign == null) {
            campaign = new Campaign();
        }
        
        this.campaign = campaign;
    }
    
    public Campaign getCampaign() {
        return campaign;
    }
    
    public ServerPolicy getPolicy() {
    	return policy;
    }
    
    public ServerConfig getConfig() {
    	return config;
    }
    
    ////
    // STANDALONE SERVER
    public static void main(String[] args) throws IOException {
    	
    	// This starts the server thread.
        MapToolServer server = new MapToolServer(new ServerConfig(), new ServerPolicy());
    }
}
