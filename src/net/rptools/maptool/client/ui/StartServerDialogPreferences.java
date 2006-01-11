package net.rptools.maptool.client.ui;

import java.util.prefs.Preferences;

import net.rptools.maptool.client.AppConstants;
import net.rptools.maptool.server.ServerConfig;

public class StartServerDialogPreferences {

    private static Preferences prefs = Preferences.userRoot().node(AppConstants.APP_NAME + "/prefs/server");        

    private static final String KEY_USERNAME = "name";
    //private static final String KEY_ROLE = "role";
    private static final String KEY_PORT = "port";
    private static final String KEY_GM_PASSWORD = "gmPassword";
    private static final String KEY_PLAYER_PASSWORD = "playerPassword";
    private static final String KEY_USE_GM_PASSWORD = "useGMPassword";
    private static final String KEY_USE_PLAYER_PASSWORD = "usePlayerPassword";
    private static final String KEY_STRICT_TOKEN_MOVEMENT = "strictTokenMovement";
    
    public String getUsername() {
    	return prefs.get(KEY_USERNAME, "");
    }
    
    public void setUsername(String name) {
    	prefs.put(KEY_USERNAME, name);
    }
    
    public boolean getUseGMPassword() {
    	return prefs.getBoolean(KEY_USE_GM_PASSWORD, false);
    }
    
    public void setUseGMPassword(boolean use) {
    	prefs.putBoolean(KEY_USE_GM_PASSWORD, use);
    }
    
    public void setGMPassword(String password) {
    	prefs.put(KEY_GM_PASSWORD, password);
    }
    
    public String getGMPassword() {
    	return prefs.get(KEY_GM_PASSWORD, "");
    }
    
    public void setPlayerPassword(String password) {
    	prefs.put(KEY_PLAYER_PASSWORD, password);
    }
    
    public String getPlayerPassword() {
    	return prefs.get(KEY_PLAYER_PASSWORD, "");
    }
    
    public boolean getUsePlayerPassword() {
    	return prefs.getBoolean(KEY_USE_PLAYER_PASSWORD, false);
    }
    
    public void setUsePlayerPassword(boolean use) {
    	prefs.putBoolean(KEY_USE_PLAYER_PASSWORD, use);
    }
    
//    public int getRole () {
//    	return prefs.getInt(KEY_ROLE, Player.Role.PLAYER);
//    }
//    
//    public void setRole(int role) {
//    	prefs.putInt(KEY_ROLE, role);
//    }
//
    
    public int getPort() {
    	return prefs.getInt(KEY_PORT, ServerConfig.DEFAULT_PORT);
    }
    
    public void setPort(int port) {
    	prefs.putInt(KEY_PORT, port);
    }
    
    public boolean useStrictTokenMovement() {
    	return prefs.getBoolean(KEY_STRICT_TOKEN_MOVEMENT, false);
    }
    
    public void setStrictTokenMovement(boolean use) {
    	prefs.putBoolean(KEY_STRICT_TOKEN_MOVEMENT, use);
    }
}
