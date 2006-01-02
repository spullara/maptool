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

public class ServerConfig {
	
    public static final int DEFAULT_PORT = 4444;

	private int port;
	private String gmPassword;
	private String playerPassword;
	private boolean personalServer;
	
	public ServerConfig() {
		/* no op */
	}
	
	public ServerConfig(String gmPassword, String playerPassword) {
		this(gmPassword, playerPassword, DEFAULT_PORT);
	}
	public ServerConfig(String gmPassword, String playerPassword, int port) {
		this.gmPassword = gmPassword;
		this.playerPassword = playerPassword;
		this.port = port;
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
		
		return config;
	}
	
	private boolean safeCompare(String s1, String s2) {

		if (s1 == null) {s1 = "";}
		if (s2 == null) {s2 = "";}
		
		return s1.equals(s2);
	}
}
