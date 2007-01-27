package net.rptools.maptool.model;

import junit.framework.TestCase;

public class TestSquareGrid extends TestCase {

	public void testSpotCheck() throws Exception {
		
		Grid grid = new SquareGrid();
		
		System.out.println(grid.convert(new CellPoint(-1, 0)));
		
	}
}
