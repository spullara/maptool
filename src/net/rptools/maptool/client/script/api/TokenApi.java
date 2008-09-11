package net.rptools.maptool.client.script.api;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.script.api.proxy.TokenProxy;
import net.rptools.maptool.model.Token;

public class TokenApi {
    public TokenProxy current() {
        Token token = MapTool.getFrame().getCurrentZoneRenderer().getZone().resolveToken(MapTool.getFrame().getCommandPanel().getIdentity());
        
        return new TokenProxy(token);
    }
    
    public TokenProxy find() {
        return null;
    }
}
