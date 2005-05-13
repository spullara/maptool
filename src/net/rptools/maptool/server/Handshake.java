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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;

import net.rptools.maptool.model.Player;

/**
 * @author trevor
 */
public class Handshake {

	private interface Message {
		public static final int OK = 1;
		public static final int FAILURE = 2;
	}
	
	/**
	 * Server side of the handshake
	 */
	public static Player receiveHandshake(Socket s) throws IOException {
		
		Writer out = new OutputStreamWriter(s.getOutputStream());
		BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

		// NAME
		String name = in.readLine();
		out.write(Message.OK);
		out.flush();
		
		// ROLE
		int role = in.read();
		out.write(Message.OK);
		out.flush();
		
		// STATUS
//		out.write(Message.OK);
//		out.flush();

//		if (in.read() != Message.OK) {
//			throw new IOException ("Failed handshake");
//		}		
		return new Player(name, role);
	}

	/**
	 * Client side of the handshake
	 */
	public static void sendHandshake(Player player, Socket s) throws IOException {
		
		Writer out = new OutputStreamWriter(s.getOutputStream());
		Reader in = new InputStreamReader(s.getInputStream());
		
		// NAME
		out.append(player.getName()).append("\n").flush();
		if (in.read() != Message.OK) {
			throw new IOException ("Name already in use");
		}
		
		// ROLE
		out.write(player.getRole());
		out.flush();
		if (in.read() != Message.OK) {
			throw new IOException ("Invalid role");
		}
		
		// STATUS
//		if (in.read() != Message.OK) {
//			throw new IOException ("Failed handshake");
//		}
		
	}
}
