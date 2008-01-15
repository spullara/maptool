package net.rptools.maptool.client.ui;

import java.util.prefs.Preferences;

import net.rptools.maptool.client.AppConstants;
import net.rptools.maptool.model.Player;
import net.rptools.maptool.server.ServerConfig;

public class StartServerDialogPreferences {

    private static Preferences prefs = Preferences.userRoot().node(AppConstants.APP_NAME + "/prefs/server");        

    private static final String KEY_USERNAME = "name";
    private static final String KEY_ROLE = "playerRole";
    private static final String KEY_PORT = "port";
    private static final String KEY_GM_PASSWORD = "gmPassword";
    private static final String KEY_PLAYER_PASSWORD = "playerPassword";
    private static final String KEY_STRICT_TOKEN_OWNERSHIP = "strictTokenOwnership";
    private static final String KEY_REGISTER_SERVER = "registerServer";
    private static final String KEY_RPTOOLS_NAME = "rptoolsName";
    private static final String KEY_RPTOOLS_PRIVATE = "rptoolsPrivate";
    private static final String KEY_PLAYERS_CAN_REVEAL_VISION = "playersCanRevealVisionCheckbox";
    private static final String KEY_USE_INDIVIDUAL_VIEWS = "useIndividualViews";
    private static final String KEY_USE_UPNP = "useUPnP";
    
    public Player.Role getRole () {
    	return Player.Role.valueOf(prefs.get(KEY_ROLE, Player.Role.GM.name()));
    }
    
    public void setRole(Player.Role role) {
    	prefs.put(KEY_ROLE, role.name());
    }

    public String getUsername() {
    	return prefs.get(KEY_USERNAME, "");
    }
    
    public void setUsername(String name) {
    	prefs.put(KEY_USERNAME, name);
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
    
    public int getPort() {
    	return prefs.getInt(KEY_PORT, ServerConfig.DEFAULT_PORT);
    }
    
    public void setPort(int port) {
    	prefs.putInt(KEY_PORT, port);
    }
    
    public boolean getUseStrictTokenOwnership() {
    	return prefs.getBoolean(KEY_STRICT_TOKEN_OWNERSHIP, false);
    }
    
    public void setUseStrictTokenOwnership(boolean use) {
    	prefs.putBoolean(KEY_STRICT_TOKEN_OWNERSHIP, use);
    }

    public boolean registerServer() {
    	return prefs.getBoolean(KEY_REGISTER_SERVER, false);
    }
    
    public void setRegisterServer(boolean register) {
    	prefs.putBoolean(KEY_REGISTER_SERVER, register);
    }
    
    public void setRPToolsName(String name) {
    	prefs.put(KEY_RPTOOLS_NAME, name);
    }
    
    public String getRPToolsName() {
    	return prefs.get(KEY_RPTOOLS_NAME, "");
    }
    
    public void setRPToolsPrivate(boolean flag) {
    	prefs.putBoolean(KEY_RPTOOLS_PRIVATE, flag);
    }
    
    public boolean getRPToolsPrivate() {
    	return prefs.getBoolean(KEY_RPTOOLS_PRIVATE, false);
    }
    
    public void setPlayersCanRevealVision(boolean flag) {
    	prefs.putBoolean(KEY_PLAYERS_CAN_REVEAL_VISION, flag);
    }
    
    public boolean getPlayersCanRevealVision() {
    	return prefs.getBoolean(KEY_PLAYERS_CAN_REVEAL_VISION, false);
    }
    
    public void setUseIndividualViews(boolean flag) {
    	prefs.putBoolean(KEY_USE_INDIVIDUAL_VIEWS, flag);
    }
    
    public boolean getUseIndividualViews() {
    	return prefs.getBoolean(KEY_USE_INDIVIDUAL_VIEWS, false);
    }
    
    public void setUseUPnP(boolean op) {
    	prefs.putBoolean(KEY_USE_UPNP, op);
    }
    
    public boolean getUseUPnP() {
    	return prefs.getBoolean(KEY_USE_UPNP, false);
    }
}
