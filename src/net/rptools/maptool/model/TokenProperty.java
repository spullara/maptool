package net.rptools.maptool.model;

import java.io.Serializable;

public class TokenProperty implements Serializable {

	private String name;
	private boolean highPriority;
	
	public TokenProperty() {
		// For serialization
	}
	
	public TokenProperty(String name) {
		this(name, false);
	}
	public TokenProperty(String name, boolean highPriority) {
		this.name = name;
		this.highPriority = highPriority;
	}
	
	public boolean isHighPriority() {
		return highPriority;
	}
	public void setHighPriority(boolean highPriority) {
		this.highPriority = highPriority;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	
}
