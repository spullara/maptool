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

import java.io.File;

import net.rptools.maptool.client.ui.zone.ZoneView;
import net.rptools.maptool.model.Player;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;

public class AppUtil {

    private static final String USER_HOME;
    
    static {
        
        USER_HOME = System.getProperty("user.home");
        
    }
    
    public static File getUserHome() {
        return USER_HOME != null ? new File(USER_HOME) : null;
    }
    
    public static File getAppHome() {
        if (USER_HOME == null) {return null;}
        
        File home = new File(USER_HOME + "/.maptool");
        home.mkdirs();
        
        return home;
    }
    
    public static File getAppHome(String subdir) {
        if (USER_HOME == null) {return null;}
        
        File home = new File(getAppHome().getPath() + "/" + subdir);
        home.mkdirs();
        
        return home;
    }
    
    public static boolean playerOwns(Token token) {

    	Player player = MapTool.getPlayer();
    	
    	if (player.isGM()) {return true;}
    	if (!MapTool.getServerPolicy().useStrictTokenManagement()) {return true;}
        
    	return token.isOwner(player.getName());
    }
    
    public static boolean tokenIsVisible(Zone zone, Token token, ZoneView view) {
    	
    	if (view.isGMView()) {return true;}
    	
    	return zone.isTokenVisible(token);
    }
}
