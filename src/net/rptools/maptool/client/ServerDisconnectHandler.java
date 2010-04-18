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

    		// hide map so player doesn't get a brief GM view
    		MapTool.getFrame().setCurrentZoneRenderer(null);

    		try {
    			MapTool.startPersonalServer(CampaignFactory.createBasicCampaign());
    		} catch (IOException ioe) {
    			MapTool.showError("Could not restart personal server");
    		}
    	} else if (!MapTool.isPersonalServer() && !MapTool.isHostingServer()) {
    		// expected disconnect from someone else's server
    		
    		// hide map so player doesn't get a brief GM view
    		MapTool.getFrame().setCurrentZoneRenderer(null);
    	}
        
    	disconnectExpected = false;
    }
}
