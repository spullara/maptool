package net.rptools.maptool.client.script.api.proxy;

import net.rptools.maptool.model.Token;

public class TokenProxy {
    private Token token;
    
    public TokenProxy(Token token) {
        this.token = token;
    }
    
    public Object setState(String state, Object value) {
        return token.setState(state, value);
    }
    
    public Object getState(String state) {
        return token.getState(state);
    }
}
