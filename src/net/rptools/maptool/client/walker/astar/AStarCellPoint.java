package net.rptools.maptool.client.walker.astar;

import net.rptools.maptool.client.CellPoint;

public class AStarCellPoint extends CellPoint {
	
	AStarCellPoint parent;
	double hScore;
	double gScore;

	public AStarCellPoint(int x, int y) {
		super(x, y);
	}
	
	public AStarCellPoint(CellPoint p) {
		super(p.x, p.y);
	}
	
	public double cost() {
		return hScore + gScore;
	}

}
