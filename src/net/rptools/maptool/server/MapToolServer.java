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

import net.rptools.clientserver.hessian.client.ClientConnection;
import net.rptools.clientserver.hessian.server.ServerConnection;
import net.rptools.clientserver.simple.server.ServerObserver;
import net.rptools.maptool.client.MapToolClient;
import net.rptools.maptool.model.Campaign;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.drawing.Drawable;
import net.rptools.maptool.model.drawing.Pen;


/**
 * @author drice
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class MapToolServer {
    public static enum COMMANDS {
        getCampaign, 
        setCampaign, 
        getZone, 
        putZone, 
        removeZone, 
        putAsset, 
        getAsset,
        removeAsset, 
        putToken, 
        removeToken, 
        draw,
        setZoneGridSize
    };

    public static final int DEFAULT_PORT = 4444;

    private final MapToolServerConnection conn;
    private final ServerMethodHandler handler;
    
    private Campaign campaign;

    public MapToolServer(Campaign campaign, int port) throws IOException {
    	
        handler = new ServerMethodHandler(this);
        conn = new MapToolServerConnection(this, port);
        conn.addMessageHandler(handler);
        
        this.campaign = campaign;
        
    }

    public void addObserver(ServerObserver observer) {
    	
        if (observer != null) {
        	conn.addObserver(observer);
        }
    }
    
    public void removeObserver(ServerObserver observer) {
    	conn.removeObserver(observer);
    }
    
    public MapToolServer() throws IOException {
    	this(new Campaign(), DEFAULT_PORT);
    }

    public ServerConnection getConnection() {
        return conn;
    }
    
    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }
    
    public Campaign getCampaign() {
        return campaign;
    }
    
    public void draw(GUID overlayId, Pen pen, Drawable d) {
    }

    ////
    // STANDALONE SERVER
    public static void main(String[] args) throws IOException {
    	
    	// This starts the server thread.
        MapToolServer server = new MapToolServer();
    }
}
