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
import java.net.ServerSocket;
import java.util.Random;

public class ServerConfig {
	
    public static final int DEFAULT_PORT = 51234;
    
    public static final int PORT_RANGE_START = 4000;
    public static final int PORT_RANGE_END = 20000;

	private int port;
	private String gmPassword;
	private String playerPassword;
	private boolean personalServer;
	private boolean registerServer;
	private String serverPassword;
	private String serverName;
	
	public ServerConfig() {
		/* no op */
	}
	
	public ServerConfig(String gmPassword, String playerPassword, int port, boolean registerServer, String serverName, String serverPassword) {
		this.gmPassword = gmPassword;
		this.playerPassword = playerPassword;
		this.port = port;
		this.registerServer = registerServer;
		this.serverName = serverName;
		this.serverPassword = serverPassword;
	}
	
	public boolean isServerRegistered() {
		return registerServer;
	}
	
	public String getServerName() {
		return serverName;
	}
	
	public String getServerPassword() {
		return serverPassword;
	}
	
	public boolean gmPasswordMatches(String password) {
		return safeCompare(gmPassword, password);
	}
	
	public boolean playerPasswordMatches(String password) {
		return safeCompare(playerPassword, password);
	}

	public boolean isPersonalServer() {
		return personalServer;
	}
	
	public int getPort() {
		return port;
	}

	public static ServerConfig createPersonalServerConfig() {
		ServerConfig config = new ServerConfig();
		config.personalServer = true;
		config.port = findOpenPort(PORT_RANGE_START, PORT_RANGE_END);
		
		return config;
	}
	
	private static Random r = new Random();
	private static int findOpenPort(int rangeLow, int rangeHigh) {
		
		// Presumably there will always be at least one open port between low and high
		while (true) {
			
			ServerSocket ss = null;
			try {
				int port = rangeLow + (int)((rangeHigh - rangeLow) * r.nextFloat());
				ss = new ServerSocket(port);
				
				// This port was open before we took it, so we'll just close it and use this one
				// LATER: This isn't super exact, it's conceivable that another process will take 
				// the port between our closing it and the server opening it.  But that's the best
				// we can do at the moment until the server is refactored.
				return port;
			} catch (Exception e) {
				// Just keep trying
			} finally {
				if (ss != null) {
					try {
						ss.close();
					} catch (IOException ioe) {
						// No big deal
						ioe.printStackTrace();
					}
				}
			}
		}
	}
	
	private static boolean safeCompare(String s1, String s2) {

		if (s1 == null) {s1 = "";}
		if (s2 == null) {s2 = "";}
		
		return s1.equals(s2);
	}
}
