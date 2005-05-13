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

import java.io.IOException;
import java.net.Socket;

import net.rptools.clientserver.hessian.client.ClientConnection;
import net.rptools.maptool.model.Player;
import net.rptools.maptool.server.Handshake;

/**
 * @author trevor
 */
public class MapToolConnection extends ClientConnection {

	private Player player;
	
	public MapToolConnection(String host, int port, Player player) throws IOException {
		super(host, port, null);
		
		this.player = player;
	}
	
	public MapToolConnection(Socket socket, Player player) throws IOException {
		super(socket, null);
		
		this.player = player;
	}
	
	/* (non-Javadoc)
	 * @see net.rptools.clientserver.simple.client.ClientConnection#sendHandshake(java.net.Socket)
	 */
	public void sendHandshake(Socket s) throws IOException {
		
		Handshake.sendHandshake(player, s);
	}
}
