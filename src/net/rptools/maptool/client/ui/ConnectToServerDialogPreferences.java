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
package net.rptools.maptool.client.ui;

import java.util.prefs.Preferences;

import net.rptools.maptool.client.AppConstants;
import net.rptools.maptool.model.Player;
import net.rptools.maptool.server.ServerConfig;

public class ConnectToServerDialogPreferences {

    private static Preferences prefs = Preferences.userRoot().node(AppConstants.APP_NAME + "/prefs/connect");        

    private static final String KEY_USERNAME = "name";
    private static final String KEY_ROLE = "playerRole";
    private static final String KEY_HOST = "host";
    private static final String KEY_PORT = "port";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_TAB = "tab";
    private static final String KEY_SERVER_NAME = "serverName";
    
    public String getUsername() {
    	return prefs.get(KEY_USERNAME, "");
    }
    
    public void setUsername(String name) {
    	prefs.put(KEY_USERNAME, name);
    }
    
    public Player.Role getRole () {
    	return Player.Role.valueOf(prefs.get(KEY_ROLE, Player.Role.PLAYER.name()));
    }
    
    public void setRole(Player.Role role) {
    	prefs.put(KEY_ROLE, role.name());
    }

    public void setHost(String host) {
    	prefs.put(KEY_HOST, host);
    }
    
    public String getHost() {
    	return prefs.get(KEY_HOST, "");
    }
    
    public int getPort() {
    	return prefs.getInt(KEY_PORT, ServerConfig.DEFAULT_PORT);
    }
    
    public void setPort(int port) {
    	prefs.putInt(KEY_PORT, port);
    }
    
    public void setPassword(String password) {
    	prefs.put(KEY_PASSWORD, password);
    }
    
    public String getPassword() {
    	return prefs.get(KEY_PASSWORD, "");
    }

    public int getTab() {
    	return prefs.getInt(KEY_TAB, 0);
    }
    
    public void setTab(int tab) {
    	prefs.putInt(KEY_TAB, tab);
    }
    
    public void setServerName(String host) {
    	prefs.put(KEY_SERVER_NAME, host);
    }
    
    public String getServerName() {
    	return prefs.get(KEY_SERVER_NAME, "");
    }
}
