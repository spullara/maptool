package net.rptools.maptool.client.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;

import net.rptools.maptool.client.MapTool;

public class MapToolFocusTraversalPolicy extends FocusTraversalPolicy {

	@Override
	public Component getComponentAfter(Container aContainer,
			Component aComponent) {
		return MapTool.getFrame().getCurrentZoneRenderer();
	}

	@Override
	public Component getComponentBefore(Container aContainer,
			Component aComponent) {
		return MapTool.getFrame().getCurrentZoneRenderer();
	}

	@Override
	public Component getFirstComponent(Container aContainer) {
		return MapTool.getFrame().getCurrentZoneRenderer();
	}

	@Override
	public Component getLastComponent(Container aContainer) {
		return MapTool.getFrame().getCurrentZoneRenderer();
	}

	@Override
	public Component getDefaultComponent(Container aContainer) {
		return MapTool.getFrame().getCurrentZoneRenderer();
	}

}
