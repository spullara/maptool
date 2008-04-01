package net.rptools.maptool.model;

import java.io.Serializable;

public class TokenProperty implements Serializable {

	private String name;
	private String shortName;
	private boolean showOnStatSheet;
	private boolean ownerOnly;
	private boolean gmOnly;
	
	public TokenProperty() {
		// For serialization
	}

	public TokenProperty(String name) {
		this(name, null, false, false, false);
	}
	
	public TokenProperty(String name, String shortName) {
		this(name, shortName, false, false, false);
	}
	
	public TokenProperty(String name, boolean highPriority, boolean isOwnerOnly, boolean isGMOnly) {
		this(name, null, highPriority, isOwnerOnly, isGMOnly);
	}
	public TokenProperty(String name, String shortName, boolean highPriority, boolean isOwnerOnly, boolean isGMOnly) {
		this.name = name;
		this.shortName = shortName;
		this.showOnStatSheet = highPriority;
		this.ownerOnly = isOwnerOnly;
		this.gmOnly = isGMOnly;
	}

	
	public boolean isOwnerOnly() {
		return ownerOnly;
	}

	public void setOwnerOnly(boolean ownerOnly) {
		this.ownerOnly = ownerOnly;
	}

	public boolean isShowOnStateSheet() {
		return showOnStatSheet;
	}
	public void setShowOnStatSheet(boolean showOnStatSheet) {
		this.showOnStatSheet = showOnStatSheet;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public boolean isGMOnly() {
		return gmOnly;
	}

	public void setGMOnly(boolean gmOnly) {
		this.gmOnly = gmOnly;
	}

	
}
