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

import java.io.File;

import net.rptools.maptool.client.ui.zone.PlayerView;
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
    
    public static File getTmpDir() {
    	return getAppHome("tmp");
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
    
    public static boolean tokenIsVisible(Zone zone, Token token, PlayerView view) {
    	
    	if (view.isGMView()) {return true;}
    	
    	return zone.isTokenVisible(token);
    }
}
