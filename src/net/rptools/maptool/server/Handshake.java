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
 */package net.rptools.maptool.server;

import java.io.IOException;
import java.net.Socket;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.Player;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

/**
 * @author trevor
 */
public class Handshake {

	public interface Code {
		public static final int UNKNOWN = 0;
		public static final int OK = 1;
		public static final int ERROR = 2;
	}
	
	
	
	/**
	 * Server side of the handshake
	 */
	public static Player receiveHandshake(MapToolServer server, Socket s) throws IOException {
		
		// TODO: remove server config as a param
		ServerConfig config = server.getConfig();
		
		HessianInput input = new HessianInput(s.getInputStream());
		HessianOutput output = new HessianOutput(s.getOutputStream());

		Request request = (Request) input.readObject();
		
		Response response = new Response();
		response.code = Code.OK;

		boolean passwordMatches = request.role == Player.Role.GM ? config.gmPasswordMatches(request.password) : config.playerPasswordMatches(request.password);
		if (!passwordMatches) {
			
			// PASSWORD
			response.code = Code.ERROR;
			response.message = "Just testing";
		} else if (server.isPlayerConnected(request.name)) {
			
			// UNIQUE NAME
			response.code = Code.ERROR;
			response.message = "That name is already in use";
		} else if (!MapTool.getVersion().equals(request.version)) {
			
			// CORRECT VERSION
			response.code = Code.ERROR;
			response.message = "Invalid version.  Client:" + request.version + " Server:" + MapTool.getVersion();
		}
		
		output.writeObject(response);

		return response.code == Code.OK ? new Player(request.name, request.role) : null;
	}

	/**
	 * Client side of the handshake
	 */
	public static Response sendHandshake(Request request, Socket s) throws IOException {
		
		HessianInput input = new HessianInput(s.getInputStream());
		HessianOutput output = new HessianOutput(s.getOutputStream());
		
		output.writeObject(request);
		
		return (Response) input.readObject();
	}
	
	public static class Request {
		
		public String name;
		public String password;
		public int role;
		public String version;
		
		public Request(String name, String password, int role, String version) {
			this.name = name;
			this.password = password;
			this.role = role;
			this.version = version;
		}
	}
	
	public static class Response {

		public int code;
		public String message;
	}
}
