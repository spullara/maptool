/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package net.rptools.maptool.server;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.rptools.clientserver.hessian.server.ServerConnection;
import net.rptools.clientserver.simple.server.ServerObserver;
import net.rptools.maptool.client.ClientCommand;
import net.rptools.maptool.model.Player;

import org.apache.log4j.Logger;

/**
 * @author trevor
 */
public class MapToolServerConnection extends ServerConnection implements ServerObserver {
	private static final Logger log = Logger.getLogger(MapToolServerConnection.class);
	private final Map<String, Player> playerMap = new ConcurrentHashMap<String, Player>();
	private final MapToolServer server;

	public MapToolServerConnection(MapToolServer server, int port) throws IOException {
		super(port);
		this.server = server;
		addObserver(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.rptools.clientserver.simple.server.ServerConnection#handleConnectionHandshake(java.net.Socket)
	 */
	@Override
	public boolean handleConnectionHandshake(String id, Socket socket) {
		try {
			Player player = Handshake.receiveHandshake(server, socket);

			if (player != null) {
				playerMap.put(id.toUpperCase(), player);
				return true;
			}
		} catch (IOException ioe) {
			log.error("Handshake failure: " + ioe, ioe);
		}
		return false;
	}

	public Player getPlayer(String id) {
		for (Player player : playerMap.values()) {
			if (player.getName().equalsIgnoreCase(id)) {
				return player;
			}
		}
		return null;
	}

	public String getConnectionId(String playerId) {
		for (Map.Entry<String, Player> entry : playerMap.entrySet()) {
			if (entry.getValue().getName().equalsIgnoreCase(playerId)) {
				return entry.getKey();
			}
		}
		return null;
	}

	////
	// SERVER OBSERVER

	/**
	 * Handle late connections
	 */
	public void connectionAdded(net.rptools.clientserver.simple.client.ClientConnection conn) {
		server.configureClientConnection(conn);

		Player player = playerMap.get(conn.getId().toUpperCase());
		for (String id : playerMap.keySet()) {
			server.getConnection().callMethod(conn.getId(), ClientCommand.COMMAND.playerConnected.name(), playerMap.get(id));
		}
		server.getConnection().broadcastCallMethod(ClientCommand.COMMAND.playerConnected.name(), player);
//     if (!server.isHostId(player.getName())) {
		// Don't bother sending the campaign file if we're hosting it ourselves
		server.getConnection().callMethod(conn.getId(), ClientCommand.COMMAND.setCampaign.name(), server.getCampaign());
//     }
	}

	public void connectionRemoved(net.rptools.clientserver.simple.client.ClientConnection conn) {
		server.releaseClientConnection(conn.getId());
		server.getConnection().broadcastCallMethod(new String[] { conn.getId() }, ClientCommand.COMMAND.playerDisconnected.name(), playerMap.get(conn.getId().toUpperCase()));
		playerMap.remove(conn.getId().toUpperCase());
	}
}
