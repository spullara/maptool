package net.rptools.maptool.client.ui;

import java.awt.Rectangle;

import net.rptools.maptool.model.Token;

public class TokenLocation {

	private Rectangle bounds;
	private Token token;
	
	public TokenLocation(Rectangle bounds, Token token) {
		this.bounds = bounds;
		this.token = token;
	}
	
	public Rectangle getBounds() {
		return bounds;
	}
	
	public Token getToken() {
		return token;
	}
}
