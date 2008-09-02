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
	public boolean sendHandshake(Socket s) throws IOException {
		
		Handshake.Response response = Handshake.sendHandshake(new Handshake.Request(player.getName(), player.getPassword(), player.getRole(), MapTool.getVersion()), s);

		if (response.code != Handshake.Code.OK) {
			MapTool.showError("ERROR: " + response.message);
			return false;
		}
		
		boolean result = response.code == Handshake.Code.OK;
		if (result) {
			MapTool.setServerPolicy(response.policy);
		}
		
		return result;
	}
}
