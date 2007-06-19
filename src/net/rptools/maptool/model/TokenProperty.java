package net.rptools.maptool.model;

import java.io.Serializable;

public class TokenProperty implements Serializable {

	private String name;
	private boolean highPriority;
	private boolean ownerOnly;
	
	public TokenProperty() {
		// For serialization
	}

	public TokenProperty(String name) {
		this(name, false, false);
	}
	
	public TokenProperty(String name, boolean highPriority, boolean isGMOnly) {
		this.name = name;
		this.highPriority = highPriority;
		this.ownerOnly = isGMOnly;
	}

	
	public boolean isOwnerOnly() {
		return ownerOnly;
	}

	public void setOwnerOnly(boolean gmOnly) {
		this.ownerOnly = gmOnly;
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
