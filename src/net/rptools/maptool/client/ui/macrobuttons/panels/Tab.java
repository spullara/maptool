package net.rptools.maptool.client.ui.macrobuttons.panels;

public enum Tab {
	GLOBAL(0, "Global"),
	CAMPAIGN(1, "Campaign"),
	SELECTED(2, "Selection"),
	IMPERSONATED(3, "Impersonated");

	public final int index;
	public final String title;

	Tab(int index, String title) {
		this.index = index;
		this.title = title;
	}
}