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

import net.rptools.clientserver.simple.AbstractConnection;
import net.rptools.clientserver.simple.DisconnectHandler;
import net.rptools.maptool.model.Campaign;

/**
 * This class handles when the server inexplicably disconnects
 */
public class ServerDisconnectHandler implements DisconnectHandler {

	// TODO: This is a temporary hack until I can come up with a cleaner mechanism
	public static boolean disconnectExpected;
    public void handleDisconnect(AbstractConnection arg0) {
    	
        // Update internal state
        MapTool.disconnect();

        // TODO: attempt to reconnect if this was unexpected
    	if (!disconnectExpected) {
    		MapTool.showError("Server has disconnected.");

    		try {
    			MapTool.startPersonalServer(new Campaign());
    		} catch (IOException ioe) {
    			MapTool.showError("Could not restart personal server");
    		}
    	}
        
    	disconnectExpected = false;
    }
}
