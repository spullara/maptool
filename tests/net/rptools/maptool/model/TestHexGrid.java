package net.rptools.maptool.model;

import junit.framework.TestCase;

public class TestHexGrid extends TestCase {

	public void testCellToZoneConversion() throws Exception {
		
		HexGrid grid = new HexGrid();
		
		for (int y = -100; y < 100; y ++) {
			
			for (int x = -100; x < 100; x++) {
				
				CellPoint cp = new CellPoint(x, y);
				
				ZonePoint zp = grid.convert(cp);
				
				assertEquals(cp, grid.convert(zp));
			}
		}
		
	}
}
