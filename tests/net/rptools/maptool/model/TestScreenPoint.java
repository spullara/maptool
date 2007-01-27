package net.rptools.maptool.model;

import net.rptools.maptool.client.ScreenPoint;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.client.ui.zone.ZoneRendererFactory;
import junit.framework.TestCase;

public class TestScreenPoint extends TestCase {

	public void testConversion() throws Exception {
		
		ZoneRenderer renderer = ZoneRendererFactory.newRenderer(new Zone());
		renderer.moveViewBy(-100, -100);

		for (int i = -10; i < 10; i++) {
			for (int j = -10; j<10; j++) {
				
				ZonePoint zp = new ZonePoint(i, j);
				assertEquals(zp, ScreenPoint.fromZonePoint(renderer, zp).convertToZone(renderer));
			}
		}
		
		
	}
}
