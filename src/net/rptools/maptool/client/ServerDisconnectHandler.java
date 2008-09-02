/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package net.rptools.maptool.client;

import java.io.IOException;

import net.rptools.clientserver.simple.AbstractConnection;
import net.rptools.clientserver.simple.DisconnectHandler;
import net.rptools.maptool.model.Campaign;
import net.rptools.maptool.model.CampaignFactory;

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
    			MapTool.startPersonalServer(CampaignFactory.createBasicCampaign());
    		} catch (IOException ioe) {
    			MapTool.showError("Could not restart personal server");
    		}
    	}
        
    	disconnectExpected = false;
    }
}
