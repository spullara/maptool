package net.rptools.maptool.client.ui.macrobuttonpanel;

/**
 * Created by IntelliJ IDEA.
* User: Ryss
* Date: Jun 26, 2008
* Time: 6:43:30 PM
* To change this template use File | Settings | File Templates.
*/
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